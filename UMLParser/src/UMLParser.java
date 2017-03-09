import java.util.*;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import com.github.javaparser.ast.Node;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.omg.CORBA.portable.InputStream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;



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
			
			for(File javaFile : location.listFiles(java_Files)){
				FileInputStream inputStream = new FileInputStream(javaFile.getAbsolutePath());
				//Import javaparser and pass input stream to javaparser library
				 CompilationUnit unit = JavaParser.parse(inputStream);
				    List<Node> cuChildNodes = unit.getChildNodes();
					List<TypeDeclaration> listOfTypeDeclarations = unit.getTypes();

					JSONObject objintclass = new JSONObject();
					
					for (Node cuChildNode : cuChildNodes) {

						if (cuChildNode instanceof ClassOrInterfaceDeclaration) {
							ClassOrInterfaceDeclaration cid = (ClassOrInterfaceDeclaration) cuChildNode;

							JSONArray method = new JSONArray();
							JSONArray extend = new JSONArray();
							JSONArray field = new JSONArray();
							JSONArray selfstruct = new JSONArray();
							JSONArray implement = new JSONArray();
							JSONArray usesstruct = new JSONArray();
							JSONArray usesStruct = new JSONArray();
							JSONArray association = new JSONArray();

							if (cid.toString().contains("interface")) {

								objintclass.put("CIName", cid.getName());
								objintclass.put("CIType", "Interface");
								objintclass.put("Fields", field);
								objintclass.put("Methods", method);
								objintclass.put("extend", extend);
								objintclass.put("implements", implement);
								objintclass.put("selfstruct", selfstruct);
								objintclass.put("usesStruct", usesstruct);
								objintclass.put("Association", association);

							} else {

								objintclass.put("CIName", cid.getName());
								objintclass.put("CIType", "Class");
								objintclass.put("Fields", field);
								objintclass.put("Methods", method);
								objintclass.put("extend", extend);
								objintclass.put("implements", implement);
								objintclass.put("selfstruct", selfstruct);
								objintclass.put("usesStruct", usesstruct);
								objintclass.put("Association", association);

								if (cid.getExtends() != null) {
									for (Node Extends : cid.getExtends()) {

										extend.put(Extends);
									}

									objintclass.put("extend", extend);

								}

								if (cid.getImplements() != null) {
									for (Node Implements : cid.getImplements()) {
										implement.put(Implements);

									}
									objintclass.put("implements", implement);

								}

								// objclass//push to Main Array

							}
						}
					}

				
			}
			
			

			
		}catch(Exception e){
			
		}
	}
	
	FileFilter java_Files = new FileFilter() {
		public boolean accept(File file) {
			if (file.isDirectory()) {
				return true; 
			}
			//returns file with extension .java
			
			return file.getName().endsWith(".java"); 
		}
	};
	
	
}
