package ulb.infof307.g02.util;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.apache.commons.lang3.StringUtils;

import static java.lang.String.valueOf;

/**
 * FieldUtils includes all methods related to checking and selecting error messages based on text fields.
 */
public class FieldUtils {

    private FieldUtils(){}

    /**
     * Checks the text according to the type of text retrieved.
     * Example: servings needs the field to contain only numbers and not to be empty
     * @param type type of field
     * @param text field text
     * @return the check as a boolean
     */
    public static boolean isTextCorrect(String type, String text) {
        return switch (type) {
            case "name", "type", "unit" -> StringUtils.isAlphaSpace(text) && !StringUtils.isBlank(text);
            case "servings" -> StringUtils.isNumericSpace(valueOf(text)) && !StringUtils.isBlank(text);
            case "instructions" -> StringUtils.isAlphanumericSpace(text.replace("\n", "")) && !StringUtils.isBlank(text);
            case "email" -> StringUtils.contains(text, "@");
            case "meal", "diet", "empty", "shoppinglist", "recipe", "username", "password", "store" -> !StringUtils.isBlank(text);
            default -> false;
        };
    }

    /**
     * Selects the type of warning message according to the type of field.
     * Example: servings needs to indicate that only numeric characters are accepted.
     * @param type type of field
     * @return the type of message as a String
     */
    public static String selectFieldMessage(String type) {
        return switch (type) {
            case "servings" -> "Caract\u00E8res num\u00E9riques accept\u00E9s uniquement (1-9)";
            case "instructions" -> "Caract\u00E8res alphanum\u00E9riques accept\u00E9s uniquement (A-Z) (1-9)";
            case "meal", "diet", "empty", "shoppinglist", "recipe", "username", "password" -> "Le champ ne peut pas \u00EAtre vide";
            case "email" -> "L'adresse email doit être valide";
            default -> "";
        };
    }

    /**
     * Checks that the text field is correctly filled according to the type of element.
     * @param node a TextField
     * @param type type of field
     * @param warning a label that displays an error message in case of error
     * @param text a text retrieved from the field
     */
    public static void checkField(Node node, String type, Label warning, String text) {
        if (!isTextCorrect(type, text)) {
            setWarningMessage(type, warning);
            setOnWarning(node, warning);
        }
        else {
            setOffWarning(node, warning);
        }
    }

    private static void setWarningMessage(String type, Label warning) {warning.setText(selectFieldMessage(type));}

    private static void setOnWarning(Node node, Label warning) {warning.setVisible(true); node.setStyle("-fx-border-color: RED");}

    private static void setOffWarning(Node node, Label warning) {warning.setVisible(false); node.setStyle("-fx-border-color: GREEN");}

}
