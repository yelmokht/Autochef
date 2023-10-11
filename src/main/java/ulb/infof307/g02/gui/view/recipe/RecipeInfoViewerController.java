package ulb.infof307.g02.gui.view.recipe;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ulb.infof307.g02.gui.view.ViewerController;
import ulb.infof307.g02.model.sql_model.recipe_model.IngredientRecipe;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.util.attributes.Diet;
import ulb.infof307.g02.util.attributes.Meal;

import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Iterables.size;

/**
 * RecipeInfoViewerController is a view controller used to show all the details of a recipe
 */
public class RecipeInfoViewerController extends ViewerController<RecipeInfoViewerController.Listener> {

    @FXML
    private Label recipeName, servings, mealType, dietType;

    @FXML
    private VBox vboxInstruction, vboxIngredient;

    private void setRecipeName(String text) {
        this.recipeName.setText(text);
    }

    private void setServings(int text) {
        this.servings.setText("Pour " + text + " personnes");
    }

    private void setMealType(Meal mealType) {
        this.mealType.setText("Type de plat: " + mealType.getMeal());
    }

    private void setDietType(Diet dietType) {
        this.dietType.setText("R\u00E9gime: " + dietType.getDiet());
    }

    private void setInstructions(List<String> list) {
        for (int i = 0; i < size(list); i++) {
            Label text = new Label("\u00c9tape " + (i + 1) + ": " + list.get(i));
            vboxInstruction.getChildren().add(text);
        }
    }

    public void setRecipe(Recipe recipe) {
        setRecipeName(recipe.getName());
        setServings(recipe.getServings());
        setMealType(recipe.getType());
        setDietType(recipe.getDiet());
        setInstructions(recipe.getInstructions());
        setIngredientsList(recipe.getIngredients());
    }

    public void setIngredientsList(List<IngredientRecipe> list) {
        for (IngredientRecipe ingredientRecipe : list) {
            Label ingredient = new Label(ingredientRecipe.getQuantity() + " " + ingredientRecipe.getUnit().getName() + " de " + ingredientRecipe.getName());
            vboxIngredient.getChildren().add(ingredient);
        }
    }

    @FXML
    protected void backButtonOnClicked() throws IOException {
        listener.goBack();
    }

    public void saveButtonOnClicked() {
        listener.saveRecipe();
    }

    public interface Listener extends ViewerController.ViewerListener {

        void saveRecipe();

        void goBack() throws IOException;

    }

}
