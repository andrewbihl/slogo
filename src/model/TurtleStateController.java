package model;
import interpreter.TurtleQueryDataSource;

public class TurtleStateController implements TurtleQueryDataSource{

	
	
	
/*
 * TurtleQueryDataSource interface methods
 */
	@Override
	public double getXCoordinate() {
		return TurtleState.getCurrentState().xCoordinate;
	}

	@Override
	public double getYCoordinate() {
		return TurtleState.getCurrentState().yCoordinate;
	}

	@Override
	public double getAngle() {
		return TurtleState.getCurrentState().angle;
	}

	@Override
	public boolean getTurtleIsShowing() {
		return TurtleState.getCurrentState().turtleIsShowing;
	}

	@Override
	public boolean getPenIsDown() {
		return TurtleState.getCurrentState().penIsDown;
	}
	
}