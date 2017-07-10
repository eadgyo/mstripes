package org.upes.model;

/**
 * Created by eadgyo on 10/07/17.
 */
public class NumberOp
{
    public static <N extends Number> N multiply(N number, Number multiplier) {
        Class<? extends Number> cls = number.getClass();
        if (cls == Integer.class) {
            return (N) Integer.valueOf(number.intValue() * multiplier.intValue());
        }
        if (cls == Long.class) {
            return (N) Long.valueOf(number.longValue() * multiplier.longValue());
        }
        if (cls == Float.class) {
            return (N) Float.valueOf(number.floatValue() * multiplier.floatValue());
        }
        if (cls == Double.class) {
            return (N) Double.valueOf(number.doubleValue() * multiplier.doubleValue());
        }
        throw new UnsupportedOperationException("unknown class: " + cls);
    }
}
