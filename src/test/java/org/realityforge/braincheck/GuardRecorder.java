package org.realityforge.braincheck;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

final class GuardRecorder
  implements BrainCheckTestUtil.OnGuardListener
{
  private final List<String> _messages = new ArrayList<>();

  @Override
  public void onGuard( @Nonnull final BrainCheckTestUtil.GuardType type,
                       @Nonnull final String message,
                       @Nonnull final StackTraceElement[] stackTrace )
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
