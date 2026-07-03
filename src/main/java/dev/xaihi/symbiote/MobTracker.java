package dev.xaihi.symbiote;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import java.util.List;

public class MobTracker {
    private static boolean enabled = false;
    private static final double SEARCH_RADIUS = 16.0;
    private static int cooldown = 0;
    private Entity _currentENT;

    public static void toggle() {
        enabled = !enabled;
        Minecraft.getInstance().player.sendSystemMessage(
            Component.literal("Mob tracking " + (enabled ? "§aENABLED" : "§cDISABLED"))
        );
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void tick() {
        if (!enabled) return;
        if (cooldown-- > 0) return;
        cooldown = 22;
        
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) return;
        
        Player player = client.player;
        
        // Create bounding box
        AABB box = AABB.ofSize(player.position(), SEARCH_RADIUS * 2, SEARCH_RADIUS * 2, SEARCH_RADIUS * 2);
        
        // Get all entities in the box using the public Level.getEntities() method
        // This method is public and takes: (excludedEntity, boundingBox, predicate)
        List<Entity> entities = client.level.getEntities(player, box, entity -> 
            entity instanceof LivingEntity mob && !(mob instanceof Player)
        );
        
        // Find the nearest mob
        LivingEntity nearest = null;
        double nearestDistSq = Double.MAX_VALUE;
        
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity mob) {
                double distSq = mob.distanceToSqr(player);
                if (distSq < nearestDistSq) {
                    nearestDistSq = distSq;
                    nearest = mob;
                }
            }
        }
        
        // Send result
        if (nearest != null) {
            double distance = Math.sqrt(nearestDistSq);
            String message = String.format("§6Nearest: §e%s §7- §f%.1f §7blocks away", 
                nearest.getName().getString(), distance);
            player.sendSystemMessage(Component.literal(message));
            // player.setHealth(nearest.getHealth());
            player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(nearest.getMaxHealth());
        }
    }
}