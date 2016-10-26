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
import gui_components.ErrorConsole;
import gui_components.InputPanel;
import gui_components.PenSettingsController;
import gui_components.SettingsController;
import gui_components.TitleBox;
import gui_components.UserDefinedCommand;
import gui_components.WorkspaceSettingsController;
import interpreter.ErrorPresenter;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
public class ViewController implements Observer, ErrorPresenter {
	private Properties viewProperties;

	private static final String VIEW_PROPERTIES_PACKAGE = "resources.properties/";
	private static final Paint BACKGROUND_COLOR_SCENE = Color.ALICEBLUE;

	private static final double HIDE_SHOW_BUTTON_WIDTH = 140;
	private Group sceneRoot;
	private TitleBox titleBox;
	private InputPanel inputPanel;
	private ObservableList<String> pastCommands;
	private CanvasActions canvasActions;
	private SettingsController settingsController;
	private WorkspaceSettingsController workspaceSettingsController;
	private PenSettingsController penSettingsController;
	private Stage stage;
	private ErrorConsole errorConsole;
	private BoardStateDataSource modelController;
	private SlogoCommandInterpreter interpreter;
	private TurtleDataTranslator turtleTranslator;
	private ListView<String> pastCommandsListView;

	// private TableColumn userDefinedCommandNames;
	// private TableColumn userDefinedCommandValues;
	// private TableColumn variableNames;
	// private TableColumn variableValues;

	TableView<Variable> variableTableView;
	TableView<UserDefinedCommand> userDefinedTableView;
	
	public double getImageWidth(){
		return viewProperties.getDoubleProperty("image_width");
	}
	
	
	public double getImageHeight(){
		return viewProperties.getDoubleProperty("image_height");
	}
	
	public ViewController(Stage stage) {
		viewProperties = new Properties(VIEW_PROPERTIES_PACKAGE + "View");
		sceneRoot = new Group();
		turtleTranslator = new TurtleDataTranslator(viewProperties.getDoubleProperty("canvas_width"),
				viewProperties.getDoubleProperty("canvas_height"), getImageWidth(), getImageHeight());
		sceneRoot.getChildren().add(setupBoxes());
		setupStage(stage);
		sceneRoot.setOnKeyPressed(e -> handleKeyInput(e.getCode()));
	}

	private Node setupBoxes() {
		VBox box1 = new VBox(15);
		HBox box2 = new HBox(15);
		VBox box3 = new VBox(15);
		HBox box4 = new HBox(15);

		box1.setPadding(new Insets(15, 15, 15, 15));
		box1.getChildren().add(createTitleBox());
		box1.getChildren().add(box2);
		box1.getChildren().add(box4);
		box1.getChildren().add(createErrorConsole());

		box2.getChildren().add(box3);
		box2.getChildren().add(createPastCommandsListView());
		box2.getChildren().add(createVariableTableView());
		box2.getChildren().add(createUserDefinedTableView());

		box3.getChildren().add(createCanvas());
		box3.getChildren().add(createCommandInputter());
	    //HBox.setHgrow(box2, Priority.ALWAYS);
		
		Node settingsController =initializeSettingsController();
		Node penSettingsController = initializePenSettingsController();
		Node workspaceSettingsController = initializeWorkspaceSettingsController();
		box4.getChildren().add(createViewSelector());
		box4.getChildren().add(penSettingsController);
		box4.getChildren().add(workspaceSettingsController);
		box4.getChildren().add(settingsController);
		
		return box1;
	}

	
	private void setupStage(Stage stage) {
		double appWidth = viewProperties.getDoubleProperty("app_width");
		double appHeight = viewProperties.getDoubleProperty("app_height");
		Scene scene = new Scene(sceneRoot, appWidth, appHeight, BACKGROUND_COLOR_SCENE);
		stage.setTitle(viewProperties.getStringProperty("title"));
		stage.setScene(scene);
		stage.show();
	}

	private Node createViewSelector(){
		//initialise buttons
		VBox hideShowButtons = new VBox(2);
		Button historicCommands = createButton("Hide history", HIDE_SHOW_BUTTON_WIDTH);
		Button variables = createButton("Hide variales",HIDE_SHOW_BUTTON_WIDTH);
		Button userVariables = createButton("Hide user variables",HIDE_SHOW_BUTTON_WIDTH);
		Button drawSettings = createButton("Hide settings",HIDE_SHOW_BUTTON_WIDTH);
		
		//initialise button actions
		hideShowButtonActions(historicCommands, pastCommandsListView, "Hide history", "Show history");
		hideShowButtonActions(variables, variableTableView, "Hide variales", "Show variales");
		hideShowButtonActions(userVariables, userDefinedTableView, "Hide user variables", "Show user variables");
		hideShowButtonActions(drawSettings, settingsController.getHBox(), "Hide settings", "Show settings");

		hideShowButtons.getChildren().addAll(historicCommands,userVariables,variables,drawSettings);
		return hideShowButtons;
	}


	private void hideShowButtonActions(Button button, Node view, String hide, String show) {
		button.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent e) {
				if(button.getText().equals(hide)){
					button.setText(show);
					view.setVisible(false);	
				}
				else{
					button.setText(hide);
					view.setVisible(true);
				}
	        }
		});
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
		double canvasWidth = viewProperties.getDoubleProperty("canvas_width");
		double canvasHeight = viewProperties.getDoubleProperty("canvas_height");
		canvasActions = new CanvasActions(canvasWidth, canvasHeight, getImageWidth(), getImageHeight());
		return canvasActions.getPane();
	}

	private Node initializeSettingsController() {
		settingsController = new SettingsController(stage, viewProperties);
		settingsController.addObserver(this);
		return settingsController.getHBox();
	}
	
	private Node initializeWorkspaceSettingsController() {
		workspaceSettingsController = new WorkspaceSettingsController(stage, viewProperties);
		workspaceSettingsController.addObserver(this);
		return workspaceSettingsController.getVBox();
	}
	
	private Node initializePenSettingsController() {
		penSettingsController = new PenSettingsController(stage, viewProperties);
		penSettingsController.addObserver(this);
		return penSettingsController.getVBox();
	}

	private void handleKeyInput(KeyCode code) {
		if (code == KeyCode.ENTER){
			runCommandFromInputLine();
		}
	}

	private Node createPastCommandsListView() {
		pastCommandsListView = new ListView<String>();
		pastCommandsListView.managedProperty().bind(pastCommandsListView.visibleProperty());
		pastCommands = FXCollections.observableArrayList();
		pastCommandsListView.setItems(pastCommands);

		pastCommandsListView.setPrefWidth(viewProperties.getDoubleProperty("past_command_list_width"));
		pastCommandsListView.setPrefHeight(viewProperties.getDoubleProperty("past_command_list_height"));
		// handle user clicking on an value of the list
		pastCommandsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> ov, String old_val, String curCommand) {
				runHistoricCommand(curCommand);
			}
		});

		return pastCommandsListView;
	}

	private Node createVariableTableView() {
		variableTableView = new TableView<Variable>();
		variableTableView.setEditable(true);
		variableTableView.managedProperty().bind(variableTableView.visibleProperty());

		TableColumn<Variable, String> variables = new TableColumn<Variable, String>("Variables");
		variables.setEditable(true);

		TableColumn<Variable, String> variableNames = new TableColumn<Variable, String>("Name");
		variableNames.setCellValueFactory(new PropertyValueFactory<Variable, String>("name"));
		TableColumn<Variable, String> variableValues = new TableColumn<Variable, String>("Value");
		variableValues.setCellValueFactory(new PropertyValueFactory<Variable, String>("value"));
		variableValues.setEditable(true);

		variables.getColumns().addAll(variableNames, variableValues);

		variableTableView.getColumns().add(variables);
		variableTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		return variableTableView;
	}

	private Node createUserDefinedTableView() {
		userDefinedTableView = new TableView<UserDefinedCommand>();
		TableColumn<UserDefinedCommand, String> userDefinedCommands = new TableColumn<UserDefinedCommand, String>(
				"User-Defined\nVariables");

		// tableView.setItems(data);
		TableColumn<UserDefinedCommand, String> userDefinedCommandNames = new TableColumn<UserDefinedCommand, String>(

				"Name");
		userDefinedCommandNames.setCellValueFactory(new PropertyValueFactory<UserDefinedCommand, String>("name"));
		TableColumn<UserDefinedCommand, String> userDefinedCommandValues = new TableColumn<UserDefinedCommand, String>(
				"Value");
		userDefinedCommandValues.setCellValueFactory(new PropertyValueFactory<UserDefinedCommand, String>("value"));
		userDefinedCommandValues.setEditable(true);
		userDefinedCommands.getColumns().addAll(userDefinedCommandNames, userDefinedCommandValues);

		userDefinedTableView.getColumns().add(userDefinedCommands);
		userDefinedTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		return userDefinedTableView;
	}

	private Node createCommandInputter() {
		EventHandler<ActionEvent> runCommandHandler = event -> {			
			runCommandFromInputLine();	
		};
		
		double inputPanelHeight = viewProperties.getDoubleProperty("input_panel_height");
		double textFieldWidth = viewProperties.getDoubleProperty("text_field_width");
		double runButtonWidth = viewProperties.getDoubleProperty("run_button_width");

		inputPanel = new InputPanel(inputPanelHeight, textFieldWidth, runButtonWidth, viewProperties,
				runCommandHandler);
		return inputPanel;
	}

	
	private Button createButton(String text, double width) {
		Button button = new Button(text);
		button.setPrefWidth(width);
		return button;
	}

	private void runHistoricCommand(String curCommand) {
		String currentCommandLine = curCommand;
		runCommand(currentCommandLine);
	}
	
	private void runCommandFromInputLine() {
		String currentCommandLine = inputPanel.getText();
		inputPanel.clear();
		runCommand(currentCommandLine);
	}

	private void runCommand(String currentCommandLine) {
		//String currentCommandLine = inputPanel.getCurrentCommandLine();
		if (!(currentCommandLine == null) && !(currentCommandLine.length() == 0)) {
			pastCommands.add(currentCommandLine);
			try {
				interpreter.parseInput(currentCommandLine);
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private Node createErrorConsole() {
		errorConsole = new ErrorConsole(viewProperties.getDoubleProperty("error_font_size"));
		return errorConsole.getErrorMessage();

	}

	// currently only observable this controller observes is settingsController
	// DOES THIS ACCOUNT FOR MY UPDATE THING TOO?
	public void update(Observable obs, Object o) {
		try {
			Method update;
			if (o != null) {
				for (Class c : o.getClass().getInterfaces()) {
					if (c.equals(BoardStateDataSource.class)) {
						update = getClass().getMethod("update", Object.class, BoardStateDataSource.class);
						update.invoke(this, obs, o);
					}
				}
			} else {
				update = getClass().getMethod("update", obs.getClass(), Object.class);
				update.invoke(this, obs, o);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void presentError(String errorMessage){
		errorConsole.displayErrorMessage(errorMessage);
	}

	public void update(Object obs, BoardStateDataSource o) {
		canvasActions.removeTurtle();
		canvasActions.setShowTurtle(modelController.getTurtleIsShowing());
		canvasActions.setHeading(turtleTranslator.convertAngle(modelController.getAngle()));
		canvasActions.setPenDown(modelController.getTurtleIsDrawing());
		canvasActions.setXandYLoc(turtleTranslator.convertXImageCordinate(modelController.getXCoordinate()),
				turtleTranslator.convertYImageCordinate(modelController.getYCoordinate()));
		canvasActions.addTurtleAtXY();
		canvasActions.drawPath(turtleTranslator.convertLineCordinates(modelController.getLineCoordinates()));
		updateVariables();

	}

	public void update(SettingsController obs, Object o) {
		if (o != null) {
			errorConsole.displayErrorMessage(o.toString());
			return;
		}
		if (settingsController.getNewImage() != null)
			canvasActions.changeImage(settingsController.getNewImage(), modelController.getXCoordinate(),
					modelController.getYCoordinate());
		if (settingsController.getNewBackgroundColor() != null)
			canvasActions.setBackgroundColorCanvas(settingsController.getNewBackgroundColor());
		if (settingsController.getNewLanguage() != null)
			interpreter.setLanguage(settingsController.getNewLanguage());
	}
	
	public void update(WorkspaceSettingsController obs, Object o) {
		if (workspaceSettingsController.getNewBackgroundColor() != null)
			canvasActions.setBackgroundColorCanvas(workspaceSettingsController.getNewBackgroundColor());
		if (workspaceSettingsController.getNewLanguage() != null)
			interpreter.setLanguage(workspaceSettingsController.getNewLanguage());
	}
	
	public void update(PenSettingsController obvs, Object o){
		if (o != null) {
			errorConsole.displayErrorMessage(o.toString());
			return;
		}
		if (penSettingsController.getNewPenColor() != null)
			canvasActions.setPenColor(penSettingsController.getNewPenColor());
		if (penSettingsController.getNewPenType() != null)
			canvasActions.setPenType(penSettingsController.getNewPenType());
		if(penSettingsController.getNewPenThickness() != 0)
			canvasActions.setPenThickness(penSettingsController.getNewPenThickness());
	}

	public void setModelController(BoardStateDataSource modelController) {
		this.modelController = modelController;
		updateVariables();
	}

	private void updateVariables() {

		ObservableList<Variable> variableList = createVariablesList();
		variableTableView.setItems(variableList);

		ObservableList<UserDefinedCommand> userDefinedVariableList = createUserDefinedCommandsList();
		userDefinedTableView.setItems(userDefinedVariableList);

	}

	private ObservableList<Variable> createVariablesList() {

		ObservableList<Variable> data = FXCollections.observableArrayList();
		data.add(new Variable("X Coordinate", Double.toString(modelController.getXCoordinate())));
		data.add(new Variable("Y Coordinate", Double.toString(modelController.getYCoordinate())));
		data.add(new Variable("Angle", Double.toString(modelController.getAngle())));
		data.add(new Variable("Turtle is Showing", Boolean.toString(modelController.getTurtleIsShowing())));
		data.add(new Variable("Pen is Down", Boolean.toString(modelController.getTurtleIsDrawing())));

		// add an empty line for separation between environment and user defined
		// variables
		data.add(new Variable("", ""));

		Map<String, String> map = modelController.getUserDefinedVariables();

		for (String s : map.keySet()) {
			if (s.charAt(0) == ':')
				data.add(new Variable(s.substring(1), map.get(s)));
		}
		return data;
	}

	private ObservableList<UserDefinedCommand> createUserDefinedCommandsList() {
		Map<String, String> map = modelController.getUserDefinedVariables();

		ObservableList<UserDefinedCommand> data = FXCollections.observableArrayList();
		for (String s : map.keySet()) {
			if (s.charAt(0) != ':')
				data.add(new UserDefinedCommand(s.substring(1), map.get(s)));
		}
		return data;

	}

	public void setInterpreter(SlogoCommandInterpreter interpreter) {
		this.interpreter = interpreter;
	}
}
