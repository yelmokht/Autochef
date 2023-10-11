package ulb.infof307.g02.gui.view.login;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import ulb.infof307.g02.gui.view.ViewerController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static ulb.infof307.g02.util.FieldUtils.checkField;
import static ulb.infof307.g02.util.FieldUtils.isTextCorrect;

/**
 * ConnectionToAccountViewerController is a view controller used to connect to an existing account
 */
public class ConnectionToAccountViewerController extends ViewerController<ConnectionToAccountViewerController.Listener>
        implements Initializable {

    @FXML
    private Button loginButton;

    @FXML
    private TextField userTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Label usernameWarning;

    @FXML
    private Label passwordWarning;

    public void initialize(URL url, ResourceBundle resourceBundle) {

        userTextField.textProperty().addListener((observable, oldValue, newValue)
                -> isFieldCorrect(userTextField, "username", usernameWarning, newValue));

        passwordTextField.textProperty().addListener((observable, oldValue, newValue)
                -> isFieldCorrect(passwordTextField, "password", passwordWarning, newValue));

    }

    /**
     * Checks that the text field is correctly filled according to the type of element.
     * @param node a TextField
     * @param type type of field
     * @param warning a label that displays an error message in case of error
     * @param text a text retrieved from the field
     */
    private void isFieldCorrect(Node node, String type, Label warning, String text) {
        checkField(node, type, warning, text);
        loginButton.setDisable(!isAllFieldsCorrect());
    }

    public boolean isAllFieldsCorrect() {
        return isUsernameCorrect() && isPasswordCorrect();
    }

    private boolean isUsernameCorrect() {
        return isTextCorrect("username", userTextField.getText());
    }

    private boolean isPasswordCorrect() {
        return isTextCorrect("password", passwordTextField.getText());
    }

    @FXML
    protected void loginButtonOnClicked() throws IOException {
        listener.goToMainMenu(userTextField.getText(), passwordTextField.getText());
    }

    @FXML
    protected void passButtonOnClicked() throws IOException {
        listener.goVIP();
    }
    @FXML
    protected void createAccountOnClicked() throws IOException {
        listener.goToRegister();
    }

    public interface Listener extends ViewerController.ViewerListener {

        void goToMainMenu(String user, String password) throws IOException;

        void goToRegister() throws IOException;

        void goVIP() throws IOException;

    }
}
