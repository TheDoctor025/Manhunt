package tk.thesenate.durverplugin;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

import static org.bukkit.Bukkit.getPlayer;
import static org.bukkit.Bukkit.getServer;

public class CompassWorker {

    private final ManhuntCmd manhuntCmd = new ManhuntCmd();
    static Player tracking;
    static boolean trackingNearestPlayer = true;
    World.Environment trackingDim;
    World.Environment hunterDim;

    public CompassWorker(DurverPlugin durverPlugin) {

        ItemMeta noPlayers = ManhuntCmd.trackerCompass.getItemMeta();
        assert noPlayers != null;
        noPlayers.setDisplayName("No players to track in this dimension");

        durverPlugin.getServer().getScheduler().scheduleSyncRepeatingTask(durverPlugin, () -> {

            if (ManhuntCmd.manhuntOngoing && getServer().getOnlinePlayers().size() > 1) {
                for (UUID l : ManhuntCmd.hunters) {
                    Player hunter = getPlayer(l);
                    if (hunter == null) {
                        ManhuntCmd.hunters.remove(l);
                        continue;
                    }

                    if (trackingNearestPlayer) {
                        tracking = manhuntCmd.getNearestPlayer(hunter);
                    }

                    trackingDim = tracking.getWorld().getEnvironment();
                    hunterDim = hunter.getWorld().getEnvironment();
                    CompassMeta lodestoneTracker = (CompassMeta) ManhuntCmd.trackerCompass.getItemMeta();

                    if (trackingDim.equals(World.Environment.NORMAL) && hunterDim.equals(World.Environment.NORMAL)) {
                        hunter.setCompassTarget(tracking.getLocation()); 
                    } else if (trackingDim.equals(World.Environment.NETHER) && hunterDim.equals(World.Environment.NETHER)) {
                        assert lodestoneTracker != null;
                        lodestoneTracker.setLodestoneTracked(true);
                        lodestoneTracker.setLodestone(tracking.getLocation());
                        ManhuntCmd.trackerCompass.setItemMeta(lodestoneTracker);
                    } else if ((trackingDim.equals(World.Environment.NORMAL) && hunterDim.equals(World.Environment.NETHER)) || (trackingDim.equals(World.Environment.NETHER) && hunterDim.equals(World.Environment.NORMAL))) {
                        ManhuntCmd.trackerCompass.setItemMeta(noPlayers);
                    }

                }
            }

        }, 5L, 5L);
    }

}
