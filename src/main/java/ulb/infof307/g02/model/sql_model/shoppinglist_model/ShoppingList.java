package ulb.infof307.g02.model.sql_model.shoppinglist_model;

import com.google.common.base.MoreObjects;
import ulb.infof307.g02.database.DatabaseTable;
import ulb.infof307.g02.database.annotation.SQLValue;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.model.model_interface.IngredientInstance;
import ulb.infof307.g02.model.sql_super_class.SQLBuilder;
import ulb.infof307.g02.model.sql_interface.SQLContainer;
import ulb.infof307.g02.model.sql_super_class.SQLModel;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;

import java.sql.SQLException;
import java.util.*;

public class ShoppingList extends SQLModel<ShoppingList> implements SQLContainer {

    @SQLValue
    private int id;
    @SQLValue("id_user")
    private final int userID;
    @SQLValue
    private final String name;

    private final List<IngredientShoppingList> ingredients;

    private ShoppingList(Builder builder) {
        super(ShoppingList.class);
        this.id = builder.id;
        this.userID = builder.userID;
        this.name = builder.name;
        this.ingredients = builder.ingredients;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public int getUserID() {return this.userID;}

    @Override
    public String getName() {
        return this.name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIngredients(List<IngredientShoppingList> ingredientShoppingLists) {
        if (ingredientShoppingLists != null) {
            ingredients.clear();

            for (IngredientShoppingList ingredientShoppingList : ingredientShoppingLists) {
                ingredients.add(ingredientShoppingList);
                setChanged();
                notifyObservers(ingredientShoppingList);
            }
        }
    }

    public List<IngredientShoppingList> getIngredients() {
        return ingredients;
    }

    public void addIngredient(IngredientShoppingList ingredientShoppingList) {
        ingredients.add(ingredientShoppingList);
    }

    private <T extends SQLModel<T> & IngredientInstance> IngredientShoppingList convertIngredient(T ingredientRecipe) {
        return IngredientShoppingList.newBuilder()
                    .setIngredientID(ingredientRecipe.getId())
                    .setName(ingredientRecipe.getName())
                    .setUnit(ingredientRecipe.getUnit())
                    .setQuantity(ingredientRecipe.getQuantity())
                    .build();
    }

    public boolean isIngredientContained(int ingredientID) {
        for (IngredientShoppingList ingredientShoppingList : ingredients) {
            if (ingredientShoppingList.getIngredientID() == ingredientID) {
                return true;
            }
        }
        return false;
    }

    public <T extends SQLModel<T> & IngredientInstance> void addIngredientFromInstance(T ingredient){
        for (IngredientShoppingList ingredientShoppingList : ingredients) {
            if (isIngredientContained(ingredient.getId())) {
                ingredientShoppingList.setQuantity(ingredientShoppingList.getQuantity() + ingredient.getQuantity());
            } else {
                addIngredient(convertIngredient(ingredient));
            }
        }
    }

    @Override
    public void insertSubModels() throws SQLException {
        for (IngredientShoppingList ingredientShoppingList : ingredients) {
            Optional<Ingredient> ingredient = Ingredient.newBuilder()
                    .setName(ingredientShoppingList.getName())
                    .build()
                    .getModel();

            Optional<ShoppingList> shoppingList = ShoppingList.newBuilder()
                    .setName(this.getName())
                    .build()
                    .getModel();

            if (ingredient.isPresent() && shoppingList.isPresent()) {
                ingredientShoppingList.completeAndInsert(ingredient.get().getId(), shoppingList.get().getId());
            } else {
                Autochef.getLogger().error("Couldn't insert the SubModel to the database");
                throw new SQLException("Failed to retrieve the SQLModel associated to the SubModel");
            }
        }
    }

    @Override
    public void deleteSubModels() throws SQLException {
        IngredientShoppingList.newBuilder()
                .setShoppingListID(id)
                .build()
                .removeFromDatabase();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("user_id", userID)
                .add("recipeName", name)
                .add("ingredients", ingredients)
                .toString();
    }

    @Override
    public final int hashCode() {
        return Objects.hash(name, userID, ingredients);
    }

    @Override
    public final boolean equals(Object other) {
        if(other == null) {
            return false;
        }
        if(this == other) {
            return true;
        }
        if(!(other instanceof ShoppingList)) {
            return false;
        }
        final var otherRecipe = (ShoppingList) other;

        return Objects.equals(otherRecipe.name, this.name)
                && Objects.equals(otherRecipe.userID, this.userID)
				&& Objects.equals(otherRecipe.ingredients, this.ingredients);
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static class Builder extends SQLBuilder<Builder, ShoppingList> {

        private int id;

        private int userID;

        private String name;

        private List<IngredientShoppingList> ingredients = new ArrayList<>();

        private Builder() { }

        public Builder setId(int id) {
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

        public Builder setIngredients(List<IngredientShoppingList> ingredients) {
            this.ingredients = ingredients;
            return this;
        }

        @Override
        protected ShoppingList makeObjectFromResultSet() throws SQLException {
            final int shoppingListId = resultSet.getInt(DatabaseTable.SHOPPING_LIST_TABLE.getKeyId());

            // Retrieve the elements associated to the shoppingList id
            final List<IngredientShoppingList> ingredientShoppingListList = new ArrayList<>(
                    IngredientShoppingList.newBuilder()
                    .setShoppingListID(shoppingListId)
                    .build()
                    .getModels()
            );

            // Set the names of the ingredients (were not in the database, have not been retrieved)
            for (final IngredientShoppingList ingredientShoppingList : ingredientShoppingListList) {
                Ingredient.newBuilder()
                        .setId(ingredientShoppingList.getIngredientID())
                        .build()
                        .getModel()
                                .ifPresent(model -> {
                                    ingredientShoppingList.setName(model.getName());
                                    ingredientShoppingList.setUnit(model.getUnit());
                                });
            }

            return ShoppingList.newBuilder()
                    .setId(shoppingListId)
                    .setUserID(Integer.parseInt(resultSet.getString("id_user")))
                    .setName(resultSet.getString("name"))
                    .setIngredients(ingredientShoppingListList)
                    .build();
        }

        @Override
        protected ShoppingList makeObjectFromBuilder() {
            return new ShoppingList(this);
        }

    }

}
