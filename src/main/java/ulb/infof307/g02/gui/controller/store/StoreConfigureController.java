package ulb.infof307.g02.gui.controller.store;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import ulb.infof307.g02.gui.controller.Controller;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.view.model_viewer.IngredientViewerController;
import ulb.infof307.g02.gui.view.store.StoreConfigureViewerController;
import ulb.infof307.g02.model.sql_super_class.SQLModel;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_model.store_model.IngredientStore;
import ulb.infof307.g02.model.sql_model.store_model.Store;
import ulb.infof307.g02.util.AlertUtils;
import ulb.infof307.g02.util.attributes.Department;
import ulb.infof307.g02.util.attributes.Unit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StoreConfigureController extends Controller implements StoreConfigureViewerController.Listener,
        IngredientViewerController.Listener  {

    private final StoreConfigureViewerController storeConfigureViewerController;
    private final List<IngredientStore> ingredientStoreList = new ArrayList<>();

    private Store store;

    private boolean isModifying = false;

    public StoreConfigureController(Controller listener) {
        super(FXMLPath.CONFIGURE_STORE, listener);

        this.storeConfigureViewerController = fxmlLoader.getController();
        storeConfigureViewerController.setListener(this);
        storeConfigureViewerController.loadDatabase();
    }

    public StoreConfigureController(Store store, Controller listener) {
        this(listener);
        this.store = store;
        setIsModifying(true);
        storeConfigureViewerController.setStore(store);
    }

    public boolean isModifying() {
        return isModifying;
    }

    public void setIsModifying(boolean isModifying) {
        this.isModifying = isModifying;
    }

    /**
     * Load all ingredients from the database.
     */
    @Override
    public void loadIngredientFromDatabase() {
        try {
            final ObservableList<Ingredient> details = FXCollections.observableArrayList(Ingredient.newBuilder()
                    .build()
                    .getModels());
            storeConfigureViewerController.setUpTableView(new FilteredList<>(details, p -> true));
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't load ingredients from database", e);
            AlertUtils.alertWarningMessage("Les ingrédients n'ont pas pu être chargés");
        }
    }

    /**
     * Adds an ingredient to the database.
     * @param ingredientName name of the ingredient
     * @param ingredientType type of the ingredient
     */
    @Override
    public void addIngredientToDatabase(String ingredientName, String ingredientType, String ingredientUnit) {
        try {
            Ingredient.newBuilder()
                    .setName(ingredientName)
                    .setType(Department.fromString(ingredientType))
                    .setUnit(Unit.fromString(ingredientUnit))
                    .build()
                    .insertIntoDatabase();
            storeConfigureViewerController.refreshTable();
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't insert ingredient into database", e);
            AlertUtils.alertWarningMessage("L'ingrédient n'a pas pu être sauvegardé");
        }
    }

    /**
     * Delete an ingredient from the database.
     * @param ingredient is the Ingredient object
     */
    @Override
    public void removeIngredientFromDatabase(Ingredient ingredient) {
        try {
            ingredient.removeFromDatabase();
            storeConfigureViewerController.refreshTable();
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't remove ingredient from database", e);
            AlertUtils.alertWarningMessage("L'ingrédient n'a pas pu être supprimé");
        }
    }

    /**
     * Load ingredients from an existing shopping list.
     */
    @Override
    public void loadIngredientFromStore() {
        for (IngredientStore ingredientStore : store.getIngredientsStores()) {
            ingredientStoreList.add(ingredientStore);
            storeConfigureViewerController.addIngredientHBoxToStore(ingredientStore);
        }
    }

    /**
     * Adds an ingredient to a shopping list.
     * @param ingredientStore which is an ingredient object linked to a shopping list
     */
    @Override
    public void addIngredientToStore(IngredientStore ingredientStore) {
        ingredientStoreList.add(ingredientStore);
        storeConfigureViewerController.addIngredientHBoxToStore(ingredientStore);
    }

    /**
     * Remove an ingredient from a shopping list.
     * @param ingredient which is an Ingredient object (which implements SQLModel)
     */
    @Override
    public void removeIngredientFromList(SQLModel<?> ingredient) {
        ingredientStoreList.remove((IngredientStore) ingredient);
        storeConfigureViewerController.removeIngredientHBoxFromStore((IngredientStore) ingredient);
    }

    /**
     * Allows you to create a new shopping list.
     * @param storeName which is the name of the shopping list
     */
    @Override
    public void createNewStore(String storeName) {
        try {
            Store.newBuilder()
                    .setName(storeName)
                    .setIngredientStore(ingredientStoreList)
                    .build()
                    .insertIntoDatabase();
            goBackToStore();
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't insert store into database", e);
            AlertUtils.alertWarningMessage("Le magasin n'a pas pu être ajouté");
        }
    }

    /**
     * Allows you to configure a shooping list
     * If a shopping list with the same name exists in the database, it is overwritten. Otherwise, a new shopping list is created.
     * @param storeName which is the name of the shopping list
     */
    @Override
    public void configureStore(String storeName) {
        if (store != null) {
            deleteStore(store);
        }
        createNewStore(storeName);
    }

    /**
     * Allows you to delete an existing shopping list and displays a confirmation window.
     * @param store indicates the shopping list to delete
     */
    private void deleteStore(Store store) {
        try {
            store.removeFromDatabase();
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't delete store from database", e);
            AlertUtils.alertWarningMessage("Le magasin n'a pas pu être supprimé");
        }

    }

    @Override
    public boolean doesStoreExist(String storeName) throws SQLException {
        try {
            return Store.newBuilder()
                    .setName(storeName)
                    .build()
                    .getModel()
                    .isPresent();
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't verify if store is into database", e);
            throw new SQLException("Failed to verify if store exists");
        }
    }

    @Override
    public void goBackToStore() {
        goTo(StoreListController::new);
    }

}
