package ulb.infof307.g02.gui.view.shoppinglist;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ulb.infof307.g02.gui.view.ViewerController;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.IngredientShoppingList;

import java.util.List;

public class ShoppingListStoreInfoViewerController extends ViewerController<ShoppingListStoreInfoViewerController.Listener> {

    @FXML
    Label storeNameLabel;
    @FXML
    Label totalPriceLabel;
    @FXML
    VBox missingIngredientBox;
    @FXML
    VBox presentIngredientBox;
    @FXML
    Button backButton;

    public void setStoreName(String storeName) {
        storeNameLabel.setText(storeName);
    }

    public void addPresentIngredient(String formattedIngredient) {
        var ingredientLabel = new Label(formattedIngredient);

        presentIngredientBox.getChildren().add(ingredientLabel);
    }

    public void addMissingIngredient(String formattedIngredient) {
        var ingredientLabel = new Label(formattedIngredient);

        missingIngredientBox.getChildren().add(ingredientLabel);
    }

    public void setTotalPrice(int totalPrice) {
        totalPriceLabel.setText(Double.toString(totalPrice)+ "€");
    }

    @FXML
    void backButtonOnClicked(){
        listener.goBack();
    }

    public interface Listener extends ViewerController.ViewerListener {

        void setStoreName(String storeName);

        void setTotalPrice();

        void addIngredients(List<IngredientShoppingList> ingredients);

        void goBack();

    }

}
