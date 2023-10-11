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

class TestRecipeSchedule {
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
    private static RecipeSchedule unusedRecipeSchedule;

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

    private static final int RECIPE_SCHEDULE_ID = 1;
    private static final String SCHEDULE_DATE = "10/3/2022";

    private static final int UNUSED_ID = 99;
    private static final String UNUSED_NAME = "ziufzidu";
    private static final String UNUSED_DATE = "1/1/1970";
    private static final Unit UNUSED_UNIT = Unit.MILLILITER;

    @BeforeAll
    static void initialize_tests() throws SQLException {
        // Delete all data from the database
        INGREDIENT.removeFromDatabase();
        INGREDIENT_RECIPE.removeFromDatabase();
        RECIPE.removeFromDatabase();
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
                .setID(RECIPE_SCHEDULE_ID)
                .setName(RECIPE_NAME)
                .setUserID(USER_ID)
                .setRecipeID(RECIPE_ID)
                .setTime(MealTime.BREAKFAST)
                .setDate(SCHEDULE_DATE)
                .build();

        unusedRecipeSchedule = RecipeSchedule.newBuilder()
                .setID(UNUSED_ID)
                .setName(UNUSED_NAME)
                .setUserID(UNUSED_ID)
                .setRecipeID(UNUSED_ID)
                .setTime(MealTime.BREAKFAST)
                .setDate(UNUSED_DATE)
                .build();
    }

    @AfterEach
    void reset_database() throws SQLException {
        // Delete all data from the database
        INGREDIENT.removeFromDatabase();
        INGREDIENT_RECIPE.removeFromDatabase();
        RECIPE.removeFromDatabase();
        RECIPE_SCHEDULE.removeFromDatabase();
    }

    @BeforeEach
    void set_database() throws SQLException {
        // Add sample data to the database
        milk.insertIntoDatabase();
        carrots.insertIntoDatabase();
        recipe.insertIntoDatabase();
        recipeSchedule.insertIntoDatabase();
    }

    @Test
    void after_inserting_objects_are_present_in_database() throws SQLException {
        Optional<RecipeSchedule> retrievedSchedule = recipeSchedule.getModel();
        Optional<RecipeSchedule> retrievedUnused = unusedRecipeSchedule.getModel();

        assertTrue(retrievedSchedule.isPresent());
        assertEquals(recipeSchedule, retrievedSchedule.get());

        assertFalse(retrievedUnused.isPresent());
    }

    @Test
    void after_removing_objects_are_not_in_database() throws SQLException {
        recipeSchedule.removeFromDatabase();

        Optional<RecipeSchedule> retrievedSchedule = recipeSchedule.getModel();

        assertFalse(retrievedSchedule.isPresent());
    }

    @Test
    void after_deleting_all_at_once_nothing_left_in_database() throws SQLException {
        RECIPE_SCHEDULE.removeFromDatabase(); // Filter nothing because empty ingredient, delete all

        Optional<RecipeSchedule> retrievedSchedule = recipeSchedule.getModel();

        assertFalse(retrievedSchedule.isPresent());
    }

    @Test
    void selecting_object_from_date_get_the_right_object() throws SQLException {
        Optional<RecipeSchedule> retrievedSchedule = RecipeSchedule.newBuilder()
                    .setDate(SCHEDULE_DATE)
                    .build()
                    .getModel();
        Optional<RecipeSchedule> retrievedUnused = RecipeSchedule.newBuilder()
                    .setDate(UNUSED_DATE)
                    .build()
                    .getModel();

        assertTrue(retrievedSchedule.isPresent());
        assertEquals(recipeSchedule, retrievedSchedule.get());

        assertFalse(retrievedUnused.isPresent());
    }

    @Test
    void selecting_object_from_id_get_the_right_object() throws SQLException {
        Optional<RecipeSchedule> retrievedSchedule = RecipeSchedule.newBuilder()
                    .setID(RECIPE_SCHEDULE_ID)
                    .build()
                    .getModel();
        Optional<RecipeSchedule> retrievedUnused = RecipeSchedule.newBuilder()
                    .setID(UNUSED_ID)
                    .build()
                    .getModel();

        assertTrue(retrievedSchedule.isPresent());
        assertEquals(recipeSchedule, retrievedSchedule.get());

        assertFalse(retrievedUnused.isPresent());
    }

    @Test
    void selecting_object_from_complete_object_get_the_right_object() throws SQLException {
        Optional<RecipeSchedule> retrievedSchedule = recipeSchedule.getModel();
        Optional<RecipeSchedule> retrievedUnused = unusedRecipeSchedule.getModel();

        assertTrue(retrievedSchedule.isPresent());
        assertEquals(recipeSchedule, retrievedSchedule.get());

        assertFalse(retrievedUnused.isPresent());
    }

    @Test
    void selecting_object_from_not_valid_id_get_nothing() throws SQLException {
        Optional<Ingredient> retrievedUnused = Ingredient.newBuilder()
                    .setId(UNUSED_ID)
                    .build()
                    .getModel();
        assertFalse(retrievedUnused.isPresent());
    }
}