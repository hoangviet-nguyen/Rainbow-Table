package com.rainbow.table;


public class App {
    public static void main(String[] args) {
        GenerateRainbowTable g = new GenerateRainbowTable();
        RainbowTable table = new RainbowTable();
        String hash = "1d56a37fb6b08aa709fe90e12ca59e12";    
        String startText = table.findClearText(hash);
        String passwd = table.getPasswd(startText, hash);
        System.out.println("The starting Text to the given hash is: " + startText);
        System.out.println("The code which generates the hash is: " + passwd);
        System.out.println("The generated matches the hash: " + hash.equals(g.hash(passwd)));

    }
}    
