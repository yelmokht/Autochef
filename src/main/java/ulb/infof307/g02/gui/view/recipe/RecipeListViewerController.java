package ulb.infof307.g02.gui.view.recipe;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.view.ViewerController;
import ulb.infof307.g02.gui.view.model_viewer.RecipeViewerController;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.util.AlertUtils;

import java.io.IOException;
import java.util.Objects;

import static ulb.infof307.g02.util.AlertUtils.alertConfirmationMessage;
import static ulb.infof307.g02.util.AlertUtils.selectAlertDeleteMessage;

/**
 * RecipeListViewerController is a view controller used to show all the recipes of the user in a single place of the
 * application. Allow the user to manage his recipes (See, Modify or Delete).
 */
public class RecipeListViewerController extends ViewerController<RecipeListViewerController.Listener> {

    @FXML
    private VBox vbox;

    public void init(){
        refreshList();
    }

    /**
     * Adds an HBox (the Recipe viewer) of a recipe in the list of recipes.
     * @param recipe : the Recipe associated to the HBox
     */
    private void addRecipeHBoxToList(Recipe recipe) {
        // Load the FXML of the recipe viewer
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(FXMLPath.RECIPE_VIEWER.getPath())));
        try {
            loader.load();
        } catch (IOException e) {
            Autochef.getLogger().error("Couldn't load the Recipe Viewer", e);
            AlertUtils.alertWarningMessage("Une recette n'a pas pu être affichée");
        }


        // Associate the controller of this view controller as the controller of the FXML of the recipe viewer
        // (Recipe Viewer is used at different locations, buttons need to be set differently each time)
        RecipeViewerController controller = loader.getController();
        controller.setListener((RecipeViewerController.Listener) listener);
        controller.setRecipe(recipe);

        // Add the Recipe Viewer to the list of Recipe
        vbox.getChildren().add(loader.getRoot());
    }

    public void setList(FilteredList<Recipe> filteredList) {
        vbox.getChildren().clear();
        for (Recipe recipe : filteredList) {
            addRecipeHBoxToList(recipe);
        }
    }

    /**
     * Refreshes the list of recipes.
     */
    public void refreshList() {
        listener.loadRecipeFromDatabase();
    }

    /**
     * Displays a warning message to the user when a recipe is deleted.
     * @return confirmation as a boolean
     */
    public boolean showAlert() {return alertConfirmationMessage(selectAlertDeleteMessage("recipe"));}

    @FXML
    void createButtonOnClicked() throws IOException {
        listener.createNewRecipe();
    }

    @FXML
    void importButtonOnClicked() throws IOException {
        listener.importNewRecipe();
    }

    @FXML
    void backButtonOnClicked() {
        listener.goToMainMenu();
    }

    public interface Listener extends ViewerController.ViewerListener {

        void loadRecipeFromDatabase();

        void createNewRecipe() throws IOException;

        void importNewRecipe() throws IOException;

        void goToMainMenu();

    }

}
