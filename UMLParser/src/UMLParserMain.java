
public class UMLParserMain {
	
	public static void main(String[] args){
		
		MakeUML mu = new MakeUML();
		String parseString = "[Customer|-forname:string;surname:string|doShiz()]<>-orders*>[Order], [Order]++-0..*>[LineItem], [Order]-[note:Aggregate root{bg:wheat}]";
		String dst_Path = "test_diagram1234.jpg";
		mu.makeUML(parseString, dst_Path);
	}
	

}
