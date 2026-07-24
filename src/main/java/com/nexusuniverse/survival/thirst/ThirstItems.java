package com.nexusuniverse.survival.thirst;

import com.nexusuniverse.survival.NexusSurvivalPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;

public class ThirstItems {

    private final NamespacedKey waterBottleKey;

    public ThirstItems(NexusSurvivalPlugin plugin) {
        this.waterBottleKey = new NamespacedKey(plugin, "water_bottle");
    }

    public ItemStack createWaterBottle() {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.setBasePotionType(PotionType.WATER);
        meta.setDisplayName("§bWater Bottle");
        meta.setLore(java.util.List.of("§7Right-click to drink and restore thirst."));
        meta.getPersistentDataContainer().set(waterBottleKey, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isWaterBottle(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        Boolean tag = item.getItemMeta().getPersistentDataContainer().get(waterBottleKey, PersistentDataType.BOOLEAN);
        return Boolean.TRUE.equals(tag);
    }
}
