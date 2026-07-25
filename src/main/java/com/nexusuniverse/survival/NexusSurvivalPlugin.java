package com.nexusuniverse.survival;

import com.nexusuniverse.survival.config.NexusSurvivalConfig;
import com.nexusuniverse.survival.data.PlayerDataManager;
import com.nexusuniverse.survival.disease.DiseaseItems;
import com.nexusuniverse.survival.disease.DiseaseListener;
import com.nexusuniverse.survival.disease.DiseaseManager;
import com.nexusuniverse.survival.disease.DiseaseSourceListener;
import com.nexusuniverse.survival.hygiene.HygieneListener;
import com.nexusuniverse.survival.hygiene.HygieneManager;
import com.nexusuniverse.survival.mobs.BleedingTracker;
import com.nexusuniverse.survival.mobs.ContagiousMobManager;
import com.nexusuniverse.survival.mobs.CrawlerManager;
import com.nexusuniverse.survival.mobs.FeralZombieManager;
import com.nexusuniverse.survival.mobs.LimbShootingListener;
import com.nexusuniverse.survival.mobs.TntZombieManager;
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

    private NexusSurvivalConfig config;
    private PlayerDataManager playerDataManager;

    private ThirstItems thirstItems;
    private ThirstManager thirstManager;

    private RadiationItems radiationItems;
    private RadiationManager radiationManager;

    private HygieneManager hygieneManager;

    private DiseaseItems diseaseItems;
    private DiseaseManager diseaseManager;

    private FeralZombieManager feralZombieManager;
    private TntZombieManager tntZombieManager;
    private CrawlerManager crawlerManager;
    private BleedingTracker bleedingTracker;
    private ContagiousMobManager contagiousMobManager;

    @Override
    public void onEnable() {
        this.config = new NexusSurvivalConfig(this);
        this.playerDataManager = new PlayerDataManager();

        this.thirstItems = new ThirstItems(this);
        this.thirstManager = new ThirstManager(playerDataManager);
        com.nexusuniverse.survival.thirst.WaterPurificationRecipe.register(this, thirstItems);

        this.radiationItems = new RadiationItems(this);
        this.radiationManager = new RadiationManager(playerDataManager, radiationItems);

        this.diseaseItems = new DiseaseItems(this);
        this.diseaseManager = new DiseaseManager(playerDataManager, config);
        com.nexusuniverse.survival.disease.DiseaseCureRecipes.registerAll(this, diseaseItems, thirstItems);

        this.hygieneManager = new HygieneManager(playerDataManager, diseaseManager);

        this.feralZombieManager = new FeralZombieManager(this, config);
        this.tntZombieManager = new TntZombieManager(this, config);
        this.crawlerManager = new CrawlerManager(this);
        this.bleedingTracker = new BleedingTracker();
        this.contagiousMobManager = new ContagiousMobManager(this, diseaseManager, config);

        getCommand("nexussurvival").setExecutor(new NexusSurvivalCommand(this));

        getServer().getPluginManager().registerEvents(new ThirstListener(this), this);
        getServer().getPluginManager().registerEvents(new RadiationListener(this), this);
        getServer().getPluginManager().registerEvents(new HygieneListener(this), this);
        getServer().getPluginManager().registerEvents(new DiseaseListener(this), this);
        getServer().getPluginManager().registerEvents(new DiseaseSourceListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerLifecycleListener(this), this);
        getServer().getPluginManager().registerEvents(feralZombieManager, this);
        getServer().getPluginManager().registerEvents(tntZombieManager, this);
        getServer().getPluginManager().registerEvents(new LimbShootingListener(bleedingTracker, crawlerManager), this);
        getServer().getPluginManager().registerEvents(contagiousMobManager, this);
        getServer().getPluginManager().registerEvents(crawlerManager, this);

        // Catches any crawlers left over from a previous session in chunks
        // that are already loaded at startup (onChunkLoad only covers
        // chunks that load AFTER this point).
        crawlerManager.scanLoadedChunks(Bukkit.getWorlds());

        // central tick loop: once per second (20 ticks) for all systems
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                thirstManager.tick(player);
                radiationManager.tick(player);
                hygieneManager.tick(player);
                diseaseManager.tick(player);
            }
            diseaseManager.tickGlobal(Bukkit.getOnlinePlayers());
            tntZombieManager.tickAll(Bukkit.getOnlinePlayers());
            crawlerManager.tickAll(getServer());
            bleedingTracker.tick(getServer());
            contagiousMobManager.tickAll(Bukkit.getOnlinePlayers());
        }, 20L, 20L);

        getLogger().info("NexusSurvival enabled -- thirst, radiation zones, hygiene, disease, and hostile mob overhauls are live.");
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) {
            playerDataManager.clearAll();
        }
    }

    public NexusSurvivalConfig getNexusSurvivalConfig() {
        return config;
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

    public FeralZombieManager getFeralZombieManager() {
        return feralZombieManager;
    }

    public TntZombieManager getTntZombieManager() {
        return tntZombieManager;
    }

    public ContagiousMobManager getContagiousMobManager() {
        return contagiousMobManager;
    }
}
