package ulb.infof307.g02.database;

import ulb.infof307.g02.model.sql_super_class.SQLBuilder;
import ulb.infof307.g02.model.sql_super_class.SQLModel;
import ulb.infof307.g02.model.sql_model.user_model.User;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.ShoppingList;
import ulb.infof307.g02.model.sql_model.store_model.Store;
import ulb.infof307.g02.model.sql_model.recipe_model.IngredientRecipe;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.IngredientShoppingList;
import ulb.infof307.g02.model.sql_model.schedule_model.RecipeSchedule;
import ulb.infof307.g02.model.sql_model.store_model.IngredientStore;

/**
 * Enum class that associates each table of the database to it's primary key id (or closest to primary key)
 * and to its related model class and builder. Which let us generalize the use of database methods since the
 * DatabaseTable Enum determine unequivocally all the required information for the database to do its job.
 */
public enum DatabaseTable {

    USER_TABLE("user", "id", User.class, User.newBuilder()),
    INGREDIENT_TABLE("ingredient", "id", Ingredient.class, Ingredient.newBuilder()),
    RECIPE_TABLE("recipe", "id", Recipe.class, Recipe.newBuilder()),
    STORE_TABLE("store", "id", Store.class, Store.newBuilder()),
    SHOPPING_LIST_TABLE("shopping_list", "id", ShoppingList.class, ShoppingList.newBuilder()),
    INGREDIENT_RECIPE_TABLE("ingredient_recipe", "id_recipe",  IngredientRecipe.class, IngredientRecipe.newBuilder()),
    INGREDIENT_SHOPPING_LIST_TABLE("ingredient_list", "id_shopping",  IngredientShoppingList.class, IngredientShoppingList.newBuilder()),
    INGREDIENT_STORE_TABLE("ingredient_store", "id_store",IngredientStore.class, IngredientStore.newBuilder()),
    RECIPE_SCHEDULE_TABLE("recipe_schedule", "id", RecipeSchedule.class, RecipeSchedule.newBuilder());

    // Name of the table in the database
    private final String name;

    // Name of the primary key column
    private final String keyId;

    // The class of the SQLModel associated to the table
    private final Class<? extends SQLModel<?>> sqlModelClass;

    // The builder of the SQL model associated to the table (fancy constructor)
    private final SQLBuilder<? extends SQLBuilder<?, ?>, ? extends SQLModel<?>> sqlBuilder;

    DatabaseTable(String name, String keyId, Class<? extends SQLModel<?>> sqlModelClass,
                  SQLBuilder<? extends SQLBuilder<?, ?>, ? extends SQLModel<?>> sqlBuilder) {
        this.name = name;
        this.keyId = keyId;
        this.sqlModelClass = sqlModelClass;
        this.sqlBuilder = sqlBuilder;
    }

    public String getName() {
        return name;
    }

    public String getKeyId() {
        return keyId;
    }

    public Class<? extends SQLModel<?>> getSqlModelClass() {
        return sqlModelClass;
    }

    @SuppressWarnings("unchecked")
    public <T extends SQLBuilder<T, R>, R extends SQLModel<R>> SQLBuilder<T, R> getSqlBuilder() {
        return (SQLBuilder<T, R>) sqlBuilder;
    }

    public static <T extends SQLModel<T>> DatabaseTable getTableFromClass(Class<T> tClass) {
        for(final var table : DatabaseTable.values()) {
            if(table.getSqlModelClass().equals(tClass)) {
                return table;
            }
        }

        throw new IllegalArgumentException(String.format("No DatabaseTable exists for class %s", tClass.getName()));
    }

}
