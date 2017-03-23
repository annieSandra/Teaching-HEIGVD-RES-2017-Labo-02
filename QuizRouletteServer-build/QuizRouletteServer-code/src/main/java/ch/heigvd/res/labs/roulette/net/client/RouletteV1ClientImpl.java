package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  private static Socket socket = null;
  private boolean isConnect = false;
  private static BufferedReader reader;
  private static PrintWriter writer;
  

  @Override
  public void connect(String server, int port) throws IOException {
    System.out.println("try to connect on " + server + " : " + port);
    socket = new Socket(server,port);
    
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    
    String line = null;
    if((line = reader.readLine()) != null){
       System.out.println("random sortie 1 serveur" + line);
       isConnect = true;
    }
      
    
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void disconnect() throws IOException {
    writer.printf(RouletteV1Protocol.CMD_BYE + "\n");
    writer.flush();
    
    writer.close();
      reader.close();
      socket.close();
      isConnect = false;
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean isConnected() {
     return isConnect;
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
     writer.printf(RouletteV1Protocol.CMD_LOAD + "\n");
     writer.flush();

     LOG.log(Level.INFO, "Server : {0}", reader.readLine());
     writer.printf(fullname + "\n");
     writer.flush();
     writer.printf(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER + "\n");
     writer.flush();
     LOG.log(Level.INFO, "Server : {0}", reader.readLine());
     
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    
     writer.printf(RouletteV1Protocol.CMD_LOAD + "\n");
     LOG.log(Level.INFO, "Server : {0}", reader.readLine());
     for(Student student:students){
        writer.printf(student.getFullname() + "\n");
     }
     writer.printf(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER + "\n");
     LOG.log(Level.INFO, "Server : {0}", reader.readLine());
     writer.flush();

    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
      String line = null;
     writer.printf(RouletteV1Protocol.CMD_RANDOM + "\n");
     writer.flush();
     line = reader.readLine();
     System.out.println("random sortie 2 serveur" + line);
     String[] result = line.split(":");
     if(result.length < 3)
        throw new EmptyStoreException();
     return new Student(result[1].substring(1, result[1].length()- 2));

    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public int getNumberOfStudents() throws IOException {
     String line;
     writer.printf(RouletteV1Protocol.CMD_INFO + "\n");
     writer.flush();
     line = reader.readLine();
     System.out.println("nombre d'etudiant sortie serveur" + line);
     String[] result = line.split(":");
     if(result.length < 3)
        return 0;
     System.out.println("nombre d'etudiant resultat split " + result[2]);
     String numberOfStudentStr = result[2].split("}")[0];
     System.out.println("nombre d'etudiant " + numberOfStudentStr);
     if(numberOfStudentStr.equalsIgnoreCase("0")){
        return 0;
     }
     return Integer.parseInt(numberOfStudentStr);

   // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String getProtocolVersion() throws IOException {
     
     String line;
     writer.printf(RouletteV1Protocol.CMD_INFO + "\n");
     writer.flush();
     line = reader.readLine();
     System.out.println("version sortie serveur" + line);
     String[] result = line.split(":");
     System.out.println("version 1er split sortie serveur" + result[1]);
     String result1 = result[1].split(",")[0];
     System.out.println("version 2eme split sortie serveur" + result1);
     return result1.substring(1,result1.length()-1);
    //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }



}
