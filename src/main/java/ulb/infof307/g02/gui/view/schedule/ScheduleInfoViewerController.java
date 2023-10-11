package ulb.infof307.g02.gui.view.schedule;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.view.ViewerController;
import ulb.infof307.g02.gui.view.model_viewer.RecipeViewerController;
import ulb.infof307.g02.model.sql_model.schedule_model.RecipeSchedule;
import ulb.infof307.g02.util.attributes.MealTime;
import ulb.infof307.g02.util.AlertUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

public class ScheduleInfoViewerController extends ViewerController<ScheduleInfoViewerController.Listener> {

    @FXML
    private CalendarViewerController calendarController;

    @FXML
    private Label date;

    @FXML
    private VBox morning, noon, evening;

    @FXML
    private DatePicker startMenu, endMenu;

    @FXML
    void modifyButtonOnClicked()     {
        listener.goToConfigureSchedule();
    }

    @FXML
    void backButtonOnClicked() {
        listener.goToMainMenu();
    }

    @FXML
    void generateShoppingButtonOnClicked() {
        Date startDate = Date.from(Instant.from(startMenu.getValue().atStartOfDay(ZoneId.systemDefault())));
        Date endDate = Date.from(Instant.from(endMenu.getValue().atStartOfDay(ZoneId.systemDefault())));
        if (startDate.getTime() < endDate.getTime()) {
            listener.generateShoppingList(startDate, endDate);
        } else {
            Autochef.getLogger().info("Start date must be before end date");
            AlertUtils.alertWarningMessage("La date de début doit précéder la date de fin");
        }
    }

    public CalendarViewerController getCalendar(){
        return calendarController;
    }

    public void setupDate(Date date){
        this.date.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
    }

    public String getDate(){
        return this.date.getText();
    }

    public void removeRecipesSchedule(){
        morning.getChildren().clear();
        noon.getChildren().clear();
        evening.getChildren().clear();
    }

    public void addRecipeFromTable(RecipeSchedule recipe) {
        FXMLLoader loader = loadRecipeViewer();
        setRecipeViewerListener(loader, recipe);
        addRecipeViewerToSchedule(loader, recipe);
    }

    /**
     * Associate the controller of this view controller as the controller of the FXML of the recipe viewer
     * (Recipe Viewer is used at different locations, buttons need to be set differently each time)
     * @param loader : the FXMLLoader of the Recipe Viewer
     * @param recipe : the recipe to show on the schedule
     */
    private void setRecipeViewerListener(FXMLLoader loader, RecipeSchedule recipe) {
        RecipeViewerController controller = loader.getController();
        controller.setListener((RecipeViewerController.Listener) listener);
        controller.setRecipeSchedule(recipe);
        controller.setNotVisible();
    }

    /**
     * Load the FXML of the Recipe viewer
     */
    private FXMLLoader loadRecipeViewer() {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(FXMLPath.RECIPE_VIEWER.getPath())));
        try {
            loader.load();
        } catch (IOException e) {
            Autochef.getLogger().error("Couldn't load the Recipe Viewer (in schedule configure)", e);
            AlertUtils.alertWarningMessage("Une recette n'a pas pu être affichée");
        }
        return loader;
    }

    /**
     * Add the Recipe Viewer to the Schedule
     * @param loader : the FXMLLoader of the Recipe Viewer
     * @param recipe : the recipe to show on the schedule
     */
    private void addRecipeViewerToSchedule(FXMLLoader loader, RecipeSchedule recipe) {
        HBox recipeViewerHBox = loader.getRoot();
        VBox recipeListVBox = getVBoxOfMealTime(recipe.getTime());
        recipeViewerHBox.setPrefWidth(recipeListVBox.getPrefWidth());
        recipeViewerHBox.setId(recipe.getName().replace(" ", "_"));
        recipeListVBox.getChildren().add(recipeViewerHBox);
    }

    public VBox getVBoxOfMealTime(MealTime time) {
        if (Objects.equals(time.name(), "BREAKFAST")) {
            return morning;
        }
        if (Objects.equals(time.name(), "DINNER")) {
            return noon;
        }
        if (Objects.equals(time.name(), "SUPPER")) {
            return evening;
        }
        return null;
    }

    public interface Listener extends ViewerController.ViewerListener {

        void goToMainMenu();

        void goToConfigureSchedule();

        void generateShoppingList(Date start, Date end);

    }

}
