package org.celssi.models;

import org.celssi.GameState;
import org.celssi.enums.LifePriority;
import org.celssi.enums.Sex;
import org.celssi.utils.MathUtils;

import java.util.Comparator;
import java.util.Optional;

public class Herbivore extends Creature implements IEatable {
    private final double foodValue;

    public Herbivore(double positionX, double positionY, LifePriority[] prioritiesInLife, double maxHealth, double healingSpeed, double attack, double defence, double hungerSpeed, double speed, double matingLevelSpeed, String character, double visionRadius, Sex sex, int generation, double foodValue) {
        super(positionX, positionY, prioritiesInLife, maxHealth, healingSpeed, attack, defence, hungerSpeed, speed, matingLevelSpeed, character, visionRadius, sex, generation);
        this.foodValue = foodValue;
    }

    @Override
    void moveTowardsFood() {
        Optional<Food> nearestVisibleFood = GameState.GetFoods()
                .stream()
                .filter(food -> MathUtils.CalculateDistanceBetweenPoints(getPositionX(), getPositionY(), food.getPositionX(), food.getPositionY()) <= getVisionRadius())
                .min(Comparator.comparing(food -> MathUtils.CalculateDistanceBetweenPoints(getPositionX(), getPositionY(), food.getPositionX(), food.getPositionY())));

        Food food = nearestVisibleFood.orElse(null);

        if (food == null) {
            moveToRandomDirection();
            return;
        }

        double distance = MathUtils.CalculateDistanceBetweenPoints(getPositionX(), getPositionY(), food.getPositionX(), food.getPositionY());

        if (distance < 1) {
            eatFood(food);
        } else {
            if (distance < getSpeed()) {
                setPosition(food.getPositionX(), food.getPositionY());
                eatFood(food);

            } else {
                moveTowardsTarget(food);
            }
        }
    }

    @Override
    void eatFood(IEatable food) {
        double foodValue = food.becomeEaten();
        setHungerLevel(getHungerLevel() - foodValue);

        if (getHungerLevel() < 0) {
            setHungerLevel(0);
        }
    }

    @Override
    public double becomeEaten() {
        setAlive(false);
        return foodValue;
    }
}
