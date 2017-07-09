package me.Tiernanator.Meow;

import org.bukkit.plugin.java.JavaPlugin;

import me.Tiernanator.Meow.Command.Meow;

public class MeowMain extends JavaPlugin {
	
	@Override
	public void onEnable() {
		registerCommands();
//		registerEvents();
	}

	@Override
	public void onDisable() {

	}

	public void registerCommands() {
		getCommand("meow").setExecutor(new Meow(this));
	}

}
