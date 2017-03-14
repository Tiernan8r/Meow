package me.Tiernanator.MickTagger.NOPE;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.Tiernanator.Colours.MessageColourer;
import me.Tiernanator.Factions.Faction;
import me.Tiernanator.MickTagger.Main;
import me.Tiernanator.Packets.Packet;
import me.Tiernanator.Permissions.PermissionGroups.Group;
import me.Tiernanator.Utilities.MetaData.MetaData;
import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_10_R1.PacketPlayOutMount;

public class PlayerTag {

	private Player player;
	private String text;
	private String defaultText;

	private static Main plugin;
	public static void setPlugin(Main main) {
		plugin = main;
	}

	public PlayerTag(Player player, String text) {

		this.player = player;
		this.text = text;

		setTag(this.player, this.text);
		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		PacketPlayOutMount packet = new PacketPlayOutMount(entityPlayer);
		Packet.sendPacket(player, packet);
	}

	public static PlayerTag getPlayerTag(Player player) {
		PlayerTag playerTag = (PlayerTag) MetaData.getMetadata(player,
				"PlayerTag", plugin);
		return playerTag;
	}

	public void setPlayerTag(Player player) {
		MetaData.setMetadata(player, "PlayerTag", this, plugin);
	}

	public Player getPlayer() {
		return this.player;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String newText, int duration) {

		PlayerTagHandler.removeTextEntities(getPlayer());
		this.defaultText = getText();
		this.text = newText;
		setTag(this.player, this.text, duration);

	}

	public void resetText() {
//		PlayerTagHandler.removeTextEntities(getPlayer());
		setText(this.defaultText, -1);
	}

	public String getDefaultText() {
		return this.defaultText;
	}

	public void setDefaultText(String newDefaultText) {

		PlayerTagHandler.removeTextEntities(getPlayer());
		this.defaultText = newDefaultText;
		setTag(this.player, this.defaultText, -1);

	}

	private void setTag(Player player, String text, int stayDuration) {

		Location location = player.getLocation();

		List<Entity> textEntities = new ArrayList<Entity>();
		int numberSubEntities = 1;
		int[] idArray = new int[numberSubEntities + 1];
		for (int i = 0; i < numberSubEntities; i++) {

			Entity entity = player.getWorld().spawnEntity(location,
					EntityType.SILVERFISH);
			
			textEntities.add(entity);
			MetaData.setMetadata(entity, "TagEntity", true, plugin);
			idArray[i] = entity.getEntityId();

		}

		ArmorStand armourStand = (ArmorStand) location.getWorld()
				.spawnEntity(location, EntityType.ARMOR_STAND);
		textEntities.add(armourStand);
		idArray[numberSubEntities] = armourStand.getEntityId();

		PotionEffect invisibility = new PotionEffect(
				PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false,
				false);
		PotionEffect invincibility = new PotionEffect(
				PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false,
				false);
		for (Entity entity : textEntities) {

			if (entity instanceof ArmorStand) {
				ArmorStand armorStand = (ArmorStand) entity;
				armorStand.setVisible(false);
				armorStand.setCustomName(text);
				armorStand.setCustomNameVisible(true);
				armorStand.setInvulnerable(true);
				armorStand.setMarker(true);
				armorStand.setCollidable(false);
			}
			if (entity instanceof LivingEntity) {

				LivingEntity livingEntity = (LivingEntity) entity;

				livingEntity.setCollidable(false);
				livingEntity.setAI(false);
				livingEntity.addPotionEffect(invisibility, true);
				livingEntity.addPotionEffect(invincibility, true);
			}

			entity.setGravity(false);
			entity.setInvulnerable(true);
			entity.setSilent(true);
		}

		PlayerTagHandler.setTextEntities(player, textEntities, stayDuration);

		player.setPassenger(textEntities.get(0));
		for (int i = 0; i < textEntities.size() - 1; i++) {
			textEntities.get(i).setPassenger(textEntities.get(i + 1));
		}

		setPlayerTag(player);

		EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		PacketPlayOutMount packet = new PacketPlayOutMount(entityPlayer);
		Packet.sendPacket(player, packet);

		PacketPlayOutEntityDestroy destroyEntity = new PacketPlayOutEntityDestroy(
				idArray);
		Packet.sendPacket(player, destroyEntity);

	}

	private void setTag(Player player, String text) {
		setTag(player, text, -1);
	}

	public static void setUpDisplayName(Player player) {
		
		PlayerTagHandler.removeTextEntities(player);

		// get the player's group
		Group playerGroup = Group.getPlayerGroup(player);
		// get the players prefix from their group: it has a colour code so must
		// be interpreted with the colour
		// defaulting to white
		String prefix = MessageColourer.parseMessage(playerGroup.getPrefix(),
				ChatColor.WHITE);
		// same with suffix
		String suffix = MessageColourer.parseMessage(playerGroup.getSuffix(),
				ChatColor.WHITE);

		// get the faction
		Faction playerFaction = Faction.getPlayerFaction(player);
		String factionPrefix = MessageColourer.parseMessage(playerFaction.getPrefix(), ChatColor.WHITE);
		String factionSuffix = MessageColourer.parseMessage(playerFaction.getSuffix(), ChatColor.WHITE);
		// get the faction's specific colour
//		ChatColor factionColour = playerFaction.chatColour();
		// combine all factors
		String tagString = prefix + suffix + factionPrefix
				+ playerFaction.getName() + factionSuffix + ChatColor.WHITE + " "
				+ player.getName();

		PlayerTagInplementorWaiter.addPlayer(player, tagString);
	}

}
