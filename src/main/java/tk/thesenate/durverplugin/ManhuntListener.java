package tk.thesenate.durverplugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

import static org.bukkit.Bukkit.getPlayer;

public class ManhuntListener implements Listener {

    private int currentTargetIndex = -1;

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        if (ManhuntCmd.manhuntOngoing && event.getItemDrop().getItemStack().equals(ManhuntCmd.trackerCompass) && ManhuntCmd.hunters.contains(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(ChatColor.BLUE + "You dropped this");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (ManhuntCmd.manhuntOngoing && event.hasItem() && Objects.equals(event.getItem(), ManhuntCmd.trackerCompass) && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {

            if (currentTargetIndex + 1 < ManhuntCmd.runners.size()) {
                CompassWorker.trackingNearestPlayer = false;
                currentTargetIndex++;
                CompassWorker.tracking = getPlayer(ManhuntCmd.runners.get(currentTargetIndex));
            } else {
                CompassWorker.trackingNearestPlayer = true;
                currentTargetIndex = -1;
            }

            ItemMeta trackerMeta = ManhuntCmd.trackerCompass.getItemMeta();
            assert trackerMeta != null;

            if (!CompassWorker.trackingNearestPlayer) {
                trackerMeta.setDisplayName("Tracking " + CompassWorker.tracking.getName());
            } else {
                trackerMeta.setDisplayName("Tracking nearest player");
            }

            if (Objects.equals(event.getHand(), EquipmentSlot.HAND)) {
                event.getPlayer().getInventory().getItemInMainHand().setItemMeta(trackerMeta);
            } else if (Objects.equals(event.getHand(), EquipmentSlot.OFF_HAND)) {
                event.getPlayer().getInventory().getItemInOffHand().setItemMeta(trackerMeta);
            }

            ManhuntCmd.trackerCompass.setItemMeta(trackerMeta);

        }
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        if (ManhuntCmd.manhuntOngoing && ManhuntCmd.hunters.contains(event.getPlayer().getUniqueId())) {
            event.getPlayer().getInventory().addItem(ManhuntCmd.trackerCompass);

        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if (ManhuntCmd.manhuntOngoing && ManhuntCmd.hunters.contains(event.getEntity().getUniqueId())) {
            for (Entity e: event.getEntity().getWorld().getEntities()) {
                if (e instanceof Item && ((Item) e).getItemStack().equals(ManhuntCmd.trackerCompass)) {
                    e.remove();
                }
            }
        }
    }

}

    
