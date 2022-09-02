package com.chat;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Helper {
    public static void assertListEquals(List<?> first, List<?> second){
        Assertions.assertEquals(first.size(), second.size());
        assertTrue(first.containsAll(second));
        assertTrue(second.containsAll(first));
    }
}
