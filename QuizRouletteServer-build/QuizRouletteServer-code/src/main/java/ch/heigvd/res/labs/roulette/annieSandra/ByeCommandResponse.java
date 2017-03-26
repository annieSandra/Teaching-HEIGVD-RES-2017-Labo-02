
package ch.heigvd.res.labs.roulette.annieSandra;

/**
 *
 * @author annie
 */
public class ByeCommandResponse {
   
   private String status;
   private int numberOfCommands;
   
   public String getStatus(){
      return status;     
   }
   public int getNumberOfCommands(){
      return numberOfCommands;
   }
   public ByeCommandResponse(String status, int numberOfCommands){
      this.status = status;
      this.numberOfCommands = numberOfCommands;
   }
   
}
