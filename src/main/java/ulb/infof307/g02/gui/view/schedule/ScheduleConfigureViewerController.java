package ulb.infof307.g02.gui.view.schedule;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.view.ViewerController;
import ulb.infof307.g02.gui.view.model_viewer.RecipeViewerController;
import ulb.infof307.g02.model.sql_super_class.SQLModel;
import ulb.infof307.g02.model.sql_model.recipe_model.Recipe;
import ulb.infof307.g02.model.sql_model.schedule_model.RecipeSchedule;
import ulb.infof307.g02.model.sql_model.user_model.User;
import ulb.infof307.g02.util.attributes.MealTime;
import ulb.infof307.g02.util.AlertUtils;
import ulb.infof307.g02.util.RecipeCellFactory;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;

public class ScheduleConfigureViewerController extends ViewerController<ScheduleConfigureViewerController.Listener> implements Initializable {

    @FXML
    private DatePicker startMenu, endMenu;

    @FXML
    private ListView<Recipe> recipeListView;

    @FXML
    private VBox breakfastVBox, dinerVBox, supperVBox;

    @FXML
    private Accordion scheduleAccordion;

    @FXML
    private Label date;

    @FXML
    private CalendarViewerController calendarController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        recipeListView.setCellFactory(new RecipeCellFactory());
    }

    public String getDate() {
        return this.date.getText();
    }

    /**
     * @return the MealTime corresponding to the accordion's pane that is opened
     */
    public MealTime getMealTime() {
        for (TitledPane tp : scheduleAccordion.getPanes()){
            if (tp.isExpanded()) {
                if (Objects.equals(tp.getId(), "breakfast")) {
                    return MealTime.BREAKFAST;
                }
                if (Objects.equals(tp.getId(), "diner")) {
                    return MealTime.DINNER;
                }
                if (Objects.equals(tp.getId(), "supper")) {
                    return MealTime.SUPPER;
                }
            }
        }
        return null;
    }

    /**
     * @return the VBox associated to the MealTile given as parameter
     */
    public VBox getVBoxOfMealTime(MealTime time) {
        if (Objects.equals(time.name(), "BREAKFAST")) {
            return breakfastVBox;
        }
        if (Objects.equals(time.name(), "DINNER")) {
            return dinerVBox;
        }
        if (Objects.equals(time.name(), "SUPPER")) {
            return supperVBox;
        }
        return null;
    }

    @FXML
    protected void recipeOnClicked() {
        MealTime time = getMealTime();
        if (time != null) {
            Recipe recipe = recipeListView.getSelectionModel().getSelectedItem();
            if (recipe != null) {
                RecipeSchedule recipeSchedule = RecipeSchedule.newBuilder()
                        .setUserID(User.getInstance().getId())
                        .setName(recipe.getName())
                        .setUserID(User.getInstance().getId())
                        .setRecipeID(recipe.getId())
                        .setDate(getDate())
                        .setTime(getMealTime())
                        .build(); // Used as DTO, could have been listener.addRecipeToScheduleAndSave(user, name, id,...)
                listener.addRecipeToSchedule(recipeSchedule);
            }
        }
    }

    @FXML
    void generateMenuButtonOnClicked() {
        Date dateStart = Date.from(Instant.from(startMenu.getValue().atStartOfDay(ZoneId.systemDefault())));
        Date dateEnd = Date.from(Instant.from(endMenu.getValue().atStartOfDay(ZoneId.systemDefault())));
        if (dateStart.getTime() < dateEnd.getTime()) {
            listener.generateMenu(dateStart, dateEnd);
        } else {
            Autochef.getLogger().info("Start date must be before end date to generate a menu");
            AlertUtils.alertWarningMessage("La date de début doit précéder la date de fin");
        }

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
        recipeViewerHBox.setId(recipe.getName().replace(" ","_"));
        recipeListVBox.getChildren().add(recipeViewerHBox);
    }

    public void removeRecipeHBoxFromSchedule(RecipeSchedule recipe) {
        VBox vb = getVBoxOfMealTime(recipe.getTime());
        vb.getChildren().remove(vb.lookup(String.format("#%s", recipe.getName().replace(" ","_"))));
    }

    public void removeRecipesSchedule(){
        breakfastVBox.getChildren().clear();
        dinerVBox.getChildren().clear();
        supperVBox.getChildren().clear();
    }

    @FXML
    void createRecipeButtonOnClicked() {
        listener.createRecipe();
    }

    @FXML
    void backButtonOnClicked() {
        listener.goBackToScheduleInfo();
    }

    protected void loadModelsFromDatabase() {
        listener.loadRecipeFromDatabase();
        listener.loadRecipeScheduleFromDatabase();
    }

    @Override
    public void setListener(Listener listener) {
        super.setListener(listener);
        loadModelsFromDatabase();
    }

    public void setList(FilteredList<Recipe> filteredList){

        recipeListView.setItems(filteredList);
    }

    public void setDate(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        this.date.setText(formatter.format(date));
    }

    public CalendarViewerController getCalendar(){
        return calendarController;
    }

    public interface Listener extends ViewerController.ViewerListener {

        void addRecipeToSchedule(SQLModel<?> recipe);

        void createRecipe();

        void goBackToScheduleInfo();

        void loadRecipeFromDatabase();

        void loadRecipeScheduleFromDatabase();

        void generateMenu(Date startMenu, Date endMenu);

    }

}
