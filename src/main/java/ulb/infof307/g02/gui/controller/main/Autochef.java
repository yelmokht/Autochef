package ulb.infof307.g02.gui.controller.main;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import ulb.infof307.g02.database.DatabaseManager;
import ulb.infof307.g02.gui.controller.login.ConnectionToAccountController;

import java.io.IOException;

/**
 * Autochef allows to launch the application from the Main class.
 * Its launch from Main allows the creation of an executable jar.
 */
public class Autochef extends Application {

	private static final Logger logger = Logger.getLogger(Autochef.class);
	private static Stage stage;

	@Override
	public void init() {
		logger.info("Initializing application.");
		try {
			DatabaseManager.getConnectionManager().formatDatabase();
		} catch (IOException e) {
			logger.error("Error formatting database.");
		}
	}

	@Override
	public void start(Stage stage) throws IOException {
		Autochef.stage = stage;
		Autochef.stage.setTitle("AutoChef");
		new ConnectionToAccountController().show();
	}

	public static Stage getStage() {
		return Autochef.stage;
	}

	public static void main() {
		launch();
	}

	public static Logger getLogger() {
		return logger;
	}

}
