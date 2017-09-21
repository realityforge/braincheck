package org.realityforge.braincheck;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.TestOnly;

/**
 * Utility class for interacting with BrainCheck in tests.
 */
@TestOnly
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
    getConfigProvider().setVerboseErrorMessages( verboseErrorMessages );
  }

  /**
   * Configure the "check invariants" setting.
   *
   * @param checkInvariants the "check invariants" setting.
   */
  public static void setCheckInvariants( final boolean checkInvariants )
  {
    getConfigProvider().setCheckInvariants( checkInvariants );
  }

  /**
   * Configure the "check api invariants" setting.
   *
   * @param checkApiInvariants the "check api invariants" setting.
   */
  public static void setCheckApiInvariants( final boolean checkApiInvariants )
  {
    getConfigProvider().setCheckApiInvariants( checkApiInvariants );
  }

  /**
   * Return the underlying config provider.
   * Generate an exception if the provider is not a DynamicProvider.
   */
  @Nonnull
  private static BrainCheckConfig.DynamicProvider getConfigProvider()
  {
    final BrainCheckConfig.Provider provider = BrainCheckConfig.getProvider();
    if ( !( provider instanceof BrainCheckConfig.DynamicProvider ) )
    {
      final String message =
        "To use BrainCheckTestUtil you need to ensure that the system property 'braincheck.dynamic_provider' " +
        "is set to true. This slows down configuration checking and should only be enabled in test environments.";
      throw new IllegalStateException( message );
    }
    return (BrainCheckConfig.DynamicProvider) provider;
  }
}
