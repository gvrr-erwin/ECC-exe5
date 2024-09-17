package com.exercise4.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputUtils {
    private static final Scanner scanner = new Scanner(System.in);

    // Method to get a trimmed string input from the user
    public static String getStringInput(String prompt) {
        System.out.print(prompt);
        return StringUtils.trimToEmpty(scanner.next());
    }

    // Method to get a valid integer input from the user, with error handling
    public static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.next(); // Clear the invalid input
            }
        }
    }

    // Method to consume any remaining newline characters in the buffer
    public static void consumeLine() {
        scanner.nextLine(); // Consume the remaining newline character
    }
}
