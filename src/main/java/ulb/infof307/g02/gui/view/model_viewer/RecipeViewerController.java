package ulb.infof307.g02.gui.view.model_viewer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import ulb.infof307.g02.gui.view.ViewerController;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.model.sql_model.schedule_model.RecipeSchedule;
import ulb.infof307.g02.model.sql_super_class.SQLModel;

import java.io.IOException;

public class RecipeViewerController extends ViewerController<RecipeViewerController.Listener> {

    @FXML
    private Button modifyButton, deleteButton;

    @FXML
    private Label label;

    private SQLModel<?> recipe;

    private void setLabel(String recipeName) {
        label.setText(recipeName);
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        setLabel(recipe.getName());
    }

    public void setRecipeSchedule(RecipeSchedule recipe) {
        this.recipe = recipe;
        setLabel(recipe.getName());
    }

    public void setNotVisible() {
        modifyButton.setVisible(false);
        deleteButton.setVisible(false);
    }

    @FXML
    void visualizeButtonOnClicked() throws IOException {
        listener.inspectRecipe(recipe);
    }

    @FXML
    void modifyButtonOnClicked() throws IOException {
        listener.configureRecipe(recipe);
    }

    @FXML
    void deleteButtonOnClicked() throws IOException {
        listener.deleteRecipe(recipe);
    }

    public interface Listener extends ViewerController.ViewerListener {

        void inspectRecipe(SQLModel<?> recipe) throws IOException;

        void configureRecipe(SQLModel<?> recipe) throws IOException;

        void deleteRecipe(SQLModel<?> recipe) throws IOException;

    }

}