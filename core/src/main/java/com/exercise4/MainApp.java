package com.exercise4;

import com.exercise4.models.Table;
import com.exercise4.services.TableService;
import com.exercise4.utils.InputUtils;

import java.io.File;
import java.io.IOException;

public class MainApp {
    public static void main(String[] args) {
        String filePath = "default.txt"; // Default file path

        if (args.length > 0) {
            filePath = args[0];
        }

        File file = new File(filePath);

        try {
            if (!file.exists()) {
                TableService.createFileWithDefaultValues(file);
            } else if (file.length() == 0) {
                TableService.populateFileWithDefaultValues(file);
            }

            Table table = TableService.loadTable(file);

            while (true) {
                TableService.displayOptions();
                int choice = InputUtils.getIntInput("Enter your choice:");

                switch (choice) {
                    case 1:
                        TableService.printTable(table);
                        break;
                    case 2:
                        TableService.editTable(table);
                        break;
                    case 3:
                        TableService.searchTable(table);
                        break;
                    case 4:
                        TableService.addRow(table);
                        break;
                    case 5:
                        TableService.sortRows(table);
                        break;
                    case 6:
                        TableService.resetTable(table);
                        break;
                    case 7:
                        TableService.saveTable(table, file);
                        System.out.println("File saved and exiting.");
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}
