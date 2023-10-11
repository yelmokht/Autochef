package ulb.infof307.g02.gui.view.model_viewer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import ulb.infof307.g02.gui.view.ViewerController;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.ShoppingList;

public class ShoppingListStoreViewerController extends ViewerController<ShoppingListStoreViewerController.Listener> {
    @FXML
    private Label label;

    private int storeID;

    @FXML
    void visualizeButtonOnClicked() {
        listener.showPriceInfo(storeID);
    }

    public void setLabel(String recipeName) {
        label.setText(recipeName);
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }


    public interface Listener extends ViewerListener {

        void showPriceInfo(int storeID);

    }

}