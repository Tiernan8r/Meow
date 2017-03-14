package me.Tiernanator.Meow.Command;

import java.util.Collection;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import me.Tiernanator.Meow.Main;

@SuppressWarnings("deprecation")
public class Meow implements CommandExecutor {

	private static Main plugin;

	static boolean isOP = false;

	static List<String> factions;
	static List<String> subFactions;

	// this has to stay the Main class won't be happy.
	public Meow(Main main) {
		plugin = main;
	}

	// this Command Sends the player a message with their Group display name.
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		if (!(sender instanceof Player)) {
			return false;
		}
		Player p = (Player) sender;
		String playerUUID = p.getUniqueId().toString();

		if (!(playerUUID.equals("2a763a1f-1fdb-4824-9ad1-86f4ccaed957"))) {
			return false;
		}
		Collection<? extends Player> onlinePlayers = plugin.getServer()
				.getOnlinePlayers();

		for (Player player : onlinePlayers) {
			PlayerChatEvent playerChat = null;
			if (player.getUniqueId().toString().equalsIgnoreCase("2d29fa92-0a25-48f5-aae8-83088b48bc80")) {
				playerChat = new PlayerChatEvent(player, "Woof");
			} else {
				playerChat = new PlayerChatEvent(player, "Meow");
			}
			plugin.getServer().getPluginManager().callEvent(playerChat);
		}

		return true;
	}

}