package gui_components;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;

public abstract class FileChooser {
	private ComboBox<String> fileSelect;
	private String selectedFilename;
	//private String myCommandLine;
	private ReadCommandFileInterface myInterface;

	public FileChooser(ReadCommandFileInterface myInterface) {
		
		createComboBox(initVal, filePath);
		
			this.myInterface = myInterface;
			fileSelect = new ComboBox<>();
			fileSelect.setPrefWidth(120);
			fileSelect.setVisibleRowCount(3);
			fileSelect.setValue("Command file");
			File dataDirectory = new File("data/examples/simple/");
			File[] dataFiles = dataDirectory.listFiles();
			for (File file : dataFiles) {
				fileSelect.getItems().add(file.getName());
			}
			selectedFilename = null;
			fileSelect.valueProperty().addListener((observable, oldValue, newValue) -> {
				selectedFilename = newValue;
				myCommandLine = "";
				readFile();
	        });
		}
	
		private void createComboBox(String initVal, String filePath){
			
		}

		private void readFile() {
			File file = new File("data/examples/simple/" +selectedFilename);
			try {
				Scanner sc = new Scanner(file);
				while (sc.hasNextLine()) {
					myCommandLine += " " + sc.nextLine();
				}
				myInterface.getCommandLineFromFile(myCommandLine);
				sc.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		public Node getCommandFileUploaderButton() {
			return fileSelect;
		}
	}


