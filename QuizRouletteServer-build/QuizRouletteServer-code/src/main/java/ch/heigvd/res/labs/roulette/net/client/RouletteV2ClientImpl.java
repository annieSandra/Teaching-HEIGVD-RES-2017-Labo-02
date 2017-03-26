package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import static ch.heigvd.res.labs.roulette.net.client.RouletteV1ClientImpl.reader;
import ch.heigvd.res.labs.roulette.annieSandra.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti, annie Dongmo, Doriane Kaffo
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());
  @Override
  public void clearDataStore() throws IOException {
     /*we send the command clear to the server*/
     if(isConnect){
     writer.printf(RouletteV2Protocol.CMD_CLEAR + "\n");
     writer.flush();
     LOG.log(Level.INFO, "Server : {0}", reader.readLine());
     }else
       throw new UnsupportedOperationException("Client is not connected"); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<Student> listStudents() throws IOException {
     if(isConnect){
          writer.printf(RouletteV2Protocol.CMD_LIST + "\n");
     writer.flush();
     String responseList = reader.readLine();
     LOG.log(Level.INFO, "Server : {0}", responseList);  
     StudentsList list = JsonObjectMapper.parseJson(responseList, StudentsList.class);
     return list.getStudents();
     
     }
      throw new UnsupportedOperationException("Client is not connect"); //To change body of generated methods, choose Tools | Templates.
  }
  
  public int getNumberOfNewStudents(List <Student> students) throws IOException {
     writer.printf(RouletteV2Protocol.CMD_LOAD + "\n");
         writer.flush();
         LOG.log(Level.INFO, "Server : {0}", reader.readLine());
         for (Student student : students) {
            writer.printf(student.getFullname() + "\n");
            writer.flush();
         }
         writer.printf(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER + "\n");
         writer.flush();
         String response = reader.readLine();
         LoadCommandResponse resp = JsonObjectMapper.parseJson(response, LoadCommandResponse.class);
         return resp.getNumberOfNewStudents();   
  }
  
}
