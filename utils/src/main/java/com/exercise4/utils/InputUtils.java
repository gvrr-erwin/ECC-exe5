package com.exercise4.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputUtils {
    private static final Scanner scanner = new Scanner(System.in);

    public static String getStringInput(String prompt) {
        System.out.print(prompt);
        return StringUtils.trimToEmpty(scanner.next());
    }

    public static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.next(); 
            }
        }
    }
    public static void consumeLine() {
        scanner.nextLine(); 
    }
}
