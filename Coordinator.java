import java.net.DatagramSocket;
import java.net.SocketException;

public class Coordinator extends Thread {
  int id;
  String ip;
  int port;
  DatagramSocket datagramSocket;

  public Coordinator(int id, String ip, int port) throws SocketException {
    this.id = id;
    this.ip = ip;
    this.port = port;
    this.datagramSocket = new DatagramSocket(port);
  }
  
}