package ulb.infof307.g02.model.sql_model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_model.recipe_model.IngredientRecipe;
import ulb.infof307.g02.util.attributes.Department;
import ulb.infof307.g02.util.attributes.Unit;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TestIngredientRecipe {
    private static final IngredientRecipe INGREDIENT_RECIPE = IngredientRecipe.newBuilder().build();
    private static final Ingredient INGREDIENT = Ingredient.newBuilder().build();

    private static Ingredient milk;
    private static Ingredient carrots;

    private static IngredientRecipe recipeMilk;
    private static IngredientRecipe recipeCarrots;
    private static IngredientRecipe unusedIngredientRecipe;

    private static final int MILK_ID = 1;
    private static final String MILK_NAME = "Lait";
    private static final Department MILK_TYPE = Department.DAIRY;
    private static final Unit MILK_UNIT = Unit.LITER;

    private static final int CARROTS_ID = 2;
    private static final String CARROTS_NAME = "Carottes";
    private static final Department CARROTS_TYPE = Department.FRUITS_AND_VEGETABLES;
    private static final Unit CARROTS_UNIT = Unit.GRAM;


    private static final int RECIPE_ID = 1;

    private static final int UNUSED_ID = 99;
    private static final String UNUSED_NAME = "ziufzidu";
    private static final Unit UNUSED_UNIT = Unit.MILLILITER;

    @BeforeAll
    static void initialize_tests() throws SQLException {
        // Delete all data from the database
        INGREDIENT_RECIPE.removeFromDatabase();
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

        recipeMilk = IngredientRecipe.newBuilder()
                .setName(MILK_NAME)
                .setIngredientID(MILK_ID)
                .setRecipeID(RECIPE_ID)
                .setQuantity(0.3)
                .setUnit(MILK_UNIT)
                .build();

        recipeCarrots = IngredientRecipe.newBuilder()
                .setName(CARROTS_NAME)
                .setIngredientID(CARROTS_ID)
                .setRecipeID(RECIPE_ID)
                .setQuantity(5)
                .setUnit(CARROTS_UNIT)
                .build();

        unusedIngredientRecipe = IngredientRecipe.newBuilder()
                .setName(UNUSED_NAME)
                .setIngredientID(UNUSED_ID)
                .setRecipeID(UNUSED_ID)
                .setQuantity(5)
                .setUnit(UNUSED_UNIT)
                .build();
    }

    @AfterEach
    void reset_database() throws SQLException {
        // Delete all data from the database
        INGREDIENT_RECIPE.removeFromDatabase();
        INGREDIENT.removeFromDatabase();
    }

    @BeforeEach
    void set_database() throws SQLException {
        // Add sample data to the database
        milk.insertIntoDatabase();
        carrots.insertIntoDatabase();
        recipeMilk.insertIntoDatabase();
        recipeCarrots.insertIntoDatabase();
    }

    @Test
    void after_inserting_objects_are_present_in_database() throws SQLException {
        Optional<IngredientRecipe> retrievedMilk = recipeMilk.getModel();
        Optional<IngredientRecipe> retrievedCarrots = recipeCarrots.getModel();
        Optional<IngredientRecipe> retrievedUnused = unusedIngredientRecipe.getModel();

        assertTrue(retrievedMilk.isPresent());
        assertEquals(recipeMilk, retrievedMilk.get());

        assertTrue(retrievedCarrots.isPresent());
        assertEquals(recipeCarrots, retrievedCarrots.get());

        assertFalse(retrievedUnused.isPresent());
    }

    @Test
    void after_removing_one_object_other_objects_in_database_() throws SQLException {
        recipeMilk.removeFromDatabase();

        Optional<IngredientRecipe> retrievedMilk = recipeMilk.getModel();
        Optional<IngredientRecipe> retrievedCarrots = recipeCarrots.getModel();

        assertFalse(retrievedMilk.isPresent());

        assertTrue(retrievedCarrots.isPresent());
        assertEquals(recipeCarrots, retrievedCarrots.get());
    }

    @Test
    void after_removing_objects_are_not_in_database() throws SQLException {
        recipeMilk.removeFromDatabase();
        recipeCarrots.removeFromDatabase();

        Optional<IngredientRecipe> retrievedMilk = recipeMilk.getModel();
        Optional<IngredientRecipe> retrievedCarrots = recipeCarrots.getModel();

        assertFalse(retrievedMilk.isPresent());
        assertFalse(retrievedCarrots.isPresent());
    }

    @Test
    void deleting_all_at_once_nothing_left_in_database() throws SQLException {
        INGREDIENT_RECIPE.removeFromDatabase(); // Filter nothing because empty ingredient, delete all

        Optional<IngredientRecipe> retrievedMilk = recipeMilk.getModel();
        Optional<IngredientRecipe> retrievedCarrots = recipeCarrots.getModel();

        assertFalse(retrievedMilk.isPresent());
        assertFalse(retrievedCarrots.isPresent());
    }

    @Test
    void selecting_object_from_id_get_the_right_object() throws SQLException {
        Optional<IngredientRecipe> retrievedMilk = IngredientRecipe.newBuilder()
                    .setIngredientID(MILK_ID)
                    .setRecipeID(RECIPE_ID)
                    .build()
                    .getModel();
        Optional<IngredientRecipe> retrievedCarrots = IngredientRecipe.newBuilder()
                    .setIngredientID(CARROTS_ID)
                    .setRecipeID(RECIPE_ID)
                    .build()
                    .getModel();

        assertTrue(retrievedMilk.isPresent());
        assertEquals(recipeMilk, retrievedMilk.get());

        assertTrue(retrievedCarrots.isPresent());
        assertEquals(recipeCarrots, retrievedCarrots.get());
    }

    @Test
    void selecting_object_from_complete_object_get_the_right_object() throws SQLException {
        Optional<IngredientRecipe> retrievedMilk = recipeMilk.getModel();
        Optional<IngredientRecipe>  retrievedCarrots = recipeCarrots.getModel();

        assertTrue(retrievedMilk.isPresent());
        assertEquals(recipeMilk, retrievedMilk.get());

        assertTrue(retrievedCarrots.isPresent());
        assertEquals(recipeCarrots, retrievedCarrots.get());
    }

    @Test
    void selecting_object_from_not_valid_id_get_nothing() throws SQLException {
        Optional<IngredientRecipe> retrievedUnused = IngredientRecipe.newBuilder()
                    .setRecipeID(UNUSED_ID)
                    .setIngredientID(UNUSED_ID)
                    .build()
                    .getModel();
        assertFalse(retrievedUnused.isPresent());
    }
}