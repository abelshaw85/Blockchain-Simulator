package blockchain;

import java.io.*;

// General purpose Serialisation class for fetching/writing serialised data.
public class SerializeUtils {
    public static void serialize(Object obj, String fileName) {
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(fileName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(obj);
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
    }

    public static Object deserialize(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream(fis);
                try (ObjectInputStream ois = new ObjectInputStream(bis)) {
                    return ois.readObject();
                } catch (IOException e) {
                    System.out.println("IO Exception: " + e.getMessage());
                } catch (ClassNotFoundException e) {
                    System.out.println("Class Not Found Exception: " + e.getMessage());
                }
            } catch (FileNotFoundException e) {
                System.out.println("File Not Found Exception: " + e.getMessage());
            }
        }
        return null;
    }
}
