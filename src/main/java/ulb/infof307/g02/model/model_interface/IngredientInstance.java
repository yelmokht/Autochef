package ulb.infof307.g02.model.model_interface;

import ulb.infof307.g02.util.attributes.Unit;

/**
 * Interface implemented by all instances of an ingredient, ensure that all the models that are IngredientInstances
 * contain the method necessary to manage quantities and units of the ingredient.
 */
public interface IngredientInstance {

    Unit getUnit();

    void setQuantity(double quantity, String unit) ;

    double getQuantity();
}
