package dev.omega24.hoppersort.listener;

import dev.omega24.hoppersort.HopperSort;
import dev.omega24.hoppersort.hacks.HSInventoryMoveItemEvent;
import dev.omega24.hoppersort.hacks.HSInventoryPickupItemEvent;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class HopperListeners implements Listener {
    private final HopperSort plugin;

    public HopperListeners(HopperSort plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryPickupItem(InventoryPickupItemEvent event) {
        if (event instanceof HSInventoryPickupItemEvent) {
            return;
        }

        if (!(event.getInventory().getHolder(false) instanceof Hopper hopper && hopper.getPersistentDataContainer().has(plugin.getHopperKey()))) {
            return;
        }

        boolean shouldPickup = false;
        ItemStack item = event.getItem().getItemStack();

        for (ItemStack content : event.getInventory().getContents()) {
            if (content == null || content.getMaxStackSize() == content.getAmount() || !content.isSimilar(item)) {
                continue;
            }

            int newAmount = content.getAmount() + item.getAmount();
            if (newAmount > content.getMaxStackSize()) {
                int amount = newAmount - content.getMaxStackSize();

                Item newItem = event.getItem();
                newItem.setItemStack(item.asQuantity(amount));

                HSInventoryPickupItemEvent newEvent = new HSInventoryPickupItemEvent(event.getInventory(), newItem);
                if (newEvent.callEvent()) {
                    event.getItem().setItemStack(item.clone().subtract(amount));
                    event.getInventory().addItem(item.asQuantity(amount));
                }
            } else {
                shouldPickup = true;
            }
            break;
        }

        if (!shouldPickup) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event instanceof HSInventoryMoveItemEvent) {
            return;
        }

        if (!(event.getDestination().getHolder(false) instanceof Hopper hopper && hopper.getPersistentDataContainer().has(plugin.getHopperKey()))) {
            return;
        }
    }
}
