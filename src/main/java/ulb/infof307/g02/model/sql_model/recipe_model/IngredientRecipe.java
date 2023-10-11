package ulb.infof307.g02.model.sql_model.recipe_model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import ulb.infof307.g02.database.annotation.SQLValue;
import ulb.infof307.g02.model.model_interface.IngredientInstance;
import ulb.infof307.g02.model.sql_super_class.SQLBuilder;
import ulb.infof307.g02.model.sql_super_class.SQLModel;
import ulb.infof307.g02.model.sql_interface.SQLSubModel;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.util.attributes.Department;
import ulb.infof307.g02.util.attributes.Unit;

import java.sql.SQLException;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IngredientRecipe extends SQLModel<IngredientRecipe> implements SQLSubModel, IngredientInstance {

    @SQLValue("id_recipe")
    private int recipeID;
    @SQLValue("id_ingredient")
    private int ingredientID;

    private final String name;
    @SQLValue
    private double quantity;

    private Unit unit;

    private Department type;

    private IngredientRecipe(Builder builder) {
        super(IngredientRecipe.class);
        this.recipeID = builder.recipeID;
        this.ingredientID = builder.ingredientID;
        this.name = builder.name;
        this.quantity = builder.quantity;
        this.unit = builder.unit;
    }

    @JsonCreator
    public IngredientRecipe(@JsonProperty("name") String name, @JsonProperty("quantity") double quantity,
                            @JsonProperty("symbol") String unit) {
        super(IngredientRecipe.class);
        this.name = name;
        this.quantity = quantity;
        this.unit = Unit.fromString(unit);
    }

    @Override
    @JsonIgnore
    public int getId() {
        return recipeID;
    }

    @Override
    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public Unit getUnit() {
        return unit;
    }


    public Department getType() {
        try {
            var ingredientModel = Ingredient.newBuilder()
                                                                .setName(name)
                                                                .build()
                                                                .getModel();
            if(ingredientModel.isPresent()){
                return ingredientModel.get().getType();
            }
        } catch (SQLException e) {
            //
        }
        return Department.UNDEFINED;
    }

    @JsonIgnore
    public Department getDepartment() {
        return type;
    }

    @JsonIgnore
    public int getIngredientID() {
        return ingredientID;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public void setRecipeID(int recipeID) {
        this.recipeID = recipeID;
    }

    public void setIngredientID(int ingredientID) {
        this.ingredientID = ingredientID;
    }

    @Override
    public void setQuantity(double quantity, String unit) {
        if("kg".equals(unit)) {
            this.quantity = quantity * 1000.0;
        }
        else if("mL".equals(unit)) {
            this.quantity = quantity / 1000;
        }
        else if("mg".equals(unit)) {
            this.quantity = quantity / 1000;
        }
        else {
            this.quantity = quantity;
        }
    }

    @Override
    public void completeAndInsert(int ingredientId, int recipeId) throws SQLException {
        IngredientRecipe.newBuilder()
                .setRecipeID(recipeId)
                .setIngredientID(ingredientId)
                .setQuantity(this.quantity)
                .build()
                .insertIntoDatabase();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id_ingredient", ingredientID)
                .add("id_recipe", recipeID)
                .add("name", name)
                .add("quantity", quantity)
                .add("unit", unit)
                .toString();
    }

    @Override
    public final int hashCode() {
        return Objects.hash(name, quantity, unit, recipeID, ingredientID);
    }

    @Override
    public final boolean equals(Object other) {
        if(other == null) {
            return false;
        }
        if(this == other) {
            return true;
        }
        if(!(other instanceof IngredientRecipe)) {
            return false;
        }
        final var otherIngredient = (IngredientRecipe) other;

        return Objects.equals(otherIngredient.name, this.name)
                && Double.compare(otherIngredient.quantity, this.quantity) == 0
                && Objects.equals(otherIngredient.unit, this.unit)
                && otherIngredient.recipeID == this.recipeID
                && otherIngredient.ingredientID == this.ingredientID;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder extends SQLBuilder<Builder, IngredientRecipe> {

        private int recipeID;

        private int ingredientID;

        private  String name;

        private double quantity;

        private Unit unit;

        private Builder() { }

        public Builder setRecipeID(int recipeID) {
            this.recipeID = recipeID;
            return this;
        }

        public Builder setIngredientID(int ingredientID) {
            this.ingredientID = ingredientID;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setQuantity(double quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder setUnit(Unit unit) {
            this.unit = unit;
            return this;
        }

        @Override
        protected IngredientRecipe makeObjectFromResultSet() throws SQLException {
            final int retrievedIngredientId = resultSet.getInt("id_ingredient");
            final var ingredientName = Ingredient.newBuilder()
                    .setId(retrievedIngredientId)
                    .build()
                    .getModel()
                    .map(Ingredient::getName)
                    .orElseThrow(SQLException::new);
            final var ingredientUnit = Ingredient.newBuilder()
                    .setId(retrievedIngredientId)
                    .build()
                    .getModel()
                    .map(Ingredient::getUnit)
                    .orElseThrow(SQLException::new);

            return IngredientRecipe.newBuilder()
                    .setName(ingredientName)
                    .setIngredientID(retrievedIngredientId)
                    .setRecipeID(resultSet.getInt("id_recipe"))
                    .setQuantity(resultSet.getDouble("quantity"))
                    .setUnit(ingredientUnit)
                    .build();
        }

        @Override
        protected IngredientRecipe makeObjectFromBuilder() {
            return new IngredientRecipe(this);
        }
    }

}
