package org.realityforge.braincheck;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class StackTraceUtilTest
  extends AbstractBraincheckTest
{
  @Test
  public void basicOperation()
  {
    final StackTraceElement[] stackTrace = StackTraceUtil.getStackTrace( 1 );
    assertEquals( stackTrace[ 0 ].getClassName(), StackTraceUtilTest.class.getName() );
    assertEquals( stackTrace[ 0 ].getMethodName(), "basicOperation" );
  }
}
