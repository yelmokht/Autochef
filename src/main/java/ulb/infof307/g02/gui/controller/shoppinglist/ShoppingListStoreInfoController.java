package ulb.infof307.g02.gui.controller.shoppinglist;

import ulb.infof307.g02.gui.controller.Controller;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.view.shoppinglist.ShoppingListStoreInfoViewerController;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.IngredientShoppingList;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.ShoppingList;
import ulb.infof307.g02.model.sql_model.store_model.IngredientStore;
import ulb.infof307.g02.util.AlertUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ShoppingListStoreInfoController extends Controller implements ShoppingListStoreInfoViewerController.Listener{

    private final ShoppingListStoreInfoViewerController shoppingListStoreInfoViewerController;
    private final int storeID;
    private int totalPrice;

    private final ShoppingList shoppingList;

    public ShoppingListStoreInfoController(FXMLPath fxmlPath, Controller parentController, ShoppingList shoppingList, int storeID) {
        super(fxmlPath, parentController);

        shoppingListStoreInfoViewerController = fxmlLoader.getController();
        shoppingListStoreInfoViewerController.setListener(this);

        this.storeID = storeID;
        this.shoppingList = shoppingList;
        setStoreName(shoppingList.getName());
        addIngredients(shoppingList.getIngredients());
    }

    @Override
    public void setStoreName(String storeName) {
        shoppingListStoreInfoViewerController.setStoreName(storeName);
    }

    @Override
    public void setTotalPrice() {
        shoppingListStoreInfoViewerController.setTotalPrice(totalPrice);
    }

    @Override
    public void addIngredients(List<IngredientShoppingList> ingredients) {
        try {
            for (var ingredient: ingredients) {
                IngredientStore ingredientStore = IngredientStore.newBuilder()
                        .setIdStore(storeID)
                        .setIdIngredient(ingredient.getIngredientID())
                        .build();

                Optional<IngredientStore> ingredientStoreModel = ingredientStore.getModel();

                if (ingredientStoreModel.isPresent()) {
                    addPresentIngredient(ingredientStoreModel.get(), ingredient);
                } else {
                    addMissingIngredient(ingredient);
                }
            }
            setTotalPrice();
        } catch (SQLException e) {
            Autochef.getLogger().error("Could not fetch an ingredient from database", e);
            AlertUtils.alertWarningMessage("L'ingrédient n'a pas pus être ajouté");
        }

    }

    private void addMissingIngredient(IngredientShoppingList ingredientShoppingList) {
        String formattedIngredient = ingredientShoppingList.getQuantity() + ingredientShoppingList.getUnit().getName() + " " + ingredientShoppingList.getName();
        shoppingListStoreInfoViewerController.addMissingIngredient(formattedIngredient);
    }

    private void addPresentIngredient(IngredientStore ingredientStore, IngredientShoppingList ingredientShoppingList) {
        double ingredientPrice = ingredientStore.getQuantity();
        double ingredientQuantity = ingredientShoppingList.getQuantity();
        String formattedIngredient =  ingredientQuantity + ingredientShoppingList.getUnit().getName() + " " + ingredientStore.getName() + " : " + ingredientPrice + "€";
        shoppingListStoreInfoViewerController.addPresentIngredient(formattedIngredient);
        totalPrice += ingredientPrice*ingredientQuantity;
    }

    @Override
    public void goBack() {
        goTo(model -> new ShoppingListInfoController(this, model), shoppingList);
    }

}
