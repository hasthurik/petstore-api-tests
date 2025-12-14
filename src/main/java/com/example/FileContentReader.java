package com.example;

import java.io.BufferedReader;
import java.io.IOException;

public class FileContentReader {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new java.io.FileReader("file.txt"));
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileContentWriter.writeFile("file.txt", "Hello Gold!");
    }
}
