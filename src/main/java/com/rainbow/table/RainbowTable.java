package com.rainbow.table;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

public class RainbowTable {
    
    private List<String> passwords;
    private List<Character> characters;
    private Map<String, String> rainbowtable;
    private List<List<String>> hashChain;
    private List<List<String>> reduceChain;
    private MessageDigest algorithm;

    public RainbowTable() {
        passwords = new ArrayList<>();
        characters = new ArrayList<>();
        rainbowtable = new HashMap<>();
        hashChain = new ArrayList<>();
        reduceChain = new ArrayList<>();

        try {
            algorithm = MessageDigest.getInstance("MD5");
        } catch(NoSuchAlgorithmException nsae) {
            System.out.println("The algorithm was not found exit");
            throw new RuntimeException(nsae.getCause());
        }

        fillCharacters();
        generatePasswords("0000000", 0, new AtomicInteger());
        initLayers();
        initRainbowTable();
    }

    public String reduce(String hash, int layer) {

        int passwordLenght = 7;
        BigInteger hashValue = new BigInteger(hash, 16);
        hashValue = hashValue.add(BigInteger.valueOf(layer));
        String result = "";
        BigInteger size = BigInteger.valueOf(characters.size());
        
        for(int i = 0; i < passwordLenght; i++) {
            int index = hashValue.mod(size).intValueExact();
            hashValue = hashValue.divide(size);
            char r = characters.get(index);
            result = r + result;
        }
         
        return result;
    }

    public String hash(String password) {
        algorithm.update(password.getBytes());
        byte[] digest = algorithm.digest();
        BigInteger convertBytes = new BigInteger(1, digest);
        String hashValue = convertBytes.toString(16);
        return hashValue;
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
        for(List<String> layer : hashChain) {
            if(layer.contains(hash)) {
                return currentLayer;
            }
            currentLayer++;
        }
        return -1;
    }

    public int findReduceLayer(String hash) {
        int currentLayer = 0;
        for(List<String> layer : reduceChain) {
            if(layer.contains(hash)) {
                return currentLayer;
            }
            currentLayer++;
        }
        return -1;
    }

    private void initLayers(){
        for(int i = 0; i < 2000; i++) {
            hashChain.add(new ArrayList<>());
            reduceChain.add(new ArrayList<>()); 
        }
    }

    private void initRainbowTable() {
        for(String password : passwords) {
            int layer = 0;
            String value = hash(password); // first hash then reduce

            // make the reduce and hash chain, each chain has 2000 entries
            while (layer < 1999) {
                hashChain.get(layer).add(value);   
                value = reduce(value, layer);
                reduceChain.get(layer).add(value);
                value = hash(value);
                layer++;
            }

            // the last reduce value is stored in a map for easy access
            rainbowtable.put(reduce(value, layer), password);
        }
    }

    private String followChain(String hash, int layer) {
        if(layer < 0) {
            System.out.println("Reached the base case");
            return null;
        }

        int currentLayer = layer;
        String reducedHash = hash;

        // follow the chain
        while(currentLayer < 2000) {
            reducedHash = reduce(reducedHash, currentLayer);
            if(rainbowtable.containsKey(reducedHash)) {
                String result = rainbowtable.get(reducedHash);
                return result;
            }

            reducedHash = hash(reducedHash);
            currentLayer++;
        }

        // if hash is not found
        return followChain(hash, layer-1);
    }

    public String findClearText(String hash) {
        String result = followChain(hash, hashChain.size());
        return result;
    }

    public String getPasswd(String password, int hashLayer) {
        int passIndex = passwords.indexOf(password);
        return reduceChain.get(hashLayer -1).get(passIndex);
    }
}
