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

            var qc = new QcBuilder(arg).build();

            Files.write(Path.of(arg + ".qc"), qc.getBytes(StandardCharsets.UTF_8));
            System.out.println("Wrote " + arg + ".qc");
        }
    }

    private static class QcBuilder {

        private static final String tab = "  ";

        private static final List<String> FLOAT_KEYS = List.of("style", "damage_mod", "health", "proj_speed_mod", "homing");
        private static final List<String> STRING_KEYS = List.of("mdl_head", "mdl_body",
                "obit_method", "obit_name", "snd_death", "snd_attack", "snd_idle", "snd_pain", "snd_sight", "mdl_proj", "snd_misc");

        private final String fileName;
        private final Map<String, String> values;
        private String qc;

        QcBuilder(String filePath) throws IOException {
            var pathParts = filePath.split("/");
            fileName = pathParts[pathParts.length-1];
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
            qc = "";
            qc += "void() " + fileName.split("\\.")[0] + " = \n";
            qc += "{\n";

            FLOAT_KEYS.forEach(key -> addKey(key, false));
            STRING_KEYS.forEach(key -> addKey(key, true));

            qc += tab + values.get("classname") + "();\n";

            qc += "};";

            return qc;
        }

        private void addKey(String key, boolean quote) {
            var qs = quote ? "\"" : "";
            if (values.containsKey(key)) {
                qc += tab + "if (!self." + key + ")\n";
                qc += tab + "{\n";
                qc += tab + tab + "self." + key + " = " + qs + values.get(key) + qs + ";\n";
                qc += tab + "}\n";
            }
        }

    }

}
