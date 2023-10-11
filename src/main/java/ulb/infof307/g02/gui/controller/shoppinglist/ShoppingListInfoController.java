package ulb.infof307.g02.gui.controller.shoppinglist;

import ulb.infof307.g02.gui.controller.Controller;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.view.shoppinglist.ShoppingListInfoViewerController;
import ulb.infof307.g02.gui.view.model_viewer.ShoppingListStoreViewerController;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.IngredientShoppingList;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.ShoppingList;
import ulb.infof307.g02.model.sql_model.store_model.IngredientStore;
import ulb.infof307.g02.model.sql_model.store_model.Store;
import ulb.infof307.g02.util.AlertUtils;

import java.sql.SQLException;
import java.util.List;

public class ShoppingListInfoController extends Controller
        implements ShoppingListInfoViewerController.Listener, ShoppingListStoreViewerController.Listener{

    private final ShoppingListInfoViewerController shoppingListInfoViewerController;

    private final ShoppingList shoppingList;

    public ShoppingListInfoController(Controller listener, ShoppingList shoppingList) {
        super(FXMLPath.INFO_SHOPPING_LIST, listener);

        this.shoppingListInfoViewerController = fxmlLoader.getController();
        shoppingListInfoViewerController.setListener(this);
        this.shoppingList = shoppingList;
        shoppingListInfoViewerController.setShoppingList(shoppingList);
        this.setStoresPrice();
    }

    /**
     * Load all the ingredients of a shopping list from the database.
     * @param shoppingList which is the shopping list from which we want to get the ingredients
     */
    @Override
    public void loadIngredientsListFromDatabase(ShoppingList shoppingList) {
        shoppingListInfoViewerController.setIngredientsList(shoppingList.getIngredients());
    }

    public void setStoresPrice() {
        double totalPrice = 0;
        int nbPresentIngredient = 0;

        try {
            List<Store> allStore = Store.newBuilder()
                    .build()
                    .getModels();

            for (Store store : allStore){
                for ( IngredientShoppingList ingredient : shoppingList.getIngredients()){
                    IngredientStore ingredientStore = IngredientStore.newBuilder()
                            .setIdStore(store.getId())
                            .setIdIngredient(ingredient.getIngredientID())
                            .build();
                    var ingredientStoreModel = ingredientStore.getModel();
                    if (ingredientStoreModel.isPresent()){
                        totalPrice += ingredientStoreModel.get().getQuantity() * ingredient.getQuantity();
                        nbPresentIngredient++;
                    }

                }
                int nbMissingIngredients = shoppingList.getIngredients().size() - nbPresentIngredient;
                String labelText = store.getName() + " : " + totalPrice + "€ " + "Ingredients manquants: " + nbMissingIngredients + "/" + shoppingList.getIngredients().size();
                shoppingListInfoViewerController.addStoreHBoxToList(store.getId(), labelText);
                totalPrice =0 ;
                nbPresentIngredient = 0;
            }
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't set store prices", e);
            AlertUtils.alertWarningMessage("Les prix associés à cette liste n'ont pas pu être chargés");
            }
    }

    @Override
    public void goBackToShoppingList() {
        goTo(ShoppingListListController::new);
    }

    @Override
    public void showPriceInfo(int storeID) {
        new ShoppingListStoreInfoController(FXMLPath.STORE_PRICE_INFO_VIEWER, this, shoppingList, storeID);
    }
}
