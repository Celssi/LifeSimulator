package org.celssi.models;

import org.celssi.GameState;
import org.celssi.astar.Graph;
import org.celssi.astar.Node;
import org.celssi.constants.Settings;
import org.celssi.enums.LifePriority;
import org.celssi.enums.MapItemType;
import org.celssi.enums.Sex;
import org.celssi.utils.MapUtils;
import org.celssi.utils.MathUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class Creature extends MapItem {
    private final String id;
    private final LifePriority[] prioritiesInLife;
    private final double maxHealth;
    private final double healingSpeed;
    private final double attack;
    private final double defence;
    private final double hungerSpeed;
    private final double speed;
    private final double matingLevelSpeed;
    private final double visionRadius;
    private final Sex sex;
    private final int generation;
    private int age;
    private double health;
    private double hungerLevel;
    private double matingLevel;
    private double direction;
    private LifePriority currentTask;

    public Creature(double positionX, double positionY, LifePriority[] prioritiesInLife, double maxHealth, double healingSpeed, double attack, double defence, double hungerSpeed, double speed, double matingLevelSpeed, String character, double visionRadius, Sex sex, int generation) {
        super(positionX, positionY, character);
        this.sex = sex;
        this.generation = generation;
        this.id = UUID.randomUUID().toString();
        this.prioritiesInLife = prioritiesInLife;
        this.health = maxHealth;
        this.maxHealth = maxHealth;
        this.healingSpeed = healingSpeed;
        this.attack = attack;
        this.defence = defence;
        this.hungerSpeed = hungerSpeed;
        this.speed = speed;
        this.matingLevelSpeed = matingLevelSpeed;
        this.visionRadius = visionRadius;
        this.age = 0;
        this.hungerLevel = 0;
        this.matingLevel = 0;
        setAlive(true);
    }

    @Override
    public void loop() {
        increaseAge();
        increaseHunger();

        if (!isAlive()) {
            return;
        }

        increaseMating();
        increaseHealth();
        doNextAction();
    }

    @Override
    public MapItemType getMapItemType() {
        return MapItemType.CREATURE;
    }

    private void moveTowardsMate() {
        Optional<Creature> nearestVisibleCreature = GameState.GetCreatures()
                .stream()
                .filter(creature -> !getId().equals(creature.getId()))
                .filter(creature -> creature.getSex() != getSex())
                .filter(Creature::isReadyForMating)
                .filter(creature -> MathUtils.CalculateDistanceBetweenPoints(getPositionX(), getPositionY(), creature.getPositionX(), creature.getPositionY()) <= visionRadius)
                .min(Comparator.comparing(creature -> MathUtils.CalculateDistanceBetweenPoints(getPositionX(), getPositionY(), creature.getPositionX(), creature.getPositionY())));

        Creature creature = nearestVisibleCreature.orElse(null);

        if (creature == null) {
            moveToRandomDirection();
            return;
        }

        double distance = MathUtils.CalculateDistanceBetweenPoints(getPositionX(), getPositionY(), creature.getPositionX(), creature.getPositionY());

        if (distance < 1) {
            mate(creature);
        } else {
            if (distance < speed) {
                setPosition(creature.getPositionX(), creature.getPositionY());
                mate(creature);

            } else {
                moveTowardsTarget(creature);
            }
        }
    }

    void moveTowardsTarget(MapItem target) {
        Graph mapAsGraph = MapUtils.GetMapAsAGraph();
        int[] safePositionFrom = MapUtils.GetNearestSafePosition(getPositionX(), getPositionY());
        int[] safePositionTo = MapUtils.GetNearestSafePosition(target.getPositionX(), target.getPositionY());
        Node fromNode = MapUtils.GetNodeFromCoordinates(mapAsGraph.getNodes(), safePositionFrom[0], safePositionFrom[1]);
        Node toNode = MapUtils.GetNodeFromCoordinates(mapAsGraph.getNodes(), safePositionTo[0], safePositionTo[1]);

        List<Node> path = mapAsGraph.findPath(fromNode, toNode);

        if (path.size() == 0) {
            moveToRandomDirection();
            return;
        }

        Node nextDestination = path.size() > 1 ? path.get(1) : path.get(0);

        direction = Math.toRadians(MathUtils.CalculateAngleBetweenPoints(getPositionX(), getPositionY(), nextDestination.getTile().column(), nextDestination.getTile().row()));
        setPosition(getPositionX() + speed * Math.cos(direction), getPositionY() + speed * Math.sin(direction));
    }

    void moveToRandomDirection() {
        double newX;
        double newY;
        int tries = 0;

        do {
            // This makes moving less random and all over the place
            direction = MathUtils.RandomGenerator.nextFloat() > 0.7 ? Math.toRadians(MathUtils.RandomGenerator.nextDouble(360)) : direction;
            newX = getPositionX() + speed * Math.cos(direction);
            newY = getPositionY() + speed * Math.sin(direction);
            tries++;
        } while (!MapUtils.IsAllowedPosition(MathUtils.DoubleToNearestSafeInt(newX), MathUtils.DoubleToNearestSafeInt(newY)) && tries < 10);

        if (tries < 10) {
            setPosition(newX, newY);
        }
    }

    void setPosition(double x, double y) {
        double newX = x < 0 ? 0 : x;
        double newY = y < 0 ? 0 : y;
        newX = newX > Settings.MAP_WIDTH - 1 ? Settings.MAP_WIDTH - 1 : newX;
        newY = newY > Settings.MAP_HEIGHT - 1 ? Settings.MAP_HEIGHT - 1 : newY;

        setPositionX(newX);
        setPositionY(newY);
    }

    private void mate(Creature creature) {
        creature.resetMatingLevel();
        resetMatingLevel();

        Creature mother = sex == Sex.FEMALE ? this : creature;
        Creature father = sex == Sex.MALE ? this : creature;
        GameState.AddHerbivoreChild(mother, father);
    }

    private boolean isAdult() {
        return age >= Settings.ADULT_AGE;
    }

    private void increaseAge() {
        age += 1;

        if (age > Settings.MAX_AGE) {
            setAlive(false);
        }
    }

    private void doNextAction() {
        boolean actionFound = false;

        for (LifePriority priority : prioritiesInLife) {
            switch (priority) {
                case FOOD -> {
                    if (hungerLevel >= Settings.READY_TO_EAT_LEVEL) {
                        currentTask = LifePriority.FOOD;
                        actionFound = true;
                        moveTowardsFood();
                    }
                }
                case MATE -> {
                    if (isReadyForMating()) {
                        currentTask = LifePriority.MATE;
                        actionFound = true;
                        moveTowardsMate();
                    }
                }
                case EXIST -> {
                    currentTask = LifePriority.EXIST;
                    actionFound = true;
                    moveToRandomDirection();
                }
            }

            if (actionFound) {
                break;
            }
        }
    }

    protected void takeDamage(double damage) {
        health -= (damage - defence);

        if (health <= 0) {
            health = 0; // TODO
        }
    }

    private void attack(Creature creature) {
        creature.takeDamage(attack);
    }

    private void increaseHealth() {
        health += healingSpeed;

        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    private void increaseHunger() {
        hungerLevel += hungerSpeed;

        if (hungerLevel >= Settings.MAX_HUNGER_BEFORE_DEATH) {
            setAlive(false);
        }
    }

    private void increaseMating() {
        if (matingLevel < Settings.READY_TO_MATE_LEVEL) {
            matingLevel += matingLevelSpeed;
        }
    }

    private boolean isReadyForMating() {
        return isAdult() && matingLevel >= Settings.READY_TO_MATE_LEVEL;
    }

    protected void resetMatingLevel() {
        matingLevel = 0;
    }

    //region Basic getters
    public String getId() {
        return id;
    }

    public Sex getSex() {
        return sex;
    }

    public int getGeneration() {
        return generation;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getHealingSpeed() {
        return healingSpeed;
    }

    public double getAttack() {
        return attack;
    }

    public double getDefence() {
        return defence;
    }

    public double getHungerSpeed() {
        return hungerSpeed;
    }

    public double getSpeed() {
        return speed;
    }

    public double getMatingLevelSpeed() {
        return matingLevelSpeed;
    }

    public double getVisionRadius() {
        return visionRadius;
    }

    public double getHungerLevel() {
        return hungerLevel;
    }

    public void setHungerLevel(double hungerLevel) {
        this.hungerLevel = hungerLevel;
    }
    //endregion

    abstract void moveTowardsFood();

    abstract void eatFood(IEatable food);

    @Override
    public void printStatus() {
        System.out.println("ID: " + getId() + ", Sex: " + sex + ", Generation: " + generation + ", Current task: " +
                currentTask + ", Hunger level: " + hungerLevel + ", Mating level:" + matingLevel + ", Health:" +
                health + ", Is ready to mate: " + isReadyForMating() + ", Age: " + age + ", Vision: " + visionRadius +
                "Speed: " + speed + ", Position: (" + getPositionX() + ", " + getPositionY() + ")");
    }
}
