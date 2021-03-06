package org.realityforge.braincheck;

import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A utility class used to perform assertions and invariant checks.
 */
public final class Guards
{
  @Nullable
  private static OnGuardListener c_onGuardListener;

  /**
   * The type of the guard.
   * This is only used during development process and will be optimized out in production mode.
   */
  enum Type
  {
    FAIL,
    INVARIANT,
    API_INVARIANT
  }

  /**
   * Interface used to receive details about an invoked guard.
   * This is only used internally during development to collect the guards/invariants/etc to ensure
   * that they comply with patterns and to ensure they are documented appropriately.
   */
  interface OnGuardListener
  {
    void onGuard( @Nonnull Type type, @Nonnull String message, @Nonnull StackTraceElement[] stackTrace );
  }

  static void setOnGuardListener( @Nullable final OnGuardListener onGuardListener )
  {
    assert BrainCheckConfig.isDevelopmentEnvironment();
    c_onGuardListener = onGuardListener;
  }

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
    if ( BrainCheckConfig.isDevelopmentEnvironment() && null != c_onGuardListener )
    {
      c_onGuardListener.onGuard( Type.API_INVARIANT,
                                 BrainCheckUtil.safeGetString( message ),
                                 StackTraceUtil.getStackTrace( 2 ) );
    }
    if ( BrainCheckConfig.checkApiInvariants() )
    {
      boolean conditionResult = isConditionTrue( condition, message );
      if ( !conditionResult )
      {
        doFail( message );
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
    if ( BrainCheckConfig.isDevelopmentEnvironment() && null != c_onGuardListener )
    {
      c_onGuardListener.onGuard( Type.INVARIANT,
                                 BrainCheckUtil.safeGetString( message ),
                                 StackTraceUtil.getStackTrace( 2 ) );
    }
    if ( BrainCheckConfig.checkInvariants() )
    {
      if ( !isConditionTrue( condition, message ) )
      {
        doFail( message );
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
      doFail( () -> "Error checking condition.\n" +
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
    if ( BrainCheckConfig.isDevelopmentEnvironment() && null != c_onGuardListener )
    {
      c_onGuardListener.onGuard( Type.FAIL,
                                 BrainCheckUtil.safeGetString( message ),
                                 StackTraceUtil.getStackTrace( 2 ) );
    }
    if ( BrainCheckConfig.checkInvariants() )
    {
      doFail( message );
    }
  }

  private static void doFail( @Nonnull final Supplier<String> message )
  {
    /*
     * This flag will only be present and set when GWT is compiling the source code and the relevant
     * compile time property is defined. Thus this will be false in normal jre runtime environment.
     */
    if ( "ENABLED".equals( System.getProperty( "jre.debugMode" ) ) )
    {
      DebuggerUtil.debugger();
    }
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
