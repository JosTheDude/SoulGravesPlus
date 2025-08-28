package gg.jos.soulgravesplus.events.logger;

import dev.faultyfunctions.soulgraves.api.event.SoulPickupEvent;
import gg.jos.soulgravesplus.SoulGravesPlus;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class SoulPickupLoggerListener implements Listener {
    private final SoulGravesPlus soulGravesPlus;

    public SoulPickupLoggerListener(SoulGravesPlus soulGravesPlus) {
        this.soulGravesPlus = soulGravesPlus;
    }

    @EventHandler
    public void onSoulPickup(SoulPickupEvent event) {

        if (!this.soulGravesPlus.loggerEnabled) {
            return;
        }

        if (!this.soulGravesPlus.logSoulPickups) {
            return;
        }

        // Get the grave location and adjust it to be above the grave
        Location soulLocation = event.getSoulLocation();
        Player player = event.getPlayer();
        String soulOwner = player.getName();
        String worldName = soulGravesPlus.getWorldAlias(soulLocation.getWorld().getName());

        this.soulGravesPlus.getLogger().info(this.soulGravesPlus.logSoulPickupsMessage
                .replace("{soulOwner}", soulOwner)
                .replace("{x}", String.valueOf(soulLocation.getBlockX()))
                .replace("{y}", String.valueOf(soulLocation.getBlockY()))
                .replace("{z}", String.valueOf(soulLocation.getBlockZ()))
                .replace("{world}", worldName)
        );

    }
}
