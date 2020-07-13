package blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Blockchain implements Serializable {
    private volatile List<Block> chain;
    private int numberOfZeroes = 0;
    private static final long serialVersionUID = 1L;
    final private int maxSize;
    final private List<Transaction> transactions;
    final private List<Miner> miners;
    private int startIndex = 0;

    public Blockchain(int maxSize, List<Miner> miners) {
        this.chain = new ArrayList();
        this.maxSize = maxSize;
        this.transactions = new ArrayList();
        this.miners = miners;
    }

    public synchronized void addBlock(int id, Miner miner, long timeStamp, String hash, String previousHash, int magicNumber,
                                      long secondsToGenerate) {
        try {
            Block previousBlock = this.chain.isEmpty() ? null : this.chain.get(this.chain.size() - 1);

            if (this.chain.size() == 0 ||
                    (id == Objects.requireNonNull(previousBlock).getId() + 1 && previousHash.equals(getPreviousHash())
                    && zeroHashCheck(hash))) {
                int previousN = this.numberOfZeroes;
                int newN = manageNumberOfSeconds(secondsToGenerate);
                Block newBlock = new Block(id, miner, timeStamp, hash, previousHash, magicNumber, secondsToGenerate,
                        previousN, newN, this.getMessages());
                this.chain.add(newBlock);
                //Gift winning miner 100 coins
                miner.receiveCoins(100);
                this.transactions.add(new Transaction(null, miner, 100));
            }

        } catch (Exception e) {
            System.out.println("Add Block Error: " + e.getLocalizedMessage());
        }
    }

    //Gets a random miner that is NOT the same as the requesting miner
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

    public Miner getMinerById(int id) {
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
        int senderAmount = 100; //initial miner coins

        for (Transaction trans: this.transactions) {
            if (trans.getReceiver() == sender) { //if the sender received coins previously
                senderAmount += trans.getAmount();
            } else if (trans.getSender() == sender) { //if the sender spent coins previously
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
            //only include transactions that are between other miners and not gifts from successful mining
            if (this.transactions.get(i).getSender() != null) {
                sb.append("\n");
                sb.append(this.transactions.get(i));
            }
        }
        this.startIndex = this.transactions.size(); //next block will only include messages from this point on
        return sb.toString();
    }

    //Increases or decreases how many 0's must be found in the magic number
    //Commented code has been removed for testing, currently keeps N at 0
    private synchronized int manageNumberOfSeconds(long secondsToGenerate) {
        if (secondsToGenerate > 60 && numberOfZeroes > 0) {
            //return --this.numberOfZeroes;
            return this.numberOfZeroes;
        } else if (secondsToGenerate < 10) {
            //return ++this.numberOfZeroes;
            return this.numberOfZeroes;
        }
        return this.numberOfZeroes;
    }

    public int getNumberOfZeroes() {
        return numberOfZeroes;
    }

    public int getSize() {
        return this.chain.size();
    }

    public synchronized String getPreviousHash() {
        if (this.chain.isEmpty()) {
            return "0";
        }
        return this.chain.get(this.chain.size() - 1).getHash();
    }

    private synchronized boolean zeroHashCheck(String hash) {
        String zeroesRegex = String.format("0{%d}[1-9A-Za-z]\\w+", numberOfZeroes);
        return hash.matches(zeroesRegex);
    }

    public synchronized boolean isAcceptingNewBlocks() {
        return this.chain.size() < this.maxSize;
    }

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
