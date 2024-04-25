package com.rainbow.table;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class RainbowTable {

    GenerateRainbowTable generateTable = new GenerateRainbowTable();
    
    private final List<String> passwords = new ArrayList<>();
    private final Map<String, String> rainbowTable = new HashMap<>();
    private final List<List<String>> hashChain = new ArrayList<>();
    private final List<List<String>> reduceChain = new ArrayList<>();
    private static final String path = "src/main/resources/";

    /**
     * Konstruktor der Klasse, initialisiert alle benötigten Datenstrukturen und generiert die Rainbow-Tabelle
     */
    public RainbowTable() {
        try {
            // Lese passwords von passwords.txt
            Scanner scanner = new Scanner(new File(path + "passwords.txt"));
            while (scanner.hasNextLine()) {
                passwords.add(scanner.nextLine());
            }
            scanner.close();

            // Lese reduceChain von reduceChain.txt
            scanner = new Scanner(new File(path + "reduceChain.txt"));
            List<String> currentList = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals("---")) {
                    reduceChain.add(currentList);
                    currentList = new ArrayList<>();
                } else {
                    currentList.add(line);
                }
            }
            scanner.close();

            // Lese die erste Hälfte der hashChain von hashChain1.txt
            scanner = new Scanner(new File(path + "hashChain1.txt"));
            currentList = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals("---")) {
                    hashChain.add(currentList);
                    currentList = new ArrayList<>();
                } else {
                    currentList.add(line);
                }
            }
            scanner.close();

            // Lese die zweite Hälfte der hashChain von hashChain2.txt
            scanner = new Scanner(new File(path + "hashChain2.txt"));
            currentList = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals("---")) {
                    hashChain.add(currentList);
                    currentList = new ArrayList<>();
                } else {
                    currentList.add(line);
                }
            }
            scanner.close();

            // Lese rainbowTable von rainbowTable.txt
            scanner = new Scanner(new File(path + "rainbowTable.txt"));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" : ");
                rainbowTable.put(parts[0], parts[1]);
            }
            scanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading from file: " + e.getMessage());
        }
    }

    /**
     * Findet die Ebene, auf der der gegebene Hash-Wert zuerst erscheint
     * @param hash Der zu suchende Hash-Wert
     * @return Die Ebene, auf der der Hash-Wert gefunden wurde, oder -1, wenn er nicht gefunden wurde
     */
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


    /**
     * Findet die Ebene, auf der der gegebene reduzierte Hash-Wert zuerst erscheint
     * @param hash Der zu suchende reduzierte Hash-Wert
     * @return Die Ebene, auf der der reduzierte Hash-Wert gefunden wurde, oder -1, wenn er nicht gefunden wurde
     */
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
            reducedHash = generateTable.reduce(reducedHash, currentLayer);
            if(rainbowTable.containsKey(reducedHash)) {
                return rainbowTable.get(reducedHash);
            }

            reducedHash = generateTable.hash(reducedHash);
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

    /**
     * Gibt das Passwort zurück, das den gegebenen Hash-Wert auf der gegebenen Ebene generiert
     * @param passwd Das Passwort
     * @param hashLayer Die Ebene
     * @return Das Passwort, das den Hash-Wert generiert
     */
    public String getPasswd(String passwd, int hashLayer) {
        int startIndex = passwords.indexOf(passwd);
        return reduceChain.get(hashLayer -1).get(startIndex);
    }
}
