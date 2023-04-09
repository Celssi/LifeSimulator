package org.celssi.utils;

import org.celssi.constants.Settings;

import java.util.Random;

public class MathUtils {
    public static final Random RandomGenerator = new Random(Settings.RANDOM_SEED);

    public static int DoubleToNearestSafeInt(double number) {
        return Math.round((float) number);
    }

    public static double CalculateDistanceBetweenPoints(double x1, double y1, double x2, double y2) {
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    public static double CalculateAngleBetweenPoints(double x1, double y1, double x2, double y2) {
        final double deltaY = (y2 - y1);
        final double deltaX = (x2 - x1);
        final double result = Math.toDegrees(Math.atan2(deltaY, deltaX));
        return (result < 0) ? (360d + result) : result;
    }
}
