package business;

import java.time.LocalDate;

/**
 * un atleta de los juegos ctiene sus datos personales y sus 5 atributos
 * una vez creado no cambia
 */
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

    /**
     * crea un atleta con todos sus datos
     * @param id identificador unico (ATH-XXX)
     * @param name nombre del atleta
     * @param birthDate fecha de nacimiento
     * @param delegationCode codigo de su delegacion
     * @param profile perfil del atleta
     * @param speed velocidad (1 a 100)
     * @param endurance resistencia (1 a 100)
     * @param strength fuerza (1 a 100)
     * @param technique tecnica (1 a 100)
     * @param precision precision (1 a 100)
     */
    public Athlete(String id, String name, LocalDate birthDate, String delegationCode, Profile profile, int speed, int endurance, int strength, int technique, int precision) {
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

    /**
     * devuelve el id del atleta
     * @return el id
     */
    public String getId() {
        return id;
    }

    /**
     * saca el nombre del atleta
     * @return el nombre
     */
    public String getName() {
        return name;
    }

    /**
     * devuelve cuando nacio
     * @return la fecha de nacimiento
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * devuelve el codigo de la delegacion a la que pertenece
     * @return el codigo de la delegacion
     */
    public String getDelegationCode() {
        return delegationCode;
    }

    /**
     * devuelve su perfil (balanced, power, endurance o technical)
     * @return el perfil
     */
    public Profile getProfile() {
        return profile;
    }

    /**
     * cuanta velocidad tiene
     * @return la velocidad
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * cuanta resistencia tiene
     * @return la resistencia
     */
    public int getEndurance() {
        return endurance;
    }

    /**
     * cuanta fuerza tiene
     * @return la fuerza
     */
    public int getStrength() {
        return strength;
    }

    /**
     * cuanta tecnica tiene
     * @return la tecnica
     */
    public int getTechnique() {
        return technique;
    }

    /**
     * cuanta precision tiene
     * @return la precision
     */
    public int getPrecision() {
        return precision;
    }
}
