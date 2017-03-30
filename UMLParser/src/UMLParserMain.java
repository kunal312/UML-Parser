
public class UMLParserMain {
	
	private static String fileLocation = null;
	public static String destination_URL = null;
	
	public static void main(String[] args){
		
		fileLocation = args[0];
		destination_URL = args[1];
		
		System.out.println("Command 1 Java File Location: "+fileLocation);
		System.out.println("Command 2 Dest UrL: "+destination_URL);
		

		//calling parse file with proper arguments
		UMLParser parser = new UMLParser();
		parser.parseFile(fileLocation,destination_URL);
		
		
		//Testing UML Class diagram generator with stubbed data.After completion of parsing file logic send grammar as an arguments to below function.
		MakeUML mu = new MakeUML();
		String parseString = "[Customer|-forname:string;surname:string|doShiz()]<>-orders*>[Order], [Order]++-0..*>[LineItem], [Order]-[note:Aggregate root{bg:wheat}]";
		String dst_Path = "test_diagram255657654.jpg";
		mu.makeUML(parseString, dst_Path);
		
		
	}
	

}
