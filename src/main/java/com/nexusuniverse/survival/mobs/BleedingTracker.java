package com.nexusuniverse.survival.mobs;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/** A wounded mob keeps dripping blood for a while after being hit, not just a one-off burst. */
public class BleedingTracker {

    private static final long BLEED_DURATION_TICKS = 20L * 8; // 8 seconds of dripping

    private final Map<UUID, Long> bleedingUntil = new HashMap<>();
    private long currentTick = 0;

    public void startBleeding(LivingEntity entity) {
        bleedingUntil.put(entity.getUniqueId(), currentTick + BLEED_DURATION_TICKS);
    }

    /** Called once per second from the central tick loop. */
    public void tick(Server server) {
        currentTick += 20;

        Iterator<Map.Entry<UUID, Long>> it = bleedingUntil.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Long> entry = it.next();
            if (entry.getValue() <= currentTick) {
                it.remove();
                continue;
            }

            if (!(server.getEntity(entry.getKey()) instanceof LivingEntity living) || living.isDead() || !living.isValid()) {
                it.remove();
                continue;
            }

            living.getWorld().spawnParticle(Particle.DUST, living.getLocation().add(0, living.getHeight() / 2, 0),
                    4, 0.25, 0.25, 0.25, new Particle.DustOptions(Color.RED, 1.0f));
        }
    }
}
