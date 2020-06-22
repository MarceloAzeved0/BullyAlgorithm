import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Listener extends Thread {
  DatagramSocket socket;
  int id;
  String ip;
  Thread receiver;
  Boolean isWinnerFighter = false;
  int countIsWinnerFighter = 0;
  Boolean conectedCoordinator = false;
  Boolean receiveMessage = false;
  SendMessage coordinator = null;
  Long countTime = null;
  List<Node> lstNodes;
  List<SendMessage> lstNodesWhoAnswered = new ArrayList<SendMessage>();

  public Listener(DatagramSocket socket, int id, String ip) {
    this.socket = socket;
    this.id = id;
    this.ip = ip;
  }

  public void setLstNodes(List<Node> lstNodes) {
    this.lstNodes = lstNodes;
  }

  public void setNodeAsCoordinator(SendMessage coordinator) {
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

  public void setCoordinator(SendMessage coordinator) {
    this.coordinator = coordinator;
  }


  public void callElection() throws UnknownHostException, InterruptedException {
    // System.out.println("************** Chamando Eleição " + this.id + " ***************\n");
    this.isWinnerFighter = true;
    for (Node node : lstNodes) {
      if(node.id > this.id){
        InetAddress inet = InetAddress.getByName(node.ip.toString());
        sendMessage(this.socket, inet, node.port, "ELEICAO");
      }
    }
  }

  public void processCommand(DatagramPacket datagramPacket, String message)
      throws NumberFormatException, SocketException, UnknownHostException, InterruptedException {
    InetAddress packetAddress = datagramPacket.getAddress();
    Integer packetPort = datagramPacket.getPort();
    // System.out.println(packetAddress + ":"+ packetPort);
    // System.out.println("Coordenador " + this.coordinator + " " + this.id + " Mensagem " + message);
    if(this.coordinator != null && this.coordinator.id == this.id ){
      if(message.startsWith("ATIVO")){
        
        String idNode = message.split("-")[1];
        SendMessage coordinator = new SendMessage(Integer.parseInt(idNode), packetAddress.toString().replace("/", ""), Integer.parseInt(packetPort.toString()));
        
        InetAddress inet = InetAddress.getByName(packetAddress.toString().replace("/", ""));
        sendMessage(this.socket, inet, Integer.parseInt(packetPort.toString()), "OK-" + this.id);
        lstNodesWhoAnswered.add(coordinator);

        int nodesLessThanId = 0;
        for (Node node : lstNodes) {
          if(node.id < this.id){
            nodesLessThanId++;
          }
        }

        if(lstNodesWhoAnswered.size() == nodesLessThanId){
          System.out.println("\n************** Coordenador Confirmado **************" + "\n" +this.coordinator);
          countTime = System.currentTimeMillis();
          DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
          Date date = new Date();
          System.out.println( "Tempo Inicial: "+ dateFormat.format(date) +"\n");
        }
      }

      if(message.startsWith("CONECTADO")){
        if(System.currentTimeMillis() >= countTime + 10000){
          System.out.println("\nTempo do coordenador expirado");
          System.out.println("Encerrando...");
          Thread.currentThread().interrupt();
        }else{
          InetAddress inet = InetAddress.getByName(packetAddress.toString().replace("/", ""));
          sendMessage(this.socket, inet, packetPort, "SIM MEU CARO AMIGO");
        }
      }
      
    }else{
      if(message.startsWith("SIM MEU CARO AMIGO")){
        this.receiveMessage = true;
      }else if(message.startsWith("OK")){
        String idNode = message.split("-")[1];
        this.coordinator = new SendMessage(Integer.parseInt(idNode), packetAddress.getHostName(), packetPort);
        this.conectedCoordinator = true;
        this.receiveMessage = true;
      }else if(message.startsWith("ELEICAO")){
        InetAddress inet = InetAddress.getByName(packetAddress.toString().replace("/", ""));
        this.sendMessage(this.socket, inet, packetPort, "SOCONACARA-" + this.id);
      }else if(message.startsWith("SOCONACARA")){
        isWinnerFighter = false;
      }else if(message.startsWith("MAIORCARADACIDADE")){
        String idNode = message.split("-")[1];

        this.coordinator = new SendMessage(Integer.parseInt(idNode), packetAddress.getHostName(), packetPort);
        this.conectedCoordinator = true;
        this.receiveMessage = true;
      }
    }

  }

  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        if(this.isWinnerFighter){
          if(countIsWinnerFighter < 10){
            countIsWinnerFighter++;
          }else{
            this.countIsWinnerFighter = 0;
            this.isWinnerFighter = false;
            this.conectedCoordinator = true;
            for (Node node : lstNodes) {
              InetAddress inet = InetAddress.getByName(node.ip);
              sendMessage(this.socket, inet, node.port, "MAIORCARADACIDADE-" + this.id);
              this.coordinator = new SendMessage(this.id, this.ip, this.socket.getLocalPort());
            }
            System.out.println("\nMaior cara da cidade: " +  this.id + "\n");

            int nodesLessThanId = 0;
            for (Node node : lstNodes) {
              if(node.id < this.id){
                nodesLessThanId++;
              }
            }
            if(nodesLessThanId == 0){
              System.out.println("Último Nodo: " + this.id);
              System.out.println("Encerrando...");
              Thread.currentThread().interrupt();
            }
          }
        }
        // System.out.println("meu coordenador " + this.coordinator +" no " + this.id);
        byte[] text = new byte[1024];
        if(this.coordinator != null && this.coordinator.id != this.id && conectedCoordinator == false){
          InetAddress inet = InetAddress.getByName(coordinator.ip);
          sendMessage(this.socket, inet, this.coordinator.port, "ATIVO-" + this.id);
        }else if(this.coordinator.id != this.id && this.conectedCoordinator){
          if(this.receiveMessage == false && this.isWinnerFighter == false){
            this.conectedCoordinator = false;
            // this.coordinator = null;
            callElection();
          }else{
            Thread.sleep(3000);
            this.receiveMessage = false;
            InetAddress inet = InetAddress.getByName(coordinator.ip);
            sendMessage(this.socket, inet, this.coordinator.port, "CONECTADO?-" + this.id);
          }
        }

        DatagramPacket datagramPacket = new DatagramPacket(text, text.length);
        socket.setSoTimeout(500);
        socket.receive(datagramPacket);
        String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
        
        System.out.println("Mensagem de: " + datagramPacket.getAddress() + ":" + datagramPacket.getPort() +" - " + message);
        processCommand(datagramPacket, message);
        
      } catch (Exception e) {
        // e.printStackTrace();
      }

    }

    System.out.println("Nodo fechado\n");
  }
}