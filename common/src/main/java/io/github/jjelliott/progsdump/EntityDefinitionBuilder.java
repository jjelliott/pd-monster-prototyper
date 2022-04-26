package io.github.jjelliott.progsdump;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityDefinitionBuilder {

    protected static final String tab = "  ";

    protected static final List<String> FLOAT_KEYS = List.of("style", "effects", "infight_mode", "berserk", "damage_mod",
            "health", "keep_ammo",
            "proj_speed_mod",
            "homing", "skin", "skin_head", "skin_proj", "skin_exproj");
    protected static final List<String> STRING_KEYS = List.of("mdl_head", "mdl_body", "mdl_gib1","mdl_gib2","mdl_gib3",
            "mdl_exproj", "mdl_proj",
            "obit_method", "obit_name",
            "snd_death", "snd_attack", "snd_idle", "snd_pain", "snd_sight", "snd_misc", "snd_misc1", "snd_misc2",
            "snd_misc3", "snd_hit");

    protected final String functionName;
    protected final Map<String, String> values;
    protected String qc;

    static EntityDefinitionBuilder getInstance(File file, File fgd) throws IOException {
        if (fgd != null) {
            return new FgdAppendingEntityDefinitionBuilder(file, fgd);
        }
        else return new EntityDefinitionBuilder(file);
    }

    EntityDefinitionBuilder(String filePath) throws IOException {
        this(new File(filePath));
    }

    EntityDefinitionBuilder(File file) throws IOException {
        String fileName = file.getName();
        functionName = fileName.split("\\.")[0];
        values = new HashMap<>();
        var lines = Files.readString(Path.of(file.getPath())).split("" + System.lineSeparator());
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

    protected void addFgd() {
        qc += "/* Copy and paste the lines between the slashes into your FGD." + System.lineSeparator();
        qc += "///// Start FGD Entry /////" + System.lineSeparator();
        qc += makeFgdEntry();
        qc += "///// End FGD Entry /////" + System.lineSeparator();
        qc += "*/" + System.lineSeparator();
    }

    protected String makeFgdEntry(){
       var fgdString = "@PointClass base(" + values.get("classname") + ")";
        if (values.containsKey("mdl_body")) {
            fgdString += " model(" + System.lineSeparator() +
                    tab + "{{" + System.lineSeparator() +
                    tab + tab + tab + "mdl_body -> {\"path\" : mdl_body, \"skin\" : skin}, {\"path\": " +
                    "\"" + values.get("mdl_body") + "\" , \"skin\" : skin}" + System.lineSeparator() +
                    tab + "}}" + System.lineSeparator() +
                    ")";
        }
        fgdString += " = " + functionName + " : " + "" + System.lineSeparator();
        fgdString += "\"Customized " + values.get("classname") + "\"" + System.lineSeparator();
        fgdString += "[\n]" + System.lineSeparator();
        return fgdString;
    }

}
