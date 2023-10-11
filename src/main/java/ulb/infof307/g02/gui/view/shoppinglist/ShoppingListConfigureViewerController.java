package ulb.infof307.g02.gui.view.shoppinglist;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.view.ViewerController;
import ulb.infof307.g02.gui.view.model_viewer.IngredientViewerController;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.IngredientShoppingList;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.ShoppingList;
import ulb.infof307.g02.util.attributes.Department;
import ulb.infof307.g02.util.AlertUtils;
import ulb.infof307.g02.util.attributes.Unit;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static ulb.infof307.g02.util.AlertUtils.*;
import static ulb.infof307.g02.util.FieldUtils.checkField;
import static ulb.infof307.g02.util.FieldUtils.isTextCorrect;

public class ShoppingListConfigureViewerController extends ViewerController<ShoppingListConfigureViewerController.Listener>
        implements Initializable {

    @FXML
    private Label shoppinglistNameWarning;

    @FXML
    private TextField shoppinglistName, search, ingredientName;

    @FXML
    private Button saveButton;

    @FXML
    private VBox vbox;

    @FXML
    private TableView<Ingredient> tableView;

    @FXML
    private TableColumn<Ingredient, String> ingredientNameColumn, ingredientTypeColumn;

    @FXML
    private ComboBox<String> ingredientType;

    @FXML
    private ComboBox<String> ingredientUnit;

    private final List<HBox> ingredientHBoxList = new ArrayList<>();

    private ShoppingList shoppingList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        shoppinglistName.textProperty().addListener((observable, oldValue, newValue) -> isFieldCorrect(shoppinglistName, "shoppinglist", shoppinglistNameWarning, newValue));
        ingredientNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ingredientTypeColumn.setCellValueFactory(new PropertyValueFactory<>("typeName"));
        setUpIngredientType();
        setUpIngredientUnit();
    }

    public void loadDatabase() {
        listener.loadIngredientFromDatabase();
    }

    private void isFieldCorrect(Node node, String type, Label warning, String text) {
        checkField(node, type, warning, text);
        saveButton.setDisable(!isShoppingListNameCorrect());
    }

    private boolean isShoppingListNameCorrect() {
        return isTextCorrect("shoppinglist", shoppinglistName.getText());
    }

    private boolean isAllIngredientFieldCorrect() {
        return  isNameFieldCorrect() && isTypeFieldCorrect() && isUnitFieldCorrect();
    }

    private boolean isNameFieldCorrect() {
        return isTextCorrect("name", ingredientName.getText());
    }

    private boolean isTypeFieldCorrect() {
        return isTextCorrect("type", ingredientType.getValue());
    }

    private boolean isUnitFieldCorrect() {
        return isTextCorrect("unit", ingredientUnit.getValue());
    }

    private void setShoppingListName(String shoppingListName) {
        this.shoppinglistName.setText(shoppingListName);
    }

    /**
     * Filter the ingredients table during a search.
     * @param filteredList the list of ingredients retrieved from the database
     */
    private void setUpSearchField(FilteredList<Ingredient> filteredList) {search.textProperty().addListener((obs, oldValue, newValue) -> filteredList.setPredicate(ingredient -> ingredient.getName().toLowerCase().contains(newValue.toLowerCase().trim())));}

    private void setUpIngredientType() {
        ingredientType.getItems().clear();
        for (final var type : Department.values()) {
            ingredientType.getItems().add(type.getDepartment());
        }
    }

    private void setUpIngredientUnit() {
        ingredientUnit.getItems().clear();
        for (final var unit : Unit.values()) {
            if (unit.isBaseUnit()) {
                ingredientUnit.getItems().add(unit.getName());
            }
        }
    }

    private boolean isIngredientInShoppingList(Ingredient ingredient) {
        if (vbox.getChildren().contains(vbox.lookup("#" + ingredient.getName()))) {
            alertWarningMessage(selectAlertMessage("list"));
        }
        return vbox.getChildren().contains(vbox.lookup("#" + ingredient.getName()));
    }

    private boolean isIngredientInTable(String ingredientName) {
        if (tableView.getItems().stream().map(Ingredient::getName).collect(Collectors.toList()).contains(ingredientName)) {
            alertWarningMessage(selectAlertMessage("ingredient"));
        }
        return tableView.getItems().stream().map(Ingredient::getName).collect(Collectors.toList()).contains(ingredientName);
    }

    private void addIngredientFromTable(Ingredient ingredient) {
        if (ingredient != null && !isIngredientInShoppingList(ingredient)) {
            listener.addIngredientToShoppingList(IngredientShoppingList.newBuilder().setName(ingredient.getName()).setQuantity(1).setUnit(ingredient.getUnit()).build());
        }
    }

    private void removeIngredientFromTable(Ingredient ingredient) {
        if (!tableView.getItems().isEmpty()) {
            vbox.getChildren().remove(vbox.lookup("#" + ingredient.getName()));
            listener.removeIngredientFromDatabase(ingredient);
        }
    }

    /**
     * Adds an HBox (the Ingredient viewer) of an ingredient in the list of ingredients.
     * @param ingredient : the Recipe associated to the HBox
     */
    public void addIngredientHBoxToShopping(IngredientShoppingList ingredient) {
        FXMLLoader loader = loadIngredientViewer();
        setIngredientViewerListener(loader, ingredient);
        addIngredientViewerToShoppingList(loader, ingredient);
    }

    /**
     * Associate the controller of this view controller as the controller of the FXML of the ingredient viewer
     * (Ingredient Viewer is used at different locations, buttons need to be set differently each time)
     * @param loader : the FXMLLoader of the Recipe Viewer
     * @param ingredient : the recipe to show on the schedule
     */
    private void setIngredientViewerListener(FXMLLoader loader, IngredientShoppingList ingredient) {
        IngredientViewerController<Integer> controller = loader.getController();
        controller.setListener((IngredientViewerController.Listener) listener);
        controller.setIngredient(ingredient);
    }

    /**
     * Load the FXML of the Ingredient viewer
     */
    private FXMLLoader loadIngredientViewer() {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(FXMLPath.INGREDIENT_VIEWER.getPath())));
        try {
            loader.load();
        } catch (IOException e) {
            Autochef.getLogger().error("Couldn't load the Ingredient Viewer", e);
            AlertUtils.alertWarningMessage("Un ingrédient n'a pas pu être affiché");
        }
        return loader;
    }

    /**
     * Add the Ingredient Viewer to the list of ingredients of the Recipe
     * @param loader : the FXMLLoader of the Ingredient Viewer
     * @param ingredient : the ingredient to show on the list of ingredients
     */
    private void addIngredientViewerToShoppingList(FXMLLoader loader, IngredientShoppingList ingredient) {
        HBox ingredientHBox = loader.getRoot();
        ingredientHBox.setId(ingredient.getName().replace(" ", "_"));
        ingredientHBoxList.add(ingredientHBox);
        vbox.getChildren().add(ingredientHBox);
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
        setShoppingListName(shoppingList.getName());
        this.listener.loadIngredientFromShoppingList();
    }

    public void removeIngredientHBoxFromShoppingList(IngredientShoppingList ingredientShopping) {
        vbox.getChildren().remove(vbox.lookup("#" + ingredientShopping.getName().replace(" ", "_")));
    }

    public void setUpTableView(FilteredList<Ingredient> filteredList) {
        tableView.setItems(filteredList);
        setUpSearchField(filteredList);
        tableView.refresh();
    }

    @FXML
    void ingredientOnClicked(MouseEvent event) {
        Ingredient ingredient = tableView.getSelectionModel().getSelectedItem();
        if (event.getButton() == MouseButton.PRIMARY) {
            addIngredientFromTable(ingredient);
        }
        if (event.getButton() == MouseButton.SECONDARY && alertConfirmationMessage(selectAlertDeleteMessage("ingredient"))) {
            removeIngredientFromTable(ingredient);
        }
    }

    @FXML void addIngredientButtonOnClicked() {
        if (!isIngredientInTable(ingredientName.getText()) && isAllIngredientFieldCorrect()) {
            listener.addIngredientToDatabase(ingredientName.getText(), ingredientType.getValue(), ingredientUnit.getValue());
        }
    }

    @FXML
    void saveButtonOnClicked() {
        try {
            if ((listener.isModifying() && Objects.equals(shoppingList.getName(), shoppinglistName.getText())) || !listener.doesShoppingListExist(shoppinglistName.getText())) {
                listener.configureShoppingList(shoppinglistName.getText());
            }
            else {
                alertWarningMessage(selectAlertMessage("shoppingListName"));
            }
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't verify existence of Recipe model");
            AlertUtils.alertWarningMessage("La recette n'a pas pu être sauvegardée");
        }
    }

    @FXML
    void backButtonOnClicked() {
        listener.goBackToShoppingList();
    }

    public interface Listener extends ViewerController.ViewerListener {

        void loadIngredientFromDatabase();

        void addIngredientToDatabase(String ingredientName, String ingredientType, String ingredientUnit);

        void removeIngredientFromDatabase(Ingredient ingredient);

        void loadIngredientFromShoppingList();

        void addIngredientToShoppingList(IngredientShoppingList ingredientShopping);

        void createNewShoppingList(String shoppingName);

        void configureShoppingList(String shoppingName);

        void goBackToShoppingList();

        boolean doesShoppingListExist(String text) throws SQLException;

        boolean isModifying();

    }

}
