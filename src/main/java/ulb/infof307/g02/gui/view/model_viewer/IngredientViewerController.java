package ulb.infof307.g02.gui.view.model_viewer;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import ulb.infof307.g02.gui.view.ViewerController;
import ulb.infof307.g02.model.model_interface.IngredientInstance;
import ulb.infof307.g02.model.sql_super_class.SQLModel;

import java.util.Objects;

public class IngredientViewerController<T> extends ViewerController<IngredientViewerController.Listener> {

    private static final int MAX_VALUE = 100000;

    @FXML
    private HBox hbox;

    @FXML
    private Label ingredientName;

    @FXML
    private Spinner<T> quantitySpinner;

    @FXML
    private ComboBox<String> unitType;

    private SQLModel<?> ingredient;

    private void setupUnit(IngredientInstance ingredient) {
        if (Objects.equals(ingredient.getUnit().getName(), "g" )) {
            unitType.getItems().add("kg");
            unitType.getItems().add("g");
            unitType.getItems().add("mg");
            unitType.setValue("g");
        } else if (Objects.equals(ingredient.getUnit().getName(), "L")) {
                unitType.getItems().add("L");
                unitType.getItems().add("mL");
                unitType.setValue("L");
        } else {
            unitType.getItems().add(ingredient.getUnit().getName());
        }
    }


    private void setIngredientName(SQLModel<?> ingredient) {
        ingredientName.setText(ingredient.getName());
    }

    @SuppressWarnings("unchecked")
    public void setQuantitySpinner(IngredientInstance ingredient) {
        quantitySpinner.setValueFactory((SpinnerValueFactory<T>) new SpinnerValueFactory.DoubleSpinnerValueFactory(0, MAX_VALUE, ingredient.getQuantity()));
        quantitySpinner.valueProperty().addListener(
                (observable, oldValue, newValue) -> ingredient.setQuantity((Double) newValue, unitType.getValue())
        );
    }

    public void setIngredient(IngredientInstance ingredient) {
        this.ingredient = (SQLModel<?>) ingredient;

        setIngredientName(this.ingredient);
        setQuantitySpinner(ingredient);
        setupUnit(ingredient);
    }

    public void setUnitType(String unit){
        unitType.setValue(unit);
        unitType.setMouseTransparent(true);
        unitType.setFocusTraversable(true);
    }

    public SQLModel<?> getIngredient() {return this.ingredient;}

    @FXML
    protected void removeButtonOnClick() {
        listener.removeIngredientFromList(ingredient);
    }

    public interface Listener extends ViewerListener {

        void removeIngredientFromList(SQLModel<?> ingredient);

    }

}