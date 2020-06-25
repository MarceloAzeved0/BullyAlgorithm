
// @Authors: Gabriel Brunichaki, Gregory Lagranha, Marcelo Bernardy
// @Algorithm: centralizado
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.net.SocketException;

public class Main {
  public static void main(String[] args) throws SocketException {
    List<SendMessage> lstSendMessage = loadFile(args[0], args[1]);
    Node nodeLine = loadNode(args[0], args[1]);
    System.out.println("\nSeu Nodo: " + nodeLine + "\n");
    SendMessage initialCoord = maxNode(lstSendMessage);
    System.out.println("Coordenador Inicial: " + initialCoord +"\n") ;
    startNodes(nodeLine, initialCoord, lstSendMessage);
  }

  public static SendMessage maxNode(List<SendMessage> lstNode){
    SendMessage aux = lstNode.get(0);
    for (SendMessage node : lstNode) {
      if(node.id > aux.id){
        aux = node;
      }
    }

    SendMessage coordAux = new SendMessage(aux.id, aux.ip, aux.port);
    return coordAux;
  }

  public static void startNodes(Node node, SendMessage initialCoord, List<SendMessage> lstSendMessage){
      node.start();
      node.setNodeList(lstSendMessage, initialCoord);
    
  }

  public static Node loadNode(String nameFile, String lineCommand) throws SocketException{
    String lineNode = "";
    try {
      FileReader arq = new FileReader(nameFile);
      BufferedReader lerArq = new BufferedReader(arq);
 
      String linha = lerArq.readLine(); 
      
      int i = 1;
      while (linha != null) {
        if(i == Integer.parseInt(lineCommand)){
         lineNode = linha;
        }
        i++;
        linha = lerArq.readLine(); 
      }
      
      arq.close();
    } catch (IOException e) {
        System.err.printf("Erro na abertura do arquivo: %s.\n",
          e.getMessage());
    }
    
    
    String[] values = lineNode.split(" ");
    Node node = new Node(Integer.parseInt(values[0]), values[1], Integer.parseInt(values[2]));
    
    return node;
  }

  public static List<SendMessage> loadFile(String nameFile, String lineCommand) throws SocketException{
    List<String> lstLines = new ArrayList<>();
    try {
      FileReader arq = new FileReader(nameFile);
      BufferedReader lerArq = new BufferedReader(arq);
 
      String linha = lerArq.readLine(); 
      
      int i = 1;
      while (linha != null) {
        lstLines.add(linha);
        if(i == Integer.parseInt(lineCommand)){
          //ver o que precisa fazer
        }
        i++;
        linha = lerArq.readLine(); 
      }
      
      arq.close();
    } catch (IOException e) {
        System.err.printf("Erro na abertura do arquivo: %s.\n",
          e.getMessage());
    }
    
    List<SendMessage> lstSendMessage = new ArrayList<>();

    for (String nodo : lstLines) {
      String[] values = nodo.split(" ");
      SendMessage node = new SendMessage(Integer.parseInt(values[0]), values[1], Integer.parseInt(values[2]));
      lstSendMessage.add(node);
    }

    return lstSendMessage;
  }
}