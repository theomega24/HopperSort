package dev.omega24.hoppersort.hacks;

import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HSInventoryMoveItemEvent extends InventoryMoveItemEvent {

    public HSInventoryMoveItemEvent(@NotNull Inventory sourceInventory, @NotNull ItemStack itemStack, @NotNull Inventory destinationInventory, boolean didSourceInitiate) {
        super(sourceInventory, itemStack, destinationInventory, didSourceInitiate);
    }
}
