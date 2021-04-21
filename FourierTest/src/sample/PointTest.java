package sample;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    private Point p1;
    private Point p2;

    @BeforeEach
    void setUp() {
        p1 = new Point(-1, 1);
        p2 = new Point(2, 5);
    }

    @Test
    void distance() {

        double x = p2.x-p1.x;
        double y = p2.y-p1.y;
        double expected = Math.sqrt(x*x + y*y);
        double actual = p2.distance(p1);

        assertEquals(expected, actual);

        expected = 0;
        actual = p1.distance(p1);

        assertEquals(expected, actual);
    }

    @Test
    void testToString() {
        Point p3 = new Point(2.1234, -5.1234);
        Point p4 = new Point(2.6, -5.6);
        char decimalSeparator = new DecimalFormatSymbols().getDecimalSeparator();
        String expected;
        String actual;

        expected = "x: 2" + decimalSeparator + "12 y: -5" + decimalSeparator + "12";
        actual = p3.toString();
        assertEquals(expected, actual);

        expected = "x: 2" + decimalSeparator + "60 y: -5" + decimalSeparator + "60";
        actual = p4.toString();
        assertEquals(expected, actual);

        expected = "x: 2" + decimalSeparator + "123 y: -5" + decimalSeparator + "123";
        actual = p3.toString(3);
        assertEquals(expected, actual);

        expected = "x: 2" + decimalSeparator + "600 y: -5" + decimalSeparator + "600";
        actual = p4.toString(3);
        assertEquals(expected, actual);

    }

    @AfterEach
    void tearDown() {

    }
}