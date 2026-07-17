package org.realityforge.braincheck;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

final class GuardRecorder
  implements BrainCheckTestUtil.OnGuardListener
{
  private final List<String> _messages = new ArrayList<>();

  @Override
  public void onGuard( final BrainCheckTestUtil.GuardType type,
                       final String message,
                       final StackTraceElement[] stackTrace )
  {
    _messages.add( type +
                   ": " +
                   message +
                   " @ " +
                   stackTrace[ 0 ].getClassName() +
                   ":" +
                   stackTrace[ 0 ].getMethodName() );
  }

  @Override
  public String toString()
  {
    return _messages.stream().collect( Collectors.joining( "\n" ) );
  }
}
