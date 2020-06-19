all:			Main.class Coordinator.class Node.class

Coordinator.class: Coordinator.java
				@javac Coordinator.java

Node.class:     Node.java
				@javac Node.java

Main.class:     Main.java
				@javac Main.java

clean:
				@rm -rf *.class *~