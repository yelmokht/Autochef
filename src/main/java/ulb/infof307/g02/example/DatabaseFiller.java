package ulb.infof307.g02.example;

import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.ShoppingList;
import ulb.infof307.g02.model.sql_model.recipe_model.IngredientRecipe;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.IngredientShoppingList;
import ulb.infof307.g02.model.sql_model.user_model.User;
import ulb.infof307.g02.util.attributes.Department;
import ulb.infof307.g02.util.attributes.Diet;
import ulb.infof307.g02.util.attributes.Meal;
import ulb.infof307.g02.util.attributes.Unit;

import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseFiller {

    public static void main(String[] args) throws SQLException {
        cleanDatabase();
        fillDatabaseForExample();
    }

    private static void cleanDatabase() throws SQLException {
        Ingredient.newBuilder().build().removeFromDatabase();
        ShoppingList.newBuilder().build().removeFromDatabase();
        Recipe.newBuilder().build().removeFromDatabase();
        IngredientShoppingList.newBuilder().build().removeFromDatabase();
        IngredientRecipe.newBuilder().build().removeFromDatabase();
        User.newBuilder().build().removeFromDatabase();
    }

    private static void fillDatabaseForExample() throws SQLException {
        User.newBuilder()
                .setName("test")
                .setPassword("test")
                .setEmail("test@gmail.com")
                .build()
                .insertIntoDatabase();

        Ingredient.newBuilder()
                .setName("Lait")
                .setType(Department.DAIRY)
                .build()
                .insertIntoDatabase();
        Ingredient.newBuilder()
                .setName("Carotte")
                .setType(Department.FRUITS_AND_VEGETABLES)
                .build()
                .insertIntoDatabase();
        Ingredient.newBuilder()
                .setName("Banane")
                .setType(Department.FRUITS_AND_VEGETABLES)
                .build()
                .insertIntoDatabase();
        Ingredient.newBuilder()
                .setName("Chocolat")
                .setType(Department.BAKERY)
                .build()
                .insertIntoDatabase();
        Ingredient.newBuilder()
                .setName("Boeuf")
                .setType(Department.BUTCHERY)
                .build()
                .insertIntoDatabase();
        Ingredient.newBuilder()
                .setName("Saumon")
                .setType(Department.FISHERY)
                .build()
                .insertIntoDatabase();
        Ingredient.newBuilder()
                .setName("Piments")
                .setType(Department.GROCERY)
                .build()
                .insertIntoDatabase();

        Recipe.newBuilder()
                .setUserID(1)
                .setName("Carrotes au Lait")
                .setServings(5)
                .setType(Meal.DISH)
                .setDiet(Diet.VEGETARIAN)
                .setInstructions(new ArrayList<>())
                .build()
                .insertIntoDatabase();
        Recipe.newBuilder()
                .setUserID(1)
                .setName("Bananes/Chocolat")
                .setServings(5)
                .setType(Meal.DESSERT)
                .setDiet(Diet.VEGETARIAN)
                .setInstructions(new ArrayList<>())
                .build()
                .insertIntoDatabase();

        IngredientRecipe.newBuilder()
                .setName("Lait")
                .setIngredientID(1)
                .setRecipeID(1)
                .setQuantity(0.3)
                .setUnit(Unit.LITER)
                .build()
                .insertIntoDatabase();

        IngredientRecipe.newBuilder()
                .setName("Carrotes")
                .setIngredientID(2)
                .setRecipeID(1)
                .setQuantity(5)
                .setUnit(Unit.UNIT)
                .build()
                .insertIntoDatabase();

        final int ingredientId3 = Ingredient.newBuilder()
                    .setName("Banane")
                    .build()
                    .getModel().get().getId();
        final int ingredientId4 = Ingredient.newBuilder()
                    .setName("Chocolat")
                    .build()
                    .getModel().get().getId();
        final int recipeId2 = Recipe.newBuilder()
                    .setName("Bananes/Chocolat")
                    .build()
                    .getModel().get().getId();

        final var ingredient3 = IngredientRecipe.newBuilder()
                .setName("Banane")
                .setQuantity(10)
                .setUnit(Unit.UNIT)
                .build();
        ingredient3.setIngredientID(ingredientId3);
        ingredient3.setRecipeID(recipeId2);
        ingredient3.insertIntoDatabase();

        final var ingredient4 = IngredientRecipe.newBuilder()
                .setName("Chocolat")
                .setQuantity(500)
                .setUnit(Unit.GRAM)
                .build();
        ingredient4.setIngredientID(ingredientId4);
        ingredient4.setRecipeID(recipeId2);
        ingredient4.insertIntoDatabase();

        final var shoppingList1 = ShoppingList.newBuilder().setName("Shopping List 1").build();
        shoppingList1.addIngredient(IngredientShoppingList.newBuilder()
                .setName("Banane")
                .build()
                .getModel().get());
        shoppingList1.addIngredient(IngredientShoppingList.newBuilder()
                .setName("Boeuf")
                .build()
                .getModel().get());
        shoppingList1.addIngredient(IngredientShoppingList.newBuilder()
                .setName("Saumon")
                .build()
                .getModel().get());
        shoppingList1.insertIntoDatabase();
    }
}
