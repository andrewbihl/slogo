package interpreter;

import javafx.application.Application;
import javafx.stage.Stage;
import model.BoardStateController;

public class TestMain extends Application{
	
	String input = "fd 100 rt 144";
	String input2 = "repeat :potato [ fd 7 ]";
	String input3 = "repeat 5 [ fd 100 rt 144 ]";
//	String language = "Chinese";
	
	public static void main(String[] args){
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		BoardStateController controller = new BoardStateController();
		MainInterpreter main = new MainInterpreter();
		
		//controller is passed in three times because there are three different interfaces
		main.setStateDataSource(controller);
		main.setStateUpdater(controller);
		main.setVarDataSource(controller);
		
//		main.setLanguage(language);
		main.parseInput(input);  
//		main.parseInput(input2);  
//		main.parseInput(input3);  
	}
}
