import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Listener extends Thread {
  DatagramSocket socket;
  Thread receiver;
  Boolean isCoordinator = false;
  
  public Listener(DatagramSocket socket) {
    this.socket = socket;
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

  public void setIsCoordinator(Boolean isCoordinator){
    this.isCoordinator = isCoordinator;
  }

  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        byte[] text = new byte[1024];

        DatagramPacket datagramPacket = new DatagramPacket(text, text.length);

        socket.setSoTimeout(500);
        socket.receive(datagramPacket);
        String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

        System.out.println("\nMensagem de: " + datagramPacket.getAddress() + "\n");
        System.out.println(message + "\n\n");
      } catch (IOException e) {
      }

    }

    System.out.println("Cliente fechado.");
  }
}