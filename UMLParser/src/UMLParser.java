import java.util.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import com.github.javaparser.ast.Node;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.omg.CORBA.portable.InputStream;

import com.github.javaparser.*;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.JavaParser;




public class UMLParser {


	public void parseFile(String fileLocation){
		
		File location = new File(fileLocation);
		try{
			
			for(File javaFile : location.listFiles(java_Files)){
				FileInputStream inputStream = new FileInputStream(javaFile.getAbsolutePath());
				//passing object to javaparser
				 CompilationUnit unit = JavaParser.parse(inputStream);
				    List<Node> child_Nodes = unit.getChildrenNodes();
				    List<TypeDeclaration> loTd = unit.getTypes();

					JSONObject objintclass = new JSONObject();
					
					for (Node child : child_Nodes) {

						if (child instanceof ClassOrInterfaceDeclaration) {
							ClassOrInterfaceDeclaration cid = child_Nodes;

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

								//Modifying the method
								if (cid.getExtends() != null) {
									for (Node Extends : cid.getExtends()) {

										extend.put(Extends);
									}

									objintclass.put("extend", extend);

								}

								
								//Modifed the method
								if (cid.getImplements() != null) {
									for (Node Implements : cid.getImplements()) {
										implement.put(Implements);

									}
									objintclass.put("implements", implement);

								}

								

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
