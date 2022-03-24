package io.github.jjelliott.progsdump;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityDefinitionBuilder {

    private static final String tab = "  ";

    private static final List<String> FLOAT_KEYS = List.of("style", "effects", "infight_mode", "berserk", "damage_mod",
            "health", "keep_ammo",
            "proj_speed_mod",
            "homing", "skin", "skin_head", "skin_proj", "skin_exproj");
    private static final List<String> STRING_KEYS = List.of("mdl_head", "mdl_body", "mdl_gib1","mdl_gib2","mdl_gib3",
            "mdl_exproj", "mdl_proj",
            "obit_method", "obit_name",
            "snd_death", "snd_attack", "snd_idle", "snd_pain", "snd_sight", "snd_misc", "snd_misc1", "snd_misc2",
            "snd_misc3", "snd_hit");

    private final String functionName;
    private final Map<String, String> values;
    private String qc;

    EntityDefinitionBuilder(String filePath) throws IOException {
        var pathParts = filePath.split("/");
        String fileName = pathParts[pathParts.length - 1];
        functionName = fileName.split("\\.")[0];
        values = new HashMap<>();
        var lines = Files.readString(Path.of(filePath)).split("" + System.lineSeparator());
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
        addFgd();
        qc += "void() " + functionName + " = " + System.lineSeparator();
        qc += "{" + System.lineSeparator();

        FLOAT_KEYS.forEach(key -> addKey(key, false));
        STRING_KEYS.forEach(key -> addKey(key, true));

        qc += tab + values.get("classname") + "();" + System.lineSeparator();

        qc += "};";

        return qc;
    }

    private void addKey(String key, boolean quote) {
        var qs = quote ? "\"" : "";
        if (values.containsKey(key)) {
            qc += tab + "if (!self." + key + ")" + System.lineSeparator();
            qc += tab + "{" + System.lineSeparator();
            qc += tab + tab + "self." + key + " = " + qs + values.get(key) + qs + ";" + System.lineSeparator();
            qc += tab + "}" + System.lineSeparator();
        }
    }

    private String removeQuotes(String s) {
        return s.replaceAll("\"", "");
    }

    private void addFgd() {
        qc += "/* Copy and paste the lines between the slashes into your FGD." + System.lineSeparator();
        qc += "///// Start FGD Entry /////" + System.lineSeparator();
        qc += "@PointClass base(" + values.get("classname") + ")";
        if (values.containsKey("mdl_body")) {
            qc += " model(" + System.lineSeparator() +
                    tab + "{{" + System.lineSeparator() +
                    tab + tab + tab + "mdl_body -> {\"path\" : mdl_body, \"skin\" : skin}, {\"path\": " +
                    "\"" + values.get("mdl_body") + "\" , \"skin\" : skin}" + System.lineSeparator() +
                    tab + "}}" + System.lineSeparator() +
                    ")";
        }
        qc += " = " + functionName + " : " + "" + System.lineSeparator();
        qc += "\"Customized " + values.get("classname") + "\"" + System.lineSeparator();
        qc += "[\n]" + System.lineSeparator();
        qc += "///// End FGD Entry /////" + System.lineSeparator();
        qc += "*/" + System.lineSeparator();
    }

}
