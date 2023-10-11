package ulb.infof307.g02.gui.controller.shoppinglist;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.stage.FileChooser;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.controller.Controller;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.view.shoppinglist.ShoppingListListViewerController;
import ulb.infof307.g02.gui.view.model_viewer.ShoppingListViewerController;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.ShoppingList;
import ulb.infof307.g02.model.sql_model.user_model.User;
import ulb.infof307.g02.util.AlertUtils;
import ulb.infof307.g02.util.import_export.EmailUtils;
import ulb.infof307.g02.util.import_export.PDFUtils;

import javax.mail.MessagingException;
import javax.mail.Session;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ShoppingListListController extends Controller
        implements ShoppingListListViewerController.Listener, ShoppingListViewerController.Listener {

    private final ShoppingListListViewerController shoppingListViewerController;

    public ShoppingListListController() {
        super(FXMLPath.LIST_OF_SHOPPING_LIST);

        this.shoppingListViewerController = fxmlLoader.getController();
        shoppingListViewerController.setListener(this);
    }

    /**
     * Loads all the shopping lists from the database and displays them in the associated view.
     */
    @Override
    public void loadShoppingListFromDatabase() {
        try {
            List<ShoppingList> list = ShoppingList.newBuilder()
                    .setUserID(User.getInstance().getId())
                    .build()
                    .getModels();
            ObservableList<ShoppingList> details = FXCollections.observableArrayList(list);
            FilteredList<ShoppingList> filteredList = new FilteredList<>(details, p -> true);
            shoppingListViewerController.setList(filteredList);
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't load recipes from database", e);
            AlertUtils.alertWarningMessage("Les recettes n'ont pas pu être chargé");
        }
    }

    /**
     * Allows you to create a new shopping list.
     */
    @Override
    public void createNewShoppingList() {
        goTo(() -> new ShoppingListConfigureController(this));
    }

    /**
     * Allows you to view an existing shopping list (name and ingredients).
     * @param shoppingList indicates the shopping list to visualize
     */
    @Override
    public void inspectShoppingList(ShoppingList shoppingList) {
        goTo(model -> new ShoppingListInfoController(this, model), shoppingList);
    }

    /**
     * Allows you to view an existing shopping list (name and ingredients).
     * @param shoppingList indicates the shopping list to visualize
     */
    @Override
    public void configureShoppingList(ShoppingList shoppingList) {
        goTo(model -> new ShoppingListConfigureController(model, this), shoppingList);
    }

    /**
     * Allows you to delete an existing shopping list and displays a confirmation window.
     * @param shoppingList indicates the shopping list to delete
     */
    @Override
    public void deleteShoppingList(ShoppingList shoppingList) {
        if (shoppingListViewerController.showAlert()) {
            try {
                shoppingList.removeFromDatabase();
                shoppingListViewerController.refreshList();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends the provided shoppingList to an email chosen by the user at account creation
     * @param shoppingList shoppingList to be sent
     */
    @Override
    public void emailShoppingList(ShoppingList shoppingList){
        try {
            try {
                File pdf = PDFUtils.createPDF(shoppingList);
                EmailUtils.sendEmail(pdf, shoppingList.getName());
            } catch (SQLException | IOException e) {
                Autochef.getLogger().error("Couldn't create the pdf to send by email", e);
                throw new MessagingException("Creation of PDF failed");
            }
        } catch (MessagingException e) {
            Autochef.getLogger().error("Couldn't send the email", e);
            AlertUtils.alertWarningMessage("L'email n'a pas été envoyé");
        }
    }

    /**
     * Saves the provided shoppingList to a file chosen by the user at runtime
     * @param shoppingList shoppingList to be saved
     */
    @Override
    public void saveShoppingList(ShoppingList shoppingList) {
        try {
            PDFUtils.saveShoppingListAsPDF(shoppingList);
        } catch (SQLException e) {
            Autochef.getLogger().error("", e);
            AlertUtils.alertWarningMessage("La liste de course n'a pas pu être sauvegardée");
        }
    }
}
