package org.realityforge.braincheck;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class BrainCheckUtilTest
{
  @Test
  public void safeGetString()
    throws Exception
  {
    assertEquals( BrainCheckUtil.safeGetString( () -> "My String" ), "My String" );
  }

  @Test
  public void safeGetString_generatesError()
    throws Exception
  {
    final String text = BrainCheckUtil.safeGetString( () -> {
      throw new RuntimeException( "X" );
    } );
    assertTrue( text.startsWith( "Exception generated whilst attempting to get supplied message.\n" +
                                 "java.lang.RuntimeException: X\n" ) );
  }

  @Test
  public void throwableToString()
    throws Exception
  {
    final String text = BrainCheckUtil.throwableToString( new RuntimeException( "X" ) );
    assertTrue( text.startsWith( "java.lang.RuntimeException: X\n" ) );
  }
}
