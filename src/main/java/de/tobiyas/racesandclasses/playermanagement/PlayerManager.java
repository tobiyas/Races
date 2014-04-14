/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.playermanagement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.armorandtool.ArmorToolManager;
import de.tobiyas.racesandclasses.datacontainer.arrow.ArrowManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.persistence.file.YAMLPersistenceProvider;
import de.tobiyas.racesandclasses.playermanagement.leveling.PlayerLevelManager;
import de.tobiyas.racesandclasses.playermanagement.spellmanagement.PlayerSpellManager;

public class PlayerManager{
	
	/**
	 * This is the map of each player's health container
	 */
	private HashMap<UUID, PlayerContainer> playerData;
	
	/**
	 * The plugin to access other parts of the Plugin.
	 */
	private RacesAndClasses plugin;
	
	/**
	 * Creates a new PlayerManager and resets all values
	 */
	public PlayerManager(){
		playerData = new HashMap<UUID, PlayerContainer>();
		plugin = RacesAndClasses.getPlugin();
	}
	
	/**
	 * Loads the health manager from the playerdata.yml
	 */
	public void init(){
		loadPlayerContainer();
	}
	
	/**
	 * Stores the data in the PlayerManager in the player data ymls
	 */
	public void savePlayerContainer(){
		boolean useDB = plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB();
		for(PlayerContainer container: playerData.values()){
			container.save(useDB);
		}
	}
	
	/**
	 * loads the health manager internally
	 */
	private void loadPlayerContainer(){
		boolean useDB = plugin.getConfigManager().getGeneralConfig().isConfig_savePlayerDataToDB();

		Set<UUID> players = new HashSet<UUID>();
		if(useDB){
			for(Player online : Bukkit.getOnlinePlayers()){
				players.add(online.getUniqueId());
			}
		}else{
			players = YAMLPersistenceProvider.getAllPlayersKnown();
		}
		
		for(UUID playerUUID : players){
			if(playerUUID == null) continue;
			
			Player player = Bukkit.getPlayer(playerUUID);
			if(player == null || !player.isOnline()) continue;
			
			PlayerContainer container = PlayerContainer.loadPlayerContainer(playerUUID, useDB);
			if(container != null){
				playerData.put(playerUUID, container);
			}
		}
	}
	
	/**
	 * Gives the player a new PlayerContainer.
	 * It scans the player's data from the Race and Class container and
	 * calculates the needed values.
	 * 
	 * @param player to create
	 */
	public void addPlayer(UUID player){
		RaceContainer container = (RaceContainer) plugin.getRaceManager().getHolderOfPlayer(player);
		double maxHealth = 1;
		
		if(container == null){
			maxHealth = plugin.getConfigManager().getGeneralConfig().getConfig_defaultHealth();
		}else{
			maxHealth = container.getRaceMaxHealth();
		}
		
		ClassContainer classContainer = (ClassContainer) plugin.getClassManager().getHolderOfPlayer(player);
		if(classContainer != null){
			maxHealth = classContainer.modifyToClass(maxHealth);
		}
			
		playerData.put(player, new PlayerContainer(player, maxHealth).checkStats());
	}
	
	/**
	 * This parses the current health of a player.
	 * NOTICE: It redirects to the bukkit function.
	 * 
	 * @param playerUUID
	 * @return
	 */
	public double getHealthOfPlayer(UUID player){
		PlayerContainer container = getCreate(player, true);
		if(container == null) return -1;
		return container.getCurrentHealth();
	}
	
	
	/**
	 * Rescans the player and creates a new {@link PlayerContainer} if he has none.
	 *  
	 * @param player to check
	 */
	public void checkPlayer(UUID player){
		PlayerContainer hContainer = playerData.get(player);
		if(hContainer == null){
			plugin.getRaceManager().loadIfNotExists(player);
			plugin.getClassManager().loadIfNotExists(player);
			
			RaceContainer container = (RaceContainer) plugin.getRaceManager().getHolderOfPlayer(player);
			
			double maxHealth = plugin.getConfigManager().getGeneralConfig().getConfig_defaultHealth();
			if(container != null){
				maxHealth = container.getRaceMaxHealth();
			}
			
			ClassContainer classContainer = (ClassContainer) plugin.getClassManager().getHolderOfPlayer(player);
			if(classContainer != null){
				maxHealth = classContainer.modifyToClass(maxHealth);
			}
			
			PlayerContainer healthContainer = new PlayerContainer(player, maxHealth);
			healthContainer.checkStats();
			playerData.put(player, healthContainer);
		}else{
			hContainer.checkStats();
		}
	}
	
	
	/**
	 * Checks if the ArrowManager of a Player exists and returns it.
	 * If the ArrowManager does not exist, it is created.
	 * 
	 * @param playerUUID to check
	 * @return the {@link ArrowManager} of the Player
	 */
	public ArrowManager getArrowManagerOfPlayer(UUID player) {
		return getCreate(player, true).getArrowManager();
	}
	
	/**
	 * Checks if the {@link ArmorToolManager} of a Player exists and returns it.
	 * If the {@link ArmorToolManager} does not exist, it is created.
	 * 
	 * @param playerUUID to check
	 * @return the {@link ArmorToolManager} of the Player
	 */
	public ArmorToolManager getArmorToolManagerOfPlayer(UUID player){
		return getCreate(player, true).getArmorToolManager();
	}

	/**
	 * Forces an HP display output for the player.
	 * 
	 * @param playerUUID to force the output
	 * @return true, if it worked.
	 */
	public boolean displayHealth(UUID player) {
		PlayerContainer container = getCreate(player, true);
		if(container == null) return false; //this should not happen...
		container.forceHPOut();
		return true;
	}

	/**
	 * Switches the God Mode of the player.
	 * 
	 * @param playerUUID to switch
	 * @return true if worked, false if player not found.
	 */
	public boolean switchGod(UUID playerUUID) {
		PlayerContainer container = playerData.get(playerUUID);
		if(container != null){
			container.switchGod();
			return true;
		}
		return false;
	}

	/**
	 * Returns the maximum Health of a Player.
	 * This should be identical to: {@link Player#getMaxHealth()}
	 * 
	 * @param playerUUID to check
	 * @return the max health of a player.
	 */
	public double getMaxHealthOfPlayer(UUID player) {
		PlayerContainer container = getCreate(player, true);
		if(container == null) return -1;
		return container.getMaxHealth();
	}
	
	/**
	 * Gets the current PlayerContainer of the Player.
	 * If not found, a new one is created. 
	 * 
	 * @param playerUUID to check
	 * @return the found or new created container.
	 */
	private PlayerContainer getCreate(UUID player){
		return getCreate(player, true);
	}
	
	/**
	 * Gets the PlayerContainer of a Player.
	 * If the container is not found, a new one is created 
	 * if the flag (create) is set to true.
	 * 
	 * @param playerUUID
	 * @param create
	 * @return
	 */
	private PlayerContainer getCreate(UUID player, boolean create){
		PlayerContainer container = playerData.get(player);
		if(container == null && create){
			checkPlayer(player);
			container = playerData.get(player);
		}
		
		return container;
	}

	/**
	 * Returns if the Player has God mode on.
	 * 
	 * @param playerUUID to check
	 * @return true if has god on
	 */
	public boolean isGod(UUID player) {
		PlayerContainer containerOfPlayer = getCreate(player);
		return containerOfPlayer.isGod();
	}
	
	
	/**
	 * Gets the SpellManager of a player.
	 * @param playerUUID to get
	 * @return the SpellManager of the Player.
	 */
	public PlayerSpellManager getSpellManagerOfPlayer(UUID player){
		PlayerContainer containerOfPlayer = getCreate(player);
		return containerOfPlayer.getSpellManager();
	}
	
	
	/**
	 * Returns the PlayerLevelManager of the Player.
	 * 
	 * @param playerUUID
	 * @return
	 */
	public PlayerLevelManager getPlayerLevelManager(UUID player){
		PlayerContainer containerOfPlayer = getCreate(player);
		return containerOfPlayer.getPlayerLevelManager();
	}

	/**
	 * Returns the current size of the PlayerManager.
	 * 
	 * @return the size of the player manager.
	 */
	public String getPlayerNumber() {
		return playerData.size() + "";
	}

}
