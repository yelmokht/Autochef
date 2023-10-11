package ulb.infof307.g02.model.sql_model.ingredient_model;

import com.google.common.base.MoreObjects;
import ulb.infof307.g02.database.annotation.SQLValue;
import ulb.infof307.g02.model.sql_interface.SQLBaseModel;
import ulb.infof307.g02.model.sql_super_class.SQLBuilder;
import ulb.infof307.g02.model.sql_super_class.SQLModel;
import ulb.infof307.g02.model.sql_model.recipe_model.IngredientRecipe;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.IngredientShoppingList;
import ulb.infof307.g02.util.attributes.Department;
import ulb.infof307.g02.util.attributes.Unit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Ingredient extends SQLModel<Ingredient> implements SQLBaseModel {

	@SQLValue
	private final int id;
	@SQLValue
	private final String name;
	@SQLValue
	private final Department type;
	@SQLValue
	private final Unit unit;


	private Ingredient(Builder builder){
		super(Ingredient.class);
		this.id = builder.id;
		this.name = builder.name;
		this.type = builder.type;
		this.unit = builder.unit;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	public Department getType() {
		return type;
	}

	// Used by tables to print the value and not the enum (use getter automatically)
	public String getTypeName() {
		return type.getDepartment();
	}

	public Unit getUnit() {
		return unit;
	}

	@Override
	public void deleteOtherInstanceOfSelf() throws SQLException {
		IngredientRecipe.newBuilder()
				.setIngredientID(id)
				.build()
				.removeFromDatabase();
		IngredientShoppingList.newBuilder()
				.setIngredientID(id)
				.build()
				.removeFromDatabase();
	}

	@Override
	public final int hashCode() {
		return Objects.hash(id, name, type, unit);
	}

	@Override
	public final boolean equals(Object other) {
		if(other == null) {
			return false;
		}
		if(this == other) {
			return true;
		}
		if(!(other instanceof Ingredient)) {
			return false;
		}
		final var otherIngredient = (Ingredient) other;

		return Objects.equals(otherIngredient.name, this.name)
				&& Objects.equals(otherIngredient.type, this.type)
				&& Objects.equals(otherIngredient.unit, this.unit);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("name", name)
				.add("type", type)
				.add("unit", unit)
				.toString();
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder extends SQLBuilder<Builder, Ingredient> {

		private int id;

		private String name;

		private Department type;

		private Unit unit;

		private Builder() { }

		public Builder setId(int id) {
			this.id = id;
			return this;
		}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public Builder setType(Department type) {
			this.type = type;
			return this;
		}

		public Builder setUnit(Unit unit) {
			this.unit = unit;
			return this;
		}

		@Override
		public SQLBuilder<Builder, Ingredient> fromResultSet(ResultSet resultSet) {
			this.resultSet = resultSet;
			return this;
		}

		@Override
		protected Ingredient makeObjectFromResultSet() throws SQLException {
			return Ingredient.newBuilder()
					.setId(resultSet.getInt("id"))
					.setName(resultSet.getString("name"))
					.setType(Department.valueOf(resultSet.getString("type")))
					.setUnit(Unit.valueOf(resultSet.getString("unit")))
					.build();
		}

		@Override
		protected Ingredient makeObjectFromBuilder() {
			return new Ingredient(this);
		}

	}

}
