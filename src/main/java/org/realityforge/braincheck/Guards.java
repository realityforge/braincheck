package org.realityforge.braincheck;

import java.util.function.Supplier;
import javax.annotation.Nonnull;

/**
 * A utility class used to perform assertions and invariant checks.
 */
public final class Guards
{
  private Guards()
  {
  }

  /**
   * Check an api invariant in the code base.
   * The invariant condition should return true if the library user is using the api correctly. This invariant
   * should be disabled in production environments but users of the library may choose to enable it when using
   * the library.
   *
   * <p>If the condition is false then an {@link IllegalStateException} is thrown.
   * The invariant check will be skipped unless the configuration setting {@link BrainCheckConfig#checkInvariants()}
   * is true. A null message is used rather than supplied message unless {@link BrainCheckConfig#verboseErrorMessages()}
   * is true.</p>
   *
   * @param condition the condition to check.
   * @param message   the message supplier used if verbose messages enabled.
   * @throws IllegalStateException if condition returns false.
   */
  public static void apiInvariant( @Nonnull final Supplier<Boolean> condition,
                                   @Nonnull final Supplier<String> message )
  {
    if ( BrainCheckConfig.checkApiInvariants() )
    {
      boolean conditionResult = isConditionTrue( condition, message );
      if ( !conditionResult )
      {
        fail( message );
      }
    }
  }

  /**
   * Check an invariant in code base.
   * The invariant condition should always return true. Returning false indicates that the host application
   * or library has an unexpected bug. These invariant checks may be compute intensive as they will likely
   * be disabled in production environments.
   *
   * <p>If the condition is false then an {@link IllegalStateException} is thrown.
   * The invariant check will be skipped unless the configuration setting {@link BrainCheckConfig#checkInvariants()}
   * is true. A null message is used rather than supplied message unless {@link BrainCheckConfig#verboseErrorMessages()}
   * is true.</p>
   *
   * @param condition the condition to check.
   * @param message   the message supplier used if verbose messages enabled.
   * @throws IllegalStateException if condition returns false.
   */
  public static void invariant( @Nonnull final Supplier<Boolean> condition,
                                @Nonnull final Supplier<String> message )
  {
    if ( BrainCheckConfig.checkInvariants() )
    {
      if ( !isConditionTrue( condition, message ) )
      {
        fail( message );
      }
    }
  }

  /**
   * Return the result of specified condition.
   * If there is an error resolving condition then fail or return false depending on config settings.
   *
   * @return the result of specified condition.
   */
  private static boolean isConditionTrue( @Nonnull final Supplier<Boolean> condition,
                                          @Nonnull final Supplier<String> message )
  {
    try
    {
      return condition.get();
    }
    catch ( final Throwable t )
    {
      fail( () -> "Error checking condition.\n" +
                  "Message: " + BrainCheckUtil.safeGetString( message ) + "\n" +
                  "Throwable:\n" + BrainCheckUtil.throwableToString( t ) );
    }
    return false;
  }

  /**
   * Throw an IllegalStateException with supplied detail message.
   * The exception is not thrown unless {@link BrainCheckConfig#checkInvariants()} is true.
   * The exception will ignore the supplied message unless {@link BrainCheckConfig#verboseErrorMessages()} is true.
   *
   * @param message the message supplier used if verbose messages enabled.
   * @throws IllegalStateException when called.
   */
  public static void fail( @Nonnull final Supplier<String> message )
  {
    if ( BrainCheckConfig.checkInvariants() )
    {
      AssertUtil.pauseIfDebuggerActive();
      if ( BrainCheckConfig.verboseErrorMessages() )
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
