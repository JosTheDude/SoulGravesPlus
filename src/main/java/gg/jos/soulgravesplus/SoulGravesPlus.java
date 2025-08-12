package gg.jos.soulgravesplus;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import dev.faultyfunctions.soulgraves.utils.Soul;
import gg.jos.soulgravesplus.commands.ReloadCommand;
import gg.jos.soulgravesplus.events.deathcoordinates.SoulSpawnDeathCoordinatesListener;
import gg.jos.soulgravesplus.events.hologram.decentholograms.*;
import gg.jos.soulgravesplus.events.hologram.fancyholograms.*;
import gg.jos.soulgravesplus.events.logger.SoulExplodeLoggerListener;
import gg.jos.soulgravesplus.events.logger.SoulPickupLoggerListener;
import gg.jos.soulgravesplus.events.logger.SoulSpawnLoggerListener;
import gg.jos.soulgravesplus.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class SoulGravesPlus extends JavaPlugin {

    // Formatting Config
    public SimpleDateFormat dateTimeFormatter;
    public String timeFormat;
    private ConcurrentHashMap<String, String> worldNameAliases;

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
    public boolean hologramTextShadow;
    public List<String> hologramLines;
    public String hologramManager;
    public long hologramUpdateTicks;

    // Death Coordinates config
    public boolean deathCoordinatesEnabled;

    public String deathCoordinatesMessage;

    private final int resourceId = 122635;

    @Override
    public void onEnable() {

        if (getServer().getPluginManager().getPlugin("SoulGraves") == null) {
            this.getLogger().warning("SoulGraves not found, disabling SoulGravesPlus");
            this.getServer().getPluginManager().disablePlugin(this);
        }

        if (!getServer().getPluginManager().isPluginEnabled("SoulGraves")) {
            this.getLogger().warning("SoulGraves was found but was not enabled, disabling SoulGravesPlus");
            this.getServer().getPluginManager().disablePlugin(this);
        }

        // Logger Messages

        this.getLogger().info("\u001B[32mEnabled!\u001B[0m \u001B[35mMade by\u001B[0m \u001B[35mJosTheDude\u001B[0m \u001B[35mwith\u001B[0m \u001B[31m<3\u001B[0m \u001B[35mand cookies!\u001B[0m");
        this.getLogger().info("\u001B[33mPlease report any issues here:\u001B[0m \u001B[35m\u001B[4mhttps://github.com/JosTheDude/SoulGravesPlus\u001B[0m");
        this.getLogger().info("\u001B[33mLike my work? Support me here:\u001B[0m \u001B[35m\u001B[4mhttps://ko-fi.com/jossydev\u001B[0m");

        new UpdateChecker(this, resourceId).getVersion(version -> {
            if (this.getPluginMeta().getVersion().equals(version)) {
                getLogger().info("\u001B[32mYou are using the latest version!\u001B[0m");
            } else {
                getLogger().info("\u001B[31mA new version of SoulGravesPlus is available! Download it here: \u001B[0m \u001B[35mhttps://www.spigotmc.org/resources/soulgravesplus.122635/\u001B[0m");
            }
        });

        // Config & Feature Subsets
        saveDefaultConfig();
        updateConfig();
        featureSubsets();

        // Commands
        this.getCommand("soulgravesplus").setExecutor(new ReloadCommand(this));

    }

    public void updateConfig() {
        // Option Features
        this.dateTimeFormatter = new SimpleDateFormat(this.getConfig().getString("options.date-format", "yyyy-MM-dd HH:mm:ss"));
        this.timeFormat = this.getConfig().getString("options.time-format", "{m}m {s}s");
        this.worldNameAliases = new ConcurrentHashMap<>();

        ConfigurationSection aliasesSection = this.getConfig().getConfigurationSection("options.world-name-aliases");
        if (aliasesSection != null) {
            for (String key : aliasesSection.getKeys(false)) {
                String alias = aliasesSection.getString(key);
                if (alias != null) {
                    this.worldNameAliases.put(key, alias);
                } else {
                    this.getLogger().warning("World name alias '" + key + "' is missing a value in config.yml.");
                }
            }
        } else {
            this.getLogger().warning("No world name aliases section found in config.yml.");
        }

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
        this.hologramManager = this.getConfig().getString("hologram.hologram-manager", "FancyHolograms");

        this.hologramXOffset = this.getConfig().getDouble("hologram.x-offset", 0.0);
        this.hologramYOffset = this.getConfig().getDouble("hologram.y-offset", 0.3);
        this.hologramZOffset = this.getConfig().getDouble("hologram.z-offset", 0.0);

        this.hologramBackground = this.getConfig().getBoolean("hologram.custom-background", false);
        this.hologramBackgroundColor = this.getConfig().getString("hologram.background-color", "1,100,255,79").split(",");

        this.hologramTextShadow = this.getConfig().getBoolean("hologram.text-shadow", false);

        this.hologramLines = this.getConfig().getStringList("hologram.lines");
        this.hologramUpdateTicks = this.getConfig().getLong("hologram.update-ticks", 10L);

        this.deathCoordinatesEnabled = this.getConfig().getBoolean("death-coordinates.enabled", true);
        this.deathCoordinatesMessage = this.getConfig().getString("death-coordinates.message", "&c☠ You died at {x} {y} {z}");

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

        if (this.deathCoordinatesEnabled) {
            deathCoordinatesFeatures();
        } else {
            this.getLogger().warning("Death coordinates features are disabled in the config.yml.");
        }

    }

    private void loggerFeatures() {

        this.getServer().getPluginManager().registerEvents(new SoulSpawnLoggerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new SoulPickupLoggerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new SoulExplodeLoggerListener(this), this);

        this.getLogger().info("Logger features enabled.");
    }

    private void hologramFeatures() {
        if (this.hologramManager.equals("FancyHolograms")) {

            if (this.getServer().getPluginManager().getPlugin("FancyHolograms") == null) {
                this.getLogger().warning("FancyHolograms not found! Hologram features disabled. Did you configure the right hologram-manager in the config?");
                return;
            }

            if (!this.getServer().getPluginManager().isPluginEnabled("FancyHolograms")) {
                this.getLogger().warning("FancyHolograms was found but was not enabled! Hologram features disabled. Did you configure the right hologram-manager in the config?");
                return;
            }

            this.hologramManager = "FancyHolograms";

            HologramManager manager = FancyHologramsPlugin.get().getHologramManager();

            this.getServer().getPluginManager().registerEvents(new SoulSpawnFancyHologramListener(this, manager), this);
            this.getServer().getPluginManager().registerEvents(new SoulPickupFancyHologramListener(this, manager), this);
            this.getServer().getPluginManager().registerEvents(new SoulExplodeFancyHologramListener(this, manager), this);
            this.getServer().getPluginManager().registerEvents(new SoulDeleteFancyHologramListener(this, manager), this);
            this.getServer().getScheduler().runTaskTimer(this, new SoulUpdaterFancyHologram(this, manager), this.hologramUpdateTicks, this.hologramUpdateTicks);

            this.getLogger().info("FancyHolograms found! Hologram features enabled.");
        } else if (this.hologramManager.equals("DecentHolograms")) {

            if (this.getServer().getPluginManager().getPlugin("DecentHolograms") == null) {
                this.getLogger().warning("DecentHolograms not found! Hologram features disabled. Did you configure the right hologram-manager in the config?");
                return;
            }

            if (!this.getServer().getPluginManager().isPluginEnabled("DecentHolograms")) {
                this.getLogger().warning("DecentHolograms was found but was not enabled! Hologram features disabled. Did you configure the right hologram-manager in the config?");
                return;
            }

            this.hologramManager = "DecentHolograms";

            this.getServer().getPluginManager().registerEvents(new SoulSpawnDecentHologramListener(this), this);
            this.getServer().getPluginManager().registerEvents(new SoulPickupDecentHologramListener(this), this);
            this.getServer().getPluginManager().registerEvents(new SoulExplodeDecentHologramListener(this), this);
            this.getServer().getPluginManager().registerEvents(new SoulDeleteDecentHologramListener(this), this);
            this.getServer().getScheduler().runTaskTimer(this, new SoulUpdaterDecentHologram(this), this.hologramUpdateTicks, this.hologramUpdateTicks);

            this.getLogger().info("DecentHolograms found! Hologram features enabled.");
        } else {
            this.getLogger().warning("A valid hologram-manager was not configured. Hologram features disabled. Did you configure the right hologram-manager in the config?");
        }
    }

    private void deathCoordinatesFeatures() {
        this.getServer().getPluginManager().registerEvents(new SoulSpawnDeathCoordinatesListener(this), this);
        this.getLogger().info("Death coordinates features enabled.");
    }

    public String formatTime(long time) {
        if (time < 0) time = 0;
        long days = TimeUnit.MILLISECONDS.toDays(time);
        long hours = TimeUnit.MILLISECONDS.toHours(time) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time) % 60;
        long milliseconds = TimeUnit.MILLISECONDS.toMillis(time) % 100;

        return this.timeFormat
                .replace("{d}", String.format("%02d", days))
                .replace("{h}", String.format("%02d", hours))
                .replace("{s}", String.format("%02d", seconds))
                .replace("{m}", String.format("%02d", minutes))
                .replace("{x}", String.format("%02d", milliseconds));
    }

    public List<String> parseHologramLines(Soul soul) {
        long timeLeft;
        OfflinePlayer player = Bukkit.getOfflinePlayer(soul.getOwnerUUID());

        if (getServer().getPluginManager().getPlugin("SoulGraves").getConfig().getBoolean("offline-owner-timer-freeze", false)) {
            if (!player.isOnline() && soul.getFreezeTime() > 0) {
                timeLeft = soul.getTimeLeft() * 1000L;
            } else {
                timeLeft = soul.getExpireTime() - System.currentTimeMillis();
            }
        } else {
            timeLeft = soul.getExpireTime() - System.currentTimeMillis();
        }

        // Define placeholders
        String soulOwner = player.getName();
        String formattedDeathTime = this.dateTimeFormatter.format(new Date(soul.getDeathTime()));
        String formattedExpireTime = this.dateTimeFormatter.format(new Date(soul.getExpireTime()));
        String formattedTimeLeft = this.formatTime(timeLeft);
        String rawDeathTime = String.valueOf(soul.getDeathTime());
        String rawExpireTime = String.valueOf(soul.getExpireTime());
        String rawTimeLeft = String.valueOf(timeLeft);
        String invAmount = String.valueOf(soul.getInventory().size());
        String expAmount = String.valueOf(soul.getXp());

        // Replace placeholders in the config lines
        return this.hologramLines.stream()
                .map(line -> line
                        .replace("{soulOwner}", Objects.requireNonNullElse(soulOwner, "unknown"))
                        .replace("{soulFormattedDeathTime}", formattedDeathTime)
                        .replace("{soulFormattedExpireTime}", formattedExpireTime)
                        .replace("{soulFormattedTimeLeft}", formattedTimeLeft)
                        .replace("{soulRawDeathTime}", rawDeathTime)
                        .replace("{soulRawExpireTime}", rawExpireTime)
                        .replace("{soulRawTimeLeft}", rawTimeLeft)
                        .replace("{soulInventoryAmount}", invAmount)
                        .replace("{soulExperienceAmount}", expAmount)
                        .replace("&", "§"))
                .toList();
    }

    public String getWorldAlias(String worldName) {
        return this.worldNameAliases.getOrDefault(worldName, worldName);
    }

}
