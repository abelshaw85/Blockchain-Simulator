package blockchain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

/*
Original code from: https://mkyong.com/java/java-digital-signatures-example/
 */
public class Message {
    final private List<byte[]> list;

    //The constructor of Message class builds the list that will be written to the file.
    //The list consists of the message and the signature.
    public Message(String data, String keyFile) {
        list = new ArrayList<>();
        list.add(data.getBytes());
        list.add(sign(data, keyFile));
    }

    //The method that signs the data using the private key that is stored in keyFile path
    public byte[] sign(String data, String keyFile) {
        try {
            Signature rsa = Signature.getInstance("SHA1withRSA");
            rsa.initSign(getPrivate(keyFile));
            rsa.update(data.getBytes());
            return rsa.sign();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    //Method to retrieve the Private Key from a file
    public PrivateKey getPrivate(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    //Method to write the List of byte[] to a file
    public void writeToFile(String filename) {
        try {
            File f = new File(filename);
            f.getParentFile().mkdirs();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
            out.writeObject(list);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
