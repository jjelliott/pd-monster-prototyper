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
            System.out.println("Processing " + arg + "");

            var qc = new QcBuilder(arg).build();

            Files.write(Path.of(arg + ".qc"), qc.getBytes(StandardCharsets.UTF_8));
            System.out.println("Wrote " + arg + ".qc");
        }
    }


}
