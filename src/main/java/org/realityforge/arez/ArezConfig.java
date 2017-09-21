package org.realityforge.arez;

import org.jetbrains.annotations.TestOnly;

/**
 * Location of all compile time configuration settings for the toolkit.
 */
final class ArezConfig
{
  private static final Provider c_provider = createProvider();

  private ArezConfig()
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

  @TestOnly
  static Provider getProvider()
  {
    return c_provider;
  }

  private static Provider createProvider()
  {
    final String environment = System.getProperty( "arez.environment", "production" );
    if ( !"production".equals( environment ) && !"development".equals( environment ) )
    {
      final String message = "System property 'arez.environment' is set to invalid property " + environment;
      throw new IllegalStateException( message );
    }
    final boolean development = environment.equals( "development" );
    final boolean verboseErrorMessages =
      "true".equals( System.getProperty( "arez.verbose_error_messages", development ? "true" : "false" ) );
    final boolean checkInvariants =
      "true".equals( System.getProperty( "arez.check_invariants", development ? "true" : "false" ) );

    return System.getProperty( "arez.dynamic_provider", "false" ).equals( "true" ) ?
           new DynamicProvider( verboseErrorMessages, checkInvariants ) :
           new StaticProvider( verboseErrorMessages, checkInvariants );
  }

  /**
   * Abstraction used to provide configuration settings for Arez system.
   * This abstraction is used to allow converting configuration to compile time
   * constants during GWT and/or closure compiler phases and thus allow elimination of
   * code during production variants of the runtime.
   */
  private interface Provider
  {
    boolean verboseErrorMessages();

    boolean checkInvariants();
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

    DynamicProvider( final boolean verboseErrorMessages, final boolean checkInvariants )
    {
      _verboseErrorMessages = verboseErrorMessages;
      _checkInvariants = checkInvariants;
    }

    void setVerboseErrorMessages( final boolean verboseErrorMessages )
    {
      _verboseErrorMessages = verboseErrorMessages;
    }

    void setCheckInvariants( final boolean checkInvariants )
    {
      _checkInvariants = checkInvariants;
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

    StaticProvider( final boolean verboseErrorMessages, final boolean checkInvariants )
    {
      _verboseErrorMessages = verboseErrorMessages;
      _checkInvariants = checkInvariants;
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
  }
}
