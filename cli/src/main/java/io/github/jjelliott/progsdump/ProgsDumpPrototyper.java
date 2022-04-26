package io.github.jjelliott.progsdump;

import picocli.CommandLine;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "pd-monster-prototyper", description = "Converts a customized monster entity to a spawn " +
        "function.")
public class ProgsDumpPrototyper implements Callable<Integer> {

    public static void main(String[] args) throws IOException {
        new CommandLine(new ProgsDumpPrototyper()).execute(args);
//        if (args.length == 0) {
//            System.out.println("No file specified, exiting.");
//        }
//
//        for (String arg : args) {
//            if (arg.startsWith("-p=")){
//                var progsSrcPath = arg.replaceFirst("-p=","");
//
//            }
//            if (!arg.endsWith(".txt")){
//                System.out.println("Arg " + arg + " is not a text file, skipping.");
//                continue;
//            }
//            System.out.println("Processing " + arg + "");
//
//            var output = new EntityDefinitionBuilder(arg).build();
//
//            Files.write(Path.of(arg + ".qc"), output.getBytes(StandardCharsets.UTF_8));
//            System.out.println("Wrote " + arg + ".qc");
//        }
    }

    @CommandLine.Option(names = {"-d", "--outputDir"}, description = "Output directory")
    private String outputDirectory;

    @CommandLine.Option(names = {"-p", "--progsSrc"}, description = "progs.src to add filename to")
    private String progsFile;

    @CommandLine.Option(names = {"-f", "--fgd"}, description = "FGD file to append definitions to")
    private File fgd;

    @CommandLine.Parameters(paramLabel = "FILE", description = "one or more files to process", arity = "1")
    private File[] files;

    @CommandLine.Option(names = { "-h", "--help" }, usageHelp = true, description = "display a help message")
    private boolean helpRequested = false;

    @Override
    public Integer call() throws Exception {
        for (File file : files) {

            if (!file.getName().endsWith(".txt")) {
                System.out.println("Arg " + file.getName() + " is not a text file, skipping.");
                continue;
            }
            System.out.println("Processing " + file.getName() + "");

            var output = EntityDefinitionBuilder.getInstance(file, fgd).build();

            var outputFileName = file.getName().replaceFirst(
                    "monster_",
                    "").replaceFirst(".txt", ".qc");
            var outputFilePath = ((outputDirectory != null ? outputDirectory :
                    file.getAbsoluteFile().getParentFile().getPath()) + "/" + outputFileName).replaceAll("//","/");
            Files.write(Path.of(outputFilePath), output.getBytes(StandardCharsets.UTF_8));
            if (progsFile != null){
                Files.write(Path.of(progsFile), ("\n" + outputFileName).getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
            System.out.println("Wrote " + outputFilePath);
        }
        return 0;
    }
}
