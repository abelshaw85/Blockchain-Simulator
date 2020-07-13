package blockchain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    final private static List<Miner> miners = new ArrayList<>();

    public static void main(String[] args) {
        Blockchain blockchain = BlockchainLoader.getBlockchain(miners);
        int poolSize = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        final int numberOfMiners = 5;
        final String pathToPublic = "KeyPair/publicKey";
        final String pathToPrivate = "KeyPair/privateKey";

        GenerateKeys gk;
        gk = new GenerateKeys(1024);
        gk.createKeys();
        gk.writeToFile(pathToPublic, gk.getPublicKey().getEncoded());
        gk.writeToFile(pathToPrivate, gk.getPrivateKey().getEncoded());


        for (int i = 0; i < numberOfMiners; i++) {
            Miner miner = new Miner(i + 1, blockchain, pathToPublic, pathToPrivate);
            miners.add(miner);
        }

        miners.forEach(miner -> executor.submit(miner));

        while (true) {
            if (!blockchain.isAcceptingNewBlocks()) {
                System.out.println(blockchain);
                executor.shutdown();
                break;
            }
        }
    }
}
