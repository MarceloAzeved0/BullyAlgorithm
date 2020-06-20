import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import java.util.Scanner;

public class Listener extends Thread {
  DatagramSocket socket;
  Thread receiver;
  Map<String, String> files;

  public Listener(DatagramSocket socket, Map<String, String> files) {
    this.socket = socket;
    this.files = files;
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

  public void processFile(DatagramPacket packet, String message) throws IOException {
    String[] token = message.split(" ");
    String fileHash = "";
    String fileName = "";

    if (token.length > 1) {
      fileName = token[1];
    }

    if (token.length > 2) {
      fileHash = token[2];
    }

    if (message.startsWith("REQUEST")) {
      InetAddress packetAddress = packet.getAddress();
      Integer packetPort = packet.getPort();

      for (Map.Entry<String, String> entry : this.files.entrySet()) {
        if (entry.getValue().endsWith(fileName)) {
          fileHash = entry.getKey();
        }
      }

      if (fileHash == "") {
        this.sendMessage(this.socket, packetAddress, packetPort, "NOTFOUND");
        return;
      }

      File newFile = new File(this.files.get(fileHash));

      if (!newFile.exists()) {
        this.files.remove(fileHash);
        this.sendMessage(this.socket, packetAddress, packetPort, "NOTFOUND");

        return;
      }

      this.sendMessage(this.socket, packetAddress, packetPort,
          "CONFIRMATION " + newFile.getName() + " " + fileHash.toString());

      return;
    }

    if (message.startsWith("CONFIRMATION")) {
      DatagramSocket receiveSocket = new DatagramSocket();
      InetAddress senderAddress = packet.getAddress();
      Integer senderPort = packet.getPort();
      String returnMessage = "FILESEND" + " " + fileName + " " + fileHash;

      this.receiver = new Receiver(receiveSocket, senderAddress, senderPort, fileName);
      this.receiver.start();

      this.sendMessage(receiveSocket, senderAddress, senderPort, returnMessage);

      return;
    }

    if (message.startsWith("FILESEND")) {
      File file = new File(this.files.get(fileHash));
      if (!file.exists())
        return;
      InetAddress receiverAddress = packet.getAddress();
      Integer port = packet.getPort();

      Scanner scanner = new Scanner(file);
      byte[] sendData = new byte[1024];
      while (scanner.hasNextLine()) {
        sendData = scanner.nextLine().getBytes();
        DatagramPacket packetFile = new DatagramPacket(sendData, sendData.length, receiverAddress, port);
        this.socket.send(packetFile);
      }
      scanner.close();
      return;
    }
    if (message.startsWith("NOTFOUND")) {
      System.out.println("Arquivo não encontrado!");
      return;

    }
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
        this.processFile(datagramPacket, message);
      } catch (IOException e) {
      }

    }

    System.out.println("Cliente fechado.");
  }
}