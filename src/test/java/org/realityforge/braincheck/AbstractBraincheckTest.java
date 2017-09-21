package org.realityforge.braincheck;

import javax.annotation.Nonnull;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class AbstractBraincheckTest
{
  @BeforeMethod
  protected void beforeTest()
    throws Exception
  {
    final Config.DynamicProvider provider = getConfigProvider();
    provider.setVerboseErrorMessages( true );
    provider.setCheckInvariants( true );
    provider.setCheckApiInvariants( true );
  }

  @AfterMethod
  protected void afterTest()
    throws Exception
  {
    final Config.DynamicProvider provider = getConfigProvider();
    provider.setVerboseErrorMessages( false );
    provider.setCheckInvariants( false );
    provider.setCheckApiInvariants( false );
  }

  @Nonnull
  final Config.DynamicProvider getConfigProvider()
  {
    return (Config.DynamicProvider) Config.getProvider();
  }
}
