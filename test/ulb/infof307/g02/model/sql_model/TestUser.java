package ulb.infof307.g02.model.sql_model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g02.model.sql_model.user_model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TestUser {
    private static final User USER = User.newBuilder().build();

    private static User user;
    private static User unusedUser;

    private static final int USER_ID = 1;
    private static final String USER_NAME = "Admin";
    private static final String USER_PASSWORD = "Password";
    private static final String USER_EMAIL = "admin.autochef@gmail.com";

    private static final int UNUSED_ID = 99;
    private static final String UNUSED_NAME = "ziufzidu";
    private static final String UNUSED_EMAIL = "qozmjbfo.aibeuf@zojbdf.azd";

    @BeforeAll
    static void initialize_tests() {
        // Delete all data from the database
        try {
            USER.removeFromDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        user = User.newBuilder()
                .setID(USER_ID)
                .setName(USER_NAME)
                .setEmail(USER_EMAIL)
                .setPassword(USER_PASSWORD)
                .build();

        unusedUser = User.newBuilder()
                .setID(UNUSED_ID)
                .setName(UNUSED_NAME)
                .setEmail(UNUSED_EMAIL)
                .setPassword(UNUSED_NAME)
                .build();
    }

    @AfterEach
    void reset_database() {
        // Delete all data from the database
        try {
            USER.removeFromDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @BeforeEach
    void set_database() {
        // Add sample data to the database
        try {
            user.insertIntoDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    void after_inserting_objects_are_present_in_database() {
        Optional<User> retrievedUser = null;
        try {
            retrievedUser = user.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        Optional<User> retrievedUnused = null;
        try {
            retrievedUnused = unusedUser.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        assertTrue(retrievedUser.isPresent());
        assertEquals(user, retrievedUser.get());

        assertFalse(retrievedUnused.isPresent());
    }

    @Test
    void after_removing_objects_are_not_in_database() {
        try {
            user.removeFromDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        Optional<User> retrievedUser = null;
        try {
            retrievedUser = user.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        assertFalse(retrievedUser.isPresent());
    }

    @Test
    void deleting_all_at_once_nothing_left_in_database() {
        try {
            USER.removeFromDatabase(); // Filter nothing because empty ingredient, delete all
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        Optional<User> retrievedUser = null;
        try {
            retrievedUser = user.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        List<User> users = null;
        try {
            users = USER.getModels();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        assertFalse(retrievedUser.isPresent());
        assertEquals(0, users.size());
    }

    @Test
    void selecting_object_from_name_get_the_right_object() {
        Optional<User> retrievedUser = null;
        try {
            retrievedUser = User.newBuilder()
                    .setName(USER_NAME)
                    .build()
                    .getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        assertTrue(retrievedUser.isPresent());
        assertEquals(user, retrievedUser.get());
    }

    @Test
    void selecting_object_from_id_get_the_right_object() {
        Optional<User> retrievedUser = null;
        try {
            retrievedUser = User.newBuilder()
                    .setID(USER_ID)
                    .build()
                    .getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        assertTrue(retrievedUser.isPresent());
        assertEquals(user, retrievedUser.get());
    }

    @Test
    void selecting_object_from_complete_object_get_the_right_object() {
        Optional<User> retrievedUser = null;
        try {
            retrievedUser = user.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        assertTrue(retrievedUser.isPresent());
        assertEquals(user, retrievedUser.get());
    }

    @Test
    void selecting_object_from_not_valid_id_get_nothing() {
        Optional<User> retrievedUnused = null;
        try {
            retrievedUnused = User.newBuilder()
                    .setID(UNUSED_ID)
                    .build()
                    .getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        assertFalse(retrievedUnused.isPresent());
    }
}