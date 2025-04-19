package dev.magicmq.jythoncompile;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.python.core.PyException;
import org.python.core.PySystemState;
import org.python.core.imp;
import org.python.modules._py_compile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mojo(name = "jython-compile", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, threadSafe = true)
public class JythonCompile extends AbstractMojo {

    @Parameter(defaultValue = "${project.basedir}/src/main/resources")
    private File sourceDirectory;

    @Parameter(defaultValue = "${project.build.outputDirectory}")
    private File outputDirectory;

    @Parameter
    private String[] jythonProperties;

    @Parameter(defaultValue = "false")
    private boolean skip;

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Skipping Jython compile.");
            return;
        }

        if (!sourceDirectory.exists())
            return;

        Path sourceDirectoryPath = sourceDirectory.toPath();
        Path outputDirectoryPath = outputDirectory.toPath();
        try (Stream<Path> walk = Files.walk(sourceDirectoryPath)) {
            Set<Path> sources = walk
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".py"))
                    .collect(Collectors.toSet());
            process(sources, sourceDirectoryPath, outputDirectoryPath);
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to read sourceDirectory.", e);
        }
    }

    private void initPySystemState() {
        Properties props = new Properties();
        props.setProperty("python.cachedir.skip", "true");
        if (jythonProperties != null) {
            for (String property : jythonProperties) {
                String[] split = property.split("=");
                if (split.length == 2) {
                    props.setProperty(split[0], split[1]);
                } else {
                    getLog().warn("Malformed Jython property: " + property);
                }
            }
        }

        getLog().info("Initializing Jython with the folowing properties: " + props);

        PySystemState.initialize(System.getProperties(), props);
    }

    private void process(Set<Path> toCompile, Path sourceDirectory, Path outputDirectory) throws MojoExecutionException{
        if (!toCompile.isEmpty()) {
            initPySystemState();

            for (Path source : toCompile) {
                Path relativePath = sourceDirectory.relativize(source);
                Path resolvedOutputDirectory = relativePath.getParent() != null
                        ? outputDirectory.resolve(relativePath.getParent())
                        : outputDirectory;
                process(source, resolvedOutputDirectory);
            }
        }
    }

    private void process(Path toCompile, Path outputDirectory) throws MojoExecutionException {
        File src = toCompile.toFile();
        try {
            String fileName = src.getName();
            String moduleName = _py_compile.getModuleName(src);
            String moduleFile = moduleName.replace('.', '/');
            if (src.getName().endsWith("__init__.py")) {
                moduleFile += "/__init__.py";
            } else {
                moduleFile += ".py";
            }


            Path compiledPath = outputDirectory.resolve(Path.of(imp.makeCompiledFilename(fileName)));
            File compiled = compiledPath.toFile();

            getLog().info("Compiling file \"" + src + "\" to output \"" + compiled + "\"");
            compile(src, compiled, moduleName, moduleFile);
        } catch (RuntimeException e) {
            throw new MojoExecutionException("Failed to compile file " + src, e);
        }
    }

    private void compile(File src, File compiled, String moduleName, String fileName) throws MojoExecutionException {
        byte[] bytes;
        try {
            bytes = imp.compileSource(moduleName, src, fileName);
        } catch (PyException pye) {
            throw new MojoExecutionException("Failed to compile file " + src, pye);
        }

        File dir = compiled.getParentFile();
        if (!dir.exists() && !compiled.getParentFile().mkdirs()) {
            throw new MojoExecutionException("Unable to make directory for compiled file: " + compiled);
        } else {
            imp.cacheCompiledSource(src.getAbsolutePath(), compiled.getAbsolutePath(), bytes);
        }
    }
}
