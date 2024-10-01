package com.exercise4;

import com.exercise4.models.Table;
import com.exercise4.services.TableService;
import com.exercise4.services.Impl.TableImpl;
import com.exercise4.utils.InputUtils;

import java.io.File;
import java.io.IOException;

public class MainApp {
    public static void main(String[] args) {
        String filePath = "default.txt";

        if (args.length > 0) {
            filePath = args[0];
        }

        File file = new File(filePath);

        TableService tableService = new TableImpl();

        try {
            if (!file.exists()) {
                tableService.createFile(file);
            } else if (file.length() == 0) {
                tableService.populateFileWithDefaultValues(file);
            }

            Table table = tableService.loadTable(file);

            while (true) {
                tableService.displayOptions();
                int choice = InputUtils.getIntInput("Enter your choice:");

                switch (choice) {
                    case 1:
                        tableService.printTable(table);
                        break;
                    case 2:
                        tableService.editTable(table);
                        break;
                    case 3:
                        tableService.searchTable(table);
                        break;
                    case 4:
                        tableService.addRow(table);
                        break;
                    case 5:
                        tableService.sortRows(table);
                        break;
                    case 6:
                        tableService.resetTable(table);
                        break;
                    case 7:
                        tableService.saveTable(table, file);
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
