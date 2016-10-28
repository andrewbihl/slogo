package gui_components;

import java.util.Observable;

import general.NewSlogoInstanceCreator;
import general.Properties;
import gui.FileChooserPath;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GeneralSettingsController extends Observable implements ReadCommandFileInterface {
	private Properties viewProperties;
	private HBox hBox;
	private boolean newTab;

	private Image newImage;
	private String newCommandString; 
	private NewSlogoInstanceCreator instanceCreator;

	public GeneralSettingsController(Properties viewProperties, NewSlogoInstanceCreator instanceCreator) {
		this.instanceCreator = instanceCreator;
		this.viewProperties = viewProperties;
		
		VBox vBoxLeft = new VBox(viewProperties.getDoubleProperty("padding"));
		vBoxLeft.getChildren().add(initializeUndoButton());
		vBoxLeft.getChildren().add(initalizeFileLoader());
		vBoxLeft.getChildren().add(initalizeCommandFileLoader());
		
		VBox vBoxRight = new VBox(viewProperties.getDoubleProperty("padding"));
		vBoxRight.getChildren().add(initializeAddTabButton());
		vBoxRight.getChildren().add(initializeGetHelpButton());

		hBox = new HBox(viewProperties.getDoubleProperty("padding"));
		hBox.getChildren().addAll(vBoxLeft,vBoxRight);
	}
	
	private Node initializeAddTabButton(){
		Button addTab = createButton("Add Tab", viewProperties.getDoubleProperty("help_button_width"));
		addTab.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				instanceCreator.addSlogoInstance();
			}
		});
		return addTab;
	}

	private Node initalizeCommandFileLoader() {
		CommandFileUploader myUploader = new CommandFileUploader();
		return myUploader.getCommandFileUploaderButton();
	}

	private Node initalizeFileLoader() {
		ComboBox<String> fileLoaderComboBox = new ComboBox<String>();
		fileLoaderComboBox.setVisibleRowCount(3);
		fileLoaderComboBox.getItems().addAll("test.xml");
		fileLoaderComboBox.setValue("Pick file");

		fileLoaderComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String t, String t1) {
				if (t1 != null) {
					setChanged();
					
					notifyObservers();
				}
			}
		});
		return fileLoaderComboBox;
	}

	private Node initializeUndoButton() {
		Button undoButton = createButton("Undo", viewProperties.getDoubleProperty("help_button_width"));
		undoButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				setChanged();
				System.out.println("UNDO ACTION");
				notifyObservers();
			}
		});
		return undoButton;
	}

	private Node initializeGetHelpButton() {
		Button helpButton = createButton("Get help!", viewProperties.getDoubleProperty("help_button_width"));
		helpButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				BrowserView myView = new BrowserView(new Stage(),viewProperties.getDoubleProperty("help_width"), viewProperties.getDoubleProperty("help_height"));
			}
		});

		return helpButton;
	}

	private Button createButton(String text, double width) {
		Button button = new Button(text);
		button.setPrefWidth(width);
		return button;
	}

	public Node getNode() {
		return hBox;
	}

	public Image getNewImage() {
		return newImage;
	}

	@Override
	public void getCommandLineFromFile(String myCommand) {
		setChanged();
		newCommandString = myCommand;
		notifyObservers();
	}

}
