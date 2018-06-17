# Change Log

## Unreleased

* Remove dependency on the `org.realityforge.anodoc:anodoc:jar` artifact.
* Change the dependency on the `com.google.jsinterop:jsinterop-annotations:jar` to being transitive.
* Change the dependency on the `com.google.code.findbugs:jsr305:jar` to being transitive.
* Move the `com.google.jsinterop:jsinterop-annotations:jar` dependency and the
  `com.google.code.findbugs:jsr305:jar` dependency to being `compile` scope rather than `provided`
  scope as it seems to fit into the J2CL vision better.
* Remove the `test` scoped dependencies from the generated POMs. The POMs are only intended for
  consumption and do not need to contain dependency details about how the project was built.

## 1.7.0

* Work-around limitations of GWT2.x compiler that was failing to eliminate the type of `AssertUtil`
  even though no reference was made to code in production mode. This involved moving guard inline
  into `Guards` class. This results in a slight performance degradation in a JRE environment when
  an invariant fails. This is should be a rare occurrence and the ability to eliminate the type in
  GWT mode was considered an acceptable trade-off.

## 1.6.0

* Removed unnecessary `com.google.gwt.core.Core` inherit from the `BrainCheck.gwt.xml` GWT module.
* Remove `super-source` construct for compatibility with GWT 3.x. Reimplemented the debugger call-out
  using jsinterop and a check against a compile time constant that should only be present in GWT
  environment.

## 1.5.0

* Remove gwt classifier from artifacts.

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
