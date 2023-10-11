package ulb.infof307.g02.gui.controller.login;

import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.controller.Controller;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.view.login.ConnectionToAccountViewerController;
import ulb.infof307.g02.model.sql_model.user_model.User;
import ulb.infof307.g02.util.AlertUtils;

import java.sql.SQLException;
import java.util.Optional;

import static ulb.infof307.g02.util.AlertUtils.alertWarningMessage;

/**
 * ConnectionToAccountController implements the Listener of ConnectionToAccountViewerController to program its buttons
 * and functionalities
 */
public class ConnectionToAccountController extends Controller implements ConnectionToAccountViewerController.Listener{

    private final ConnectionToAccountViewerController connectionToAccountViewerController;

    public ConnectionToAccountController() {
        super(FXMLPath.CONNECTION_TO_ACCOUNT);

        this.connectionToAccountViewerController = fxmlLoader.getController();
        connectionToAccountViewerController.setListener(this);
    }

    @Override
    public void goToMainMenu(String name, String password) {
        User userTemplate = User.newBuilder()
                .setName(name)
                .setPassword(password)
                .build();

        if (!userTemplate.validateUserConnection()) {
            alertWarningMessage(AlertUtils.selectAlertUserMessage("user"));
        }

        try {
            Optional<User> user = userTemplate.getModel();

            if (user.isEmpty()) {
                Autochef.getLogger().error("Unable to find user in database.");
                AlertUtils.alertWarningMessage("L'identifiant ou le mot de passe est incorrect");
            } else {
                User.setInstance(user.get());
                goToMainMenu();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void goToRegister() {
        goTo(() -> new CreateAccountController(this));
    }

    /**
     * Used to debug the application, permit to bypass the connection screen and connect automatically to a test account
     */
    @Override
    public void goVIP() {
        User vipUser = User.newBuilder()
                .setName("test")
                .setEmail("test@test.test")
                .setPassword("test")
                .build();

        try {
            if (vipUser.getModel().isPresent()) {
                User.setInstance(vipUser.getModel().get());
                goToMainMenu();
            } else {
                try {
                    vipUser.insertIntoDatabase();
                } catch (SQLException e) {
                    Autochef.getLogger().error("Couldn't insert the test user into the database", e);
                    AlertUtils.alertWarningMessage("L'utilisateur VIP n'a pas été enregistré");
                }
            }
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't retrieve the test user from the database", e);
            AlertUtils.alertWarningMessage("Un problème est survenu en essayant de récupérer le compte");
        }

    }

}
