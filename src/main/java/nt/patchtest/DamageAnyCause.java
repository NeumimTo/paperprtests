package nt.patchtest;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Plugin(name = "testd", version = "2")
public class DamageAnyCause extends JavaPlugin implements Listener {

    private static Map<UUID, DamageCause> damageMap = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onlmb(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            World world = e.getPlayer().getWorld();
            Location l = e.getPlayer().getLocation().add(0, 10, 0);

            for (EntityType value : EntityType.values()) {
                if (value == EntityType.UNKNOWN
                        || value.getEntityClass() != null
                        && (!(Damageable.class.isAssignableFrom(value.getEntityClass()))
                        || (Player.class.isAssignableFrom(value.getEntityClass())))

                ) {
                    continue;
                }
                System.out.println("Testing " + value);
                Entity entity = world.spawnEntity(l, value);
                if (entity instanceof Damageable) {
                    System.out.println(entity.getClass().getSimpleName() + ".damage(1, player)");
                    ((Damageable) entity).damage(1, e.getPlayer());
                    System.out.println(entity.getClass().getSimpleName() + ".damage(100, player)");
                    ((Damageable) entity).damage(1000, e.getPlayer());
                } else {
                    entity.remove();
                }
            }

            for (DamageCause v : DamageCause.values()) {
                Damageable entity = (Damageable) world.spawnEntity(l, EntityType.CHICKEN);
                damageMap.put(entity.getUniqueId(), v);
                System.out.println("entity.damage(100, " + v + " player)");
                entity.damage(1, v, e.getPlayer());
                System.out.println("entity.damage(100, " + v + " player)");
                entity.damage(100, v, e.getPlayer());
            }

        }
    }

    @EventHandler
    public void onDmg(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if (damageMap.containsKey(entity.getUniqueId())) {
            DamageCause damageCause = damageMap.get(entity.getUniqueId());
            System.out.println(e.getClass().getSimpleName()+ " " + e.getCause() + " expected " + damageCause + " " + (damageCause == e.getCause() ? "OK" : "NOK" ));
        }
    }
}
