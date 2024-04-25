package com.rainbow.table;

/**
 * Die FileInput Klasse ist verantwortlich für die Erstellung einer Instanz der GenerateRainbowTable Klasse
 * und das Aufrufen der WriteToFile Methode.
 */
public class FileInput {

    /**
     * Die main Methode ist der Einstiegspunkt für die Ausführung des Programms.
     * Sie erstellt eine Instanz der GenerateRainbowTable Klasse und ruft die WriteToFile Methode auf,
     * um die generierten Daten in verschiedene .txt-Dateien zu schreiben.
     * @param args Die Kommandozeilenargumente
     */
    public static void main(String[] args) {
        GenerateRainbowTable table = new GenerateRainbowTable();
        table.WriteToFile();
    }
}
