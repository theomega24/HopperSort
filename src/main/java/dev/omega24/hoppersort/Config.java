package dev.omega24.hoppersort;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.List;
import java.util.Map;

public class Config {
    private final HopperSort plugin;

    public Config(HopperSort plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
    }

    public List<String> recipe = Lists.newArrayList();
    public Map<String, Material> recipeItems = Maps.newHashMap();

    public Component itemName;
    public List<Component> itemLore = Lists.newArrayList();
    public int customModelData = -1;

    public void load() throws InvalidConfigurationException {
        recipe = plugin.getConfig().getStringList("recipe.shape");

        for (String key : plugin.getConfig().getConfigurationSection("recipe.translations").getKeys(false)) {
            String value = plugin.getConfig().getString("recipe.translations." + key);
            Material material = Material.matchMaterial(value);
            if (material == null) {
                throw new InvalidConfigurationException(value + " is not a valid material");
            }

            recipeItems.put(key.toUpperCase(), material);
        }

        itemName = MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("item.name"));
        plugin.getConfig().getStringList("item.lore").forEach(line -> itemLore.add(MiniMessage.miniMessage().deserialize(line)));
        customModelData = plugin.getConfig().getInt("item.custom-model-data");
    }
}
