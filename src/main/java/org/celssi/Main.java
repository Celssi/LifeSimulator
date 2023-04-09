package org.celssi;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        GameEngine gameEngine;

        try {
            gameEngine = new GameEngine("map.txt");
            gameEngine.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}