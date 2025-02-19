package gg.jos.soulgravesplus.events.logger;

import dev.faultyfunctions.soulgraves.api.event.SoulExplodeEvent;
import gg.jos.soulgravesplus.SoulGravesPlus;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class SoulExplodeLoggerListener implements Listener {
    private final SoulGravesPlus soulGravesPlus;

    public SoulExplodeLoggerListener(SoulGravesPlus soulGravesPlus) {
        this.soulGravesPlus = soulGravesPlus;
    }

    @EventHandler
    public void onSoulExplode(SoulExplodeEvent event) {

        if (!soulGravesPlus.loggerEnabled) {
            return;
        }

        if (!soulGravesPlus.logSoulExplosions) {
            return;
        }

        // Get the grave location and adjust it to be above the grave
        Location soulLocation = event.getSoulLocation();
        OfflinePlayer player = event.getOwner().getPlayer();
        String soulOwner = player != null ? player.getName() : "unknown";

        soulGravesPlus.getLogger().info(soulGravesPlus.logSoulExplosionsMessage
                .replace("{soulOwner}", soulOwner)
                .replace("{x}", String.valueOf(soulLocation.getBlockX()))
                .replace("{y}", String.valueOf(soulLocation.getBlockY()))
                .replace("{z}", String.valueOf(soulLocation.getBlockZ()))
        );

    }
}
