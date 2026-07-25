package com.nexusuniverse.survival;

import com.nexusuniverse.survival.disease.Disease;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class NexusSurvivalCommand implements CommandExecutor {

    private final NexusSurvivalPlugin plugin;

    public NexusSurvivalCommand(NexusSurvivalPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§7Usage: /nexussurvival <give|radiation|status|resetme|removeall>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "give" -> handleGive(player, args);
            case "radiation" -> handleRadiation(player, args);
            case "status" -> handleStatus(player);
            case "resetme" -> handleResetMe(player);
            case "removeall" -> handleRemoveAll(player);
            default -> player.sendMessage("§cUnknown subcommand.");
        }
        return true;
    }

    private void handleGive(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /nexussurvival give <waterbottle|rawwater|gasmask|wand|cure> [disease]");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "waterbottle" -> {
                player.getInventory().addItem(plugin.getThirstItems().createWaterBottle());
                player.sendMessage("§aGiven a Water Bottle.");
            }
            case "rawwater" -> {
                player.getInventory().addItem(plugin.getThirstItems().createRawWater());
                player.sendMessage("§aGiven Raw Water -- boil it in a furnace to purify it.");
            }
            case "gasmask" -> {
                player.getInventory().addItem(plugin.getRadiationItems().createGasMask());
                player.sendMessage("§aGiven a Hazmat Mask.");
            }
            case "wand" -> {
                player.getInventory().addItem(plugin.getRadiationItems().createWand());
                player.sendMessage("§aGiven a Radiation Zone Wand.");
            }
            case "cure" -> {
                if (args.length < 3) {
                    player.sendMessage("§cUsage: /nexussurvival give cure <disease>. Options: "
                            + diseaseNames());
                    return;
                }
                Disease disease = parseDisease(args[2]);
                if (disease == null) {
                    player.sendMessage("§cUnknown disease. Options: " + diseaseNames());
                    return;
                }
                player.getInventory().addItem(plugin.getDiseaseItems().createCure(disease));
                player.sendMessage("§aGiven a cure for " + disease.getDisplayName() + ".");
            }
            default -> player.sendMessage("§cUnknown item. Options: waterbottle, rawwater, gasmask, wand, cure");
        }
    }

    private void handleRadiation(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /nexussurvival radiation <wand|create <name>|remove <name>|list>");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "wand" -> {
                player.getInventory().addItem(plugin.getRadiationItems().createWand());
                player.sendMessage("§aGiven a Radiation Zone Wand.");
            }
            case "create" -> {
                if (args.length < 3) {
                    player.sendMessage("§cUsage: /nexussurvival radiation create <name>");
                    return;
                }
                plugin.getRadiationManager().createZone(player, args[2]);
            }
            case "remove" -> {
                if (args.length < 3) {
                    player.sendMessage("§cUsage: /nexussurvival radiation remove <name>");
                    return;
                }
                boolean removed = plugin.getRadiationManager().removeZone(args[2]);
                player.sendMessage(removed ? "§aZone removed." : "§cNo zone with that name.");
            }
            case "list" -> {
                var names = plugin.getRadiationManager().listZoneNames();
                player.sendMessage(names.isEmpty() ? "§7No radiation zones defined." : "§7Zones: §f" + names);
            }
            default -> player.sendMessage("§cUnknown radiation subcommand.");
        }
    }

    private void handleStatus(Player player) {
        var data = plugin.getPlayerDataManager().get(player);
        player.sendMessage("§7--- Survival Status ---");
        player.sendMessage("§bThirst: §f" + (int) data.thirst + "/20");
        player.sendMessage("§aRad-O2: §f" + (int) data.radOxygen + "/20");
        player.sendMessage("§eDirtiness: §f" + (int) data.dirtiness + "/100");
        player.sendMessage(data.infection == null
                ? "§aHealthy -- no infection."
                : "§4Infected: §c" + data.infection.getDisplayName());
    }

    private void handleResetMe(Player player) {
        var data = plugin.getPlayerDataManager().get(player);
        data.thirst = 20;
        data.radOxygen = 20;
        data.dirtiness = 0;
        data.infection = null;
        player.sendMessage("§aYour survival stats have been reset.");
    }

    private void handleRemoveAll(Player player) {
        plugin.getPlayerDataManager().clearAll();
        player.sendMessage("§aCleared all tracked survival state. It will regenerate as players are touched again.");
    }

    private Disease parseDisease(String raw) {
        try {
            return Disease.valueOf(raw.toUpperCase().replace(' ', '_'));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String diseaseNames() {
        return Arrays.stream(Disease.values()).map(Enum::name).reduce((a, b) -> a + ", " + b).orElse("");
    }
}
