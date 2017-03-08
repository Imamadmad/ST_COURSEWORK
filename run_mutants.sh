#!/bin/bash

for A in {0..9} 
do

	echo Testing m_$A.jar
	javac -cp .:/usr/share/java/junit4.jar:./Mutations/m_$A.jar Task22.java
	java -cp .:/usr/share/java/junit4.jar:./Mutations/m_$A.jar org.junit.runner.JUnitCore Task22

done
