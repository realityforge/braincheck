require 'buildr/git_auto_version'
require 'buildr/gpg'
require 'buildr/gwt'
require 'buildr/single_intermediate_layout'
require 'buildr/top_level_generate_dir'

Buildr::MavenCentral.define_publish_tasks(:profile_name => 'org.realityforge', :username => 'realityforge')

desc 'BrainCheck: A very simply invariant verification library'
define 'braincheck' do
  project.group = 'org.realityforge.braincheck'
  compile.options.source = '1.8'
  compile.options.target = '1.8'
  compile.options.lint = 'all'

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  pom.add_apache_v2_license
  pom.add_github_project('realityforge/braincheck')
  pom.add_developer('realityforge', 'Peter Donald')

  desc 'Core invariant verification library'
  define 'core' do
    deps = [artifact(:javax_annotation), artifact(:jsinterop_annotations)]
    pom.include_transitive_dependencies << deps
    pom.dependency_filter = Proc.new { |dep| dep[:scope].to_s != 'test' && deps.include?(dep[:artifact]) }

    compile.with :javax_annotation,
                 :jsinterop_annotations

    gwt_enhance(project)

    test.using :testng
    test.with :guiceyloops
    test.options[:properties] = { 'braincheck.environment' => 'development' }
    test.options[:java_args] = ['-ea']

    doc.using(:javadoc,
              :windowtitle => 'Braincheck API Documentation',
              :linksource => true,
              :timestamp => false,
              :link => %w(https://docs.oracle.com/javase/8/docs/api)
    ).sourcepath << compile.sources

    package(:jar)
    package(:sources)
    package(:javadoc)
  end

  desc 'Super-source jre classes'
  define 'jre' do
    pom.dependency_filter = Proc.new { |_| false }

    gwt_enhance(project, :extra_deps => [_('src/main/super')])

    package(:jar)
    package(:sources)
    package(:javadoc)

    project.no_iml
  end

  desc 'TestNG support library'
  define 'testng' do
    deps = [artifact(:javax_annotation), artifact(:jsinterop_annotations), artifact(:javax_json), artifact(:testng)]
    pom.include_transitive_dependencies << deps
    pom.dependency_filter = Proc.new { |dep| dep[:scope].to_s != 'test' && deps.include?(dep[:artifact]) }

    compile.with project('core').package(:jar),
                 project('core').compile.dependencies,
                 :javax_json,
                 :testng

    gwt_enhance(project, :extra_deps => [_('src/main/super')])

    test.using :testng
    test.with :guiceyloops
    test.options[:properties] = { 'braincheck.environment' => 'development' }
    test.options[:java_args] = ['-ea']

    package(:jar)
    package(:sources)
    package(:javadoc)
  end

  t = Buildr::BazelJ2cl.define_bazel_j2cl_test(Buildr.project('braincheck'), [Buildr.project('braincheck:core').package(:jar)])
  package.enhance([t])

  cleanup_javadocs(project, 'org')

  ipr.add_default_testng_configuration(:jvm_args => '-ea -Dbraincheck.environment=development')
  ipr.add_component_from_artifact(:idea_codestyle)
end

desc 'Deploy Javadocs to github site'
task 'deploy:docs' do
  task('braincheck:package').invoke
  sh 'yarn deploy:docs'
end
