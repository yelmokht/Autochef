package ulb.infof307.g02.util;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;

public class RecipeCellFactory implements Callback<ListView<Recipe>, ListCell<Recipe>> {
    int maxSize = 120;
    @Override
    public ListCell<Recipe> call(ListView<Recipe> param) {
        return new ListCell<>(){
            @Override
            public void updateItem(Recipe recipe, boolean empty) {
                super.updateItem(recipe, empty);
                if (empty || recipe == null) {
                    setText(null);
                } else {
                    int paddingSize = (maxSize - (recipe.getName().length() + recipe.getType().getMeal().length()));
                    String padding = String.format("%-"+ paddingSize +"s"," :");
                    setText(recipe.getName() + padding + recipe.getType().getMeal());
                }
            }
        };
    }
}