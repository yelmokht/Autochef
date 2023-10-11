package ulb.infof307.g02.util.attributes;

public enum MealTime {

    BREAKFAST("Petit-d\u00e9jeuner"),
    DINNER("D\u00eener"),
    SUPPER("Souper");

    private final String name;

    MealTime(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
