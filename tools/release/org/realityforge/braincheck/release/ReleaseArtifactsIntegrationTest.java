package org.realityforge.braincheck.release;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public final class ReleaseArtifactsIntegrationTest {
    private static final String PACKAGE_PATH = "org/realityforge/braincheck/";
    private static final Pattern DEPENDENCY =
            Pattern.compile("<dependency>\\s*<groupId>([^<]+)</groupId>\\s*<artifactId>([^<]+)</artifactId>"
                    + "\\s*<version>([^<]+)</version>\\s*</dependency>");

    private ReleaseArtifactsIntegrationTest() {}

    public static void main(final String[] args) throws Exception {
        if (args.length != 12) {
            throw new IllegalArgumentException("Expected version file and eleven Maven artifact paths");
        }
        final String version = Files.readString(resolve(Path.of(args[0])), StandardCharsets.UTF_8)
                .trim();

        assertJarEntries(resolve(Path.of(args[1])), coreMainEntries());
        assertJarEntries(resolve(Path.of(args[2])), coreSourceEntries());
        assertJarContains(resolve(Path.of(args[3])), PACKAGE_PATH + "Guards.html");
        assertPom(
                resolve(Path.of(args[4])),
                "braincheck-core",
                "Core invariant verification library",
                version,
                Set.of("org.jspecify:jspecify:1.0.0", "com.google.jsinterop:jsinterop-annotations:2.0.0"));

        assertJarEntries(
                resolve(Path.of(args[5])),
                Set.of("META-INF/MANIFEST.MF", "META-INF/LICENSE", PACKAGE_PATH + "super/java/util/Objects.java"));
        assertJarEntries(resolve(Path.of(args[6])), Set.of("META-INF/MANIFEST.MF"));
        assertPom(resolve(Path.of(args[7])), "braincheck-jre", "Super-source jre classes", version, Set.of());

        assertJarEntries(resolve(Path.of(args[8])), testngMainEntries());
        assertJarEntries(resolve(Path.of(args[9])), testngSourceEntries());
        assertJarContains(resolve(Path.of(args[10])), PACKAGE_PATH + "GuardMessageCollector.html");
        assertPom(
                resolve(Path.of(args[11])),
                "braincheck-testng",
                "TestNG support library",
                version,
                Set.of(
                        "org.jspecify:jspecify:1.0.0",
                        "com.google.jsinterop:jsinterop-annotations:2.0.0",
                        "org.glassfish:javax.json:1.1",
                        "org.testng:testng:6.11"));
    }

    private static Set<String> coreMainEntries() {
        final var entries = new LinkedHashSet<>(coreSourceEntries());
        entries.add("META-INF/LICENSE");
        entries.addAll(List.of(
                PACKAGE_PATH + "BrainCheck.gwt.xml",
                PACKAGE_PATH + "BrainCheckConfig$AbstractConfigProvider.class",
                PACKAGE_PATH + "BrainCheckConfig$ConfigProvider.class",
                PACKAGE_PATH + "BrainCheckConfig.class",
                PACKAGE_PATH + "BrainCheckConfig.native.js",
                PACKAGE_PATH + "BrainCheckDev.gwt.xml",
                PACKAGE_PATH + "BrainCheckTestUtil$GuardType.class",
                PACKAGE_PATH + "BrainCheckTestUtil$OnGuardListener.class",
                PACKAGE_PATH + "BrainCheckTestUtil.class",
                PACKAGE_PATH + "BrainCheckUtil.class",
                PACKAGE_PATH + "DebuggerUtil.class",
                PACKAGE_PATH + "Guards$OnGuardListener.class",
                PACKAGE_PATH + "Guards$Type.class",
                PACKAGE_PATH + "Guards.class",
                PACKAGE_PATH + "GwtIncompatible.class",
                PACKAGE_PATH + "StackTraceUtil$AbstractStackTraceProvider.class",
                PACKAGE_PATH + "StackTraceUtil$StackTraceProvider.class",
                PACKAGE_PATH + "StackTraceUtil.class",
                PACKAGE_PATH + "braincheck.js",
                PACKAGE_PATH + "package-info.class"));
        return entries;
    }

    private static Set<String> coreSourceEntries() {
        return sourceEntries(List.of(
                "BrainCheckConfig.java",
                "BrainCheckTestUtil.java",
                "BrainCheckUtil.java",
                "DebuggerUtil.java",
                "Guards.java",
                "GwtIncompatible.java",
                "StackTraceUtil.java",
                "package-info.java"));
    }

    private static Set<String> testngMainEntries() {
        final var entries = new LinkedHashSet<>(testngSourceEntries());
        entries.add("META-INF/LICENSE");
        entries.addAll(List.of(
                PACKAGE_PATH + "AbstractTestNGMessageCollector.class",
                PACKAGE_PATH + "GuardMessageCollector$Message.class",
                PACKAGE_PATH + "GuardMessageCollector.class",
                PACKAGE_PATH + "package-info.class"));
        return entries;
    }

    private static Set<String> testngSourceEntries() {
        return sourceEntries(
                List.of("AbstractTestNGMessageCollector.java", "GuardMessageCollector.java", "package-info.java"));
    }

    private static Set<String> sourceEntries(final List<String> sources) {
        final var entries = new LinkedHashSet<String>();
        entries.add("META-INF/MANIFEST.MF");
        for (final String source : sources) {
            entries.add(PACKAGE_PATH + source);
        }
        return entries;
    }

    private static void assertJarEntries(final Path path, final Set<String> expected) throws IOException {
        try (JarFile jar = new JarFile(path.toFile())) {
            final var names = new ArrayList<String>();
            Collections.list(jar.entries()).stream()
                    .filter(entry -> !entry.isDirectory())
                    .map(entry -> entry.getName())
                    .forEach(names::add);
            final Set<String> actual = new LinkedHashSet<>(names);
            if (!actual.equals(expected)) {
                throw new AssertionError(
                        "Unexpected entries in " + path + "\nExpected: " + expected + "\nActual: " + actual);
            }
            final String manifestVersion = jar.getManifest().getMainAttributes().getValue("Manifest-Version");
            if (!"1.0".equals(manifestVersion)) {
                throw new AssertionError("Unexpected manifest version in " + path + ": " + manifestVersion);
            }
        }
    }

    private static void assertJarContains(final Path path, final String entry) throws IOException {
        try (JarFile jar = new JarFile(path.toFile())) {
            if (jar.getJarEntry(entry) == null) {
                throw new AssertionError("Missing " + entry + " from " + path);
            }
        }
    }

    private static void assertPom(
            final Path path,
            final String artifactId,
            final String description,
            final String version,
            final Set<String> expectedDependencies)
            throws IOException {
        final String pom = Files.readString(path, StandardCharsets.UTF_8);
        assertContains(pom, "<groupId>org.realityforge.braincheck</groupId>", path);
        assertContains(pom, "<artifactId>" + artifactId + "</artifactId>", path);
        assertContains(pom, "<version>" + version + "</version>", path);
        assertContains(pom, "<name>" + description + "</name>", path);
        assertContains(pom, "<description>" + description + "</description>", path);
        assertContains(pom, "<url>https://github.com/realityforge/braincheck</url>", path);
        assertContains(pom, "<id>realityforge</id>", path);
        if (pom.contains("<scope>") || pom.contains("<optional>")) {
            throw new AssertionError("POM dependencies must use default compile scope and be non-optional: " + path);
        }
        final var dependencies = new LinkedHashSet<String>();
        final var matcher = DEPENDENCY.matcher(pom);
        while (matcher.find()) {
            dependencies.add(matcher.group(1) + ":" + matcher.group(2) + ":" + matcher.group(3));
        }
        if (!dependencies.equals(expectedDependencies)) {
            throw new AssertionError("Unexpected dependencies in " + path + "\nExpected: " + expectedDependencies
                    + "\nActual: " + dependencies);
        }
    }

    private static void assertContains(final String actual, final String expected, final Path path) {
        if (!actual.contains(expected)) {
            throw new AssertionError("Missing expected POM content in " + path + ": " + expected);
        }
    }

    private static Path resolve(final Path path) {
        if (Files.exists(path)) {
            return path.toAbsolutePath().normalize();
        }
        for (final String env : List.of("RUNFILES_DIR", "JAVA_RUNFILES", "TEST_SRCDIR")) {
            final String root = System.getenv(env);
            if (root != null) {
                final Path candidate = Path.of(root).resolve(path);
                if (Files.exists(candidate)) {
                    return candidate.toAbsolutePath().normalize();
                }
                final Path mainCandidate = Path.of(root).resolve("_main").resolve(path);
                if (Files.exists(mainCandidate)) {
                    return mainCandidate.toAbsolutePath().normalize();
                }
            }
        }
        throw new IllegalArgumentException("File does not exist: " + path);
    }
}
