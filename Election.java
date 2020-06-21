import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
      buffer = new byte[4096];
      setNodeAsCoordinator();
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }

  public void run() {
    while (true) {
      try {
        for (Node node : lstNodes) {
          if(node.isCoordinator == false){
            InetAddress inet = InetAddress.getByName(coord.ip);
            node.sendMessage(node.datagramSocket, inet, coord.port, "amigo estou aqui");
          }
        }
      
        
      } catch (Exception e) {
      }
    }
  }

  public void setNodeAsCoordinator(){
    if(coord != null){
      System.out.println("not null");
      coord.setNodeAsCoordinator(false);
    }
    coord = maxNode(lstNodes);
    coord.setNodeAsCoordinator(true);  
    System.out.println(coord);
  }

  public Node maxNode(List<Node> lstNode){
    Node aux = lstNode.get(0);
    for (Node node : lstNode) {
      if(node.id > aux.id){
        aux = node;
      }
    }
    return aux;
  }
}