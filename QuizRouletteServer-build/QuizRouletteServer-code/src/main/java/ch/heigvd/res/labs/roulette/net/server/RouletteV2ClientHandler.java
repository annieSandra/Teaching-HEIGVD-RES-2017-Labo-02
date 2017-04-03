package ch.heigvd.res.labs.roulette.net.server;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.IStudentsStore;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.annieSandra.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.annieSandra.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import static ch.heigvd.res.labs.roulette.net.server.RouletteV2ClientHandler.LOG;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Roulette protocol (version 2).
 *
 * @author Olivier Liechti, annie Dongmo, doriane Kaffo
 */
public class RouletteV2ClientHandler implements IClientHandler {
   
  final static Logger LOG = Logger.getLogger(RouletteV2ClientHandler.class.getName());

  private final IStudentsStore store;


  public RouletteV2ClientHandler(IStudentsStore store) {
     this.store = store;  
  }

  @Override
  public void handleClientConnection(InputStream is, OutputStream os) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(os,"UTF-8"));

    writer.println("Hello. Online HELP is available. Will you find it?");
    writer.flush();

    String command;
    int numberOfCommand = 0;
    boolean done = false;
    while (!done && ((command = reader.readLine()) != null)) {
      LOG.log(Level.INFO, "COMMAND: {0}", command);
      switch (command.toUpperCase()) {
        case RouletteV2Protocol.CMD_RANDOM:
          ++numberOfCommand;
          RandomCommandResponse rcResponse = new RandomCommandResponse();
          try {
            rcResponse.setFullname(store.pickRandomStudent().getFullname());
          } catch (EmptyStoreException ex) {
            rcResponse.setError("There is no student, you cannot pick a random one");
          }
          writer.println(JsonObjectMapper.toJson(rcResponse));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_HELP:
          ++numberOfCommand;
          writer.println("Commands: " + Arrays.toString(RouletteV2Protocol.SUPPORTED_COMMANDS));
          break;
        case RouletteV2Protocol.CMD_INFO:
          ++numberOfCommand;
          InfoCommandResponse response = new InfoCommandResponse(RouletteV2Protocol.VERSION, store.getNumberOfStudents());
          writer.println(JsonObjectMapper.toJson(response));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LOAD:
          ++numberOfCommand;
          writer.println(RouletteV2Protocol.RESPONSE_LOAD_START);
          writer.flush();
          LOG.log(Level.INFO, "Importing data from input reader of type {0}", reader.getClass());
          String record;
          int numberOfNewStudent = 0;
          boolean endReached = false;
          /*we read line by line the line enter by the client while we don't receive the ENDOFLOAD to end the 
            command LOAD*/
          while (!endReached && (record = reader.readLine()) != null) {
           if (record.equalsIgnoreCase(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER)) {
             LOG.log(Level.INFO, "End of stream reached. New students have been added to the store. How many? We'll tell you when the lab is complete...");
             endReached = true;
           } else {
             LOG.log(Level.INFO, "Adding student {0} to the store.", record);
             store.addStudent(new Student(record));
             //we count the number of student enter by the client 
             ++numberOfNewStudent;
           }
          }
          //create the new object to response to load command, transform it on JSON format and 
          //send it to the client 
          LoadCommandResponse responseLoad = new LoadCommandResponse("success", numberOfNewStudent);
          writer.println(JsonObjectMapper.toJson(responseLoad));
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_LIST:
          ++numberOfCommand;
          StudentsList studentsList = new StudentsList ();
          studentsList.setStudents(store.listStudents());
          writer.println(JsonObjectMapper.toJson(studentsList));
          writer.flush();
          break;
         case RouletteV2Protocol.CMD_CLEAR:
          ++numberOfCommand;
          //we clear the data store and we  send a response to the client
          store.clear();
          writer.println(RouletteV2Protocol.RESPONSE_CLEAR_DONE);
          writer.flush();
          break;
        case RouletteV2Protocol.CMD_BYE:
          ++numberOfCommand;
          ByeCommandResponse responseBye = new ByeCommandResponse ("success",numberOfCommand);
          writer.println(JsonObjectMapper.toJson(responseBye));
          writer.flush();
          done = true;
          break;
        default:
          writer.println("Huh? please use HELP if you don't know what commands are available.");
          writer.flush();
          break;
      }
      writer.flush();
    }


  }

}
