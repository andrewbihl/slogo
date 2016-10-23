package gui;

import java.awt.Canvas;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import general.MainController;
import general.Properties;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * 
 * @author LuciaMartos
 */
public class ViewController implements Observer {
	private Properties viewProperties;

	private static final String VIEW_PROPERTIES_PACKAGE = "resources.properties/";
	private static final Paint BACKGROUND_COLOR_SCENE = Color.ALICEBLUE;
	private Group sceneRoot;
	private TitleBox titleBox;
	private InputPanel inputPanel;
	private ObservableList<String> pastCommands;
	private CanvasActions canvasActions;
	private SettingsController settingsController;
	private Stage stage;
	private ErrorConsole errorConsole;
	private TableColumn userDefinedCommandNames;
	private TableColumn userDefinedCommandValues;
	private BoardStateDataSource dataSource;
	private SlogoCommandInterpreter interpreter;
	private TurtleDataTranslator turtleTranslator; 

	public ViewController(Stage stage) {
		viewProperties = new Properties(VIEW_PROPERTIES_PACKAGE + "View");
		sceneRoot = new Group();
		turtleTranslator = new TurtleDataTranslator(viewProperties.getDoubleProperty("canvas_width"), viewProperties.getDoubleProperty("canvas_height"));

		//HBox box1 = new HBox(15);
		//box1.setPadding(new Insets(15, 15, 15, 15));
		VBox box2 = new VBox(15);
		box2.setPadding(new Insets(15, 15, 15, 15));
		HBox box3 = new HBox(15);

		VBox box4 = new VBox(15);

		sceneRoot.getChildren().add(box2);


		box2.getChildren().add(createTitleBox());
		box2.getChildren().add(box3);
		box2.getChildren().add(initializeSettingsController());
		box2.getChildren().add(createErrorConsole());

		box3.getChildren().add(box4);
		box3.getChildren().add(createPastCommandsListView());
		box3.getChildren().add(createTableView());

		box4.getChildren().add(createCanvas());
		box4.getChildren().add(createCommandInputter());

		setupStage(stage);
	}

	private void setupStage(Stage stage) {
		double appWidth = viewProperties.getDoubleProperty("app_width");
		double appHeight = viewProperties.getDoubleProperty("app_height");
		Scene scene = new Scene(sceneRoot, appWidth, appHeight, BACKGROUND_COLOR_SCENE);
		stage.setTitle(viewProperties.getStringProperty("title"));
		stage.setScene(scene);
		stage.show();
	}

	private Node createTitleBox() {
		double padding = viewProperties.getDoubleProperty("padding");
		double x = padding;
		double y = padding;
		double width = viewProperties.getDoubleProperty("title_box_width");
		double height = viewProperties.getDoubleProperty("title_box_height");
		String title = "SLOGO";
		titleBox = new TitleBox(x, y, width, height, title);
		return titleBox;
	}

	public Node createCanvas() {
		double canvasX = viewProperties.getDoubleProperty("canvas_x");
		double canvasY = viewProperties.getDoubleProperty("canvas_y");
		double canvasWidth = viewProperties.getDoubleProperty("canvas_width");
		double canvasHeight = viewProperties.getDoubleProperty("canvas_height");
		double canvasLayoutX = viewProperties.getDoubleProperty("canvas_layout_x");
		double canvasLayoutY = viewProperties.getDoubleProperty("canvas_layout_y");
		double errorLabelX = viewProperties.getDoubleProperty("error_label_x");
		double errorLabelY = viewProperties.getDoubleProperty("error_label_y");
		canvasActions = new CanvasActions(canvasX, canvasY, canvasWidth, canvasHeight, canvasLayoutX, canvasLayoutY,
				errorLabelX, errorLabelY);
		return canvasActions.getPane();
	}

	private Node initializeSettingsController() {
		settingsController = new SettingsController(stage, viewProperties);
		settingsController.addObserver(this);
		return settingsController.getHBox();

	}

	private Node createPastCommandsListView() {
		ListView<String> pastCommandsListView = new ListView<String>();
		pastCommands = FXCollections.observableArrayList();
		pastCommandsListView.setItems(pastCommands);

		pastCommandsListView.setPrefWidth(viewProperties.getDoubleProperty("past_command_list_width"));
		pastCommandsListView.setPrefHeight(viewProperties.getDoubleProperty("past_command_list_height"));
		// handle user clicking on an value of the list
		pastCommandsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> ov, String old_val, String curCommand) {
				// not actually an error
				errorConsole.displayErrorMessage("Run: " + curCommand);
				// TODO: RUN COMMAND OF STRING
			}
		});

		return pastCommandsListView;
	}

	private Node createTableView() {
		TableView tableView = new TableView();
		TableColumn variables = new TableColumn("Environment\nVariables");
		TableColumn userDefinedCommands = new TableColumn("User-Defined\nCommands");
		tableView.getColumns().addAll(variables, userDefinedCommands);
		
		TableColumn variableNames = new TableColumn("Name");
		TableColumn variableValues = new TableColumn("Value");
		variables.getColumns().addAll(variableNames, variableValues);
        
		//tableView.setItems(data);
		userDefinedCommandNames = new TableColumn("Name");
		userDefinedCommandValues = new TableColumn("Value");
		
		userDefinedCommands.getColumns().addAll(userDefinedCommandNames, userDefinedCommandValues);
		
//		userDefinedCommandNames.setCellValueFactory((p)->{
//	        String[] x = getUserDefinedVariableNamesAndVars()[0];
//	        return new SimpleStringProperty(x != null && x.length>0 ? x[0] : "<no name>");
//		});
//
//		userDefinedCommandValues.setCellValueFactory((p)->{
//	        String[] x = p.getValue();
//	        return new SimpleStringProperty(x != null && x.length>1 ? x[1] : "<no value>");
//		});
		
		//updateUserDefinedVariableHashMap();
		
		// turtleVariables = FXCollections.observableArrayList();
//		pastCommandsListView.setItems(pastCommands);
//
//		pastCommandsListView.setPrefWidth(viewProperties.getDoubleProperty("past_command_list_width"));
//		pastCommandsListView.setPrefHeight(viewProperties.getDoubleProperty("past_command_list_height"));
//		pastCommandsListView.setLayoutX(viewProperties.getDoubleProperty("past_command_list_x"));
//		pastCommandsListView.setLayoutY(viewProperties.getDoubleProperty("past_command_list_y"));
//
//		// handle user clicking on an value of the list
//		pastCommandsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
//			public void changed(ObservableValue<? extends String> ov, String old_val, String curCommand) {
//				// not actually an error
//				errorConsole.displayErrorMessage("Run: " + curCommand);
//				// TODO: RUN COMMAND OF STRING
//			}
//		});

		return tableView;
	}
	
	//WHERE DO I CALL THIS METHOD SO IT UPDATES WHEN NECCESARY?
	private void updateUserDefinedVariableHashMap(){
		Map<String, String> myMap = dataSource.getUserDefinedVariables();
		for(String elem:myMap.keySet()){
			userDefinedCommandNames.getColumns().add(elem);
			userDefinedCommandValues.getColumns().add(myMap.get(elem));
		}
	}
	
	private String[][] getUserDefinedVariableNamesAndVars(HashMap<String, String> myMap){
		String[] userDefinedVars = new String[myMap.size()];
		String[] userDefinedNames = new String[myMap.size()];
		int i =0;
		for(String elem:myMap.keySet()){
			userDefinedVars[i] = elem;
			userDefinedNames[i] = myMap.get(elem);
			i++;
		}
		
		return new String[][]{userDefinedVars,userDefinedNames};
	}


	private Node createCommandInputter() {
		EventHandler<ActionEvent> runCommandHandler = event -> {
			String currentCommandLine = inputPanel.getCurrentCommandLine();
			if (!(currentCommandLine == null) && !(currentCommandLine.length() == 0)) {
				pastCommands.add(currentCommandLine);
				try {
					interpreter.parseInput(currentCommandLine);
				} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
						| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		inputPanel = new InputPanel(viewProperties, runCommandHandler);
		return inputPanel;
	}

	private Node createErrorConsole() {
		errorConsole = new ErrorConsole(viewProperties.getDoubleProperty("error_label_x"),
				viewProperties.getDoubleProperty("error_label_y"), viewProperties.getDoubleProperty("error_font_size"));
		return errorConsole.getErrorMessage();

	}

	// currently only observable this controller observes is settingsController
	// DOES THIS ACCOUNT FOR MY UPDATE THING TOO?
	public void update(Observable obs, Object o) {
		try {
			Method update = getClass().getMethod("update", obs.getClass(), Object.class);
			update.invoke(this, obs, o);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(MainController obs, Object o){
		canvasActions.removeTurtle();
		canvasActions.setShowTurtle(dataSource.getTurtleIsShowing());
		canvasActions.setHeading(turtleTranslator.convertAngle(dataSource.getAngle()));
		canvasActions.setPenDown(dataSource.getTurtleIsDrawing());
		canvasActions.setXandYLoc(turtleTranslator.convertXCordinate(dataSource.getXCoordinate()), turtleTranslator.convertYCordinate(dataSource.getYCoordinate()));
		canvasActions.addTurtleAtXY();
		canvasActions.drawPath(turtleTranslator.convertLineCordinates(dataSource.getLineCoordinates()));
	}

	public void update(SettingsController obs, Object o) {
		if (o != null) {
			errorConsole.displayErrorMessage(o.toString());
			return;
		}
		if (settingsController.getNewImage() != null)
			canvasActions.changeImage(settingsController.getNewImage(),dataSource.getXCoordinate(),dataSource.getYCoordinate());
		if (settingsController.getNewBackgroundColor() != null)
			canvasActions.setBackgroundColorCanvas(settingsController.getNewBackgroundColor());
		if (settingsController.getNewPenColor() != null)
			canvasActions.setPenColor(settingsController.getNewPenColor());
	}
	
	public void setDataSource(BoardStateDataSource source){
		this.dataSource = source;
	}
	
	public void setInterpreter(SlogoCommandInterpreter interpreter){
		this.interpreter = interpreter;
	}
}
