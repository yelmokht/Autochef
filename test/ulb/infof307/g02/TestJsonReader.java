package ulb.infof307.g02;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ulb.infof307.g02.database.DatabaseTool;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_model.recipe_model.IngredientRecipe;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.util.attributes.Department;
import ulb.infof307.g02.util.attributes.Unit;
import ulb.infof307.g02.util.import_export.JsonReader;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestJsonReader {

    private static ObjectMapper mapper;

    private static final String JSON_TEST_FILE = "TestRecipe.json";
    private static final String JSON_TEST_TEMP_FILE = "TmpTestRecipe.json";

    private static final Ingredient INGREDIENT = Ingredient.newBuilder().build();

    @BeforeAll
    static void setup() throws SQLException {
        mapper = JsonReader.getDefaultMapper();
        JsonReader.configure();

        Ingredient.newBuilder()
                .setName("flour")
                .setType(Department.BAKERY)
                .setUnit(Unit.GRAM)
                .build()
                .insertIntoDatabase();

        Ingredient.newBuilder()
                .setName("water")
                .setType(Department.BAKERY)
                .setUnit(Unit.GRAM)
                .build()
                .insertIntoDatabase();

        Ingredient.newBuilder()
                .setName("salt")
                .setType(Department.BAKERY)
                .setUnit(Unit.GRAM)
                .build()
                .insertIntoDatabase();

        Ingredient.newBuilder()
                .setName("freshYeast")
                .setType(Department.BAKERY)
                .setUnit(Unit.GRAM)
                .build()
                .insertIntoDatabase();
    }

    @AfterAll
    static void cleanup() throws SQLException {
        File testFile = getJsonTempFile();
        if(!(testFile.exists() && testFile.delete())){
            Autochef.getLogger().error("Temporary file failed to be deleted.");
        }
        INGREDIENT.removeFromDatabase();
    }

    @Test
    void given_json_file_then_matching_fields_are_retrieved() throws IOException {
        final var loadedRecipe = TestJsonReader.mapper.readValue(getJsonTestFile(), Recipe.class);
        final List<IngredientRecipe> ingredients = List.of(
                IngredientRecipe.newBuilder().setName("flour").setQuantity(200).setUnit(Unit.GRAM).build(),
                IngredientRecipe.newBuilder().setName("water").setQuantity(175).setUnit(Unit.GRAM).build(),
                IngredientRecipe.newBuilder().setName("salt").setQuantity(10).setUnit(Unit.GRAM).build(),
                IngredientRecipe.newBuilder().setName("freshYeast").setQuantity(1).setUnit(Unit.GRAM).build()
        );

        final List<String> instructions = new ArrayList<>(Arrays.asList(
                "Mix the YEAST and the WATER",
                "Mix the SALT and the FLOUR",
                "Mix the wet and the dry ingredients",
                "Knead on a worksurface until the dough is smooth",
                "Split the dough in equal pieces",
                "Cook a 220C for 10 to 15min or until golden brown")
        );

        assertEquals("Pain", loadedRecipe.getName());
        assertEquals(2, loadedRecipe.getServings());
        assertEquals(ingredients, loadedRecipe.getIngredients());
        assertEquals(instructions, loadedRecipe.getInstructions());
    }

    @Test
    void given_java_object_then_matching_json_is_generated() throws IOException {
        final var originalRecipe = TestJsonReader.mapper.readValue(getJsonTestFile(), Recipe.class);
        final var newJsonFile = getJsonTempFile();
        TestJsonReader.mapper.writeValue(newJsonFile, originalRecipe);
        final var newRecipe = TestJsonReader.mapper.readValue(newJsonFile, Recipe.class);

        assertEquals(originalRecipe, newRecipe);
    }

    private static File getJsonTempFile() {
        return new File("." + File.separator + "test" + File.separator + "resources" + File.separator
                + JSON_TEST_TEMP_FILE);
    }

    private static File getJsonTestFile() {
        return new File("." + File.separator + "test" + File.separator + "resources" + File.separator
                + JSON_TEST_FILE);
    }

}