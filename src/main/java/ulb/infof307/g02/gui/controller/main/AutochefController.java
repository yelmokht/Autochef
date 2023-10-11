package ulb.infof307.g02.gui.controller.main;

import ulb.infof307.g02.gui.controller.Controller;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.controller.login.ConnectionToAccountController;
import ulb.infof307.g02.gui.controller.recipe.RecipeListController;
import ulb.infof307.g02.gui.controller.schedule.ScheduleInfoController;
import ulb.infof307.g02.gui.controller.shoppinglist.ShoppingListListController;
import ulb.infof307.g02.gui.controller.store.StoreListController;
import ulb.infof307.g02.gui.view.main.AutochefViewerController;
import ulb.infof307.g02.model.sql_model.user_model.User;

public class AutochefController extends Controller implements AutochefViewerController.Listener {

    public AutochefController()  {
        super(FXMLPath.AUTOCHEF_MENU);

        ((AutochefViewerController) fxmlLoader.getController()).setListener(this);
    }

    /**
     * Displays the shopping list.
     */
    @Override
    public void goToShoppingList() {
        goTo(ShoppingListListController::new);
    }

    /**
     * Displays the list of recipes.
     */
    @Override
    public void goToRecipeList() {
        goTo(RecipeListController::new);
    }

    /**
     * Displays the list of menus.
     */
    @Override
    public void goToMenuList() {
        goTo(ScheduleInfoController::new);
    }

    @Override
    public void goToStore() {
        goTo(StoreListController::new);
    }

    @Override
    public void goToConnectionToAccount() {
        User.resetInstance();
        goTo(ConnectionToAccountController::new);
    }

}
