package interpreter;

public class TurtleCommandInterpreter extends SubInterpreter{
	
	private SlogoUpdate model;
	
	TurtleCommandInterpreter(SlogoUpdate model){
		this.model = model;
	}
	
	double forward(double pixels){
		model.moveForward(pixels);
		return pixels;
	}
	
	double backward(double pixels){
		model.moveBackward(pixels);
		return pixels;
	}
	
	double left(double degrees){
		model.rotateCounterClockwise(convertAngle(degrees));
		return degrees;
	}
	
	double right(double degrees){
		model.rotateClockwise(convertAngle(degrees));
		return degrees;
	}
	
	double setheading(double degrees){
		double temp = model.getAngle();
		model.setAngle(convertAngle(degrees));
		return Math.abs(temp-convertAngle(degrees));
	}
	
	double settowards(double x, double y){
		double tempX = model.getXCoordinate();
		double tempY = model.getYCoordinate();
		model.turnToward(x, y);
		return angleBetweenTwoPoints(tempX, tempY, x, y);
	}
	
	double setxy(double x, double y){
		double tempX = model.getXCoordinate();
		double tempY = model.getYCoordinate();
		model.moveTo(x, y);
		return Math.abs(x-tempX) + Math.abs(y-tempY);
	}
	
	double penup(){
		model.putPenUp();
		return 1;
	}
	
	double pendown(){
		model.putPenDown();
		return 0;
	}
	
	double showturtle(){
		model.show();
		return 1;
	}
	
	double hideturtle(){
		model.hide();
		return 0;
	}
	
	double home(){
		double tempX = model.getXCoordinate();
		double tempY = model.getYCoordinate();
		model.moveTo(0, 0);
		return Math.abs(tempX) + Math.abs(tempY);
	}
	
	
	double clearScreen(){
		//TODO: how do I erase the turtle trail?
		double tempX = model.getXCoordinate();
		double tempY = model.getYCoordinate();
		model.moveTo(0, 0);
		return Math.abs(tempX) + Math.abs(tempY);
	}
	
	/**
	 * Calculated the angle created by two points and the origin(0,0).
	 * @author Ray Song (ys101)
	 */
	private double angleBetweenTwoPoints(double point1X, double point1Y, 
	        double point2X, double point2Y) {
	    double angle1 = Math.atan2(point1Y, point1X);
	    double angle2 = Math.atan2(point2Y, point2X);
	    return Math.toDegrees(angle1 - angle2); 
	}
	
	SlogoUpdate getModel(){
		return model;
	}

}
