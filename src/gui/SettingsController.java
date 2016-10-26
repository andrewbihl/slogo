package gui;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

import general.Properties;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SettingsController extends Observable {

	private static final String IMAGE_PATH = "resources/images/";

	private static final int PADDING = 4;

	private static final double MIN_THICKNESS = 1;
	private static final double MAX_THICKNESS = 10;
	private static final double INIT_THICKNESS = 2;


	private Properties viewProperties;
	private HBox hBox;
	private Button imageButton;

	private Color newBackgroundColor;
	private Color newPenColor;
	private Image newImage;
	private String newLanguage;
	private Stage stage;
	private double newPenThickness;
	private String newPenType;

	public SettingsController(Stage myStage, Properties viewProperties) {
		stage = myStage;
		this.viewProperties = viewProperties;
		hBox = new HBox(viewProperties.getDoubleProperty("padding"));
		hBox.getChildren().add(initializePenColorSetting());
		hBox.getChildren().add(initializePenThicknessSetting());
		hBox.getChildren().add(initializePenTypeSetting());
		hBox.getChildren().add(initializeBackgroundColorSetting());
		hBox.getChildren().add(initializeLanguageSetting());
		hBox.getChildren().add(initializeTurtleImageSetting(stage));
		hBox.getChildren().add(initializeGetHelpButton());
	}
	
	private Node initializePenTypeSetting() {
		//TitledPane titledPane = new TitledPane();
		//titledPane.setRotate(270);
		ComboBox<String> penTypesComboBox = new ComboBox<String>();
		penTypesComboBox.setVisibleRowCount(3);
		penTypesComboBox.getItems().addAll("solid", "dashed", "dotted");
		penTypesComboBox.setValue("Pen type");

		penTypesComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String t, String t1) {
				if(t1!=null){
				setChanged();
				newPenType = t1;
				notifyObservers();
				}
			}
		});
		//titledPane .setContent(penTypesComboBox);
		return penTypesComboBox;
	}

	private Node initializeGetHelpButton() {
		Button helpButton = createButton("Get help!", viewProperties.getDoubleProperty("help_button_width"));
		helpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            BrowserView myView = new BrowserView(new Stage());
            }
        });

		return helpButton;
	}

	private Node initializePenColorSetting() {
		ColorPicker penColorPicker = new ColorPicker();
		penColorPicker.setValue(Color.BLACK);
		penColorPicker.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent t) {
				setChanged();
				newPenColor = penColorPicker.getValue();
				notifyObservers();
			}
		});
		VBox tempBox = makeLabel(penColorPicker, "Pen color");
		return tempBox;
	}
	
	private Node initializePenThicknessSetting(){
		Slider thicknessSlider = new Slider();
		thicknessSlider.setMin(MIN_THICKNESS);
		thicknessSlider.setMax(MAX_THICKNESS);
		thicknessSlider.setValue(INIT_THICKNESS);
		
		thicknessSlider.valueProperty().addListener(new ChangeListener<Number>(){
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val){
				setChanged();
				newPenThickness = new_val.doubleValue();
				notifyObservers();
			}
		});
		VBox tempBox = makeLabel(thicknessSlider, "Pen thickness");
		return tempBox;
	}

	private Node initializeBackgroundColorSetting() {
		ColorPicker backgroundColorPicker = new ColorPicker();
		backgroundColorPicker.setValue(Color.WHITE);
		backgroundColorPicker.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent t) {
				setChanged();
				newBackgroundColor = backgroundColorPicker.getValue();
				notifyObservers();
			}
		});
		VBox tempBox = makeLabel(backgroundColorPicker, "Background color");
		return tempBox;
	}


	private Node initializeLanguageSetting() {
		ComboBox<String> languageSelect = new ComboBox<String>();
		languageSelect.setVisibleRowCount(3);
		String rawLanguage = viewProperties.getStringProperty("languages");
		rawLanguage = rawLanguage.replaceAll("\\s","");
		List<String> languages = Arrays.asList(rawLanguage.split(","));
		languageSelect.getItems().addAll(languages);
		languageSelect.setValue("English");
		languageSelect.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String t, String t1) {
				setChanged();
				newLanguage = languageSelect.getValue();
				notifyObservers();
			}
		});
		VBox tempBox = makeLabel(languageSelect, "Set language");
		return tempBox;
	}

	private Node initializeTurtleImageSetting(Stage stage) {
		ComboBox<String> shapesComboBox = new ComboBox<String>();
		shapesComboBox.setVisibleRowCount(3);
		shapesComboBox.getItems().addAll("elephant", "turtle", "pig", "frog");
		shapesComboBox.setValue("Change Shape");

		shapesComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue ov, String t, String t1) {
				if(t1!=null){
				setChanged();
				Image image = ViewImageChooser.selectImage(IMAGE_PATH+t1+".png", 50, 50);
				newImage = image;
				notifyObservers();
				}
			}
		});
		return shapesComboBox;

	}
	
	private VBox makeLabel(Node backgroundColorPicker, String text) {
		Label lbl = new Label();
		lbl.setText(text);
		VBox tempBox = new VBox(PADDING);
		tempBox.getChildren().addAll(backgroundColorPicker,lbl);
		return tempBox;
	}


	private Button createButton(String text, double width) {
		Button button = new Button(text);
		button.setPrefWidth(width);
		return button;
	}

	public HBox getHBox() {
		return hBox;
	}

	public Color getNewBackgroundColor() {
		return newBackgroundColor;
	}

	public Color getNewPenColor() {
		return newPenColor;
	}
	
	public String getNewLanguage(){
		return newLanguage;
	}

	public Image getNewImage() {
		return newImage;
	}

	public double getNewPenThickness() {
		return newPenThickness;
	}
	
	public String getNewPenType(){
		return newPenType;
	}

}
