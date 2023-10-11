package ulb.infof307.g02.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * AlertUtils includes all the methods related to the selection of alert messages and the display of dialog boxes
 */
public class AlertUtils {

    private AlertUtils() {}

    /**
     * Selects the type of alert message according to the type of object.
     * @param type type of object
     * @return the alert message
     */
    public static String selectAlertMessage(String type) {
        return switch (type) {
            case "user" -> "Ce nom d'utilisateur existe d\u00E9jà";
            case "shoppinglist" -> "Cette liste de course existe d\u00E9jà";
            case "recipe" -> "Cette recette existe d\u00E9jà";
            case "schedule" -> "Ce menu existe d\u00E9jà";
            case "ingredient" -> "Ce produit est d\u00E9jà dans votre base de produit";
            case "list" -> "Ce produit est d\u00E9jà dans votre liste";
            case "recipeName" -> "Ce nom de recette existe d\u00E9jà";
            case "shoppingListName" -> "Ce nom de liste existe d\u00E9jà";
            default -> "";
        };
    }

    /**
     * Selects the type of alert message when a deletion is made according to the type of object
     * @param type type of object
     * @return the alert message
     */
    public static String selectAlertDeleteMessage(String type) {
        return switch (type) {
            case "user" -> "Supprimer d\u00E9finitivement cette utilisateur ?";
            case "shoppinglist" -> "Supprimer d\u00E9finitivement cette liste de course ?";
            case "recipe" -> "Supprimer d\u00E9finitivement cette recette ?";
            case "schedule" -> "Supprimer d\u00E9finitivement ce menu ?";
            case "ingredient" -> "Supprimer d\u00E9finitivement cet ingrédient de votre base de produit ?";
            default -> "";
        };
    }

    /**
     * Selects the type of alert message when a user try to sign in
     * @param type type of object
     * @return the alert message
     */
    public static String selectAlertUserMessage(String type) {
        return switch (type) {
            case "username" -> "Le nom d'utilisateur est incorrect ou n'existe pas";
            case "password" -> "Le mot de passe est incorrect";
            case "user" -> "Le nom d'utilisateur est incorrect ou n'existe pas\nLe mot de passe est incorrect";
            default -> "";
        };
    }


    public static void alertInformationMessage(String title, String header) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }
    /**
     * Displays a confirmation alert with the corresponding text
     * @param text alert message
     * @return the answer as a boolean
     */
    public static boolean alertConfirmationMessage(String text){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirmation");
        alert.setContentText(text);
        ButtonType yesButton = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesButton, noButton);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isEmpty()){
            return false;
        }
        return result.get() == yesButton;
    }

    /**
     * Displays a warning alert with the corresponding text
     * @param text warning message
     */
    public static void alertWarningMessage(String text){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Avertissement");
        alert.setHeaderText("Attention");
        alert.setContentText(text);
        alert.showAndWait();
    }
}
