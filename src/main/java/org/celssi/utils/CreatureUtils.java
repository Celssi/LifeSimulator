package org.celssi.utils;

import org.celssi.enums.Sex;

public class CreatureUtils {
    public static Sex GetRandomSex() {
        Sex[] sexes = Sex.values();
        return sexes[MathUtils.RandomGenerator.nextInt(sexes.length)];
    }

    public static double evolveFromParents(double motherValue, double fatherValue) {
        double average = (motherValue + fatherValue) / 2;
        double[] possibleValues = new double[]{average, average * 1.1, average * 0.9};
        return possibleValues[MathUtils.RandomGenerator.nextInt(possibleValues.length)];
    }
}
