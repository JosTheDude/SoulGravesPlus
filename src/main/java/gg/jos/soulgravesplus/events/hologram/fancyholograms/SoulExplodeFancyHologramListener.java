package gg.jos.soulgravesplus.events.hologram.fancyholograms;

import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.hologram.Hologram;
import dev.faultyfunctions.soulgraves.api.event.SoulExplodeEvent;
import gg.jos.soulgravesplus.SoulGravesPlus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class SoulExplodeFancyHologramListener implements Listener {
    private final SoulGravesPlus soulGravesPlus;
    private final HologramManager manager;

    public SoulExplodeFancyHologramListener(SoulGravesPlus soulGravesPlus, HologramManager manager) {
        this.soulGravesPlus = soulGravesPlus;
        this.manager = manager;
    }

    @EventHandler
    public void onSoulExplode(SoulExplodeEvent event) {

        String hologramName = "grave_hologram_" + event.getSoul().getMarkerUUID();

        Hologram hologram = this.manager.getHologram(hologramName).orElse(null);

        if (hologram == null)
            return;

        this.manager.removeHologram(hologram);
    }
}
