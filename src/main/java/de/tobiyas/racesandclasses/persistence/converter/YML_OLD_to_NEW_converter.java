package de.tobiyas.racesandclasses.persistence.converter;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class YML_OLD_to_NEW_converter {

	
	/**
	 * Converts the old data to new one.
	 */
	public static void convert(){
		File playerDataFolder = new File(Consts.playerDataPath);
		
		int converted = 0;
		File[] files = playerDataFolder.listFiles();
		System.out.println("Converting " + files.length + " files");
		
		for(File file : files){
			if(!file.getName().endsWith(".yml")) continue;
			
			YAMLConfigExtended oldConfig = new YAMLConfigExtended(file).load();
			if(!oldConfig.contains("playerdata")) continue;
			
			String playerName = file.getName().replace(".yml", "");
			OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
			UUID playerUUID = player.getUniqueId();
			
			File newPlayerFile = new File(Consts.playerDataPath, playerUUID.toString() + ".yml");
			//check for already consistency
			if(newPlayerFile.exists()){
				RacesAndClasses.getPlugin().logWarning("Wanted to convert Player file of: " + playerName 
						+ " but already found an UUID file (" + playerUUID.toString() + ") of him. "
						+ "His old file will be renamed to " + playerName + ".yml_old.");
				
				file.renameTo(new File(Consts.playerDataPath, playerName + ".yml_old"));
				file.delete();
				continue;
			}

			YAMLConfigExtended newConfig = new YAMLConfigExtended(newPlayerFile).load();
			newConfig.set("lastKnownName", playerName);
			newConfig.set("uuid", player.getUniqueId().toString());
			
			String mainPath = "playerdata." + playerName;
			for(String child : oldConfig.getChildren(mainPath)){
				newConfig.set(child, oldConfig.get(mainPath + "." + child));
			}
			
			newConfig.saveAsync();
			
			if(!file.delete()) RacesAndClasses.getPlugin().logWarning("Could not delete " + file.getAbsolutePath() + " please delete by hand.");
			converted++;
		}
		
		if(converted > 0){
			RacesAndClasses.getPlugin().log("Converted " + converted + " old files to UUID support.");
		}
	}
}
