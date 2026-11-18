package business;

import java.time.LocalDate;

public class Athlete {

    private String id;
    private String name;
    private LocalDate birthDate;
    private String delegationCode;
    private Profile profile;
    private int speed;
    private int endurance;
    private int strength;
    private int technique;
    private int precision;

    public Athlete(String id, String name, LocalDate birthDate, String delegationCode, Profile profile,
                   int speed, int endurance, int strength, int technique, int precision) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.delegationCode = delegationCode;
        this.profile = profile;
        this.speed = speed;
        this.endurance = endurance;
        this.strength = strength;
        this.technique = technique;
        this.precision = precision;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getDelegationCode() {
        return delegationCode;
    }

    public Profile getProfile() {
        return profile;
    }

    public int getSpeed() {
        return speed;
    }

    public int getEndurance() {
        return endurance;
    }

    public int getStrength() {
        return strength;
    }

    public int getTechnique() {
        return technique;
    }

    public int getPrecision() {
        return precision;
    }
}
