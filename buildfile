require 'buildr/git_auto_version'
require 'buildr/gpg'

PROVIDED_DEPS = [:javax_jsr305, :jetbrains_annotations]
COMPILE_DEPS = []
OPTIONAL_DEPS = []
TEST_DEPS = [:guiceyloops]

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

  pom.provided_dependencies.concat PROVIDED_DEPS

  compile.with PROVIDED_DEPS,
               COMPILE_DEPS

  gwt_enhance(project, %w(org.realityforge.braincheck.BrainCheck org.realityforge.braincheck.BrainCheckDev))

  test.using :testng
  test.with TEST_DEPS
  test.options[:properties] = { 'braincheck.dynamic_provider' => 'true', 'braincheck.environment' => 'development' }
  test.options[:java_args] = ['-ea']

  package(:jar)
  package(:sources)
  package(:javadoc)

  iml.excluded_directories << project._('tmp/gwt')

  ipr.add_default_testng_configuration(:jvm_args => '-ea -Dbraincheck.dynamic_provider=true -Dbraincheck.environment=development')
  ipr.add_component_from_artifact(:idea_codestyle)
end

desc 'Deploy Javadocs to github site'
task 'deploy:docs' do
  task('braincheck:package').invoke
  sh 'yarn deploy:docs'
end
