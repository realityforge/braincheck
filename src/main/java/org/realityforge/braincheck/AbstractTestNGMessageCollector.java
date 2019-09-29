package org.realityforge.braincheck;

import javax.annotation.Nonnull;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

/**
 * A base class for creating a TestNG listener to attach to tests.
 */
@GwtIncompatible
public abstract class AbstractTestNGMessageCollector
  extends TestListenerAdapter
  implements ITestListener
{
  @Nonnull
  private final GuardMessageCollector _messages = createCollector();

  @Override
  public void onTestStart( @Nonnull final ITestResult result )
  {
    if ( shouldCheckDiagnosticMessages() )
    {
      _messages.onTestStart();
    }
  }

  @Override
  public void onTestSuccess( @Nonnull final ITestResult result )
  {
    if ( shouldCheckDiagnosticMessages() )
    {
      _messages.onTestComplete();
    }
  }

  @Override
  public void onStart( @Nonnull final ITestContext context )
  {
    if ( shouldCheckDiagnosticMessages() )
    {
      _messages.onTestSuiteStart();
    }
  }

  @Override
  public void onFinish( @Nonnull final ITestContext context )
  {
    if ( shouldCompleteCollection( context ) )
    {
      _messages.onTestSuiteComplete(context.getFailedTests().size() <= _messages.getMatchFailureCount());
    }
  }

  protected boolean shouldCompleteCollection( @Nonnull final ITestContext context )
  {
    return shouldCheckDiagnosticMessages();
  }

  protected abstract boolean shouldCheckDiagnosticMessages();

  @Nonnull
  protected abstract GuardMessageCollector createCollector();
}
