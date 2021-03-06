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
package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.reminder;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.util.schedule.DebugBukkitRunnable;

public class RaceReminder extends DebugBukkitRunnable {

	private RacesAndClasses plugin;
	
	public RaceReminder(){
		super("RaceReminder");
		plugin = RacesAndClasses.getPlugin();
		int reminderTime = plugin.getConfigManager().getGeneralConfig().getConfig_reminder_interval() * 20 * 60;
		this.runTaskTimer(plugin, reminderTime, reminderTime);
	}
	
	@Override
	protected void runIntern() {
		if(plugin.getConfigManager().getGeneralConfig().isConfig_activate_reminder()){
			AbstractTraitHolder defaultContainer = plugin.getRaceManager().getDefaultHolder();
			List<RaCPlayer> list = plugin.getRaceManager().getAllPlayersOfHolder(defaultContainer);
			for(RaCPlayer player : list){
				if(player == null || !player.isOnline()) continue;
				postSelectRace(player);
			}
		}
	}
	
	/**
	 * Checks if the Player has any permission for any Race.
	 * This is only checked, when the Configuration option for checking is set.
	 * 
	 * @param player
	 * @return
	 */
	private boolean hasAnyRacePermission(RaCPlayer player) {
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_usePermissionsForRaces()) return true;
		
		String name = null;
		for(OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()){
			if(offlinePlayer.getUniqueId().equals(player)){
				name = offlinePlayer.getName();
				break;
			}
		}
		
		if(name == null) return false;
		
		//TODO fix with Utils.
		if(plugin.getPermissionManager().checkPermissionsSilent(name, PermissionNode.racePermPre + "*")) return true;
		
		for(String raceName : plugin.getRaceManager().listAllVisibleHolders()){
			if(plugin.getPermissionManager().checkPermissionsSilent(name, PermissionNode.racePermPre + raceName)){
				return true;
			}
		}
		
		return false;
	}
	

	/**
	 * Tries posting the reminder to the {@link Player}.
	 * It also checks the Permissions if set in Configuration.
	 * 
	 * @param player
	 */
	private void postSelectRace(RaCPlayer player){
		if(!player.isOnline()) return;
		if(!hasAnyRacePermission(player)) return;
		
		boolean useGui = plugin.getConfigManager().getGeneralConfig().isConfig_useRaceGUIToSelect();
		
		if(player != null){
			player.sendMessage(ChatColor.YELLOW + "[PRIVATE-INFO]: You have not selected a race.");
			player.sendMessage(ChatColor.YELLOW + "[PRIVATE-INFO]: Use " + ChatColor.RED + "/race select " + (useGui?"":"<racename>") + " " + ChatColor.YELLOW + "to select a race.");
			player.sendMessage(ChatColor.YELLOW + "[PRIVATE-INFO]: To see all races use: " + ChatColor.LIGHT_PURPLE + "/race list");
		}
	}

}
