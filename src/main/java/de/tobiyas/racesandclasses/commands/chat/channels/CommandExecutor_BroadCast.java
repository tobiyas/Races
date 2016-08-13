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
package de.tobiyas.racesandclasses.commands.chat.channels;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.commands.AbstractCommand;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;

public class CommandExecutor_BroadCast extends AbstractCommand {

	private RacesAndClasses plugin;

	public CommandExecutor_BroadCast(){
		super("globalbroadcast", new String[]{"gbr"});
		plugin = RacesAndClasses.getPlugin();
	}
	
	@Override
	public boolean onInternalCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if(!plugin.getPermissionManager().checkPermissions(sender, PermissionNode.broadcast)) return true;
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			LanguageAPI.sendTranslatedMessage(sender, "something_disabled",
					"value", "Channels");
			return true;
		}
		
		if(args.length == 0){
			LanguageAPI.sendTranslatedMessage(sender, "no_message");
			return true;
		}
		
		String message = "";
		for(String arg : args){
			message += arg + " ";
		}
		
		plugin.getChannelManager().broadcastMessageToChannel("Global", sender, message);
		return true;
	}

	
}
