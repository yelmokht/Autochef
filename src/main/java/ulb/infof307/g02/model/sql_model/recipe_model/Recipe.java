package ulb.infof307.g02.model.sql_model.recipe_model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.MoreObjects;
import ulb.infof307.g02.database.DatabaseTable;
import ulb.infof307.g02.database.annotation.SQLValue;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.model.sql_interface.SQLBaseModel;
import ulb.infof307.g02.model.sql_super_class.SQLBuilder;
import ulb.infof307.g02.model.sql_interface.SQLContainer;
import ulb.infof307.g02.model.sql_super_class.SQLModel;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_model.schedule_model.RecipeSchedule;
import ulb.infof307.g02.util.attributes.Diet;
import ulb.infof307.g02.util.attributes.Meal;

import java.sql.SQLException;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe extends SQLModel<Recipe> implements SQLContainer, SQLBaseModel {

	@SQLValue
	private int id;
	@SQLValue("id_user")
	private int userID;
	@SQLValue
	private String name;
	@SQLValue
	private int servings;
	@SQLValue
	private Meal type;
	@SQLValue
	private Diet diet;

	private List<IngredientRecipe> ingredients;
	@SQLValue
	private List<String> instructions;

	public Recipe() {
		super(Recipe.class);
	}

	private Recipe(Builder builder) {
		super(Recipe.class);
		this.id = builder.id;
		this.userID = builder.userID;
		this.name = builder.name;
		this.servings = builder.servings;
		this.type = builder.type;
		this.diet = builder.diet;
		this.ingredients = builder.ingredients;
		this.instructions = builder.instructions;
	}

	public void setID(int id) {
		this.id = id;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	@Override
	@JsonIgnore
	public int getId() {
		return id;
	}

	@JsonIgnore
	public int getUserID() {
		return userID;
	}

	@Override
	public String getName() {
		return name;
	}

	public int getServings() {
		return servings;
	}

	public Meal getType() {
		return type;
	}

	public Diet getDiet() {
		return diet;
	}

	public List<IngredientRecipe> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<IngredientRecipe> ingredients) {
		this.ingredients = ingredients;
	}

	public List<String> getInstructions() {
		return instructions;
	}

	@Override
	public void insertSubModels() throws SQLException {
		for (IngredientRecipe ingredientRecipe : ingredients) {
			try {
				Optional<Ingredient> ingredient = Ingredient.newBuilder()
						.setName(ingredientRecipe.getName())
						.build()
						.getModel();

				Optional<Recipe> recipe = Recipe.newBuilder()
						.setName(this.getName())
						.build()
						.getModel();

				if (ingredient.isPresent() && recipe.isPresent()) {
					ingredientRecipe.completeAndInsert(ingredient.get().getId(), recipe.get().getId());
				}
			} catch (SQLException e) {
				Autochef.getLogger().error("Couldn't insert a SubModel of Recipe in the database");
				throw new SQLException("Failed to insert SubModel into database");
			}
		}
	}

	@Override
	public void deleteSubModels() throws SQLException {
		IngredientRecipe.newBuilder()
				.setRecipeID(id)
				.build()
				.removeFromDatabase();
	}

	@Override
	public void deleteOtherInstanceOfSelf() throws SQLException {
		RecipeSchedule.newBuilder()
				.setRecipeID(id)
				.build()
				.removeFromDatabase();
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("recipeName", name)
				.add("servings", servings)
				.add("type", type)
				.add("diet", diet)
				.add("ingredients", ingredients)
				.add("instructions", instructions)
				.toString();
	}

	@Override
	public final int hashCode() {
		return Objects.hash(id, name, servings, type, diet, ingredients, instructions);
	}

	@Override
	public final boolean equals(Object other) {
		if(other == null) {
			return false;
		}
		if(this == other) {
			return true;
		}
		if(!(other instanceof Recipe)) {
			return false;
		}
		final var otherRecipe = (Recipe) other;

		return Objects.equals(otherRecipe.name, this.name)
				&& otherRecipe.servings == this.servings
				&& Objects.equals(otherRecipe.type, this.type)
				&& Objects.equals(otherRecipe.diet, this.diet)
				&& Objects.equals(otherRecipe.ingredients, this.ingredients)
				&& Objects.equals(otherRecipe.instructions, this.instructions);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends SQLBuilder<Builder, Recipe> {

		@JsonIgnore
		private int id;

		private int userID;

		private String name;

		private int servings;

		private Meal type;

		private Diet diet = Diet.UNSPECIFIED;

		private List<IngredientRecipe> ingredients = new ArrayList<>();

		private List<String> instructions = new ArrayList<>();

		private Builder() { }

		public Builder setID(int id) {
			this.id = id;
			return this;
		}
		public Builder setUserID(int userID) {
			this.userID = userID;
			return this;
		}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public Builder setServings(int servings) {
			this.servings = servings;
			return this;
		}

		public Builder setType(Meal type) {
			this.type = type;
			return this;
		}

		public Builder setDiet(Diet diet) {
			this.diet = diet;
			return this;
		}

		public Builder setIngredients(List<IngredientRecipe> ingredients) {
			this.ingredients = ingredients;
			return this;
		}

		public Builder addIngredients(IngredientRecipe ingredients) {
			this.ingredients.addAll(Set.of(ingredients));
			return this;
		}

		public Builder setInstructions(List<String> instructions) {
			this.instructions = instructions;
			return this;
		}

		@Override
		protected Recipe makeObjectFromResultSet() throws SQLException {
			final int recipeId = resultSet.getInt(DatabaseTable.RECIPE_TABLE.getKeyId());
			final List<IngredientRecipe> ingredientRecipeList = new ArrayList<>(
					IngredientRecipe.newBuilder()
					.setRecipeID(recipeId)
					.build()
					.selectFromDatabase()
			);
			var instructions = resultSet.getString("instructions");
			List<String> instructionsList;
			if (Objects.equals(instructions, "[]")) {
				instructionsList = new ArrayList<>();
			} else {
				instructionsList = new ArrayList<>(List.of(
						resultSet.getString("instructions")
								.replace("[", "")
								.replace("]", "")
								.split(", "))
				);
			}

			return Recipe.newBuilder()
					.setID(recipeId)
					.setUserID(Integer.parseInt(resultSet.getString("id_user")))
					.setName(resultSet.getString("name"))
					.setServings(resultSet.getInt("servings"))
					.setType(Meal.valueOf(resultSet.getString("type")))
					.setDiet(Diet.valueOf(resultSet.getString("diet")))
					.setIngredients(ingredientRecipeList)
					.setInstructions(instructionsList)
					.build();
			}

		@Override
		protected Recipe makeObjectFromBuilder() {
			return new Recipe(this);
		}
	}
}
