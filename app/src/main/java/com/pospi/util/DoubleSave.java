package com.pospi.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 保留两位小数
 */
public class DoubleSave {
    public static double doubleSaveTwo(double q) {
        BigDecimal b = new BigDecimal(q);
        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }
    public static String formatDouble(double d) {
       return String.format("%.2f", d);
    }
    public static String formatThree(double d) {
        return String.format("%.3f", d);
    }
    public static double doubleSaveThree(double q) {
        BigDecimal b = new BigDecimal(q);
        double f1 = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }
}
