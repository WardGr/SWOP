package Tests;

import Domain.Tuple;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TupleTest {
    private Tuple<String,String> stringTuple;
    private Tuple<Integer,Double> numberTuple;
    @Before
    public void setUp(){
        stringTuple = new Tuple<>("first", "second");
        numberTuple = new Tuple<>(5, 0.6);
    }
    @Test
    public void testGetters(){
        assertEquals("first", stringTuple.getFirst());
        assertEquals("second", stringTuple.getSecond());

        assertEquals(5, numberTuple.getFirst(),0);
        assertEquals(0.6, numberTuple.getSecond(), 0);
    }

    @Test
    public void testToString(){
        assertEquals("(first, second)", stringTuple.toString());
        assertEquals("(5, 0.6)", numberTuple.toString());
    }

    @Test
    public void testEquals(){
        assertEquals(new Tuple<>("first", "second"), stringTuple);
        assertNotEquals(new Tuple<>("false", "second"), stringTuple);
        assertNotEquals(new Tuple<>("first", "false"), stringTuple);
        assertNotEquals(new Tuple<>(5, "false"), stringTuple);
        assertNotEquals(new Tuple<>("first", 0.6), stringTuple);
        assertNotEquals("second", stringTuple);
        assertNotEquals(stringTuple, "second");


        assertEquals(new Tuple<>(5, 0.6), numberTuple);
        assertNotEquals(new Tuple<>(4, 0.6), numberTuple);
        assertNotEquals(new Tuple<>(5, 0.5), numberTuple);
        assertNotEquals(new Tuple<>("first", 0.6), numberTuple);
        assertNotEquals(new Tuple<>(5, "0.6"), numberTuple);
        assertNotEquals(0.6, stringTuple);
        assertNotEquals(stringTuple, 0.6);
    }

    @Test
    public void testHashCode(){
        assertEquals("second".hashCode(), stringTuple.hashCode());
        assertNotEquals("first".hashCode(), stringTuple.hashCode());

        assertEquals(Double.hashCode(0.6), numberTuple.hashCode(), 0);
        assertNotEquals(Integer.hashCode(5), numberTuple.hashCode(), 0);

    }
}
