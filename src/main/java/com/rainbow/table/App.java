package com.rainbow.table;


public class App {
    public static void main(String[] args) {
        RainbowTable table = new RainbowTable();
        String hash = "1d56a37fb6b08aa709fe90e12ca59e12";    
        String startText = table.findClearText(hash);
        int hashLayer = table.findHashLayer(hash);
        System.out.println("The starting Text to the given hash is: " + startText);
        System.out.println("The hash was generated on layer: " + hashLayer);
        System.out.println("The code which generates the hash is: " + table.getPasswd(startText, hashLayer));
    }
}    
