package ulb.infof307.g02.gui.view.store;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.view.ViewerController;
import ulb.infof307.g02.gui.view.model_viewer.StoreViewerController;
import ulb.infof307.g02.model.sql_model.store_model.Store;
import ulb.infof307.g02.util.AlertUtils;

import java.io.IOException;
import java.util.Objects;

import static ulb.infof307.g02.util.AlertUtils.alertConfirmationMessage;
import static ulb.infof307.g02.util.AlertUtils.selectAlertDeleteMessage;

public class StoreListViewerController extends ViewerController<StoreListViewerController.Listener> {

    @FXML
    private VBox vbox;

    /**
     * Adds an HBox for each shopping list retrieved from the database.
     * @param store a shopping list
     */
    private void addStoreHBoxToList(Store store) {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(FXMLPath.STORE_VIEWER.getPath())));
        try {
            loader.load();
        } catch (IOException e) {
            Autochef.getLogger().error("Couldn't load the Store Viewer", e);
            AlertUtils.alertWarningMessage("Un magasin n'a pas pu être affiché");
        }

        StoreViewerController controller = loader.getController();
        controller.setListener((StoreViewerController.Listener) listener);
        controller.setStore(store);
        vbox.getChildren().add(loader.getRoot());
    }

    /**
     * Retrieves the shopping list received from the database.
     * @param filteredList shopping list
     */
    public void setList(FilteredList<Store> filteredList) {
        vbox.getChildren().clear();
        for (Store Store : filteredList) {
            addStoreHBoxToList(Store);
        }
    }

    /**
     * Refreshes the shopping list.
     */
    public void refreshList() {
        listener.loadStoreFromDatabase();
    }

    /**
     * Displays a warning message to the user when deleting a shopping list.
     * @return confirmation as a boolean
     */
    public boolean showAlert() {
        return alertConfirmationMessage(selectAlertDeleteMessage("Store"));
    }

    @FXML
    void createButtonOnClicked() {
        listener.createNewStore();
    }

    @FXML
    void backButtonOnClicked() {
        listener.goToMainMenu();
    }

    @Override
    public void setListener(Listener listener){
        super.setListener(listener);
        try {
            listener.loadStoreFromDatabase();
        } catch (Exception e) {
            Autochef.getLogger().error("Couldn't load stores from database", e);
        }
    }

    public interface Listener extends ViewerListener {

        void loadStoreFromDatabase();

        void createNewStore();

        void goToMainMenu();

    }

}
