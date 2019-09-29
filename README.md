# Braincheck

[![Build Status](https://secure.travis-ci.org/realityforge/braincheck.svg?branch=master)](http://travis-ci.org/realityforge/braincheck)
[<img src="https://img.shields.io/maven-central/v/org.realityforge.braincheck/braincheck.svg?label=latest%20release"/>](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.realityforge.braincheck%22%20a%3A%22braincheck%22)
![GWT3/J2CL compatible](https://img.shields.io/badge/GWT3/J2CL-compatible-brightgreen.svg)

Braincheck is a very simple library that you can used to check invariants in code. It is designed that these invariant
checks can be compiled out in production environments either by the JIT or the GWT compiler.

## Quick Start

The simplest way to use Braincheck is to;

* Review the [javadocs](http://realityforge.org/braincheck/).

* Add the BrainCheck dependencies into the build system. i.e.

```xml
<dependency>
   <groupId>org.realityforge.braincheck</groupId>
   <artifactId>braincheck</artifactId>
   <version>1.23.0</version>
</dependency>
```

* If the application is a GWT application, then inherit from `org.realityforge.braincheck.BrainCheck`
  in your .gwt.xml for a production build or inherit `org.realityforge.braincheck.BrainCheckDev` in a
  development build. You can also explicitly set the configuration properties to control which parts
  of the application are optimized away during builds. See the configuration properties section below.

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

    // Raise an exception if invariant checks enabled
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

## Configuration Properties

The following configuration properties can be used to configure BrainCheck. These are mainly used to optimize
the size of applications that statically compile code (i.e. GWT) when we want to minimize code size.

* `braincheck.verbose_error_messages` which can be set to `true` or `false` and if `true`, then invariant exception
  messages will use the supplied message, otherwise no message will be passed to exception.
* `braincheck.check_invariants` which can be set to `true` or `false` and if `false`, calls to `Guards.invariant()`
  and `Guards.fail()` are ignored.
* `braincheck.check_api_invariants` which can be set to `true` or `false` and if `false` or if
  `braincheck.check_invariants` is false then calls to `Guards.apiInvariant()` are ignored.
* `braincheck.environment` which can be set to `development` or `production` and defaults to `production`. If
  `production` then the default values for `braincheck.verbose_error_messages`, `braincheck.check_invariants`
  and `braincheck.check_api_invariants` will be `false` otherwise they will default to be `true`.
