package ulb.infof307.g02.model.sql_interface;

import java.sql.SQLException;

/**
 * Interface implemented by SQLModels that are contained by other SQLModels. Those "SubModels" are composed of a
 * reference to the {@link SQLContainer} they are part of, the reference to the {@link SQLBaseModel} they are an
 * instance of and finally the additional information they contain.
 *
 * Use Case : Suppose that you add an ingredient to a recipe. The BaseModel is the ingredient while the container
 * is the recipe. In this case, the SubModel is defined as "a certain quantity of that ingredient goes in this recipe",
 * we thus save the id of the ingredient (BaseModel), the id of the recipe (container) and the quantity.
 */
public interface SQLSubModel {

    /**
     * Receive the references to two elements and complete the SubModel before inserting it to the database
     * @param baseModelId : the id of the SQLBaseModel associated (IngredientRecipe is an ingredient -> Ingredient id)
     * @param containerModelId : the id of the SQLContainer associated (IngredientRecipe is contained by recipes -> Recipe id)
     * @throws SQLException if the insertion into the database failed
     */
    void completeAndInsert(int baseModelId, int containerModelId) throws SQLException;

}
