import java.util.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
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

	List<CompilationUnit> java_files;
	HashSet<String> set_classes;
	HashSet<String> set_interfaces;
	String yUML_grammar ="";
	String heading = "";
	String append = ",";
	String classOrInterfaceGrammar="";
	String classOrInterfaceName="";
	String method_grammar="";
	ClassOrInterfaceDeclaration classorinterface;
	List<String> publicFields = new ArrayList<String>();
	boolean isMore;
	boolean isFields;
	
	public String parseFile(String fileLocation, String destination_URL) throws Exception{
		
		this.fileLocation = fileLocation;
		this.destination_URL = destination_URL;
		getAllFilesWithJava(fileLocation);
		findClassorInterface(java_files);
		String completeGrammar = createGrammar(java_files);
		return completeGrammar;
	}
	

	private String createGrammar(List<CompilationUnit> java_files) {

		for (CompilationUnit file : java_files) {
			List<TypeDeclaration> listtypedec = file.getTypes();
			Node node = listtypedec.get(0);


			classorinterface = (ClassOrInterfaceDeclaration) node;
			if (classorinterface.isInterface()) {
				classOrInterfaceGrammar = "[" + "<<interface>>;";
			} else
				classOrInterfaceGrammar = "[";

			classOrInterfaceGrammar += classorinterface.getName();
			classOrInterfaceName = classorinterface.getName();

			//Parsing Methods,Constructors
			checkConstructor(node);
			checkMethods(node);
			checkFields(node);

		}
		System.out.println("grammar:" + heading);
		return heading;
	}


	private void checkConstructor(Node node){
		isMore =false;
		List<BodyDeclaration> members = ((TypeDeclaration) node).getMembers();
		for (BodyDeclaration member : members) {
			//Checking if its constructor
			if (member instanceof ConstructorDeclaration) {

				ConstructorDeclaration member_constructor = ((ConstructorDeclaration) member);
				System.out.println("Constructor: " + member_constructor);
				String memberAsString = ((ConstructorDeclaration) member).getDeclarationAsString();

				if (!classorinterface.isInterface() && isPublic(memberAsString)) {
					System.out.println("Found Public Constructor");
					if(isMore)
					method_grammar += ";";
					method_grammar += "+ " + member_constructor.getName() + "(";
					for (Object child_nodes : member_constructor.getChildrenNodes()) {
						System.out.println("Child_Nodes:" + child_nodes);
						if (child_nodes instanceof Parameter) {
							System.out.println("Found parameter in constructor");
							String name = ((Parameter) child_nodes).getChildrenNodes().get(0).toString();
							System.out.println("Name: "+name);
							String type = ((Parameter) child_nodes).getType().toString();
							System.out.println("type: "+type);
							method_grammar += name + " : " + type;
							if (set_classes.contains(type)) {
								append += "[" + heading + "] uses -.->";
								if (set_interfaces.contains(type)) {
									append += "[<<interface>>;" + type + "]";
								} else
									append += "[" + type + "]";
							}
							append += ",";
						}
					}

					method_grammar += ")";
					isMore =true;
					System.out.println("Method_grammar:"+method_grammar);
				}
			}
		}

	}

	private void checkMethods(Node node){

		// check if its method
		List<BodyDeclaration> members = ((TypeDeclaration) node).getMembers();
		for(BodyDeclaration member : members){
			if(member instanceof  MethodDeclaration){
				MethodDeclaration member_method = (MethodDeclaration)member;
				String memberAsString = ((MethodDeclaration) member).getDeclarationAsString();
				String memberName = ((MethodDeclaration) member).getName();

				if(!classorinterface.isInterface() && isPublic(memberAsString)){

					if(isGetterSetter(memberName)){
						//Make field public
						publicFields.add(memberName.substring(3).toLowerCase());
					}else{
						if(isMore)
						method_grammar +=";";
						method_grammar += "+" + memberName +"(";

						for(Object child_nodes : member_method.getChildrenNodes()){
							if (child_nodes instanceof Parameter) {
								System.out.println("Found parameters in methods");
								String name = ((Parameter) child_nodes).getChildrenNodes().get(0).toString();
								String type = ((Parameter) child_nodes).getType().toString();
								method_grammar += name + " : "+type;

								if(set_classes.contains(type) && !set_interfaces.contains(classOrInterfaceName)){
								append +="[" + classOrInterfaceName + "] uses -.->";
									if(set_interfaces.contains(type))
									append += "[<<interface>>;" + type + "]";
									else
										append += "[" +type+ "]";

								}
								append+=",";
							}else{
								String methods [] = child_nodes.toString().split(" ");
								for(String method: methods){
									if(set_classes.contains(method) && !set_interfaces.contains(classOrInterfaceName)){
										append += "[" + classOrInterfaceName + "] uses -.->";
										if(set_interfaces.contains(method))
											append += "[<<interface>>;" + method + "]";
										else
											append += "[" +method+ "]";
											append+=",";
									}
								}
							}

						}
						method_grammar+= ") : "+member_method.getType();
						isMore=true;
					}
				}
			}
		}
	}

	private void checkFields(Node node){
		isFields =false;
		List<BodyDeclaration> members = ((TypeDeclaration) node).getMembers();
		for(BodyDeclaration member : members){
			if(member instanceof FieldDeclaration){
				FieldDeclaration field_declaration = (FieldDeclaration)member;

				String field_modifier = "";
			}
		}



	}


	private Boolean isGetterSetter(String str){
		if(str.startsWith("get") || str.startsWith("set"))
			return true;
		else
			return false;

	}

	private Boolean isPublic(String str){
		if(str.startsWith("public"))
			return true;
		else
			return false;
	}

    private void findClassorInterface(List<CompilationUnit> java_files) {
    	
    	set_interfaces = new HashSet<>();
    	set_classes = new HashSet<>();
    	
    	for (CompilationUnit file : java_files) {
    		//Creating List of "TypeDeclaration" to know if its class or interface
    		List<TypeDeclaration> list_files = file.getTypes();
        	if(!list_files.isEmpty() ){
        		for(Node node : list_files)
            	{
            	 ClassOrInterfaceDeclaration classorinterface = (ClassOrInterfaceDeclaration)node;
            	 if(classorinterface.isInterface())
            		 set_interfaces.add(classorinterface.getName());
            	 else
            		 set_classes.add(classorinterface.getName());

                }
        	}
        }
    }
	
	private void  getAllFilesWithJava(String fileLocation2) throws Exception {
		java_files = new ArrayList<>();
		File directory = new File(fileLocation2);
		for(File file : directory.listFiles()){
			if(file.isFile() && file.getName().endsWith(".java")){
				//System.out.println("File " + file.getName());
	     //Using JavaParser.parse to generate Abstract Syntax Tree (AST) from Java code.AST structure is easy to process which returns CompilationUnit object
				java_files.add(JavaParser.parse(file));
			//System.out.println(java_files.get(java_files.size()-1));
			}
		
		}
		//System.out.println("Total number of files:"+java_files.size());
	
	}
}
