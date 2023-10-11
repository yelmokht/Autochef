package ulb.infof307.g02.recipe;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g02.model.sql_model.recipe_model.IngredientRecipe;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.util.attributes.Unit;
import ulb.infof307.g02.util.import_export.JsonReader;
import ulb.infof307.g02.util.import_export.RecipeManager;
import ulb.infof307.g02.util.attributes.Meal;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestRecipeManager {

    private static final RecipeManager RECIPE_MANAGER = RecipeManager.getInstance();

    final List<IngredientRecipe> INGREDIENTS = List.of(
            IngredientRecipe.newBuilder().setName("flour").setQuantity(200).setUnit(Unit.GRAM).build(),
            IngredientRecipe.newBuilder().setName("water").setQuantity(175).setUnit(Unit.GRAM).build(),
            IngredientRecipe.newBuilder().setName("salt").setQuantity(10).setUnit(Unit.GRAM).build(),
            IngredientRecipe.newBuilder().setName("freshYeast").setQuantity(1).setUnit(Unit.GRAM).build()
    );

    final List<String> INSTRUCTIONS = new ArrayList<>(Arrays.asList(
            "Mix the YEAST and the WATER",
            "Mix the SALT and the FLOUR",
            "Mix the wet and the dry ingredients",
            "Knead on a worksurface until the dough is smooth",
            "Split the dough in equal pieces",
            "Cook a 220C for 10 to 15min or until golden brown")
    );

    @AfterAll
    static void cleanup() {
        getTempJsonTestFile("TestRecipe").delete();
    }

    @BeforeAll
    static void configure() {
        JsonReader.configure();
    }

    @BeforeEach
    void clearCache() {
        RECIPE_MANAGER.clearCache();
    }

    @Test
    void given_valid_path_then_json_recipes_are_loaded() {
        RECIPE_MANAGER.loadRecipes(getRecipeFolder());

        assertTrue(RECIPE_MANAGER.getRecipe("Pain").isPresent());
        assertTrue(RECIPE_MANAGER.getRecipe("PainBis").isPresent());
        assertTrue(RECIPE_MANAGER.getRecipe("Recette Inexistante").isEmpty());
    }

    @Test
    void given_null_recipe_folder_then_exception_is_raised() {
        assertThrows(IllegalArgumentException.class, () -> RECIPE_MANAGER.loadRecipes(null));
    }

    @Test
    void given_non_folder_file_then_exception_is_raised() {
        assertThrows(IllegalArgumentException.class, () -> RECIPE_MANAGER.loadRecipes(getJsonTestFile()));
    }

    @Test
    void given_valid_recipe_and_folder_then_valid_json_is_generated() {
        final var recipe = Recipe.newBuilder()
                .setName("TestRecipe")
                .setServings(2)
                .setType(Meal.DISH)
                .setIngredients(INGREDIENTS)
                .setInstructions(INSTRUCTIONS)
                .build();
        RECIPE_MANAGER.saveToJson(recipe, getRecipeFolder());
        final var generatedFile = getTempJsonTestFile("TestRecipe");

        assertEquals("TestRecipe.json", generatedFile.getName());
    }

    private static File getRecipeFolder() {
        return new File("." + File.separator + "test" + File.separator + "resources"
                + File.separator + "recipes");
    }

    private static File getJsonTestFile() {
        return new File("." + File.separator + "test" + File.separator + "resources" + File.separator
                + "50.json");
    }

    private static File getTempJsonTestFile(String tempFileName) {
        return new File("." + File.separator + "test" + File.separator + "resources" + File.separator +
                "recipes" + File.separator + tempFileName + ".json");
    }
}
