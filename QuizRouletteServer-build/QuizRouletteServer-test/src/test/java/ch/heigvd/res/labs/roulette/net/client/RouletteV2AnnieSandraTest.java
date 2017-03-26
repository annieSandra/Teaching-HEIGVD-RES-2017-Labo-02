/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.data.*;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.rules.ExpectedException;
import java.util.ArrayList;
import java.util.List;

/**
 * we add some test with method which is not defined in client class RouletteV2Impl, so we have to
 * add them if we want to test them correctly.  
 * 
 * @author annie Dongmo, doriane Kaffo
 */
@Ignore
public class RouletteV2AnnieSandraTest {
     @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  @Test
  @TestAuthor(githubId = "annieSandra")
  public void theTestRouletteServerShouldRunDuringTests() throws IOException {
    assertTrue(roulettePair.getServer().isRunning());
  }

  @Test
  @TestAuthor(githubId = "annieSandra")
  public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
    assertTrue(roulettePair.getClient().isConnected());
  }

  @Test
  @TestAuthor(githubId = "annieSandra")
  public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    assertFalse(client.isConnected());
    client.connect("localhost", port);
    assertTrue(client.isConnected());
  }
  
  @Test
  @TestAuthor(githubId = "annieSandra")
  public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }

  @Test
  @TestAuthor(githubId = "annieSandra")
  public void theServerShouldHaveZeroStudentsAtStart() throws IOException {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    client.connect("localhost", port);
    int numberOfStudents = client.getNumberOfStudents();
    assertEquals(0, numberOfStudents);
  }

  @Test
  @TestAuthor(githubId = {"annieSandra", "dorianeKaffo"})
  public void theServerShouldStillHaveZeroStudentsAtStart() throws IOException {
    assertEquals(0, roulettePair.getClient().getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = {"annieSandra", "dorianeKaffo"})
  public void theServerShouldCountStudents() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    assertEquals(0, client.getNumberOfStudents());
    client.loadStudent("sandra");
    assertEquals(1, client.getNumberOfStudents());
    client.loadStudent("annie");
    assertEquals(2, client.getNumberOfStudents());
    client.loadStudent("fabienne");
    assertEquals(3, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = {"annieSandra", "dorianeKaffo"})
  public void theServerShouldSendAnErrorResponseWhenRandomIsCalledAndThereIsNoStudent() throws IOException, EmptyStoreException {
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    exception.expect(EmptyStoreException.class);
    client.pickRandomStudent();
  }
  
  
  @Test
  @TestAuthor(githubId = {"annieSandra", "dorianeKaffo"})
  public void theServerShouldCountTheCorrectNumberOfNewStudent() throws IOException, EmptyStoreException {
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    List<Student> students = new ArrayList<>();
    students.add(new Student("anne"));
    students.add(new Student("rose"));
    students.add(new Student("dongmo"));
    students.add(new Student("sandra"));
    int numberOfNewStudent = client.getNumberOfNewStudent(students);
    assertEquals(4, numberOfNewStudent);
    numberOfNewStudent = client.getNumberOfNewStudent("hugo");
    assertEquals(1, numberOfNewStudent);
  }
  
  
  @Test 
  @TestAuthor(githubId = {"annieSandra", "dorianeKaffo"})
  public void theServerShouldSendStatusOfCommand() throws IOException, EmptyStoreException{
     IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
     assertEquals("success",client.getStatusCommandLoad("anne"));
  }
  
  
  @Test 
  @TestAuthor(githubId = {"annieSandra", "dorianeKaffo"})
  public void theServerShouldSendTheCorrectNumberOfCommand() throws IOException, EmptyStoreException {
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    client.getNumberOfStudents();
    client.loadStudent("biphaga");
    client.pickRandomStudent();
    int numberOfCommand = client.getNumberOfCommand();
    assertEquals(4, numberOfCommand);
  }
  
  @Test 
  @TestAuthor(githubId = {"annieSandra", "dorianeKaffo"})
  public void theServerShouldHaveZeroStudentAfterClearCommand() throws IOException, EmptyStoreException{
     IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
     client.loadStudent("terri");
     client.clearDataStore();
     assertEquals(0, client.getNumberOfStudents());
	
  }
  

   
}
