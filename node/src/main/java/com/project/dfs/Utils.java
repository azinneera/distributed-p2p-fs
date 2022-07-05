package com.project.dfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Random;

public class Utils {

    private static Integer A;
    private static Path NODES_DIR;

    public static Path getNodesDir() throws IOException {
        if (NODES_DIR == null) {
            NODES_DIR = Paths.get(System.getProperty("java.io.tmpdir")).resolve("ds-group-d/nodes");
            if (Files.notExists(NODES_DIR)) {
                Files.createDirectories(NODES_DIR);
            }
        }
        return NODES_DIR;
    }

    public static Integer getA() {
        return A;
    }

    public static void setA(Integer a) {
        A = a;
    }

    public static String getFileChecksum(MessageDigest digest, File file) throws IOException {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        ;

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }

    public static void createRandomContentToFile(String filePath) throws IOException {
        Random random = new Random();
        FileWriter myWriter = new FileWriter(filePath);
        int randContent = random.nextInt(100000000);
        String str = String.valueOf(randContent);
        myWriter.write(str.repeat(1250000));
        myWriter.close();
    }

    public static void nowEpoch(String flag) {
        long currentTimestamp = System.currentTimeMillis();
        Node.log("INFO", "Epoch timestamp in millis: " + currentTimestamp + " at " + flag);
    }

    public static boolean deleteDirectory(Path directoryPath) {
        File directory = new File(String.valueOf(directoryPath));
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File f : files) {
                    boolean success = deleteDirectory(f.toPath());
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return directory.delete();
    }
}
