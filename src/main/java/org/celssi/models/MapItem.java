package org.celssi.models;

import org.celssi.enums.MapItemType;

public abstract class MapItem {
    private final String character;
    private double positionX;
    private double positionY;
    private boolean alive;

    protected MapItem(double positionX, double positionY, String character) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.character = character;
    }

    public double getPositionX() {
        return positionX;
    }

    protected void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    protected void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    public String getCharacter() {
        return character;
    }

    public abstract void loop();

    public abstract void printStatus();

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public abstract MapItemType getMapItemType();
}
