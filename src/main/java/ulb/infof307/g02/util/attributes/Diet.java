package ulb.infof307.g02.util.attributes;


public enum Diet {

	VEGETARIAN("V\u00e9g\u00e9tarien"),
	VEGAN("V\u00e9gan"),
	FISH("Poisson"),
	MEAT("Viande"),
	FISH_AND_MEAT("Viande et poisson"),
	UNSPECIFIED("Non sp\u00e9cifi\u00e9");

	private final String diet;

	Diet(String diet) {
		this.diet = diet;
	}

	public String getDiet() {
		return diet;
	}

	public static Diet fromString(String diet) {
		for (Diet dietEnum : Diet.values()) {
			if (dietEnum.getDiet().equalsIgnoreCase(diet)) {
				return dietEnum;
			}
		}
		return null;
	}
}
