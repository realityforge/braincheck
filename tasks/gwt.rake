require 'buildr/gwt'

#
# Enhance the Buildr project to compile gwt sources.
# For each of the supplied gwt modules, this task will create
# a synthetic gwt module that includes a single entrypoint to
# compile against. It will also include all the sources in the jar.
#
def gwt_enhance(project, gwt_modules)
  base_synthetic_module_dir = project._(:generated, :synthetic_gwt_module, :main, :resources)
  t = project.task('gwt_synthetic_module') do
    gwt_modules.each do |gwt_module|
      file = "#{base_synthetic_module_dir}/#{gwt_module.gsub('.', '/')}Test.gwt.xml"
      mkdir_p File.dirname(file)
      IO.write(file, <<CONTENT)
<module>
  <inherits name="#{gwt_module}"/>
  <inherits name="com.google.gwt.user.User"/>
  <collapse-all-properties/>
</module>
CONTENT
    end
  end
  dir = project.file(base_synthetic_module_dir => [t.name])

  dependencies =
    project.compile.dependencies + [project.compile.target] + [dir] + [Buildr.artifact(:gwt_user)]
  unless ENV['GWT'] == 'no'
    project.gwt(gwt_modules.collect {|gwt_module| "#{gwt_module}Test"},
                { :java_args => %w(-Xms512M -Xmx1024M), :dependencies => dependencies })
  end

  project.package(:jar).tap do |j|
    j.include("#{project._(:source, :main, :java)}/*")
  end

  config = {}
  gwt_modules.each do |gwt_module|
    config[gwt_module] = false
  end
  project.iml.add_gwt_facet(config, :settings => { :compilerMaxHeapSize => '1024' }, :gwt_dev_artifact => :gwt_dev)
end
