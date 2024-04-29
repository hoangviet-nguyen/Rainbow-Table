package com.rainbow.table;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;



public class GenerateRainbowTable {

    private final List<String> passwords;
    private final List<Character> characters;
    private final Map<String, String> rainbowTable;
    private static MessageDigest algorithm;

    /**
     * Konstruktor der Klasse, initialisiert alle benötigten Datenstrukturen und generiert die Rainbow-Tabelle
     */
    public GenerateRainbowTable () {
        passwords = new ArrayList<>();
        rainbowTable = new HashMap<>();
        characters = new ArrayList<>();

        try {
            algorithm = MessageDigest.getInstance("MD5");
        } catch(NoSuchAlgorithmException nsae) {
            throw new RuntimeException(nsae.getCause());
        }

        fillCharacters();
        generatePasswords("0000000", 0, new AtomicInteger());
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
                value = reduce(value, layer);
                value = hash(value);
                layer++;
            }

            // the last reduce value is stored in a map for easy access
            rainbowTable.put(reduce(value, layer), password);
        }
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
     * Schreibt die generierten Daten in verschiedene .txt-Dateien
     */
    public void WriteToFile() {
        initRainbowTable();

        String path = "src/main/resources/";
        try {

            // Schreibe rainbowTable in rainbowTable.txt
            PrintWriter writer = new PrintWriter(path + "rainbowTable.txt", "UTF-8");
            for (Map.Entry<String, String> entry : rainbowTable.entrySet()) {
                writer.println(entry.getKey() + " : " + entry.getValue());
            }
            writer.close();

        } catch (IOException e) {
            System.out.println("An error occurred while writing to file: " + e.getMessage());
        }
    }
}
