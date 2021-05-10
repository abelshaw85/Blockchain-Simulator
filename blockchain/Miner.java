package blockchain;

import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Miner implements Runnable {
    final private int id;
    final private Blockchain blockchain;
    private int virtualCoins = 100;
    final private String pathToPubicKey;
    final private String pathToPrivateKey;
    final private String name;

    public Miner(int id, Blockchain blockchain, String pathToPublicKey, String pathToPrivateKey) {
        this.id = id;
        this.blockchain = blockchain;
        this.pathToPubicKey = pathToPublicKey;
        this.pathToPrivateKey = pathToPrivateKey;
        this.name = "miner" + id;
    }

    private void tryToAddBlock() {
        int blockId = blockchain.getSize() + 1;
        long timeStamp = new Date().getTime();
        String previousHash = blockchain.getPreviousHash();
        String zeroesRegex = String.format("0{%d}[1-9A-Za-z]\\w+", blockchain.getNumberOfZeroes());
        Pattern pattern = Pattern.compile(zeroesRegex);

        while (blockchain.isAcceptingNewBlocks()) {
            Random random = new Random();
            int magicNumber = random.nextInt();
            // We create a new 128 character hash using Sha256 and all the block's data.
            String hash = StringUtil.applySha256(blockId + timeStamp + previousHash + magicNumber);
            // Check if the hash has the correct number of leading 0's
            Matcher matcher = pattern.matcher(hash);
            if (matcher.matches()) {
            	// We try to add the block to our blockchain.
                long secondsToGenerate = (new Date().getTime() - timeStamp) / 1000;
                blockchain.addBlock(blockId, this, timeStamp, hash, previousHash, magicNumber, secondsToGenerate);
                break;
            }
        }
    }

    private void tryToSpendCoins() {
        try {
            Random random = new Random();
            // 50% chance they will try and spend coins
            if (random.nextInt(2) > 0) {
                // Sets a random amount of coins to spend
                int amountToSpend = random.nextInt(this.virtualCoins + 1);
                // Get a random miner that differs from the current miner
                Miner minerToGift = blockchain.getRandomMiner(this.id);
                if (minerToGift == null) {
                    return;
                }
                String message = this.id + "," + amountToSpend + "," + minerToGift.getId();
                String fileName = this.name + "Data/SignedData.txt";
                new Message(message, this.pathToPrivateKey).writeToFile(fileName);
                blockchain.addMessage(fileName, this.pathToPubicKey);
            }
        } catch (Exception e) {
            System.out.println("COIN SPENDING ERROR: " + this.getName() + " " + e.getMessage());
        }
    }

    public void spendCoins(int amountToSpend) {
        if (this.virtualCoins >= amountToSpend) {
            this.virtualCoins -= amountToSpend;
        }
    }

    public void receiveCoins(int coins) {
        this.virtualCoins += coins;
    }

    public int getVirtualCoins() {
        return virtualCoins;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public void run() {
        while (this.blockchain.isAcceptingNewBlocks()) {
            if (this.virtualCoins > 0) {
                tryToSpendCoins();
            }
            tryToAddBlock();
        }
    }
}
