package ulb.infof307.g02.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.ShoppingList;
import ulb.infof307.g02.model.sql_model.recipe_model.IngredientRecipe;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.IngredientShoppingList;
import ulb.infof307.g02.util.attributes.Department;
import ulb.infof307.g02.util.attributes.Diet;
import ulb.infof307.g02.util.attributes.Meal;
import ulb.infof307.g02.util.attributes.Unit;

import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TestDatabaseTools {
    private static Ingredient milk;
    private static Ingredient carrots;
    private static Ingredient unusedIngredient;

    private static IngredientRecipe recipeMilk;
    private static IngredientRecipe recipeCarrots;
    private static IngredientRecipe unusedIngredientRecipe;

    private static Recipe recipe;
    private static Recipe unusedRecipe;

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

    private static final int USER_ID = 1;

    private static final Ingredient INGREDIENT = Ingredient.newBuilder().build();
    private static final IngredientShoppingList INGREDIENT_LIST = IngredientShoppingList.newBuilder().build();
    private static final ShoppingList SHOPPING_LIST = ShoppingList.newBuilder().build();
    private static final Recipe RECIPE = Recipe.newBuilder().build();
    private static final IngredientRecipe INGREDIENT_RECIPE = IngredientRecipe.newBuilder().build();

    private static final DatabaseTool<Ingredient> databaseToolIngredient = new DatabaseTool<>(Ingredient.class);
    private static final DatabaseTool<IngredientRecipe> databaseToolIngredientRecipe = new DatabaseTool<>(IngredientRecipe.class);
    private static final DatabaseTool<Recipe> databaseToolRecipe = new DatabaseTool<>(Recipe.class);

    @BeforeAll
    static void initialize_tests() throws SQLException {
        INGREDIENT.removeFromDatabase();
        SHOPPING_LIST.removeFromDatabase();
        RECIPE.removeFromDatabase();
        INGREDIENT_LIST.removeFromDatabase();
        INGREDIENT_RECIPE.removeFromDatabase();

        milk = Ingredient.newBuilder()
                .setName(MILK_NAME)
                .setType(MILK_TYPE)
                .setUnit(MILK_UNIT)
                .build();
        carrots = Ingredient.newBuilder()
                .setName(CARROTS_NAME)
                .setType(CARROTS_TYPE)
                .setUnit(CARROTS_UNIT)
                .build();

        recipeMilk = IngredientRecipe.newBuilder()
                .setName(MILK_NAME)
                .setIngredientID(MILK_ID)
                .setRecipeID(RECIPE_ID)
                .setQuantity(3)
                .setUnit(MILK_UNIT)
                .build();

        recipeCarrots = IngredientRecipe.newBuilder()
                .setName(CARROTS_NAME)
                .setIngredientID(CARROTS_ID)
                .setRecipeID(RECIPE_ID)
                .setQuantity(5)
                .setUnit(CARROTS_UNIT)
                .build();

        recipe = Recipe.newBuilder()
                .setID(RECIPE_ID)
                .setUserID(USER_ID)
                .setName(RECIPE_NAME)
                .setServings(5)
                .setType(Meal.DISH)
                .setDiet(Diet.VEGETARIAN)
                .setIngredients(List.of(recipeMilk, recipeCarrots))
                .setInstructions(new ArrayList<>(Arrays.asList("","","")))
                .build();


    }

    @AfterEach
    void reset_database() throws SQLException {
        INGREDIENT.removeFromDatabase();
        SHOPPING_LIST.removeFromDatabase();
        RECIPE.removeFromDatabase();
        INGREDIENT_LIST.removeFromDatabase();
        INGREDIENT_RECIPE.removeFromDatabase();
    }


    @BeforeEach
    void refillDatabase() throws SQLException {
        milk.insertIntoDatabase();
        carrots.insertIntoDatabase();
        recipe.insertIntoDatabase();
    }


    @Test
    void insert_sql_container() throws SQLException {
        //cleaning
        Recipe.newBuilder().build().removeFromDatabase();

        String recipeName= "carrote au lait cuit";
        Recipe recipe = Recipe.newBuilder()
                .setName(recipeName)
                .setServings(5)
                .setType(Meal.DISH)
                .setDiet(Diet.VEGETARIAN)
                .setIngredients(List.of(recipeMilk, recipeCarrots))
                .setInstructions(new ArrayList<>())
                .setUserID(USER_ID)
                .build();

        // Recipe has been inserted
        databaseToolRecipe.insert(recipe);
        recipe.setID(1);

        // If recipe has been inserted
        assertFalse(databaseToolRecipe.select(recipe).isEmpty());
        assertEquals(recipe, databaseToolRecipe.select(recipe).get(0));

        List<IngredientRecipe> retrievedRecipe = IngredientRecipe.newBuilder().setRecipeID(1).build().selectFromDatabase();

        // If ingredientRecipe has been inserted
        assertEquals(recipeMilk.getName(), databaseToolIngredientRecipe.select(recipeMilk).get(0).getName());
        assertEquals(recipeCarrots.getName(), databaseToolIngredientRecipe.select(recipeCarrots).get(0).getName());
    }

    @Test
    void select_ingredient_from_existing_id() throws SQLException {
        final var ingredientModel = Ingredient.newBuilder()
                .setId(MILK_ID)
                .setName(MILK_NAME)
                .setType(MILK_TYPE)
                .setUnit(MILK_UNIT)
                .build();

        final List<Ingredient> ingredientTest = databaseToolIngredient.select(
                Ingredient.newBuilder()
                        .setId(MILK_ID)
                        .build());
        //If ingredient exist
        assertFalse(ingredientTest.isEmpty());
        assertEquals(ingredientModel, ingredientTest.get(0));
    }

    @Test
    void select_ingredient_from_existing_name() throws SQLException {
        //set up les objets a tester
        final var ingredientInDb1 = Ingredient.newBuilder()
                .setId(MILK_ID)
                .setName(MILK_NAME)
                .setType(MILK_TYPE)
                .setUnit(MILK_UNIT)
                .build();
        final List<Ingredient> retrievedIngredient1 = databaseToolIngredient.select(ingredientInDb1);


        final var ingredientInDb2 = Ingredient.newBuilder()
                .setId(CARROTS_ID)
                .setName(CARROTS_NAME)
                .setType(CARROTS_TYPE)
                .setUnit(CARROTS_UNIT)
                .build();
        final List<Ingredient> retrievedIngredient2 = databaseToolIngredient.select(ingredientInDb2);

        assertFalse(retrievedIngredient1.isEmpty());
        assertEquals(ingredientInDb1, retrievedIngredient1.get(0));

        assertFalse(retrievedIngredient2.isEmpty());
        assertEquals(ingredientInDb2, retrievedIngredient2.get(0));
    }

    @Test
    void select_recipe_from_existing_id() throws SQLException {
        //set up les objets a tester
        final var ingredientRecipeInDb1 = IngredientRecipe.newBuilder()
                .setName(MILK_NAME)
                .setIngredientID(MILK_ID)
                .setRecipeID(RECIPE_ID)
                .setQuantity(3)
                .setUnit(MILK_UNIT)
                .build();

        final var ingredientRecipeInDb2 = IngredientRecipe.newBuilder()
                .setName(CARROTS_NAME)
                .setIngredientID(CARROTS_ID)
                .setRecipeID(RECIPE_ID)
                .setQuantity(5)
                .setUnit(CARROTS_UNIT)
                .build();

        final var recipeInDb = Recipe.newBuilder()
                .setID(RECIPE_ID)
                .setName(RECIPE_NAME)
                .setServings(5)
                .setType(Meal.DISH)
                .setDiet(Diet.VEGETARIAN)
                .setInstructions(new ArrayList<>())
                .setIngredients(new ArrayList<>(Arrays.asList(ingredientRecipeInDb1, ingredientRecipeInDb2)))
                .build();


        final List<Recipe> retrievedRecipe = databaseToolRecipe.select(Recipe.newBuilder().setID(RECIPE_ID).build());

        assertFalse(retrievedRecipe.isEmpty());
        assertEquals(recipeInDb, retrievedRecipe.get(0));
    }

    @Test
    void select_recipe_from_existing_name() throws SQLException {
        //set up les objets a tester
        final var ingredientRecipeInDb1 = IngredientRecipe.newBuilder()
                .setName(MILK_NAME)
                .setIngredientID(MILK_ID)
                .setRecipeID(RECIPE_ID)
                .setQuantity(3)
                .setUnit(MILK_UNIT)
                .build();

        final var ingredientRecipeInDb2 = IngredientRecipe.newBuilder()
                .setName(CARROTS_NAME)
                .setIngredientID(CARROTS_ID)
                .setRecipeID(RECIPE_ID)
                .setQuantity(5)
                .setUnit(CARROTS_UNIT)
                .build();

        final var recipeInDb = Recipe.newBuilder()
                .setID(RECIPE_ID)
                .setName(RECIPE_NAME)
                .setServings(5)
                .setType(Meal.DISH)
                .setDiet(Diet.VEGETARIAN)
                .setInstructions(new ArrayList<>())
                .setIngredients(new ArrayList<>(Arrays.asList(ingredientRecipeInDb1, ingredientRecipeInDb2)))
                .build();


        final List<Recipe> retrievedRecipe = databaseToolRecipe.select(Recipe.newBuilder().setName(RECIPE_NAME).build());

        //Verifie si la recette load de la database existe bien et est bien la meme
        //check if the recipe has been load in the database and if it's egual with the test
        assertFalse(retrievedRecipe.isEmpty());
        assertEquals(recipeInDb, retrievedRecipe.get(0));


        final List<IngredientRecipe> retrievedIngrediantRecipe = databaseToolIngredientRecipe.select(IngredientRecipe
                        .newBuilder()
                        .setRecipeID(RECIPE_ID)
                        .build());

        //check the existence of subModel
        assertFalse(retrievedIngrediantRecipe.isEmpty());
        assertEquals(ingredientRecipeInDb1,retrievedIngrediantRecipe.get(0));
        assertEquals(ingredientRecipeInDb2, retrievedIngrediantRecipe.get(1));
    }

    @Test
    void try_selecting_not_existing_id_return_nothing() throws SQLException {
        assertTrue(databaseToolIngredient.select(Ingredient.newBuilder().setId(UNUSED_ID).build()).isEmpty());
    }

    @Test
    void try_selecting_not_existing_name_return_nothing() throws SQLException {
        assertTrue(databaseToolIngredient.select(Ingredient.newBuilder().setName(UNUSED_NAME).build()).isEmpty());
    }

    @Test
    void delete_ingredient_from_id() throws SQLException {
        databaseToolIngredient.delete(Ingredient.newBuilder().setId(MILK_ID).build());
        databaseToolIngredient.delete(Ingredient.newBuilder().setId(CARROTS_ID).build());

        assertTrue(databaseToolIngredient.select(Ingredient.newBuilder().setId(MILK_ID).build()).isEmpty());
        assertTrue(databaseToolIngredient.select(Ingredient.newBuilder().setId(CARROTS_ID).build()).isEmpty());
    }

    @Test
    void delete_ingredient_from_name() throws SQLException {
        databaseToolIngredient.delete(Ingredient.newBuilder().setName(MILK_NAME).build());
        databaseToolIngredient.delete(Ingredient.newBuilder().setName(CARROTS_NAME).build());

        assertTrue(databaseToolIngredient.select(Ingredient.newBuilder().setId(MILK_ID).build()).isEmpty());
        assertTrue(databaseToolIngredient.select(Ingredient.newBuilder().setId(CARROTS_ID).build()).isEmpty());
    }

    @Test
    void delete_sql_container() throws SQLException {
        databaseToolRecipe.delete(Recipe.newBuilder().setID(RECIPE_ID).build());
        //check if recipe has deleted all of its components
        assertTrue(databaseToolRecipe.select(Recipe.newBuilder().setID(RECIPE_ID).build()).isEmpty());
        assertTrue(databaseToolIngredientRecipe.select(IngredientRecipe.newBuilder().build()).isEmpty());
    }

    @Test
    void delete_sql_base_model() throws SQLException {
        databaseToolIngredient.delete(Ingredient.newBuilder().setId(MILK_ID).build());

        assertTrue(databaseToolIngredient.select(Ingredient.newBuilder().setId(MILK_ID).build()).isEmpty());
    }
}