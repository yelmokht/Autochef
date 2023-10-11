package ulb.infof307.g02.model;

import org.junit.jupiter.api.Test;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.util.attributes.Department;
import ulb.infof307.g02.util.attributes.Diet;
import ulb.infof307.g02.util.attributes.Meal;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TestSQLModel {

    private static final int MILK_ID = 1;
    private static final String MILK_NAME = "Lait";
    private static final Department MILK_TYPE = Department.DAIRY;

    private static final int CARROTS_ID = 2;
    private static final String CARROTS_NAME = "Carottes";
    private static final Department CARROTS_TYPE = Department.FRUITS_AND_VEGETABLES;

    private static final int RECIPE_ID = 1;
    private static final String RECIPE_NAME = "Carottes au Lait";

    private static final int UNUSED_ID = 99;
    private static final String UNUSED_NAME = "ziuf&zidub&";

    @Test
    void given_sql_ingredient_model_then_toSQLValues_return_matching_values() {
        final var ingredient1 = Ingredient.newBuilder()
                .setId(MILK_ID)
                .setType(MILK_TYPE)
                .setName(MILK_NAME)
                .build();

        final var ingredient2 = Ingredient.newBuilder()
                .setId(CARROTS_ID)
                .setType(CARROTS_TYPE)
                .setName(CARROTS_NAME)
                .build();

        assertEquals(String.format("('%s','%s')", MILK_NAME, MILK_TYPE),
                ingredient1.toSQLValuesInsert());
        assertEquals(String.format("('%s','%s','%s')", MILK_ID, MILK_NAME, MILK_TYPE),
                ingredient1.toSQLValuesRetrieve());

        assertEquals(String.format("('%s','%s')", CARROTS_NAME, CARROTS_TYPE),
                ingredient2.toSQLValuesInsert());
        assertEquals(String.format("('%s','%s','%s')", CARROTS_ID, CARROTS_NAME, CARROTS_TYPE),
                ingredient2.toSQLValuesRetrieve());
    }

    @Test
    void given_sql_ingredient_model_then_toSQLColumns_return_matching_columns() {
        final var ingredient = Ingredient.newBuilder()
                .setId(MILK_ID)
                .setType(MILK_TYPE)
                .setName(MILK_NAME)
                .build();

        assertEquals("('name','type','unit')", ingredient.toSQLColumnsInsert());
        assertEquals("('id','name','type','unit')", ingredient.toSQLColumnsRetrieve());
    }

    @Test
    void given_sql_recipe_model_then_toSQLValues_return_matching_values() {
        final var recipe = Recipe.newBuilder()
                .setID(RECIPE_ID)
                .setName(RECIPE_NAME)
                .setServings(5)
                .setType(Meal.DISH)
                .setDiet(Diet.VEGETARIAN)
                .setInstructions(new ArrayList<>())
                .build();

        assertEquals(String.format("('%s','%d','%s','%s','%s')",
                RECIPE_NAME, 5, Meal.DISH, Diet.VEGETARIAN, "[]"), recipe.toSQLValuesInsert());
        assertEquals(String.format("('%d','%s','%d','%s','%s','%s')",
                RECIPE_ID, RECIPE_NAME, 5, Meal.DISH, Diet.VEGETARIAN, "[]"), recipe.toSQLValuesRetrieve());
    }

    @Test
    void given_sql_recipe_model_then_toSQLColumns_return_matching_columns() {
        final var recipe = Recipe.newBuilder()
                .setID(RECIPE_ID)
                .setName(RECIPE_NAME)
                .setServings(5)
                .setType(Meal.DISH)
                .setDiet(Diet.VEGETARIAN)
                .setInstructions(new ArrayList<>())
                .build();

        assertEquals("('id_user','name','servings','type','diet','instructions')", recipe.toSQLColumnsInsert());
        assertEquals("('id','id_user','name','servings','type','diet','instructions')", recipe.toSQLColumnsRetrieve());
    }
}