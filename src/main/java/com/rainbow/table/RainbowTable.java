package com.rainbow.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.HashMap;

public class RainbowTable {
    
    private List<String> passwords;
    private List<Character> characters;
    private Map<String, String> rainbowtable;
    private List<Set<String>> hashReduceChain;

    public RainbowTable() {
        passwords = new ArrayList<>();
        characters = new ArrayList<>();
        rainbowtable = new HashMap<>();
        hashReduceChain = new ArrayList<>();

        fillCharacters();
        generatePasswords("0000000", 0, new AtomicInteger());
        passwords.stream().limit(10).forEach(System.out::println);
    }

    private String reduce(String hash, int layer) {
        // TO DO IMPLEMENT METHOD
        return null;
    }

    private String hash(String password) {
        //TO DO IMPLEMENT METHOD
        return null;
    }

    private void fillCharacters(){
        
        // add characters 1-9
        for (int i = 0; i < 10; i++) {
            char character = (char) ('0' + i);
            characters.add(character);
        }

        // add lowercast characters a-z
        for(int i = 0; i < 26; i++) {
            char character = (char)('a' + i);
            characters.add(character);
        }
    }

    private void generatePasswords(String password, int index, AtomicInteger numberOfPasswords) {
        StringBuffer currentPassword = new StringBuffer(password);
        for(char c : characters) {

            if(index >= password.length() || numberOfPasswords.get() >= 2000) {
                return;
            }

            currentPassword.setCharAt(index, c);
            generatePasswords(currentPassword.toString(), index + 1, numberOfPasswords);

            if(!passwords.contains(currentPassword.toString())) {
                passwords.add(currentPassword.toString());
                numberOfPasswords.incrementAndGet();
            }
        }
    }

    public int findHashLayer(String hash) {
        int currentLayer = 0;
        for(Set<String> layer : hashReduceChain) {
            if(layer.contains(hash)) {
                return currentLayer;
            }
            currentLayer++;
        }

        return -1;
    }

    private String followChain(String hash, int layer) {
        // TO DO IMPLEMENT METHOD
        return null;
    }

    public String findClearTest(String Hash) {
        // TO DO IMPLEMENT METHOD
        return null;
    }
}
