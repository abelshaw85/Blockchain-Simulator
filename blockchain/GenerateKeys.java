package blockchain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

/*
Original code from: https://mkyong.com/java/java-digital-signatures-example/
 */
public class GenerateKeys {

    private KeyPairGenerator keyGen;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public GenerateKeys(int keyLength) {
        try {
            this.keyGen = KeyPairGenerator.getInstance("RSA");
            this.keyGen.initialize(keyLength);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void createKeys() {
        KeyPair pair = this.keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public void writeToFile(String path, byte[] key) {
        File f = new File(path);
        f.getParentFile().mkdirs();

        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(key);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}