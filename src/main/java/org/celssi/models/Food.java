package org.celssi.models;

import org.celssi.enums.MapItemType;

public class Food extends MapItem implements IEatable {
    private final double foodValue;

    public Food(double positionX, double positionY, double foodValue, String character) {
        super(positionX, positionY, character);
        this.foodValue = foodValue;
        setAlive(true);
    }

    protected double getFoodValue() {
        return foodValue;
    }

    @Override
    public void loop() {

    }

    @Override
    public MapItemType getMapItemType() {
        return MapItemType.FOOD;
    }

    @Override
    public double becomeEaten() {
        setAlive(false);
        return foodValue;
    }

    @Override
    public void printStatus() {
        System.out.println("Is eaten:" + !isAlive() + ", Food value: " + getFoodValue() +
                ", Position: (" + getPositionX() + ", " + getPositionY() + ")");
    }
}
