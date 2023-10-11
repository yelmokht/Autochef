package ulb.infof307.g02.model.sql_model.schedule_model;

import ulb.infof307.g02.database.annotation.SQLValue;
import ulb.infof307.g02.model.sql_super_class.SQLBuilder;
import ulb.infof307.g02.model.sql_super_class.SQLModel;
import ulb.infof307.g02.util.attributes.MealTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class RecipeSchedule extends SQLModel<RecipeSchedule> {

    private String name;
    @SQLValue
    private int id;
    @SQLValue("id_user")
    private final int userID;
    @SQLValue("id_recipe")
    private final int recipeID;
    @SQLValue
    private final String date;
    @SQLValue
    private final MealTime time;

    private RecipeSchedule(Builder builder){
        super(RecipeSchedule.class);
        this.name = builder.name;
        this.id = builder.id;
        this.userID = builder.userID;
        this.recipeID = builder.recipeID;
        this.date = builder.date;
        this.time = builder.time;
    }

    @Override
    public int getId() {
        return id;
    }

    public int getUserID() {
        return userID;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getRecipeID() {
        return recipeID;
    }

    public String getDate() {
        return date;
    }

    public MealTime getTime() {
        return time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id){this.id =id;}

    @Override
    public final int hashCode() {
        return Objects.hash(recipeID, date, time, id, name);
    }

    @Override
    public final boolean equals(Object other) {
        if(other == null) {
            return false;
        }
        if(this == other) {
            return true;
        }
        if(!(other instanceof RecipeSchedule)) {
            return false;
        }
        final var otherRecipe = (RecipeSchedule) other;
        return otherRecipe.recipeID == this.recipeID
                && Objects.equals(otherRecipe.date, this.date)
                && Objects.equals(otherRecipe.time, this.time);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder extends SQLBuilder<Builder, RecipeSchedule> {

        private String name;

        private int id;
        
        private int userID;

        private int recipeID;

        private String date;

        private MealTime time;

        private Builder() { }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setID(int id) {
            this.id = id;
            return this;
        }
        public Builder setUserID(int userID) {
            this.userID = userID;
            return this;
        }

        public Builder setRecipeID(int recipeID) {
            this.recipeID = recipeID;
            return this;
        }

        public Builder setDate(String date) {
            this.date = date;
            return this;
        }

        public Builder setTime(MealTime time) {
            this.time = time;
            return this;
        }

        @Override
        public SQLBuilder<Builder, RecipeSchedule> fromResultSet(ResultSet resultSet) {
            this.resultSet = resultSet;
            return this;
        }

        @Override
        protected RecipeSchedule makeObjectFromResultSet() throws SQLException {
            return RecipeSchedule.newBuilder()
                    .setID(resultSet.getInt("id"))
                    .setUserID(resultSet.getInt("id_user"))
                    .setRecipeID(resultSet.getInt("id_recipe"))
                    .setDate(resultSet.getString("date"))
                    .setTime(MealTime.valueOf(resultSet.getString("time")))
                    .build();
        }

        @Override
        protected RecipeSchedule makeObjectFromBuilder() {
            return new RecipeSchedule(this);
        }

    }

}
