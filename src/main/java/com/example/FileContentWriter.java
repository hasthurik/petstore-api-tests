package com.example;

import java.io.FileWriter;
import java.io.IOException;

public class FileContentWriter {
    public static void writeFile(String fileName, String content) {

        try(FileWriter writer = new FileWriter("file.txt", false))
        {
            // запись всей строки
            String text = "Hello Gold!";
            writer.write(text);
            writer.flush();
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
    }
}
