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

	String fileLocation;
	String destination_URL;
	List<File> java_files;
	
	public UMLParser(){
		
		java_files = new ArrayList<File>();
	}
	
	public void parseFile(String fileLocation, String destination_URL){
		
		this.fileLocation = fileLocation;
		this.destination_URL = destination_URL;
		getAllFilesWithJava(fileLocation);
			

	}
	
	
	public void  getAllFilesWithJava(String fileLocation2) {
		
		File directory = new File(fileLocation2);

		for(File file : directory.listFiles()){
			if(file.isFile() && file.getName().endsWith(".java")){
				System.out.println("File " + file.getName());
				java_files.add(file);
			}
		}
		
		System.out.println("Total number of files:"+java_files.size());
	
	}
}
