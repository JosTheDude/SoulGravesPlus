package gg.jos.soulgravesplus.events.hologram.fancyholograms;

import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import dev.faultyfunctions.soulgraves.api.event.SoulSpawnEvent;
import gg.jos.soulgravesplus.SoulGravesPlus;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SoulSpawnFancyHologramListener implements Listener {
    private final HologramManager manager;
    private final SoulGravesPlus soulGravesPlus;

    public SoulSpawnFancyHologramListener(SoulGravesPlus soulGravesPlus, HologramManager manager) {
        this.soulGravesPlus = soulGravesPlus;
        this.manager = manager;
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

        TextHologramData hologramData = new TextHologramData(hologramName, location);
        // Create the hologram
        hologramData.setText(this.soulGravesPlus.parseHologramLines(event.getSoul()));
        hologramData.setPersistent(false);

        // Hologram background color
        if (this.soulGravesPlus.hologramBackground) {
            int a = Integer.parseInt(this.soulGravesPlus.hologramBackgroundColor[0]);
            int r = Integer.parseInt(this.soulGravesPlus.hologramBackgroundColor[1]);
            int g = Integer.parseInt(this.soulGravesPlus.hologramBackgroundColor[2]);
            int b = Integer.parseInt(this.soulGravesPlus.hologramBackgroundColor[3]);

            hologramData.setBackground(Color.fromARGB(a, r, g, b));
        }

        Hologram hologram = manager.create(hologramData);
        manager.addHologram(hologram);
    }
}
