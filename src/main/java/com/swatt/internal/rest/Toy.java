package com.swatt.internal.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Toy {
    public Toy() {
    }

    public String getTrades() {
        File file = new File("src/main/resources/trades.json");
        BufferedReader reader = null;
        String trades = "";

        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;

            while ((text = reader.readLine()) != null) {
                trades += text;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return trades;
    }
}
