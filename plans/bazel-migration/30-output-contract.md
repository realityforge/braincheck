# Maven Publication Output Contract

This contract is derived from `target/braincheck-1.33.0.zip`. Version strings vary per release; coordinates, classifiers, jar entry categories, and dependency scopes do not vary except for the accepted JSpecify replacement.

## Artifact Matrix

| Artifact | Main jar | Sources jar | Javadoc jar | POM |
| --- | --- | --- | --- | --- |
| `braincheck-core` | required | required | required | required |
| `braincheck-jre` | required | required (historically empty) | absent | required |
| `braincheck-testng` | required | required | required | required |

Every main jar includes a normalized manifest and `META-INF/LICENSE`. The 1.33.0 source and Javadoc jars do not contain the license entry, so release validation must not require it there.

## Main Jar Entries

### `braincheck-core`

- Compiled classes for every production Java type under `org/realityforge/braincheck`.
- The corresponding production `.java` sources, including `package-info.java`.
- `BrainCheck.gwt.xml` and `BrainCheckDev.gwt.xml`.
- `BrainCheckConfig.native.js` and `braincheck.js`.

### `braincheck-jre`

- `org/realityforge/braincheck/super/java/util/Objects.java` as GWT super-source.
- No compiled class is required because the source intentionally shadows `java.util.Objects` only in GWT.

### `braincheck-testng`

- Compiled classes for `AbstractTestNGMessageCollector` and `GuardMessageCollector`, including nested classes.
- The corresponding production `.java` sources.

Release integration tests must compare normalized archive entry manifests against these requirements so Bazel's ordinary source jars are not mistaken for the source-bearing main jars required by GWT consumers.

## POM Dependencies

All dependencies use Maven's default compile scope and are non-optional.

### `braincheck-core`

- `org.jspecify:jspecify:1.0.0`, replacing `org.realityforge.javax.annotation:javax.annotation:1.1.1`.
- `com.google.jsinterop:jsinterop-annotations:2.0.0`.

### `braincheck-jre`

- No dependencies.

### `braincheck-testng`

- `org.jspecify:jspecify:1.0.0`, replacing `org.realityforge.javax.annotation:javax.annotation:1.1.1`.
- `com.google.jsinterop:jsinterop-annotations:2.0.0`.
- `org.glassfish:javax.json:1.1`.
- `org.testng:testng:6.11`.

## Common POM Metadata

- Group: `org.realityforge.braincheck`.
- Packaging: `jar`.
- Project URL: `https://github.com/realityforge/braincheck`.
- Apache License 2.0 metadata.
- Existing SCM, issue-management, and `realityforge` developer metadata.

## Distribution Layout, Signatures, and Checksums

The release ZIP stores every primary artifact beneath:

`org/realityforge/braincheck/<artifact-id>/<version>/<artifact-id>-<version><classifier>.<extension>`

For every primary main jar, sources jar, Javadoc jar (when present), and POM:

- Include the primary file.
- Include a detached ASCII-armored signature `<file>.asc`.
- Include `<file>.md5` and `<file>.sha1`.
- Include `<file>.asc.md5` and `<file>.asc.sha1` for the detached signature.

The normalized full distribution manifest therefore contains 66 files: 24 for core's four primary artifacts, 18 for JRE's three primary artifacts, and 24 for TestNG's four primary artifacts. Release tests must compare the generated distribution's relative-path manifest with this matrix and verify that every signature and checksum is non-empty.
