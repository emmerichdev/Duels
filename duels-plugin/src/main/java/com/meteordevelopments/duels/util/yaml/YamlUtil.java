package com.meteordevelopments.duels.util.yaml;

import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

public final class YamlUtil {

    private static final Yaml BUKKIT_YAML;
    private static final Yaml YAML;

    static {
        final DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        final Representer yamlRepresenter = new YamlRepresenter(options);
        yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        BUKKIT_YAML = new Yaml(new YamlBukkitConstructor(), yamlRepresenter, options);
        YAML = new Yaml(options);
    }

    private YamlUtil() {
    }

    public static String yamlDump(final Object object) {
        return YAML.dump(object);
    }

    public static String bukkitYamlDump(final Object object) {
        return BUKKIT_YAML.dump(object);
    }

    public static <T> T yamlLoad(final String yaml) {
        return YAML.load(yaml);
    }

    public static <T> T bukkitYamlLoadAs(final String yaml, final Class<T> type) {
        return BUKKIT_YAML.loadAs(yaml, type);
    }

    private static class YamlBukkitConstructor extends YamlConstructor {

        public YamlBukkitConstructor() {
            super(new LoaderOptions());
            this.yamlConstructors.put(new Tag(Tag.PREFIX + "org.bukkit.inventory.ItemStack"), yamlConstructors.get(Tag.MAP));
        }
    }
}
