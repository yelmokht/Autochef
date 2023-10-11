package ulb.infof307.g02.gui.controller;

/**
 * Enum class used to store all the FXML path's used at some point in the application. Let us call statically a path
 * constant that won't change even if the path changes or if the name of the file change.
 */
public enum FXMLPath {

	CONNECTION_TO_ACCOUNT("/ulb/infof307/g02/gui/view/ConnectionToAccountViewer.fxml"),
	CREATE_ACCOUNT("/ulb/infof307/g02/gui/view/CreateAccountViewer.fxml"),
	AUTOCHEF_MENU("/ulb/infof307/g02/gui/view/Autochef.fxml"),
	INGREDIENT_VIEWER("/ulb/infof307/g02/gui/view/IngredientViewer.fxml"),
	RECIPE_VIEWER("/ulb/infof307/g02/gui/view/RecipeViewer.fxml"),
	CONFIGURE_RECIPE("/ulb/infof307/g02/gui/view/RecipeConfigureViewer.fxml"),
	INFO_RECIPE("/ulb/infof307/g02/gui/view/RecipeInfoViewer.fxml"),
	LIST_OF_RECIPE("/ulb/infof307/g02/gui/view/RecipeListViewer.fxml"),
	CONFIGURE_SCHEDULE("/ulb/infof307/g02/gui/view/ScheduleConfigureViewer.fxml"),
	INFO_SCHEDULE("/ulb/infof307/g02/gui/view/ScheduleInfoViewer.fxml"),
	LIST_OF_SCHEDULE("/ulb/infof307/g02/gui/view/ScheduleListViewer.fxml"),
	SHOPPING_LIST_VIEWER("/ulb/infof307/g02/gui/view/ShoppingListViewer.fxml"),
	CONFIGURE_SHOPPING_LIST("/ulb/infof307/g02/gui/view/ShoppingListConfigureViewer.fxml"),
	INFO_SHOPPING_LIST("/ulb/infof307/g02/gui/view/ShoppingListInfoViewer.fxml"),
	LIST_OF_SHOPPING_LIST("/ulb/infof307/g02/gui/view/ShoppingListListViewer.fxml"),
	LIST_OF_STORES("/ulb/infof307/g02/gui/view/StoreListViewer.fxml"),
	STORE_VIEWER("/ulb/infof307/g02/gui/view/StoreViewer.fxml"),
	INFO_STORE("/ulb/infof307/g02/gui/view/StoreInfoViewer.fxml"),
	CONFIGURE_STORE("/ulb/infof307/g02/gui/view/StoreConfigureViewer.fxml"),
	STORE_PRICE_INFO_VIEWER("/ulb/infof307/g02/gui/view/StorePriceInfoViewer.fxml"),
	NONE("");

	private final String path;

	FXMLPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
