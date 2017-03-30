# UML-Parser

This project aims at generating UML diagrams from the Java Source Code. We would be using two Tools:
1) Core Java Parser-- For parsing the java files
2) yUML Generator -- For generating the UML diagrams

1)JavaParser: https://github.com/javaparser/javaparser

The above library convets java code into grammar. This grammar is provided as an input to yUML which generates the output as UML diagram accordingly.
Example Grammar : 

[Customer|-forname:string;surname:string|doShiz()]<>-orders*>[Order], [Order]++-0..*>[LineItem], [Order]-[note:Aggregate root{bg:wheat}]

2)http://yuml.me/
yUML is online service through which by making a get request to it and providing the proper grammar one can generate UML diagrams. The links are as follows:

For Plain UML
https://yuml.me/diagram/plain/class/"

For nofunky UML
https://yuml.me/diagram/nofunky/class/"	

For Scruffy UML
https://yuml.me/diagram/scruffy/class/"

We can use any one of the URL's above and append our grammar to generate UML diagram. One of the examples of complete URL is shown below:
Complete Link:
https://yuml.me/diagram/plain/class/[Customer|-forname:string;surname:string|doShiz()]<>-orders*>[Order], [Order]++-0..*>[LineItem], [Order]-[note:Aggregate root{bg:wheat}]

The above generates class diagram.


Currently working on:
-Develop and enhance the logic for creating grammar for yUML.
