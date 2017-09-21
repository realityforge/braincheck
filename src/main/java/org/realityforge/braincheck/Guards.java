package org.realityforge.braincheck;

import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;

/**
 * A utility class used to perform assertions and invariant checks.
 * The invariants
 */
public final class Guards
{
  private Guards()
  {
  }

  /**
   * Check an invariant in code base.
   * The invariant condition should always return true. Returning false indicates that the host application
   * or library has an unexpected bug. These invariant checks may be compute intensive as they will likely
   * be disabled in production environments.
   *
   * <p>If the condition is false then an {@link IllegalStateException} is thrown.
   * The invariant check will be skipped unless the configuration setting {@link Config#checkInvariants()}
   * is true. A null message is used rather than supplied message unless {@link Config#verboseErrorMessages()}
   * is true.</p>
   *
   * @param condition the condition to check.
   * @param message   the message supplier used if verbose messages enabled.
   * @throws IllegalStateException if condition returns false.
   */
  public static void invariant( @Nonnull final Supplier<Boolean> condition,
                                @Nonnull final Supplier<String> message )
  {
    if ( Config.checkInvariants() )
    {
      boolean conditionResult = false;
      try
      {
        conditionResult = condition.get();
      }
      catch ( final Throwable t )
      {
        fail( () -> "Error checking condition.\n" +
                    "Message: " + BrainCheckUtil.safeGetString( message ) + "\n" +
                    "Throwable:\n" + BrainCheckUtil.throwableToString( t ) );
      }
      if ( !conditionResult )
      {
        fail( message );
      }
    }
  }

  /**
   * Throw an IllegalStateException with supplied detail message.
   * The exception is not thrown unless {@link Config#checkInvariants()} is true.
   * The exception will ignore the supplied message unless {@link Config#verboseErrorMessages()} is true.
   *
   * @param message the message supplier used if verbose messages enabled.
   * @throws IllegalStateException when called.
   */
  @Contract( "_ -> fail" )
  public static void fail( @Nonnull final Supplier<String> message )
  {
    if ( Config.checkInvariants() )
    {
      if ( Config.verboseErrorMessages() )
      {
        throw new IllegalStateException( BrainCheckUtil.safeGetString( message ) );
      }
      else
      {
        throw new IllegalStateException();
      }
    }
  }
}
