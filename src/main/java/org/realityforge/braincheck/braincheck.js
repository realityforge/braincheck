/**
 * This file provides the @defines for braincheck configuration options.
 * See BrainCheckConfig.java for details.
 */
goog.provide('braincheck');

/** @define {string} */
braincheck.environment = goog.define('braincheck.environment', 'production');

/** @define {string} */
braincheck.verbose_error_messages = goog.define('braincheck.verbose_error_messages', 'false');
/** @define {string} */
braincheck.check_invariants = goog.define('braincheck.check_invariants', 'false');
/** @define {string} */
braincheck.check_api_invariants = goog.define('braincheck.check_api_invariants', 'false');
