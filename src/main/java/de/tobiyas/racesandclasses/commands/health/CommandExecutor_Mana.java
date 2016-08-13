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
package de.tobiyas.racesandclasses.commands.health;

import static de.tobiyas.racesandclasses.translation.languages.Keys.only_players;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;

public class CommandExecutor_Mana extends AbstractCommand {

	public CommandExecutor_Mana(){
		super("playermana", new String[]{"mana"});
		RacesAndClasses.getPlugin();
	}
	
	@Override
	public boolean onInternalCommand(CommandSender sender, Command command, String label,
			String[] args) {
		
		if(!(sender instanceof Player)){
			sender.sendMessage(LanguageAPI.translateIgnoreError(only_players)
					.build());
			return true;
		}
		
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer((Player) sender);
		
		double currentMana = racPlayer.getManaManager().getCurrentMana();
		double maxMana = racPlayer.getManaManager().getMaxMana();
		
		String builder = ChatColor.BLUE + "[";
		int length = 20;
		
		double percent = (currentMana / maxMana);
		int filled = (int) (percent * length);
		for( int i = 0; i < filled; i++){
			builder += "|";
		}
		
		builder += ChatColor.LIGHT_PURPLE + "";
		for( int i = filled; i < length; i++){
			builder += "|";
		}
		
		builder += ChatColor.BLUE + "]";
		
		DecimalFormat f = new DecimalFormat("0.0");
		racPlayer.sendMessage(builder + ChatColor.BLUE + "Mana: " + ChatColor.AQUA + f.format(currentMana) 
				+ ChatColor.BLUE + "/" + ChatColor.AQUA + f.format(maxMana));
		return true;
	}

}
