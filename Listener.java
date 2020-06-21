import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Listener extends Thread {
  DatagramSocket socket;
  int id;
  Thread receiver;
  Boolean conectedCoordinator = false;
  Boolean receiveMessage = false;
  Coordinator coordinator = null;
  Long countTime = null;
  List<Node> lstNodes;
  List<Coordinator> lstNodesWhoAnswered = new ArrayList<Coordinator>();

  public Listener(DatagramSocket socket, int id) {
    this.socket = socket;
    this.id = id;
  }

  public void setLstNodes(List<Node> lstNodes) {
    this.lstNodes = lstNodes;
  }

  public void setNodeAsCoordinator(Coordinator coordinator) {
    this.coordinator = coordinator;
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

  public Node maxNode(List<Node> lstNode) {
    Node aux = lstNode.get(0);
    for (Node node : lstNode) {
      if (node.id > aux.id) {
        aux = node;
      }
    }
    return aux;
  }

  public void setCoordinator(Coordinator coordinator) {
    this.coordinator = coordinator;
  }

  public void processCommand(DatagramPacket datagramPacket, String message)
      throws NumberFormatException, SocketException, UnknownHostException {
    InetAddress packetAddress = datagramPacket.getAddress();
    Integer packetPort = datagramPacket.getPort();
      
    if(this.coordinator.id == this.id ){
      if(message.startsWith("ATIVO")){
        
        String idNode = message.split("-")[1];
        Coordinator coordinator = new Coordinator(Integer.parseInt(idNode), packetAddress.toString().replace("/", ""), Integer.parseInt(packetPort.toString()));
        
        InetAddress inet = InetAddress.getByName(packetAddress.toString().replace("/", ""));
        sendMessage(this.socket, inet, Integer.parseInt(packetPort.toString()), "OK-" + this.id);
        lstNodesWhoAnswered.add(coordinator);

        if(lstNodesWhoAnswered.size() == lstNodes.size() - 1){
          System.out.println("Coordenador Confirmado: " + this.coordinator);
          countTime = System.currentTimeMillis();
          System.out.println("\nComeçando a contagem: " + countTime);
        }
      }

      if(message.startsWith("CONECTADO")){
        if(System.currentTimeMillis() >= countTime + 10000){
          System.out.println("Acabou o coordenador");
        }else{
          InetAddress inet = InetAddress.getByName(packetAddress.toString().replace("/", ""));
          sendMessage(this.socket, inet, coordinator.port, "SIM MEU CARO AMIGO");
        }
      }
      
    }else{
      if(message.startsWith("SIM MEU CARO AMIGO")){
        this.receiveMessage = true;
      }else if(message.startsWith("OK")){
        String idNode = message.split("-")[1];
        this.coordinator = new Coordinator(Integer.parseInt(idNode), packetAddress.getHostName(), packetPort);
        this.conectedCoordinator = true;
        this.receiveMessage = true;
      }
    }

  }

  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        // System.out.println("meu coordenador " + this.coordinator +" no " + this.id);
        byte[] text = new byte[1024];
        if(this.coordinator.id != this.id && conectedCoordinator == false){
          InetAddress inet = InetAddress.getByName(coordinator.ip);
          sendMessage(this.socket, inet, this.coordinator.port, "ATIVO-" + this.id);
        }else if(this.coordinator.id != this.id && conectedCoordinator){
          Thread.sleep(3000);
          this.receiveMessage = false;
          InetAddress inet = InetAddress.getByName(coordinator.ip);
          sendMessage(this.socket, inet, this.coordinator.port, "CONECTADO?-" + this.id);
        }

        DatagramPacket datagramPacket = new DatagramPacket(text, text.length);
        socket.setSoTimeout(500);
        socket.receive(datagramPacket);
        String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
        
        System.out.println("\nMensagem de: " + datagramPacket.getAddress() + ":" + datagramPacket.getPort() +" - " + message);
        processCommand(datagramPacket, message);
        if(receiveMessage == false && this.coordinator.id != this.id){
          System.out.println("chamar eleição");
        }

      } catch (Exception e) {
        // e.printStackTrace();
      }

    }

    System.out.println("Cliente fechado.");
  }
}