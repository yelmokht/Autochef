package ulb.infof307.g02.model.sql_model.user_model;

import ulb.infof307.g02.database.annotation.SQLValue;
import ulb.infof307.g02.model.sql_super_class.SQLBuilder;
import ulb.infof307.g02.model.sql_super_class.SQLModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class User extends SQLModel<User> {

    private static User instance = null;
    @SQLValue
    private final int id;
    @SQLValue
    private final String name;
    @SQLValue
    private final String email;
    @SQLValue
    private final String password;

    private User(Builder builder) {
        super(User.class);
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.password = builder.password;
    }

    public static User getInstance() {
        return User.instance;
    }

    public static void setInstance(User user) {
        User.instance = user;
    }

    public static void resetInstance() {
        User.instance = null;
    }

    @Override
    public int getId() {return this.id;}

    @Override
    public String getName() {return this.name;}

    public String getEmail() {return this.email;}

    public String getPassword() {return this.password;}

    public boolean validateUserConnection() {
        try {
            return this.getModel()
                    .map(user -> user.getName().equals(this.getName()) && user.getPassword().equals(this.getPassword()))
                    .orElse(false);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
    @Override
    public final int hashCode() {
        return Objects.hash(name, email, password);
    }

    @Override
    public final boolean equals(Object other) {
        if(other == null) {
            return false;
        }
        if(this == other) {
            return true;
        }
        if(!(other instanceof User)) {
            return false;
        }
        final var otherRecipe = (User) other;

        return Objects.equals(otherRecipe.name, this.name)
                && Objects.equals(otherRecipe.email, this.email)
                && Objects.equals(otherRecipe.password, this.password);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder extends SQLBuilder<Builder, User> {

        private int id;
        private String name;
        private String email;
        private String password;

        private Builder() {
        }

        public Builder setID(int id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        @Override
        public SQLBuilder<Builder, User> fromResultSet(ResultSet resultSet) {
            this.resultSet = resultSet;
            return this;
        }

        @Override
        protected User makeObjectFromResultSet() throws SQLException {
            return User.newBuilder()
                    .setID(resultSet.getInt("id"))
                    .setName(resultSet.getString("name"))
                    .setEmail(resultSet.getString("email"))
                    .setPassword(resultSet.getString("password"))
                    .build();
        }

        @Override
        protected User makeObjectFromBuilder() {
            return new User(this);
        }
    }
}
