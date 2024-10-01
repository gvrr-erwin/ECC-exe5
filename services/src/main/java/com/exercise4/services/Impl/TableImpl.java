package com.exercise4.services.Impl;

import com.exercise4.models.Table;
import com.exercise4.utils.InputUtils;
import com.exercise4.services.TableService;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TableImpl implements TableService {

    private final String DEFAULT_VALUE = "src/main/resources/default.txt";

    @Override
    public Table loadTable(File file) {
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

    @Override
    public void saveTable(Table table, File file) {
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

    @Override
    public void createFile(File file) throws IOException {
        if (file.createNewFile()) {
            System.out.println("File created: " + file.getAbsolutePath());
            populateFileWithDefaultValues(file);
        } else {
            throw new IOException("File could not be created: " + file.getAbsolutePath());
        }
    }

    @Override
    public void populateFileWithDefaultValues(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("Target file cannot be null");
        }

        String defaultFilePath = DEFAULT_VALUE;
        File defaultFile = new File(defaultFilePath);
    
        if (!defaultFile.exists()) {
            throw new FileNotFoundException("Default file not found: " + defaultFilePath);
        }
    
        FileUtils.copyFile(defaultFile, file);
        System.out.println("File populated with default values: " + file.getAbsolutePath());
    }

    @Override
    public void printTable(Table table) {
        if (table.getRows().isEmpty()) {
            System.out.println("The table is empty.");
            return;
        }
        System.out.println(table); 
    }

    @Override
    public void editTable(Table table) {
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

    @Override
    public void searchTable(Table table) {
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

    private void countOccurrences(String target, String searchString, Map.Entry<String, String> entry, 
                                  Map<String, Integer> charOccurrences, Map<String, Set<String>> matchIndices) {
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

    @Override
    public void addRow(Table table) {
        int numColumns = InputUtils.getIntInput("Enter the number of columns for the new row: ");

        Map<String, String> newRow = generateRandomRow(numColumns);
        table.getRows().add(newRow);
        System.out.println("New row added: " + newRow);
    }

    // @Override
    // public void resetTable(Table table) {
    //     int numRows = InputUtils.getIntInput("Enter number of rows: ");
        
    //     List<Map<String, String>> rows = new ArrayList<>();
        
    //     for (int i = 0; i < numRows; i++) {
    //         int numColumns = 2 + (int)(Math.random() * 4);
    //         Map<String, String> row = generateRandomRow(numColumns);
            
    //         rows.add(row);
    //     }
    //     table.setRows(rows);
    //     System.out.println("Table reset to " + numRows + " rows with random key-value pairs.");
    // }

    @Override
    public void resetTable(Table table) {
        int numRows = 0;

        while (true) {
            String input = InputUtils.getStringInput("Enter number of rows: ");
            
            if (input.isEmpty()) {
                System.out.println("No input detected. Please enter a number.");
                continue; 
            }

            try {
                numRows = Integer.parseInt(input);
                if (numRows < 1) {
                    System.out.println("Please enter a positive number.");
                    continue; 
                }
                break; 
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }

        List<Map<String, String>> rows = new ArrayList<>();
        
        for (int i = 0; i < numRows; i++) {
            int numColumns = 2 + (int)(Math.random() * 4);
            Map<String, String> row = generateRandomRow(numColumns);
            
            rows.add(row);
        }
        table.setRows(rows);
        System.out.println("Table reset to " + numRows + " rows with random key-value pairs.");
    }


    @Override
    public void sortRows(Table table) {
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
        System.out.println("Rows sorted successfully.");
    }

    @Override
    public Map<String, String> parseRow(String line) {
        Map<String, String> row = new LinkedHashMap<>();
        String[] entries = line.split(" ");
        Pattern validPattern = Pattern.compile("^[\\x21-\\x7E]+$");

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

    @Override
    public void writeRow(BufferedWriter writer, Map<String, String> row) throws IOException {
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

    @Override
    public Map<String, String> generateRandomRow(int numColumns) {
        Map<String, String> row = new LinkedHashMap<>();
        for (int i = 0; i < numColumns; i++) {
            String randomKey = RandomStringUtils.randomAlphabetic(5);
            String randomValue = RandomStringUtils.randomAlphanumeric(5);
            row.put(randomKey, randomValue);
        }
        return row;
    }

    @Override
    public Map<String, String> sortRowByValues(Map<String, String> row) {
        return row.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }
    
    @Override
    public void displayOptions() {
        System.out.println("Choose an option:\n"
            + "[1] Print Table\n"
            + "[2] Edit Table\n"
            + "[3] Search Table\n"
            + "[4] Add a Row\n"
            + "[5] Sort Rows\n"
            + "[6] Reset Table\n"
            + "[7] Exit\n");
    }
}
