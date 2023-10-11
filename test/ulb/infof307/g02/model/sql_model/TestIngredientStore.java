package ulb.infof307.g02.model.sql_model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_model.recipe_model.IngredientRecipe;
import ulb.infof307.g02.model.sql_model.store_model.IngredientStore;
import ulb.infof307.g02.model.sql_model.store_model.Store;
import ulb.infof307.g02.util.attributes.Department;
import ulb.infof307.g02.util.attributes.Unit;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TestIngredientStore {
    private static final IngredientStore INGREDIENT_STORE = IngredientStore.newBuilder().build();
    private static final Ingredient INGREDIENT = Ingredient.newBuilder().build();
    private static final Store STORE = Store.newBuilder().build();

    private static Store store;
    private static Ingredient milk;
    private static Ingredient carrots;

    private static IngredientStore storeMilk;
    private static IngredientStore storeCarrots;

    private static final int STORE_ID = 1;
    private static final String STORE_NAME = "Delhaize";

    private static final int MILK_ID = 1;
    private static final String MILK_NAME = "Lait";
    private static final Department MILK_TYPE = Department.DAIRY;
    private static final Unit MILK_UNIT = Unit.LITER;

    private static final double MILK_PRICE = 2.50;

    private static final int CARROTS_ID = 2;
    private static final String CARROTS_NAME = "Carottes";
    private static final Department CARROTS_TYPE = Department.FRUITS_AND_VEGETABLES;
    private static final Unit CARROTS_UNIT = Unit.GRAM;

    private static final double CARROT_PRICE = 0.8;

    @BeforeAll
    static void initialize_tests() {
        // Delete all data from the database
        try {
            INGREDIENT_STORE.removeFromDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            INGREDIENT.removeFromDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        milk = Ingredient.newBuilder()
                .setId(MILK_ID)
                .setName(MILK_NAME)
                .setType(MILK_TYPE)
                .setUnit(MILK_UNIT)
                .build();

        carrots = Ingredient.newBuilder()
                .setId(CARROTS_ID)
                .setName(CARROTS_NAME)
                .setType(CARROTS_TYPE)
                .setUnit(CARROTS_UNIT)
                .build();

        storeMilk = IngredientStore.newBuilder()
                .setName(MILK_NAME)
                .setIdStore(STORE_ID)
                .setIdIngredient(MILK_ID)
                .setPrice(MILK_PRICE)
                .build();

        storeCarrots = IngredientStore.newBuilder()
                .setName(CARROTS_NAME)
                .setIdStore(STORE_ID)
                .setIdIngredient(CARROTS_ID)
                .setPrice(CARROT_PRICE)
                .build();

        store = Store.newBuilder()
                .setId(STORE_ID)
                .setName(STORE_NAME)
                .setIngredientStore(List.of(storeMilk,storeCarrots))
                .build();
    }

    @AfterEach
    void reset_database() {
        // Delete all data from the database
        try {
            INGREDIENT_STORE.removeFromDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            INGREDIENT.removeFromDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
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
            milk.insertIntoDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            carrots.insertIntoDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            store.insertIntoDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    void after_inserting_objects_are_present_in_database() {
        Optional<IngredientStore> retrievedMilk = Optional.empty();
        try {
            retrievedMilk = storeMilk.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        Optional<IngredientStore> retrievedCarrots = Optional.empty();
        try {
            retrievedCarrots = storeCarrots.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        assertTrue(retrievedMilk.isPresent());
        assertEquals(storeMilk, retrievedMilk.get());

        assertTrue(retrievedCarrots.isPresent());
        assertEquals(storeCarrots, retrievedCarrots.get());
    }

    @Test
    void after_removing_objects_are_not_in_database() {
        try {
            storeMilk.removeFromDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            storeCarrots.removeFromDatabase();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        Optional<IngredientStore> retrievedMilk = Optional.empty();
        try {
            retrievedMilk = storeMilk.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        Optional<IngredientStore> retrievedCarrots = Optional.empty();
        try {
            retrievedCarrots = storeCarrots.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        assertFalse(retrievedMilk.isPresent());
        assertFalse(retrievedCarrots.isPresent());
    }

    @Test
    void deleting_all_at_once_nothing_left_in_database() {
        try {
            INGREDIENT_STORE.removeFromDatabase(); // Filter nothing because empty ingredient, delete all
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        Optional<IngredientStore> retrievedMilk = Optional.empty();
        try {
            retrievedMilk = storeMilk.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        Optional<IngredientStore> retrievedCarrots = Optional.empty();
        try {
            retrievedCarrots = storeCarrots.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        assertFalse(retrievedMilk.isPresent());
        assertFalse(retrievedCarrots.isPresent());
    }

    @Test
    void deleting_all_at_once_from_store_nothing_left_in_database() {
        try {
            STORE.removeFromDatabase(); // Filter nothing because empty ingredient, delete all
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        Optional<IngredientStore> retrievedMilk = Optional.empty();
        try {
            retrievedMilk = storeMilk.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        Optional<IngredientStore> retrievedCarrots = Optional.empty();
        try {
            retrievedCarrots = storeCarrots.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        assertFalse(retrievedMilk.isPresent());
        assertFalse(retrievedCarrots.isPresent());
    }

    @Test
    void selecting_object_from_complete_object_get_the_right_object() {
        Optional<IngredientStore> retrievedMilk = Optional.empty();
        try {
            retrievedMilk = storeMilk.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        Optional<IngredientStore> retrievedCarrots = Optional.empty();
        try {
            retrievedCarrots = storeCarrots.getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }

        assertTrue(retrievedMilk.isPresent());
        assertEquals(storeMilk, retrievedMilk.get());

        assertTrue(retrievedCarrots.isPresent());
        assertEquals(storeCarrots, retrievedCarrots.get());
    }

    @Test
    void selecting_object_from_not_valid_id_get_nothing() {
        Optional<IngredientStore> retrievedUnused = Optional.empty();
        try {
            retrievedUnused = IngredientStore.newBuilder()
                    .setIdStore(99)
                    .setIdIngredient(99)
                    .build()
                    .getModel();
        } catch (java.sql.SQLException throwables) {
            throwables.printStackTrace();
        }
        assertFalse(retrievedUnused.isPresent());
    }
}
