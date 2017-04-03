
package ch.heigvd.res.labs.roulette.annieSandra;

/**
 *
 * @author annie
 */
public class LoadCommandResponse {
   
   private String status;
   private int numberOfNewStudents;
   
   public String getStatus(){
      return status;
   }
   public int getNumberOfNewStudents(){
     return numberOfNewStudents;  
   }
   public LoadCommandResponse(String status, int numberOfStudents){
      this.status = status;
      numberOfNewStudents = numberOfStudents;
   }
   
}
