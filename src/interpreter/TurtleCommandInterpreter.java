package interpreter;

import slogo_update.slogoUpdate;

public class TurtleCommandInterpreter {

	private slogoUpdate model;
	
	TurtleCommandInterpreter(slogoUpdate model){
		this.model = model;
	}
	
	double forward(double pixels){
		return pixels;
	}
	
	
	double clearScreen(){
		//erase trail of the turtle
		//return total distance travelled
		return 0;
	}
	
	slogoUpdate getModel(){
		return model;
	}
	
}
