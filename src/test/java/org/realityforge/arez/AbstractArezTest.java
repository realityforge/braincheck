package org.realityforge.arez;

import javax.annotation.Nonnull;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class AbstractArezTest
{
  @BeforeMethod
  protected void beforeTest()
    throws Exception
  {
    final ArezConfig.DynamicProvider provider = getConfigProvider();
    provider.setVerboseErrorMessages( true );
    provider.setCheckInvariants( true );
    getProxyLogger().setLogger( new TestLogger() );
  }

  @AfterMethod
  protected void afterTest()
    throws Exception
  {
    final ArezConfig.DynamicProvider provider = getConfigProvider();
    provider.setVerboseErrorMessages( false );
    provider.setCheckInvariants( false );
    getProxyLogger().setLogger( null );
  }

  @Nonnull
  final TestLogger getTestLogger()
  {
    return (TestLogger) getProxyLogger().getLogger();
  }

  @Nonnull
  private ArezLogger.ProxyLogger getProxyLogger()
  {
    return (ArezLogger.ProxyLogger) ArezLogger.getLogger();
  }

  @Nonnull
  final ArezConfig.DynamicProvider getConfigProvider()
  {
    return (ArezConfig.DynamicProvider) ArezConfig.getProvider();
  }
}
