package gg.jos.soulgravesplus.events.logger;

import dev.faultyfunctions.soulgraves.api.event.SoulSpawnEvent;
import gg.jos.soulgravesplus.SoulGravesPlus;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class SoulSpawnLoggerListener implements Listener {
    private final SoulGravesPlus soulGravesPlus;

    public SoulSpawnLoggerListener(SoulGravesPlus soulGravesPlus) {
        this.soulGravesPlus = soulGravesPlus;
    }

    @EventHandler
    public void onSoulSpawn(SoulSpawnEvent event) {

        if (!this.soulGravesPlus.loggerEnabled) {
            return;
        }

        if (!this.soulGravesPlus.logSoulSpawns) {
            return;
        }

        // Get the grave location and adjust it to be above the grave
        Location soulLocation = event.getSoulLocation();
        Player player = event.getPlayer();
        String soulOwner = player.getName();

        this.soulGravesPlus.getLogger().info(this.soulGravesPlus.logSoulSpawnsMessage
                .replace("{soulOwner}", soulOwner)
                .replace("{x}", String.valueOf(soulLocation.getBlockX()))
                .replace("{y}", String.valueOf(soulLocation.getBlockY()))
                .replace("{z}", String.valueOf(soulLocation.getBlockZ()))
        );

    }
}
