package com.exercise4.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.exercise4.models.Table;

public interface TableService {
    
    Table loadTable(File file);

    void saveTable(Table table, File file);

    void createFileWithDefaultValues(File tempFile) throws IOException;

    void populateFileWithDefaultValues(File tempFile) throws IOException;

    void printTable(Table table);

    void editTable(Table table);

    void searchTable(Table table);

    void addRow(Table table);

    void resetTable(Table table);

    void sortRows(Table table);

    void displayOptions();

    Map<String, String> parseRow(String line);

    void writeRow(BufferedWriter writer, Map<String, String> row) throws IOException;

    Map<String, String> generateRandomRow(int numColumns);

    Map<String, String> sortRowByValues(Map<String, String> row);

}
