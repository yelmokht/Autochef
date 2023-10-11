package ulb.infof307.g02.gui.controller.store;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import ulb.infof307.g02.gui.controller.Controller;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.view.store.StoreListViewerController;
import ulb.infof307.g02.gui.view.model_viewer.StoreViewerController;
import ulb.infof307.g02.model.sql_model.store_model.Store;
import ulb.infof307.g02.util.AlertUtils;

import java.sql.SQLException;

public class StoreListController extends Controller
        implements StoreListViewerController.Listener, StoreViewerController.Listener {

    private final StoreListViewerController storeListViewerController;

    public StoreListController(){
        super(FXMLPath.LIST_OF_STORES);

        this.storeListViewerController = fxmlLoader.getController();
        storeListViewerController.setListener(this);
    }

    /**
     * Loads all the stores from the database and displays them in the associated view.
     */
    @Override
    public void loadStoreFromDatabase() {
        try {
            final ObservableList<Store> details = FXCollections.observableArrayList(Store
                    .newBuilder()
                    .build()
                    .getModels());
            storeListViewerController.setList(new FilteredList<>(details, p -> true));
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't load the stores from database", e);
            AlertUtils.alertWarningMessage("Les magasins n'ont pas pu être chargés");
        }
    }

    /**
     * Allows you to create a new shopping list.
     */
    @Override
    public void createNewStore() {
        goTo(() -> new StoreConfigureController(this));
    }

    /**
     * Allows you to view an existing shopping list (name and ingredients).
     * @param store indicates the shopping list to visualize
     */
    @Override
    public void inspectStore(Store store) {
        goTo(model -> new StoreInfoController(this, (Store) model), store);
    }

    /**
     * Allows you to view an existing shopping list (name and ingredients).
     * @param store indicates the shopping list to visualize
     */
    @Override
    public void configureStore(Store store) {
        goTo(model -> new StoreConfigureController(model, this), store);
    }

    /**
     * Allows you to delete an existing shopping list and displays a confirmation window.
     * @param store indicates the shopping list to delete
     */
    @Override
    public void deleteStore(Store store) {
        if (storeListViewerController.showAlert()) {
            try {
                store.removeFromDatabase();
                storeListViewerController.refreshList();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
