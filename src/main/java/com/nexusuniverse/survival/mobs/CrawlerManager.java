package com.nexusuniverse.survival.mobs;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A mob shot in the legs loses normal movement: its AI is switched off (so
 * vanilla pathfinding/jumping stops entirely) and it's forced into a prone
 * pose for the visual. Instead of walking, it's manually nudged toward the
 * nearest player each tick.
 *
 * PERSISTENCE NOTE: the crawler tag itself lives in PDC (survives a
 * restart, same as any other NBT data), but the "actively drive this one
 * toward a player" tracking is in-memory only. Without re-detecting them,
 * a crawler from a previous session would sit frozen forever after a
 * restart -- harmless, but a stuck, undying mob is a bad thing to leave
 * scattered around a live world. This class now implements Listener and
 * re-adds any tagged crawler to the active set as its chunk loads, plus
 * scanLoadedChunks() catches everything already loaded at startup.
 *
 * HONEST LIMITATION: movement itself is still a straight-line vector nudge,
 * not real pathfinding -- Bukkit's public API doesn't expose the game's own
 * goal selector/navigation system. A crawler beelines for you and can get
 * stuck on a step, a fence, a one-block gap -- it won't route around
 * obstacles the way a normal mob does.
 */
public class CrawlerManager implements Listener {

    private static final double CRAWL_SPEED = 0.04;
    private static final double DETECTION_RADIUS = 16.0;

    private final NamespacedKey crawlerKey;
    private final Set<UUID> crawlers = new HashSet<>();

    public CrawlerManager(Plugin plugin) {
        this.crawlerKey = new NamespacedKey(plugin, "crawler");
    }

    public void makeCrawler(LivingEntity entity) {
        if (!crawlers.add(entity.getUniqueId())) return; // already crawling
        entity.getPersistentDataContainer().set(crawlerKey, PersistentDataType.BYTE, (byte) 1);
        entity.setAI(false);
        entity.setPose(Pose.SWIMMING, true);

        var speed = entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (speed != null) speed.setBaseValue(0.0); // AI is off anyway; keeps state consistent if it's ever restored
    }

    public boolean isCrawler(LivingEntity entity) {
        Byte tag = entity.getPersistentDataContainer().get(crawlerKey, PersistentDataType.BYTE);
        return tag != null && tag == 1;
    }

    /** Call once at startup: catches every already-tagged crawler in every already-loaded chunk. */
    public void scanLoadedChunks(Iterable<? extends World> worlds) {
        for (World world : worlds) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof LivingEntity living && isCrawler(living)) {
                    crawlers.add(living.getUniqueId());
                }
            }
        }
    }

    /** Catches tagged crawlers in chunks that load after startup (e.g. a player walking back into an old area). */
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof LivingEntity living && isCrawler(living)) {
                crawlers.add(living.getUniqueId());
            }
        }
    }

    /** Called once per second from the central tick loop: manually drags every known crawler toward its nearest player. */
    public void tickAll(Server server) {
        crawlers.removeIf(id -> {
            Entity entity = server.getEntity(id);
            return !(entity instanceof LivingEntity living) || living.isDead() || !living.isValid();
        });

        for (UUID id : crawlers) {
            if (!(server.getEntity(id) instanceof LivingEntity entity)) continue;
            Player nearest = findNearestPlayer(entity);
            if (nearest == null) continue;

            Vector toTarget = nearest.getLocation().toVector().subtract(entity.getLocation().toVector());
            if (toTarget.lengthSquared() < 0.01) continue;

            Vector step = toTarget.clone().normalize().multiply(CRAWL_SPEED);
            step.setY(0);

            Location moved = entity.getLocation().add(step);
            moved.setDirection(toTarget);
            entity.teleport(moved);
        }
    }

    private Player findNearestPlayer(LivingEntity entity) {
        Player nearest = null;
        double closestSq = DETECTION_RADIUS * DETECTION_RADIUS;

        for (Entity nearby : entity.getNearbyEntities(DETECTION_RADIUS, DETECTION_RADIUS, DETECTION_RADIUS)) {
            if (!(nearby instanceof Player player)) continue;
            double distSq = player.getLocation().distanceSquared(entity.getLocation());
            if (distSq < closestSq) {
                closestSq = distSq;
                nearest = player;
            }
        }
        return nearest;
    }
}

