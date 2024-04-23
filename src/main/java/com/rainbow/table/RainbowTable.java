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
    private List<Set<String>> hashChain;
    private List<Set<String>> reduceChain;
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

        rainbowtable.keySet().stream().forEach(System.out::println);
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
        for(Set<String> layer : hashChain) {
            if(layer.contains(hash)) {
                return currentLayer;
            }
            currentLayer++;
        }
        return -1;
    }

    public int findReduceLayer(String hash) {
        int currentLayer = 0;
        for(Set<String> layer : reduceChain) {
            if(layer.contains(hash)) {
                return currentLayer;
            }
            currentLayer++;
        }
        return -1;
    }

    private void initLayers(){
        for(int i = 0; i < 2000; i++) {
            hashChain.add(new HashSet<>());
            reduceChain.add(new HashSet<>()); 
        }
    }

    private void initRainbowTable() {
        for(int i = 0; i < 1; i++) {

            String password = passwords.get(i);
            int overallLayer = 0;
            int reduceLayer = 0;
            int hashLayer = 0;
            String value = hash(password);

            // make the reduce and hash chain, each chain has 2000 entries
            while (overallLayer < 2) {
                
                // first hash then reduce => hash has all even ints
                if(overallLayer % 2 == 0) {
                    hashChain.get(hashLayer).add(value);
                    hashLayer++;
                    value = reduce(value, reduceLayer);
                } else {
                    reduceChain.get(reduceLayer).add(value);
                    reduceLayer++;
                    value = hash(value);
                }
                overallLayer++;
            }

            // the last hash value is stored in a map for easy access
            rainbowtable.put(hash(value), password);
        }
    }

    private String followChain(String hash, int layer) {
        return null;
    }

    public String findClearTest(String Hash) {
        // TO DO IMPLEMENT METHOD
        return null;
    }
}
