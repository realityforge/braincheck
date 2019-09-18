# Change Log

### [v1.21.0](https://github.com/realityforge/braincheck/tree/v1.21.0) (2019-09-18)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.20.0...v1.21.0)

* Upgrade the `org.realityforge.javax.annotation` artifact to version `1.0.1`.
* Upgrade the `org.realityforge.guiceyloops` artifact to version `0.102`.
* Upgrade the `au.com.stocksoftware.idea.codestyle` artifact to version `1.14`.
* Change the implementation of the `GuardMessageCollector` so that it generates an `AssertionError` if an invariant message fails to match the existing pattern rather than an `IllegalStateException` in some circumstances and an `AssertionError` in other circumstances.
* Record the number of invariant messages that failed to match in the `GuardMessageCollector` and expose the count via the `GuardMessageCollector.getMatchFailureCount()` method.
* Introduce `AbstractTestNGMessageCollector` to simplify writing a TestNG listener that collects invariant messages.

### [v1.20.0](https://github.com/realityforge/braincheck/tree/v1.20.0) (2019-07-02)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.19.0...v1.20.0)

* Import the `GuardMessageCollector` class from Arez. The class is used to collect invariant invocations during tests for the purpose of documentation and verification. See the javadocs for further details.

### [v1.19.0](https://github.com/realityforge/braincheck/tree/v1.19.0) (2019-06-30)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.18.0...v1.19.0)

* Add the ability to register a callback that is invoked every time the `Guards.invariant(...)` or `Guards.apiInvariant(...)` methods are invoked. The callback is only invoked in development mode when braincheck is invoked in a jre environment and is primarily intended to enable the collection and verification of all invariant checks within a system. The callback is registered via `BrainCheckTestUtil.setOnGuardListener(...)`.

### [v1.18.0](https://github.com/realityforge/braincheck/tree/v1.18.0) (2019-06-30)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.17.0...v1.18.0)

* Ensure that the `com.google.jsinterop:jsinterop-annotations` is declared as a dependency in the generated pom.

### [v1.17.0](https://github.com/realityforge/braincheck/tree/v1.17.0) (2019-06-30)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.16.0...v1.17.0)

* Decouple from the `com.google.jsinterop:base` artifact, re-add dependency on `com.google.jsinterop:jsinterop-annotations` and implement the `debugger` javascript statement locally. This change reverses the change applied in version `1.14.0` due to changes in either closure compiler or j2cl that meant that even if `com.google.jsinterop:base` is included, the downstream `j2cl_library` Bazel rule still needed to suppress the `checkDebuggerStatement` warning. Thus the decision was made to simplify the dependency hierarchy.

### [v1.16.0](https://github.com/realityforge/braincheck/tree/v1.16.0) (2019-04-16)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.15.0...v1.16.0)

* Improve the way defines are handled in closure by assigning the results of `goog.define` to a module local variable.

### [v1.15.0](https://github.com/realityforge/braincheck/tree/v1.15.0) (2019-04-07)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.14.0...v1.15.0)

* Improve the formatting of javadocs, add source crosslinking and include source in generated documentation.

### [v1.14.0](https://github.com/realityforge/braincheck/tree/v1.14.0) (2019-04-07)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.13.0...v1.14.0)

* Use `const` rather than `let` for module import to avoid closure compiler warning.
* Use `Js.debugger()` from the `com.google.jsinterop:base` artifact. This avoids the need to explicitly
  allow the `debugger` statement when compiled by closure compiler pass after transpiling via J2CL.
* Remove direct dependency on the `com.google.jsinterop:jsinterop-annotations` artifact from the pom
  as it is is no longer required for this library but is instead only used via the `com.google.jsinterop:base`
  artifact.

### [v1.13.0](https://github.com/realityforge/braincheck/tree/v1.13.0)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.12.0...v1.13.0)

* Remove deployment from TravisCI infrastructure as it is no longer feasible.
* If `BrainCheckConfig.checkInvariants()` returned `false` and `BrainCheckConfig.checkApiInvariants()`
  returned `true` and `Guards.apiInvariant()` had false invariant then no invariant failure
  would be triggered. This has been fixed by ensuring that the failure code is only marked as
  dead code if it is not used.

### [v1.12.0](https://github.com/realityforge/braincheck/tree/v1.12.0)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.11.0...v1.12.0)

* Add the `@define` configuration for the compile-time constants that is required for the
  closure compiler to correctly process constants at compile time.

### [v1.11.0](https://github.com/realityforge/braincheck/tree/v1.11.0)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.10.0...v1.11.0)

* Fix bug introduced in `1.10.0` with compile time constants other than `braincheck.environment` in
  JRE mode being incorrectly interpreted.

### [v1.10.0](https://github.com/realityforge/braincheck/tree/v1.10.0)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.9.0...v1.10.0)

* Compile-time constants work differently between the JRE, J2CL and GWT2.x environments. Adopt an
  approach that has the same effective outcome across all environments. This involves using instance
  comparisons for results returned from `System.getProperty(...)` in GWT2.x and J2CL environments and
  using normal `equals()` method in JRE. It should be noted that for this to work correctly in the J2CL
  environment, the properties still need to defined via code such as:
  `/** @define {string} */ goog.define('braincheck.environment', 'production');`

### [v1.9.0](https://github.com/realityforge/braincheck/tree/v1.9.0)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.8.0...v1.9.0)

* Replace usage of the `com.google.code.findbugs:jsr305:jar` dependency with the
  `org.realityforge.javax.annotation:javax.annotation:jar` dependency as the former includes code that
  is incompatible with J2CL compiler.

### [v1.8.0](https://github.com/realityforge/braincheck/tree/v1.8.0)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.7.0...v1.8.0)

* Remove dependency on the `org.realityforge.anodoc:anodoc:jar` artifact.
* Change the dependency on the `com.google.jsinterop:jsinterop-annotations:jar` to being transitive.
* Change the dependency on the `com.google.code.findbugs:jsr305:jar` to being transitive.
* Move the `com.google.jsinterop:jsinterop-annotations:jar` dependency and the
  `com.google.code.findbugs:jsr305:jar` dependency to being `compile` scope rather than `provided`
  scope as it seems to fit into the J2CL vision better.
* Remove the `test` scoped dependencies from the generated POMs. The POMs are only intended for
  consumption and do not need to contain dependency details about how the project was built.
* Remove GWT dependencies and GWT as a dependency in the POM as these dependencies are incompatible
  with J2CL and are not actually required but instead artifacts of GWT compilation process.

### [v1.7.0](https://github.com/realityforge/braincheck/tree/v1.7.0)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.6.0...v1.7.0)

* Work-around limitations of GWT2.x compiler that was failing to eliminate the type of `AssertUtil`
  even though no reference was made to code in production mode. This involved moving guard inline
  into `Guards` class. This results in a slight performance degradation in a JRE environment when
  an invariant fails. This is should be a rare occurrence and the ability to eliminate the type in
  GWT mode was considered an acceptable trade-off.

### [v1.6.0](https://github.com/realityforge/braincheck/tree/v1.6.0)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.5.0...v1.6.0)

* Removed unnecessary `com.google.gwt.core.Core` inherit from the `BrainCheck.gwt.xml` GWT module.
* Remove `super-source` construct for compatibility with GWT 3.x. Reimplemented the debugger call-out
  using jsinterop and a check against a compile time constant that should only be present in GWT
  environment.

### [v1.5.0](https://github.com/realityforge/braincheck/tree/v1.5.0)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.4.0...v1.5.0)

* Remove gwt classifier from artifacts.

### [v1.4.0](https://github.com/realityforge/braincheck/tree/v1.4.0)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.3.0...v1.4.0)

* Mark `BrainCheckConfig.PRODUCTION_ENVIRONMENT` and use it to control whether can modify values
  using the `BrainCheckTestUtil` class.

### [v1.3.0](https://github.com/realityforge/braincheck/tree/v1.3.0)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.2.0...v1.3.0)

* Issue a `debugger` javascript command when GWT compiling code and an assertion fails. Use super-sourcing to
  ensure code has no GWT dependency.
* Add rake task to automate publishing to maven central.
* Introduce `BrainCheckTestUtil.resetConfig(boolean productionMode)` utility method that resets the configuration
  to either development or production mode. Useful to simplify test setup.

### [v1.2.0](https://github.com/realityforge/braincheck/tree/v1.2.0)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.1.0...v1.2.0)

* Replace usage of `org.jetbrains:annotations:jar` dependency with `org.realityforge.anodoc:anodoc:jar`.

### [v1.1.0](https://github.com/realityforge/braincheck/tree/v1.1.0)
[Full Changelog](https://github.com/realityforge/braincheck/compare/v1.0.0...v1.1.0)

* Mark `BrainCheckTestUtil` as `GwtIncompatible` so that it is not compiled by GWT.

### [v1.0.0](https://github.com/realityforge/braincheck/tree/v1.0.0)
[Full Changelog](https://github.com/realityforge/braincheck/compare/2e6a33153660e6074dab5c8056ee58a7a4ad6770...v1.0.0)

* 🎉 Initial release.
