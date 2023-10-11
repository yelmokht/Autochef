package ulb.infof307.g02.util.attributes;

public enum Meal {

    STARTER("Entr\u00e9e"),
    DISH("Plat"),
    DESSERT("Dessert"),
    DRINK("Boisson"),
    UNSPECIFIED("Autres");

    private final String meal;

    Meal(String meal) {
        this.meal = meal;
    }

    public String getMeal() {
        return meal;
    }

    public static Meal fromString(String unit) {
        for (Meal mealEnum : Meal.values()) {
            if (mealEnum.getMeal().equalsIgnoreCase(unit)) {
                return mealEnum;
            }
        }
        return null;
    }

}
