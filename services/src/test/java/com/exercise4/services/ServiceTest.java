package com.exercise4.services;

import com.exercise4.models.Table;
import com.exercise4.services.Impl.TableImpl;
import com.exercise4.utils.InputUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.FileInputStream;

import java.nio.charset.StandardCharsets;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

public class ServiceTest {

    private TableService tableService;
    private Table table;

    @BeforeEach
    void setUp() {
        tableService = new TableImpl(); // Replace with actual implementation if needed
        table = new Table();

        // Populate the table with initial rows for testing
        Map<String, String> row1 = new HashMap<>();
        row1.put("987", "1AS");
        row1.put("123", "Us7");

        Map<String, String> row2 = new HashMap<>();
        row2.put("ZXY", "a9i");
        row2.put("ABC", "21D");

        // Create a mutable list and add the initial rows
        List<Map<String, String>> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);

        // Set the rows in the table
        table.setRows(rows); // Add rows to the table
    }

    @Test
    void testWriteRow() throws IOException {
        // Arrange
        BufferedWriter mockWriter = mock(BufferedWriter.class); // Mock BufferedWriter
        Map<String, String> row = new LinkedHashMap<>(); // Use LinkedHashMap to preserve order
        row.put("987", "1AS");
        row.put("123", "Us7");
    
        // Act
        tableService.writeRow(mockWriter, row); // Call the method with the mocked writer and row
    
        // Capture the argument passed to the write() method
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockWriter).write(argumentCaptor.capture()); // Capture the argument for verification
        verify(mockWriter).newLine(); // Verify that a new line is written
    
        // Assert
        String writtenContent = argumentCaptor.getValue().trim(); // Trim trailing spaces in the captured value
        assertEquals("987:1AS 123:Us7", writtenContent, "The written content should match the expected string.");
    }


    @Test
    void testResetTable() {
        int inputRows = 2; // Example input
        try (MockedStatic<InputUtils> inputUtilsMockedStatic = mockStatic(InputUtils.class)) {
            inputUtilsMockedStatic.when(() -> InputUtils.getIntInput(Mockito.anyString()))
                    .thenReturn(inputRows);

            // Call the resetTable method
            tableService.resetTable(table);

            // Verify that the table has the expected number of rows
            List<Map<String, String>> rows = table.getRows();
            assertEquals(inputRows, rows.size(), "Number of rows should match the input");
        }
    }

    @Test
    void testAddRow() {
        // Mock the static InputUtils.getIntInput() method
        try (MockedStatic<InputUtils> mockedInputUtils = mockStatic(InputUtils.class)) {
            mockedInputUtils.when(() -> InputUtils.getIntInput(Mockito.anyString()))
                    .thenReturn(3);  // Simulate user input for 3 columns

            // Call the addRow method
            tableService.addRow(table);

            // Verify the table now has 3 rows (2 original + 1 new row)
            List<Map<String, String>> rows = table.getRows();
            assertEquals(3, rows.size(), "Table should have 3 rows after adding a new row");
            assertEquals(3, rows.get(2).size(), "New row should have 3 columns");
        }
    }

    @Test
    void testEditTable() {
        try (MockedStatic<InputUtils> mockedInputUtils = mockStatic(InputUtils.class)) {
            mockedInputUtils.when(() -> InputUtils.getStringInput("Enter key to edit: "))
                    .thenReturn("987");
            mockedInputUtils.when(() -> InputUtils.getStringInput("Enter new value: "))
                    .thenReturn("UpdatedValue");

            tableService.editTable(table);
            assertEquals("UpdatedValue", table.getRows().get(0).get("987"), "The value for key '987' should be updated to 'UpdatedValue'");
        }
    }

    @Test
    void testLoadTable() throws Exception {
        File tempFile = File.createTempFile("testLoadTable", ".txt");
        tempFile.deleteOnExit(); // Ensure the file is deleted after the test

        String content = "987:1AS 123:Us7\nZXY:a9i ABC:21D";
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(content);
        }

        Table loadedTable = tableService.loadTable(tempFile);
        assertNotNull(loadedTable, "Loaded table should not be null");
        assertEquals(2, loadedTable.getRows().size(), "Table should have 2 rows loaded from the file");
        assertEquals("1AS", loadedTable.getRows().get(0).get("987"), "First row should have key '987' with value '1AS'");
        assertEquals("a9i", loadedTable.getRows().get(1).get("ZXY"), "Second row should have key 'ZXY' with value 'a9i'");
    }

    @Test
    void testSaveTable() throws IOException {
        // Create a temporary file
        File tempFile = File.createTempFile("testSaveTable", ".txt");
        tempFile.deleteOnExit(); // Ensure the file is deleted after the test

        // Call the saveTable method
        tableService.saveTable(table, tempFile);

        // Verify that the file exists and has the expected content
        assertTrue(tempFile.exists(), "The saved file should exist.");
        List<String> lines = IOUtils.readLines(new FileInputStream(tempFile), StandardCharsets.UTF_8);
        assertEquals(2, lines.size(), "There should be 2 lines in the saved file.");
        assertTrue(lines.get(0).contains("987:1AS"), "First line should contain '987:1AS'");
        assertTrue(lines.get(1).contains("ZXY:a9i"), "Second line should contain 'ZXY:a9i'");
    }

    @Test
    void testCreateFileWithDefaultValues() throws Exception {
        // Use a user-specific temporary directory
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "myCustomTempDir");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }

        // Create a temporary file in this directory
        File tempFile = new File(tempDir, "testCreateFileWithDefaultValues.txt");
        tempFile.deleteOnExit();

        // Call the createFileWithDefaultValues method
        tableService.createFileWithDefaultValues(tempFile);

        // Verify that the file exists
        assertTrue(tempFile.exists(), "The default values file should exist.");
    }

    @Test
    void testPopulateFileWithDefaultValues() throws IOException {
        // Create a temporary file for the test
        File tempFile = File.createTempFile("testPopulateFileWithDefaultValues", ".txt");
        tempFile.deleteOnExit(); // Ensure the file is deleted after the test

        // Create a sample default values file for copying
        File defaultFile = new File("src/main/resources/default.txt");
        try (FileWriter writer = new FileWriter(defaultFile)) {
            writer.write("DefaultKey:DefaultValue\n");
        }

        // Call the populateFileWithDefaultValues method
        tableService.populateFileWithDefaultValues(tempFile);

        // Verify that the file contains the default values
        List<String> lines = IOUtils.readLines(new FileInputStream(tempFile), StandardCharsets.UTF_8);
        assertEquals(1, lines.size(), "There should be 1 line in the populated file.");
        assertTrue(lines.get(0).contains("DefaultKey:DefaultValue"), "The populated file should contain the default key-value pair.");
    }

    @Test
    void testSearchTable() {
        try (MockedStatic<InputUtils> mockedInputUtils = mockStatic(InputUtils.class)) {
            mockedInputUtils.when(() -> InputUtils.getStringInput("Enter search string: "))
                    .thenReturn("Us7");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outputStream));

            tableService.searchTable(table);

            System.setOut(originalOut);
            String output = outputStream.toString().trim();
            assertTrue(output.contains("The character \"Us7\" occurred 1 times at:"), 
                       "Output should indicate that 'Us7' was found in the table.");
        }
    }

    // Mock row creation from a string input
    @SuppressWarnings("unused")
    private Map<String, String> createMockRow(String line) {
        Map<String, String> row = new HashMap<>();
        String[] pairs = line.split(" "); // Split on space
        for (String pair : pairs) {
            String[] keyValue = pair.split(":"); // Split on colon
            if (keyValue.length == 2) {
                row.put(keyValue[0], keyValue[1]); // Add to map
            }
        }
        return row;
    }
}
