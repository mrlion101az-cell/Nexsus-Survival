package com.nexusuniverse.survival.data;

import com.nexusuniverse.survival.disease.Disease;
import org.bukkit.boss.BossBar;

/**
 * All per-player state for the four survival systems, kept in one place
 * so managers don't each need their own map + join/quit bookkeeping.
 */
public class SurvivalPlayerData {

    // thirst: 0-180
    public double thirst = 180.0;
    public int thirstTickCounter = 0;

    // radiation "rad-oxygen": 0-20, drains in radiation zones, regens outside
    public double radOxygen = 20.0;
    public int radTickCounter = 0;

    // hygiene: 0 (clean) - 100 (filthy)
    public double dirtiness = 0.0;
    public int hygieneTickCounter = 0;

    // disease: null if healthy
    public Disease infection = null;
    public int symptomTickCounter = 0;

    // UI
    public final BossBar thirstBar;
    public final BossBar radiationBar;

    public SurvivalPlayerData(BossBar thirstBar, BossBar radiationBar) {
        this.thirstBar = thirstBar;
        this.radiationBar = radiationBar;
    }
}
