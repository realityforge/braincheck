package org.realityforge.braincheck;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class BrainCheckTestUtilTest
  extends AbstractBraincheckTest
{
  @Test
  public void onGuardListenerReceivesMessages()
  {
    final GuardRecorder recorder = new GuardRecorder();
    BrainCheckTestUtil.setOnGuardListener( recorder );

    assertEquals( recorder.toString(), "" );

    assertThrows( () -> Guards.invariant( () -> false, () -> "Some Message" ) );

    assertEquals( recorder.toString(),
                  "INVARIANT: Some Message @ org.realityforge.braincheck.BrainCheckTestUtilTest:lambda$onGuardListenerReceivesMessages$2" );

    Guards.invariant( () -> true, () -> "Some Other Message" );

    assertEquals( recorder.toString(),
                  "INVARIANT: Some Message @ org.realityforge.braincheck.BrainCheckTestUtilTest:lambda$onGuardListenerReceivesMessages$2\n" +
                  "INVARIANT: Some Other Message @ org.realityforge.braincheck.BrainCheckTestUtilTest:onGuardListenerReceivesMessages" );

    Guards.apiInvariant( () -> true, () -> "Blah" );

    assertEquals( recorder.toString(),
                  "INVARIANT: Some Message @ org.realityforge.braincheck.BrainCheckTestUtilTest:lambda$onGuardListenerReceivesMessages$2\n" +
                  "INVARIANT: Some Other Message @ org.realityforge.braincheck.BrainCheckTestUtilTest:onGuardListenerReceivesMessages\n" +
                  "API_INVARIANT: Blah @ org.realityforge.braincheck.BrainCheckTestUtilTest:onGuardListenerReceivesMessages" );
  }
}
