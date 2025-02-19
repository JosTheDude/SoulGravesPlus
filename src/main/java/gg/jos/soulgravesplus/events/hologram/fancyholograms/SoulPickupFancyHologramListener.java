package gg.jos.soulgravesplus.events.hologram.fancyholograms;

import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.hologram.Hologram;
import dev.faultyfunctions.soulgraves.api.event.SoulPickupEvent;
import gg.jos.soulgravesplus.SoulGravesPlus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class SoulPickupFancyHologramListener implements Listener {
    private final SoulGravesPlus soulGravesPlus;
    private final HologramManager manager;

    public SoulPickupFancyHologramListener(SoulGravesPlus soulGravesPlus, HologramManager manager) {
        this.soulGravesPlus = soulGravesPlus;
        this.manager = manager;
    }

    @EventHandler
    public void onSoulPickup(SoulPickupEvent event) {

        String hologramName = "grave_hologram_" + event.getSoul().getMarkerUUID();

        Hologram hologram = this.manager.getHologram(hologramName).orElse(null);

        if (hologram == null)
            return;

        this.manager.removeHologram(hologram);
    }
}
