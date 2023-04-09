package org.celssi.utils;

import com.diogonunes.jcolor.Attribute;

import java.io.IOException;

import static com.diogonunes.jcolor.Attribute.*;

public class ConsoleUtils {
    public static void ClearConsole() {
        try {
            String operatingSystem = System.getProperty("os.name");

            if (operatingSystem.contains("Windows")) {
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            } else {
                ProcessBuilder pb = new ProcessBuilder("clear");
                Process startProcess = pb.inheritIO().start();

                startProcess.waitFor();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Attribute GetTileColor(String tile) {
        switch (tile) {
            case "o" -> {
                return RED_TEXT();
            }
            case "*" -> {
                return YELLOW_TEXT();
            }
            case "@" -> {
                return GREEN_TEXT();
            }
            default -> {
                return WHITE_TEXT();
            }

        }
    }
}
