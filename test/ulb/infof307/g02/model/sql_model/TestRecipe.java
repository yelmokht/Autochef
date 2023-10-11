package ulb.infof307.g02.model.sql_model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_model.recipe_model.IngredientRecipe;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.model.sql_model.schedule_model.RecipeSchedule;
import ulb.infof307.g02.util.attributes.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TestRecipe {
    private static final Recipe RECIPE = Recipe.newBuilder().build();
    private static final Ingredient INGREDIENT = Ingredient.newBuilder().build();
    private static final IngredientRecipe INGREDIENT_RECIPE = IngredientRecipe.newBuilder().build();
    private static final RecipeSchedule RECIPE_SCHEDULE = RecipeSchedule.newBuilder().build();

    private static Ingredient milk;
    private static Ingredient carrots;
    private static Ingredient unusedIngredient;

    private static IngredientRecipe recipeMilk;
    private static IngredientRecipe recipeCarrots;
    private static IngredientRecipe unusedIngredientRecipe;

    private static Recipe recipe;
    private static Recipe unusedRecipe;

    private static RecipeSchedule recipeSchedule;

    private static final int USER_ID = 1;

    private static final int MILK_ID = 1;
    private static final String MILK_NAME = "Lait";
    private static final Department MILK_TYPE = Department.DAIRY;
    private static final Unit MILK_UNIT = Unit.LITER;

    private static final int CARROTS_ID = 2;
    private static final String CARROTS_NAME = "Carottes";
    private static final Department CARROTS_TYPE = Department.FRUITS_AND_VEGETABLES;
    private static final Unit CARROTS_UNIT = Unit.GRAM;

    private static final int RECIPE_ID = 1;
    private static final String RECIPE_NAME = "Carottes au Lait";

    private static final int UNUSED_ID = 99;
    private static final String UNUSED_NAME = "ziufzidu";
    private static final Unit UNUSED_UNIT = Unit.MILLILITER;


    @BeforeAll
    static void initialize_tests() throws SQLException {
        // Delete all data from the database
        RECIPE.removeFromDatabase();
        INGREDIENT.removeFromDatabase();
        RECIPE_SCHEDULE.removeFromDatabase();

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

        unusedIngredient = Ingredient.newBuilder()
                .setId(UNUSED_ID)
                .setName(UNUSED_NAME)
                .setUnit(UNUSED_UNIT)
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

        recipe = Recipe.newBuilder()
                .setID(RECIPE_ID)
                .setUserID(USER_ID)
                .setName(RECIPE_NAME)
                .setServings(5)
                .setType(Meal.DISH)
                .setDiet(Diet.VEGETARIAN)
                .setIngredients(List.of(recipeMilk, recipeCarrots))
//                .setInstructions(new ArrayList<>(Arrays.asList("","","")))
                .build();

        unusedRecipe = Recipe.newBuilder()
                .setID(UNUSED_ID)
                .setUserID(UNUSED_ID)
                .setName(UNUSED_NAME)
                .setServings(5)
                .setType(Meal.DISH)
                .setDiet(Diet.VEGETARIAN)
//                .setInstructions(new ArrayList<>(Arrays.asList("","","")))
                .build();

        recipeSchedule = RecipeSchedule.newBuilder()
                .setUserID(USER_ID)
                .setRecipeID(RECIPE_ID)
                .setDate("10/3/2023")
                .setTime(MealTime.SUPPER)
                .build();
    }

    @BeforeEach
    void set_database() throws SQLException {
        // Add sample data to the database
        milk.insertIntoDatabase();
        carrots.insertIntoDatabase();
        recipe.insertIntoDatabase();
        recipeSchedule.insertIntoDatabase();
    }

    @AfterEach
    void reset_database() throws SQLException {
        // Delete all data from the database
        RECIPE.removeFromDatabase();
        INGREDIENT.removeFromDatabase();
        INGREDIENT_RECIPE.removeFromDatabase();
    }

    @Test
    void inserting_a_valid_object_and_object_in_database() throws SQLException {
        Optional<Recipe> retrievedRecipe = recipe.getModel();

        assertTrue(retrievedRecipe.isPresent());
        assertEquals(recipe, retrievedRecipe.get());
    }

    @Test
    void inserting_a_non_valid_object_and_object_not_inserted() throws SQLException {
        // Not valid, because mandatory values are not set
        Recipe notValidRecipe1 = Recipe.newBuilder()
                .setName(UNUSED_NAME)
                .build();

        // Already insert, trying to insert it again, throw exception and don't insert
        try {
            notValidRecipe1.insertIntoDatabase();
            assertEquals(0, 1); // Make the test fail
        } catch (SQLException e) {
            // Do nothing, just keep going, shouldn't be able to insert now so an error is supposed to happen
        }

        Recipe notValidRecipe2 = Recipe.newBuilder()
                .setID(UNUSED_ID)
                .build();
        try {
            notValidRecipe2.insertIntoDatabase();
            assertEquals(0, 1);
        } catch (SQLException e) {
            // Do nothing
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
            recipe.insertIntoDatabase();
            assertEquals(0, 1); // Make the test fail
        } catch (SQLException e) {
            // Do nothing, just keep going, shouldn't be able to insert now so an error is supposed to happen
        }

        assertEquals(1, recipe.getModels().size());
    }

    @Test
    void selecting_object_from_name_get_the_right_object() throws SQLException {
        Optional<Recipe> retrievedRecipe = Recipe.newBuilder()
                .setName(RECIPE_NAME)
                .build()
                .getModel();

        assertTrue(retrievedRecipe.isPresent());
        assertEquals(recipe, retrievedRecipe.get());
    }

    @Test
    void selecting_object_from_id_get_the_right_object() throws SQLException {
        Optional<Recipe> retrievedRecipe = Recipe.newBuilder()
                .setID(RECIPE_ID)
                .build()
                .getModel();

        assertTrue(retrievedRecipe.isPresent());
        assertEquals(recipe, retrievedRecipe.get());
    }

    @Test
    void selecting_object_from_complete_object_get_the_right_object() throws SQLException {
        Optional<Recipe> retrievedRecipe = recipe.getModel();

        assertTrue(retrievedRecipe.isPresent());
        assertEquals(recipe, retrievedRecipe.get());
    }

    @Test
    void selecting_object_from_not_valid_id_and_name_get_nothing() throws SQLException {
        Optional<Recipe> retrievedUnused1 = Recipe.newBuilder()
                .setID(UNUSED_ID)
                .build()
                .getModel();
        Optional<Recipe> retrievedUnused2 = Recipe.newBuilder()
                .setName(UNUSED_NAME)
                .build()
                .getModel();

        assertFalse(retrievedUnused1.isPresent());
        assertFalse(retrievedUnused2.isPresent());
    }

    @Test
    void after_inserting_sub_models_are_present_in_database() throws SQLException {
        Optional<IngredientRecipe> retrievedRecipeMilk = recipeMilk.getModel();
        Optional<IngredientRecipe> retrievedRecipeCarrots = recipeCarrots.getModel();

        assertTrue(retrievedRecipeMilk.isPresent());
        assertEquals(recipeMilk, retrievedRecipeMilk.get());

        assertTrue(retrievedRecipeCarrots.isPresent());
        assertEquals(recipeCarrots, retrievedRecipeCarrots.get());
    }

    @Test
    void after_deleting_sub_models_are_not_in_database() throws SQLException {
        Optional<IngredientRecipe> retrievedRecipeMilk = recipeMilk.getModel();
        Optional<IngredientRecipe> retrievedRecipeCarrots = recipeCarrots.getModel();

        // They were in the database
        assertTrue(retrievedRecipeMilk.isPresent());
        assertTrue(retrievedRecipeCarrots.isPresent());

        // Remove the container
        recipe.removeFromDatabase();

        retrievedRecipeMilk = recipeMilk.getModel();
        retrievedRecipeCarrots = recipeCarrots.getModel();

        // They shouldn't be in the database anymore
        assertFalse(retrievedRecipeMilk.isPresent());
        assertFalse(retrievedRecipeCarrots.isPresent());
    }

    @Test
    void after_deleting_other_instance_of_self_models_are_not_in_database() throws SQLException {
        Optional<RecipeSchedule> retrievedRecipeSchedule = recipeSchedule.getModel();
        System.out.println(recipeSchedule.getId());
        retrievedRecipeSchedule.get().setId(0);

        assertTrue(retrievedRecipeSchedule.isPresent());
        assertEquals(recipeSchedule, retrievedRecipeSchedule.get());

        recipe.deleteOtherInstanceOfSelf();

        retrievedRecipeSchedule = recipeSchedule.getModel();
        assertFalse(retrievedRecipeSchedule.isPresent());
    }
}