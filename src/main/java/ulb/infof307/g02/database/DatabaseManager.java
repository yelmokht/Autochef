package ulb.infof307.g02.database;

import org.apache.commons.io.FileUtils;
import ulb.infof307.g02.gui.controller.main.Autochef;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseManager is a tool used to manage the database access. The singleton design pattern is used to keep the
 * DatabaseManager instantiations to only one, making it easier to set options if necessary.
 */
public class DatabaseManager {

    private static final String DATABASE_PATH = "jdbc:sqlite:" + System.getProperty("user.dir") + "/db.db";
    private static final int NORMAL_TABLE_COUNT = 9;

    private static DatabaseManager connectionManager;

    private Connection connection;

    private DatabaseManager() { }

    /**
     * @return an instance of DatabaseManager
     */
    public static DatabaseManager getConnectionManager() {
        if (connectionManager == null) {
            connectionManager = new DatabaseManager();
        }
        return connectionManager;
    }

    /**
     * Request a connection to the database.
     *
     * @return a connection to the database
     * @throws SQLException if the connection fails
     */
    public Connection getConnection() throws SQLException {
        try {
            this.connection = DriverManager.getConnection(DATABASE_PATH);
        } catch (SQLException e) {
            // Log the error and try to overcome it, else forward the error to calling function
            Autochef.getLogger().error("Could not open the database properly", e);

            closeDatabaseConnection();

            // Throw a new exception to avoid trying to execute queries on a close connection to the db
            throw new SQLException("Could not connect to the Database");
        }

        return connection;
    }

    /**
     * Close the connection to the database.
     */
    public void closeDatabaseConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch(SQLException e) {
                // Only need to log the error, if file didn't close properly, we can't do anything about it
                Autochef.getLogger().error("Failed to close the connection to the database", e);
            }
        }
    }

    /**
     * Checks if the database is correctly formatted.
     *
     * @return true if the database is formatted correctly
     */
    public boolean checkDatabaseFormat() {
        List<String> tablesList = new ArrayList<>();

        try (Connection databaseConnection = getConnection()) {
            try(Statement statement = databaseConnection.createStatement()) {
                final ResultSet resultSet = statement
                        .executeQuery("SELECT name FROM sqlite_schema WHERE type ='table' AND name NOT LIKE 'sqlite_%'");

                while(resultSet.next()){
                    tablesList.add(resultSet.getString("name"));
                }
            }
        } catch (SQLException ex){
            Autochef.getLogger().error("Error: Could not check database format.");
        }

        return tablesList.size() == NORMAL_TABLE_COUNT;
    }

    /**
     * Format the database.
     *
     * @throws IOException if creating the temporary dump files fails
     */
    public void formatDatabase() throws IOException {
        if(!checkDatabaseFormat()) {
            // Copy dump from resources to tmp
            final var stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("originalDB.dump");

            if(stream == null) {
                Autochef.getLogger().error("Could not load resource originalDB.dump. Unable to format database.");
                return;
            }
            final var tmpDumpFile = File.createTempFile("originalDB", ".dump");
            // create tmp init file for sqlite
            final var tmpInitFile = File.createTempFile("initDb", ".init");
            tmpDumpFile.deleteOnExit();
            tmpInitFile.deleteOnExit();

            FileUtils.copyInputStreamToFile(stream, tmpDumpFile);

            try (final var initFileWriter = new PrintWriter(tmpInitFile.getAbsolutePath())) {
                initFileWriter.println(".read " + tmpDumpFile.getAbsolutePath());
            }
            //give init file to db for restoration
            restoreDBFromFile(tmpInitFile.getAbsolutePath());
        }
    }

    /**
     * Use command line tools to create a new database from a dump file (backup database file)
     *
     * @param initPath : path for the directory where restored database file need to be put
     */
    private void restoreDBFromFile(String initPath){
        try {
            Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "sqlite3 -init "+ initPath +" db.db"});
        } catch (IOException e) {
            Autochef.getLogger().error("Could not initialize new DB from dump.");
        }
    }
}
