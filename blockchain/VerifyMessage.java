package blockchain;

import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;
/*
Original code from: https://mkyong.com/java/java-digital-signatures-example/
 */
public class VerifyMessage {
    private String verifiedMessage;

    //The constructor of VerifyMessage class retrieves the byte arrays from the File
    //and prints the message only if the signature is verified.
    public VerifyMessage(String filename, String keyFile) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
            List<byte[]> list = (List<byte[]>) in.readObject();
            in.close();

            if (verifySignature(list.get(0), list.get(1), keyFile)) {
                this.verifiedMessage = new String(list.get(0));
            } else {
                this.verifiedMessage = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getVerifiedMessage() {
        return this.verifiedMessage;
    }

    //Method for signature verification that initializes with the Public Key,
    //updates the data to be verified and then verifies them using the signature
    private boolean verifySignature(byte[] data, byte[] signature, String keyFile) {
        try {
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(getPublic(keyFile));
            sig.update(data);
            return sig.verify(signature);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //Method to retrieve the Public Key from a file
    public PublicKey getPublic(String filename) {
        try {
            byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }
}
