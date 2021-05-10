package blockchain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    final private static List<Miner> miners = new ArrayList<>();

    public static void main(String[] args) {
        Blockchain blockchain = BlockchainLoader.getBlockchain(miners);
        
        // Create an executor the size of the number of processors/cores we have
        int poolSize = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        
        final int numberOfMiners = 5;
        
        // Location of Key files
        final String pathToPublic = "KeyPair/publicKey";
        final String pathToPrivate = "KeyPair/privateKey";

        // Generates a public and private key
        GenerateKeys gk = new GenerateKeys(1024);
        gk.createKeys();
        gk.writeToFile(pathToPublic, gk.getPublicKey().getEncoded());
        gk.writeToFile(pathToPrivate, gk.getPrivateKey().getEncoded());

        // Create a list of miners that will attempt to mine blocks
        for (int i = 0; i < numberOfMiners; i++) {
            Miner miner = new Miner(i + 1, blockchain, pathToPublic, pathToPrivate);
            miners.add(miner);
        }

        // Adding miners to the executor will manage their run() methods.
        miners.forEach(miner -> executor.submit(miner));

        System.out.println(">> Blockchain created, mining...");
        boolean shutdown = false;
        while (!shutdown) {
            if (!blockchain.isAcceptingNewBlocks()) {
            	System.out.println("");
                System.out.print(blockchain);
                executor.shutdown();
                shutdown = true;
            }
        }
        System.out.println("Completed Blockchain is valid: " + blockchain.validate());
    }
}
