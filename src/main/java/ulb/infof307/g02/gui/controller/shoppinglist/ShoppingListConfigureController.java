package ulb.infof307.g02.gui.controller.shoppinglist;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import ulb.infof307.g02.gui.controller.Controller;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.view.model_viewer.IngredientViewerController;
import ulb.infof307.g02.gui.view.shoppinglist.ShoppingListConfigureViewerController;
import ulb.infof307.g02.model.sql_super_class.SQLModel;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.IngredientShoppingList;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.ShoppingList;
import ulb.infof307.g02.model.sql_model.user_model.User;
import ulb.infof307.g02.util.AlertUtils;
import ulb.infof307.g02.util.attributes.Department;
import ulb.infof307.g02.util.attributes.Unit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListConfigureController extends Controller
        implements ShoppingListConfigureViewerController.Listener, IngredientViewerController.Listener  {

    private final ShoppingListConfigureViewerController shoppingConfigureViewerController;
    private final List<IngredientShoppingList> ingredientShoppingListList = new ArrayList<>();

    private ShoppingList shoppingList;
    private boolean isModifying = false;

    public ShoppingListConfigureController(Controller listener) {
        super(FXMLPath.CONFIGURE_SHOPPING_LIST, listener);

        this.shoppingConfigureViewerController = fxmlLoader.getController();
        shoppingConfigureViewerController.setListener(this);
        shoppingConfigureViewerController.loadDatabase();
    }

    public ShoppingListConfigureController(ShoppingList shoppingList, Controller listener) {
        this(listener);
        this.shoppingList = shoppingList;
        setModifying(true);
        shoppingConfigureViewerController.setShoppingList(shoppingList);
    }

    /**
     * Load all ingredients from the database.
     */
    @Override
    public void loadIngredientFromDatabase() {
        try {
            List<Ingredient> list = Ingredient.newBuilder()
                    .build()
                    .getModels();

            ObservableList<Ingredient> details = FXCollections.observableArrayList(list);
            FilteredList<Ingredient> filteredList = new FilteredList<>(details, p -> true);
            shoppingConfigureViewerController.setUpTableView(filteredList);
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't load ingredient from database", e);
            AlertUtils.alertWarningMessage("Les ingrédients n'ont pas pu être chargé");
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
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't insert ingredient into database", e);
            AlertUtils.alertWarningMessage("L'ingrédient n'ont pas pu être sauvegardés");
        }
        loadIngredientFromDatabase();
    }

    /**
     * Delete an ingredient from the database.
     * @param ingredient is the Ingredient object
     */
    @Override
    public void removeIngredientFromDatabase(Ingredient ingredient) {
        try {
            ingredient.removeFromDatabase();
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't delete ingredient from database", e);
            AlertUtils.alertWarningMessage("L'ingrédient n'a pas pu être supprimé");
        }
        loadIngredientFromDatabase();
    }

    /**
     * Load ingredients from an existing shopping list.
     */
    @Override
    public void loadIngredientFromShoppingList() {
        for (IngredientShoppingList ingredientShoppingList : shoppingList.getIngredients()) {
            ingredientShoppingListList.add(ingredientShoppingList);
            shoppingConfigureViewerController.addIngredientHBoxToShopping(ingredientShoppingList);
        }
    }

    /**
     * Adds an ingredient to a shopping list.
     * @param ingredientShoppingList which is an ingredient object linked to a shopping list
     */
    @Override
    public void addIngredientToShoppingList(IngredientShoppingList ingredientShoppingList) {
        ingredientShoppingListList.add(ingredientShoppingList);
        shoppingConfigureViewerController.addIngredientHBoxToShopping(ingredientShoppingList);
    }

    /**
     * Remove an ingredient from a shopping list.
     * @param ingredient which is an Ingredient object (which implements SQLModel)
     */
    @Override
    public void removeIngredientFromList(SQLModel<?> ingredient) {
        ingredientShoppingListList.remove((IngredientShoppingList) ingredient);
        shoppingConfigureViewerController.removeIngredientHBoxFromShoppingList((IngredientShoppingList) ingredient);
    }

    /**
     * Allows you to create a new shopping list.
     * @param shoppingListName which is the name of the shopping list
     */
    @Override
    public void createNewShoppingList(String shoppingListName) {
        try {
            ShoppingList.newBuilder()
                    .setUserID(User.getInstance().getId())
                    .setName(shoppingListName)
                    .setIngredients(ingredientShoppingListList)
                    .build()
                    .insertIntoDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        goBackToShoppingList();
    }

    /**
     * Allows you to configure a shooping list
     * If a shopping list with the same name exists in the database, it is overwritten. Otherwise, a new shopping list is created.
     * @param shoppingListName which is the name of the shopping list
     */
    @Override
    public void configureShoppingList(String shoppingListName) {
        if (shoppingList != null) {
            deleteShoppingList(shoppingList);
        }
        createNewShoppingList(shoppingListName);
    }

    /**
     * Allows you to delete an existing shopping list and displays a confirmation window.
     * @param shoppingList indicates the shopping list to delete
     */
    private void deleteShoppingList(ShoppingList shoppingList) {
        try {
            shoppingList.removeFromDatabase();
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't delete shopping list from database", e);
            AlertUtils.alertWarningMessage("La liste de course n'a pas pu être supprimée");
        }
    }

    @Override
    public boolean doesShoppingListExist(String shoppingListName) throws SQLException {
        try {
            return ShoppingList.newBuilder().setName(shoppingListName).build().doesModelExists();
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't verify if shopping list is into database", e);
            throw new SQLException("Failed to verify if shopping list exists");
        }
    }

    @Override
    public void goBackToShoppingList() {
        goTo(ShoppingListListController::new);
    }

    @Override
    public boolean isModifying() {
        return isModifying;
    }

    public void setModifying(boolean modifying) {
        isModifying = modifying;
    }
}
