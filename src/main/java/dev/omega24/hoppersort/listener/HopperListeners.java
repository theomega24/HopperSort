package dev.omega24.hoppersort.listener;

import dev.omega24.hoppersort.HopperSort;
import dev.omega24.hoppersort.hacks.HSInventoryMoveItemEvent;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class HopperListeners implements Listener {
    private final HopperSort plugin;

    public HopperListeners(HopperSort plugin) {
        this.plugin = plugin;
    }

    // todo: this ends up adding a new filter in many cases, handle the logic ourselves :'(
    @EventHandler(ignoreCancelled = true)
    public void onInventoryPickupItem(InventoryPickupItemEvent event) {
        if (!(event.getInventory().getHolder(false) instanceof Hopper hopper)) {
            return;
        }

        if (!hopper.getPersistentDataContainer().has(plugin.getHopperKey(), PersistentDataType.BYTE)) {
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

    // todo: properly replicate vanilla behaviour (down before sideways)
    @EventHandler(ignoreCancelled = true)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event instanceof HSInventoryMoveItemEvent) {
            return;
        }

        if (event.getSource().getHolder(false) instanceof Hopper hopper) {
            if (!hopper.getPersistentDataContainer().has(plugin.getHopperKey(), PersistentDataType.BYTE)) {
                return;
            }

            event.setCancelled(true);
            int amount = event.getItem().getAmount();

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                for (ItemStack item : hopper.getInventory().getContents()) {
                    if (item == null || item.getAmount() == 1) {
                        continue;
                    }

                    ItemStack moved = item.clone().asQuantity(amount);
                    InventoryMoveItemEvent newEvent = new HSInventoryMoveItemEvent(hopper.getInventory(), moved, event.getDestination(), event.getInitiator() == hopper.getInventory());
                    if (newEvent.callEvent()) {
                        item.subtract(amount);
                        event.getDestination().addItem(moved); // todo: make sure inventory isn't full before moving
                    }

                    break;
                }
            }, 1);

            return;
        }

        if (event.getDestination().getHolder(false) instanceof Hopper hopper) {
            if (!hopper.getPersistentDataContainer().has(plugin.getHopperKey(), PersistentDataType.BYTE)) {
                return;
            }

            event.setCancelled(true);
            Inventory inventory = event.getSource().getHolder(false).getInventory();
            int amount = event.getItem().getAmount();

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                for (ItemStack item : inventory.getContents()) {
                    if (item == null || !hopper.getInventory().containsAtLeast(item, 1)) {
                        continue;
                    }

                    ItemStack moved = item.clone().asQuantity(amount);
                    InventoryMoveItemEvent newEvent = new HSInventoryMoveItemEvent(event.getSource(), moved, hopper.getInventory(), event.getInitiator() == event.getSource());
                    if (newEvent.callEvent()) {
                        item.subtract(amount);
                        event.getDestination().addItem(moved); // todo: make sure inventory isn't full before moving
                    }
                }
            }, 1);
        }
    }
}
