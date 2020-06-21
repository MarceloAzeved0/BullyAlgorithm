all:			Main.class  Listener.class Coordinator.class Node.class


Listener.class: Listener.java
				@javac Election.java

Node.class:     Node.java
				@javac Node.java

Coordinator.class:     Coordinator.java
				@javac Coordinator.java

Main.class:     Main.java
				@javac Main.java

clean:
				@rm -rf *.class *~