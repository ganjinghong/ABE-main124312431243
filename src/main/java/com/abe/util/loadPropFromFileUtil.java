package com.abe.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class loadPropFromFileUtil {
    public static Properties loadPropFromFile(String fileName) {
        Properties prop = new Properties();
        try (FileInputStream in = new FileInputStream(fileName)){
            prop.load(in);
        }
        catch (IOException e){
            e.printStackTrace();
//            System.out.println(fileName + " load failed!");
            System.exit(-1);
        }
        return prop;
    }
}
