import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

public class Election extends Thread {
  private boolean mutex = false;

  private DatagramSocket datagramSocket;
  private byte[] buffer;
  private DatagramPacket datagramPacket;
  private List<Node> lstNodes;
  private Node coord;
  

  public Election(List<Node> lstNodes) {
    try {
      this.lstNodes = lstNodes;
      this.datagramSocket = new DatagramSocket(6000);
      coord = maxNode(lstNodes);
      buffer = new byte[4096];
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }

  public void run() {
    while (true) {
      try {
        datagramPacket = new DatagramPacket(buffer, buffer.length);
        datagramSocket.setSoTimeout(500);
        datagramSocket.receive(datagramPacket);
        String response = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

        if (response.equalsIgnoreCase("lock")) {
          byte[] buffer = String.valueOf(lock()).getBytes();
          DatagramPacket packetToSend = new DatagramPacket(buffer, buffer.length, datagramPacket.getAddress(),
              datagramPacket.getPort());
          datagramSocket.send(packetToSend);
        }

        if (response.equalsIgnoreCase("unlock")) {
          unlock();
        }
      } catch (IOException e) {
      }
    }
  }

  public boolean lock() {
    if (mutex) {
      return false;
    } else {
      mutex = true;
      return true;
    }
  }

  public boolean unlock() {
    mutex = false;
    return true;
  }

  public Node maxNode(List<Node> lstNode){
    Node aux = lstNode.get(0);
    for (Node node : lstNode) {
      if(node.id > aux.id){
        aux = node;
      }
    }
    System.out.println(aux.id);
    return aux;
  }
}