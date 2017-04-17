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
import com.github.javaparser.ast.type.ClassOrInterfaceType;



public class UMLParser {

	String fileLocation;
	String destination_URL;

	List<CompilationUnit> java_files;
	HashSet<String> set_classes;
	HashSet<String> set_interfaces;
    HashMap<String, String> mapConnections;
	String yUML_grammar ="";
	String append;
	String classOrInterfaceGrammar;
	String classOrInterfaceName;
	String method_grammar;
	ClassOrInterfaceDeclaration classorinterface;
	List<String> publicFields;
	boolean isMore;
	boolean isFields;
    String fields;


	
	public String parseFile(String fileLocation, String destination_URL) throws Exception{

	    mapConnections = new HashMap<>();
		this.fileLocation = fileLocation;
		this.destination_URL = destination_URL;
		getAllFilesWithJava(fileLocation);
		findClassorInterface(java_files);
		String completeGrammar = createGrammar(java_files);
        System.out.println(completeGrammar);
		completeGrammar += adddOns();
        completeGrammar = convertGrammar(completeGrammar);
       // System.out.println("Complete grammar");
       // System.out.println(completeGrammar);
        return completeGrammar;

    }

    private String convertGrammar(String gramm) {
        String[] lines = gramm.split(",");
        String[] unique_lines = new LinkedHashSet<String>(
                Arrays.asList(lines)).toArray(new String[0]);
        String res = String.join(",", unique_lines);
        return res;
    }

	private String adddOns(){
        String gramm = "";
        Set<String> keys = mapConnections.keySet(); // get all keys
        for (String i : keys) {
            String[] classes = i.split("-");
            if (set_interfaces.contains(classes[0]))
                gramm += "[<<interface>>;" + classes[0] + "]";
            else
                gramm += "[" + classes[0] + "]";
            gramm += mapConnections.get(i); // Add connection
            if (set_interfaces.contains(classes[1]))
                gramm += "[<<interface>>;" + classes[1] + "]";
            else
                gramm += "[" + classes[1] + "]";
            gramm += ",";
        }
        return gramm;
    }

	private String createGrammar(List<CompilationUnit> java_files) {

		for (CompilationUnit file : java_files) {

            classOrInterfaceGrammar="";
            classOrInterfaceName="";
            method_grammar="";
            fields="";
            append=",";
            publicFields = new ArrayList<String>();


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
			checkExtendsOrImplements();

            yUML_grammar += classOrInterfaceGrammar;
            if (!fields.isEmpty()) {
                yUML_grammar += "|" + checkClasses(fields);
            }
            if (!method_grammar.isEmpty()) {
                yUML_grammar += "|" + checkClasses(method_grammar);
            }
            yUML_grammar += "]";
            yUML_grammar += append;

        }

        return yUML_grammar;

	}



	private void checkConstructor(Node node){
		isMore =false;
		List<BodyDeclaration> members = ((TypeDeclaration) node).getMembers();
		for (BodyDeclaration member : members) {
			//Checking if its constructor
			if (member instanceof ConstructorDeclaration) {

				ConstructorDeclaration member_constructor = ((ConstructorDeclaration) member);
				//System.out.println("Constructor: " + member_constructor);
				String memberAsString = ((ConstructorDeclaration) member).getDeclarationAsString();

				if (!classorinterface.isInterface() && isPublic(memberAsString)) {
					//System.out.println("Found Public Constructor");
					if(isMore)
					method_grammar += ";";
					method_grammar += "+ " + member_constructor.getName() + "(";
					for (Object child_nodes : member_constructor.getChildrenNodes()) {
						if (child_nodes instanceof Parameter) {
                            String name = ((Parameter) child_nodes).getChildrenNodes().get(0).toString();
                            String type = ((Parameter) child_nodes).getType().toString();
                            method_grammar += name + " : " + type;
                            if(set_interfaces.contains(type) && !set_interfaces.contains(classOrInterfaceName)) {
                                System.out.println("Inside set classes and not set interfaces");
                                append += "[" + classOrInterfaceName + "] uses -.-> [<<interface>>;" + type + "]";
                                System.out.println("Inside set classes and not set interfaces append" + append);
                            }else if(set_classes.contains(type) && set_classes.contains(classOrInterfaceName)) {
                                //if(set_classes.contains(type) && !set_interfaces.contains(classOrInterfaceName)){
                                //else if(set_classes.contains(type) && set_classes.contains(classOrInterfaceName)){
                                //if(set_interfaces.contains(type))
                                //append += "[<<interface>>;" + type + "]";
                                //else
                                append += "[" + classOrInterfaceName + "] uses -.-> [" + type + "]";
                                System.out.println("Inside else set classes and not set interfaces append" + append);

                            }

//							if (set_classes.contains(type) && !set_interfaces.contains(classOrInterfaceName)) {
//								append += "[" + classOrInterfaceName + "] uses -.->";
//								if (set_interfaces.contains(type)) {
//									append += "[<<interface>>;" + type + "]";
//								} else
//									append += "[" + type + "]";
//							}
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
								//System.out.println("Found parameters in methods");
								String name = ((Parameter) child_nodes).getChildrenNodes().get(0).toString();
								String type = ((Parameter) child_nodes).getType().toString();
								method_grammar += name + " : "+type;
                                System.out.println("Inside paramter");
                                System.out.println("type: "+type);
                                System.out.println("class or intname:"+classOrInterfaceName);
                                System.out.println("Set classes type: "+set_classes.contains(type));
                                System.out.println("Set interfaces not name: "+!set_interfaces.contains(classOrInterfaceName));
                                if(set_interfaces.contains(type) && !set_interfaces.contains(classOrInterfaceName)) {
                                    System.out.println("Inside set classes and not set interfaces");
                                    append += "[" + classOrInterfaceName + "] uses -.-> [<<interface>>;" + type + "]";
                                    System.out.println("Inside set classes and not set interfaces append" + append);
                                }else if(set_classes.contains(type) && set_classes.contains(classOrInterfaceName)) {
                                    //if(set_classes.contains(type) && !set_interfaces.contains(classOrInterfaceName)){
                                    //else if(set_classes.contains(type) && set_classes.contains(classOrInterfaceName)){
                                    //if(set_interfaces.contains(type))
									//append += "[<<interface>>;" + type + "]";
									//else
                                    append += "[" + classOrInterfaceName + "] uses -.-> [" + type + "]";
                                    System.out.println("Inside else set classes and not set interfaces append" + append);

								}
								append+=",";
							}else{
								String methods1 [] = child_nodes.toString().split(" ");
								for(String meth: methods1){

                                    if(set_interfaces.contains(meth) && !set_interfaces.contains(classOrInterfaceName)) {
                                        System.out.println("Inside set classes and not set array");
                                        append += "[" + classOrInterfaceName + "] uses -.-> [<<interface>>;" + meth + "]";
                                        System.out.println("Inside set classes and not set interfaces append array" + append);
                                    }
                                    else if(set_classes.contains(meth) && set_classes.contains(classOrInterfaceName)){
                                        append += "[" + classOrInterfaceName + "] uses -.-> [" + meth + "]";
                                        System.out.println("Inside set classes and not set interfaces append else array" + append);
                                    }
                                    append+=",";

//									if(set_interfaces.contains(meth) && !set_interfaces.contains(classOrInterfaceName)){
//										append += "[" + classOrInterfaceName + "] uses -.->";
//										if(set_interfaces.contains(meth))
//											append += "[<<interface>>;" + meth + "]";
//										else
//											append += "[" +meth+ "]";
//											append+=",";
//									}
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

	private void checkFields(Node node) {
        isFields = false;
        List<BodyDeclaration> members = ((TypeDeclaration) node).getMembers();
        for (BodyDeclaration member : members) {
            if (member instanceof FieldDeclaration) {
                FieldDeclaration field_declaration = (FieldDeclaration) member;
                String modifier = member.toStringWithoutComments().substring(0, member.toStringWithoutComments().indexOf(" "));
                String fieldModifier = checkAccessModifier(modifier);
                String fieldType = checkClasses(field_declaration.getType().toString());
                String p_name = field_declaration.getChildrenNodes().get(1).toString();
                String fieldName = p_name;
                if (p_name.contains("="))
                    fieldName = p_name.substring(0, p_name.indexOf("=") - 1);

                //If getter setters are present change the scope
                if (fieldModifier.equals("-") && publicFields.contains(fieldName.toLowerCase()))
                    fieldModifier = "+";

                String dependencies = "";
                boolean multipleDependencies = false;
                if (fieldType.contains("(")) {
                    dependencies = fieldType.substring(fieldType.indexOf("(") + 1, fieldType.indexOf(")"));
                    multipleDependencies = true;
                    System.out.println("multi true");
                } else if (set_classes.contains(fieldType) || set_interfaces.contains(fieldType)) {
                    System.out.println("here in doubt condition");
                    System.out.println("fuled class:"+fieldType);
                    dependencies = fieldType;
                }
                System.out.println("getdeppen: "+dependencies +" class value: "+set_classes.contains(dependencies)+"interface valu: "+set_interfaces.contains(dependencies));
                if (dependencies.length() > 0 && (set_classes.contains(dependencies) || set_interfaces.contains(dependencies))) {
                    String conn = "-";
                    if (mapConnections.containsKey(dependencies + "-" + classOrInterfaceName)) {
                        conn = mapConnections.get(dependencies + "-" + classOrInterfaceName);
                        if (multipleDependencies) {
                            conn = "*" + conn;
                        }
                        System.out.println("depend0: "+dependencies + "-" + classOrInterfaceName + "conn"+conn);

                        mapConnections.put(dependencies + "-" + classOrInterfaceName, conn);
                    } else {
                        if (multipleDependencies)
                            conn += "*";
                        System.out.println("depend: "+classOrInterfaceName + "-" + dependencies + "conn"+conn);

                        mapConnections.put(classOrInterfaceName + "-" + dependencies, conn);
                    }
                }
                if (fieldModifier == "+" || fieldModifier == "-") {
                    if (isFields)
                        fields += "; ";

                    fields += fieldModifier + " " + fieldName + " : " + fieldType;
                    isFields = true;
                    //System.out.println("Fields: fields:"+fields);
                }
                System.out.println("Fields: fields:"+fields);
            }
        }
    }

    private void checkExtendsOrImplements(){
	    if(classorinterface.getExtends()!=null){
            append += "[" + classOrInterfaceName + "] " + "-^ " + classorinterface.getExtends();
            append += ",";
        }
        if(classorinterface.getImplements()!=null){
            List<ClassOrInterfaceType> listofinterfaces = (List<ClassOrInterfaceType>) classorinterface
                    .getImplements();
            for (ClassOrInterfaceType intface : listofinterfaces) {
                append += "[" + classOrInterfaceName + "] " + "-.-^ " + "["
                        + "<<interface>>;" + intface + "]";
                append += ",";
            }
        }
    }

	private String checkClasses(String str){
		str = str.replace("[", "(");
		str = str.replace("]", ")");
		str = str.replace("<", "(");
		str = str.replace(">", ")");
		return str;
	}

	private String checkAccessModifier(String str){
		if(str.equals("private")){
			return "-";
		}else if(str.equals("public")){
			return "+";
		}else
			return "";
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
