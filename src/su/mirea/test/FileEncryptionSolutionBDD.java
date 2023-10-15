package su.mirea.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

public class FileEncryptionSolutionBDD {

        public static void xorCipher(byte[] input, byte[] key, BDDAssistant bddAssistant) {
                if (bddAssistant != null) bddAssistant.scenario("Applying  XOR cipher to file");
                byte[] initial = Arrays.copyOf(input, input.length);
                if (bddAssistant != null) bddAssistant.when("xorCipher is called with input and key");
                int keyIndex = 0;
                for (int i = 0; i < input.length; i++) {
                        input[i] = (byte) (input[i] ^ key[keyIndex]);
                        keyIndex = (keyIndex + 1) % key.length;
                }
                byte[] reconstructed = Arrays.copyOf(input, input.length);
                if (bddAssistant != null) xorCipher(reconstructed, key, null);
                if (bddAssistant != null) bddAssistant.then("the input is XORed with the key");
                if (!Arrays.equals(initial, reconstructed) || Arrays.equals(input, initial)){
                        if (bddAssistant != null) bddAssistant.but("the operation is not performed as expected");
                }
        }

        public static void inverse(byte[] input, BDDAssistant bddAssistant) {
                if (bddAssistant != null) bddAssistant.scenario("Applying byte reversing to file");
                byte[] initial = Arrays.copyOf(input, input.length);
                if (bddAssistant != null) bddAssistant.when("inverse is called with input");
                for (int i = 0; i < input.length; i++) {
                        input[i] = reverseBits(input[i]);
                }
                byte[] reconstructed = Arrays.copyOf(input, input.length);
                if (bddAssistant != null) inverse(reconstructed, null);
                if (bddAssistant != null) bddAssistant.then("the bits in the input are reversed");
                if (!Arrays.equals(initial, reconstructed) || Arrays.equals(input, initial)){
                        if (bddAssistant != null) bddAssistant.but("the operation is not performed as expected");
                }
        }

        public static void batchFileCipher(String foldername, String algorithm, BDDAssistant bddAssistant) {
                if (bddAssistant != null) bddAssistant.scenario("Batch operation on folder " + foldername + " with algorithm " + algorithm);
                if (bddAssistant != null) bddAssistant.when("batchFileCipher is called with input folder name " + foldername + " and algorithm " + algorithm);
                File folder = new File(foldername);
                if (!folder.exists() || !folder.isDirectory()) {
                        System.out.println("Invalid folder name!");
                        return;
                }

                File[] files = folder.listFiles();
                if (files == null || files.length == 0) {
                        System.out.println("No files found in the folder!");
                        return;
                }
                if (bddAssistant != null) bddAssistant.then("the folder is being opened");
                for (File file : files) {
                        if (file.isFile()) {
                                String filename = file.getName();
                                try {
                                        FileInputStream fis = new FileInputStream(file);
                                        byte[] data = new byte[(int) file.length()];
                                        fis.read(data);
                                        if (bddAssistant != null) bddAssistant.when("the next file is being read");
                                        if (bddAssistant != null) bddAssistant.then("the algorithm is being chosen");
                                        if (algorithm.equals("XOR-cipher")) {
                                                if (bddAssistant != null) bddAssistant.and("XOR-cipher is being applied");
                                                if (bddAssistant != null) bddAssistant.freeze();
                                                xorCipher(data, filename.getBytes(), bddAssistant);
                                                if ((bddAssistant != null) && bddAssistant.scriptBuilderCopy.toString().contains("not performed")){
                                                        bddAssistant.unfreeze();
                                                        bddAssistant.but("the operation is not performed as expected");
                                                } else {
                                                        if (bddAssistant != null) bddAssistant.unfreeze();
                                                }
                                        } else if (algorithm.equals("inverse-byte")) {
                                                if (bddAssistant != null) bddAssistant.and("inverse-cipher is being applied");
                                                if (bddAssistant != null) bddAssistant.freeze();
                                                inverse(data, bddAssistant);
                                                if ((bddAssistant != null) && bddAssistant.scriptBuilderCopy.toString().contains("not performed")){
                                                        bddAssistant.unfreeze();
                                                        bddAssistant.but("the operation is not performed as expected");
                                                } else {
                                                        if (bddAssistant != null) bddAssistant.unfreeze();
                                                }
                                        }

                                        if (bddAssistant != null) bddAssistant.and("the modified data is being saved");
                                        new FileOutputStream(file).write(data);

                                } catch (IOException e) {
                                        if (bddAssistant != null) bddAssistant.and("the fatal error occurs");
                                        System.out.println("Failed to process file: " + filename);
                                        e.printStackTrace();
                                }
                        }
                }
        }

        private static byte reverseBits(byte value) {
                byte result = 0;
                for (int i = 0; i < 8; i++) {
                        result <<= 1;
                        result |= (value & 1);
                        value >>= 1;
                }
                return result;
        }

        public static void main(String[] args) {
                BDDAssistant bddAssistant = new BDDAssistant();
                bddAssistant.story("File Encryption")
                        .asA("user")
                        .inOrderTo("protect sensitive data")
                        .iWantTo("encrypt and decrypt files");

                byte[] input = new byte[] { 1, 2, 3 };
                byte[] key = new byte[] { 4, 5, 6 };

                xorCipher(input, key, bddAssistant);
                inverse(input, bddAssistant);
                batchFileCipher("small_data", "XOR-cipher", bddAssistant);
                batchFileCipher("small_data", "inverse-byte", bddAssistant);
                String expectedScript = bddAssistant.readScriptFromFile("FES.scenario");

                boolean isMatch = bddAssistant.matchScript(expectedScript);
                System.out.println("╔═════════════════════════════════════════════════╗");
                System.out.println("║               BDD Test Results                  ║");
                System.out.println("╠═════════════════════════════════════════════════╣");
                System.out.println("║Is script matching? " + isMatch + (isMatch ? "                         ║" : "                        ║"));
                System.out.println("╠═════════════════════════════════════════════════╣");
                System.out.println("║Script:                                          ║\n" + bddAssistant.getScript());
                System.out.println("╚═════════════════════════════════════════════════╝");
        }
}