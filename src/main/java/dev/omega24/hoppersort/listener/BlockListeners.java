package dev.omega24.hoppersort.listener;

import dev.omega24.hoppersort.HopperSort;
import org.bukkit.GameMode;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.block.TileState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class BlockListeners implements Listener {
    private final HopperSort plugin;

    public BlockListeners(HopperSort plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getItemInHand().getItemMeta().getPersistentDataContainer().has(plugin.getHopperKey()) && event.getItemInHand().getItemMeta().getPersistentDataContainer().get(plugin.getHopperKey(), PersistentDataType.BYTE) == 1) {
            ((TileState) event.getBlock().getState(false)).getPersistentDataContainer().set(plugin.getHopperKey(), PersistentDataType.BYTE, (byte) 1);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!(event.getBlock().getState(false) instanceof TileState tileState) || !(tileState instanceof Hopper hopper)) {
            return;
        }

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (hopper.getPersistentDataContainer().has(plugin.getHopperKey(), PersistentDataType.BYTE)) {
            if (hopper.getPersistentDataContainer().get(plugin.getHopperKey(), PersistentDataType.BYTE) == 1) {
                event.getBlock().getDrops(event.getPlayer().getInventory().getItemInMainHand()).forEach(item -> {
                    ItemMeta meta = item.getItemMeta();
                    meta.getPersistentDataContainer().set(plugin.getHopperKey(), PersistentDataType.BYTE, (byte) 1);
                    item.setItemMeta(meta);

                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
                });

                for (ItemStack content : hopper.getInventory().getContents()) {
                    if (content == null) {
                        continue;
                    }

                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), content);
                }

                event.setDropItems(false);
            }
        }
    }
}
