package ulb.infof307.g02.gui.view.recipe;

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
import org.apache.commons.lang3.StringUtils;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.view.ViewerController;
import ulb.infof307.g02.gui.view.model_viewer.IngredientViewerController;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_model.recipe_model.IngredientRecipe;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.model.sql_model.user_model.User;
import ulb.infof307.g02.util.attributes.Department;
import ulb.infof307.g02.util.attributes.Diet;
import ulb.infof307.g02.util.attributes.Meal;
import ulb.infof307.g02.util.AlertUtils;
import ulb.infof307.g02.util.attributes.Unit;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static ulb.infof307.g02.util.AlertUtils.*;
import static ulb.infof307.g02.util.FieldUtils.checkField;
import static ulb.infof307.g02.util.FieldUtils.isTextCorrect;

public class RecipeConfigureViewerController extends ViewerController<RecipeConfigureViewerController.Listener>
        implements Initializable {

    @FXML
    private TextField recipeName, servings, search, ingredientName;
    @FXML
    private ChoiceBox<String> mealType;
    @FXML
    private ChoiceBox<String> dietType;
    @FXML
    private TextArea instructions;
    @FXML
    private VBox vbox;
    @FXML
    private Label recipeNameWarning, servingsWarning, mealTypeWarning, dietTypeWarning, instructionsWarning;
    @FXML
    private Button saveButton;
    @FXML
    private TableView<Ingredient> tableView;
    @FXML
    private TableColumn<Ingredient, String> ingredientNameColumn, ingredientTypeColumn;
    @FXML
    private ComboBox<String> ingredientType;
    @FXML
    private ComboBox<String> ingredientUnit;

    private final List<HBox> ingredientHBoxList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        recipeName.textProperty().addListener((observable, oldValue, newValue) -> isFieldCorrect(recipeName, "recipe", recipeNameWarning, newValue));
        servings.textProperty().addListener((observable, oldValue, newValue) -> isFieldCorrect(servings, "servings", servingsWarning, newValue));
        mealType.valueProperty().addListener((observable, oldValue, newValue) -> isFieldCorrect(mealType, "meal", mealTypeWarning, String.valueOf(newValue)));
        dietType.valueProperty().addListener((observable, oldValue, newValue) -> isFieldCorrect(dietType, "diet", dietTypeWarning, String.valueOf(newValue)));
        instructions.textProperty().addListener((observable, oldValue, newValue) -> isFieldCorrect(instructions, "instructions", instructionsWarning, newValue));
        ingredientNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ingredientTypeColumn.setCellValueFactory(new PropertyValueFactory<>("typeName"));
        setupDiet();
        setupMeal();
        setUpIngredientType();
        setUpIngredientUnit();
    }

    /**
     * Checks that the text field is correctly filled according to the type of element.
     * @param node a TextField
     * @param type type of field
     * @param warning a label that displays an error message in case of error
     * @param text a text retrieved from the field
     */
    private void isFieldCorrect(Node node, String type, Label warning, String text) {
        checkField(node, type, warning, text);
        saveButton.setDisable(!isAllFieldsCorrect());
    }

    public boolean isAllFieldsCorrect() {
        return isRecipeNameCorrect() && isServingsFieldCorrect() && isMealFieldCorrect() && isDietFieldCorrect() && isInstructionFieldCorrect();
    }

    private boolean isRecipeNameCorrect() {
        return isTextCorrect("recipe", recipeName.getText());
    }

    private boolean isUnitFieldCorrect() {
        return isTextCorrect("unit", String.valueOf(ingredientUnit.getValue()));
    }

    private boolean isServingsFieldCorrect() {
        return isTextCorrect("servings", servings.getText());
    }

    private boolean isMealFieldCorrect() {
        return isTextCorrect("meal", String.valueOf(mealType.getValue()));
    }

    private boolean isDietFieldCorrect() {
        return isTextCorrect("diet", String.valueOf(dietType.getValue()));
    }

    private boolean isInstructionFieldCorrect() {
        return isTextCorrect("instructions", instructions.getText());
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

    private void setRecipeName(String recipeName) {
        this.recipeName.setText(recipeName);
    }

    private void setServings(String servings) {
        this.servings.setText(String.valueOf(servings));
    }

    private void setMealType(Meal mealType) {
        this.mealType.setValue(mealType.getMeal());
    }

    private void setDietType(Diet dietType) {
        this.dietType.setValue(dietType.getDiet());
    }

    private void setInstructions(String instructions) {
        this.instructions.setText(instructions);
    }

    private void setupMeal() {
        mealType.getItems().clear();
        for (final var meal : Meal.values()) {
            mealType.getItems().add(meal.getMeal());
        }
    }

    private void setupDiet() {
        dietType.getItems().clear();
        for (final var diet : Diet.values()) {
            dietType.getItems().add(diet.getDiet());
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

    /**
     * Filter the ingredients table during a search
     * @param filteredList the list of ingredients retrieved from the database
     */
    private void setupSearchField(FilteredList<Ingredient> filteredList) {
        search.textProperty().addListener((obs, oldValue, newValue)
                -> filteredList.setPredicate(ingredient
                -> ingredient.getName().toLowerCase().contains(newValue.toLowerCase().trim())));
    }

    private void setUpIngredientType() {
        ingredientType.getItems().clear();
        for (final var type : Department.values()) {
            ingredientType.getItems().add(type.getDepartment());
        }
    }

    private boolean isIngredientInRecipe(Ingredient ingredient) {
        if (vbox.getChildren().contains(vbox.lookup("#" + ingredient.getName().replace(" ", "_")))) {
            alertWarningMessage(selectAlertMessage("list"));
        }
        return vbox.getChildren().contains(vbox.lookup("#" + ingredient.getName().replace(" ", "_")));
    }

    private boolean isIngredientInTable(String ingredientName) {
        if (tableView.getItems().stream().map(Ingredient::getName).collect(Collectors.toList()).contains(ingredientName)) {
            alertConfirmationMessage(selectAlertMessage("ingredient"));
        }
        return tableView.getItems().stream().map(Ingredient::getName).collect(Collectors.toList()).contains(ingredientName);
    }

    private void addIngredientFromTable(Ingredient ingredient) {
        if (ingredient != null && !isIngredientInRecipe(ingredient)) {
            listener.addIngredientToRecipe(IngredientRecipe.newBuilder()
                    .setName(ingredient.getName())
                    .setQuantity(1)
                    .setUnit(ingredient.getUnit())
                    .build());
        }
    }

    private void removeIngredientFromTable(Ingredient ingredient) {
        if (!tableView.getItems().isEmpty()) {
            vbox.getChildren().remove(vbox.lookup("#" + ingredient.getName().replace(" ", "_")));
            listener.removeIngredientFromDatabase(ingredient);
        }
    }

    /**
     * Adds an HBox (the Ingredient viewer) of an ingredient in the list of ingredients.
     * @param ingredient : the Recipe associated to the HBox
     */
    public void addIngredientHBoxToRecipe(IngredientRecipe ingredient) {
        FXMLLoader loader = loadIngredientViewer();
        setIngredientViewerListener(loader, ingredient);
        addIngredientViewerToRecipe(loader, ingredient);
    }

    /**
     * Associate the controller of this view controller as the controller of the FXML of the ingredient viewer
     * (Ingredient Viewer is used at different locations, buttons need to be set differently each time)
     * @param loader : the FXMLLoader of the Recipe Viewer
     * @param ingredient : the ingredient to show in the ingredient list
     */
    private void setIngredientViewerListener(FXMLLoader loader, IngredientRecipe ingredient) {
        IngredientViewerController<Double> controller = loader.getController();
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
    private void addIngredientViewerToRecipe(FXMLLoader loader, IngredientRecipe ingredient) {
        HBox ingredientHBox = loader.getRoot();
        ingredientHBox.setId(ingredient.getName().replace(" ", "_"));
        ingredientHBoxList.add(ingredientHBox);
        vbox.getChildren().add(ingredientHBox);
    }


    public void removeIngredientHBoxFromRecipe(IngredientRecipe ingredientRecipe) {
        vbox.getChildren().remove(vbox.lookup("#" + ingredientRecipe.getName().replace(" ", "_")));
    }

    public void setRecipe(Recipe recipe) {
        setRecipeName(recipe.getName());
        setServings(String.valueOf(recipe.getServings()));
        setMealType(recipe.getType());
        setDietType(recipe.getDiet());
        setInstructions(StringUtils.join(recipe.getInstructions(), "\n"));
    }

    public void setupTableView(FilteredList<Ingredient> filteredList) {
        Autochef.getLogger().info(filteredList);
        tableView.setItems(filteredList);
        setupSearchField(filteredList);
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

    @FXML
    void addIngredientButtonOnClicked() {
        if (!isIngredientInTable(ingredientName.getText()) && isAllIngredientFieldCorrect()) {
            listener.addIngredientToDatabase(ingredientName.getText(), ingredientType.getValue(), ingredientUnit.getValue());
        }
    }

    @FXML
    protected void saveButtonOnClicked() {
        // Saving all fields to pass them to the controller (object used as DTO)
        Recipe recipeToSave = Recipe.newBuilder()
                .setUserID(User.getInstance().getId())
                .setName(recipeName.getText())
                .setServings(Integer.parseInt(servings.getText()))
                .setDiet(Diet.fromString(dietType.getValue()))
                .setType(Meal.fromString(mealType.getValue()))
                .setInstructions(Arrays.asList(instructions.getText().split("\n")))
                .build();

        listener.saveRecipe(recipeToSave);
    }

    @FXML
    protected void backButtonOnClicked() {
        listener.goBack();
    }

    public interface Listener extends ViewerController.ViewerListener {

        void loadIngredientFromDatabase();

        void addIngredientToDatabase(String ingredientName, String ingredientType, String ingredientUnit);

        void removeIngredientFromDatabase(Ingredient ingredient);

        void loadIngredientFromRecipe();

        void addIngredientToRecipe(IngredientRecipe ingredientRecipe);

        void createNewRecipe(Recipe recipe);

        void saveRecipe(Recipe recipe);

        void goBack();

    }

}
