package io.github.jjelliott.progsdump;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProgsDumpPrototyper {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("No file specified, exiting.");
        }

        for (String arg : args) {
            if (!arg.endsWith(".txt")){
                System.out.println("Arg " + arg + " is not a text file, skipping.");
                continue;
            }
            System.out.println("Processing " + arg + "");

            var output = new EntityDefinitionBuilder(arg).build();

            Files.write(Path.of(arg + ".qc"), output.getBytes(StandardCharsets.UTF_8));
            System.out.println("Wrote " + arg + ".qc");
        }
    }


}
