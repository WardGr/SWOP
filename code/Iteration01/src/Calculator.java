public class Calculator {
    public static int sum(int x, int y) {
        return x + y;
    }

    public static int subtract(int x, int y) {
        return x - y;
    }

    public static int multiply(int x, int y) {
        return x * y;
    }

    public static int divide(int x, int y) {
        return x / y;
    }

    public static int modulo(int x, int y) {
        return x % y;
    }

    public static int power(int x, int y) {
        return (int) Math.pow(x, y);
    }
}