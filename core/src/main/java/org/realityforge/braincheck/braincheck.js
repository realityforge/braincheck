/**
 * This file provides the @defines for braincheck configuration options.
 * See BrainCheckConfig.java for details.
 */
goog.module('braincheck');
goog.module.declareLegacyNamespace();

const {addSystemPropertyFromGoogDefine} = goog.require('jre');

/** @define {string} */
const environment = goog.define('braincheck.environment', 'production');
addSystemPropertyFromGoogDefine('braincheck.environment', environment);

/** @define {string} */
const verboseErrorMessages = goog.define('braincheck.verbose_error_messages', 'false');
addSystemPropertyFromGoogDefine('braincheck.verbose_error_messages', verboseErrorMessages);
/** @define {string} */
const checkInvariants = goog.define('braincheck.check_invariants', 'false');
addSystemPropertyFromGoogDefine('braincheck.check_invariants', checkInvariants);
/** @define {string} */
const checkApiInvariants = goog.define('braincheck.check_api_invariants', 'false');
addSystemPropertyFromGoogDefine('braincheck.check_api_invariants', checkApiInvariants);

exports = {
  check_api_invariants: checkApiInvariants,
  check_invariants: checkInvariants,
  environment,
  verbose_error_messages: verboseErrorMessages,
};
