package com.nexusuniverse.survival;

import com.nexusuniverse.survival.data.PlayerDataManager;
import com.nexusuniverse.survival.disease.DiseaseItems;
import com.nexusuniverse.survival.disease.DiseaseListener;
import com.nexusuniverse.survival.disease.DiseaseManager;
import com.nexusuniverse.survival.hygiene.HygieneListener;
import com.nexusuniverse.survival.hygiene.HygieneManager;
import com.nexusuniverse.survival.radiation.RadiationItems;
import com.nexusuniverse.survival.radiation.RadiationListener;
import com.nexusuniverse.survival.radiation.RadiationManager;
import com.nexusuniverse.survival.thirst.ThirstItems;
import com.nexusuniverse.survival.thirst.ThirstListener;
import com.nexusuniverse.survival.thirst.ThirstManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class NexusSurvivalPlugin extends JavaPlugin {

    private PlayerDataManager playerDataManager;

    private ThirstItems thirstItems;
    private ThirstManager thirstManager;

    private RadiationItems radiationItems;
    private RadiationManager radiationManager;

    private HygieneManager hygieneManager;

    private DiseaseItems diseaseItems;
    private DiseaseManager diseaseManager;

    @Override
    public void onEnable() {
        this.playerDataManager = new PlayerDataManager();

        this.thirstItems = new ThirstItems(this);
        this.thirstManager = new ThirstManager(playerDataManager);

        this.radiationItems = new RadiationItems(this);
        this.radiationManager = new RadiationManager(playerDataManager, radiationItems);

        this.diseaseItems = new DiseaseItems(this);
        this.diseaseManager = new DiseaseManager(playerDataManager);

        this.hygieneManager = new HygieneManager(playerDataManager, diseaseManager);

        getCommand("nexussurvival").setExecutor(new NexusSurvivalCommand(this));

        getServer().getPluginManager().registerEvents(new ThirstListener(this), this);
        getServer().getPluginManager().registerEvents(new RadiationListener(this), this);
        getServer().getPluginManager().registerEvents(new HygieneListener(this), this);
        getServer().getPluginManager().registerEvents(new DiseaseListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerLifecycleListener(this), this);

        // central tick loop: once per second (20 ticks) for all four systems
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                thirstManager.tick(player);
                radiationManager.tick(player);
                hygieneManager.tick(player);
                diseaseManager.tick(player);
            }
        }, 20L, 20L);

        getLogger().info("NexusSurvival enabled -- thirst, radiation zones, hygiene, and disease are live.");
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) {
            playerDataManager.clearAll();
        }
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public ThirstItems getThirstItems() {
        return thirstItems;
    }

    public ThirstManager getThirstManager() {
        return thirstManager;
    }

    public RadiationItems getRadiationItems() {
        return radiationItems;
    }

    public RadiationManager getRadiationManager() {
        return radiationManager;
    }

    public HygieneManager getHygieneManager() {
        return hygieneManager;
    }

    public DiseaseItems getDiseaseItems() {
        return diseaseItems;
    }

    public DiseaseManager getDiseaseManager() {
        return diseaseManager;
    }
}
