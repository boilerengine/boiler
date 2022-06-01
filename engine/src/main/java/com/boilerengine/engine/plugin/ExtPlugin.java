package com.boilerengine.engine.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static java.lang.ClassLoader.getSystemResourceAsStream;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.regex.Pattern.compile;

public class ExtPlugin implements Plugin {

    private static final Pattern[] PATTERN_PLUGIN_FILE = new Pattern[]{compile("\\.jar$")};

    private final String name;
    private final BooleanSupplier enabled;
    private boolean naggable;
    private final YamlConfiguration config = new YamlConfiguration();

    public ExtPlugin(String name, BooleanSupplier enabled) {
        this.name = name;
        this.enabled = enabled;
    }

    public String getName() { return name; }
    public boolean isNaggable() {
        return naggable;
    }
    public void setNaggable(boolean naggable) { this.naggable = naggable; }
    public boolean isEnabled() { return enabled.getAsBoolean(); }

    public Server getServer() { return Bukkit.getServer(); }
    public Logger getLogger() { return Bukkit.getLogger(); }

    public File getDataFolder() { return new File("plugin", getName()); }
    public PluginDescriptionFile getDescription() { return new PluginDescriptionFile(getName(), "1.0.0", ""); }

    public InputStream getResource(String uri) { return getSystemResourceAsStream(uri); }
    public void saveResource(String uri, boolean replace) {
        final Path file = getDataFolder().toPath().resolve(uri);
        if (!replace && Files.exists(file)) return;
        try (final InputStream in = getResource(uri)) { Files.copy(in, file); }
        catch (Throwable reason) { throw new RuntimeException(reason); }
    }

    public FileConfiguration getConfig() { return config; }
    public void reloadConfig() {
        try { config.load(new File(getDataFolder(), "config.yml")); }
        catch (Throwable reason) { throw new RuntimeException(reason); }
    }
    public void saveConfig() {
        try { config.save(new File(getDataFolder(), "config.yml")); }
        catch (Throwable reason) { throw new RuntimeException(reason); }
    }
    public void saveDefaultConfig() { saveResource("config.yml", false); }

    public PluginLoader getPluginLoader() { return new PluginLoader() {
        public Plugin loadPlugin(File file) throws InvalidPluginException, UnknownDependencyException {
            try { return Bukkit.getPluginManager().loadPlugin(file); }
            catch (InvalidDescriptionException reason) { throw new InvalidPluginException(reason); }
        }
        public PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
            try {
                final JarFile jar = new JarFile(file);
                return new PluginDescriptionFile(jar.getInputStream(jar.getJarEntry("plugin.yml")));
            } catch (IOException reason) { throw new InvalidDescriptionException(reason); }
        }
        public Pattern[] getPluginFileFilters() { return PATTERN_PLUGIN_FILE; }
        public Map createRegisteredListeners(Listener listener, Plugin plugin) { return emptyMap(); }

        public void enablePlugin(Plugin plugin) { Bukkit.getPluginManager().enablePlugin(plugin); }
        public void disablePlugin(Plugin plugin) { Bukkit.getPluginManager().disablePlugin(plugin); }
    }; }

    public void onDisable() {}
    public void onLoad() {}
    public void onEnable() {}

    public List<String> onTabComplete(CommandSender a, Command b, String c, String[] d) { return emptyList(); }
    public boolean onCommand(CommandSender a, Command b, String c, String[] d) {
        return false;
    }

    public ChunkGenerator getDefaultWorldGenerator(String a, String b) { return new ChunkGenerator() {
        @Override
        public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
            return createChunkData(world);
        }
    }; }

    @Override public @Nullable BiomeProvider getDefaultBiomeProvider(@NotNull String s, @Nullable String s1) {
        return null;
    }

}
