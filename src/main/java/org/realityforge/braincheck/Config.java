package org.realityforge.braincheck;

import org.jetbrains.annotations.TestOnly;

/**
 * Location of all compile time configuration settings for the toolkit.
 */
final class Config
{
  private static final Provider c_provider = createProvider();

  private Config()
  {
  }

  static boolean verboseErrorMessages()
  {
    return c_provider.verboseErrorMessages();
  }

  static boolean checkInvariants()
  {
    return c_provider.checkInvariants();
  }

  static boolean checkApiInvariants()
  {
    return c_provider.checkApiInvariants();
  }

  @TestOnly
  static Provider getProvider()
  {
    return c_provider;
  }

  private static Provider createProvider()
  {
    final String environment = System.getProperty( "braincheck.environment", "production" );
    if ( !"production".equals( environment ) && !"development".equals( environment ) )
    {
      final String message = "System property 'braincheck.environment' is set to invalid property " + environment;
      throw new IllegalStateException( message );
    }
    final boolean development = environment.equals( "development" );
    final boolean verboseErrorMessages =
      "true".equals( System.getProperty( "braincheck.verbose_error_messages", development ? "true" : "false" ) );
    final boolean checkInvariants =
      "true".equals( System.getProperty( "braincheck.check_invariants", development ? "true" : "false" ) );
    final boolean checkApiInvariants =
      "true".equals( System.getProperty( "braincheck.check_api_invariants", development ? "true" : "false" ) );

    return System.getProperty( "braincheck.dynamic_provider", "false" ).equals( "true" ) ?
           new DynamicProvider( verboseErrorMessages, checkInvariants, checkApiInvariants ) :
           new StaticProvider( verboseErrorMessages, checkInvariants, checkApiInvariants );
  }

  /**
   * Abstraction used to provide configuration settings for Arez system.
   * This abstraction is used to allow converting configuration to compile time
   * constants during GWT and/or closure compiler phases and thus allow elimination of
   * code during production variants of the runtime.
   */
  interface Provider
  {
    boolean verboseErrorMessages();

    boolean checkInvariants();

    boolean checkApiInvariants();
  }

  /**
   * A provider implementation that allows changing of values at runtime.
   * Only really used during testing.
   */
  @TestOnly
  static final class DynamicProvider
    implements Provider
  {
    private boolean _verboseErrorMessages;
    private boolean _checkInvariants;
    private boolean _checkApiInvariants;

    DynamicProvider( final boolean verboseErrorMessages,
                     final boolean checkInvariants,
                     final boolean checkApiInvariants )
    {
      _verboseErrorMessages = verboseErrorMessages;
      _checkInvariants = checkInvariants;
      _checkApiInvariants = checkApiInvariants;
    }

    void setVerboseErrorMessages( final boolean verboseErrorMessages )
    {
      _verboseErrorMessages = verboseErrorMessages;
    }

    void setCheckInvariants( final boolean checkInvariants )
    {
      _checkInvariants = checkInvariants;
    }

    void setCheckApiInvariants( final boolean checkApiInvariants )
    {
      _checkApiInvariants = checkApiInvariants;
    }

    @Override
    public boolean verboseErrorMessages()
    {
      return _verboseErrorMessages;
    }

    @Override
    public boolean checkInvariants()
    {
      return _checkInvariants;
    }

    @Override
    public boolean checkApiInvariants()
    {
      return _checkApiInvariants;
    }
  }

  /**
   * The normal provider implementation for statically defining properties.
   * Properties do not change at runtime and can be used by GWT and closure compiler
   * to set values at compile time and eliminate dead/unused code.
   */
  private static final class StaticProvider
    implements Provider
  {
    private final boolean _verboseErrorMessages;
    private final boolean _checkInvariants;
    private final boolean _checkApiInvariants;

    StaticProvider( final boolean verboseErrorMessages,
                    final boolean checkInvariants,
                    final boolean checkApiInvariants )
    {
      _verboseErrorMessages = verboseErrorMessages;
      _checkInvariants = checkInvariants;
      _checkApiInvariants = checkApiInvariants;
    }

    @Override
    public boolean verboseErrorMessages()
    {
      return _verboseErrorMessages;
    }

    @Override
    public boolean checkInvariants()
    {
      return _checkInvariants;
    }

    @Override
    public boolean checkApiInvariants()
    {
      return _checkApiInvariants;
    }
  }
}
