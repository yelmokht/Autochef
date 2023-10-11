package ulb.infof307.g02.gui.view.main;

import javafx.fxml.FXML;
import ulb.infof307.g02.gui.view.ViewerController;

public class AutochefViewerController extends ViewerController<AutochefViewerController.Listener> {

    @FXML
    void shoppingListButtonOnClicked() {
        listener.goToShoppingList();
    }

    @FXML
    void recipeListButtonOnClicked() {
        listener.goToRecipeList();
    }

    @FXML
    void menuListButtonOnClicked() {
        listener.goToMenuList();
    }

    @FXML
    protected void disconnectButtonOnClicked() {
        listener.goToConnectionToAccount();
    }

    @FXML
    void storeListButtonOnClicked() {
        listener.goToStore();
    }

    public interface Listener extends ViewerController.ViewerListener {

        void goToShoppingList();

        void goToRecipeList();

        void goToMenuList();

        void goToStore();

        void goToConnectionToAccount();

    }

}
