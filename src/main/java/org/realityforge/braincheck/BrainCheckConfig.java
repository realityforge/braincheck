package org.realityforge.braincheck;

/**
 * Location of all compile time configuration settings for the toolkit.
 */
public final class BrainCheckConfig
{
  private static final boolean PRODUCTION_ENVIRONMENT =
    System.getProperty( "braincheck.environment", "production" ).equals( "production" );
  private static boolean VERBOSE_ERROR_MESSAGES =
    System.getProperty( "braincheck.verbose_error_messages", PRODUCTION_ENVIRONMENT ? "false" : "true" ).
      equals( "true" );
  private static boolean CHECK_INVARIANTS =
    System.getProperty( "braincheck.check_invariants", PRODUCTION_ENVIRONMENT ? "false" : "true" ).equals( "true" );
  private static boolean CHECK_API_INVARIANTS =
    System.getProperty( "braincheck.check_api_invariants", PRODUCTION_ENVIRONMENT ? "false" : "true" ).
      equals( "true" );

  private BrainCheckConfig()
  {
  }

  /**
   * Return true if BrainCheck is running in production mode.
   *
   * @return true if BrainCheck is running in production mode.
   */
  static boolean isProductionEnvironment()
  {
    return PRODUCTION_ENVIRONMENT;
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
