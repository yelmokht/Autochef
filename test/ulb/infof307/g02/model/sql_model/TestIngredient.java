package ulb.infof307.g02.model.sql_model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_model.recipe_model.IngredientRecipe;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.IngredientShoppingList;
import ulb.infof307.g02.util.attributes.Department;
import ulb.infof307.g02.util.attributes.Unit;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TestIngredient {
    private static final Ingredient INGREDIENT = Ingredient.newBuilder().build();

    private static Ingredient milk;
    private static Ingredient carrots;
    private static Ingredient unused;

    private static final int MILK_ID = 1;
    private static final String MILK_NAME = "Lait";
    private static final Department MILK_TYPE = Department.DAIRY;
    private static final Unit MILK_UNIT = Unit.LITER;

    private static final int CARROTS_ID = 2;
    private static final String CARROTS_NAME = "Carottes";
    private static final Department CARROTS_TYPE = Department.FRUITS_AND_VEGETABLES;
    private static final Unit CARROTS_UNIT = Unit.GRAM;

    private static final int UNUSED_ID = 99;
    private static final String UNUSED_NAME = "ziufzidu";
    private static final Unit UNUSED_UNIT = Unit.MILLILITER;


    @BeforeAll
    static void initialize_tests() throws SQLException {
        // Delete all data from the database
        INGREDIENT.removeFromDatabase();

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
    }

    @AfterEach
    void reset_database() throws SQLException {
        // Delete all data from the database
        INGREDIENT.removeFromDatabase();
    }

    @BeforeEach
    void set_database() throws SQLException {
        // Add sample data to the database
        milk.insertIntoDatabase();
        carrots.insertIntoDatabase();
    }

    @Test
    void after_inserting_objects_are_present_in_database() throws SQLException {
        Optional<Ingredient> retrievedMilk = milk.getModel();
        Optional<Ingredient> retrievedCarrots = carrots.getModel();
        Optional<Ingredient> retrievedUnused = unused.getModel();

        assertTrue(retrievedMilk.isPresent());
        assertEquals(milk, retrievedMilk.get());

        assertTrue(retrievedCarrots.isPresent());
        assertEquals(carrots, retrievedCarrots.get());

        assertFalse(retrievedUnused.isPresent());
    }

    @Test
    void after_removing_one_object_other_objects_in_database_() throws SQLException {
        milk.removeFromDatabase();

        Optional<Ingredient> retrievedMilk = milk.getModel();
        Optional<Ingredient> retrievedCarrots = carrots.getModel();

        assertFalse(retrievedMilk.isPresent());

        assertTrue(retrievedCarrots.isPresent());
        assertEquals(carrots, retrievedCarrots.get());
    }

    @Test
    void after_removing_objects_are_not_in_database() throws SQLException {
        milk.removeFromDatabase();
        carrots.removeFromDatabase();

        Optional<Ingredient> retrievedMilk = milk.getModel();
        Optional<Ingredient> retrievedCarrots = carrots.getModel();

        assertFalse(retrievedMilk.isPresent());
        assertFalse(retrievedCarrots.isPresent());
    }

    @Test
    void deleting_all_at_once_nothing_left_in_database() throws SQLException {
        INGREDIENT.removeFromDatabase(); // Filter nothing because empty ingredient, delete all

        Optional<Ingredient> retrievedMilk = milk.getModel();
        Optional<Ingredient> retrievedCarrots = carrots.getModel();

        assertFalse(retrievedMilk.isPresent());
        assertFalse(retrievedCarrots.isPresent());
    }

    @Test
    void selecting_object_from_name_get_the_right_object() throws SQLException {
        Optional<Ingredient> retrievedMilk = Ingredient.newBuilder()
                    .setName(MILK_NAME)
                    .build()
                    .getModel();
        Optional<Ingredient> retrievedCarrots = Ingredient.newBuilder()
                    .setName(CARROTS_NAME)
                    .build()
                    .getModel();
        assertTrue(retrievedMilk.isPresent());
        assertEquals(milk, retrievedMilk.get());

        assertTrue(retrievedCarrots.isPresent());
        assertEquals(carrots, retrievedCarrots.get());
    }

    @Test
    void selecting_object_from_id_get_the_right_object() throws SQLException {
        Optional<Ingredient> retrievedMilk = Ingredient.newBuilder()
                    .setId(MILK_ID)
                    .build()
                    .getModel();
        Optional<Ingredient> retrievedCarrots = Ingredient.newBuilder()
                    .setId(CARROTS_ID)
                    .build()
                    .getModel();

        assertTrue(retrievedMilk.isPresent());
        assertEquals(milk, retrievedMilk.get());

        assertTrue(retrievedCarrots.isPresent());
        assertEquals(carrots, retrievedCarrots.get());
    }

    @Test
    void selecting_object_from_complete_object_get_the_right_object() throws SQLException {
        Optional<Ingredient> retrievedMilk = milk.getModel();
        Optional<Ingredient> retrievedCarrots = carrots.getModel();

        assertTrue(retrievedMilk.isPresent());
        assertEquals(milk, retrievedMilk.get());

        assertTrue(retrievedCarrots.isPresent());
        assertEquals(carrots, retrievedCarrots.get());
    }

    @Test
    void selecting_object_from_not_valid_id_get_nothing() throws SQLException {
        Optional<Ingredient> retrievedUnused = Ingredient.newBuilder()
                    .setId(UNUSED_ID)
                    .build()
                    .getModel();
        assertFalse(retrievedUnused.isPresent());
    }

    @Test
    void deleting_other_instance_of_self() throws SQLException {
        IngredientRecipe recipeMilk = IngredientRecipe.newBuilder()
                .setRecipeID(1)
                .setIngredientID(MILK_ID)
                .setQuantity(5)
                .setUnit(MILK_UNIT)
                .build();
        IngredientShoppingList shoppingMilk = IngredientShoppingList.newBuilder()
                .setShoppingListID(1)
                .setIngredientID(MILK_ID)
                .setQuantity(5)
                .build();

        recipeMilk.insertIntoDatabase();
        shoppingMilk.insertIntoDatabase();
        milk.deleteOtherInstanceOfSelf();

        Optional<IngredientRecipe> retrievedRecipeMilk = recipeMilk.getModel();
        Optional<IngredientShoppingList> retrievedShoppingMilk = shoppingMilk.getModel();

        assertFalse(retrievedRecipeMilk.isPresent());
        assertFalse(retrievedShoppingMilk.isPresent());
    }
}