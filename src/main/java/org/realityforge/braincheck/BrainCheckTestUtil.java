package org.realityforge.braincheck;

import java.lang.reflect.Field;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utility class for interacting with BrainCheck in tests.
 */
@GwtIncompatible
public final class BrainCheckTestUtil
{
  private BrainCheckTestUtil()
  {
  }

  /**
   * The type of the guard.
   * This is only used during development process and will be optimized out in production mode.
   */
  @GwtIncompatible
  public enum GuardType
  {
    INVARIANT,
    API_INVARIANT
  }

  /**
   * Interface used to receive details about an invoked guard.
   * This is only used internally during development to collect the guards/invariants/etc to ensure
   * that they comply with patterns and to ensure they are documented appropriately.
   */
  @GwtIncompatible
  public interface OnGuardListener
  {
    void onGuard( @Nonnull GuardType type, @Nonnull String message, @Nonnull StackTraceElement[] stackTrace );
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
      setVerboseErrorMessages( false );
      setCheckInvariants( false );
      setCheckApiInvariants( false );
    }
    else
    {
      setVerboseErrorMessages( true );
      setCheckInvariants( true );
      setCheckApiInvariants( true );
    }
    setOnGuardListener( null );
  }

  /**
   * Configure the verbose error messages setting.
   *
   * @param verboseErrorMessages the verbose error messages setting.
   */
  public static void setVerboseErrorMessages( final boolean verboseErrorMessages )
  {
    setConstant( "VERBOSE_ERROR_MESSAGES", verboseErrorMessages );
  }

  /**
   * Configure the "check invariants" setting.
   *
   * @param checkInvariants the "check invariants" setting.
   */
  public static void setCheckInvariants( final boolean checkInvariants )
  {
    setConstant( "CHECK_INVARIANTS", checkInvariants );
  }

  /**
   * Configure the "check api invariants" setting.
   *
   * @param checkApiInvariants the "check api invariants" setting.
   */
  public static void setCheckApiInvariants( final boolean checkApiInvariants )
  {
    setConstant( "CHECK_API_INVARIANTS", checkApiInvariants );
  }

  /**
   * Set the specified field name on BrainCheckConfig.
   */
  @SuppressWarnings( "NonJREEmulationClassesInClientCode" )
  private static void setConstant( @Nonnull final String fieldName, final boolean value )
  {
    try
    {
      final Field field = BrainCheckConfig.class.getDeclaredField( fieldName );
      field.setAccessible( true );
      field.set( null, value );
    }
    catch ( NoSuchFieldException | IllegalAccessException e )
    {
      throw new IllegalStateException( "Unable to change constant " + fieldName, e );
    }
  }
}
