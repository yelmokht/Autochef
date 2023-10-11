package ulb.infof307.g02.gui.view.model_viewer;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ulb.infof307.g02.gui.view.ViewerController;
import ulb.infof307.g02.model.sql_model.store_model.Store;

public class StoreViewerController extends ViewerController<StoreViewerController.Listener> {
    @FXML
    private Label label;

    private Store store;

    private void setLabel(String storeName) {
        label.setText(storeName);
    }

    public void setStore(Store store){
        this.store = store; setLabel(store.getName());
    }

    @FXML
    void visualizeButtonOnClicked() {
        listener.inspectStore(store);
    }

    @FXML
    void modifyButtonOnClicked() {
        listener.configureStore(store);
    }

    @FXML
    void deleteButtonOnClicked() {
        listener.deleteStore(store);
    }

    public interface Listener extends ViewerListener {

        void inspectStore(Store store);

        void configureStore(Store store);

        void deleteStore(Store store);

    }

}
