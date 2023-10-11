package ulb.infof307.g02.model.sql_super_class;

import ulb.infof307.g02.gui.controller.main.Autochef;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The builder class is used to generalize all SQLModel builders and force them to define the two methods
 * {@link #makeObjectFromResultSet()} and {@link #makeObjectFromBuilder()}
 *
 * @param <T> is the Type of the builder himself, allow SQLBuilder to return itself
 * @param <R> is the Type of the object that the builder need to build
 */
public abstract class SQLBuilder<T extends SQLBuilder<T, R>, R extends SQLModel<?>> {

	protected ResultSet resultSet;

	/**
	 * Pseudo constructor, do nothing, then provide the builder
	 *
	 * @return the builder
	 */
	@SuppressWarnings("unchecked")
	protected T thisObject() {
		return (T) this;
	}

	/**
	 * Pseudo constructor, take a ResultSet and set it as input data, then provide the builder
	 *
	 * @param resultSet is the object containing the data that need to be mapped in the object to build
	 * @return the builder
	 */
	public SQLBuilder<T, R> fromResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
		return thisObject();
	}

	/**
	 * Use a ResultSet to map the data it contains into the model that the Builder is required to build
	 *
	 * @return the model that the builder is required to build
	 * @throws SQLException if the retrieve of information via the ResultSet failed
	 */
	protected abstract R makeObjectFromResultSet() throws SQLException;

	/**
	 * Use the Builder himself to map the data it contains into the model that the Builder is required to build
	 *
	 * @return the model that the builder is required to build
	 */
	protected abstract R makeObjectFromBuilder();

	/**
	 * Use the Builder, whatever it's configuration, to build the object it is required to build
	 *
	 * @return the model that the builder is required to build
	 */
	public R build() {
		if(resultSet != null) {
			try {
				return makeObjectFromResultSet();
			} catch (SQLException e) {
				Autochef.getLogger().error("ResultSet Object could not be converted to a SQLModel Object", e);
				return null;
			}
		} else {
			return makeObjectFromBuilder();
		}
	}

}
