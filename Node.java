import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.io.*;

public class Node extends Thread {
  String id;
  String coordHost;
  DatagramSocket datagramSocket;
  Integer count;

  public Node(String id, int port) throws SocketException {
    this.id = id;
    this.coordHost = "127.0.0.1";
    this.datagramSocket = new DatagramSocket(port);
    count = 0;
  }

  public void run() {
    System.out.println("🎈\tInitializing process #" + id);

    while (count < 50) {
      try {
        // System.out.println("🔐\tProcess #" + id + "\ttrying to lock the
        // resource...");
        if (lock()) {
          System.out.println("🔒\tProcess #" + id + "\tlocked the resource.");

          Integer lastValue = read();
          write(lastValue + 100, id, count);

          count++;

          System.out.println("📖\tProcess #" + id + "\tread " + lastValue);
          System.out.println("✏️\tProcess #" + id + "\twrite " + (lastValue + 100));

          unlock();

          System.out.println("🔓\tProcess #" + id + "\tunlocked the resource.");
        }

      } catch (Exception e) {
        System.out.println(e);
      }
    }
  }

  private boolean lock() throws IOException {
    byte[] buffer = "lock".getBytes();
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(coordHost), 6000);
    datagramSocket.send(packet);
    buffer = new byte[8192];
    while (true) {
      try {
        packet = new DatagramPacket(buffer, buffer.length);
        datagramSocket.setSoTimeout(500);
        datagramSocket.receive(packet);
        return Boolean.parseBoolean(new String(packet.getData(), 0, packet.getLength()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void unlock() throws IOException {
    byte[] buffer = "unlock".getBytes();
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(coordHost), 6000);
    datagramSocket.send(packet);
  }

  public static Integer read() throws IOException {
    String lastLine = "";
    String currentLine;
    BufferedReader bufferedReader = new BufferedReader(new FileReader("sharedResource.txt"));
    while ((currentLine = bufferedReader.readLine()) != null) {
      lastLine = currentLine;
    }
    bufferedReader.close();
    return Integer.parseInt(lastLine);
  }

  public static void write(final Integer value, final String idProcess, final Integer i) throws IOException {
    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("sharedResource.txt", true));

    bufferedWriter.write("\n".concat(String.valueOf(value)));
    bufferedWriter.close();
  }
}