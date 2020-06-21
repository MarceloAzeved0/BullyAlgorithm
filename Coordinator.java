public class Coordinator {
  int id;
  String ip;
  int port;

  public Coordinator(int id, String ip, int port){
    this.id = id;
    this.ip = ip;
    this.port = port;
  }
  
  public String toString(){
    return "Id: " + id + " Ip: " + ip+ " Porta: " + port;
  }
}