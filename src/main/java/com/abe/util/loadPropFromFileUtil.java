package com.abe.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class loadPropFromFileUtil {
    public static Properties loadPropFromFile(String fileName) {
        Properties prop = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get(fileName));
             InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            prop.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return prop;
    }
}
