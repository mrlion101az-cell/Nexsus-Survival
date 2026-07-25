package com.nexusuniverse.survival.disease;

import com.nexusuniverse.survival.data.PlayerDataManager;
import com.nexusuniverse.survival.data.SurvivalPlayerData;
import org.bukkit.entity.Player;

import java.util.Random;

public class DiseaseManager {

    // how often (in ticks of the central 20-tick loop, i.e. seconds) symptoms re-apply
    private static final int SYMPTOM_INTERVAL_SECONDS = 5;

    private final PlayerDataManager playerData;
    private final Random random = new Random();

    public DiseaseManager(PlayerDataManager playerData) {
        this.playerData = playerData;
    }

    public void infect(Player player, Disease disease) {
        SurvivalPlayerData d = playerData.get(player);
        if (d.infection != null) return; // already sick -- one disease at a time
        d.infection = disease;
        d.symptomTickCounter = 0;
        player.sendMessage("§4You feel unwell... §c[" + disease.getDisplayName() + "]");
        player.sendMessage(disease.getDescription());
    }

    public void infectRandom(Player player) {
        Disease[] all = Disease.values();
        infect(player, all[random.nextInt(all.length)]);
    }

    public boolean cure(Player player, Disease disease) {
        SurvivalPlayerData d = playerData.get(player);
        if (d.infection != disease) return false;
        d.infection = null;
        player.sendMessage("§aYou feel the " + disease.getDisplayName() + " lift. You're cured.");
        return true;
    }

    public Disease getInfection(Player player) {
        return playerData.get(player).infection;
    }

    /** Called every second from the central tick loop. */
    public void tick(Player player) {
        SurvivalPlayerData d = playerData.get(player);
        if (d.infection == null) return;

        d.symptomTickCounter++;
        if (d.symptomTickCounter < SYMPTOM_INTERVAL_SECONDS) return;
        d.symptomTickCounter = 0;

        for (var effect : d.infection.getSymptoms()) {
            player.addPotionEffect(effect);
        }
    }
}
