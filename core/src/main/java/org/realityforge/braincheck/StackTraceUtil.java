package org.realityforge.braincheck;

final class StackTraceUtil {
    private static final StackTraceProvider PROVIDER = new StackTraceProvider();

    private StackTraceUtil() {}

    static StackTraceElement[] getStackTrace(final int frameCountToDrop) {
        return PROVIDER.getStackTrace(frameCountToDrop + 1);
    }

    private static final class StackTraceProvider extends AbstractStackTraceProvider {
        @SuppressWarnings("NonJREEmulationClassesInClientCode")
        @GwtIncompatible
        @Override
        StackTraceElement[] getStackTrace(final int frameCountToDrop) {
            final StackTraceElement[] stackTrace =
                    java.lang.Thread.currentThread().getStackTrace();
            final int skipCount = frameCountToDrop + 1;
            final StackTraceElement[] result = new StackTraceElement[Math.max(stackTrace.length - skipCount, 0)];
            System.arraycopy(stackTrace, skipCount, result, 0, result.length);
            return result;
        }
    }

    private abstract static class AbstractStackTraceProvider {
        StackTraceElement[] getStackTrace(final int frameCountToDrop) {
            return new StackTraceElement[0];
        }
    }
}
