package dev.omega24.hoppersort;

import dev.omega24.hoppersort.listener.BlockListeners;
import dev.omega24.hoppersort.listener.HopperListeners;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class HopperSort extends JavaPlugin {
    private final Config config = new Config(this);
    private final NamespacedKey hopperKey = new NamespacedKey(this, "hopper");

    @Override
    public void onEnable() {
        try {
            config.load();
        } catch (InvalidConfigurationException e) {
            getLogger().severe("Failed to load config: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.addRecipe();
        getServer().getPluginManager().registerEvents(new BlockListeners(this), this);
        getServer().getPluginManager().registerEvents(new HopperListeners(this), this);
    }

    private void addRecipe() {
        ItemStack item = new ItemStack(Material.HOPPER);

        ItemMeta meta = item.getItemMeta();
        meta.displayName(config.itemName);
        if (config.itemLore.size() > 0) {
            meta.lore(config.itemLore);
        }

        meta.getPersistentDataContainer().set(hopperKey, PersistentDataType.BYTE, (byte) 1);
        if (config.customModelData >= 1) {
            meta.setCustomModelData(config.customModelData);
        }

        item.setItemMeta(meta);

        ShapedRecipe recipe = new ShapedRecipe(hopperKey, item);
        recipe.shape(config.recipe.get(0), config.recipe.get(1), config.recipe.get(2));

        config.recipeItems.forEach((itemKey, value) -> recipe.setIngredient(itemKey.charAt(0), value));
        getServer().addRecipe(recipe);
    }

    public NamespacedKey getHopperKey() {
        return hopperKey;
    }
}
