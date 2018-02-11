package org.realityforge.braincheck;

import java.lang.reflect.Field;
import javax.annotation.Nonnull;
import org.realityforge.anodoc.TestOnly;

/**
 * Utility class for interacting with BrainCheck in tests.
 */
@TestOnly
@GwtIncompatible
public final class BrainCheckTestUtil
{
  private BrainCheckTestUtil()
  {
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
      /*
       * This should really never happen but if it does add assertion (so code stops in debugger) or
       * failing that throw an exception.
       */
      assert !BrainCheckConfig.isProductionEnvironment();
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
