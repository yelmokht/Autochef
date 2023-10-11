package ulb.infof307.g02.model.sql_model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.IngredientShoppingList;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.ShoppingList;
import ulb.infof307.g02.util.attributes.Department;
import ulb.infof307.g02.util.attributes.Unit;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TestShoppingList {

    private static final Ingredient INGREDIENT = Ingredient.newBuilder().build();
    private static final ShoppingList SHOPPING_LIST = ShoppingList.newBuilder().build();

    private static Ingredient milk;
    private static Ingredient carrots;
    private static Ingredient unused;

    private static IngredientShoppingList shoppingMilk;
    private static IngredientShoppingList shoppingCarrots;

    private static ShoppingList shoppingList;

    private static final int USER_ID = 1;

    private static final int MILK_ID = 1;
    private static final String MILK_NAME = "Lait";
    private static final Department MILK_TYPE = Department.DAIRY;
    private static final Unit MILK_UNIT = Unit.LITER;

    private static final int CARROTS_ID = 2;
    private static final String CARROTS_NAME = "Carottes";
    private static final Department CARROTS_TYPE = Department.FRUITS_AND_VEGETABLES;
    private static final Unit CARROTS_UNIT = Unit.GRAM;

    private static final int SHOPPING_LIST_ID = 1;
    private static final String SHOPPING_LIST_NAME = "Liste de course";

    private static final int UNUSED_ID = 99;
    private static final String UNUSED_NAME = "ziufzidu";
    private static final Unit UNUSED_UNIT = Unit.MILLILITER;

    @BeforeAll
    static void initialize_tests() throws SQLException {
        // Delete all data from the database
        INGREDIENT.removeFromDatabase();
        SHOPPING_LIST.removeFromDatabase();

        milk = Ingredient.newBuilder()
                .setId(MILK_ID)
                .setName(MILK_NAME)
                .setType(MILK_TYPE)
                .setUnit(MILK_UNIT)
                .build();

        carrots = Ingredient.newBuilder()
                .setId(CARROTS_ID)
                .setName(CARROTS_NAME)
                .setType(CARROTS_TYPE)
                .setUnit(CARROTS_UNIT)
                .build();

        unused = Ingredient.newBuilder()
                .setId(UNUSED_ID)
                .setName(UNUSED_NAME)
                .setUnit(UNUSED_UNIT)
                .build();

        shoppingMilk = IngredientShoppingList.newBuilder()
                .setName(MILK_NAME)
                .setType(MILK_TYPE)
                .setIngredientID(MILK_ID)
                .setShoppingListID(SHOPPING_LIST_ID)
                .setQuantity(1)
                .setUnit(MILK_UNIT)
                .build();

        shoppingCarrots = IngredientShoppingList.newBuilder()
                .setName(CARROTS_NAME)
                .setType(CARROTS_TYPE)
                .setIngredientID(CARROTS_ID)
                .setShoppingListID(SHOPPING_LIST_ID)
                .setQuantity(5)
                .setUnit(CARROTS_UNIT)
                .build();

        shoppingList = ShoppingList.newBuilder()
                .setId(SHOPPING_LIST_ID)
                .setUserID(USER_ID)
                .setName(SHOPPING_LIST_NAME)
                .setIngredients(List.of(shoppingMilk, shoppingCarrots))
                .build();
    }

    @AfterEach
    void reset_database() {
        // Delete all data from the database
        try {
            INGREDIENT.removeFromDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            SHOPPING_LIST.removeFromDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @BeforeEach
    void set_database() throws SQLException {
        // Add sample data to the database
        milk.insertIntoDatabase();
        carrots.insertIntoDatabase();
        shoppingList.insertIntoDatabase();
    }

    @Test
    void inserting_a_valid_object_and_object_in_database() throws SQLException {
        Optional<ShoppingList> retrievedShoppingList = shoppingList.getModel();

        assertTrue(retrievedShoppingList.isPresent());
        assertEquals(shoppingList, retrievedShoppingList.get());
    }

    @Test
    void inserting_a_non_valid_object_and_object_not_inserted() throws SQLException {
        // Not valid, because mandatory values are not set
        Recipe notValidRecipe1 = Recipe.newBuilder()
                .setName(UNUSED_NAME)
                .build();
        try {
            notValidRecipe1.insertIntoDatabase();
            assertEquals(0, 1);
        } catch (SQLException e) {
            // Do nothing, should throw an exception
        }

        Recipe notValidRecipe2 = Recipe.newBuilder()
                .setID(UNUSED_ID)
                .build();
        try {
            notValidRecipe2.insertIntoDatabase();
            assertEquals(0, 1);
        } catch (SQLException e) {
            // Do nothing, should throw an exception
        }

        // Searches with filters corresponding to notValidRecipe : name = 'ziufzidu'
        List<Recipe> result1 = notValidRecipe1.getModels();
        List<Recipe> result2 = notValidRecipe2.getModels();

        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
    }

    @Test
    void same_object_inserted_two_times_only_one_in_database() throws SQLException {
        // Already insert, trying to insert it again, throw exception and don't insert
        try {
            shoppingList.insertIntoDatabase();
            assertEquals(0, 1); // Make the test fail
        } catch (SQLException e) {
            // Do nothing, just keep going, shouldn't be able to insert now so an error is supposed to happen
        }

        assertEquals(1, shoppingList.getModels().size());
    }

    @Test
    void selecting_object_from_name_get_the_right_object() throws SQLException {
        Optional<ShoppingList> retrievedShoppingList = ShoppingList.newBuilder()
                    .setName(SHOPPING_LIST_NAME)
                    .build()
                    .getModel();

        assertTrue(retrievedShoppingList.isPresent());
        assertEquals(shoppingList, retrievedShoppingList.get());
    }

    @Test
    void selecting_object_from_id_get_the_right_object() throws SQLException {
        Optional<ShoppingList> retrievedShoppingList = ShoppingList.newBuilder()
                    .setUserID(SHOPPING_LIST_ID)
                    .build()
                    .getModel();

        assertTrue(retrievedShoppingList.isPresent());
        assertEquals(shoppingList, retrievedShoppingList.get());
    }

    @Test
    void selecting_object_from_complete_object_get_the_right_object() throws SQLException {
        Optional<ShoppingList> retrievedShoppingList = shoppingList.getModel();

        assertTrue(retrievedShoppingList.isPresent());
        assertEquals(shoppingList, retrievedShoppingList.get());
    }

    @Test
    void selecting_object_from_not_valid_id_and_name_get_nothing() throws SQLException {
        Optional<ShoppingList> retrievedUnused1 = ShoppingList.newBuilder()
                    .setId(UNUSED_ID)
                    .build()
                    .getModel();
        Optional<ShoppingList> retrievedUnused2 = ShoppingList.newBuilder()
                    .setName(UNUSED_NAME)
                    .build()
                    .getModel();

        assertFalse(retrievedUnused1.isPresent());
        assertFalse(retrievedUnused2.isPresent());
    }

    @Test
    void after_inserting_sub_models_are_present_in_database() throws SQLException {
        Optional<IngredientShoppingList> retrievedRecipeMilk = shoppingMilk.getModel();
        Optional<IngredientShoppingList> retrievedRecipeCarrots = shoppingCarrots.getModel();

        assertTrue(retrievedRecipeMilk.isPresent());
        assertEquals(shoppingMilk, retrievedRecipeMilk.get());

        assertTrue(retrievedRecipeCarrots.isPresent());
        assertEquals(shoppingCarrots, retrievedRecipeCarrots.get());
    }

    @Test
    void after_deleting_sub_models_are_not_in_database() throws SQLException {
        Optional<IngredientShoppingList> retrievedShoppingMilk = shoppingMilk.getModel();
        Optional<IngredientShoppingList> retrievedShoppingCarrots = shoppingCarrots.getModel();

        // They were in the database
        assertTrue(retrievedShoppingMilk.isPresent());
        assertTrue(retrievedShoppingCarrots.isPresent());

        // Remove the container
        shoppingList.removeFromDatabase();
        retrievedShoppingMilk = shoppingMilk.getModel();
        retrievedShoppingCarrots = shoppingCarrots.getModel();

        // They shouldn't be in the database anymore
        assertFalse(retrievedShoppingMilk.isPresent());
        assertFalse(retrievedShoppingCarrots.isPresent());
    }
}