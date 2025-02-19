package gg.jos.soulgravesplus.events.hologram.decentholograms;

import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.hologram.Hologram;
import dev.faultyfunctions.soulgraves.api.event.SoulPickupEvent;
import eu.decentsoftware.holograms.api.DHAPI;
import gg.jos.soulgravesplus.SoulGravesPlus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class SoulPickupDecentHologramListener implements Listener {
    private final SoulGravesPlus soulGravesPlus;

    public SoulPickupDecentHologramListener(SoulGravesPlus soulGravesPlus) {
        this.soulGravesPlus = soulGravesPlus;
    }

    @EventHandler
    public void onSoulPickup(SoulPickupEvent event) {

        String hologramName = "grave_hologram_" + event.getSoul().getMarkerUUID();

        DHAPI.removeHologram(hologramName);

    }
}
