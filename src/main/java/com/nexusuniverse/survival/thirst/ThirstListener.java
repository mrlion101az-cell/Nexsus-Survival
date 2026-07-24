package com.nexusuniverse.survival.thirst;

import com.nexusuniverse.survival.NexusSurvivalPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class ThirstListener implements Listener {

    private final NexusSurvivalPlugin plugin;

    public ThirstListener(NexusSurvivalPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (!plugin.getThirstItems().isWaterBottle(event.getItem())) return;
        Player player = event.getPlayer();
        // vanilla already converts the potion to a glass bottle in hand -- we just add the effect
        plugin.getThirstManager().drink(player);
    }
}
