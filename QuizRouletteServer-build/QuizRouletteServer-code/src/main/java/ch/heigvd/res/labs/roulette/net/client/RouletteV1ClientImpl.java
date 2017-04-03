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
 * @author Olivier Liechti, Annie Dongmo, Doriane Kaffo
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

   private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
   protected static Socket socket = null; //to connect on the server
   protected boolean isConnect = false;    //to know our state with server
   protected static BufferedReader reader; //to recover the server response
   protected static PrintWriter writer;    //to send commands to the server

   @Override
   public void connect(String server, int port) throws IOException {
      //if the client is not connected we create the socket and connect it on the 
      //correct server and port
      if (!isConnect) {
         System.out.println("try to connect on " + server + " : " + port);
         //we connect to the server on the giving port
         socket = new Socket(server, port);
         //initialisation of reader and writer 
         reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
         writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

         String line = null;
         //if we receive the first line of the server we can say that we are connected
         if ((line = reader.readLine()) != null) {
            System.out.println("random sortie 1 serveur" + line);
            isConnect = true;
         }
      }
   }

   @Override
   public void disconnect() throws IOException {
      /*if we are connected we send the command BYE to server to disconnect us and we 
       close reader and writer instead of socket*/
      if (isConnect) {
         writer.printf(RouletteV1Protocol.CMD_BYE + "\n");
         writer.flush();
         writer.close();
         reader.close();
         socket.close();
         isConnect = false;

      } 
   }

   @Override
   public boolean isConnected() {
      return isConnect;

   }

   @Override
   public void loadStudent(String fullname) throws IOException {
      /*if we are connected we send the comand LOAD to the server, retrieve server response,
       send the name of new student we want to add, send the command LOAD END and read the 
       answer of the server */
      if (isConnect) {
         writer.printf(RouletteV1Protocol.CMD_LOAD + "\n");
         writer.flush();
         LOG.log(Level.INFO, "Server : {0}", reader.readLine());
         writer.printf(fullname + "\n");
         writer.flush();
         writer.printf(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER + "\n");
         writer.flush();
         LOG.log(Level.INFO, "Server : {0}", reader.readLine());

      } else {//if we are not connected we throw an error
         throw new UnsupportedOperationException("Client is not connected");
      }

   }

   @Override
   public void loadStudents(List<Student> students) throws IOException {
      /*if we are connected we send the comand LOAD,retrieve server response, send
       the student's name line by line to the server,send the command LOAD END and retrieve 
       server response*/
      if (isConnect) {
         writer.printf(RouletteV1Protocol.CMD_LOAD + "\n");
         writer.flush();
         LOG.log(Level.INFO, "Server : {0}", reader.readLine());
         for (Student student : students) {
            writer.printf(student.getFullname() + "\n");
            writer.flush();
         }
         writer.printf(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER + "\n");
         writer.flush();
         LOG.log(Level.INFO, "Server : {0}", reader.readLine());
      } else {
         //if we are not connected we throw exception
         throw new UnsupportedOperationException("Client is not connected");
      }

   }

   @Override
   public Student pickRandomStudent() throws EmptyStoreException, IOException {
      /*if we are connected we send the command RANDOM to pick random student,retrieve server response
       check if there is no student (error) throw empty exception otherwise deserialize the response
       and return the pick student*/
      if (isConnect) {
         String line = null;
         writer.printf(RouletteV1Protocol.CMD_RANDOM + "\n");
         writer.flush();
         line = reader.readLine();
         LOG.log(Level.INFO, "Server : {0}", line);

         //we deserialize the response   
         RandomCommandResponse randomResponse = JsonObjectMapper.parseJson(line, RandomCommandResponse.class);

         /*if the line contains error its means that there is no student in store we can then
          throw an empty storee exception*/
         if (randomResponse.getError() != null) {
            throw new EmptyStoreException();
         }
         //we get the name of the student pick for return
         String name = randomResponse.getFullname();
         return new Student(name);

      }
      //if we are not connected we throw and error
      throw new UnsupportedOperationException("Client is not connect to the server");
   }

   @Override
   public int getNumberOfStudents() throws IOException {
      /*if we are connected we send the command INFO, deserialize the result, get the number
       of students and return it*/
      if (isConnect) {
         String line;
         writer.printf(RouletteV1Protocol.CMD_INFO + "\n");
         writer.flush();
         line = reader.readLine();
         System.out.println("nombre d'etudiant sortie serveur" + line);
         //we deserialize the server response 
         InfoCommandResponse infoResponse = JsonObjectMapper.parseJson(line, InfoCommandResponse.class);
         return infoResponse.getNumberOfStudents();

      }
      //if we are not connected we throw exception
      throw new UnsupportedOperationException("Client is not connectes");
   }

   @Override
   public String getProtocolVersion() throws IOException {
      /*if we are connected we send command INFO, deserialize the server response, get the 
       protocole version and return it*/
      if (isConnect) {
         String line;
         writer.printf(RouletteV1Protocol.CMD_INFO + "\n");
         writer.flush();
         line = reader.readLine();
         System.out.println("version sortie serveur" + line);
         InfoCommandResponse infoResponse = JsonObjectMapper.parseJson(line, InfoCommandResponse.class);
         return infoResponse.getProtocolVersion();

      }
      //if we are not connected we throw and error
      throw new UnsupportedOperationException("Client is not connect");
   }

}
