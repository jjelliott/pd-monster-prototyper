package io.github.jjelliott.progsdump;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QcBuilder {

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
        var lines = Files.readString(Path.of(filePath)).split("\n");
        Arrays.stream(lines)
                .filter(it -> it.contains("\""))
                .map(it -> it.split("\"\\s\""))
                .forEach(line -> values.put(removeQuotes(line[0]), removeQuotes(line[1])));
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

    private String removeQuotes(String s){
        return s.replaceAll("\"", "");
    }

}
