package org.realityforge.braincheck;

import jsinterop.annotations.JsProperty;

final class DebuggerUtil
{
  private DebuggerUtil()
  {
  }

  @JsProperty( namespace = "<window>", name = "debugger" )
  static native void debugger();
}
