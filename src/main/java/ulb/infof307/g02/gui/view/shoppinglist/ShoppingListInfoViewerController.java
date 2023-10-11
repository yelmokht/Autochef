package ulb.infof307.g02.gui.view.shoppinglist;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.view.ViewerController;
import ulb.infof307.g02.gui.view.model_viewer.ShoppingListStoreViewerController;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.IngredientShoppingList;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.ShoppingList;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ShoppingListInfoViewerController extends ViewerController<ShoppingListInfoViewerController.Listener> {

    @FXML
    private VBox ingredientBox;
    @FXML
    private VBox storeBox;
    @FXML
    private Label shoppingName;

    private void setShoppingName(String shoppingListName) {
        shoppingName.setText(shoppingListName);
    }

    public void setShoppingList(ShoppingList shopping) {
        setShoppingName(shopping.getName());
        listener.loadIngredientsListFromDatabase(shopping);

    }

    public void setIngredientsList(List<IngredientShoppingList> list) {
        for (IngredientShoppingList ingredientShoppingList : list) {
            Label ingredient = new Label(ingredientShoppingList.getQuantity() + " " + ingredientShoppingList.getUnit().getName() + " de " + ingredientShoppingList.getName());
            ingredientBox.getChildren().add(ingredient);
        }
    }

    public void addStoreHBoxToList(int storeID, String labelText) {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/ulb/infof307/g02/gui/view/ShoppingListStoreViewer.fxml")));
        try {
            loader.load();
            ShoppingListStoreViewerController controller = loader.getController();
            controller.setListener((ShoppingListStoreViewerController.Listener) listener);
            controller.setStoreID(storeID);
            controller.setLabel(labelText);
            storeBox.getChildren().add(loader.getRoot());
        } catch (IOException e) {
            Autochef.getLogger().error("Could not load ShoppingListStore FXML properly.");
        }
    }

    @FXML
    void backButtonOnClicked() {
        listener.goBackToShoppingList();
    }

    public interface Listener extends ViewerController.ViewerListener {

        void loadIngredientsListFromDatabase(ShoppingList shopping);

        void goBackToShoppingList();

    }

}