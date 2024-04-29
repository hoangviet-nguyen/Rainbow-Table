package com.rainbow.table;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class RainbowTable {

    GenerateRainbowTable generateTable = new GenerateRainbowTable();
    
    private final Map<String, String> rainbowTable = new HashMap<>();
    private final List<List<String>> hashChain = new ArrayList<>();
    private static final String path = "src/main/resources/";

    /**
     * Konstruktor der Klasse, initialisiert alle benötigten Datenstrukturen und generiert die Rainbow-Tabelle
     */
    public RainbowTable() {
        try {
            // Lese rainbowTable von rainbowTable.txt
            Scanner scanner = new Scanner(new File(path + "rainbowTable.txt"));
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
        return followChain(hash, 2000);
    }

    /**
     * Gibt das Passwort zurück, das den gegebenen Hash-Wert auf der gegebenen Ebene generiert
     * @param passwd Das Passwort
     * @param hashLayer Die Ebene
     * @return Das Passwort, das den Hash-Wert generiert
     */
    public String getPasswd(String startingpoint, String hash) {
        String currentHash = generateTable.hash(startingpoint);
        String result = "";
    
        int i = 0;
        while (i < 2000) {
        
            if(currentHash.equals(hash)) {
                return result;
            }
        
            result = generateTable.reduce(currentHash, i);
            currentHash = generateTable.hash(result);
            i++;
        }

        return result; 
    }
}
