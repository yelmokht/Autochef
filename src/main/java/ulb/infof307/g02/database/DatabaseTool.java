package ulb.infof307.g02.database;

import javafx.util.Pair;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.model.sql_interface.SQLBaseModel;
import ulb.infof307.g02.model.sql_interface.SQLContainer;
import ulb.infof307.g02.model.sql_super_class.SQLModel;

import java.sql.SQLException;
import java.util.List;

/**
 * DatabaseTool is used for indirect queries to the SQLite Database, forming the top layer of the database
 * implementation. This is the only database-related class that will be instanced in the non-database-related code
 * <p>
 * A DatabaseTool is like a toolbox, DatabaseTool&lt;T&gt; refers to the tool we are going to use.
 * Example : Toolbox&lt;Screw&gt; refers to a screwdriver
 *
 * @param <T> explains to the database which tool it needs to use from the toolbox to manage object of type &lt;T&gt;
 */
public class DatabaseTool<T extends SQLModel<T>> {

	// Associate DatabaseTool object to a DatabaseQuery object to make low level queries
	private final DatabaseQuery<T> databaseQuery;

	/**
	 * Associate the constructed DatabaseTool and DatabaseQuery to a {@link DatabaseTable}, which informs the
	 * DatabaseTool object of all classical information about related table in the database
	 *
	 * @param modelClass : the class of the SQLModel we want to manage
	 */
	public DatabaseTool(Class<T> modelClass) {
		this.databaseQuery = new DatabaseQuery<>(DatabaseTable.getTableFromClass(modelClass));
	}

	/**
	 * Insert a SQLModel in the database
	 *
	 * @param model is the SQLModel to insert
	 */
	public void insert(T model) throws SQLException {
		try {
			databaseQuery.insertToDatabase(model);
			// Some objects contain a list of other object, we need to insert them as well
			if (model instanceof SQLContainer) {
				((SQLContainer) model).insertSubModels();
			}
		} catch (SQLException e) {
			Autochef.getLogger().error("Couldn't insert the SQLModel into the database : " + model.getClass(), e);
			throw new SQLException("Couldn't insert into database");
		}
	}

	/**
	 * Delete a SQLModel from the database
	 *
	 * @param model is the SQLModel to delete
	 */
	public void delete(T model) throws SQLException {
		// If SQLBaseModel : delete other instance in other tables of the database
		if (model instanceof SQLBaseModel) {
			((SQLBaseModel) model).deleteOtherInstanceOfSelf();
		}
		// If SQLContainer : delete relation with contained models from the database
		if (model instanceof SQLContainer) {
			((SQLContainer) model).deleteSubModels();
		}
		try {
			databaseQuery.deleteFromDatabase(formattedConstraints(model));
		} catch (SQLException e) {
			Autochef.getLogger().error("Couldn't delete the SQLModel from the database : " + model.getClass(), e);
			throw new SQLException("Couldn't delete from database");
		}
	}

	public List<T> select(T model) throws SQLException {
		try {
			return databaseQuery.selectInDatabase(formattedConstraints(model));
		} catch (SQLException e) {
			Autochef.getLogger().error("Couldn't select the SQLModel from the database : " + model.getClass(), e);
			throw new SQLException("Couldn't select from database");
		}
	}

	private String formattedConstraints(T model) {
		final var constraintsBuilder = new StringBuilder();

		final Pair<List<String>, List<String>> valuesSet = model.toSQLValuesSet();
		final List<String> columns = valuesSet.getKey();

		for (int i = 0; i < columns.size(); i++) {
			constraintsBuilder.append(String.format("%s = '%s' AND ", columns.get(i), valuesSet.getValue().get(i)));
		}

		if (!constraintsBuilder.isEmpty()) {
			constraintsBuilder.delete(constraintsBuilder.length() - 5, constraintsBuilder.length() - 1);
		}

		return constraintsBuilder.toString();
	}
}
