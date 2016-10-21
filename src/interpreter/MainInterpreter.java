package interpreter;

import java.util.Arrays;

import regularExpression.ProgramParser;
import slogo_update.slogoUpdate;

public class MainInterpreter {
	
	final String WHITESPACE = "\\p{Space}";
	
	private slogoUpdate model;
//	private SubInterpreter interpreter;
	
	public MainInterpreter(){
		model = new slogoUpdate();
	}
	
	public void parseInput(String input){
		String[] split = input.split("\\s+");
		ProgramParser lang = new ProgramParser();
		lang = addPatterns(lang);
		
		String[] parsed = createParsedArray(split, lang);
		interpretCommand(parsed);
	}
	
	public void interpretCommand(String[] input){
		//use reflection to decide which one to call
		
		String keyword = input[0].toLowerCase();
		
		if(keyword.equals("sum")){
			MathInterpreter mi = new MathInterpreter();
			double[] param = parseParam(Arrays.copyOfRange(input, 1, 3)); //last index is exclusive
			System.out.println(mi.sum(param[0],param[1]));
		}
		
		//parse command, get number of Parameters. 
		int numberOfParameters = input.length-1;
		for (int i = 1; i <= numberOfParameters; i++){  //Handle case of not ENOUGH parameters
			String s = input[i];
			if(isDouble(s)){
				double param = Double.parseDouble(s);
			}
			else{
				String[] substring = new String[0];
				interpretCommand(substring);
			}	
					
		}
		
		
	}
	
	public double[] parseParam(String[] params){
		double[] res = new double[params.length];
		int index = 0;
		for(String elem: params){
			if(isDouble(elem)){
				double temp = Double.parseDouble(elem);
				res[index++] = temp;
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
		lang.addPatterns("resources/languages/English");
        lang.addPatterns("resources/languages/Syntax");
        return lang;
	}

	private boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public slogoUpdate getModel(){
		return model;
	}
}
