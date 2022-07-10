package dev.omega24.hoppersort.listener;

import dev.omega24.hoppersort.HopperSort;
import dev.omega24.hoppersort.hacks.HSInventoryMoveItemEvent;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class HopperListeners implements Listener {
    private final HopperSort plugin;

    public HopperListeners(HopperSort plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryPickupItem(InventoryPickupItemEvent event) {
        if (!(event.getInventory().getHolder() instanceof Hopper hopper)) {
            return;
        }

        if (hopper.getPersistentDataContainer().has(plugin.getHopperKey(), PersistentDataType.BYTE)) {
            if (hopper.getPersistentDataContainer().get(plugin.getHopperKey(), PersistentDataType.BYTE) != 1) {
                return;
            }

            boolean canPickup = false;
            for (ItemStack item : hopper.getInventory().getContents()) {
                if (item == null) {
                    continue;
                }

                if (event.getItem().getItemStack().getMaxStackSize() == 1) {
                    continue;
                }

                if (event.getItem().getItemStack().getType() == item.getType()) {
                    canPickup = true;
                    break;
                }
            }

            if (!canPickup) {
                event.setCancelled(true);
            }
        }
    }

    // this method is gross and it's not my fault
    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event instanceof HSInventoryMoveItemEvent) {
            return;
        }

        if (!(event.getSource().getHolder(false) instanceof Hopper hopper)) {
            return;
        }

        if (hopper.getPersistentDataContainer().has(plugin.getHopperKey(), PersistentDataType.BYTE)) {
            if (hopper.getPersistentDataContainer().get(plugin.getHopperKey(), PersistentDataType.BYTE) != 1) {
                return;
            }

            event.setCancelled(true);
            int amount = event.getItem().getAmount();

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                for (int i = 0; i < hopper.getInventory().getContents().length; i++) {
                    ItemStack item = hopper.getInventory().getItem(i);

                    if (item == null || item.getAmount() < 2) {
                        continue;
                    }

                    ItemStack movedItem = item.clone();
                    movedItem.setAmount(amount);

                    InventoryMoveItemEvent newEvent = new HSInventoryMoveItemEvent(hopper.getInventory(), movedItem, event.getDestination(), event.getInitiator() == hopper.getInventory());
                    if (newEvent.callEvent()) {
                        item.subtract(amount);
                        event.getDestination().addItem(movedItem);
                    }
                    break;
                }
            }, 1);
        }
    }
}
