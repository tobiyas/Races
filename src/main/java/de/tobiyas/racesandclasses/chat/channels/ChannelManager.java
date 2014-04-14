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
package de.tobiyas.racesandclasses.chat.channels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.chat.channels.container.ChannelContainer;
import de.tobiyas.racesandclasses.chat.channels.container.ChannelInvalidException;
import de.tobiyas.racesandclasses.chat.channels.container.ChannelTicker;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.util.chat.ChannelLevel;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;

public class ChannelManager {
	
	/**
	 * plugin main
	 */
	private RacesAndClasses plugin;
	
	/**
	 * All channels saved as Map String -> {@link ChannelContainer}
	 */
	private Map<String, ChannelContainer> channels;
	
	/**
	 * The ConfigTotal to save the channel settings in
	 */
	private YAMLConfigExtended config;

	
	/**
	 * This constructor is for Testing
	 * 
	 * @param config to load from and save to
	 */
	protected ChannelManager(YAMLConfigExtended config){
		this.config = config;
		this.plugin = RacesAndClasses.getPlugin();
		this.channels = new HashMap<String, ChannelContainer>();
	}
	
	
	/**
	 * Creates the ChannelManager
	 */
	public ChannelManager(){
		plugin = RacesAndClasses.getPlugin();
		channels = new HashMap<String, ChannelContainer>();
		config = new YAMLConfigExtended(Consts.channelsYML);
	}
	
	/**
	 * Inits the Channel system.
	 * It creates all Channels + the ticker for timings.
	 */
	public void init(){
		channels.clear();
		ChannelTicker.init();
		loadChannelsFromFile();
		createSTDChannels();
	}
	
	/**
	 * Creates all channels given by default (races / local / global / tutorials)
	 */
	private void createSTDChannels(){
		registerChannel(ChannelLevel.GlobalChannel, "Global");
		registerChannel(ChannelLevel.LocalChannel, "Local");
		registerChannel(ChannelLevel.GlobalChannel, "Tutorial");
		
		for(World world : Bukkit.getWorlds()){
			registerChannel(ChannelLevel.WorldChannel, world.getName());
		}
		
		for(String race : plugin.getRaceManager().listAllVisibleHolders()){
			if(race.equalsIgnoreCase(Consts.defaultRace)) continue;
			registerChannel(ChannelLevel.RaceChannel, race);
		}
	}
	
	/**
	 * Registers a new Channel with the ChannelLevel passed
	 * 
	 * @param level to create with
	 * @param channelName to create with
	 * @return the {@link ChannelContainer} created, null otherwise
	 */
	public ChannelContainer registerChannel(ChannelLevel level, String channelName){
		ChannelContainer container = getContainer(channelName);
		if(container != null){
			return null;
		}
		
		try{
			container = new ChannelContainer(channelName, level);
			channels.put(channelName, container);
			return container;
		}catch(ChannelInvalidException exp){
			//tried to do a RaceChannel that did not work...
			return null;
		}
	}
	
	/**
	 * Registers a Channel with a player in it.
	 * The player is notified to errors.
	 * 
	 * @param level to register
	 * @param channelName to register
	 * @param player to pass into the channel + send errors if failed
	 * 
	 * @return true if worked, false otherwise
	 */
	public boolean registerChannel(ChannelLevel level, String channelName, UUID player){
		ChannelContainer container = registerChannel(level, channelName);
		if(container == null){
			Player realplayer = Bukkit.getPlayer(player);
			if(realplayer != null){
				realplayer.sendMessage(ChatColor.RED + "Channel: " + ChatColor.AQUA + channelName + ChatColor.RED + " already exists.");
			}
			
			return false;
		}
		
		container.addPlayerToChannel(player, "", true);
		return true;
	}
	
	/**
	 * Registers a Channel with a player in it.
	 * The player is notified to errors.
	 * 
	 * @param channelLevel
	 * @param channelName
	 * @param channelPassword
	 * @param player
	 */
	public boolean registerChannel(ChannelLevel channelLevel, String channelName, String channelPassword, UUID player) {
		boolean worked = registerChannel(channelLevel, channelName, player);
		if(worked){
			ChannelContainer container = getContainer(channelName);
			container.setAdmin(player);
			container.setPassword(channelPassword);
			
			Player realPlayer = Bukkit.getPlayer(player);
			if(realPlayer != null){
				realPlayer.sendMessage(ChatColor.GREEN + "The channel " + ChatColor.AQUA + channelName + 
								ChatColor.GREEN + " has been created successfully");
			}
		}
		
		return worked;
	}
	
	/**
	 * Creates an empty Channel file if none exists
	 */
	private void createStructure(){
		File file = config.getFileLoadFrom();
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				plugin.log("Could not create " + Consts.channelsYML);
				return;
			}
			config = new YAMLConfigExtended(Consts.channelsYML).load();
			
			if(!config.contains("channel")){
				config.createSection("channel");
			}
			
			if(!config.contains("channel." + ChannelLevel.PasswordChannel.name())){
				config.createSection("channel." + ChannelLevel.PasswordChannel.name());
			}
			
			if(!config.contains("channel." + ChannelLevel.PrivateChannel.name())){
				config.createSection("channel." + ChannelLevel.PrivateChannel.name());
			}
			
			if(!config.contains("channel." + ChannelLevel.PublicChannel.name())){
				config.createSection("channel." + ChannelLevel.PublicChannel.name());
			}
			
			config.save();
		}
	}
	
	/**
	 * Loads all Channels from the Channel file
	 */
	private void loadChannelsFromFile(){
		createStructure();
		config.load();
		for(ChannelLevel level : ChannelLevel.values()){
			for(String channelName : config.getChildren("channel." + level.name())){
				try{
					ChannelContainer container = ChannelContainer.constructFromYml(config, channelName, level);
					if(container.getChannelLevel() == ChannelLevel.WorldChannel && Bukkit.getWorld(container.getChannelName()) == null){
						//never load worlds that do not exist any more.
						continue;
					}
					
					if(container != null){
						channels.put(channelName, container);
					}
				}catch(ChannelInvalidException exp){
					continue;
				}
			}
		}
	}
	
	/**
	 * Saves all channels to Channel file.
	 * Note: Channels can save themselves
	 */
	public void saveChannels(){
		for(String channelName : channels.keySet()){
			ChannelContainer container = getContainer(channelName);
			container.saveChannel(config);
		}
	}
	
	/**
	 * Sends an Broadcast message to the channel.
	 * This can be sent from an Player or from the Console (player = null)
	 * 
	 * @param channel
	 * @param sender
	 * @param message
	 * 
	 * @return true if worked, false if channel not found
	 */
	public boolean broadcastMessageToChannel(String channel, CommandSender sender,
			String message) {
		
		ChannelContainer container = getContainer(channel);
		if(container == null){
			if(sender != null){
				sender.sendMessage(ChatColor.RED + "Channel " + ChatColor.AQUA + channel + ChatColor.RED + " was not found.");
			}
			return false;
		}
		
		container.sendMessageInChannel(sender, message, "");
		return true;		
	}
	
	/**
	 * Returns a list of all Channel names present
	 * 
	 * @return
	 */
	public List<String> listAllChannels(){
		ArrayList<String> channelList = new ArrayList<String>();
		for(String channel : channels.keySet()){
			channelList.add(channel);
		}
		return channelList;
	}
	
	/**
	 * Returns a list of all public Channel names present
	 * 
	 * @return
	 */
	public List<String> listAllPublicChannels() {
		ArrayList<String> channelList = new ArrayList<String>();
		for(String channel : channels.keySet()){
			ChannelContainer container = getContainer(channel);
			ChannelLevel level = container.getChannelLevel();
			if(level == ChannelLevel.PasswordChannel || level == ChannelLevel.PrivateChannel){
				continue;
			}
			
			channelList.add(channel);
		}
		return channelList;
	}
	
	/**
	 * Posts the channel info to a player.
	 * 
	 * @param sender
	 * @param channel
	 */
	public void postChannelInfo(CommandSender sender, String channel) {
		ChannelContainer container = getContainer(channel);
		if(container == null){
			sender.sendMessage(ChatColor.RED + "Channel " + ChatColor.AQUA + channel + ChatColor.RED + " could not be found.");
			return;
		}
		
		container.postInfo(sender);
	}
	
	/**
	 * Returns the Level of a channel according to it's name.
	 * Returns {@link ChannelLevel#NONE} if the channel was not found.
	 * 
	 * @param channel
	 * @return
	 */
	public ChannelLevel getChannelLevel(String channel){
		if(channel == null) return ChannelLevel.NONE;
		
		for(String containerName : channels.keySet()){
			if(containerName == null) continue;
			
			ChannelContainer container = getContainer(containerName);
			if(container.getChannelName().equalsIgnoreCase(channel)){
				return container.getChannelLevel();
			}
		}
		
		return ChannelLevel.NONE;
	}
	
	/**
	 * Joins the player to the channel.
	 * If a password is required, it is checked here.
	 * <br>
	 * If notify flag is set, other players in the channel will be informed about this.
	 * 
	 * @param player to join
	 * @param channelName to join
	 * @param password needed for the channel (if it has one, ignored otherwise)
	 * @param notify if the player join should be notified.
	 */
	public void joinChannel(UUID player, String channelName, String password, boolean notify) {
		ChannelContainer container = getContainer(channelName);
		if(container == null)
			return;
		
		container.addPlayerToChannel(player, password, notify);	
	}
	
	/**
	 * passes the ChannelContainer associated with a channel Name.
	 * 
	 * @param channelName
	 * @return
	 */
	private ChannelContainer getContainer(String channelName){
		if(channelName == null) return null;
		
		for(String name : channels.keySet()){
			if(channelName.equalsIgnoreCase(name)){
				return channels.get(name);
			}
		}
		return null;
	}
	
	/**
	 * Removes a player from a channel.
	 * <br>
	 * If the notify flag is set, other players in the channel will be notified.
	 * 
	 * @param player
	 * @param channelName
	 * @param notify
	 */
	public void leaveChannel(UUID player, String channelName, boolean notify) {
		ChannelContainer container = getContainer(channelName);
		if(container == null){
			if(notify){
				Player realplayer = Bukkit.getPlayer(player);
				if(realplayer != null) realplayer.sendMessage(ChatColor.RED + "The Channel: " + channelName + " does not exist.");
			}
			return;
		}
		
		container.removePlayerFromChannel(player, notify);
	}
	
	/**
	 * Checks if a Player (as id) is present in a channel.
	 * 
	 * @param playerUUID
	 * @param channelName
	 * @return if is present, false otherwise
	 */
	public boolean isMember(UUID player, String channelName) {
		ChannelContainer container = getContainer(channelName);
		if(container == null){
			return false;
		}
		
		if(container.getChannelLevel() == ChannelLevel.LocalChannel){
			return true;
		}
		
		return container.isMember(player);
	}
	
	/**
	 * Mutes a Player in a channel.
	 * The time passed is the time in Seconds the player is muted.
	 * <br>
	 * NOTE: The muting player needs Admin previleges in the channel.
	 * 
	 * @param admin
	 * @param mutedName
	 * @param channelName
	 * @param time
	 */
	public void mutePlayer(UUID admin, UUID mutedUUID, String channelName, int time){
		Player realPlayer = Bukkit.getPlayer(admin);
		ChannelContainer container = getContainer(channelName);
		if(container == null){
			if(realPlayer != null) realPlayer.sendMessage(ChatColor.RED + "Channel not found.");
			return;
		}
		
		if(!container.checkPermissionMute(realPlayer)){
			if(realPlayer != null) realPlayer.sendMessage(ChatColor.RED + "You don't have the permission to do this.");
			return;
		}
		
		if(!container.isMember(mutedUUID)){
			if(realPlayer != null) realPlayer.sendMessage(ChatColor.RED + "There is no player with this name in this channel.");
			return;
		}
		
		if(container.isMuted(mutedUUID)){
			if(realPlayer != null) realPlayer.sendMessage(ChatColor.RED + "The Player is already muted.");
			return;
		}
		
		container.mutePlayer(mutedUUID, time);
		broadcastMessageToChannel(channelName, null, " Player: " + mutedUUID + " got muted by: " + realPlayer.getDisplayName());
	}
	
	/**
	 * Unmutes a player in a channel.
	 * <br>
	 * Note: The unmuteing player needs Admin privileges in this channel. 
	 * 
	 * @param admin
	 * @param unmutedUUID
	 * @param channelName
	 */
	public void unmutePlayer(UUID admin, UUID unmutedUUID, String channelName){
		Player realPlayer = Bukkit.getPlayer(admin);
		ChannelContainer container = getContainer(channelName);
		if(container == null){
			if(realPlayer != null) realPlayer.sendMessage(ChatColor.RED + "Channel not found.");
			return;
		}
		
		if(!container.checkPermissionUnmute(realPlayer)){
			if(realPlayer != null) realPlayer.sendMessage(ChatColor.RED + "You don't have the permission to do this.");
			return;
		}
		
		if(!container.isMember(unmutedUUID)){
			if(realPlayer != null) realPlayer.sendMessage(ChatColor.RED + "There is no player with this name in this channel.");
			return;
		}
		
		if(!container.isMuted(unmutedUUID)){
			if(realPlayer != null) realPlayer.sendMessage(ChatColor.RED + "No player with this name is muted.");
			return;
		}
		
		container.unmutePlayer(unmutedUUID);
		broadcastMessageToChannel(channelName, null, " Player: " + unmutedUUID + " got unmuted by: " + realPlayer.getDisplayName());
	}
	
	/**
	 * Bans a Player in a channel.
	 * The time passed is the time in Seconds the player is banned.
	 * <br>
	 * NOTE: The banning player needs Admin previleges in the channel.
	 * 
	 * @param admin
	 * @param banName
	 * @param channelName
	 * @param time
	 */
	public void banPlayer(UUID admin, UUID banName, String channelName, int time){
		Player realPlayer = Bukkit.getPlayer(admin);
		ChannelContainer container = getContainer(channelName);
		if(container == null){
			if(realPlayer != null) realPlayer.sendMessage(ChatColor.RED + "Channel not found.");
			return;
		}
		
		if(!container.checkPermissionBann(realPlayer)){
			if(realPlayer != null) realPlayer.sendMessage(ChatColor.RED + "You don't have the permission to do this.");
			return;
		}
		
		if(!container.isMember(banName)){
			if(realPlayer != null) realPlayer.sendMessage(ChatColor.RED + "There is no player with this name in this channel.");
			return;
		}
		
		if(container.isBanned(banName)){
			if(realPlayer != null) realPlayer.sendMessage(ChatColor.RED + "This Player is already banned from the channel.");
			return;
		}
		
		broadcastMessageToChannel(channelName, null, " Player: " + banName + " got baned from channel by: " + realPlayer.getDisplayName());
		container.banAndRemovePlayer(banName, time);
	}
	
	/**
	 * Unbans a player in a channel.
	 * <br>
	 * Note: The unbanning player needs Admin privileges in this channel. 
	 * 
	 * @param admin
	 * @param unbanName
	 * @param channelName
	 */
	public void unbanPlayer(UUID admin, UUID unbanUUID, String channelName){
		Player realPlayer = Bukkit.getPlayer(admin);
		ChannelContainer container = getContainer(channelName);
		if(container == null){
			if(realPlayer != null) realPlayer.sendMessage(ChatColor.RED + "Channel not found.");
			return;
		}
		
		if(!container.checkPermissionMute(realPlayer)){
			if(realPlayer != null) realPlayer.sendMessage(ChatColor.RED + "You don't have the permission to do this.");
			return;
		}
		
		if(!container.isBanned(unbanUUID)){
			if(realPlayer != null) realPlayer.sendMessage(ChatColor.RED + "No player with this name is banned.");
			return;
		}
		
		container.unbanPlayer(unbanUUID);
		//broadcastMessageToChannel(channelName, null, " Player: " + unbanName + " got unbaned by: " + admin.getDisplayName());
		if(realPlayer != null) realPlayer.sendMessage(ChatColor.GREEN + "Player: " + ChatColor.LIGHT_PURPLE +  unbanUUID + ChatColor.GREEN +
							" got unbanned from channel: " + ChatColor.LIGHT_PURPLE + channelName);
		
		Player player = Bukkit.getPlayer(unbanUUID);
		if(player != null){
			player.sendMessage(ChatColor.GREEN + "You got unbanned from channel: " + ChatColor.LIGHT_PURPLE + channelName);
		}
	}
	
	/**
	 * Removes a channel from the Channel list.
	 * 
	 * @param channelName2
	 */
	public void removeChannel(String channelName) {
		ChannelContainer container = getContainer(channelName);
		if(container == null){
			return;
		}
		
		ChannelTicker.unregisterChannel(container);
		
		config.load();
		config.set("channel." + container.getChannelLevel() + "." + channelName, 1);
		config.set("channel." + container.getChannelLevel() + "." + channelName, null);
		config.save();
		
		channels.remove(channelName);
	}
	
	/**
	 * Notifies all players that a player has joined
	 * 
	 * @param player
	 */
	public void playerLogin(UUID playerUUID){
		Player player = Bukkit.getPlayer(playerUUID);
		
		this.joinChannel(playerUUID, "Global", "", true);
		this.joinChannel(playerUUID, player.getWorld().getName(), "", false);
		
		AbstractTraitHolder container = plugin.getRaceManager().getHolderOfPlayer(playerUUID);
		if(container != null){
			this.joinChannel(playerUUID, container.getName(), "", false);
		}
	}

	/**
	 * Notifies all players that a player has left
	 * 
	 * @param player
	 */
	public void playerQuit(UUID playerUUID) {
		Player player = Bukkit.getPlayer(playerUUID);

		this.leaveChannel(playerUUID, "Global", true);
		this.leaveChannel(playerUUID, player.getWorld().getName(), false);
		
		AbstractTraitHolder container = plugin.getRaceManager().getHolderOfPlayer(playerUUID);
		if(container != null){
			this.leaveChannel(playerUUID, container.getName(), false);
		}
	}

	/**
	 * Changes the world channels for a player
	 * 
	 * @param oldWorld
	 * @param player
	 */
	public void playerChangedWorld(World oldWorld, UUID playerUUID) {
		Player player = Bukkit.getPlayer(playerUUID);
		World newWorld = player.getWorld();
		
		boolean notify = plugin.getConfigManager().getGeneralConfig().isConfig_disableChatJoinLeaveMessages();
		leaveChannel(playerUUID, oldWorld.getName(), notify);
		joinChannel(playerUUID, newWorld.getName(), "", notify);
	}
	
	
	/**
	 * Changes the race channel if a player changes his race
	 * 
	 * @param oldRace
	 * @param player
	 */
	public void playerLeaveRace(String oldRace, UUID player){
		if(player == null) return;
		
		AbstractTraitHolder container = plugin.getRaceManager().getHolderByName(oldRace);
		if(container == null) return;
		
		String raceToLeave = container.getName();		
		leaveChannel(player, raceToLeave, true);
	}
	
	/**
	 * Changes the race channel if a player changes his race
	 * 
	 * @param oldRace
	 * @param player
	 */
	public void playerJoinRace(String newRace, UUID player){
		if(player == null) return;
		
		AbstractTraitHolder container = plugin.getRaceManager().getHolderByName(newRace);
		if(container == null) return;
		
		String raceToLeave = container.getName();		
		joinChannel(player, raceToLeave, "", true);
	}

	/**
	 * Edits the properties of a channel.
	 * <br>
	 * Only specific properties are present.
	 * Look at {@link ChannelContainer} to see the properties.
	 * 
	 * @param player
	 * @param channel
	 * @param property
	 * @param newValue
	 */
	public void editChannel(UUID playerUUID, String channel, String property,
			String newValue) {
		ChannelContainer container = getContainer(channel);
		if(container == null)
			return;
		
		container.editChannel(playerUUID, property, newValue);
	}


	/**
	 * Edits the Event to the channel passed.
	 * 
	 * @param channel to edit to
	 * @param event to edit
	 */
	public void editToChannel(String channel, AsyncPlayerChatEvent event) {
		//only format messages that have a message
		String format = event.getFormat();
		if(format.contains("%1$s") && format.contains("%2$s")){
			Player sender = event.getPlayer();
			
			ChannelContainer container = getContainer(channel);
			if(container == null){
				if(sender != null){
					sender.sendMessage(ChatColor.RED + "Channel " + ChatColor.AQUA + channel + ChatColor.RED + " was not found.");
					event.setCancelled(true);
				}
				
				return;
			}
			
			container.editEvent(event);
		}
	}
	
}
