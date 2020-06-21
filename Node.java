import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.io.*;

public class Node extends Thread {
  int id;
  String ip;
  int port;
  DatagramSocket datagramSocket;
  Integer countSendMessage;
  Integer timeAsCoordinator;
  Listener listener;
  Boolean isCoordinator;

  public Node(int id, String ip, int port) throws SocketException {
    this.id = id;
    this.ip = ip;
    this.port = port;
    this.datagramSocket = new DatagramSocket(port);
    this.listener = new Listener(datagramSocket);
    this.listener.start();
    this.isCoordinator = false;
    countSendMessage = 0;
    timeAsCoordinator = 0;
  }

  public void run() {
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

  public void setNodeAsCoordinator(Boolean isCoordinator){
    this.isCoordinator = isCoordinator;
    this.listener.setIsCoordinator(isCoordinator);
  }

  public void setCountSendMessage(Integer count){
    this.countSendMessage = count;
  }

  public String toString(){
    return "Id: " + id + " Ip: " + ip+ " Porta: " + port;
  }

}