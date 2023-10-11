package ulb.infof307.g02.util.attributes;

public enum Unit {

    KILOGRAM("kg", false),
    GRAM("g", true),
    LITER("l", true),
    MILLILITER("ml", false),
    UNIT("unité", true),
    EURO("€", false);

    private final String unit;

    private final boolean baseUnit;

    Unit(String unit, boolean baseUnit) {
        this.unit = unit;
        this.baseUnit = baseUnit;
    }

    public String getName() {
        return unit;
    }

    public boolean isBaseUnit() {
        return baseUnit;
    }

    public static Unit fromString(String unit) {
        for (Unit unitEnum : Unit.values()) {
            if (unitEnum.getName().equalsIgnoreCase(unit)) {
                return unitEnum;
            }
        }
        return null;
    }
}