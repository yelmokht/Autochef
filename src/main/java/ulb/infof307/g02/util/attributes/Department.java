package ulb.infof307.g02.util.attributes;

public enum Department {

    FRUITS_AND_VEGETABLES("Fruits et l\u00e9gumes"),
    DAIRY ("Produits laitiers"),
    BUTCHERY("Viande"),
    FISHERY("Poisson"),
    BEVERAGES("Boisson"),
    GROCERY("\u00c9picerie"),
    BAKERY("Boulangerie"),
    UNDEFINED("Autre");

    private final String department;

    Department(String department) {
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public static Department fromString(String department) {
        for (Department departmentEnum : Department.values()) {
            if (departmentEnum.getDepartment().equalsIgnoreCase(department)) {
                return departmentEnum;
            }
        }
        return null;
    }

}
