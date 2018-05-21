MiniNet:
	javac src/com/*.java src/com/Controller/*.java src/com/Model/*.java src/com/Model/Exceptions/*.java src/com/Services/*.java src/com/Services/PathDegree/*.java
	java -cp "./lib/*:./src/" com.MiniNet


windows:
	javac src/com/*.java src/com/Controller/*.java src/com/Model/*.java src/com/Model/Exceptions/*.java src/com/Services/*.java src/com/Services/PathDegree/*.java
	java -cp "./lib/*;./src/" com.MiniNet

clean:
	rm -r src/com/*.class src/com/Controller/*.class src/com/Model/*.class src/com/Model/Exceptions/*.class src/com/Services/*.class src/com/Services/PathDegree/*.class
