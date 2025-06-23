# PBLImagewithjit
<pre><h1>commands:</h1>mvn clean install
mvn clean compile
( run this in root folder (i.e.) parsingfiles ).
java -jar E:\pbl\PBJImagewithjit-main\Imageprocessing\antlr-4.13.1-complete.jar -Dlanguage=Java Imagescript.g4
</pre>
> mvn exec:java -Dexec.mainClass="org.MainApp"
<br>
> 
> git branch -m master main

Generated Files:
ImagescriptLexer.java - Tokenizes your input
ImagescriptParser.java - Parses the tokens into a tree
ImagescriptBaseListener.java - Base class for listening to parse events
ImagescriptListener.java - Interface for custom listeners
Imagescript.tokens - Token definitions
ImagescriptLexer.tokens - Lexer token definitions
Imagescript.interp - Interpreter data
ImagescriptLexer.interp - Lexer interpreter data