package org.realityforge.braincheck;

import jsinterop.annotations.JsProperty;

final class AssertUtil
{
  /**
   * This flag will only be present and set when GWT is compiling the source code and the relevant
   * compile time property is defined. Thus this will be false in normal jre runtime environment.
   */
  private static final boolean DEBUGGER_ENABLED = "ENABLED".equals( System.getProperty( "jre.debugMode" ) );

  private AssertUtil()
  {
  }

  static void pauseIfDebuggerActive()
  {
    if ( DEBUGGER_ENABLED )
    {
      debugger();
    }
  }

  @JsProperty( namespace = "<window>", name = "debugger" )
  private static native void debugger();
}
