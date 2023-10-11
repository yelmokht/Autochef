package ulb.infof307.g02.model.sql_model.store_model;

import ulb.infof307.g02.database.annotation.SQLValue;
import ulb.infof307.g02.model.model_interface.IngredientInstance;
import ulb.infof307.g02.model.sql_interface.SQLSubModel;
import ulb.infof307.g02.model.sql_model.ingredient_model.Ingredient;
import ulb.infof307.g02.model.sql_super_class.SQLBuilder;
import ulb.infof307.g02.model.sql_super_class.SQLModel;
import ulb.infof307.g02.util.attributes.Unit;

import java.sql.SQLException;
import java.util.Objects;

public class IngredientStore extends SQLModel<IngredientStore> implements SQLSubModel, IngredientInstance {

    @SQLValue("id_store")
    private int idStore;

    @SQLValue("id_ingredient")
    private int idIngredient;

    private String name;

    @SQLValue
    private double price;



    IngredientStore(Builder builder){
        super(IngredientStore.class);
        this.idStore = builder.idStore;
        this.idIngredient = builder.idIngredient;
        this.name = builder.name;
        this.price = builder.price;
    }

    @Override
    public int getId() {
        return idStore;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getIdIngredient() {
        return idIngredient;
    }

    public void setId(int idStore){
        this.idStore = idStore;
    }

    public void setIdIngredient(int idIngredient){
        this.idIngredient = idIngredient;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void completeAndInsert(int ingredientId, int storeId) throws SQLException {
        IngredientStore.newBuilder()
                .setIdStore(storeId)
                .setIdIngredient(ingredientId)
                .setPrice(this.price)
                .build()
                .insertIntoDatabase();
    }

    @Override
    public Unit getUnit() {
        return Unit.EURO;
    }

    @Override
    public void setQuantity(double quantity, String unit) {
        this.price = quantity;
    }

    @Override
    public double getQuantity() {
        return price;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(idStore, idIngredient, name, price);
    }

    @Override
    public final boolean equals(Object other) {
        if(other == null) {
            return false;
        }
        if(this == other) {
            return true;
        }
        if(!(other instanceof IngredientStore)) {
            return false;
        }
        final var otherIngredientStore = (IngredientStore) other;

        return otherIngredientStore.idStore == this.idStore
                && otherIngredientStore.idIngredient == this.idIngredient
                && Objects.equals(otherIngredientStore.name, this.name)
                && Objects.equals(otherIngredientStore.price, this.price);
    }

    public static Builder newBuilder(){
        return new Builder();
    }

    public static class Builder extends SQLBuilder<Builder, IngredientStore> {

        private int idStore;

        private int idIngredient;

        private String name;

        private double price;

        Builder() {}

        public Builder setIdStore(int idStore) {
            this.idStore = idStore;
            return this;
        }

        public Builder setIdIngredient(int idIngredient) {
            this.idIngredient = idIngredient;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPrice(double price) {
            this.price = price;
            return this;
        }

        @Override
        protected IngredientStore makeObjectFromResultSet() throws SQLException {
            final int retrievedIngredientId = resultSet.getInt("id_ingredient");
            final String ingredientName = Ingredient.newBuilder()
                    .setId(retrievedIngredientId)
                    .build()
                    .getModel()
                    .map(Ingredient::getName)
                    .orElseThrow(SQLException::new);

            return IngredientStore.newBuilder()
                    .setIdStore(resultSet.getInt("id_store"))
                    .setIdIngredient(retrievedIngredientId)
                    .setName(ingredientName)
                    .setPrice(resultSet.getDouble("price"))
                    .build();
        }

        @Override
        protected IngredientStore makeObjectFromBuilder() {
            return new IngredientStore(this);
        }
    }

}
