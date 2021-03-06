package gui;

import java.util.ArrayList;
import java.util.List;

import general.NewSlogoInstanceCreator;
import general.Properties;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

/**
 * @author Eric Song
 *
 */
public class WindowViewController {

	private static final String VIEW_PROPERTIES_PACKAGE = "resources.properties/";
	private static final Paint BACKGROUND_COLOR_SCENE = Color.ALICEBLUE;

	private Properties viewProperties;
	private Stage stage;
	private TabPane tabPane;
	private List<TabViewController> tabViewControllerList;
	private NewSlogoInstanceCreator instanceCreator;

	public WindowViewController(Stage stage, NewSlogoInstanceCreator instanceCreator) {
		this.instanceCreator = instanceCreator;
		viewProperties = new Properties(VIEW_PROPERTIES_PACKAGE + "View");
		setupStage();
		tabViewControllerList = new ArrayList<TabViewController>();

	}

	/**
	 * @param tabTitle
	 * @return a new tab view controller
	 */
	public TabViewController makeTabViewController(String tabTitle) {
		TabViewController tabViewController = new TabViewController(tabPane, viewProperties, tabTitle, instanceCreator);
		tabViewControllerList.add(tabViewController);

		return tabViewController;
	}

	/**
	 * @param closedTab
	 * performs garbage collection for when tab is closed
	 */
	public void closeTabViewController(TabViewController closedTab) {
		tabPane.getTabs().remove(closedTab.getTab());

	}

	private void setupStage() {
		double appWidth = viewProperties.getDoubleProperty("app_width");
		double appHeight = viewProperties.getDoubleProperty("app_height");
		stage = new Stage();
		Group sceneRoot = new Group();
		BorderPane borderPane = new BorderPane();
		tabPane = new TabPane();
		borderPane.setCenter(tabPane);
		sceneRoot.getChildren().add(borderPane);
		stage.setTitle(viewProperties.getStringProperty("title"));
		Scene scene = new Scene(sceneRoot, appWidth, appHeight, BACKGROUND_COLOR_SCENE);
		borderPane.prefHeightProperty().bind(scene.heightProperty());
		borderPane.prefWidthProperty().bind(scene.widthProperty());
		stage.setScene(scene);
		stage.show();

	}

}
