
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
    List<Node> lstNodes = loadFile(args[0], args[1]);
  
    Election coordinator = new Election(lstNodes);
    coordinator.start();
  }
  
  public static List<Node> loadFile(String nameFile, String lineCommand) throws SocketException{
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
    
    List<Node> lstNodes = new ArrayList<>();

    for (String nodo : lstLines) {
      String[] values = nodo.split(" ");
      Node node = new Node(values[0], values[1], Integer.parseInt(values[2]));
      lstNodes.add(node);
    }

    return lstNodes;
  }
}