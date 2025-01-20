package dev.magicmq.jythoncompiler;


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

public class Main {

    public static void main(String[] args) {
        if (args.length > 0) {
            Path dir = Path.of(args[0]);

            if (!Files.exists(dir)) {
                return;
            }

            try (Stream<Path> walk = Files.walk(dir)) {
                Set<Path> sources = walk
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".py"))
                        .collect(Collectors.toSet());
                process(sources, dir);
            } catch (IOException e) {
                System.err.println("Compile failed " + e);
            }
        } else {
            throw new RuntimeException("Must specify a directory to compile");
        }
    }

    private static void process(Set<Path> toCompile, Path destDir) {
        if (!toCompile.isEmpty()) {
            if (toCompile.size() == 1) {
                System.out.println("Compiling 1 file");
            } else {
                System.out.println("Compiling " + toCompile.size() + " files");
            }

            Properties props = new Properties();
            props.setProperty("python.cachedir.skip", "true");
            PySystemState.initialize(System.getProperties(), props);

            for (Path path : toCompile) {
                File src = path.toFile();
                try {
                    System.out.println("Compiling file " + src);

                    String name = _py_compile.getModuleName(src);
                    String compiledFilePath = name.replace('.', '/');
                    if (src.getName().endsWith("__init__.py")) {
                        compiledFilePath = compiledFilePath + "/__init__.py";
                    } else {
                        compiledFilePath = compiledFilePath + ".py";
                    }

                    Path compiledPath = destDir.resolve(Path.of(imp.makeCompiledFilename(compiledFilePath)));
                    File compiled = compiledPath.toFile();

                    System.out.println("Compiling file \"" + src + "\" to output \"" + compiled + "\"");
                    compile(src, compiled, name, compiledFilePath);
                } catch (RuntimeException e) {
                    System.out.println("Could not compile " + src);
                    throw e;
                }
            }

            System.out.println("Finished compiling all files");
        }
    }

    private static void compile(File src, File compiled, String moduleName, String fileName) {
        byte[] bytes;
        try {
            bytes = imp.compileSource(moduleName, src, fileName);
        } catch (PyException pye) {
            pye.printStackTrace();
            throw new RuntimeException("Compile failed. See the compiler error output for details.");
        }

        File dir = compiled.getParentFile();
        if (!dir.exists() && !compiled.getParentFile().mkdirs()) {
            throw new RuntimeException("Unable to make directory for compiled file: " + compiled);
        } else {
            imp.cacheCompiledSource(src.getAbsolutePath(), compiled.getAbsolutePath(), bytes);
        }
    }
}
