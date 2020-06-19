// @Authors: Gabriel Brunichaki, Gregory Lagranha, Marcelo Bernardy
// @Algorithm: centralizado

import java.net.SocketException;

public class Main {
  public static void main(String[] args) throws SocketException {
    Coordinator coordinator = new Coordinator();
    coordinator.start();

    Node Node1 = new Node("1", 6001);
    Node Node2 = new Node("2", 6002);
    Node Node3 = new Node("3", 6003);
    Node Node4 = new Node("4", 6004);
    Node Node5 = new Node("5", 6005);

    Node1.start();
    Node2.start();
    Node3.start();
    Node4.start();
    Node5.start();
  }
}