# Change Log

## Unreleased

* Eliminate `braincheck.environment` configuration property as does not seem to add any value. It also seems
  to limit the effectiveness of the GWT 2.8.2 compilers optimizer as not all unused code is removed.
* Duplicate `BrainCheckConfig` as a super-sourced class so the configuration fields can be explicitly marked
  as final. This should not be needed but it helps the GWT compiler optimize code out more effectively.

## 1.3.0

* Issue a `debugger` javascript command when GWT compiling code and an assertion fails. Use super-sourcing to
  ensure code has no GWT dependency.
* Add rake task to automate publishing to maven central.
* Introduce `BrainCheckTestUtil.resetConfig(boolean productionMode)` utility method that resets the configuration
  to either development or production mode. Useful to simplify test setup.

## 1.2.0:

* Replace usage of `org.jetbrains:annotations:jar` dependency with `org.realityforge.anodoc:anodoc:jar`.

## 1.1.0:

* Mark `BrainCheckTestUtil` as `GwtIncompatible` so that it is not compiled by GWT.

## 1.0.0:

* 🎉 Initial release.
