package com.rainbow.table;
import static org.junit.Assert.assertEquals;
import org.junit.*;

/*
 * Implement some Tests according to
 * outlook mail
 * 
 */
public class RainbowTableTest {
    
    RainbowTable table = new RainbowTable();
    GenerateRainbowTable generateTable = new GenerateRainbowTable();

    @Test
    public void testHash() {

        // given the strings
        String word = "0000000";
        String shouldBeHash = "29c3eea3f305d6b823f562ac4be35217";

        //when calling the method
        String myHash = generateTable.hash(word);

        assertEquals(shouldBeHash, myHash);
    }

    @Test
    public void testReduce() {
        String hash = "29c3eea3f305d6b823f562ac4be35217";
        String shouldBeReduce = "87inwgn";

        String myReduce = generateTable.reduce(hash, 0);
    
        assertEquals(myReduce, shouldBeReduce);
    }

    @Test
    public void testFollowChain() {

        // this hash should be found
        String layer3Hash = "c0e9a2f2ae2b9300b6f7ef3e63807e84";
        String password = "0000000";
        String foundPassword = table.findClearText(layer3Hash);

        assertEquals(password, foundPassword);

        // random hash which should not be found
        String randomPassword = "1234";
        String hash = generateTable.hash(randomPassword);

        foundPassword = table.findClearText(hash);
        assertEquals(foundPassword, null);
    }
}
