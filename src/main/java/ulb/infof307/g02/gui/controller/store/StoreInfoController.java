package ulb.infof307.g02.gui.controller.store;

import ulb.infof307.g02.gui.controller.Controller;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.view.store.StoreInfoViewerController;
import ulb.infof307.g02.model.sql_model.store_model.Store;

public class StoreInfoController extends Controller implements StoreInfoViewerController.Listener {

    private final StoreInfoViewerController storeInfoViewerController;

    public StoreInfoController(Controller listener, Store store) {
        super(FXMLPath.INFO_STORE, listener);

        this.storeInfoViewerController = fxmlLoader.getController();
        storeInfoViewerController.setListener(this);
        storeInfoViewerController.setStore(store);
    }

    /**
     * Load all the ingredients of a store list from the database.
     * @param store which is the store list from which we want to get the ingredients
     */
    @Override
    public void loadIngredientsListFromDatabase(Store store) {
        storeInfoViewerController.setIngredientsList(store.getIngredientsStores());
    }

    @Override
    public void goToStoreList() {
        goTo(StoreListController::new);
    }

}
