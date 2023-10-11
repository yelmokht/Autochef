package ulb.infof307.g02.database;

import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.model.sql_super_class.SQLModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * DatabaseQuery is used for direct SQL queries to the SQLite Database, forming the bottom layer of the database implementation.
 *
 * @param <T> the SQLModel.
 */
public class DatabaseQuery<T extends SQLModel<T>> {

    private final DatabaseTable databaseTable;

    DatabaseQuery(DatabaseTable databaseTable) {
        this.databaseTable = databaseTable;
    }

    /**
     * Uses the SQL result set to create an instance of T.
     *
     * @param resultSet the SQL result set
     * @return a new instance of T
     */
    @SuppressWarnings("unchecked")
    public Optional<T> makeObject(ResultSet resultSet) {
        try {
            return Optional.of((T) databaseTable.getSqlBuilder().fromResultSet(resultSet).build());
        } catch (ClassCastException e) {
            Autochef.getLogger().error("Could not cast result set content to its matching SQLModel.");
        }

        return Optional.empty();
    }

    /**
     * Inserts the specified SQL model in the database.
     *
     * @param sqlModel the SQL model
     * @throws SQLException if a sql error occurs
     */
    public void insertToDatabase(SQLModel<T> sqlModel) throws SQLException {
        try (final var databaseConnection = DatabaseManager.getConnectionManager().getConnection();
             final var statement = databaseConnection.createStatement()) {
                statement.executeUpdate(
                        String.format("INSERT INTO %s %s VALUES %s",
                                databaseTable.getName(),
                                sqlModel.toSQLColumnsInsert(),
                                sqlModel.toSQLValuesInsert()
                        )
                );
        }
    }

    /**
     * Remove from the database the elements matching the given constraints.
     *
     * @param constraints the SQL constraints
     *
     * Examples:
     *   deleteFromDatabase("id = 1");
     *   deleteFromDatabase("name LIKE 'Milk'");
     *   deleteFromDatabase("id"); -> delete all models
     */
    public void deleteFromDatabase(String constraints) throws SQLException {
        try (final var databaseConnection = DatabaseManager.getConnectionManager().getConnection();
             final var statement = databaseConnection.createStatement()) {

            final var instructions = constraints.isBlank() ? "DELETE FROM " + databaseTable.getName()
                    : "DELETE FROM " + databaseTable.getName() + " WHERE " + constraints;
            statement.executeUpdate(instructions);
        }
    }

    /**
     * Find the SQL models matching the given constraints.
     *
     * @param constraints the constraints
     * @return a list of SQL models.
     */
    public List<T> selectInDatabase(String constraints) throws SQLException {

        try (final var databaseConnection = DatabaseManager.getConnectionManager().getConnection();
             final var statement = databaseConnection.createStatement()) {

            final var sqlInstruction = constraints.isEmpty() ? "SELECT * FROM " + databaseTable.getName() :
                    "SELECT * FROM " + databaseTable.getName() + " WHERE " + constraints;

            try (final ResultSet resultSet = statement.executeQuery(sqlInstruction)){
                return retrieveListFromDatabase(resultSet);
            }
        }
    }

    /**
     * Retrieve a list of SQL models matching the given result set
     *
     * @param resultSet the result set
     * @return a list of SQL models
     */
    private List<T> retrieveListFromDatabase(ResultSet resultSet) throws SQLException {
        final List<T> results = new ArrayList<>();

        while (resultSet.next()) {
            makeObject(resultSet).ifPresent(results::add);
        }
        return results;
    }
}
