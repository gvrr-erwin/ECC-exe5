package com.exercise4.services;

import com.exercise4.models.Table;
import com.exercise4.services.Impl.TableImpl;
import com.exercise4.utils.InputUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.IOException;
import java.io.PrintStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;

import java.nio.charset.StandardCharsets;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServiceTest {

    private static TableService tableService;
    private Table table;

    @BeforeEach
    void setUp() {
        tableService = new TableImpl();
        table = new Table();

        Map<String, String> row1 = new HashMap<>();
        row1.put("987", "1AS");
        row1.put("123", "Us7");

        Map<String, String> row2 = new HashMap<>();
        row2.put("ZXY", "a9i");
        row2.put("ABC", "21D");

        List<Map<String, String>> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);

        table.setRows(rows);
    }

    @Test
    void testResetTable() {
        String input = "2";
        int expectedRows = Integer.parseInt(input);

        try (MockedStatic<InputUtils> inputUtilsMockedStatic = mockStatic(InputUtils.class)) {
            inputUtilsMockedStatic.when(() -> InputUtils.getStringInput(Mockito.anyString()))
                    .thenReturn(input); 

            tableService.resetTable(table);

            List<Map<String, String>> rows = table.getRows();
            assertEquals(expectedRows, rows.size(), "Number of rows should match the input");
        }
    }

    @Test
    void testResetTableWithNegativeInput() {
        String input = "-2"; 
        try (MockedStatic<InputUtils> inputUtilsMockedStatic = mockStatic(InputUtils.class)) {
            inputUtilsMockedStatic.when(() -> InputUtils.getStringInput(Mockito.anyString()))
                    .thenReturn(input, "-3","4"); 

            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            tableService.resetTable(table);

            List<Map<String, String>> rows = table.getRows();
            assertEquals(4, rows.size(), "Number of rows should match the valid input");
        }
    }

    @Test
    void testAddRow() {
        int input = 3;
        int expectedValue = 3;
        try (MockedStatic<InputUtils> mockedInputUtils = mockStatic(InputUtils.class)) {
            mockedInputUtils.when(() -> InputUtils.getIntInput(Mockito.anyString()))
                    .thenReturn(input);

            tableService.addRow(table);

            List<Map<String, String>> rows = table.getRows();
            assertEquals(expectedValue, rows.size(), "Table should have 3 rows after adding a new row"); 
            assertEquals(expectedValue, rows.get(2).size(), "New row should have 3 columns");
        }
    }

    @Test
    void testEditTable() {
        String input = "987";
        String inputNewValue = "DOG";
        String expectedValue = "DOG";
    
        try (MockedStatic<InputUtils> mockedInputUtils = mockStatic(InputUtils.class)) {
            mockedInputUtils.when(() -> InputUtils.getStringInput("Enter key to edit: "))
                    .thenReturn(input);
            mockedInputUtils.when(() -> InputUtils.getStringInput("Enter new value: "))
                    .thenReturn(inputNewValue);
    
            tableService.editTable(table);
    
            assertEquals(expectedValue, table.getRows().get(0).get("987"), "The value for key '987' should be updated to 'DOG'");
        }
    }    

    @Test
    void testLoadTable() throws Exception {
        File tempFile = File.createTempFile("testLoadTable", ".txt");

        String content = "987:1AS 123:Us7\nZXY:a9i ABC:21D";
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(content);
        }

        Table loadedTable = tableService.loadTable(tempFile);
        assertNotNull(loadedTable, "Loaded table should not be null");
        assertEquals(2, loadedTable.getRows().size(), "Table should have 2 rows loaded from the file");
        assertEquals("1AS", loadedTable.getRows().get(0).get("987"), "First row should have key '987' with value '1AS'");
        assertEquals("a9i", loadedTable.getRows().get(1).get("ZXY"), "Second row should have key 'ZXY' with value 'a9i'");

        tempFile.deleteOnExit(); 
    }

    @Test
    void testSaveTable() throws IOException {
        File tempFile = File.createTempFile("testSaveTable", ".txt");
        tableService.saveTable(table, tempFile);

        assertTrue(tempFile.exists(), "The saved file should exist.");
        List<String> lines = IOUtils.readLines(new FileInputStream(tempFile), StandardCharsets.UTF_8);
        assertEquals(2, lines.size(), "There should be 2 lines in the saved file.");
        assertTrue(lines.get(0).contains("987:1AS"), "First line should contain '987:1AS'");
        assertTrue(lines.get(1).contains("ZXY:a9i"), "Second line should contain 'ZXY:a9i'");

        tempFile.deleteOnExit(); 
    }

    @Test
    void testCreateFile() throws Exception {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "myCustomTempDir");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }

        File tempFile = new File(tempDir, "testCreateFile.txt");

        tableService.createFile(tempFile);

        assertTrue(tempFile.exists(), "The default values file should exist.");

        tempFile.deleteOnExit();
    }
    
    @Test
    void testPopulateFileWithDefaultValues() throws IOException {
        File targetFile = mock(File.class); 
        File defaultFile = mock(File.class); 
        
        when(defaultFile.exists()).thenReturn(true);

        try (MockedStatic<FileUtils> mockedFileUtils = mockStatic(FileUtils.class)) {
            
            tableService.populateFileWithDefaultValues(targetFile);

            mockedFileUtils.verify(() -> FileUtils.copyFile(any(File.class), eq(targetFile)), times(1));
        }
    }

    @Test
    void testSearchTable() {
        String searchString = "Us7";
        try (MockedStatic<InputUtils> mockedInputUtils = mockStatic(InputUtils.class)) {
            mockedInputUtils.when(() -> InputUtils.getStringInput("Enter search string: "))
                            .thenReturn(searchString);

            tableService.searchTable(table); 

            boolean isValueFound = table.getRows().stream()
                                    .anyMatch(row -> row.containsValue(searchString));

            assertTrue(isValueFound, "'Us7' should be found in the table.");
        }
    }

    @Test
    void testWriteRow() throws IOException {
        BufferedWriter mockWriter = mock(BufferedWriter.class);
        Map<String, String> row = new LinkedHashMap<>();
        row.put("987", "1AS");
        row.put("123", "Us7");
    
        tableService.writeRow(mockWriter, row);
    
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockWriter).write(argumentCaptor.capture());
        verify(mockWriter).newLine();
    
        String writtenContent = argumentCaptor.getValue().trim();
        assertEquals("987:1AS 123:Us7", writtenContent, "The written content should match the expected string.");
    }
}
