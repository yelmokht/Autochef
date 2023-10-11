package ulb.infof307.g02.gui.controller.recipe;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.controller.Controller;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.controller.schedule.ScheduleConfigureController;
import ulb.infof307.g02.gui.view.model_viewer.IngredientViewerController;
import ulb.infof307.g02.gui.view.recipe.RecipeConfigureViewerController;
import ulb.infof307.g02.model.sql_super_class.SQLModel;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_model.recipe_model.IngredientRecipe;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.util.AlertUtils;
import ulb.infof307.g02.util.attributes.Department;
import ulb.infof307.g02.util.attributes.Unit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static ulb.infof307.g02.util.AlertUtils.alertWarningMessage;
import static ulb.infof307.g02.util.AlertUtils.selectAlertMessage;

/**
 * Controller used when creating or modifying a recipe in the app (behaviour of controller changes a bit when modifying,
 * which explain the "modifying" attribute)
 */
public class RecipeConfigureController extends Controller implements RecipeConfigureViewerController.Listener, IngredientViewerController.Listener {

    private static final Ingredient INGREDIENT = Ingredient.newBuilder().build();

    private final RecipeConfigureViewerController recipeConfigureViewerController;

    private Recipe oldRecipe;
    private Recipe recipe;
    private boolean isModifying = false;

    private List<IngredientRecipe> recipeIngredientList = new ArrayList<>();

    public RecipeConfigureController(Controller listener) {
        super(FXMLPath.CONFIGURE_RECIPE, listener);

        this.recipeConfigureViewerController = fxmlLoader.getController();
        recipeConfigureViewerController.setListener(this);

        loadIngredientFromDatabase();
    }

    public RecipeConfigureController(Controller listener, Recipe recipe) {
        this(listener);

        // Using this constructor means tha twe are modifying an existing recipe
        this.oldRecipe = recipe; // Backup, let us search the old element to delete it from database when necessary
        this.recipe = recipe; // Object that is going to be modified
        loadIngredientFromRecipe();
        setModifying(true);
        recipeConfigureViewerController.setRecipe(recipe);
    }

    /**
     * Load all ingredients from the database.
     */
    @Override
    public void loadIngredientFromDatabase() {
        try {
            List<Ingredient> list = INGREDIENT.getModels();
            ObservableList<Ingredient> details = FXCollections.observableArrayList(list);
            FilteredList<Ingredient> filteredList = new FilteredList<>(details, p -> true);
            recipeConfigureViewerController.setupTableView(filteredList);
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't load ingredient from database", e);
            AlertUtils.alertWarningMessage("Les ingrédients n'ont pas pu être chargé");
        }
    }

    /**
     * Adds an ingredient to the database.
     * @param ingredientName name of the ingredient
     * @param ingredientType type of the ingredient
     */
    @Override
    public void addIngredientToDatabase(String ingredientName, String ingredientType, String ingredientUnit) {
        try {
            Ingredient.newBuilder()
                    .setName(ingredientName)
                    .setType(Department.fromString(ingredientType))
                    .setUnit(Unit.fromString(ingredientUnit))
                    .build()
                    .insertIntoDatabase();
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't insert ingredient into database", e);
            AlertUtils.alertWarningMessage("L'ingrédient n'ont pas pu être sauvegardés");
        }
        loadIngredientFromDatabase();
    }

    /**
     * Delete an ingredient from the database.
     * @param ingredient is the Ingredient object
     */
    @Override
    public void removeIngredientFromDatabase(Ingredient ingredient) {
        try {
            ingredient.removeFromDatabase();
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't delete ingredient from database", e);
            AlertUtils.alertWarningMessage("L'ingrédient n'a pas pu être supprimé");
        }
        loadIngredientFromDatabase();
    }

    /**
     * Load the ingredients of an existing recipe.
     */
    public void loadIngredientFromRecipe() {
        this.recipeIngredientList = oldRecipe.getIngredients();

        for (IngredientRecipe ingredientRecipe : recipeIngredientList) {
            recipeConfigureViewerController.addIngredientHBoxToRecipe(ingredientRecipe);
        }
    }

    /**
     * Adds an ingredient to a recipe.
     * @param ingredientRecipe which is an ingredient object linked to a recipe
     */
    @Override
    public void addIngredientToRecipe(IngredientRecipe ingredientRecipe) {
        recipeIngredientList.add(ingredientRecipe);
        recipeConfigureViewerController.addIngredientHBoxToRecipe(ingredientRecipe);
    }

    /**
     * Remove an ingredient from a recipe.
     * @param ingredient which is an Ingredient object (which implements SQLModel)
     */
    @Override
    public void removeIngredientFromList(SQLModel<?> ingredient) {
        recipeIngredientList.remove((IngredientRecipe) ingredient);
        recipeConfigureViewerController.removeIngredientHBoxFromRecipe((IngredientRecipe) ingredient);
    }

    /**
     * Allows to create a new recipe.
     * @param recipe which is the Recipe object
     */
    @Override
    public void createNewRecipe(Recipe recipe) {
        try {
            recipe.setIngredients(recipeIngredientList);
            recipe.insertIntoDatabase();
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't insert a recipe into the database", e);
            AlertUtils.alertWarningMessage("La recette n'a pas pu être sauvegardée");
        }

        goTo(RecipeListController::new);
    }


    /**
     * Allows you to delete an existing recipe.
     * @param recipe indicates the recipe to delete
     */
    private void deleteRecipe(Recipe recipe) {
        try {
            recipe.removeFromDatabase();
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't remove a recipe from the database", e);
            AlertUtils.alertWarningMessage("La recette n'a pas pu être supprimée");
        }
    }

    @Override
    public void saveRecipe(Recipe recipeToSave) {
        try {
            recipe = recipeToSave;

            if (recipe.doesModelExists() && !isModifying) {
                Autochef.getLogger().info("Recipe already exists");
                alertWarningMessage(selectAlertMessage("recipeName"));
            } else {
                if (isModifying) {
                    // Allows the user to save an existing Recipe after modifying it
                    deleteRecipe(oldRecipe);
                }

                createNewRecipe(recipe);
            }
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't verify existence of Recipe model");
            AlertUtils.alertWarningMessage("La recette n'a pas pu être sauvegardée");
        }
    }

    @Override
    public void goBack() {
        if (parentController instanceof RecipeListController) {
            goTo(RecipeListController::new);
        } else if (parentController instanceof ScheduleConfigureController) {
            goTo(ScheduleConfigureController::new);
        }
    }

    public boolean isModifying() {
        return isModifying;
    }

    public void setModifying(boolean modifying) {
        isModifying = modifying;
    }
}
