package blockchain;

import java.util.List;

// BlockchainLoader will either recover a saved blockchain or generate a new chain.
public class BlockchainLoader {
    final static private String path = "Blockchain Files/blockchain.data";

    // To limit our runtime, we pass in how many blocks we want to generate.
    public static Blockchain getBlockchain(List<Miner> miners, int blocksToMine) {
        return new Blockchain(blocksToMine, miners);
    }

    public static Blockchain loadBlockchain(List<Miner> miners) {
        Blockchain bc = (Blockchain) SerializeUtils.deserialize(path);
        if (bc == null) {
            bc = new Blockchain(15, miners);
        }
        return bc;
    }

    public static void saveBlockchain(Blockchain bc) {
        SerializeUtils.serialize(bc, path);
    }
}
