all:			Main.class Election.class Node.class

Election.class: Election.java
				@javac Election.java

Node.class:     Node.java
				@javac Node.java

Main.class:     Main.java
				@javac Main.java

clean:
				@rm -rf *.class *~