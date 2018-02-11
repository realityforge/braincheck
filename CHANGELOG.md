# Change Log

## Unreleased

## 1.4.0

* Mark `BrainCheckConfig.PRODUCTION_ENVIRONMENT` and use it to control whether can modify values
  using the `BrainCheckTestUtil` class.

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
