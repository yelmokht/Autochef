package ulb.infof307.g02.model.sql_model.shoppinglist_model;

import com.google.common.base.MoreObjects;
import ulb.infof307.g02.database.annotation.SQLValue;
import ulb.infof307.g02.model.model_interface.IngredientInstance;
import ulb.infof307.g02.model.sql_super_class.SQLBuilder;
import ulb.infof307.g02.model.sql_interface.SQLSubModel;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.util.attributes.Department;
import ulb.infof307.g02.util.attributes.Unit;
import ulb.infof307.g02.model.sql_super_class.SQLModel;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Represents an individual item from a shopping list, similar to the IngredientRecipe class.
 */
public class IngredientShoppingList extends SQLModel<IngredientShoppingList> implements SQLSubModel, IngredientInstance {

    @SQLValue("id_shopping")
    private int shoppingListID;
    @SQLValue("id_ingredient")
    private int ingredientID;

    private String name;

    private final Department type;
    @SQLValue
    private double quantity;

    private Unit unit;

    private IngredientShoppingList(Builder builder) {
        super(IngredientShoppingList.class);
        this.shoppingListID = builder.shoppingListID;
        this.ingredientID = builder.ingredientID;
        this.name = builder.name;
        this.type = builder.type;
        this.quantity = builder.quantity;
        this.unit = builder.unit;
    }

    @Override
    public int getId() {
        return shoppingListID;
    }

    public int getIngredientID() {
        return this.ingredientID;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public double getQuantity() {
        return this.quantity;
    }

    public Unit getUnit(){
        return this.unit;
    }

    public void setUnit(Unit unit){
        this.unit = unit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIngredientID(int id) {
        this.ingredientID = id;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
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

    public Department getType() {
        return this.type;
    }

    @Override
    public void completeAndInsert(int ingredientId, int shoppingListId) throws SQLException {
        IngredientShoppingList.newBuilder()
                .setIngredientID(ingredientId)
                .setShoppingListID(shoppingListId)
                .setQuantity(this.quantity)
                .build()
                .insertIntoDatabase();
    }

    @Override
    public final int hashCode() {
        return Objects.hash(shoppingListID, ingredientID, name, type, quantity, unit);
    }

    @Override
    public final boolean equals(Object other) {
        if(other == null) {
            return false;
        }
        if(this == other) {
            return true;
        }
        if(!(other instanceof IngredientShoppingList)) {
            return false;
        }
        final var otherRecipe = (IngredientShoppingList) other;

        return otherRecipe.shoppingListID == this.shoppingListID
                && otherRecipe.ingredientID == this.ingredientID
                && Objects.equals(otherRecipe.name, this.name)
                && Objects.equals(otherRecipe.type, this.type)
                && Objects.equals(otherRecipe.quantity, this.quantity);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("shopping_list_id", shoppingListID)
                .add("ingredient_id", ingredientID)
                .add("ingredient_name", name)
                .add("type", type)
                .add("quantity", quantity)
                .add("unit", unit)
                .toString();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder extends SQLBuilder<Builder, IngredientShoppingList> {

        private int shoppingListID;

        private int ingredientID;

        private String name;

        private Department type;

        private Unit unit;

        private double quantity;

        private Builder() { }

        public Builder setShoppingListID(int shoppingListID) {
            this.shoppingListID = shoppingListID;
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

        public Builder setType(Department type) {
            this.type = type;
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
        protected IngredientShoppingList makeObjectFromResultSet() throws SQLException {
            final int retrievedId = resultSet.getInt("id_ingredient");
            final var ingredient = Ingredient.newBuilder()
                    .setId(retrievedId)
                    .build()
                    .getModel()
                    .orElseThrow();

            return IngredientShoppingList.newBuilder()
                    .setIngredientID(resultSet.getInt("id_ingredient"))
                    .setShoppingListID(resultSet.getInt("id_shopping"))
                    .setIngredientID(ingredient.getId())
                    .setName(ingredient.getName())
                    .setType(ingredient.getType())
                    .setQuantity(resultSet.getDouble("quantity"))
                    .build();
        }

        @Override
        protected IngredientShoppingList makeObjectFromBuilder() {
            return new IngredientShoppingList(this);
        }

    }

}
