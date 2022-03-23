package io.github.jjelliott.progsdump;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProgsDumpPrototyper {

    public static void main(String[] args) throws IOException {
        var argList = Arrays.stream(args).collect(Collectors.toList());
        if (argList.size() == 0) {
            System.out.println("No file specified, exiting.");
        }

        for (String arg : argList) {
            System.out.println("Processing " + arg + "");
//            var entDef = Files.readString(Path.of(arg));
//            System.out.println(entDef);
//            var lines = entDef.split("\n");
//
//            var qc = new StringBuilder();
//            qc.append("void() " + arg.split("\\.")[0] + " = \n{\n");
//            Arrays.stream(lines).filter(it -> it.contains("classname")).findFirst().ifPresent(it -> {
//                qc.append("  " + it.split("\\s")[1].replaceAll("\"", "") + "();");
//            });
//            qc.append("\n};");
//            System.out.println(qc);

            var argParts = arg.split("/");

            var qc = new QcBuilder(argParts[argParts.length-1]).build();
            System.out.println(qc);
            Files.write(Path.of(arg + ".qc"), qc.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static class QcBuilder {

        private static final List<String> FLOAT_KEYS = List.of("style", "damage_mod", "health", "proj_speed_mod", "homing");
        private static final List<String> STRING_KEYS = List.of("mdl_head", "mdl_body",
                "obit_method", "obit_name", "snd_death", "snd_attack", "snd_idle", "snd_pain", "snd_sight", "mdl_proj", "snd_misc");

        String fileName;
        Map<String, String> values;
        StringBuilder qc;

        QcBuilder(String filePath) throws IOException {
            fileName = filePath;
            values = new HashMap<>();
            Arrays.stream(Files.readString(Path.of(filePath)).split("\n")).forEach(it -> {
                if (it.contains("\"")) {
                    var line = it.split("\"\\s\"");
                    values.put(line[0].replaceAll("\"", ""), line[1].replaceAll("\"", ""));
                }
            });
            if (!values.containsKey("classname")) {
                throw new IllegalArgumentException("Not a valid entity");
            }
        }

        String build() {
            qc = new StringBuilder();
            qc.append("void() ").append(fileName.split("\\.")[0]).append(" = \n{\n");

            FLOAT_KEYS.forEach(this::addFloatKey);
            STRING_KEYS.forEach(this::addStringKey);

            qc.append("  ").append(values.get("classname")).append("();");

            qc.append("\n};");

            return qc.toString();
        }

        private void addFloatKey(String key) {
            addKey(key, false);
        }

        private void addStringKey(String key) {
            addKey(key, true);
        }

        private void addKey(String key, boolean quote) {

            if (values.containsKey(key)) {
                qc.append("  if (!self.").append(key).append(")\n  {\n");
                qc.append("    self.").append(key).append(" = ");
                if (quote) {
                    qc.append("\"");
                }
                qc.append(values.get(key));
                if (quote) {
                    qc.append("\"");
                }
                qc.append(";\n");
                qc.append("  }\n");
            }
        }
    }

}
