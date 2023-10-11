package ulb.infof307.g02.util.attributes;

public enum QuantityUnit {

	KILOGRAM("kg") {
		@Override
		public double actualValue(double value) {
			return value * 1000.0;
		}
	},
	GRAM("g") {
		@Override
		public double actualValue(double value) {
			return value;
		}
	},
	MILLIGRAM("mg") {
		@Override
		public double actualValue(double value) {
			return value / 1000.0;
		}
	},
	LITER("l") {
		@Override
		public double actualValue(double value) {
			return value;
		}
	},
	MILLILITER("ml") {
		@Override
		public double actualValue(double value) {
			return value / 1000.0;
		}
	};

	private final String symbol;

	QuantityUnit(String symbol) {
		this.symbol = symbol;
	}

	public abstract double actualValue(double value);

	public String getSymbol() {
		return symbol;
	}

}
