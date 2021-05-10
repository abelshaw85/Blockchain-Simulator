package blockchain;

import java.io.Serializable;

public class Block implements Serializable {
	final private static long serialVersionUID = 1L;
	final private int id;
    final private Miner miner;
    final private long timeStamp;
    final private String hash;
    final private String previousHash;
    final private int magicNumber;
    final private long secondsToGenerate;
    final private String blockData;
    final private int previousN;
    final private int newN;

    public Block(int id, Miner miner, long timeStamp, String hash, String previousHash, int magicNumber,
                 long secondsToGenerate, int previousN, int newN, String blockData) {
        this.id = id;
        this.miner = miner;
        this.timeStamp = timeStamp;
        this.hash = hash;
        this.previousHash = previousHash;
        this.magicNumber = magicNumber;
        this.secondsToGenerate = secondsToGenerate;
        this.previousN = previousN;
        this.newN = newN;
        if (blockData == null || blockData.isEmpty()) {
            this.blockData = "no messages";
        } else {
            this.blockData = blockData;
        }
    }

    public int getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String mainOutput = String.format("Block:\n" +
                        "Created by: %s\n" +
                        "%s gets 100 VC\n" +
                        "Id: %d\n" +
                        "Timestamp: %d\n" +
                        "Magic number: %d\n" +
                        "Hash of the previous block: \n" +
                        "%s\n" +
                        "Hash of the block: \n" +
                        "%s\n" +
                        "Block data: %s\n" +
                        "Block was generating for %d seconds\n",
                this.miner.getName(), this.miner.getName(), this.id, this.timeStamp, this.magicNumber, this.previousHash,
                this.hash, this.blockData, this.secondsToGenerate);
        sb.append(mainOutput);
        if (previousN < newN) {
            sb.append("N was increased to ");
            sb.append(newN);
        } else if (previousN > newN) {
            sb.append("N was decreased to ");
            sb.append(newN);
        } else {
            sb.append("N stays the same");
        }
        sb.append("\n");
        return sb.toString();
    }
}
