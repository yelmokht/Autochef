package ulb.infof307.g02.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_model.recipe_model.IngredientRecipe;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.util.attributes.Department;
import ulb.infof307.g02.util.attributes.Diet;
import ulb.infof307.g02.util.attributes.Meal;
import ulb.infof307.g02.util.attributes.Unit;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class  TestDatabaseQuery {

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
    private static final String UNUSED_NAME = "ziuf&zidub&";
    private static final Unit UNUSED_UNIT = Unit.MILLILITER;

    private static final  int USER_ID = 1;

    private static Ingredient milk;
    private static Ingredient carrots;
    private static Ingredient unusedIngredient;

    private static IngredientRecipe recipeMilk;
    private static IngredientRecipe recipeCarrots;
    private static IngredientRecipe unusedIngredientRecipe;

    private static Recipe recipe;
    private static Recipe unusedRecipe;

    private static final DatabaseQuery<Ingredient> INGREDIENT_QUERY = new DatabaseQuery<>(DatabaseTable.INGREDIENT_TABLE);

    private static final DatabaseQuery<Recipe> RECIPE_QUERY = new DatabaseQuery<>(DatabaseTable.RECIPE_TABLE);

    private static final DatabaseQuery<IngredientRecipe> INGREDIENT_RECIPE_QUERY = new DatabaseQuery<>(DatabaseTable.INGREDIENT_RECIPE_TABLE);

    @BeforeAll
    static void cleanDatabase() throws SQLException{
        INGREDIENT_QUERY.deleteFromDatabase("");
        RECIPE_QUERY.deleteFromDatabase("");
        INGREDIENT_RECIPE_QUERY.deleteFromDatabase("");

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
    }

    @AfterEach
    void refreshDatabase() throws SQLException {
        TestDatabaseQuery.cleanDatabase();
    }

    @BeforeEach
    void refillDatabase() throws SQLException {
        milk.insertIntoDatabase();
        carrots.insertIntoDatabase();
        recipe.insertIntoDatabase();
    }

    @Test
    void select_ingredients_from_matching_id() throws SQLException {
        assertEquals(MILK_NAME, INGREDIENT_QUERY.selectInDatabase(String.format("id = %d", MILK_ID)).get(0).getName());
        assertEquals(MILK_ID, INGREDIENT_QUERY.selectInDatabase(String.format("id = %d", MILK_ID)).get(0).getId());

        assertEquals(CARROTS_NAME, INGREDIENT_QUERY.selectInDatabase(String.format("id = %d", CARROTS_ID)).get(0).getName());
        assertEquals(CARROTS_ID, INGREDIENT_QUERY.selectInDatabase(String.format("id = %d", CARROTS_ID)).get(0).getId());
    }

    @Test
    void select_ingredients_from_matching_name() throws SQLException {
        assertEquals(MILK_NAME, INGREDIENT_QUERY.selectInDatabase(String.format("name = '%s'", MILK_NAME)).get(0).getName());
        assertEquals(MILK_ID, INGREDIENT_QUERY.selectInDatabase(String.format("name = '%s'", MILK_NAME)).get(0).getId());

        assertEquals(CARROTS_NAME, INGREDIENT_QUERY.selectInDatabase(String.format("name = '%s'", CARROTS_NAME)).get(0).getName());
        assertEquals(CARROTS_ID, INGREDIENT_QUERY.selectInDatabase(String.format("name = '%s'", CARROTS_NAME)).get(0).getId());
    }

    @Test
    void select_recipes_from_matching_id() throws SQLException {
        Recipe retrievedRecipe = RECIPE_QUERY.selectInDatabase(String.format("name = '%s'", RECIPE_NAME)).get(0);

        assertEquals(RECIPE_NAME, retrievedRecipe.getName());
        assertEquals(RECIPE_ID, retrievedRecipe.getId());
    }

    @Test
    void select_recipes_from_matching_name() throws SQLException {
        Recipe retrievedRecipe = RECIPE_QUERY.selectInDatabase(String.format("name = '%s'", RECIPE_NAME)).get(0);

        assertEquals(RECIPE_NAME, retrievedRecipe.getName());
        assertEquals(RECIPE_ID, retrievedRecipe.getId());
    }

    @Test
    void try_selecting_not_existing_id_return_nothing() throws SQLException {
        assertEquals(Collections.emptyList(), INGREDIENT_QUERY.selectInDatabase(String.format("id = %d", UNUSED_ID)));
        assertEquals(Collections.emptyList(), RECIPE_QUERY.selectInDatabase(String.format("id = %d", UNUSED_ID)));
    }

    @Test
    void try_selecting_not_existing_name_return_nothing() throws SQLException {
        assertEquals(Collections.emptyList(), INGREDIENT_QUERY.selectInDatabase(String.format("name = '%s'", UNUSED_NAME)));
        assertEquals(Collections.emptyList(), RECIPE_QUERY.selectInDatabase(String.format("name = '%s'", UNUSED_NAME)));
    }

    @Test
    void delete_from_database_using_constraints() throws SQLException {
        INGREDIENT_QUERY.deleteFromDatabase(String.format("id = %d", CARROTS_ID));
        RECIPE_QUERY.deleteFromDatabase(String.format("name = '%s'", RECIPE_NAME));

        assertEquals(Collections.emptyList(), new DatabaseQuery<>(DatabaseTable.INGREDIENT_TABLE)
                .selectInDatabase(String.format("id = %d", CARROTS_ID)));
        assertEquals(Collections.emptyList(), new DatabaseQuery<>(DatabaseTable.INGREDIENT_TABLE)
                .selectInDatabase(String.format("name = '%s'", RECIPE_NAME)));

        // Deleted what we asked to delete
        assertEquals(Collections.emptyList(), INGREDIENT_QUERY.selectInDatabase(String.format("id = %d", CARROTS_ID)));
        assertEquals(Collections.emptyList(), RECIPE_QUERY.selectInDatabase(String.format("name = '%s'", RECIPE_NAME)));

        // Did not delete everything
        assertEquals(MILK_NAME, INGREDIENT_QUERY.selectInDatabase(String.format("id = %d", MILK_ID)).get(0).getName());
        assertEquals(MILK_ID, INGREDIENT_QUERY.selectInDatabase(String.format("name = '%s'", MILK_NAME)).get(0).getId());
    }

    @Test
    void try_deleting_from_database_using_not_matching_id() throws SQLException {
        // Try to suppress not existing models
        INGREDIENT_QUERY.deleteFromDatabase(String.format("id = %d", UNUSED_ID));
        RECIPE_QUERY.deleteFromDatabase(String.format("id = %d", UNUSED_ID));

        // Verify that we did not suppress existing Models from the database
        select_ingredients_from_matching_id();
        select_ingredients_from_matching_name();
        select_recipes_from_matching_id();
        select_recipes_from_matching_name();
    }

    @Test
    void try_deleting_from_database_using_not_matching_name() throws SQLException {
        // Try to suppress not existing models
        INGREDIENT_QUERY.deleteFromDatabase(String.format("name = '%s'", UNUSED_NAME));
        RECIPE_QUERY.deleteFromDatabase(String.format("name = '%s'", UNUSED_NAME));

        // Verify that we did not suppress existing Models from the database
        select_ingredients_from_matching_id();
        select_ingredients_from_matching_name();
        select_recipes_from_matching_id();
        select_recipes_from_matching_name();
    }

    @Test
    void delete_all_ingredients() throws SQLException {
        INGREDIENT_QUERY.deleteFromDatabase("");

        // All ingredients are gone from the database
        assertEquals(Collections.emptyList(), INGREDIENT_QUERY.selectInDatabase(String.format("id = %d", MILK_ID)));
        assertEquals(Collections.emptyList(), INGREDIENT_QUERY.selectInDatabase(String.format("id = %d", CARROTS_ID)));
    }

    @Test
    void delete_all_recipes() throws SQLException {
        RECIPE_QUERY.deleteFromDatabase("");

        // All recipes are gone from the database
        assertEquals(Collections.emptyList(), RECIPE_QUERY.selectInDatabase(String.format("id = %d", RECIPE_ID)));
    }
}