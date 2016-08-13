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
package de.tobiyas.racesandclasses.commands.skilltree;

import static de.tobiyas.racesandclasses.translation.languages.Keys.only_players;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.gui.SkillTreeGui;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_SkillTree extends AbstractCommand {

	private RacesAndClasses plugin;
	
	public CommandExecutor_SkillTree(){
		super("skilltree", new String[]{"skt"});
		plugin = RacesAndClasses.getPlugin();
		this.description = "Opens the SkillTree GUI";
	}
	
	
	@Override
	public boolean onInternalCommand(CommandSender sender, Command command, String label,
			String[] args) {
		
		//Clears the Points:
		if(args.length == 2 && args[0].equals("clear") && sender.hasPermission(PermissionNode.skillTreeClear)){
			RaCPlayer player = RaCPlayerManager.get().getPlayerByName(args[1]);
			if(player == null){
				LanguageAPI.sendTranslatedMessage(sender, Keys.player_not_exist, "player", args[1]);
				return true;
			}
			
			//Clear and rescan!
			player.getSkillTreeManager().clearSkills();
			player.getSpellManager().rescan();
			player.getArrowManager().rescanPlayer();
			
			sender.sendMessage(ChatColor.GREEN + "Cleared.");
			return true;
		}
		
		//If no player -> return.
		if(!(sender instanceof Player)){
			sender.sendMessage(LanguageAPI.translateIgnoreError(only_players).build());
			return true;
		}
		
		
		//If disabled -> send message!
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_useSkillSystem()){
			LanguageAPI.sendTranslatedMessage(sender, Keys.something_disabled, "value", "SkillTree");
			return true;
		}
		
		Player player = (Player) sender;
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		
		Collection<Trait> traits = TraitHolderCombinder.getAllTraitsOfPlayer(racPlayer);
		boolean hasAnySkillable = false;
		for(Trait trait : traits){
			if(!trait.isPermanentSkill() && trait.isVisible() && trait.getSkillPointCost(1) > 0) {
				hasAnySkillable = true;
			}
		}
		
		//Open gui or send error message:
		if(hasAnySkillable) player.openInventory(new SkillTreeGui(player));
		else LanguageAPI.sendTranslatedMessage(sender, Keys.no_traits);
		
		return true;
	}

}
