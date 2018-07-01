package org.realityforge.braincheck;

/**
 * Location of all compile time configuration settings for the toolkit.
 */
@SuppressWarnings( "StringEquality" )
public final class BrainCheckConfig
{
  /**
   * The provider abstraction is needed due to limitations in the way J2CL performs dead-code elimination.
   * In J2CL the code <code>"production" == System.getProperty( "braincheck.environment" )</code> will return
   * true when values match while this will not work in JRE mode but will work with later versions of GWT2.x
   * it seems. Using a pair of classes with @GwtIncompatible allows us to have the best of both worlds.
   */
  private static final ConfigProvider PROVIDER = new ConfigProvider();

  private static final boolean PRODUCTION_ENVIRONMENT = PROVIDER.isProductionEnvironment();
  private static boolean VERBOSE_ERROR_MESSAGES = PROVIDER.verboseErrorMessages();
  private static boolean CHECK_INVARIANTS = PROVIDER.checkInvariants();
  private static boolean CHECK_API_INVARIANTS = PROVIDER.checkApiInvariants();

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

  private static final class ConfigProvider
    extends AbstractConfigProvider
  {
    @GwtIncompatible
    @Override
    boolean isProductionEnvironment()
    {
      return System.getProperty( "braincheck.environment", "production" ).equals( "production" );
    }

    @GwtIncompatible
    @Override
    boolean verboseErrorMessages()
    {
      return "true".equals( System.getProperty( "braincheck.verbose_error_messages",
                                                PRODUCTION_ENVIRONMENT ? "false" : "true" ) );
    }

    @GwtIncompatible
    @Override
    boolean checkInvariants()
    {
      return "true".equals( System.getProperty( "braincheck.check_invariants",
                                                PRODUCTION_ENVIRONMENT ? "false" : "true" ) );
    }

    @GwtIncompatible
    @Override
    boolean checkApiInvariants()
    {
      return "true".equals( System.getProperty( "braincheck.check_api_invariants",
                                                PRODUCTION_ENVIRONMENT ? "false" : "true" ) );
    }
  }

  @SuppressWarnings( "unused" )
  private static abstract class AbstractConfigProvider
  {
    boolean isProductionEnvironment()
    {
      return "production" == System.getProperty( "braincheck.environment" );
    }

    boolean verboseErrorMessages()
    {
      return "true" == System.getProperty( "braincheck.verbose_error_messages" );
    }

    boolean checkInvariants()
    {
      return "true" == System.getProperty( "braincheck.check_invariants" );
    }

    boolean checkApiInvariants()
    {
      return "true" == System.getProperty( "braincheck.check_api_invariants" );
    }
  }
}
