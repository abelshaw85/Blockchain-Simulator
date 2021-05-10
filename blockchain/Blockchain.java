package blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Blockchain implements Serializable {
	private static final long serialVersionUID = 1L;
	
    final private List<Block> chain;
    final private List<Transaction> transactions;
    final private List<Miner> miners;
    
    final private int maxSize;
    private int numberOfZeroes = 0;
    private int startIndex = 0;
    // Values used to decide whether to increase or decrease the magic number
    private final int reducingValue = 10;
    private final int increasingValue = 5;

    public Blockchain(int maxSize, List<Miner> miners) {
        this.chain = new ArrayList<Block>();
        this.maxSize = maxSize;
        this.transactions = new ArrayList<Transaction>();
        this.miners = miners;
    }

    // Miner passes in the values it wants for the new block.
    public synchronized void addBlock(int id, Miner miner, 
							    		long timeStamp, String hash, 
							    		String previousHash, 
							    		int magicNumber,    
							    		long secondsToGenerate) {
        try {
            Block previousBlock = this.chain.isEmpty() ? null : this.chain.get(this.chain.size() - 1);

            // If this is the first block in the chain, we accept it
            // Otherwise we check that the hash data is correct (the previous hash matches the previous block, and that the magic number is correct)
            if (this.chain.size() == 0 ||
                    (id == Objects.requireNonNull(previousBlock).getId() + 1 && previousHash.equals(getPreviousHash()) && zeroHashCheck(hash))) {
            	System.out.println(">> Adding Block... ");
                int previousN = this.numberOfZeroes;
                // Either increases or decreases the magic number depending on how long it took to mine the block.
                numberOfZeroes = manageNumberOfSeconds(secondsToGenerate);
                Block newBlock = new Block(
                		id, 
                		miner, 
                		timeStamp, 
                		hash, 
                		previousHash, 
                		magicNumber, 
                		secondsToGenerate,
                        previousN, 
                        numberOfZeroes, 
                        this.getMessages());
                this.chain.add(newBlock);
                
                // Gift winning miner 100 coins
                miner.receiveCoins(100);
                this.transactions.add(new Transaction(null, miner, 100));
                System.out.println(">> Block added! Number of blocks: " + this.getSize() + "/" + this.maxSize);
            }
        } catch (Exception e) {
            System.out.println("Add Block Error: " + e.getLocalizedMessage());
        }
    }

    // Gets a random miner that is NOT the same as the requesting miner
    // This is used to simulate a miner donating coins to fellow miners
    public synchronized Miner getRandomMiner(int idOfSender) {
        if (this.miners.size() > 1) {
            while (true) {
                Random random = new Random();
                int index = random.nextInt(this.miners.size());
                Miner randomMiner = this.miners.get(index);
                if (randomMiner.getId() != idOfSender) {
                    return randomMiner;
                }
            }
        }
        return null;
    }

    private Miner getMinerById(int id) {
        return this.miners.stream().filter(miner -> miner.getId() == id).findFirst().orElse(null);
    }

    public synchronized void addMessage(String fileName, String keyFile) {
        String message = new VerifyMessage(fileName, keyFile).getVerifiedMessage();
        if (message != null) {
            String[] parts = message.split(",");
            Miner sender = getMinerById(Integer.parseInt(parts[0]));
            Miner receiver = getMinerById(Integer.parseInt(parts[2]));
            int amount = Integer.parseInt(parts[1]);

            if (verifyTransactionIsPossible(sender, amount)) {
                sender.spendCoins(amount);
                receiver.receiveCoins(amount);
                this.transactions.add(new Transaction(sender, receiver, amount));
            }
        }
    }

    public synchronized boolean verifyTransactionIsPossible(Miner sender, int amount) {
        int senderAmount = 100; // Initial miner coins

        for (Transaction trans: this.transactions) {
            if (trans.getReceiver() == sender) { // If the sender received coins previously
                senderAmount += trans.getAmount();
            } else if (trans.getSender() == sender) { // If the sender spent coins previously
                senderAmount -= trans.getAmount();
            }
        }
        return (senderAmount >= amount) && (sender.getVirtualCoins() >= amount);
    }


    public synchronized String getMessages() {
        if (this.transactions.isEmpty() || this.startIndex == this.transactions.size()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < this.transactions.size(); i++) {
            // Only include transactions that are between other miners and not gifts from successful mining
            if (this.transactions.get(i).getSender() != null) {
                sb.append("\n");
                sb.append(this.transactions.get(i));
            }
        }
        this.startIndex = this.transactions.size(); //next block will only include messages from this point on
        return sb.toString();
    }

    // Increases or decreases how many 0's must be found in the magic number
    // Commented code has been removed for testing, currently keeps N at 0
    private synchronized int manageNumberOfSeconds(long secondsToGenerate) {
    	System.out.print(">> Time to generate: " + secondsToGenerate + " seconds, ");
        if (secondsToGenerate > reducingValue && numberOfZeroes > 0) {
        	System.out.println("reducing magic number...");
            return --this.numberOfZeroes;
        } else if (secondsToGenerate < increasingValue) {
        	System.out.println("increasing magic number...");
            return ++this.numberOfZeroes;
        }
        System.out.println("no change to magic number.");
        return this.numberOfZeroes;
    }

    public int getNumberOfZeroes() {
        return numberOfZeroes;
    }

    public int getSize() {
        return this.chain.size();
    }

    public String getPreviousHash() {
        if (this.chain.isEmpty()) {
            return "0";
        }
        return this.chain.get(this.chain.size() - 1).getHash();
    }

    private boolean zeroHashCheck(String hash) {
        String zeroesRegex = String.format("0{%d}[1-9A-Za-z]\\w+", numberOfZeroes);
        return hash.matches(zeroesRegex);
    }

    public synchronized boolean isAcceptingNewBlocks() {
        return this.chain.size() < this.maxSize;
    }

    // Pass through chain and ensure all blocks are valid!
    public boolean validate() {
        if (this.chain.isEmpty()) {
            return false;
        }
        for (int i = 1; i < this.chain.size() - 1; i++) {
            Block block = this.chain.get(i);
            Block previousBlock = this.chain.get(i - 1);
            if (block.getId() != i + 1 ||
                    !block.getPreviousHash().equals(previousBlock.getHash())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        if (this.chain.size() == 0) {
            return "empty";
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < getSize(); i++) {
            sb.append(this.chain.get(i));
            sb.append("\n");
        }
        return sb.toString();
    }
}
