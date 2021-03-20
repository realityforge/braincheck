require 'buildr/git_auto_version'
require 'buildr/gpg'
require 'buildr/gwt'

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

  optional_deps = [artifact(:javax_json), artifact(:testng)]
  deps = [artifact(:javax_annotation), artifact(:jsinterop_annotations)] + optional_deps
  pom.include_transitive_dependencies << deps
  pom.optional_dependencies << optional_deps
  pom.dependency_filter = Proc.new {|dep| dep[:scope].to_s != 'test' && deps.include?(dep[:artifact])}

  compile.with :javax_annotation,
               :javax_json,
               :testng,
               :jsinterop_annotations

  gwt_enhance(project, :extra_deps => [_('src/main/super')])

  test.using :testng
  test.with :guiceyloops
  test.options[:properties] = { 'braincheck.environment' => 'development' }
  test.options[:java_args] = ['-ea']

  cleanup_javadocs(project, 'org')

  package(:jar)
  package(:sources)
  package(:javadoc)

  doc.using(:javadoc,
            :windowtitle => 'Braincheck API Documentation',
            :linksource => true,
            :timestamp => false,
            :link => %w(https://docs.oracle.com/javase/8/docs/api)
  ).sourcepath << compile.sources

  ipr.add_default_testng_configuration(:jvm_args => '-ea -Dbraincheck.environment=development')
  ipr.add_component_from_artifact(:idea_codestyle)
end

define 'braincheck-j2cl' do
  project.group = 'org.realityforge.braincheck'
  compile.options.source = '1.8'
  compile.options.target = '1.8'
  compile.options.lint = 'all'

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  pom.add_apache_v2_license
  pom.add_github_project('realityforge/braincheck')
  pom.add_developer('realityforge', 'Peter Donald')

  optional_deps = [artifact(:javax_json), artifact(:testng)]
  deps = [artifact(:javax_annotation), artifact(:jsinterop_annotations)] + optional_deps
  pom.include_transitive_dependencies << deps
  pom.optional_dependencies << optional_deps
  pom.dependency_filter = Proc.new {|dep| dep[:scope].to_s != 'test' && deps.include?(dep[:artifact])}

  compile.with :javax_annotation,
               :javax_json,
               :testng,
               :jsinterop_annotations

  package(:jar).tap do |j|
    j.include("#{project._(:source, :main, :java)}/*")
  end
  package(:sources)
  package(:javadoc)

  project.no_ipr
end

desc 'Deploy Javadocs to github site'
task 'deploy:docs' do
  task('braincheck:package').invoke
  sh 'yarn deploy:docs'
end
