package ulb.infof307.g02.model.sql_super_class;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.util.Pair;
import ulb.infof307.g02.database.DatabaseTool;
import ulb.infof307.g02.database.annotation.SQLValue;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.util.attributes.Diet;
import ulb.infof307.g02.util.attributes.Meal;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class SQLModel<T extends SQLModel<T>> extends Observable {

	DatabaseTool<T> databaseTools;

	protected SQLModel(Class<T> typeClass) {
		databaseTools = new DatabaseTool<>(typeClass);
	}


	/**
	 * Used to make SQL queries by forming the Columns string for inserting SQLModels in the database
	 *
	 * @return a string with columns to insert in the database
	 */
	public String toSQLColumnsInsert() {
		return toSQLColumns(false);
	}

	/**
	 * Used to make SQL queries by forming the Columns string for retrieving SQLModels in the database
	 *
	 * @return a string with columns to retrieve from the database
	 */
	public String toSQLColumnsRetrieve() {
		return toSQLColumns(true);
	}

	/**
	 * Used to make SQL queries by forming the Columns string
	 *
	 * @return a string of database columns
	 */
	public String toSQLColumns(boolean includeId) {
		final var stringBuilder = new StringBuilder("(");

		Stream.of(getClass().getDeclaredFields())
				.filter(field -> field.isAnnotationPresent(SQLValue.class))
				.forEach(field -> {
					if (includeId) {
						stringBuilder.append(String.format("'%s',", getActualFieldName(field)));
					} else {
						if (!field.getName().equals("id")) {
							stringBuilder.append(String.format("'%s',", getActualFieldName(field)));
						}
					}
				});
		//Remove the last ','
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		stringBuilder.append(")");

		return stringBuilder.toString();
	}

	/**
	 * Used to make SQL queries by forming the Values string for inserting SQLModels in the database
	 *
	 * @return a string with the values to insert in the database
	 */
	public String toSQLValuesInsert() {
		return toSQLValues(false);
	}

	/**
	 * Used to make SQL queries by forming the Values string for retrieving SQLModels from the database
	 *
	 * @return a string with the values to insert in the database
	 */
	public String toSQLValuesRetrieve() {
		return toSQLValues(true);
	}

	/**
	 * Used to make SQL queries by forming the Values string
	 *
	 * @return a string of database values
	 */
	public String toSQLValues(boolean includeId) {
		final var stringBuilder = new StringBuilder("(");

		Stream.of(getClass().getDeclaredFields()).filter(field -> field.isAnnotationPresent(SQLValue.class))
				.forEach(field -> {
					field.setAccessible(true);
					try {
						final var value = field.get(this);

						if (value != null && !value.equals(0)) {
							if (includeId) {
								stringBuilder.append(String.format("'%s',", value));
							} else {
								if (!field.getName().equals("id")) {
									stringBuilder.append(String.format("'%s',", value));
								}
							}
						}
					} catch (IllegalAccessException e) {
						Autochef.getLogger().error("Unable to access " + getClass().getName() + " fields value");
					}
				});
		//Remove the last ','
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		stringBuilder.append(")");

		return stringBuilder.toString();
	}

	public Pair<List<String>, List<String>> toSQLValuesSet() {
		final List<String> columns = new ArrayList<>();
		final List<String> values = new ArrayList<>();

		Stream.of(getClass().getDeclaredFields()).filter(field -> field.isAnnotationPresent(SQLValue.class))
				.forEach(field -> {
					field.setAccessible(true);
					try {
						final var value = field.get(this);

						if (isValueDefined(value)) {
							columns.add(String.format("%s", getActualFieldName(field)));
							values.add(String.format("%s", value));
						}
					} catch (IllegalAccessException e) {
						Autochef.getLogger().error("Unable to access " + getClass().getName() + " fields value");
					}
				});

		return new Pair<>(columns, values);
	}

	private boolean isValueDefined(Object value) {
		return value != null
				&& !value.equals(0)
				&& !value.equals(Meal.UNSPECIFIED)
				&& !value.equals(Diet.UNSPECIFIED)
				&& !value.equals(0.0)
				&& !value.equals(new ArrayList<>());
	}

	private String getActualFieldName(Field field) {
		final var actualName = field.getAnnotation(SQLValue.class).value();
		return actualName.isBlank() ? field.getName() : actualName;
	}

	public boolean doesModelExists() throws SQLException {
		try {
			return this.getModel().isPresent();
		} catch (SQLException e) {
			Autochef.getLogger().error("Couldn't retrieve a model from the database", e);
			throw new SQLException("Failed to select a model from the database");
		}
	}

	public abstract int getId();

	public abstract String getName();

	/**
	 * used to push himself(object) in the database
	 */
	@SuppressWarnings("unchecked")
	public void insertIntoDatabase() throws SQLException {
		databaseTools.insert((T) this);
	}
	/**
	 * used to remove each occurrence with the same constraints(attribut inizialize)in the database
	 */
	@SuppressWarnings("unchecked")
	public void removeFromDatabase() throws SQLException {
		databaseTools.delete((T) this);
	}

	/**
	 * Used to select each occurrence with the same constraints(attribut initialize)in the database
	 * @return list of objects of type T that have been invoked by a SQLModel subclass
	 */
	@SuppressWarnings("unchecked")
	public List<T> selectFromDatabase() throws SQLException {
		return databaseTools.select((T) this);
	}

	@JsonIgnore
	public List<T> getModels() throws SQLException {
		return this.selectFromDatabase();
	}

	@JsonIgnore
	public Optional<T> getModel() throws SQLException {
		final List<T> ingredients = this.selectFromDatabase();

		if (ingredients.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(ingredients.get(0));
	}

}
