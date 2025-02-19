package gg.jos.soulgravesplus.events.hologram.decentholograms;

import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import dev.faultyfunctions.soulgraves.api.event.SoulSpawnEvent;
import eu.decentsoftware.holograms.api.DHAPI;
import gg.jos.soulgravesplus.SoulGravesPlus;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SoulSpawnDecentHologramListener implements Listener {
    private final SoulGravesPlus soulGravesPlus;

    public SoulSpawnDecentHologramListener(SoulGravesPlus soulGravesPlus) {
        this.soulGravesPlus = soulGravesPlus;
    }

    @EventHandler
    public void onSoulSpawn(SoulSpawnEvent event) {

        if (!this.soulGravesPlus.hologramEnabled)
            return;

        // Get the grave location and adjust it to be above the grave
        Location soulLocation = event.getSoulLocation();
        Location location = soulLocation.clone().add(
                this.soulGravesPlus.hologramXOffset,
                this.soulGravesPlus.hologramYOffset,
                this.soulGravesPlus.hologramZOffset
        );

        // Fetch the hologram config
        if (this.soulGravesPlus.hologramLines.isEmpty()) {
            this.soulGravesPlus.getLogger().warning("Hologram lines are missing in the config!");
            return;
        }

        // Create a unique hologram name
        String hologramName = "grave_hologram_" + event.getSoul().getMarkerUUID();

        DHAPI.createHologram(hologramName, location, this.soulGravesPlus.parseHologramLines(event.getSoul()));

    }
}
