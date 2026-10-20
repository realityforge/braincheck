package org.realityforge.braincheck;

/**
 * Location of all compile time configuration settings for the toolkit.
 */
@SuppressWarnings({"StringEquality", "FieldMayBeFinal"})
public final class BrainCheckConfig {
    /**
     * The provider abstraction is needed due to limitations in the way J2CL performs dead-code elimination.
     * J2CL uses string identity comparisons for compile-time properties, while the JVM needs value comparisons
     * and default values. J2CL strips the JVM overrides marked with @GwtIncompatible.
     */
    private static final ConfigProvider PROVIDER = new ConfigProvider();

    private static final boolean PRODUCTION_ENVIRONMENT = PROVIDER.isProductionEnvironment();
    private static boolean c_verboseErrorMessages = PROVIDER.verboseErrorMessages();
    private static boolean c_checkInvariants = PROVIDER.checkInvariants();
    private static boolean c_checkApiInvariants = PROVIDER.checkApiInvariants();

    private BrainCheckConfig() {}

    /**
     * Return true if BrainCheck is running in production mode.
     *
     * @return true if BrainCheck is running in production mode.
     */
    static boolean isProductionEnvironment() {
        return PRODUCTION_ENVIRONMENT;
    }

    /**
     * Return true if BrainCheck is running in development mode.
     *
     * @return true if BrainCheck is running in development mode.
     */
    static boolean isDevelopmentEnvironment() {
        return !isProductionEnvironment();
    }

    /**
     * Return true if invariant failures will include a detail message.
     *
     * @return true if invariant failures will include a detail message.
     */
    public static boolean verboseErrorMessages() {
        return c_verboseErrorMessages;
    }

    static void setVerboseErrorMessages(final boolean verboseErrorMessages) {
        c_verboseErrorMessages = verboseErrorMessages;
    }

    /**
     * Return true if invariants will be checked.
     *
     * @return true if invariants will be checked.
     */
    public static boolean checkInvariants() {
        return c_checkInvariants;
    }

    static void setCheckInvariants(final boolean checkInvariants) {
        c_checkInvariants = checkInvariants;
    }

    /**
     * Return true if apiInvariants will be checked.
     *
     * @return true if apiInvariants will be checked.
     */
    public static boolean checkApiInvariants() {
        return c_checkApiInvariants;
    }

    static void setCheckApiInvariants(final boolean checkApiInvariants) {
        c_checkApiInvariants = checkApiInvariants;
    }

    @SuppressWarnings("SimplifiableConditionalExpression")
    private static final class ConfigProvider extends AbstractConfigProvider {
        @GwtIncompatible
        @Override
        boolean isProductionEnvironment() {
            return "production".equals(System.getProperty("braincheck.environment", "production"));
        }

        @GwtIncompatible
        @Override
        boolean verboseErrorMessages() {
            return "true"
                    .equals(System.getProperty(
                            "braincheck.verbose_error_messages", PRODUCTION_ENVIRONMENT ? "false" : "true"));
        }

        @GwtIncompatible
        @Override
        boolean checkInvariants() {
            return "true"
                    .equals(System.getProperty(
                            "braincheck.check_invariants", PRODUCTION_ENVIRONMENT ? "false" : "true"));
        }

        @GwtIncompatible
        @Override
        boolean checkApiInvariants() {
            return "true"
                    .equals(System.getProperty(
                            "braincheck.check_api_invariants", PRODUCTION_ENVIRONMENT ? "false" : "true"));
        }
    }

    @SuppressWarnings("unused")
    private abstract static class AbstractConfigProvider {
        boolean isProductionEnvironment() {
            return "production" == System.getProperty("braincheck.environment");
        }

        boolean verboseErrorMessages() {
            return "true" == System.getProperty("braincheck.verbose_error_messages");
        }

        boolean checkInvariants() {
            return "true" == System.getProperty("braincheck.check_invariants");
        }

        boolean checkApiInvariants() {
            return "true" == System.getProperty("braincheck.check_api_invariants");
        }
    }
}
