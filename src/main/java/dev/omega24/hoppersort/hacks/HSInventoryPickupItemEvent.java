package dev.omega24.hoppersort.hacks;

import org.bukkit.entity.Item;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class HSInventoryPickupItemEvent extends InventoryPickupItemEvent {

    public HSInventoryPickupItemEvent(@NotNull Inventory inventory, @NotNull Item item) {
        super(inventory, item);
    }
}
