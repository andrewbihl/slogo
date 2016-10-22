package interpreter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.ResourceBundle;

import regularExpression.ProgramParser;

public class MainInterpreter {
	
	private final String WHITESPACE = "\\p{Space}";
	private final String DEFAULT_RESOURCE_LANGUAGE = "resources/languages/";
	private final String DEFAULT_RESOURCE_PACKAGE = "resources/properties/";
	private final String PROPERTIES_TITLE = "Interpreter";
	private final String[] languages = {"Chinese","English","French","German","Italian",
			"Portuguese","Russian","Spanish","Syntax"};
	
	private SlogoUpdate model;
	private ResourceBundle rb;
	private String[] parsed;
	
	public MainInterpreter(){
		model = new SlogoUpdate();
		rb = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE+PROPERTIES_TITLE);
	}
	
	public void parseInput(String input) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		String[] split = input.split("\\s+");
		ProgramParser lang = new ProgramParser();
		lang = addPatterns(lang);
		parsed = createParsedArray(split, lang);
//		for(String elem: parsed){
//			System.out.println(elem);
//		}
		//split is the original input, parsed is the translated version (translated with ProgramParser)
		interpretCommand(split, parsed, 0);
	}
	
	private double interpretCommand(String[] input, String[] parsed, int searchStartIndex) throws ClassNotFoundException, NoSuchMethodException, 
	SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		GeneralInterpreter decideCommand = new GeneralInterpreter();
		String keyword = parsed[searchStartIndex].toLowerCase();
		
		if(decideCommand.isNonInputTurtleCommand(keyword) || decideCommand.isUnaryTurtleCommand(keyword) || 
				decideCommand.isBinaryTurtleCommand(keyword)){
			return interpretTurtleCommand(input, keyword, searchStartIndex);
		}
		
		else if(decideCommand.isNonInputMathExpression(keyword) || decideCommand.isUnaryMathExpression(keyword) || 
				decideCommand.isBinaryMathExpression(keyword)){
			return interpretMathCommand(input, keyword, searchStartIndex);
		}
		
		else if(decideCommand.isUnaryBooleanExpression(keyword) || decideCommand.isBinaryBooleanExpression(keyword)){
			return interpretBooleanCommand(input, keyword, searchStartIndex);
		}
		
		else{
			System.out.println("Invalid argument!");
			return 0;
		}
		
	}
	
	private double interpretTurtleCommand(String[] input, String keyword, int searchStartIndex) throws ClassNotFoundException, 
	InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, 
	IllegalArgumentException, InvocationTargetException{
		double[] param;
		Class interpreterClass = Class.forName(rb.getString("TurtleCommandInterpreterLabel"));
		Object obj = interpreterClass.newInstance();
		Class[] args;
		TurtleCommandInterpreter interpreter = new TurtleCommandInterpreter(model);
		
		if(interpreter.isNonInputTurtleCommand(keyword)){
			args = createDoubleArgs(0);
			Method method = interpreterClass.getDeclaredMethod(keyword, args);
			System.out.println(method.invoke(obj));
			double res =  (double) method.invoke(obj);
			model = interpreter.getModel();
			return res;
		}
		else if(interpreter.isUnaryTurtleCommand(keyword)){
			param = parseParam(input, searchStartIndex+1, 1);
			args = createDoubleArgs(1);
			Method method = interpreterClass.getDeclaredMethod(keyword, args);
			System.out.println(method.invoke(obj, param[0]));
			double res = (double) method.invoke(obj, param[0]);
			model = interpreter.getModel();
			return res;
		}
		else if(interpreter.isBinaryTurtleCommand(keyword)){
			param = parseParam(input, searchStartIndex+1, 2);
			args = createDoubleArgs(2);
			Method method = interpreterClass.getDeclaredMethod(keyword, args);
			System.out.println(method.invoke(obj, param[0], param[1]));
			double res = (double) method.invoke(obj, param[0], param[1]);
			model = interpreter.getModel();
			return res;
		}
		else throw new IllegalArgumentException();
	}
	
	private double interpretMathCommand(String[] input, String keyword, int searchStartIndex) throws ClassNotFoundException, InstantiationException, 
	IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException{
		double[] param;
		Class interpreterClass = Class.forName(rb.getString("MathInterpreterLabel"));
		Object obj = interpreterClass.newInstance();
		Class[] args;
		MathInterpreter interpreter = new MathInterpreter();
		
		if(interpreter.isNonInputMathExpression(keyword)){
			args = createDoubleArgs(0);
			Method method = interpreterClass.getDeclaredMethod(keyword, args);
			System.out.println(method.invoke(obj));
			return (double) method.invoke(obj);
		}
		
		else if(interpreter.isUnaryMathExpression(keyword)){
			param = parseParam(input, searchStartIndex+1, 1);
			args = createDoubleArgs(1);
			Method method = interpreterClass.getDeclaredMethod(keyword, args);
			System.out.println(method.invoke(obj, param[0]));
			return (double) method.invoke(obj, param[0]);
		}
		
		else if(interpreter.isBinaryMathExpression(keyword)){
			param = parseParam(input, searchStartIndex+1, 2);
			args = createDoubleArgs(2);
			Method method = interpreterClass.getDeclaredMethod(keyword, args);
			System.out.println(method.invoke(obj, param[0], param[1]));
			return (double) method.invoke(obj, param[0], param[1]);
		}
		else throw new IllegalArgumentException();
	}
	
	private double interpretBooleanCommand(String[] input, String keyword, int searchStartIndex) throws ClassNotFoundException, InstantiationException, 
	IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException{
		double[] param;
		Class interpreterClass = Class.forName(rb.getString("BooleanInterpreterLabel"));
		Object obj = interpreterClass.newInstance();
		Class[] args;
		BooleanInterpreter interpreter = new BooleanInterpreter();
		
		if(interpreter.isUnaryBooleanExpression(keyword)){
			param = parseParam(input, searchStartIndex+1, 1);
			args = createDoubleArgs(1);
			Method method = interpreterClass.getDeclaredMethod(keyword, args);
			System.out.println(method.invoke(obj, param[0]));
			return (double) method.invoke(obj, param[0]);
		}
		
		else if(interpreter.isBinaryBooleanExpression(keyword)){
			param = parseParam(input, searchStartIndex+1, 2); //last index is exclusive
			args = createDoubleArgs(2);
			Method method = interpreterClass.getDeclaredMethod(keyword, args);
			System.out.println(method.invoke(obj, param[0], param[1]));
			return (double) method.invoke(obj, param[0], param[1]);
		}
		else throw new IllegalArgumentException();
	}
	
	private double[] parseParam(String[] input, int startSearchIndex, int numOfParams) throws ClassNotFoundException, 
	NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, 
	IllegalArgumentException, InvocationTargetException{
		double[] res = new double[numOfParams];
		int index=0;
		for(int i=startSearchIndex;i<startSearchIndex+numOfParams;i++){
			if(isDouble(input[i])){
				double temp = Double.parseDouble(input[i]);
				res[index++] = temp;
			}
			
			//recursive part of parsing input statement
			else{
				res[index++] = interpretCommand(input, parsed, i);
			}
		}
		return res;
	}
	
	private String[] createParsedArray(String[] in, ProgramParser lang){
		String[] out = new String[in.length];
		for(int i=0;i<in.length;i++){
			out[i] = lang.getSymbol(in[i]);
		}
		return out;
	}
	
	private ProgramParser addPatterns(ProgramParser lang){
		for(String language:languages){
			lang.addPatterns(DEFAULT_RESOURCE_LANGUAGE+language);
		}
        return lang;
	}
	
	private Class[] createDoubleArgs(int num){
		Class[] args = new Class[num];
		for(int i=0;i<args.length;i++){
			args[i] = Double.TYPE;
		}
		return args;
	}

	private boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	
	public SlogoUpdate getModel(){
		return model;
	}
}
