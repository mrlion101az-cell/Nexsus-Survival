package com.nexusuniverse.survival.disease;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Each disease has a display name, a short flavor description, and a set
 * of symptom effects re-applied periodically while infected. Cures are
 * matched to these by name in DiseaseItems / DiseaseManager (one dedicated
 * cure item per disease, no cross-curing).
 */
public enum Disease {

    RATTLING_COUGH(
            "Rattling Cough",
            "§7A wet, rattling cough. Mild but persistent.",
            List.of(
                    new PotionEffect(PotionEffectType.NAUSEA, 100, 0, true, false),
                    new PotionEffect(PotionEffectType.WEAKNESS, 100, 0, true, false)
            )
    ),
    FEVER_ROT(
            "Fever Rot",
            "§7Burns through food reserves and slows the hands.",
            List.of(
                    new PotionEffect(PotionEffectType.HUNGER, 100, 1, true, false),
                    new PotionEffect(PotionEffectType.MINING_FATIGUE, 100, 0, true, false)
            )
    ),
    GLOWSICKNESS(
            "Glowsickness",
            "§7Radiation exposure gone wrong -- you glow, and everyone can see it.",
            List.of(
                    new PotionEffect(PotionEffectType.GLOWING, 100, 0, true, false),
                    new PotionEffect(PotionEffectType.WEAKNESS, 100, 1, true, false)
            )
    ),
    BONE_CHILL(
            "Bone Chill",
            "§7A deep cold that won't leave. Slows and disorients.",
            List.of(
                    new PotionEffect(PotionEffectType.SLOWNESS, 100, 1, true, false),
                    new PotionEffect(PotionEffectType.BLINDNESS, 60, 0, true, false)
            )
    );

    private final String displayName;
    private final String description;
    private final List<PotionEffect> symptoms;

    Disease(String displayName, String description, List<PotionEffect> symptoms) {
        this.displayName = displayName;
        this.description = description;
        this.symptoms = symptoms;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public List<PotionEffect> getSymptoms() {
        return symptoms;
    }
}
