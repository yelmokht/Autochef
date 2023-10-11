package ulb.infof307.g02.gui.view.model_viewer;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ulb.infof307.g02.gui.view.ViewerController;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.ShoppingList;

import java.io.IOException;

public class ShoppingListViewerController extends ViewerController<ShoppingListViewerController.Listener> {

    @FXML
    private Label label;

    private ShoppingList shoppingList;

    private void setLabel(String shoppingListName) {
        label.setText(shoppingListName);
    }

    public void setShoppingList(ShoppingList shoppingList){
        this.shoppingList = shoppingList; setLabel(shoppingList.getName());
    }

    @FXML
    void visualizeButtonOnClicked() throws IOException {
        listener.inspectShoppingList(shoppingList);
    }

    @FXML
    void modifyButtonOnClicked() throws IOException {
        listener.configureShoppingList(shoppingList);
    }

    @FXML
    void deleteButtonOnClicked() throws IOException {
        listener.deleteShoppingList(shoppingList);
    }

    @FXML
    void emailButtonOnClicked() throws IOException {
        listener.emailShoppingList(shoppingList);
    }

    @FXML
    void saveButtonOnClicked() throws IOException {
        listener.saveShoppingList(shoppingList);
    }

    public interface Listener extends ViewerListener {

        void inspectShoppingList(ShoppingList shoppingList) throws IOException;

        void configureShoppingList(ShoppingList shoppingList) throws IOException;

        void deleteShoppingList(ShoppingList shoppingList) throws IOException;

        void emailShoppingList(ShoppingList shoppingList) throws IOException;

        void saveShoppingList(ShoppingList shoppingList) throws IOException;

    }

}
