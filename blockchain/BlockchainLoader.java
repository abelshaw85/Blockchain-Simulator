package blockchain;

import java.util.List;

public class BlockchainLoader {
    final static private String path = "Blockchain Files/blockchain.data";

    public static Blockchain getBlockchain(List<Miner> miners) {

        return new Blockchain(15, miners);
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
