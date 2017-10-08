package org.realityforge.braincheck;

import java.lang.reflect.Field;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.TestOnly;

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
