package ulb.infof307.g02.gui.controller.recipe;

import javafx.stage.FileChooser;
import ulb.infof307.g02.gui.controller.Controller;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.controller.schedule.ScheduleConfigureController;
import ulb.infof307.g02.gui.controller.schedule.ScheduleInfoController;
import ulb.infof307.g02.gui.view.recipe.RecipeInfoViewerController;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.util.import_export.RecipeManager;

import java.io.File;

/**
 * RecipeInfoController implements the Listener of RecipeInfoViewerController to program its buttons and
 * functionalities
 */
public class RecipeInfoController extends Controller implements RecipeInfoViewerController.Listener {

    private final Recipe recipe;
    public RecipeInfoController(Controller listener, Recipe recipe) {
        super(FXMLPath.INFO_RECIPE, listener);

        RecipeInfoViewerController recipeInfoViewerController = fxmlLoader.getController();
        recipeInfoViewerController.setListener(this);
        recipeInfoViewerController.setRecipe(recipe);
        this.recipe = recipe;
    }

    /**
     * Saves the recipe currently shown on screen
     */
    @Override
    public void saveRecipe() {
        //setup fileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisissez o\u00f9 sauvegarder la recette :");
        File recipeDir = new File("./recettes/");
        // setup save Directory
        if(!recipeDir.isDirectory()){
            recipeDir.mkdir();
        }
        fileChooser.setInitialDirectory(recipeDir);
        // setup extention
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
        fileChooser.setInitialFileName(recipe.getName()+".json");
        File jsonFile = fileChooser.showSaveDialog(stage);
        RecipeManager.getInstance().saveToJson(recipe, jsonFile);
    }

    @Override
    public void goBack() {
        if (parentController instanceof RecipeListController) {
            goTo(RecipeListController::new);
        } else if (parentController instanceof ScheduleConfigureController) {
            goTo(ScheduleConfigureController::new);
        } else if (parentController instanceof ScheduleInfoController) {
            goTo(ScheduleInfoController::new);
        }
    }

}
