import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;


public class UMLParser {

	private static String fileLocation = null;
	
	public static void main(String[] args){
		
		fileLocation = args[0];
		System.out.println("Command 1 Java File Location: "+fileLocation);
		UMLParser parser = new UMLParser();
		parser.parseFile();
		
	}
	
	
	public void parseFile(){
		
		File location = new File(fileLocation);
		try{
			
			for(File javaFile : location.listFiles(javaFiles)){
				FileInputStream inputStream = new FileInputStream(javaFile.getAbsolutePath());
				//Import javaparser and pass input stream to javaparser library
				
			}
			
			

			
		}catch(Exception e){
			
		}
	}
	
	FileFilter javaFiles = new FileFilter() {
		public boolean accept(File file) {
			if (file.isDirectory()) {
				return true; 
			}
			//returns file with extension .java
			
			return file.getName().endsWith(".java"); 
		}
	};
	
	
}
