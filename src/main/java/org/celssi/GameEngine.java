package org.celssi;

import org.celssi.constants.Settings;
import org.celssi.enums.GameStatus;
import org.celssi.models.MapItem;
import org.celssi.utils.ConsoleUtils;
import org.celssi.utils.MapUtils;

import java.io.IOException;

import static com.diogonunes.jcolor.Ansi.colorize;

public class GameEngine {
    private volatile GameStatus status = GameStatus.STOPPED;

    protected GameEngine(String path) throws IOException {
        GameState.LoadMap(path);
        GameState.LoadCreatures();
    }

    protected void stop() {
        status = GameStatus.STOPPED;
    }

    protected boolean isGameRunning() {
        return status == GameStatus.RUNNING;
    }

    protected void run() {
        status = GameStatus.RUNNING;
        Thread gameThread = new Thread(this::processGameLoop);
        gameThread.start();
    }

    private void processGameLoop() {
        while (isGameRunning()) {
            updateState();
            render();
            GameState.ROUND++;
            waitUntilNextLoop();
        }
    }

    protected void updateState() {
        System.out.println("Round: " + GameState.ROUND);
        ProcessBirthQueue();
        GameState.MAP_ITEMS.forEach(MapItem::loop);
        GameState.RandomlySpawnFood();
        GameState.CleanDeadItems();
    }

    private void ProcessBirthQueue() {
        GameState.MAP_ITEMS.addAll(GameState.BIRTH_QUEUE);
        GameState.BIRTH_QUEUE.clear();
    }

    protected void render() {
        ConsoleUtils.ClearConsole();

        String[][] mapToRender = GameState.GetCopyOfMap();

        GameState.MAP_ITEMS.forEach(mapItem -> {
            int[] safePosition = MapUtils.GetNearestSafePosition(mapItem.getPositionX(), mapItem.getPositionY());
            mapToRender[safePosition[1]][safePosition[0]] = mapItem.getCharacter();
        });

        for (String[] row : mapToRender) {
            for (String tile : row) {
                System.out.print(colorize(tile, ConsoleUtils.GetTileColor(tile)));
            }

            System.out.print("\n");
        }

        GameState.MAP_ITEMS.forEach(MapItem::printStatus);
    }

    private void waitUntilNextLoop() {
        try {
            Thread.sleep(Settings.LOOP_WAIT_TIME);
        } catch (InterruptedException e) {
            stop();
        }
    }
}
