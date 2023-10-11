package ulb.infof307.g02.gui.controller.schedule;

import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.controller.Controller;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.controller.recipe.RecipeInfoController;
import ulb.infof307.g02.gui.view.model_viewer.RecipeViewerController;
import ulb.infof307.g02.gui.view.schedule.ScheduleInfoViewerController;
import ulb.infof307.g02.model.sql_super_class.SQLModel;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.model.sql_model.shoppinglist_model.ShoppingList;
import ulb.infof307.g02.model.sql_model.recipe_model.IngredientRecipe;
import ulb.infof307.g02.model.sql_model.schedule_model.RecipeSchedule;
import ulb.infof307.g02.model.sql_model.user_model.User;
import ulb.infof307.g02.util.AlertUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleInfoController extends Controller
        implements ScheduleInfoViewerController.Listener, CalendarController.Listener, RecipeViewerController.Listener {

    private final ScheduleInfoViewerController scheduleInfoViewerController;

    public ScheduleInfoController() {
        super(FXMLPath.INFO_SCHEDULE);

        this.scheduleInfoViewerController = fxmlLoader.getController();
        scheduleInfoViewerController.setListener(this);

        new CalendarController(this, scheduleInfoViewerController.getCalendar());
    }

    @Override
    public void goToConfigureSchedule() {
        goTo(ScheduleConfigureController::new);
    }

    @Override
    public void setupDate(Date date) {
        scheduleInfoViewerController.setupDate(date);
        loadRecipeScheduleFromDatabase();
    }

    private List<RecipeSchedule> retrieveScheduledRecipes(Date startDate, Date endDate) throws SQLException {
        List<RecipeSchedule> scheduledRecipes = new ArrayList<>();

        while(startDate.getDate() != endDate.getDate() || startDate.getMonth() != endDate.getMonth() || startDate.getYear() != endDate.getYear()){
            String dateString = new SimpleDateFormat("dd/MM/yyyy").format(startDate);
            try {
                scheduledRecipes.addAll(RecipeSchedule.newBuilder().setDate(dateString).build().getModels());
            } catch (SQLException e) {
                Autochef.getLogger().error("Couldn't retrieve recipes from database", e);
                throw new SQLException("Error while trying to select all matching models");
            }
            startDate.setDate(startDate.getDate() + 1);
        }
        return scheduledRecipes;
    }

    private ShoppingList createShoppingList(List<RecipeSchedule> recipeScheduleList, Date startDate, Date endDate) throws SQLException {
        // Create a shopping list named after the time period it concerns
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        ShoppingList shoppingList = ShoppingList.newBuilder()
                .setUserID(User.getInstance().getId())
                .setName("Shopping List : " + formatter.format(startDate) + " - " + formatter.format(endDate))
                .build();

        // Fill the shopping list with all the ingredients contained by the recipes scheduled on that time period
        for(RecipeSchedule recipeSchedule: recipeScheduleList){
            try {
                Recipe.newBuilder()
                        .setID(recipeSchedule.getRecipeID())
                        .build()
                        .getModel()
                        .ifPresent(recipe -> {
                            for(IngredientRecipe ingredientRecipe : recipe.getIngredients()){
                                shoppingList.addIngredientFromInstance(ingredientRecipe);
                            }
                        });
            } catch (SQLException e) {
                Autochef.getLogger().error("Couldn't select a recipe from database", e);
                throw new SQLException("Failed to retrieve the Recipe model");
            }

        }

        return shoppingList;
    }

    public void generateShoppingList(Date startDate, Date endDate) {
        try {
            List<RecipeSchedule> allRecipesScheduled = retrieveScheduledRecipes(startDate, endDate);
            ShoppingList shoppingList = createShoppingList(allRecipesScheduled, startDate, endDate);

            shoppingList.insertIntoDatabase();
            AlertUtils.alertInformationMessage("G\u00e9n\u00e9ration r\u00e9ussie", shoppingList.getName()+ " a \u00e9t\u00e9 g\u00e9n\u00e9r\u00e9e avec succ\u00e8s");
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't generate a ShoppingList from Schedule", e);
            AlertUtils.alertWarningMessage("La liste de course n'a pas pu être sauvegardée");
        }
    }

    public void loadRecipeScheduleFromDatabase() {
        RecipeSchedule recipesOfDay = RecipeSchedule.newBuilder()
                .setDate(scheduleInfoViewerController.getDate())
                .build();

        removeRecipeFromSchedule();

        try {
            for (RecipeSchedule recipeSchedule : recipesOfDay.getModels()) {
                Recipe.newBuilder()
                        .setID(recipeSchedule.getRecipeID())
                        .build()
                        .getModel()
                        .ifPresent(model -> {
                            recipeSchedule.setName(model.getName());
                            scheduleInfoViewerController.addRecipeFromTable(recipeSchedule);
                        });
            }
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't select a recipe from database", e);
            AlertUtils.alertWarningMessage("Certaines recettes de cette journée n'ont pas pu être récupérées");
        }
    }

    public void removeRecipeFromSchedule(){
        scheduleInfoViewerController.removeRecipesSchedule();
    }

    @Override
    public void inspectRecipe(SQLModel<?> recipeSchedule) {
        // Receive RecipeSchedule from argument "SQLModel recipe", need to retrieve the true Recipe object from the db
        try {
            Recipe recipe = Recipe.newBuilder()
                    .setUserID(User.getInstance().getId())
                    .setName(recipeSchedule.getName())
                    .build()
                    .getModel()
                    .orElseThrow();
            goTo(model -> new RecipeInfoController(this, model), recipe);
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't retrieve the Recipe from the database", e);
            AlertUtils.alertWarningMessage("La informations de la recette n'ont pas pu être chargées");
        }

    }

    @Override
    public void configureRecipe(SQLModel<?> recipe) throws IOException {
        // Nothing to do, not implemented on that instance of the view
    }

    @Override
    public void deleteRecipe(SQLModel<?> recipe) throws IOException {
        // Nothing to do, not implemented on that instance of the view
    }

}
