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
    
    private final List<String> passwords;
    private final List<Character> characters;
    private final Map<String, String> rainbowTable;
    private final List<Set<String>> hashChain;
    private final List<Set<String>> reduceChain;
    private final MessageDigest algorithm;

    /**
     * Konstruktor der Klasse, initialisiert alle benötigten Datenstrukturen und generiert die Rainbow-Tabelle
     */
    public RainbowTable() {
        passwords = new ArrayList<>();
        characters = new ArrayList<>();
        rainbowTable = new HashMap<>();
        hashChain = new ArrayList<>();
        reduceChain = new ArrayList<>();

        try {
            algorithm = MessageDigest.getInstance("MD5");
        } catch(NoSuchAlgorithmException nsae) {
            throw new RuntimeException(nsae.getCause());
        }

        fillCharacters();
        generatePasswords("0000000", 0, new AtomicInteger());
        initLayers();
        initRainbowTable();
    }

    /**
     * Reduziert den gegebenen Hash-Wert in eine Zeichenkette, die als Passwort dient
     * @param hash Der zu reduzierende Hash-Wert
     * @param layer Die Ebene, auf der die Reduzierung stattfindet
     * @return Das resultierende reduzierte Passwort
     */
    public String reduce(String hash, int layer) {

        int passwordLength = 7;
        BigInteger hashValue = new BigInteger(hash, 16);
        hashValue = hashValue.add(BigInteger.valueOf(layer));
        StringBuilder result = new StringBuilder();
        BigInteger size = BigInteger.valueOf(characters.size());
        
        for(int i = 0; i < passwordLength; i++) {
            int index = hashValue.mod(size).intValueExact();
            hashValue = hashValue.divide(size);
            char r = characters.get(index);
            result.insert(0, r);
        }
         
        return result.toString();
    }

    /**
     * Erzeugt einen Hash-Wert aus dem gegebenen Passwort
     * @param password Das Passwort, das gehasht werden soll
     * @return Der resultierende Hash-Wert
     */
    public String hash(String password) {
        algorithm.update(password.getBytes());
        byte[] digest = algorithm.digest();
        BigInteger convertBytes = new BigInteger(1, digest);
        return convertBytes.toString(16);
    }

    /**
     * Füllt die Liste der möglichen Zeichen für Passwörter
     */
    private void fillCharacters(){
        
        // add characters 1-9
        for (int i = 0; i < 10; i++) {
            char character = (char) ('0' + i);
            characters.add(character);
        }

        // add lowercase characters a-z
        for(int i = 0; i < 26; i++) {
            char character = (char)('a' + i);
            characters.add(character);
        }
    }

    /**
     * Generiert rekursiv alle möglichen Passwörter
     * @param password Das aktuelle Passwort
     * @param index Der aktuelle Index
     * @param numberOfPasswords Die Anzahl der generierten Passwörter
     */
    private void generatePasswords(String password, int index, AtomicInteger numberOfPasswords) {
        StringBuilder currentPassword = new StringBuilder(password);
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

    /**
     * Findet die Ebene, auf der der gegebene Hash-Wert zuerst erscheint
     * @param hash Der zu suchende Hash-Wert
     * @return Die Ebene, auf der der Hash-Wert gefunden wurde, oder -1, wenn er nicht gefunden wurde
     */
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


    /**
     * Findet die Ebene, auf der der gegebene reduzierte Hash-Wert zuerst erscheint
     * @param hash Der zu suchende reduzierte Hash-Wert
     * @return Die Ebene, auf der der reduzierte Hash-Wert gefunden wurde, oder -1, wenn er nicht gefunden wurde
     */
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

    /**
     * Initialisiert die Listen für die Hash- und Reduzier ketten
     */
    private void initLayers(){
        for(int i = 0; i < 2000; i++) {
            hashChain.add(new HashSet<>());
            reduceChain.add(new HashSet<>()); 
        }
    }

    /**
     * Initialisiert die Rainbow-Tabelle
     */
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
            rainbowTable.put(reduce(value, layer), password);
        }
    }


    /**
     * Folgt der Kette von Hash- und Reduzier werten, um das ursprüngliche Passwort zu finden
     * @param hash Der zu suchende Hash-Wert
     * @param layer Die Ebene, auf der die Suche beginnt
     * @return Das gefundene Passwort oder null, wenn es nicht gefunden wurde
     */
    private String followChain(String hash, int layer) {
        if(layer < 0) {
            return null;
        }

        int currentLayer = layer;
        String reducedHash = hash;

        // follow the chain
        while(currentLayer < 2000) {
            reducedHash = reduce(reducedHash, currentLayer);
            if(rainbowTable.containsKey(reducedHash)) {
                return rainbowTable.get(reducedHash);
            }

            reducedHash = hash(reducedHash);
            currentLayer++;
        }

        // if hash is not found
        return followChain(hash, layer-1);
    }

    /**
     * Findet das Klartext-Passwort für den gegebenen Hash-Wert
     * @param hash Der zu suchende Hash-Wert
     * @return Das gefundene Passwort oder null, wenn es nicht gefunden wurde
     */
    public String findClearText(String hash) {
        return followChain(hash, hashChain.size());
    }
}
