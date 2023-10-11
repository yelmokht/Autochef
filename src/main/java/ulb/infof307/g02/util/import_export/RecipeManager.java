package ulb.infof307.g02.util.import_export;

import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class RecipeManager {

    private static final RecipeManager INSTANCE = new RecipeManager();

    private static final String EXTENSION_NAME = ".json";

    private final Set<Recipe> loadedRecipes = new HashSet<>();

    private RecipeManager() { }

    /**
     * Load all the json recipes from the given folder.
     *
     * @param recipeFolder the recipe folder
     */
    public void loadRecipes(File recipeFolder) {
        if(recipeFolder == null) {
            throw new IllegalArgumentException("recipeFolder cannot be null.");
        }
        if(!recipeFolder.isDirectory()) {
            throw new IllegalArgumentException("recipeFolder must be a folder.");
        }
        final var files = recipeFolder.listFiles();

        if(files == null) {
            return;
        }

        for(final var file : files) {
            loadRecipe(file);
        }
    }

    public void loadRecipesFromDB () {
        try {
            loadedRecipes.addAll(Recipe.newBuilder().build().getModels());
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't retrieve models from database", e);
        }
    }

    /**
     * Load a recipe from the given file.
     *
     * @param file the recipe file
     * @return a recipe object
     */
    public Recipe loadRecipe(File file) {
        if(file == null) {
            throw new IllegalArgumentException("file cannot be null.");
        }
        if(!file.getName().endsWith(EXTENSION_NAME)) {
            throw new IllegalArgumentException("file must be a json file");
        }
        try {
            final var recipe = JsonReader.getDefaultMapper().readValue(file, Recipe.class);
            loadedRecipes.add(recipe);

            return recipe;
        } catch (IOException e) {
            Autochef.getLogger().error("Could not load recipe from given File");
        }
        return null;
    }

    /**
     * Save the given recipe in the cache.
     *
     * @param recipe the recipe
     */
    public void saveRecipe(Recipe recipe) {
        loadedRecipes.add(recipe);
        try {
            recipe.insertIntoDatabase();
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't retrieve models from database", e);
        }
    }

    /**
     * Save the given recipe to a json file.
     *
     * @param recipe the recipe
     * @param destinationFile the destination file where the recipe will be written to
     */
    public void saveToJson(Recipe recipe, File destinationFile) {
        if(recipe == null) {
            return;
        }
        if(destinationFile == null) {
            throw new IllegalArgumentException("destinationFile cannot be null.");
        }
        try {
            if(!destinationFile.createNewFile()) {
                Autochef.getLogger().info("You are overwriting an already existing file");
            }
            JsonReader.getDefaultMapper().writeValue(destinationFile, recipe);
        } catch (IOException e) {
            Autochef.getLogger().error("Saving to JSON unsuccessful");
        }
    }

    /**
     * Save all the loaded recipes as json files in the destination folder.
     *
     * @param destinationFolder the destination folder
     */
    public void saveAllRecipes(File destinationFolder) {
        if(destinationFolder == null) {
            throw new IllegalArgumentException("destinationFolder cannot be null.");
        }
        if(!destinationFolder.isDirectory()) {
            throw new IllegalArgumentException("destinationFolder must be a folder.");
        }
        loadedRecipes.forEach(recipe -> saveToJson(recipe, destinationFolder));
    }

    public void deleteRecipe(Recipe recipe) {
        loadedRecipes.remove(recipe);
        try {
            recipe.removeFromDatabase();
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't remove the recipe from the database", e);

        }
    }

    public void clearCache() {
        loadedRecipes.clear();
    }

    /**
     * Get the recipe with the given id.
     *
     * @param name the recipe name
     * @return the recipe (name is a unique constraint)
     */
    public Optional<Recipe> getRecipe(String name) {
        return loadedRecipes.stream().filter(recipe -> (Objects.equals(recipe.getName(), name))).findFirst();
    }

    public static RecipeManager getInstance() {
        return INSTANCE;
    }

}