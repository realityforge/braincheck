package org.realityforge.braincheck;

/**
 * Location of all compile time configuration settings for the toolkit.
 */
public final class BrainCheckConfig
{
  private static boolean VERBOSE_ERROR_MESSAGES =
    "true".equals( System.getProperty( "braincheck.verbose_error_messages" ) );
  private static boolean CHECK_INVARIANTS =
    "true".equals( System.getProperty( "braincheck.check_invariants" ) );
  private static boolean CHECK_API_INVARIANTS =
    "true".equals( System.getProperty( "braincheck.check_api_invariants" ) );

  private BrainCheckConfig()
  {
  }

  /**
   * Return true if invariant failures will include a detail message.
   *
   * @return true if invariant failures will include a detail message.
   */
  public static boolean verboseErrorMessages()
  {
    return VERBOSE_ERROR_MESSAGES;
  }

  /**
   * Return true if invariants will be checked.
   *
   * @return true if invariants will be checked.
   */
  public static boolean checkInvariants()
  {
    return CHECK_INVARIANTS;
  }

  /**
   * Return true if apiInvariants will be checked.
   *
   * @return true if apiInvariants will be checked.
   */
  public static boolean checkApiInvariants()
  {
    return CHECK_API_INVARIANTS;
  }
}
