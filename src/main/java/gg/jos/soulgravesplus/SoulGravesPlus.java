package gg.jos.soulgravesplus;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import dev.faultyfunctions.soulgraves.utils.Soul;
import gg.jos.soulgravesplus.commands.ReloadCommand;
import gg.jos.soulgravesplus.events.hologram.decentholograms.SoulExplodeDecentHologramListener;
import gg.jos.soulgravesplus.events.hologram.decentholograms.SoulPickupDecentHologramListener;
import gg.jos.soulgravesplus.events.hologram.decentholograms.SoulSpawnDecentHologramListener;
import gg.jos.soulgravesplus.events.hologram.decentholograms.SoulUpdaterDecentHologram;
import gg.jos.soulgravesplus.events.hologram.fancyholograms.SoulExplodeFancyHologramListener;
import gg.jos.soulgravesplus.events.hologram.fancyholograms.SoulPickupFancyHologramListener;
import gg.jos.soulgravesplus.events.hologram.fancyholograms.SoulSpawnFancyHologramListener;
import gg.jos.soulgravesplus.events.hologram.fancyholograms.SoulUpdaterFancyHologram;
import gg.jos.soulgravesplus.events.logger.SoulExplodeLoggerListener;
import gg.jos.soulgravesplus.events.logger.SoulPickupLoggerListener;
import gg.jos.soulgravesplus.events.logger.SoulSpawnLoggerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public final class SoulGravesPlus extends JavaPlugin {

    // Formatting Config
    public DateTimeFormatter dateTimeFormatter;
    public String timeFormat;

    // Logger Config
    public boolean loggerEnabled;
    public boolean logSoulSpawns;
    public boolean logSoulPickups;
    public boolean logSoulExplosions;

    public String logSoulSpawnsMessage;
    public String logSoulPickupsMessage;
    public String logSoulExplosionsMessage;

    // Hologram Config
    public boolean hologramEnabled;
    public double hologramXOffset;
    public double hologramYOffset;
    public double hologramZOffset;
    public boolean hologramBackground;
    public String[] hologramBackgroundColor;
    public List<String> hologramLines;
    public String hologramManager; // Unused but exists for future needs
    public long hologramUpdateTicks;

    @Override
    public void onEnable() {

        if (getServer().getPluginManager().getPlugin("SoulGraves") == null) {
            this.getLogger().warning("SoulGraves not found, disabling SoulGravesPlus");
            this.getServer().getPluginManager().disablePlugin(this);
        }

        // Logger Messages

        this.getLogger().info("\u001B[35mSoulGravesPlus enabled! Made by JosTheDude with \u001B[31m<3\u001B[35m and cookies\u001B[0m\n");
        this.getLogger().info("\u001B[37mPlease report any issues to \u001B[37m\u001B[4mhttps://github.com/JosTheDude/SoulGravesPlus\u001B[0m");

        // Config & Feature Subsets
        saveDefaultConfig();
        updateConfig();

        // Commands
        this.getCommand("soulgravesplus").setExecutor(new ReloadCommand(this));

    }

    public void updateConfig() {
        // Option Features
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(this.getConfig().getString("options.date-format", "yyyy-MM-dd HH:mm:ss"));
        this.timeFormat = this.getConfig().getString("options.time-format", "{m}m {s}s");

        // Logger Features
        this.loggerEnabled = this.getConfig().getBoolean("logger.enabled", true);
        this.logSoulSpawns = this.getConfig().getBoolean("logger.log-soul-spawns", true);
        this.logSoulPickups = this.getConfig().getBoolean("logger.log-soul-pickups", true);
        this.logSoulExplosions = this.getConfig().getBoolean("logger.log-soul-explosions", true);

        this.logSoulSpawnsMessage = this.getConfig().getString(
                "logger.log-soul-spawns-message",
                "\u001B[37m[SoulTracker] \u001B[92mSoul spawned for \u001B[93m{soulOwner}\u001B[92m at \u001B[93m{x} {y} {z}\u001B[0m"
        );
        this.logSoulPickupsMessage = this.getConfig().getString(
                "logger.log-soul-pickups-message",
                "\u001B[37m[SoulTracker] \u001B[93m{soulOwner}\u001B[92m picked up a soul at \u001B[93m{x} {y} {z}\u001B[0m"
        );
        this.logSoulExplosionsMessage = this.getConfig().getString(
                "logger.log-soul-explosions-message",
                "\u001B[37m[SoulTracker] \u001B[91mSoul for \u001B[93m{soulOwner}\u001B[91m exploded at \u001B[93m{x} {y} {z}\u001B[0m"
        );

        // Hologram Features
        this.hologramEnabled = this.getConfig().getBoolean("hologram.enabled", true);

        this.hologramXOffset = this.getConfig().getDouble("hologram.x-offset", 0.0);
        this.hologramYOffset = this.getConfig().getDouble("hologram.y-offset", 0.3);
        this.hologramZOffset = this.getConfig().getDouble("hologram.z-offset", 0.0);

        this.hologramBackground = this.getConfig().getBoolean("hologram.custom-background", false);
        this.hologramBackgroundColor = this.getConfig().getString("hologram.background-color", "1,100,255,79").split(",");

        this.hologramLines = this.getConfig().getStringList("hologram.lines");
        this.hologramUpdateTicks = this.getConfig().getLong("hologram.update-ticks", 10L);

        featureSubsets();

    }

    private void featureSubsets() {

        // Logger Feature
        if (this.loggerEnabled) {
            loggerFeatures();
        } else {
            this.getLogger().warning("Logger features are disabled in the config.yml.");
        }

        // Hologram Features
        if (this.hologramEnabled) {
            hologramFeatures();
        } else {
            this.getLogger().warning("Hologram features are disabled in the config.yml.");
        }

    }

    private void loggerFeatures() {

        this.getServer().getPluginManager().registerEvents(new SoulSpawnLoggerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new SoulPickupLoggerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new SoulExplodeLoggerListener(this), this);

        this.getLogger().info("Logger features enabled.");
    }

    private void hologramFeatures() {
        if (this.getServer().getPluginManager().getPlugin("FancyHolograms") != null) {

            this.hologramManager = "FancyHolograms";

            HologramManager manager = FancyHologramsPlugin.get().getHologramManager();

            this.getServer().getPluginManager().registerEvents(new SoulSpawnFancyHologramListener(this, manager), this);
            this.getServer().getPluginManager().registerEvents(new SoulPickupFancyHologramListener(this, manager), this);
            this.getServer().getPluginManager().registerEvents(new SoulExplodeFancyHologramListener(this, manager), this);
            this.getServer().getScheduler().runTaskTimer(this, new SoulUpdaterFancyHologram(this, manager), this.hologramUpdateTicks, this.hologramUpdateTicks);

            this.getLogger().info("FancyHolograms found! Hologram features enabled.");
        } else if (this.getServer().getPluginManager().getPlugin("DecentHolograms") != null) {

            this.hologramManager = "DecentHolograms";

            this.getServer().getPluginManager().registerEvents(new SoulSpawnDecentHologramListener(this), this);
            this.getServer().getPluginManager().registerEvents(new SoulPickupDecentHologramListener(this), this);
            this.getServer().getPluginManager().registerEvents(new SoulExplodeDecentHologramListener(this), this);
            this.getServer().getScheduler().runTaskTimer(this, new SoulUpdaterDecentHologram(this), this.hologramUpdateTicks, this.hologramUpdateTicks);

            this.getLogger().info("DecentHolograms found! Hologram features enabled.");
        } else {
            this.getLogger().warning("FancyHolograms or DecentHolograms not found! Hologram features disabled.");
        }
    }

    public String formatTime(int time) {
        if (time < 0) time = 0;
        int days = time / 86400;
        int hours = (time % 86400) / 3600;
        int minutes = (time % 3600) / 60;
        int seconds = time % 60;
        return this.timeFormat
                .replace("{d}", days + "")
                .replace("{h}", hours + "")
                .replace("{s}", seconds + "")
                .replace("{m}", minutes + "");
    }

    public List<String> parseHologramLines(Soul soul) {
        // Define placeholders
        String soulOwner = Bukkit.getOfflinePlayer(soul.getOwnerUUID()).getName();
        String formattedTime = LocalDateTime.now().format(this.dateTimeFormatter);
        int soulTime = soul.getTimeLeft();
        String formattedSoulTime = this.formatTime(soulTime);
        int invAmount = soul.getInventory().size();
        int expAmount = soul.getXp();

        // Replace placeholders in the config lines
        return this.hologramLines.stream()
                .map(line -> line
                        .replace("{soulOwner}", Objects.requireNonNullElse(soulOwner, "unknown"))
                        .replace("{formattedTime}", formattedTime)
                        .replace("{formattedSoulTime}", formattedSoulTime)
                        .replace("{soulTime}", soulTime + "")
                        .replace("{invAmount}", invAmount + "")
                        .replace("{expAmount}", expAmount + "")
                        .replace("&", "§"))
                .toList();
    }

}
