package org.realityforge.braincheck;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Collections;
import javax.annotation.Nonnull;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class GuardMessageCollectorTest
  extends AbstractBraincheckTest
{
  @Test
  public void matchExistingMessage()
    throws Exception
  {
    final Path messageTemplates = getMessageTemplatesFile();
    final String content =
      "[{\"code\":1234,\"type\":\"INVARIANT\",\"messagePattern\":\"Some message\",\"callers\":[{\"class\":\"org.realityforge.braincheck.GuardMessageCollectorTest\",\"method\":\"matchExistingMessage\",\"file\":\"GuardMessageCollectorTest.java\",\"lineNumber\":37}]}]";
    final byte[] bytes = content.getBytes( StandardCharsets.UTF_8 );
    Files.write( messageTemplates, bytes );

    final GuardMessageCollector collector = new GuardMessageCollector( "Arez", messageTemplates.toFile() );

    collector.onTestSuiteStart();
    collector.onTestStart();
    Guards.invariant( () -> true, () -> "Arez-1234: Some message" );
    collector.onTestComplete();
    collector.onTestSuiteComplete( true );

    // The data would be formatted differently if it did not match
    assertEquals( Files.readAllBytes( messageTemplates ),
                  bytes,
                  "Actual: " + readMessageTemplates( messageTemplates ) );
  }

  @Test
  public void recordMatchingMessage_whenSaveIfChanged()
    throws Exception
  {
    final Path messageTemplates = getMessageTemplatesFile();

    final GuardMessageCollector collector = new GuardMessageCollector( "Arez", messageTemplates.toFile() );

    assertFalse( Files.exists( messageTemplates ) );
    collector.onTestSuiteStart();
    assertFalse( Files.exists( messageTemplates ) );

    collector.onTestStart();
    Guards.invariant( () -> true, () -> "Arez-1234: Some message" );

    assertFalse( Files.exists( messageTemplates ) );
    collector.onTestComplete();
    assertFalse( Files.exists( messageTemplates ) );

    collector.onTestSuiteComplete( true );
    assertTrue( Files.exists( messageTemplates ) );

    final JsonArray messages = readMessageTemplates( messageTemplates );
    assertEquals( messages.size(), 1 );
    assertSingleCallerMessage( messages,
                               0,
                               1234,
                               "INVARIANT",
                               "Some message",
                               "recordMatchingMessage_whenSaveIfChanged" );
  }

  @Test
  public void existingMessageNotMatchedButTestSuiteHasErrors()
    throws Exception
  {
    final Path messageTemplates = getMessageTemplatesFile();
    final String content = "[{\"code\":70,\"type\":\"API_INVARIANT\",\"messagePattern\":\"Bleep\",\"callers\":[]}]";
    final byte[] bytes = content.getBytes( StandardCharsets.UTF_8 );
    Files.write( messageTemplates, bytes );

    final GuardMessageCollector collector = new GuardMessageCollector( "Arez", messageTemplates.toFile() );

    collector.onTestSuiteStart();
    collector.onTestStart();
    collector.onTestComplete();
    collector.onTestSuiteComplete( false );

    final JsonArray messages = readMessageTemplates( messageTemplates );
    assertEquals( messages.size(), 1 );
    ensureMessage( messages, 0, 70, "API_INVARIANT", "Bleep" );
  }

  @Test
  public void existingMessageNotMatchedButDeleteIfUnmatchedFalse()
    throws Exception
  {
    final Path messageTemplates = getMessageTemplatesFile();
    final String content = "[{\"code\":70,\"type\":\"API_INVARIANT\",\"messagePattern\":\"Bleep\",\"callers\":[]}]";
    final byte[] bytes = content.getBytes( StandardCharsets.UTF_8 );
    Files.write( messageTemplates, bytes );

    final GuardMessageCollector collector = new GuardMessageCollector( "Arez", messageTemplates.toFile(), true, false );

    collector.onTestSuiteStart();
    collector.onTestStart();
    collector.onTestComplete();
    collector.onTestSuiteComplete( true );

    final JsonArray messages = readMessageTemplates( messageTemplates );
    assertEquals( messages.size(), 1 );
    ensureMessage( messages, 0, 70, "API_INVARIANT", "Bleep" );
  }

  @Test
  public void existingMessageNotMatched()
    throws Exception
  {
    final Path messageTemplates = getMessageTemplatesFile();
    final String content = "[{\"code\":70,\"type\":\"API_INVARIANT\",\"messagePattern\":\"Bleep\",\"callers\":[]}]";
    final byte[] bytes = content.getBytes( StandardCharsets.UTF_8 );
    Files.write( messageTemplates, bytes );

    final GuardMessageCollector collector = new GuardMessageCollector( "Arez", messageTemplates.toFile() );

    collector.onTestSuiteStart();
    collector.onTestStart();
    collector.onTestComplete();
    collector.onTestSuiteComplete( true );

    final JsonArray messages = readMessageTemplates( messageTemplates );
    assertEquals( messages.size(), 0 );
  }

  @Test
  public void ignoreUnmatchedMessage()
    throws Exception
  {
    final Path messageTemplates = getMessageTemplatesFile();

    final GuardMessageCollector collector = new GuardMessageCollector( "Arez", messageTemplates.toFile() );

    collector.onTestSuiteStart();
    collector.onTestStart();
    Guards.invariant( () -> true, () -> "Other-1234: Some message" );
    Guards.invariant( () -> true, () -> "Random string not matching message format" );
    collector.onTestComplete();
    collector.onTestSuiteComplete( true );
    assertFalse( Files.exists( messageTemplates ) );
  }

  @Test
  public void fileUnReadable()
    throws Exception
  {
    final Path messageTemplates = getMessageTemplatesFile();
    Files.write( messageTemplates, new byte[ 0 ] );
    Files.setPosixFilePermissions( messageTemplates, Collections.emptySet() );

    final GuardMessageCollector collector = new GuardMessageCollector( "Arez", messageTemplates.toFile() );

    final IllegalStateException exception = expectThrows( IllegalStateException.class, collector::onTestSuiteStart );
    assertEquals( exception.getMessage(), "Failed to read diagnostic messages file " + messageTemplates + "." );
    Files.setPosixFilePermissions( messageTemplates, Collections.singleton( PosixFilePermission.OWNER_WRITE ) );
    Files.delete( messageTemplates );
  }

  @Test
  public void fileUnwriteable()
    throws Exception
  {
    final Path messageTemplates = getMessageTemplatesFile();
    final byte[] bytes = "[]".getBytes( StandardCharsets.UTF_8 );
    Files.write( messageTemplates, bytes );
    Files.setPosixFilePermissions( messageTemplates, Collections.singleton( PosixFilePermission.OWNER_READ ) );

    final GuardMessageCollector collector =
      new GuardMessageCollector( "Spritz", messageTemplates.toFile() );

    collector.onTestSuiteStart();
    collector.onTestStart();
    Guards.invariant( () -> true, () -> "Spritz-0012: Blah" );
    Guards.invariant( () -> true, () -> "Random string not matching message format" );
    collector.onTestComplete();
    final IllegalStateException exception =
      expectThrows( IllegalStateException.class, () -> collector.onTestSuiteComplete( true ) );
    assertEquals( exception.getMessage(), "Failed to write diagnostic messages file " + messageTemplates + "." );
    assertEquals( Files.readAllBytes( messageTemplates ), bytes );
  }

  @Test
  public void errorWhenUnmatchedMessageRecorded()
    throws Exception
  {
    final Path messageTemplates = getMessageTemplatesFile();

    final GuardMessageCollector collector = new GuardMessageCollector( "Arez", messageTemplates.toFile(), false );

    collector.onTestSuiteStart();
    collector.onTestStart();
    Guards.invariant( () -> true, () -> "Arez-1234: Some message" );
    collector.onTestComplete();
    final IllegalStateException exception =
      expectThrows( IllegalStateException.class, () -> collector.onTestSuiteComplete( true ) );

    assertEquals( exception.getMessage(),
                  "Diagnostic messages template is out of date. 1 messages need to be updated including messages:\n" +
                  "Arez-1234: Some message" );
  }

  @Test
  public void recordMatchingMessageWithMultipleCalls()
    throws Exception
  {
    final Path messageTemplates = getMessageTemplatesFile();

    final GuardMessageCollector collector = new GuardMessageCollector( "Arez", messageTemplates.toFile() );

    collector.onTestSuiteStart();
    collector.onTestStart();
    assertEquals( collector.getMatchFailureCount(), 0 );
    workerMethod1();
    assertEquals( collector.getMatchFailureCount(), 0 );
    workerMethod2();
    assertEquals( collector.getMatchFailureCount(), 0 );

    assertFalse( Files.exists( messageTemplates ) );
    collector.onTestComplete();
    assertFalse( Files.exists( messageTemplates ) );

    collector.onTestSuiteComplete( true );
    assertTrue( Files.exists( messageTemplates ) );

    final JsonArray messages = readMessageTemplates( messageTemplates );
    assertEquals( messages.size(), 1 );
    final JsonObject message = ensureMessage( messages, 0, 1234, "INVARIANT", "Some message" );
    final JsonArray callers = message.getJsonArray( "callers" );
    assertEquals( callers.size(), 2 );
    assertCaller( callers, 0, "workerMethod1" );
    assertCaller( callers, 1, "workerMethod2" );
  }

  @Test
  public void recordMatchingMessageFromFail()
    throws Exception
  {
    final Path messageTemplates = getMessageTemplatesFile();

    final GuardMessageCollector collector = new GuardMessageCollector( "Arez", messageTemplates.toFile() );

    collector.onTestSuiteStart();
    collector.onTestStart();

    assertEquals( collector.getMatchFailureCount(), 0 );
    workerMethod3();
    assertEquals( collector.getMatchFailureCount(), 0 );

    assertFalse( Files.exists( messageTemplates ) );
    collector.onTestComplete();
    assertFalse( Files.exists( messageTemplates ) );

    collector.onTestSuiteComplete( true );
    assertTrue( Files.exists( messageTemplates ) );

    final JsonArray messages = readMessageTemplates( messageTemplates );
    assertEquals( messages.size(), 1 );
    final JsonObject message = ensureMessage( messages, 0, 4567, "FAIL", "Blah blee" );
    final JsonArray callers = message.getJsonArray( "callers" );
    assertEquals( callers.size(), 1 );
    assertCaller( callers, 0, "workerMethod3" );
  }

  private void workerMethod1()
  {
    Guards.invariant( () -> true, () -> "Arez-1234: Some message" );
  }

  private void workerMethod2()
  {
    Guards.invariant( () -> true, () -> "Arez-1234: Some message" );
  }

  private void workerMethod3()
  {
    try
    {
      Guards.fail( () -> "Arez-4567: Blah blee" );
    }
    catch ( final Exception ignored )
    {
    }
  }

  @Test
  public void matchUsingWildcardMatch()
    throws Exception
  {
    final Path messageTemplates = getMessageTemplatesFile();
    final String content = "[{\"code\":1,\"type\":\"API_INVARIANT\",\"messagePattern\":\"Hello %s\",\"callers\":[]}]";
    final byte[] bytes = content.getBytes( StandardCharsets.UTF_8 );
    Files.write( messageTemplates, bytes );

    final GuardMessageCollector collector = new GuardMessageCollector( "Arez", messageTemplates.toFile() );

    collector.onTestSuiteStart();
    collector.onTestStart();
    Guards.apiInvariant( () -> true, () -> "Arez-0001: Hello bob" );
    Guards.apiInvariant( () -> true, () -> "Arez-0001: Hello fred" );
    Guards.apiInvariant( () -> true, () -> "Arez-0001: Hello other" );
    final AssertionError exception =
      expectThrows( AssertionError.class,
                    () -> Guards.apiInvariant( () -> true, () -> "Arez-0001: This no match" ) );
    assertEquals( exception.getMessage(),
                  "Failed to match API_INVARIANT diagnostic message with key Arez and code 1.\n" +
                  "Expected pattern:\n" +
                  "Hello %s\n" +
                  "\n" +
                  "Actual Message:\n" +
                  "This no match" );
  }

  @Test
  public void failMatchDueToBadType()
    throws Exception
  {
    final Path messageTemplates = getMessageTemplatesFile();
    final String content = "[{\"code\":1,\"type\":\"API_INVARIANT\",\"messagePattern\":\"Hello\",\"callers\":[]}]";
    final byte[] bytes = content.getBytes( StandardCharsets.UTF_8 );
    Files.write( messageTemplates, bytes );

    final GuardMessageCollector collector = new GuardMessageCollector( "Arez", messageTemplates.toFile() );

    collector.onTestSuiteStart();
    collector.onTestStart();
    assertEquals( collector.getMatchFailureCount(), 0 );
    Guards.apiInvariant( () -> true, () -> "Arez-0001: Hello" );
    assertEquals( collector.getMatchFailureCount(), 0 );
    final AssertionError exception =
      expectThrows( AssertionError.class, () -> Guards.invariant( () -> true, () -> "Arez-0001: Hello" ) );
    assertEquals( exception.getMessage(),
                  "Failed to match diagnostic message type with key Arez and code 1." );
    assertEquals( collector.getMatchFailureCount(), 1 );
  }

  @Nonnull
  private Path getMessageTemplatesFile()
    throws IOException
  {
    final Path messageTemplates = Files.createTempFile( "messages", ".json" );
    Files.delete( messageTemplates );
    messageTemplates.toFile().deleteOnExit();
    return messageTemplates;
  }

  @SuppressWarnings( "SameParameterValue" )
  private void assertSingleCallerMessage( @Nonnull final JsonArray messages,
                                          final int index,
                                          final int code,
                                          @Nonnull final String type,
                                          @Nonnull final String messagePattern,
                                          @Nonnull final String callerMethod )
  {
    final JsonObject message = ensureMessage( messages, index, code, type, messagePattern );
    final JsonArray callers = message.getJsonArray( "callers" );
    assertEquals( callers.size(), 1 );
    assertCaller( callers, 0, callerMethod );
  }

  @Nonnull
  private JsonObject ensureMessage( @Nonnull final JsonArray messages,
                                    final int index,
                                    final int code,
                                    @Nonnull final String type,
                                    @Nonnull final String messagePattern )
  {
    final JsonObject message = messages.getJsonObject( index );
    assertEquals( message.getInt( "code" ), code );
    assertEquals( message.getString( "type" ), type );
    assertEquals( message.getString( "messagePattern" ), messagePattern );
    return message;
  }

  private void assertCaller( @Nonnull final JsonArray callers, final int index, @Nonnull final String methodName )
  {
    assertTrue( callers.size() > index );
    final JsonObject caller = callers.getJsonObject( index );
    assertEquals( caller.getString( "class" ), "org.realityforge.braincheck.GuardMessageCollectorTest" );
    assertEquals( caller.getString( "method" ), methodName );
    assertEquals( caller.getString( "file" ), "GuardMessageCollectorTest.java" );
    assertTrue( caller.containsKey( "lineNumber" ) &&
                JsonValue.ValueType.NUMBER == caller.get( "lineNumber" ).getValueType() );
  }

  @Nonnull
  private JsonArray readMessageTemplates( @Nonnull final Path messageTemplates )
    throws IOException
  {
    try ( final Reader reader = new FileReader( messageTemplates.toFile() ) )
    {
      return readMessageTemplates( reader );
    }
  }

  @Nonnull
  private JsonArray readMessageTemplates( @Nonnull final Reader reader )
  {
    try ( final JsonReader jsonReader = Json.createReader( reader ) )
    {
      return jsonReader.readArray();
    }
  }
}
