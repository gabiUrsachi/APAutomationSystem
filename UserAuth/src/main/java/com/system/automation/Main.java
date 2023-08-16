package com.system.automation;

import com.system.automation.presentation.utils.Password;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        String password = "random";

        System.out.println(Password.encrypt(password));
    }
}