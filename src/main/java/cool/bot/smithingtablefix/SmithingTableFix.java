package cool.bot.smithingtablefix;

// Imports
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

// Main Class
public final class SmithingTableFix extends JavaPlugin {
    // Variables from config
    FileConfiguration config = this.getConfig();
    boolean creativeSmithsFree = config.getBoolean("creativeSmithsFree");
    int maxDistance = config.getInt("maxDistance");
    boolean playSound = config.getBoolean("playSound");
    boolean sendFailureMessages = config.getBoolean("sendFailureMessages");
    boolean sendSuccessMessages = config.getBoolean("sendSuccessMessages");

    @Override
    public void onEnable() {
        getCommand("smithing").setExecutor(new SmithingCommandExecutor());
        this.saveDefaultConfig();

    }

    public class SmithingCommandExecutor implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            // Negative checks
            /// Check if command is coming from a Player
            if (!(sender instanceof Player)) {
                if (sendFailureMessages) {
                    getLogger().info(config.getString("logger.notPlayer"));
                }
                return true;
            }
            Player player = (Player) sender;

            /// Check if player has the permissions to use the command
            if (!(player.hasPermission("smithing.use") || player.hasPermission("smithing.anywhere"))) {
                sendFailMessage(player,"message.noPermission");
                return true;
            }

            /// Check if the player is looking at a smithing table.
            if (!(player.getTargetBlockExact(maxDistance) != null && player.getTargetBlockExact(maxDistance).getType() == Material.SMITHING_TABLE)) {
                if (!(player.hasPermission("smithing.anywhere"))) {
                    sendFailMessage(player,"message.noTable");
                    return true;
                }
            }

            /// Check if the player has Netherite
            ItemStack freshNeth = new ItemStack(Material.NETHERITE_INGOT);
            if (!((player.getGameMode() == GameMode.CREATIVE && creativeSmithsFree) || player.hasPermission("smithing.free"))) {
                if (!(player.getInventory().containsAtLeast(freshNeth, 1))) {
                    sendFailMessage(player,"message.noIngot");
                    return true;
                }
            }

            // Command Body
            /// Check if the player is holding a valid item
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            switch (itemInHand.getType()) {
                case DIAMOND_AXE:
                    itemInHand.setType(Material.NETHERITE_AXE);
                    break;
                case DIAMOND_SWORD:
                    itemInHand.setType(Material.NETHERITE_SWORD);
                    break;
                case DIAMOND_SHOVEL:
                    itemInHand.setType(Material.NETHERITE_SHOVEL);
                    break;
                case DIAMOND_PICKAXE:
                    itemInHand.setType(Material.NETHERITE_PICKAXE);
                    break;
                case DIAMOND_HOE:
                    itemInHand.setType(Material.NETHERITE_HOE);
                    break;
                case DIAMOND_HELMET:
                    itemInHand.setType(Material.NETHERITE_HELMET);
                    break;
                case DIAMOND_CHESTPLATE:
                    itemInHand.setType(Material.NETHERITE_CHESTPLATE);
                    break;
                case DIAMOND_LEGGINGS:
                    itemInHand.setType(Material.NETHERITE_LEGGINGS);
                    break;
                case DIAMOND_BOOTS:
                    itemInHand.setType(Material.NETHERITE_BOOTS);
                    break;
                default:
                    sendFailMessage(player,"message.invalidItem");
                    return true;
            }

            if (playSound) {
                player.getWorld().playSound(player.getLocation(),Sound.BLOCK_SMITHING_TABLE_USE, 1, 1);
            }
            if (!((player.getGameMode() == GameMode.CREATIVE && creativeSmithsFree) || player.hasPermission("smithing.free"))) {
                player.getInventory().removeItem(freshNeth);
            }
            if (sendSuccessMessages) {
                player.spigot().sendMessage(new TextComponent(config.getString("message.success")));
            }
            return true;
        }

    }

    // Helper function for sending messages to a player when a check fails.
    public void sendFailMessage(Player player, String message) {
        if (config.getBoolean("sendFailureMessages")) {
            player.spigot().sendMessage(new TextComponent(config.getString(message)));
        }
    }

}
