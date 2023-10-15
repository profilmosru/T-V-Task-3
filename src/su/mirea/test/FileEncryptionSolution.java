package su.mirea.test;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.security.*;

public class FileEncryptionSolution {

    public static void xorCipher(byte[] input, byte[] key) {
    }

    public static void inverse(byte[] input) {
    }

    public static void batchFileCipher(String foldername, String algorithm) {
    }

    public static void testXORCipher(String filePath) {
        File file = new File(filePath);
        byte[] originalData = readFileData(file);

        // Make a copy of the original data for comparison
        byte[] originalCopy = Arrays.copyOf(originalData, originalData.length);

        // Encrypt the data using XOR cipher
        xorCipher(originalData, file.getName().getBytes());

        // Assert that the original data is different from the encrypted data
        if (!!Arrays.equals(originalData, originalCopy)) {
            throw new AssertionError("XOR cipher operation did not change the data");
        }

        // Decrypt the data back to its original form
        xorCipher(originalData, file.getName().getBytes());

        // Assert that the decrypted data matches the original data
        if (!Arrays.equals(originalData, originalCopy)) {
            throw new AssertionError("XOR cipher operation did not revert the data back to its original form");
        }

    }

    public static void testInverse(String filePath) {
        File file = new File(filePath);
        byte[] originalData = readFileData(file);

        // Make a copy of the original data for comparison
        byte[] originalCopy = Arrays.copyOf(originalData, originalData.length);

        // Apply the inverse operation to the data
        inverse(originalData);

        // Assert that the original data is different from the inverted data
        if (!!Arrays.equals(originalData, originalCopy)){
            throw new AssertionError("Inverse operation did not change the data");
        }

        // Apply the inverse operation again to revert the data back to its original form
        inverse(originalData);

        // Assert that the inverted data matches the original data
        if (!Arrays.equals(originalData, originalCopy)){
            throw new AssertionError("Inverse operation did not revert the data back to its original form");
        }
    }

    private static byte[] readFileData(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            return data;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + file.getName(), e);
        }
    }

    public static String calculateHash(File file, String algorithm) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);

        try (DigestInputStream dis = new DigestInputStream(new FileInputStream(file), digest)) {
            while (dis.read() != -1) {
                // Read the file to update the digest
            }
        }

        byte[] hashBytes = digest.digest();

        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    public static boolean testHashAndCipher(String foldername, String algorithm) {
        try {
            // Step 1: Hash all files in the directory
            File folder = new File(foldername);
            if (!folder.exists() || !folder.isDirectory()) {
                System.out.println("Invalid folder name!");
                return false;
            }

            File[] files = folder.listFiles();
            if (files == null || files.length == 0) {
                System.out.println("No files found in the folder!");
                return false;
            }

            // Map to store initial hashes
            HashMap<String, String> initialHashes = new HashMap<>();

            for (File file : files) {
                if (file.isFile()) {
                    try {
                        String filename = file.getName();
                        String hash = calculateHash(file, "SHA-256");
                        initialHashes.put(filename, hash);
                    } catch (NoSuchAlgorithmException | IOException e) {
                        System.out.println("Failed to calculate hash for file: " + file.getName());
                        e.printStackTrace();
                        return false;
                    }
                }
            }

            // Step 2: Execute cipher method twice
            batchFileCipher(foldername, algorithm);
            for (File file : files) {
                if (file.isFile()) {
                    try {
                        String filename = file.getName();
                        String hash = calculateHash(file, "SHA-256");
                        String initialHash = initialHashes.get(filename);

                        if (hash.equals(initialHash)) {
                            System.out.println("Hash match for file and his ciphered version: " + filename);
                            return false;
                        }
                    } catch (NoSuchAlgorithmException | IOException e) {
                        System.out.println("Failed to calculate hash for file: " + file.getName());
                        e.printStackTrace();
                        return false;
                    }
                }
            }
            batchFileCipher(foldername, algorithm);

            // Step 3: Compare hashes of twice-processed files with initial hashes
            for (File file : files) {
                if (file.isFile()) {
                    try {
                        String filename = file.getName();
                        String hash = calculateHash(file, "SHA-256");
                        String initialHash = initialHashes.get(filename);

                        if (!hash.equals(initialHash)) {
                            System.out.println("Hash mismatch for file: " + filename);
                            return false;
                        }
                    } catch (NoSuchAlgorithmException | IOException e) {
                        System.out.println("Failed to calculate hash for file: " + file.getName());
                        e.printStackTrace();
                        return false;
                    }
                }
            }

            // All hashes match
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        // Perform tests
        boolean xorCipherTestPassed = true;
        boolean inverseTestPassed = true;

        try {
            testXORCipher("medium_data/file1.txt");
        } catch (AssertionError e) {
            xorCipherTestPassed = false;
            System.out.println("XOR Cipher Test Failed: " + e.getMessage());
        }

        try {
            testInverse("medium_data/file2.txt");
        } catch (AssertionError e) {
            inverseTestPassed = false;
            System.out.println("Inverse Test Failed: " + e.getMessage());
        }
        // Perform tests
        boolean xorCipherTestPassed2 = true;
        boolean inverseTestPassed2 = true;

        try {
            testXORCipher("medium_data/file2.txt");
        } catch (AssertionError e) {
            xorCipherTestPassed2 = false;
            System.out.println("XOR Cipher Test Failed: " + e.getMessage());
        }

        try {
            testInverse("medium_data/file1.txt");
        } catch (AssertionError e) {
            inverseTestPassed2 = false;
            System.out.println("Inverse Test Failed: " + e.getMessage());
        }
        // Print fancy table-style test results
        System.out.println("╔═════════════════════════════════════════════════╗");
        System.out.println("║               TTD Test Results                  ║");
        System.out.println("╠═════════════════════════════════════════════════╣");
        System.out.println("║   Test Name        │     Status                 ║");
        System.out.println("╠═════════════════════════════════════════════════╣");
        System.out.printf("║ XOR Cipher Test 1  │    %s%n", xorCipherTestPassed ?
                "Passed                  ║" : "Failed                  ║");
        System.out.printf("║ Inverse Test 1     │    %s%n", inverseTestPassed ?
                "Passed                  ║" : "Failed                  ║");
        System.out.printf("║ XOR Cipher Test 2  │    %s%n", xorCipherTestPassed2 ?
                "Passed                  ║" : "Failed                  ║");
        System.out.printf("║ Inverse Test  2    │    %s%n", inverseTestPassed2 ?
                "Passed                  ║" : "Failed                  ║");
        System.out.printf("║ XOR Batch Test     │    %s%n", testHashAndCipher("small_data", "XOR-cipher") ?
                "Passed                  ║" : "Failed                  ║");
        System.out.printf("║ Inverse Batch Test │    %s%n", testHashAndCipher("small_data", "inverse-byte") ?
                "Passed                  ║" : "Failed                  ║");
        System.out.println("╚═════════════════════════════════════════════════╝");
    }
}
