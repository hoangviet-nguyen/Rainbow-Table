package com.rainbow.table;


public class App {
    public static void main(String[] args) {
        String hash = "1d56a37fb6b08aa709fe90e12ca59e12";
        RainbowTable table = new RainbowTable();
        String clearText = table.findClearText(hash);
        System.out.println("The clear Text to the given hash is: " + clearText);
        System.out.println("The hash was generated on layer: " + table.findHashLayer(hash));
    }
}    
