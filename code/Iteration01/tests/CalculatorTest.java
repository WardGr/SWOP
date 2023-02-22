import org.junit.Test;

import static org.junit.Assert.assertEquals;




public class CalculatorTest {
    @Test
    public void test() {
        assertEquals(2, Calculator.subtract(5, 3));
        assertEquals(15, Calculator.multiply(5, 3));
        assertEquals(34, Calculator.sum(5, 29));
    }
}