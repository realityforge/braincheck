# Braincheck

[<img src="https://img.shields.io/maven-central/v/org.realityforge.braincheck/braincheck-core.svg?label=latest%20release"/>](https://central.sonatype.com/artifact/org.realityforge.braincheck/braincheck-core)

Braincheck checks invariants in Java 17 and J2CL applications. Invariant checks can be optimized away in production
by the JVM's JIT or J2CL's Closure compiler. GWT is no longer supported.

## Quick Start

The simplest way to use Braincheck is to;

* Add the BrainCheck dependencies into the build system. i.e.

```xml
<dependency>
   <groupId>org.realityforge.braincheck</groupId>
   <artifactId>braincheck-core</artifactId>
   <version>1.35.0</version>
</dependency>
```

* Configure checks using JVM system properties or J2CL Closure defines, as described below.

* Add invariant checks to your code to verify various conditions.

```java
import static org.realityforge.braincheck.Guards.*;

class MyClass
{
  public void myMethod(int i)
  {
    // Raise an exception if condition (first parameter) is not true
    apiInvariant( () -> i > 0, () -> "You are using the api incorrectly!" );

    // Raise an exception if condition (first parameter) is not true
    invariant( () -> 1 == 1, () -> "Maths has broken down!" );

    // Always raise an exception
    fail( () -> "You have reached a failing scenario in the application" );
  }
}

```

* You can also modify the invariant configuration in tests by setting system property
  `braincheck.environment` to `development` and interacting with the `BrainCheckTestUtil`
  class. i.e.

```java
import org.realityforge.braincheck.BrainCheckTestUtil;

class MyTest
{
  @Test
  public void myTest()
  {
    BrainCheckTestUtil.setCheckApiInvariants( true );
    //...
  }
}

```

## Bazel

Depend on `@braincheck//:core` for JVM code, `@braincheck//:core-j2cl` for J2CL code,
and `@braincheck//:testng` for TestNG support. These are the supported public Bazel labels.

## Configuration Properties

Set JVM system properties with `-Dname=value`. In J2CL builds, pass Closure compiler defines with
`--define=name=value`. J2CL can remove disabled invariant checks and verbose messages from optimized output.

* `braincheck.verbose_error_messages` which can be set to `true` or `false` and if `true`, then invariant exception
  messages will use the supplied message, otherwise no message will be passed to exception.
* `braincheck.check_invariants` which can be set to `true` or `false` and if `false`, calls to `Guards.invariant()`
  are ignored. `Guards.fail()` always throws.
* `braincheck.check_api_invariants` which can be set to `true` or `false` and if `false`, calls to
  `Guards.apiInvariant()` are ignored. This setting is independent of `braincheck.check_invariants`.
* `braincheck.environment` which can be set to `development` or `production` and defaults to `production`. If
  `production` then the default values for `braincheck.verbose_error_messages`, `braincheck.check_invariants`
  and `braincheck.check_api_invariants` will be `false`. On the JVM, `development` defaults all three to `true`.
  In J2CL, each flag defaults to `false` independently of the environment; explicitly define all three as `true`
  for a development build.
