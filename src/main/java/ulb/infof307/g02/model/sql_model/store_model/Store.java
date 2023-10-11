package ulb.infof307.g02.model.sql_model.store_model;

import ulb.infof307.g02.database.DatabaseTable;
import ulb.infof307.g02.database.annotation.SQLValue;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.model.sql_super_class.SQLBuilder;
import ulb.infof307.g02.model.sql_interface.SQLContainer;
import ulb.infof307.g02.model.sql_super_class.SQLModel;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Store extends SQLModel<Store> implements SQLContainer {

    @SQLValue
    public final int id;
    @SQLValue
    private final String name;

    private final List<IngredientStore> ingredientsStore;


    public Store(Builder builder) {
        super(Store.class);
        this.id = builder.id;
        this.name = builder.name;
        this.ingredientsStore = builder.ingredientStore;
    }


    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<IngredientStore> getIngredientsStores() {
        return ingredientsStore;
    }


    @Override
    public void insertSubModels() throws SQLException {
        for (IngredientStore ingredientStore : ingredientsStore) {
            try {
                Optional<Ingredient> ingredient = Ingredient.newBuilder()
                        .setName(ingredientStore.getName())
                        .build()
                        .getModel();

                Optional<Store> store = Store.newBuilder()
                        .setName(this.getName())
                        .build()
                        .getModel();

                if (ingredient.isPresent() && store.isPresent()) {
                    ingredientStore.completeAndInsert(ingredient.get().getId(), store.get().getId());
                }

            } catch (SQLException e) {
                Autochef.getLogger().error("Couldn't insert a SubModel of Store in the database");
                throw new SQLException("Failed to insert SubModel into database");
            }
        }
    }

    @Override
    public void deleteSubModels() throws SQLException {
        try {
            IngredientStore.newBuilder()
                    .setIdStore(id)
                    .build()
                    .removeFromDatabase();
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't delete a SubModel of Store from the database");
            throw new SQLException("Failed to remove SubModel from database");
        }
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id, name, ingredientsStore);
    }

    @Override
    public final boolean equals(Object other) {
        if(other == null) {
            return false;
        }
        if(this == other) {
            return true;
        }
        if(!(other instanceof Store)) {
            return false;
        }
        final var otherStore = (Store) other;

        return Objects.equals(otherStore.name, this.name)
                && Objects.equals(otherStore.ingredientsStore, this.ingredientsStore);
    }

    public static Builder newBuilder(){return new Builder();}

    public static class Builder extends SQLBuilder<Builder, Store>{

        private int id;

        private String name;

        private List<IngredientStore> ingredientStore = new ArrayList<>();

        Builder(){}

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setIngredientStore(List<IngredientStore> ingredientStore) {
            this.ingredientStore = ingredientStore;
            return this;
        }


        @Override
        protected Store makeObjectFromResultSet() throws SQLException {
            final int storeId = resultSet.getInt(DatabaseTable.STORE_TABLE.getKeyId());
            final List<IngredientStore> ingredientStoreList = new ArrayList<>();

            for (SQLModel<?> model : IngredientStore.newBuilder().setIdStore(storeId).build().selectFromDatabase()) {
                ingredientStoreList.add((IngredientStore) model);
            }
            for (IngredientStore ingredientStore : ingredientStoreList) {
                ingredientStore.setName(Ingredient
                        .newBuilder().setId(ingredientStore.getIdIngredient()).build()
                        .selectFromDatabase().get(0).getName()
                );
            }
            return Store.newBuilder()
                    .setId(resultSet.getInt("id"))
                    .setName(resultSet.getString("name"))
                    .setIngredientStore(ingredientStoreList)
                    .build();
        }

        @Override
        protected Store makeObjectFromBuilder() {
            return new Store(this);
        }
    }



}
