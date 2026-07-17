package org.realityforge.braincheck;

import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

/**
 * Utility class for interacting with BrainCheck in tests.
 */
public final class BrainCheckTestUtil
{
  private BrainCheckTestUtil()
  {
  }

  /**
   * The type of the guard.
   * This is only used during development process and will be optimized out in production mode.
   */
  public enum GuardType
  {
    /**
     * A guard type indicating a failure.
     */
    FAIL,
    /**
     * A guard type indicating a failure of an invariant condition in the codebase.
     */
    INVARIANT,
    /**
     * A guard type indicating a failure of an api invariant condition in the codebase.
     */
    API_INVARIANT
  }

  /**
   * Interface used to receive details about an invoked guard.
   * This is only used internally during development to collect the guards/invariants/etc to ensure
   * that they comply with patterns and to ensure they are documented appropriately.
   */
  public interface OnGuardListener
  {
    /**
     * Invoked when a guard is triggered. This method provides details about the guard type,
     * the associated message, and the stack trace at the point of invocation.
     *
     * @param type       The type of the guard (e.g., FAIL, INVARIANT, API_INVARIANT). Must not be null.
     * @param message    A detailed message describing the reason for the guard invocation. Must not be null.
     * @param stackTrace The stack trace captured at the time of the guard invocation. Must not be null.
     */
    void onGuard( GuardType type, String message, StackTraceElement[] stackTrace );
  }

  /**
   * Specify a callback that is invoked anytime the {@link Guards#invariant(Supplier, Supplier)} method or
   * the {@link Guards#apiInvariant(Supplier, Supplier)} method is invoked while in development mode.
   *
   * @param onGuardListener the listener.
   */
  public static void setOnGuardListener( @Nullable final OnGuardListener onGuardListener )
  {
    if ( null == onGuardListener )
    {
      Guards.setOnGuardListener( null );
    }
    else
    {
      final Guards.OnGuardListener listener =
        ( type, message, stackTrace ) -> onGuardListener.onGuard( GuardType.valueOf( type.name() ),
                                                                  message,
                                                                  stackTrace );
      Guards.setOnGuardListener( listener );
    }
  }

  /**
   * Reset the state of BrainCheck config to either production or development state.
   *
   * @param productionMode true to set it to production mode configuration, false to set it to development mode config.
   */
  public static void resetConfig( final boolean productionMode )
  {
    if ( BrainCheckConfig.isProductionEnvironment() )
    {
      throw new IllegalStateException( "Unable to reset config as BrainCheck is in production mode" );
    }

    if ( productionMode )
    {
      BrainCheckConfig.setVerboseErrorMessages( false );
      BrainCheckConfig.setCheckInvariants( false );
      BrainCheckConfig.setCheckApiInvariants( false );
    }
    else
    {
      BrainCheckConfig.setVerboseErrorMessages( true );
      BrainCheckConfig.setCheckInvariants( true );
      BrainCheckConfig.setCheckApiInvariants( true );
    }
    setOnGuardListener( null );
  }
}
