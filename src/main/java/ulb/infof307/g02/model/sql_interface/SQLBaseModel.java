package ulb.infof307.g02.model.sql_interface;

import java.sql.SQLException;

/**
 * Interface implemented by SQLModels which have other instances that require to be deleted from the database
 * when main instance is deleted from the database
 *
 * Use Case : If an ingredient is deleted from the database, we don't want a recipe using that ingredient to be
 * corrupted. That's why deleteOtherInstanceOfSelf() is used, it suppresses all instances of the ingredient calling
 * the method in all the SQLModels that refers to it
 */
public interface SQLBaseModel {

    /**
     * Delete all rows of the database that refers to the associated model. If it does not exist anymore,
     * we should not point in its direction from somewhere else in the database
     *
     * @throws SQLException if the deletion of other instance of associated model couldn't be deleted
     */
    void deleteOtherInstanceOfSelf() throws SQLException;

}
