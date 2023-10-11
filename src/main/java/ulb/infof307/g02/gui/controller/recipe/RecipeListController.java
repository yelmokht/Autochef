package ulb.infof307.g02.gui.controller.recipe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.stage.FileChooser;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.controller.Controller;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.view.recipe.RecipeListViewerController;
import ulb.infof307.g02.gui.view.model_viewer.RecipeViewerController;
import ulb.infof307.g02.model.sql_super_class.SQLModel;
import ulb.infof307.g02.model.sql_model.recipe_model.IngredientRecipe;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_model.user_model.User;
import ulb.infof307.g02.util.import_export.RecipeManager;
import ulb.infof307.g02.util.AlertUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * RecipeListController implements the Listener of RecipeListViewerController to program its buttons and
 * functionalities
 */
public class RecipeListController extends Controller implements  RecipeListViewerController.Listener, RecipeViewerController.Listener {

    private final RecipeListViewerController recipeListViewerController;

    public RecipeListController(){
        super(FXMLPath.LIST_OF_RECIPE);
        this.recipeListViewerController = fxmlLoader.getController();

        recipeListViewerController.setListener(this);
        recipeListViewerController.init();
    }

    /**
     * Loads all recipes from the database and displays them in the associated view.
     */
    @Override
    public void loadRecipeFromDatabase() {
        try {
            List<Recipe> list = Recipe.newBuilder()
                    .setUserID(User.getInstance().getId())
                    .build()
                    .getModels();
            ObservableList<Recipe> details = FXCollections.observableArrayList(list);
            FilteredList<Recipe> filteredList = new FilteredList<>(details, p -> true);
            recipeListViewerController.setList(filteredList);
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't load recipes from database", e);
            AlertUtils.alertWarningMessage("Les recettes n'ont pas pu être chargé");
        }
    }

    /**
     * Allows you to create a new recipe.
     */
    @Override
    public void createNewRecipe() throws IOException {
        goTo(() -> new RecipeConfigureController(this));
    }

    /**
     * Open a FileChooser to allow the user to select a file
     */
    private File openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Fichier JSON", "*.json");
        fileChooser.setTitle("Importer un fichier JSON");
        fileChooser.getExtensionFilters().addAll(filter);

        return fileChooser.showOpenDialog(stage);
    }

    /**
     * Allows you to import a recipe via a file in JSON format.
     */
    @Override
    public void importNewRecipe() {
        File selectedFile = openFileChooser();

        // Insert the Recipes of the selected file in the database
        if (selectedFile != null) {
            try {
                Recipe newRecipe = RecipeManager.getInstance().loadRecipe(selectedFile);
                newRecipe.setUserID(User.getInstance().getId());

                List<IngredientRecipe> ingredientsOfRecipe = newRecipe.getIngredients();
                // inserts every ingredient that is not in the db into it
                for (var ingredientRecipe : ingredientsOfRecipe) {
                    final var searchIngredient = Ingredient.newBuilder()
                            .setName(ingredientRecipe.getName())
                            .build();

                    if (!searchIngredient.doesModelExists()) {
                        Ingredient.newBuilder()
                                .setName(ingredientRecipe.getName())
                                .setUnit(ingredientRecipe.getUnit())
                                .setType(ingredientRecipe.getDepartment())
                                .build()
                                .insertIntoDatabase();
                    }
                }

                newRecipe.insertIntoDatabase();
            } catch (SQLException e) {
                Autochef.getLogger().error("Couldn't insert imported Recipes into the database", e);
                AlertUtils.alertWarningMessage("Une erreur est survenue.\nL'importation de recettes n'a pas eu lieu.");
            }

            recipeListViewerController.refreshList();
        }
    }

    /**
     * Allows you to view existing recipes (name, servings, diet, type and ingredients).
     * @param recipe indicates the recipe to visualize
     */
    @Override
    public void inspectRecipe(SQLModel<?> recipe) {
        goTo(model -> new RecipeInfoController(this, model), (Recipe) recipe);
    }

    /**
     * Allows you to modify an existing recipe (name, number of people, diet, type and ingredients).
     * @param recipe indicates the recipe to modify
     */
    @Override
    public void configureRecipe(SQLModel<?> recipe) {
        goTo(model -> new RecipeConfigureController(this, model), (Recipe) recipe);
    }

    /**
     * Allows you to delete an existing recipe and displays a confirmation window.
     * @param recipe indicates the recipe to delete
     */
    @Override
    public void deleteRecipe(SQLModel<?> recipe) {
        if (recipeListViewerController.showAlert()) {
            try {
                recipe.removeFromDatabase();
            } catch (SQLException e) {
                Autochef.getLogger().error("Couldn't delete recipe from database", e);
                AlertUtils.alertWarningMessage("La recette n'a pas pu être supprimée");
            }

            recipeListViewerController.refreshList();
        }
    }

}
