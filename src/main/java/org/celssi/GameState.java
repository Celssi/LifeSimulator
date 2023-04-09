package org.celssi;

import org.celssi.constants.Settings;
import org.celssi.enums.LifePriority;
import org.celssi.enums.MapItemType;
import org.celssi.models.Creature;
import org.celssi.models.Food;
import org.celssi.models.Herbivore;
import org.celssi.models.MapItem;
import org.celssi.utils.CreatureUtils;
import org.celssi.utils.MapUtils;
import org.celssi.utils.MathUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.util.function.Predicate.not;

public class GameState {
    public static final String[][] MAP = new String[Settings.MAP_HEIGHT][Settings.MAP_WIDTH];
    public static final List<MapItem> MAP_ITEMS = new ArrayList<>();
    public static final List<Creature> BIRTH_QUEUE = new ArrayList<>();
    public static int ROUND = 0;

    protected static void LoadMap(String path) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(path));

        for (int i = 0; i < allLines.size(); i++) {
            String[] characters = allLines.get(i).split("");
            System.arraycopy(characters, 0, MAP[i], 0, characters.length);
        }
    }

    protected static String[][] GetCopyOfMap() {
        String[][] mapCopy = new String[Settings.MAP_HEIGHT][Settings.MAP_WIDTH];

        for (int i = 0; i < MAP.length; i++) {
            mapCopy[i] = MAP[i].clone();
        }

        return mapCopy;
    }

    protected static void LoadCreatures() {
        for (int i = 0; i < Settings.INITIAL_MONSTER_COUNT; i++) {
            int[] position = MapUtils.GetRandomFreePosition();
            AddHerbivoreToPosition(position[0], position[1]);
        }
    }

    public static void AddHerbivoreChild(Creature mother, Creature father) {
        double maxHealth = CreatureUtils.evolveFromParents(mother.getMaxHealth(), father.getMaxHealth());
        double healingSpeed = CreatureUtils.evolveFromParents(mother.getHealingSpeed(), father.getHealingSpeed());
        double attack = CreatureUtils.evolveFromParents(mother.getAttack(), father.getAttack());
        double defence = CreatureUtils.evolveFromParents(mother.getDefence(), father.getDefence());
        double hungerSpeed = CreatureUtils.evolveFromParents(mother.getHungerSpeed(), father.getHungerSpeed());
        double speed = CreatureUtils.evolveFromParents(mother.getSpeed(), father.getSpeed());
        double matingLevelSpeed = CreatureUtils.evolveFromParents(mother.getMatingLevelSpeed(), father.getMatingLevelSpeed());
        double visionRadius = CreatureUtils.evolveFromParents(mother.getVisionRadius(), father.getVisionRadius());

        BIRTH_QUEUE.add(new Herbivore(mother.getPositionX(), mother.getPositionY(), new LifePriority[]{LifePriority.FOOD, LifePriority.MATE, LifePriority.EXIST}, maxHealth, healingSpeed, attack, defence, hungerSpeed, speed, matingLevelSpeed, "@", visionRadius, CreatureUtils.GetRandomSex(), mother.getGeneration() + 1, 10));
    }

    public static void AddHerbivoreToPosition(double x, double y) {
        BIRTH_QUEUE.add(new Herbivore(x, y, new LifePriority[]{LifePriority.FOOD, LifePriority.MATE, LifePriority.EXIST}, 10, 1, 1, 1, 1, 2, 1, "@", 15, CreatureUtils.GetRandomSex(), 0, 10));
    }

    protected static void CleanDeadItems() {
        GameState.MAP_ITEMS.removeIf(not(MapItem::isAlive));
    }

    protected static void RandomlySpawnFood() {
        if (Settings.FOOD_PROBABILITY > MathUtils.RandomGenerator.nextFloat() && GameState.GetFoods().size() < Settings.MAX_FOOD_COUNT) {
            int[] position = MapUtils.GetRandomFreePosition();
            GameState.MAP_ITEMS.add(new Food(position[0], position[1], 15, "o"));
        }
    }

    public static List<Creature> GetCreatures() {
        return MAP_ITEMS.stream().filter(mapItem -> mapItem.getMapItemType() == MapItemType.CREATURE).map(mapItem -> (Creature) mapItem).toList();
    }

    public static List<Food> GetFoods() {
        return MAP_ITEMS.stream().filter(mapItem -> mapItem.getMapItemType() == MapItemType.FOOD).map(mapItem -> (Food) mapItem).toList();
    }
}
