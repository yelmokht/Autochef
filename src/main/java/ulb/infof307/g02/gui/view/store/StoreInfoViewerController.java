package ulb.infof307.g02.gui.view.store;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ulb.infof307.g02.gui.controller.Controller;
import ulb.infof307.g02.gui.view.ViewerController;
import ulb.infof307.g02.model.sql_model.store_model.IngredientStore;
import ulb.infof307.g02.model.sql_model.store_model.Store;

import java.io.IOException;
import java.util.List;

public class StoreInfoViewerController extends ViewerController<StoreInfoViewerController.Listener> {

    @FXML
    private VBox vbox;

    @FXML
    private Label storeName;

    private void setStoreName(String storeName) {
        this.storeName.setText(storeName);
    }

    public void setStore(Store store) {
        this.setStoreName(store.getName());
        listener.loadIngredientsListFromDatabase(store);
    }

    public void setIngredientsList(List<IngredientStore> list) {
        for (IngredientStore ingredientStore : list) {
            Label ingredient = new Label("'" + ingredientStore.getName() + "' coûte : " + ingredientStore.getQuantity() + "€");
            vbox.getChildren().add(ingredient);
        }
    }

    @FXML
    void backButtonOnClicked()  {
        listener.goToStoreList();
    }

    public interface Listener extends ViewerListener {

        void loadIngredientsListFromDatabase(Store store);

        void goToStoreList();

    }

}
