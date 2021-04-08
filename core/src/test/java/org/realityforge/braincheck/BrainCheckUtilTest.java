package org.realityforge.braincheck;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class BrainCheckUtilTest
{
  @Test
  public void safeGetString()
  {
    assertEquals( BrainCheckUtil.safeGetString( () -> "My String" ), "My String" );
  }

  @Test
  public void safeGetString_generatesError()
  {
    final String text = BrainCheckUtil.safeGetString( () -> {
      throw new RuntimeException( "X" );
    } );
    assertTrue( text.startsWith( "Exception generated whilst attempting to get supplied message.\n" +
                                 "java.lang.RuntimeException: X\n" ) );
  }

  @Test
  public void throwableToString()
  {
    final String text = BrainCheckUtil.throwableToString( new RuntimeException( "X" ) );
    assertTrue( text.startsWith( "java.lang.RuntimeException: X\n" ) );
  }
}
