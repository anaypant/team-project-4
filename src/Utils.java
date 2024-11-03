package src;

import java.io.*;
import java.util.ArrayList;

public class Utils {
    public static void replaceLineInFile(String oldLine, String newLine, String fileName) throws IOException {
        File inputFile = new File(fileName);
        File tempFile = new File("tempfile98743214.txt");

        // Try-with-resources for BufferedReader and PrintWriter
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                // Check if the line matches the old serialized data
                if (currentLine.trim().equals(oldLine)) {
                    writer.println(newLine); // Write the updated line
                } else {
                    writer.println(currentLine); // Write the original line
                }
            }
        }

        // Delete the original file and rename the temporary file to the original name
        if (!inputFile.delete()) {
            System.out.println("Could not delete original file.");
            return;
        }
        if (!tempFile.renameTo(inputFile)) {
            System.out.println("Could not rename temporary file.");
        }
    }

    public static ArrayList<String> arrayFromString(String s) {
        ArrayList<String> result = new ArrayList<>();
        String st = s.substring(1, s.length() - 1);

        String currentElement = "";
        boolean inQuotes = false;

        for (int i = 0; i < st.length(); i++) {
            char c = st.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(currentElement.trim());
                currentElement = "";
            } else {
                currentElement += c;
            }
        }
        if (!currentElement.isEmpty()) {
            result.add(currentElement.trim());
        }

        return result;
    }

}
