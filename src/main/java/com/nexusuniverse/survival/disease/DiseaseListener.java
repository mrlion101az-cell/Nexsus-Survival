package com.nexusuniverse.survival.disease;

import com.nexusuniverse.survival.NexusSurvivalPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class DiseaseListener implements Listener {

    private final NexusSurvivalPlugin plugin;

    public DiseaseListener(NexusSurvivalPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Disease cureType = plugin.getDiseaseItems().readCureType(event.getItem());
        if (cureType == null) return;

        Player player = event.getPlayer();
        boolean cured = plugin.getDiseaseManager().cure(player, cureType);
        if (!cured) {
            player.sendMessage("§7That cure doesn't match what you have. Nothing happens.");
        }
    }
}
