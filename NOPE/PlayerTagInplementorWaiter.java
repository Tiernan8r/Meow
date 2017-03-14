package me.Tiernanator.MickTagger.NOPE;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.Tiernanator.MickTagger.Main;
import me.Tiernanator.Packets.Packet;
import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.PacketPlayOutMount;

public class PlayerTagInplementorWaiter extends BukkitRunnable {

	private static Main plugin;
	private static HashMap<Player, Boolean> setUpTag = new HashMap<Player, Boolean>();
	private static HashMap<Player, Integer> setUpTagTime = new HashMap<Player, Integer>();
	private static HashMap<Player, String> tagStringMap = new HashMap<Player, String>();

	public PlayerTagInplementorWaiter(Main main) {
		plugin = main;
	}
	
	public static void addPlayer(Player player, String string) {
		
		if(setUpTag.containsKey(player)) {
			setUpTag.remove(player);
			setUpTagTime.remove(player);
			tagStringMap.remove(player);
		}
		setUpTag.put(player, true);
		setUpTagTime.put(player, 0);
		tagStringMap.put(player, string);
	}
	
	public static boolean needsSetUp(Player player) {
		
		if(setUpTag.containsKey(player)) {
			return setUpTag.get(player);
		} else {
			return false;
		}
		
	}
	
	//Each time a player logs in you wait 5 seconds before giving them the title tag.
	@Override
	public void run() {
		
		Collection<? extends Player> onlinePlayers = plugin.getServer().getOnlinePlayers();
		for(Player player : onlinePlayers) {
			
			if(needsSetUp(player)) {
				
				int i = setUpTagTime.get(player);

				if(i >= 1) {
					setUpTag.remove(player);
					setUpTag.put(player, false);
					
					String tagString = tagStringMap.get(player);
					
					PlayerTag playerTag = new PlayerTag(player, tagString);
					EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
					PacketPlayOutMount packet = new PacketPlayOutMount(entityPlayer);
					Packet.sendPacket(player, packet);
					playerTag.setPlayerTag(player);
				}
				i++;
				setUpTagTime.remove(player);
				setUpTagTime.put(player, i);
				
			}
			
			
		}
		
	}
}
