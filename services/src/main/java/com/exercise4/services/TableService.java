package com.exercise4.services;

import com.exercise4.models.Table;
import com.exercise4.utils.InputUtils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TableService {

    public static Table loadTable(File file) {
        Table table = new Table();
        try (InputStream in = new FileInputStream(file)) {
            List<String> lines = IOUtils.readLines(in, "UTF-8");
            for (String line : lines) {
                Map<String, String> row = parseRow(line);
                if (row != null) {
                    table.getRows().add(row);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading table: " + e.getMessage());
        }
        return table;
    }

    public static void saveTable(Table table, File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Map<String, String> row : table.getRows()) {
                writeRow(writer, row);
            }
            writer.flush();
            System.out.println("File saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving table: " + e.getMessage());
        }
    }

    public static void createFileWithDefaultValues(File file) throws IOException {
        if (file.createNewFile()) {
            System.out.println("File created: " + file.getAbsolutePath());
            populateFileWithDefaultValues(file);
        } else {
            throw new IOException("File could not be created: " + file.getAbsolutePath());
        }
    }

    public static void populateFileWithDefaultValues(File file) throws IOException {
        String defaultFilePath = "src/main/resources/default.txt";
        File defaultFile = new File(defaultFilePath);
    
        if (!defaultFile.exists()) {
            throw new FileNotFoundException("Default file not found: " + defaultFilePath);
        }
    
        FileUtils.copyFile(defaultFile, file);
        System.out.println("File populated with default values: " + file.getAbsolutePath());
    }

    public static void printTable(Table table) {
        if (table.getRows().isEmpty()) {
            System.out.println("The table is empty.");
            return;
        }
        System.out.println(table); 
    }

    public static void editTable(Table table) {
        String key = InputUtils.getStringInput("Enter key to edit: ");
        String value = InputUtils.getStringInput("Enter new value: ");

        boolean edited = false;
        for (Map<String, String> row : table.getRows()) {
            if (row.containsKey(key)) {
                row.put(key, value);
                edited = true;
                break;
            }
        }
        if (!edited) {
            System.out.println("Key not found.");
        } else {
            System.out.println("Key " + key + " updated with new value: " + value);
        }
    }

    public static void searchTable(Table table) {
        String searchString = InputUtils.getStringInput("Enter search string: ");
    
        Map<String, Integer> charOccurrences = new HashMap<>();
        Map<String, Set<String>> matchIndices = new HashMap<>();
    
        for (int i = 0; i < table.getRows().size(); i++) {
            Map<String, String> row = table.getRows().get(i);
            for (Map.Entry<String, String> entry : row.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
    
                countOccurrences(key, searchString, entry, charOccurrences, matchIndices);
                countOccurrences(value, searchString, entry, charOccurrences, matchIndices);
            }
        }
        if (charOccurrences.isEmpty()) {
            System.out.println("No matches found.");
        } else {
            for (Map.Entry<String, Integer> entry : charOccurrences.entrySet()) {
                String searchKey = entry.getKey();
                int count = entry.getValue();
                Set<String> indicesSet = matchIndices.get(searchKey);
                if (indicesSet != null && !indicesSet.isEmpty()) {
                    System.out.println("The character \"" + searchKey + "\" occurred " + count + " times at: " + indicesSet);
                }
            }
        }
    }
    
    private static void countOccurrences(String target, String searchString, Map.Entry<String, String> entry, Map<String, Integer> charOccurrences, Map<String, Set<String>> matchIndices) {
        int index = target.indexOf(searchString);
        while (index != -1) {
            String matchKey = searchString;
            charOccurrences.put(matchKey, charOccurrences.getOrDefault(matchKey, 0) + 1);
    
            String entryDescription = entry.getKey() + ":" + entry.getValue();
            matchIndices.computeIfAbsent(matchKey, k -> new HashSet<>())
                        .add(entryDescription);
            index = target.indexOf(searchString, index + 1);
        }
    }    
    
    public static void addRow(Table table) {
        int numColumns = InputUtils.getIntInput("Enter the number of columns for the new row: ");

        Map<String, String> newRow = generateRandomRow(numColumns);
        table.getRows().add(newRow);
        System.out.println("New row added: " + newRow);
    }

    public static void resetTable(Table table) {
        int numRows = InputUtils.getIntInput("Enter number of rows: ");
        
        List<Map<String, String>> rows = new ArrayList<>();
        
        for (int i = 0; i < numRows; i++) {
            int numColumns = 2 + (int)(Math.random() * 4);
            Map<String, String> row = generateRandomRow(numColumns);
            
            rows.add(row);
        }
        table.setRows(rows);
        System.out.println("Table reset to " + numRows + " rows with random key-value pairs.");
    }    

    public static void sortRows(Table table) {
        List<Map<String, String>> sortedRows = table.getRows().stream()
            .map(row -> {
                List<Map.Entry<String, String>> sortedEntries = new ArrayList<>(row.entrySet());
                sortedEntries.sort((entry1, entry2) -> 
                    (entry1.getKey() + entry1.getValue()).compareTo(entry2.getKey() + entry2.getValue())
                );
                Map<String, String> sortedRow = new LinkedHashMap<>();
                for (Map.Entry<String, String> entry : sortedEntries) {
                    sortedRow.put(entry.getKey(), entry.getValue());
                }
                return sortedRow;
            })
            .sorted((row1, row2) -> {
                String concatenatedRow1 = row1.entrySet().stream()
                    .map(entry -> entry.getKey() + entry.getValue())
                    .collect(Collectors.joining());
                String concatenatedRow2 = row2.entrySet().stream()
                    .map(entry -> entry.getKey() + entry.getValue())
                    .collect(Collectors.joining());
                return concatenatedRow1.compareTo(concatenatedRow2);
            })
            .collect(Collectors.toList());

        table.setRows(sortedRows);
        System.out.println("Rows sorted.");
    }

    public static void displayOptions() {
        System.out.println("Choose an option:");
        System.out.println("1. Print");
        System.out.println("2. Edit");
        System.out.println("3. Search");
        System.out.println("4. Add Row");
        System.out.println("5. Sort Row");
        System.out.println("6. Reset");
        System.out.println("7. Save and Exit");
    }

    private static Map<String, String> parseRow(String line) {
        Map<String, String> row = new LinkedHashMap<>();
        String[] entries = line.split(" ");
        Pattern validPattern = Pattern.compile("^[\\x21-\\x7E]+$"); // ASCII characters from 33 to 126

        for (String entry : entries) {
            String[] keyValue = entry.split(":");
            if (keyValue.length == 2 && validPattern.matcher(keyValue[0].trim()).matches() &&
                validPattern.matcher(keyValue[1].trim()).matches()) {
                row.put(keyValue[0].trim(), keyValue[1].trim());
            } else {
                System.out.println("Invalid key-value pair encountered!");
            }
        }
        return row.isEmpty() ? null : row;
    }

    private static void writeRow(BufferedWriter writer, Map<String, String> row) throws IOException {
        StringBuilder line = new StringBuilder();
        for (Map.Entry<String, String> entry : row.entrySet()) {
            line.append(entry.getKey()).append(":").append(entry.getValue()).append(" ");
        }
        if (line.length() > 0) {
            line.setLength(line.length() - 1);
        }
        writer.write(line.toString());
        writer.newLine();
    }

    private static Map<String, String> generateRandomRow(int numColumns) {
        Map<String, String> row = new LinkedHashMap<>();
        for (int i = 0; i < numColumns; i++) {
            String key = RandomStringUtils.randomAlphanumeric(3);
            String value = RandomStringUtils.randomAlphanumeric(3);
            row.put(key, value);
        }
        return row;
    }

    private static Map<String, String> sortRowByValues(Map<String, String> row) {
        List<Map.Entry<String, String>> entries = new ArrayList<>(row.entrySet());
        entries.sort(Comparator.comparing(Map.Entry::getValue));

        Map<String, String> sortedRow = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : entries) {
            sortedRow.put(entry.getKey(), entry.getValue());
        }
        return sortedRow;
    }
}
