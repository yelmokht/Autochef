package ulb.infof307.g02.gui.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.controller.main.AutochefController;
import ulb.infof307.g02.model.sql_super_class.SQLModel;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Controller super-class, used to generalize some parts that all controller have in common. For example, loading the
 * associated view, setting the stage and scene and display it. Also used to generalize navigation between controllers
 */
public abstract class Controller implements ControllerNavigator {

	// Used as static attribute for all controllers because we need to pass it from controller to controller otherwise
	protected static final Stage stage = Autochef.getStage();

	// Calling parent, used to "goBack" to the previous view
	protected Controller parentController;

	protected FXMLLoader fxmlLoader;

	protected Controller(FXMLPath fxmlPath) {
		if (!Objects.equals(fxmlPath.getPath(), "")) {
			fxmlLoader = loadFXML(fxmlPath);

			stage.setScene(new Scene((fxmlLoader.getRoot())));
		}
	}

	protected Controller(FXMLPath fxmlPath, Controller parentController) {
		this(fxmlPath);
		this.parentController = parentController;
	}

	/**
	 * Loads the FXML of the associated view in a FXMLLoader object
	 * @param fxmlPath : the path to the associated FXML file
	 * @return a FXMLLoader, which can be used to configure the scene before displaying it
	 */
	private FXMLLoader loadFXML(FXMLPath fxmlPath) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath.getPath()));
		try {
			loader.load();
		} catch (IOException e) {
			Autochef.getLogger().error("Couldn't load the FXML File : " + fxmlPath, e);
		}

		return loader;
	}

	/**
	 * Used to display the FXML loaded in the controller on the stage
	 */
	public void show() {
		stage.show();
		stage.centerOnScreen();
	}

	/**
	 * Displays the main menu. For example, after closing a window or when logging into the app.
	 */
	public void goToMainMenu() {
		goTo(AutochefController::new);
	}


	// See "ControllerNavigator" interface for goTo methods explanation

	@Override
	public <T extends Controller> void goTo(Supplier<T> nextController) {
		// Since <T> is a controller, we've got access to a show() method (through the Controller class) which displays the associated view
		nextController.get().show();
	}

	@Override
	public <M extends SQLModel<M>, T extends Controller> void goTo(Function<M, T> nextController, M model) {
		// Since <T> is a controller, we've got access to a show() method (through the Controller class) which displays the associated view
		nextController.apply(model).show();
	}

}
