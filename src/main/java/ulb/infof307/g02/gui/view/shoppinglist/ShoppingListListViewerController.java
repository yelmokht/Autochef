package ulb.infof307.g02.gui.view.shoppinglist;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.view.ViewerController;
import ulb.infof307.g02.gui.view.model_viewer.ShoppingListViewerController;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.ShoppingList;
import ulb.infof307.g02.util.AlertUtils;

import java.io.IOException;
import java.util.Objects;

import static ulb.infof307.g02.util.AlertUtils.*;

public class ShoppingListListViewerController extends ViewerController<ShoppingListListViewerController.Listener> {

    @FXML
    private VBox vbox;

    /**
     * Load the FXML of the Ingredient viewer
     */
    private FXMLLoader loadShoppingListViewer() {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(FXMLPath.SHOPPING_LIST_VIEWER.getPath())));
        try {
            loader.load();
        } catch (IOException e) {
            Autochef.getLogger().error("Couldn't load the ShoppingList Viewer", e);
            AlertUtils.alertWarningMessage("Une liste de course n'a pas pu être affichée");
        }
        return loader;
    }

    /**
     * Associate the controller of this view controller as the controller of the FXML of the ingredient viewer
     * (Ingredient Viewer is used at different locations, buttons need to be set differently each time)
     * @param loader : the FXMLLoader of the Recipe Viewer
     * @param shoppingList : the shopping list to show in the list of shopping lists
     */
    private void setShoppingListViewerListener(FXMLLoader loader, ShoppingList shoppingList) {
        ShoppingListViewerController controller = loader.getController();
        controller.setListener((ShoppingListViewerController.Listener) listener);
        controller.setShoppingList(shoppingList);
    }

    /**
     * Adds an HBox for each shopping list retrieved from the database.
     * @param shoppingList a shopping list
     */
    private void addShoppingListHBoxToList(ShoppingList shoppingList) {
        FXMLLoader loader = loadShoppingListViewer();
        setShoppingListViewerListener(loader, shoppingList);
        vbox.getChildren().add(loader.getRoot());
    }

    /**
     * Retrieves the shopping list received from the database.
     * @param filteredList shopping list
     */
    public void setList(FilteredList<ShoppingList> filteredList) {
        vbox.getChildren().clear();
        for (ShoppingList shoppingList : filteredList) {
            addShoppingListHBoxToList(shoppingList);
        }
    }

    /**
     * Refreshes the shopping list.
     */
    public void refreshList() {
        listener.loadShoppingListFromDatabase();
    }

    /**
     * Displays a warning message to the user when deleting a shopping list.
     * @return confirmation as a boolean
     */
    public boolean showAlert() {
        return alertConfirmationMessage(selectAlertDeleteMessage("shoppinglist"));
    }

    @FXML void createButtonOnClicked() {
        listener.createNewShoppingList();
    }

    @FXML void backButtonOnClicked() {
        listener.goToMainMenu();
    }

    @Override
    public void setListener(Listener listener) {
        super.setListener(listener);
        listener.loadShoppingListFromDatabase();
    }

    public interface Listener extends ViewerController.ViewerListener {

        void loadShoppingListFromDatabase();

        void createNewShoppingList();

        void goToMainMenu();

    }

}
