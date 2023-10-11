package ulb.infof307.g02.gui.controller.schedule;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.controller.Controller;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.controller.recipe.RecipeConfigureController;
import ulb.infof307.g02.gui.controller.recipe.RecipeInfoController;
import ulb.infof307.g02.gui.view.model_viewer.RecipeViewerController;
import ulb.infof307.g02.gui.view.schedule.ScheduleConfigureViewerController;
import ulb.infof307.g02.model.sql_super_class.SQLModel;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.model.sql_model.schedule_model.RecipeSchedule;
import ulb.infof307.g02.model.sql_model.user_model.User;
import ulb.infof307.g02.util.attributes.MealTime;
import ulb.infof307.g02.util.AlertUtils;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Random;

public class ScheduleConfigureController extends Controller
        implements ScheduleConfigureViewerController.Listener,
        CalendarController.Listener,
        RecipeViewerController.Listener {

    private final ScheduleConfigureViewerController scheduleConfigureViewerController;

    private final CalendarController calendarController;

    private final Recipe RECIPE = Recipe.newBuilder().setUserID(User.getInstance().getId()).build();

    public ScheduleConfigureController() {
        super(FXMLPath.CONFIGURE_SCHEDULE);

        this.scheduleConfigureViewerController = fxmlLoader.getController();
        scheduleConfigureViewerController.setListener(this);

        calendarController = new CalendarController(this, scheduleConfigureViewerController.getCalendar());
    }

    public void loadRecipeFromDatabase() {
        try {
            List<Recipe> allRecipesOfUser = RECIPE.getModels();

            ObservableList<Recipe> details = FXCollections.observableArrayList(allRecipesOfUser);
            FilteredList<Recipe> filteredList = new FilteredList<>(details, p -> true);
            scheduleConfigureViewerController.setList(filteredList);
        } catch (SQLException e) {
            Autochef.getLogger().error("Coudn't load recipes of user", e);
            AlertUtils.alertWarningMessage("Un problème est survenu lors de la récupération des recettes de l'utilisateur");
        }
    }

    @Override
    public void loadRecipeScheduleFromDatabase() {
        // Instantiate the filter model for the search
        RecipeSchedule recipeOfDay = RecipeSchedule.newBuilder()
                .setDate(scheduleConfigureViewerController.getDate())
                .build();

        // Clear the previous views (we only need to show the recipes of the selected day)
        removeRecipeFromSchedule();

        // Show the recipes of the selected day
        try {
            for (RecipeSchedule recipeSchedule : recipeOfDay.getModels()) {
                    Recipe.newBuilder()
                            .setID(recipeSchedule.getRecipeID())
                            .build()
                            .getModel()
                            .ifPresent(model -> {
                                recipeSchedule.setName(model.getName());
                                scheduleConfigureViewerController.addRecipeFromTable(recipeSchedule);
                            });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String formatDate(Date dayDate) {
        return new SimpleDateFormat("dd/MM/yyyy").format(dayDate);
    }

    private boolean[] findAlreadySetSchedule(Date dayDate) throws SQLException {
        boolean[] setSchedules = {false, false, false};

        try {
            List<RecipeSchedule> scheduledRecipesOfDay = RecipeSchedule.newBuilder()
                    .setDate(formatDate(dayDate))
                    .build()
                    .getModels();

            // Write down if a day time is already filled on the schedule date
            for (RecipeSchedule recipeSchedule : scheduledRecipesOfDay) {
                if (recipeSchedule.getTime() == MealTime.BREAKFAST) {
                    setSchedules[0] = true;
                }
                if (recipeSchedule.getTime() == MealTime.DINNER) {
                    setSchedules[1] = true;
                }
                if (recipeSchedule.getTime() == MealTime.SUPPER) {
                    setSchedules[2] = true;
                }
            }
            return setSchedules;

        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't retrieve the Recipes from the database", e);
            throw new SQLException("Retrieve from database failed");
        }
    }

    /**
     * Add a random recipe in the schedule to given date at a given time
     * @param dayDate : date of the schedule
     * @param mealTimeIndex : time of the schedule
     */
    private void addRandomRecipeToSchedule(Date dayDate, int mealTimeIndex) throws EmptyStackException, SQLException {
        try {
            List<Recipe> allRecipesOfUser = RECIPE.getModels();

            if (allRecipesOfUser.isEmpty()) {
                throw new EmptyStackException();
            }

            Recipe recipe = allRecipesOfUser.get(new Random().nextInt(allRecipesOfUser.size()));
            addRecipeToSchedule(RecipeSchedule.newBuilder()
                    .setUserID(User.getInstance().getId())
                    .setName(recipe.getName())
                    .setRecipeID(recipe.getId())
                    .setDate(formatDate(dayDate))
                    .setTime(MealTime.values()[mealTimeIndex])
                    .build()
            );
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't retrieve the Recipes from the database", e);
            throw new SQLException("Retrieve from database failed");
        }
    }

    /**
     * Fill the schedule with a day with random recipes
     * @param dayDate : date of the schedule to fill
     */
    private void fillScheduleOfDay(Date dayDate) throws SQLException {
        boolean[] alreadySetSchedules = findAlreadySetSchedule(dayDate);
        if (alreadySetSchedules == null) {
            return;
        }

        for (int i = 0; i < 3; i++) {
            if (!alreadySetSchedules[i]) {
                addRandomRecipeToSchedule(dayDate, i);
            }
        }
    }

    @Override
    public void generateMenu(Date startMenu, Date endMenu) {
        if (startMenu.getDate() > endMenu.getDate() ) {
            throw new IllegalArgumentException("Start date should be before the end date");
        }

        while (startMenu.getDay() != endMenu.getDay() ||startMenu.getMonth() != endMenu.getMonth() || startMenu.getYear() != endMenu.getYear()){
            try {
                fillScheduleOfDay(startMenu);
            } catch (SQLException e) {
                Autochef.getLogger().error("Couldn't fill the schedule", e);
                AlertUtils.alertWarningMessage("La génération n'a pas pu se terminer correctement");
                return;
            }
            startMenu.setDate(startMenu.getDate() + 1);
        }
    }

    public void setupDate(Date date){
        scheduleConfigureViewerController.setDate(date);
        loadRecipeScheduleFromDatabase();
    }

    @Override
    public void addRecipeToSchedule(SQLModel<?> recipe) {
        try {
            recipe.insertIntoDatabase();
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't insert a RecipeSchedule into the database", e);
            AlertUtils.alertWarningMessage("La recette n'a pas pu être sauvegardée");
        }
        loadRecipeScheduleFromDatabase();
    }

    public void removeRecipeFromSchedule(){
        scheduleConfigureViewerController.removeRecipesSchedule();
    }

    @Override
    public void createRecipe() {
        goTo(() -> new RecipeConfigureController(this));
    }

    @Override
    public void inspectRecipe(SQLModel<?> recipeSchedule) {
        // Receive RecipeSchedule from argument "SQLModel recipe", need to retrieve the true Recipe object from the db
        try {
            Recipe.newBuilder()
                    .setName(recipeSchedule.getName())
                    .build()
                    .getModel()
                    .ifPresent(
                            recipe -> goTo(model -> new RecipeInfoController(this, model), recipe)
                    );
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't retrieve the model from the database", e);
            AlertUtils.alertWarningMessage("La recette n'a pas pu être chargée");
        }


    }

    @Override
    public void configureRecipe(SQLModel<?> recipe) {
        // Receive RecipeSchedule from argument "SQLModel recipe" and get Recipe from RecipeSchedule name in db
        try {
            Recipe.newBuilder()
                    .setName(recipe.getName())
                    .build()
                    .getModel()
                    .ifPresent(
                            infoRecipe -> goTo(model -> new RecipeConfigureController(this, model), infoRecipe)
                    );
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't retrieve the model from the database", e);
            AlertUtils.alertWarningMessage("La recette n'a pas pu être chargée");
        }
    }

    @Override
    public void deleteRecipe(SQLModel<?> recipe) {
        RecipeSchedule newRecipe = (RecipeSchedule) recipe;
        scheduleConfigureViewerController.removeRecipeHBoxFromSchedule((RecipeSchedule) recipe);
        try {
            newRecipe.removeFromDatabase();
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't delete the RecipeSchedule from the database", e);
            AlertUtils.alertWarningMessage("La recette n'a pas pu être retirée du planning");
        }
    }

    @Override
    public void goBackToScheduleInfo() {
        goTo(ScheduleInfoController::new);
    }

}
