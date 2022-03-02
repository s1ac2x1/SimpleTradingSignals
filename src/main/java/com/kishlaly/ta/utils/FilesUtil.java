package com.kishlaly.ta.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class FilesUtil {

    public static void appendToFile(String filename, String content) {
        File file = new File(filename);
        try {
            FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8, true);
            FileUtils.writeStringToFile(file, System.lineSeparator(), StandardCharsets.UTF_8, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToFile(String name, String content) {
        try {
            java.nio.file.Files.write(Paths.get(name), content.toString().getBytes());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
