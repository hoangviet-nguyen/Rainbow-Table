package com.rainbow.table;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.*;

/*
 * Implement some Tests according to
 * outlook mail
 * 
 */
public class RainbowTableTest {
    
    RainbowTable table = new RainbowTable();

    @Test
    public void testHash() {

        // given the strings
        String word = "0000000";
        String shouldBeHash = "29c3eea3f305d6b823f562ac4be35217";

        //when calling the method
        String myHash = table.hash(word);

        assertEquals(shouldBeHash, myHash);
    }

    @Test
    public void testReduce() {
        String hash = "29c3eea3f305d6b823f562ac4be35217";
        String shouldBeReduce = "87inwgn";

        String myReduce = table.reduce(hash, 0);
    
        assertEquals(myReduce, shouldBeReduce);
    }

    @Test
    public void testReduceHashChain() {
        String origin = "0000000";
        String firstHash = "29c3eea3f305d6b823f562ac4be35217";
        String firstRduce = "87inwgn";
        String layer1Hash = "12e2feb5a0feccf82a8d4172a3bd51c3";
        String layer1Reduce = "frrkiis";
        String layer2Hash = "437988e45a53c01e54d21e5dc4ae658a";
        String layer2Reduce = "dues6fg";
        String layer3Hash = "c0e9a2f2ae2b9300b6f7ef3e63807e84";

        List<String> testHashCase = List.of(firstHash, layer1Hash, layer2Hash, layer3Hash);
        List<String> testReduceCase = List.of(firstRduce, layer1Reduce, layer2Reduce);

        // assert all values are found
        for (String test : testHashCase) {
            int hashLayer = table.findHashLayer(test);
            assertTrue(hashLayer != -1);
        }

        for(String test: testReduceCase) {
            int reduceLayer = table.findReduceLayer(test);
            System.out.println(reduceLayer);
            //assertTrue(reduceLayer != -1);
        }

        // assert correct hash layers
        assertEquals(table.findHashLayer(firstHash), 0);
        assertEquals(table.findHashLayer(layer1Hash), 1);
        assertEquals(table.findHashLayer(layer2Hash), 2);
        assertEquals(table.findHashLayer(layer3Hash), 3);

        // assert correct reduce layers
        assertEquals(table.findReduceLayer(firstRduce), 0);
        assertEquals(table.findReduceLayer(layer1Reduce), 1);
        assertEquals(table.findReduceLayer(layer2Reduce), 2);
    }
}
