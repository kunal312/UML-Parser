
public class UMLParserMain {
	
	private static String fileLocation = null;
	public static String destination_URL = null;
	
	public static void main(String[] args) throws Exception{
		
		fileLocation = args[0];
		destination_URL = fileLocation+"/"+args[1]+".png";
		System.out.println("Command 1 Java Files Location: "+fileLocation);
		System.out.println("Command 2 Dest UrL: "+destination_URL);

		if(fileLocation.endsWith("sequence")){
			System.out.println("gotcha!");
		}
		else{

			//calling parse to generate class diagram file with proper arguments
			UMLParser parser = new UMLParser();
			String grammar = parser.parseFile(fileLocation,destination_URL);
			System.out.println("Final Grammar : "+grammar);
			MakeUML mu = new MakeUML();
			mu.makeUML(grammar, destination_URL);
		}



		
		
	}
	

}
