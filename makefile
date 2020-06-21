all:			Main.class  Listener.class SendMessage.class Node.class


Listener.class: Listener.java
				@javac Election.java

Node.class:     Node.java
				@javac Node.java

SendMessage.class:     SendMessage.java
				@javac SendMessage.java

Main.class:     Main.java
				@javac Main.java

clean:
				@rm -rf *.class *~