package me.Tiernanator.MickTagger.NOPE;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.Tiernanator.MickTagger.Main;
import me.Tiernanator.Utilities.MetaData.MetaData;

public class PlayerTagHandler extends BukkitRunnable {

	private static Main plugin;
	private static HashMap<Player, List<Entity>> playerEntityTextMap = new HashMap<Player, List<Entity>>();
	private static HashMap<Player, Integer> playerEntityTextDuration = new HashMap<Player, Integer>();
	private static HashMap<Player, Integer> playerEntityTextStayDuration = new HashMap<Player, Integer>();

	public PlayerTagHandler(Main main) {
		plugin = main;
	}
	
	public static List<Entity> getTextEntities(Player player) {
		if(hasTextEntities(player)) {
			return playerEntityTextMap.get(player);
		} else {
			return null;
		}
	}
	
	public static void setTextEntities(Player player, List<Entity> textEntities, int stayDuration) {
		if(hasTextEntities(player)) {
			playerEntityTextMap.remove(player);
		}
		playerEntityTextMap.put(player, textEntities);
		playerEntityTextDuration.put(player, 0);
		playerEntityTextStayDuration.put(player, stayDuration);
	}
	
	public static void removeTextEntities(Player player) {
		
		if(hasTextEntities(player)) {
			List<Entity> entityTexts = getTextEntities(player);
			//Setting passenger for an entity that is a passenger, unsets it as passenger...
			player.setPassenger(entityTexts.get(0));
			for (int i = 0; i < entityTexts.size() - 1; i++) {
				entityTexts.get(i).setPassenger(entityTexts.get(i + 1));
			}
			for(Entity entity : entityTexts) {
				entity.remove();
			}
			playerEntityTextDuration.remove(player);
			playerEntityTextMap.remove(player);
			playerEntityTextStayDuration.remove(player);
		}
	}
	
	public static boolean hasTextEntities(Player player) {
		return playerEntityTextMap.containsKey(player);
	}
	
	public void incrementTextEntitiesDuration(Player player) {
		if(!hasTextEntities(player)) {
			return;
		}
		int i = playerEntityTextDuration.get(player);
		i++;
		playerEntityTextDuration.put(player, i);
	}
	
	//Each time a player chats, the message stays for 10 seconds in the chat window
	@Override
	public void run() {
		
		Collection<? extends Player> onlinePlayers = plugin.getServer().getOnlinePlayers();
		for(Player player : onlinePlayers) {
			
			if(hasTextEntities(player)) {
				int i = playerEntityTextDuration.get(player);
				int stayDuration = playerEntityTextStayDuration.get(player);
				if(stayDuration == -1) {
					continue;
				}
				if(i > stayDuration) {
					PlayerTag playerTag = (PlayerTag) MetaData.getMetadata(player, "PlayerTag", plugin);
					playerTag.resetText();
				} else {
					incrementTextEntitiesDuration(player);
				}
			}
			
			
		}
		
	}
}
