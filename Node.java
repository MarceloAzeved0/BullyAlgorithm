import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;
public class Node extends Thread {
  int id;
  String ip;
  int port;
  DatagramSocket datagramSocket;
  Listener listener;
  
  public Node(int id, String ip, int port) throws SocketException {
    this.id = id;
    this.ip = ip;
    this.port = port;
    this.datagramSocket = new DatagramSocket(port);
    this.listener = new Listener(datagramSocket, id, ip);
  }

  public void run() {
    this.listener.start();
    System.out.println("🎈\tInitializing process #" + id);
  }

  public void sendMessage(DatagramSocket socket, InetAddress inetAddress, Integer port, String message) {
    byte[] command = new byte[1024];
    command = message.getBytes();

    try {
      socket.send(new DatagramPacket(command, command.length, inetAddress, port));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setNodeList(List<Node> lstNode, SendMessage initialCoord){
    this.listener.setLstNodes(lstNode);
    this.listener.coordinator = initialCoord;
  }

  public String toString(){
    return "Id: " + id + " Ip: " + ip+ " Porta: " + port;
  }

}