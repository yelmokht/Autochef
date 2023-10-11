package ulb.infof307.g02.model.sql_interface;

import java.sql.SQLException;

/**
 * Interface implemented by SQLModels that (virtually) contains other SQLModels.
 *
 * Use Case : Recipes are SQLContainers because they "store" ingredients, when we delete a recipe from the database,
 * Tables such as the one that bind each ingredient to a recipe need to delete the bounds of the recipe we just deleted.
 * (Same goes when inserting a SQLContainer is the database)
 */
public interface SQLContainer {

    /**
     * Insert all contained models of the container calling the method
     *
     * @throws SQLException if the method failed to insert a model
     */
    void insertSubModels() throws SQLException;

    /**
     * Delete all contained models of the container calling the mathod
     *
     * @throws SQLException if the method failed to delete a model
     */
    void deleteSubModels() throws SQLException;

}
