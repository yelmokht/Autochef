package ulb.infof307.g02.model.sql_model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.model.sql_model.store_model.Store;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TestStore {

    private static final Store STORE = Store.newBuilder().build();

    private static Store store1;
    private static Store store2;
    private static Store unused;

    private static final int STORE_1_ID = 1;

    private static final String STORE_1_NAME = "Delhaize";

    private static final int STORE_2_ID = 2;

    private static final String STORE_2_NAME = "Proxy";

    private static final int UNUSED_ID = 99;

    private static final String UNUSED_NAME = "ziufzidu";

    @BeforeAll
    static void initialize_tests() {
        // Delete all data from the database
        try {
            STORE.removeFromDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        store1 = Store.newBuilder()
                .setId(STORE_1_ID)
                .setName(STORE_1_NAME)
                .build();

        store2 = Store.newBuilder()
                .setId(STORE_2_ID)
                .setName(STORE_2_NAME)
                .build();


        unused = Store.newBuilder()
                .setId(UNUSED_ID)
                .setName(UNUSED_NAME)
                .build();
    }

    @AfterEach
    void reset_database() {
        // Delete all data from the database
        try {
            STORE.removeFromDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @BeforeEach
    void set_database() {
        // Add sample data to the database
        try {
            store1.insertIntoDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            store2.insertIntoDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    void after_inserting_objects_are_present_in_database() {
        Optional<Store> retrievedStore = Optional.empty();
        try {
            retrievedStore = store1.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        Optional<Store> retrievedUnused = Optional.empty();
        try {
            retrievedUnused = unused.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        assertTrue(retrievedStore.isPresent());
        assertEquals(store1, retrievedStore.get());

        assertFalse(retrievedUnused.isPresent());
    }

    @Test
    void after_removing_one_object_other_objects_in_database_() {
        try {
            store1.removeFromDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        Optional<Store> retrievedStore1 = Optional.empty();
        try {
            retrievedStore1 = store1.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        Optional<Store> retrievedStore2 = Optional.empty();
        try {
            retrievedStore2 = store2.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        assertFalse(retrievedStore1.isPresent());

        assertTrue(retrievedStore2.isPresent());
        assertEquals(store2, retrievedStore2.get());
    }

    @Test
    void after_removing_objects_are_not_in_database() {
        try {
            store1.removeFromDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            store2.removeFromDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        Optional<Store> retrievedStore1 = Optional.empty();
        try {
            retrievedStore1 = store1.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        Optional<Store> retrievedStore2 = Optional.empty();
        try {
            retrievedStore2 = store2.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        assertFalse(retrievedStore1.isPresent());
        assertFalse(retrievedStore2.isPresent());
    }

    @Test
    void deleting_all_at_once_nothing_left_in_database() {
        try {
            STORE.removeFromDatabase(); // Filter nothing because empty ingredient, delete all
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        Optional<Store> retrievedStore1 = Optional.empty();
        try {
            retrievedStore1 = store1.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        Optional<Store> retrievedStore2 = Optional.empty();
        try {
            retrievedStore2 = store2.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        assertFalse(retrievedStore1.isPresent());
        assertFalse(retrievedStore2.isPresent());
    }

    @Test
    void selecting_object_from_name_get_the_right_object() {
        Optional<Store> retrievedStore1 = Optional.empty();
        try {
            retrievedStore1 = Store.newBuilder()
                    .setName(STORE_1_NAME)
                    .build()
                    .getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        Optional<Store> retrievedStore2 = Optional.empty();
        try {
            retrievedStore2 = Store.newBuilder()
                    .setName(STORE_2_NAME)
                    .build()
                    .getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        assertTrue(retrievedStore1.isPresent());
        assertEquals(store1, retrievedStore1.get());

        assertTrue(retrievedStore2.isPresent());
        assertEquals(store2, retrievedStore2.get());
    }

    @Test
    void selecting_object_from_id_get_the_right_object() {
        Optional<Store> retrievedStore1 = Optional.empty();
        try {
            retrievedStore1 = Store.newBuilder()
                    .setId(STORE_1_ID)
                    .build()
                    .getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        Optional<Store> retrievedStore2 = Optional.empty();
        try {
            retrievedStore2 = Store.newBuilder()
                    .setId(STORE_2_ID)
                    .build()
                    .getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        assertTrue(retrievedStore1.isPresent());
        assertEquals(store1, retrievedStore1.get());

        assertTrue(retrievedStore2.isPresent());
        assertEquals(store2, retrievedStore2.get());
    }

    @Test
    void selecting_object_from_complete_object_get_the_right_object() {
        Optional<Store> retrievedStore1 = Optional.empty();
        try {
            retrievedStore1 = store1.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        Optional<Store> retrievedStore2 = Optional.empty();
        try {
            retrievedStore2 = store2.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        assertTrue(retrievedStore1.isPresent());
        assertEquals(store1, retrievedStore1.get());

        assertTrue(retrievedStore2.isPresent());
        assertEquals(store2, retrievedStore2.get());
    }

    @Test
    void selecting_object_from_not_valid_id_get_nothing() {
        Optional<Store> retrievedUnused = Optional.empty();
        try {
            retrievedUnused = Store.newBuilder()
                    .setId(UNUSED_ID)
                    .build()
                    .getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        assertFalse(retrievedUnused.isPresent());
    }
}
