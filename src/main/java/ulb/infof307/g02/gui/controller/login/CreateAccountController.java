package ulb.infof307.g02.gui.controller.login;

import ulb.infof307.g02.gui.controller.main.Autochef;
import ulb.infof307.g02.gui.controller.Controller;
import ulb.infof307.g02.gui.controller.FXMLPath;
import ulb.infof307.g02.gui.view.login.CreateAccountViewerController;
import ulb.infof307.g02.model.sql_model.user_model.User;
import ulb.infof307.g02.util.AlertUtils;

import java.sql.SQLException;

import static ulb.infof307.g02.util.AlertUtils.alertWarningMessage;
import static ulb.infof307.g02.util.AlertUtils.selectAlertMessage;

/**
 * CreateAccountController implements the Listener of CreateAccountViewerController to program its buttons and
 * functionalities
 */
public class CreateAccountController extends Controller
        implements CreateAccountViewerController.Listener {

    public CreateAccountController(ConnectionToAccountController listener) {
        super(FXMLPath.CREATE_ACCOUNT, listener);

        ((CreateAccountViewerController) fxmlLoader.getController()).setListener(this);
    }

    @Override
    public void goBackToConnectionToAccount() {
        goTo(ConnectionToAccountController::new);
    }

    @Override
    public void createAccount(String name, String mail, String password) {
        User newUser = User.newBuilder()
                .setName(name)
                .setEmail(mail)
                .setPassword(password)
                .build();

        try {
            if (newUser.doesModelExists()) {
                Autochef.getLogger().info("User already exists");
                alertWarningMessage(selectAlertMessage("user"));
                return;
            }
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't verify existence of User model");
            AlertUtils.alertWarningMessage("L'utilisateur n'a pas pu être créé");
            return;
        }

        try {
            newUser.insertIntoDatabase();
        } catch (SQLException e) {
            Autochef.getLogger().error("Couldn't create a new user", e);
            AlertUtils.alertWarningMessage("Le compte n'a pas pu être cr\u00E9\u00E9");
            return;
        }

        goBackToConnectionToAccount();
    }

}
