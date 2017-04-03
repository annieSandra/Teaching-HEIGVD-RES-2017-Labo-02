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
      /*if we are connect to the server we send her the command clear */
      if (isConnect) {
         writer.printf(RouletteV2Protocol.CMD_CLEAR + "\n");
         writer.flush();
         LOG.log(Level.INFO, "Server : {0}", reader.readLine());
      } else {
         throw new UnsupportedOperationException("Client is not connected");
      }
   }

   @Override
   public List<Student> listStudents() throws IOException {
      /*if the client is connected we send the command list to the server, deserialize 
      the response of the server and return it*/
      if (isConnect) {
         writer.printf(RouletteV2Protocol.CMD_LIST + "\n");
         writer.flush();
         String responseList = reader.readLine();
         LOG.log(Level.INFO, "Server : {0}", responseList);
         StudentsList list = JsonObjectMapper.parseJson(responseList, StudentsList.class);
         return list.getStudents();

      }
      //if not we throw an exception
      throw new UnsupportedOperationException("Client is not connect");
   }

   /*this method send the command LOAD, get the number of new students he has load on the store
   and return the result  */
   public int getNumberOfNewStudents(List<Student> students) throws IOException {
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
