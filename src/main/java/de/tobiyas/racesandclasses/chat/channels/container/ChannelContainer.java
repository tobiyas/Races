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
package de.tobiyas.racesandclasses.chat.channels.container;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.chat.ChatFormatter;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.tutorial.TutorialStepContainer;
import de.tobiyas.racesandclasses.util.chat.ChannelLevel;
import de.tobiyas.racesandclasses.util.consts.PermissionNode;
import de.tobiyas.racesandclasses.util.tutorial.TutorialState;
import de.tobiyas.util.config.YAMLConfigExtended;

public class ChannelContainer extends Observable{

	private final RacesAndClasses plugin;
	
	private String channelName;
	private ChatFormatter channelFormat;
	
	private String channelPassword;
	private UUID channelAdmin = null;
	
	private ChannelLevel channelLevel;
	
	private ArrayList<UUID> participants;
	
	private MuteContainer muteContainer;
	private BanContainer banContainer;
	
	public ChannelContainer(String channelName, ChannelLevel level) throws ChannelInvalidException{
		plugin = RacesAndClasses.getPlugin();
		this.channelName = channelName;
		this.channelLevel = level;
		
		String prefix = "�f[";
		String suffix =  "�f]";
		
		this.channelPassword = "";
		participants = new ArrayList<UUID>();
		muteContainer = new MuteContainer();
		banContainer = new BanContainer();
		
		adaptFormatingToLevel();
		channelFormat.setPrefix(prefix);
		channelFormat.setSuffix(suffix);
		
		rescanPartitions(null);
		
		ChannelTicker.registerChannel(this);
		if("Tutorial".equalsIgnoreCase(channelName)){
			registerTutorial();
		}
		
		if(this.channelLevel == ChannelLevel.RaceChannel){
			boolean raceExists = plugin.getRaceManager().getHolderByName(this.channelName) != null;
			if(!raceExists){
				throw new ChannelInvalidException();
			}
		}
	}
	
	private ChannelContainer(String channelName, ChannelLevel level, YAMLConfigExtended config){
		participants = new ArrayList<UUID>();
		this.channelLevel = level;
		this.channelName = channelName;
		plugin = RacesAndClasses.getPlugin();
		config.load();
		
		String channelPre = "channel." + level.name() + "." + channelName;
		String prefix = config.getString(channelPre + ".prefix" , "�f[");
		String suffix = config.getString(channelPre + ".suffix" , "�f]");
		
		//Chanel formating
		String channelColor = config.getString(channelPre + ".channelColor", plugin.getConfigManager().getChannelConfig().getConfig_channel_default_color());
		String stdFormat = config.getString(channelPre + ".channelFormat", plugin.getConfigManager().getChannelConfig().getConfig_channel_default_format());
		
		RaceContainer raceContainer = (RaceContainer) plugin.getRaceManager().getHolderByName(channelName);
		boolean forceOverride = channelLevel == ChannelLevel.RaceChannel && raceContainer != null;
		
		if(forceOverride){
			channelColor = raceContainer.getRaceChatColor();
			stdFormat = raceContainer.getRaceChatFormat();
		}
			
		channelPassword = config.getString(channelPre + ".channelPassword", "");
		try{ channelAdmin = UUID.fromString(config.getString(channelPre + ".channelAdmin", null)); }catch(IllegalArgumentException exp){}
		
		muteContainer = new MuteContainer(config, channelPre);
		banContainer = new BanContainer(config, channelPre);
		
		List<String> tempList = config.getStringList(channelPre + ".members");
		for(String uuid : tempList){
			try{ participants.add(UUID.fromString(uuid)); }catch(IllegalArgumentException exp){}
		}
		
		channelFormat = new ChatFormatter(channelName, channelColor, channelLevel, stdFormat);
		channelFormat.setPrefix(prefix);
		channelFormat.setSuffix(suffix);
		
		if(!config.getBoolean(channelPre + ".saveLoad", false))
			rescanPartitions(null);
		
		config.set(channelPre + ".saveLoad", false);
		config.save();
		
		ChannelTicker.registerChannel(this);
		if(channelName.equalsIgnoreCase("Tutorial"))
			registerTutorial();
	}
	
	private void registerTutorial(){
		plugin.getTutorialManager().registerObserver(this);
		this.setChanged();
	}
	
	private void adaptFormatingToLevel(){
		String stdFormat = "";
		String stdColor = "";
		
		switch(channelLevel){
			case GlobalChannel:
				stdColor = plugin.getConfigManager().getChannelConfig().getConfig_globalchat_default_color();
				stdFormat = plugin.getConfigManager().getChannelConfig().getConfig_globalchat_default_format();
				break;
			
			case WorldChannel:
				stdColor = plugin.getConfigManager().getChannelConfig().getConfig_worldchat_default_color();
				stdFormat = plugin.getConfigManager().getChannelConfig().getConfig_worldchat_default_format();
				break;
			
			case RaceChannel:
				RaceContainer raceContainer = (RaceContainer) plugin.getRaceManager().getHolderByName(channelName);
				if(raceContainer == null){
					stdColor = plugin.getConfigManager().getChannelConfig().getConfig_racechat_default_color();
					stdFormat = plugin.getConfigManager().getChannelConfig().getConfig_racechat_default_format();
				}else{
					stdColor = raceContainer.getRaceChatColor();
					stdFormat = raceContainer.getRaceChatFormat();
				}
				break;
				
			case LocalChannel:
				stdColor = plugin.getConfigManager().getChannelConfig().getConfig_localchat_default_color();
				stdFormat = plugin.getConfigManager().getChannelConfig().getConfig_localchat_default_format();
				break;
				
			default:
				stdColor = plugin.getConfigManager().getChannelConfig().getConfig_channel_default_color();
				stdFormat = plugin.getConfigManager().getChannelConfig().getConfig_channel_default_format();
		}
		
		channelFormat = new ChatFormatter(channelName, stdColor, channelLevel, stdFormat);
	}
	
	public void setPassword(String password){
		if(channelLevel == ChannelLevel.PasswordChannel){
			this.channelPassword = password;
		}
	}
	
	protected String getPassword(){
		return channelPassword;
	}
	
	public void banAndRemovePlayer(UUID player, int time){
		banContainer.banPlayer(player, time);
		if(isMember(player)){
			removePlayerFromChannel(player, false);
		}
	}
	
	public void unbanPlayer(UUID player){
		banContainer.unbanPlayer(player);
	}
	
	public void mutePlayer(UUID player, int time){
		muteContainer.mutePlayer(player, time);
	}
	
	public void unmutePlayer(UUID player){
		muteContainer.unmutePlayer(player);
	}
	
	public void setAdmin(UUID player){
		if(!participants.contains(player)){
			participants.add(player);
		}
		
		channelAdmin = player;
	}
	
	public UUID getAdmin(){
		return channelAdmin;
	}
	
	public void saveChannel(YAMLConfigExtended config){
		if(channelLevel == ChannelLevel.LocalChannel) return;
		
		config.load();
		String channelPre = "channel." + channelLevel.name() + "." + channelName;
		config.createSection(channelPre);
		config.set(channelPre + ".prefix" , channelFormat.getPrefix());
		config.set(channelPre + ".suffix" , channelFormat.getSuffix());
		config.set(channelPre + ".channelColor" , channelFormat.getColor());
		config.set(channelPre + ".members" , participants);
		config.set(channelPre + ".channelFormat", channelFormat.getFormat());
		config.set(channelPre + ".channelPassword", channelPassword);
		config.set(channelPre + ".channelAdmin", channelAdmin);
		
		config.set(channelPre + ".saveLoad", true);
		
		banContainer.saveContainer(config, channelPre);
		muteContainer.saveContainer(config, channelPre);
		config.save();
	}
	
	public static ChannelContainer constructFromYml(YAMLConfigExtended config, String channelName, ChannelLevel level) throws ChannelInvalidException{
		try{
			return new ChannelContainer(channelName, level, config);
		}catch(Exception exp){
			throw new ChannelInvalidException();
		}
	}
	
	public void addPlayerToChannel(UUID playerUUID, String password, boolean notify){
		Player realPlayer = Bukkit.getPlayer(playerUUID);
		
		if(participants.contains(playerUUID)){ 
			realPlayer.sendMessage(ChatColor.RED + "You are already member of this channel.");
			return;
		}
		
		int isBanned = banContainer.isBanned(playerUUID);
		if(isBanned != -1){
			String time = getTimeString(isBanned);
			realPlayer.sendMessage(ChatColor.RED + "You are banned from this channel for: " + ChatColor.LIGHT_PURPLE + time);
			return;
		}
		
		if(channelLevel == ChannelLevel.RaceChannel){
			AbstractTraitHolder container = plugin.getRaceManager().getHolderOfPlayer(playerUUID);
			
			if(container == null || !container.getName().equalsIgnoreCase(channelName)){
				realPlayer.sendMessage(ChatColor.RED + "You don't belong to this race.");
				return;
			}
		}
		
		if(channelLevel == ChannelLevel.WorldChannel){
			if(!realPlayer.getWorld().getName().equalsIgnoreCase(channelName)){
				realPlayer.sendMessage(ChatColor.RED + "You are not on this world.");
				return;
			}
		}
		
		if(!channelPassword.equals("")){
			if(!password.equals(channelPassword)){
				if(realPlayer != null)
					realPlayer.sendMessage(ChatColor.RED + "Wrong password.");
				return;
			}
		}
			
		participants.add(playerUUID);
		if(realPlayer != null){
			if(notify){
				String joinMessage = plugin.getConfigManager().getChannelConfig().getConfig_PlayerJoinFormat();
				sendMessageInChannel(realPlayer, "", joinMessage);
			}
		}
		
		this.notifyObservers(new TutorialStepContainer(playerUUID, TutorialState.channels, 2));
		this.setChanged();
	}
	
	public void removePlayerFromChannel(UUID player, boolean notify){
		Player realPlayer = Bukkit.getPlayer(player);
		if(!participants.contains(player) && realPlayer != null){
			realPlayer.sendMessage(ChatColor.RED + "You are no member of this channel.");
			return;
		}
		
		if(player != null){
			if(notify){
				String leaveMessage = plugin.getConfigManager().getChannelConfig().getConfig_PlayerLeaveFormat();
				sendMessageInChannel(realPlayer, "", leaveMessage);
			}
		}
		
		participants.remove(player);
		if(channelLevel == ChannelLevel.PasswordChannel || channelLevel == ChannelLevel.PrivateChannel || channelLevel == ChannelLevel.PublicChannel){
			if(participants.size() == 0){
				plugin.getChannelManager().removeChannel(this.channelName);
				return;
			}else{
				if(player.equals(channelAdmin)){
					UUID oldChannelAdmin = channelAdmin;
					UUID newChannelAdmin = participants.get(0);
					setAdmin(newChannelAdmin);
					sendMessageInChannel(null, "Channel-Admin changed from: " + ChatColor.RED + oldChannelAdmin + " TO " + newChannelAdmin, "");
				}
			}
		}
		
		this.notifyObservers(new TutorialStepContainer(player, TutorialState.channels, 6));
		this.setChanged();
	}
	
	public ArrayList<UUID> getAllParticipants(){
		return participants;
	}
	
	public void sendMessageInChannel(CommandSender sender, String message, String forceFormat){
		Player player = (sender instanceof Player) ? (Player)sender : null;
		if(channelLevel == ChannelLevel.LocalChannel && sender instanceof Player) rescanPartitions((Player) sender);

		if(player != null){
			int isMuted = muteContainer.isMuted(player.getUniqueId());
			if(isMuted != -1){
				String time = getTimeString(isMuted);				
				sender.sendMessage(ChatColor.RED + "You are muted in this channel for: " + ChatColor.LIGHT_PURPLE + time);
				return;
			}
		}
		
		if(channelName.equalsIgnoreCase("Tutorial") && sender instanceof Player){
			this.notifyObservers(new TutorialStepContainer(player.getUniqueId(), TutorialState.channels, 5));
			this.setChanged();
		}
		
		Set<Player> players = new HashSet<Player>();
		for(UUID playerUUID : participants){
			Player member = Bukkit.getPlayer(playerUUID);
			if(member != null && member.isOnline()){
				players.add(member);
			}
		}
		
		AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(!Bukkit.isPrimaryThread(), player, message, players);
		event.setFormat(forceFormat);
		Bukkit.getPluginManager().callEvent(event);
		
		if(event.isCancelled()) return;
		
		String modifiedMessage = "";
		if(sender == null){
			//TODO fix this: modifiedMessage = modifyMessageToPlayer("CONSOLE", event.getMessage(), event.getFormat());
		}else{
			modifiedMessage = modifyMessageToPlayer(player == null ? null : player.getUniqueId(), event.getMessage(), event.getFormat());
		}
		
		
		for(Player recipient : event.getRecipients()){
			recipient.sendMessage(modifiedMessage);
		}
	}
	
	private void rescanPartitions(Player localPlayer){
		switch(channelLevel){
			case GlobalChannel:
				if(!channelName.equalsIgnoreCase("Global"))
					return;
				
				participants.clear();
				for(Player player : Bukkit.getOnlinePlayers())
					participants.add(player.getUniqueId());
				break;
				
			case WorldChannel:
				participants.clear();
				for(Player player : Bukkit.getWorld(channelName).getPlayers())
					participants.add(player.getUniqueId());
				break;
				
			case RaceChannel:
				participants.clear();
				RaceContainer container = (RaceContainer) plugin.getRaceManager().getHolderByName(channelName);
				if(container == null){
					//channel is not valid any more.
					break;
				}
				
				List<UUID> allPlayersOfRace = plugin.getRaceManager().getAllPlayersOfHolder(container);
				for(UUID player : allPlayersOfRace) {
					participants.add(player);
				}
				break;
				
			case LocalChannel:
				participants.clear();
				if(localPlayer == null) return;
				int distance = plugin.getConfigManager().getChannelConfig().getConfig_localchat_range();
				Location loc = localPlayer.getLocation();
				for(Player tempPlayer : loc.getWorld().getPlayers())
					if(loc.distance(tempPlayer.getLocation()) < distance)
						participants.add(tempPlayer.getUniqueId());
				break;
				
			default: return;
		}
	}
	
	private String modifyMessageToPlayer(UUID player, String message, String forceFormat){		
		return channelFormat.format(player, message, forceFormat, true);
	}
	
	private String getTimeString(int timeInSec){
		String time = "";
		int orgSeconds = timeInSec * 1000;
		int seconds = (int) (orgSeconds / 1000) % 60 ;
		int minutes = (int) ((orgSeconds / (1000*60)) % 60);
		int hours   = (int) ((orgSeconds / (1000*60*60)) % 24);
		
		if(hours != 0)
			time += hours + " hours, ";
		
		if(minutes != 0)
			time += minutes + " minutes, ";
		
		if(seconds != 0)
			time += seconds + " seconds";
		
		if(timeInSec == Integer.MAX_VALUE)
			time = " ever";
		
		return time;
	}
	
	private String decodeColor(String message){
		return message.replaceAll("(&([a-f0-9]))", "�$2");
	}
	
	private String encodeColor(String message){
		return message.replaceAll("(�([a-f0-9]))", "&$2");
	}
	
	public String getChannelName(){
		return channelName;
	}

	public void postInfo(CommandSender sender) {
		Player player = (sender instanceof Player) ? (Player) sender : null;
		
		sender.sendMessage(ChatColor.YELLOW + "ChannelName: " + ChatColor.AQUA + channelName);
		sender.sendMessage(ChatColor.YELLOW + "ChannelLevel: " + ChatColor.AQUA + channelLevel.name());
		sender.sendMessage(ChatColor.YELLOW + "ChannelColor: " + decodeColor(channelFormat.getColor()) + "COLOR");
		sender.sendMessage(ChatColor.YELLOW + "ChannelFormat: " + ChatColor.RESET + encodeColor(channelFormat.getFormat()));
		
		if(player != null && player.getUniqueId().equals(channelAdmin)){
			sender.sendMessage(ChatColor.YELLOW + "ChannelPassword: " + ChatColor.AQUA + channelPassword);
		}else{
			sender.sendMessage(ChatColor.YELLOW + "Has Password: " + ChatColor.AQUA + !channelPassword.equalsIgnoreCase(""));
		}
			
		if(channelAdmin != null){
			sender.sendMessage(ChatColor.YELLOW + "ChannelAdmin: " + ChatColor.AQUA + channelAdmin);
		}
		
		int isMuted = player == null ? 0 : muteContainer.isMuted(player.getUniqueId());
		if(isMuted != -1)
			sender.sendMessage(ChatColor.YELLOW + "Muted for: " + ChatColor.AQUA + getTimeString(isMuted));
		
		int isBanned = player == null ? 0 : banContainer.isBanned(player.getUniqueId());
		if(isBanned != -1)
			sender.sendMessage(ChatColor.YELLOW + "Banned for: " + ChatColor.AQUA + getTimeString(isBanned));
		
		sender.sendMessage(ChatColor.YELLOW + "===== Channel Members: =====");
		String memberString = "";
		if(channelLevel == ChannelLevel.LocalChannel && sender instanceof Player){ 
			rescanPartitions((Player) sender);
		}	
		
		for(UUID member : participants){
			Player playerMember = Bukkit.getPlayer(member);
			if(playerMember == null || !playerMember.isOnline()){
				memberString += ChatColor.RED + "" + member + " (offline), ";
			}else{
				memberString += ChatColor.GREEN + "" + member + ", ";
			}
		}
		if(memberString.length() > 0){
			sender.sendMessage(memberString.substring(0, memberString.length() - 2));
		}else{
			sender.sendMessage(ChatColor.RED + "This channel has currently no Members.");
		}
		
		if(player != null) this.notifyObservers(new TutorialStepContainer(player.getUniqueId(), TutorialState.channels, 3));
		this.setChanged();
	}

	public ChannelLevel getChannelLevel() {
		return channelLevel;
	}

	public boolean isMember(UUID player) {
		return participants.contains(player);
	}

	public void tick() {
		muteContainer.tick();
		banContainer.tick();
	}

	public boolean checkPermissionMute(Player admin) {
		if(channelLevel == ChannelLevel.GlobalChannel || channelLevel == ChannelLevel.RaceChannel || channelLevel == ChannelLevel.WorldChannel || channelLevel == ChannelLevel.LocalChannel){
			return plugin.getPermissionManager().checkPermissionsSilent(admin, PermissionNode.channelGlobalMutePower);
		}
		
		return admin.getUniqueId().equals(this.channelAdmin);
	}
	
	public boolean checkPermissionUnmute(Player admin) {
		if(channelLevel == ChannelLevel.GlobalChannel || channelLevel == ChannelLevel.RaceChannel || channelLevel == ChannelLevel.WorldChannel || channelLevel == ChannelLevel.LocalChannel){
			return plugin.getPermissionManager().checkPermissionsSilent(admin, PermissionNode.channelGlobalUnmutePower);
		}
		
		return admin.getUniqueId().equals(this.channelAdmin);
	}
	
	public boolean checkPermissionBann(Player admin) {
		if(channelLevel == ChannelLevel.GlobalChannel || channelLevel == ChannelLevel.RaceChannel || channelLevel == ChannelLevel.WorldChannel || channelLevel == ChannelLevel.LocalChannel){
			return plugin.getPermissionManager().checkPermissionsSilent(admin, PermissionNode.channelGlobalBanPower);
		}
		
		return admin.getUniqueId().equals(this.channelAdmin);
	}
	
	public boolean checkPermissionUnban(Player admin) {
		if(channelLevel == ChannelLevel.GlobalChannel || channelLevel == ChannelLevel.RaceChannel || channelLevel == ChannelLevel.WorldChannel || channelLevel == ChannelLevel.LocalChannel){
			return plugin.getPermissionManager().checkPermissionsSilent(admin, PermissionNode.channelGlobalUnbanPower);
		}
		
		return admin.getUniqueId().equals(this.channelAdmin);
	}
	
	public boolean isMuted(UUID player){
		return muteContainer.isMuted(player) != -1;
	}
	
	public boolean isBanned(UUID player){
		return banContainer.isBanned(player) != -1;
	}

	/**
	 * Edits a property of the channel.
	 * 
	 * @param playerUUID2
	 * @param property
	 * @param newValue
	 */
	public void editChannel(UUID playerUUID, String property, String newValue) {
		Player player = Bukkit.getPlayer(playerUUID);
		if(channelLevel == ChannelLevel.PasswordChannel || channelLevel == ChannelLevel.PrivateChannel || channelLevel == ChannelLevel.PublicChannel || channelLevel == ChannelLevel.LocalChannel){
			if(!playerUUID.equals(channelAdmin)){
				player.sendMessage(ChatColor.RED + "You must be the channel-admin to edit the channel");
				return;
			}
		}
		
		boolean changed = true;
		String loweredProperty = property.toLowerCase();
		
		if("format".equals(loweredProperty)){
			channelFormat.setFormat(newValue);
		}else
		if("color".equals(loweredProperty)){
			channelFormat.setColor(newValue);
		}else
		if("admin".equals(loweredProperty)){
			changed = changeAdmin(playerUUID, newValue);
		}else
		if("prefix".equals(loweredProperty)){
			channelFormat.setPrefix(newValue);
		}else
		if("suffix".equals(loweredProperty)){
			channelFormat.setSuffix(newValue);
		}else
		if("password".equals(loweredProperty)){
			if(channelLevel == ChannelLevel.PasswordChannel){
				channelPassword = newValue;
			}
		}else{
			changed = false;
		}
		
		if(changed){
			player.sendMessage(ChatColor.LIGHT_PURPLE + property + ChatColor.GREEN + " was changed to: " + ChatColor.LIGHT_PURPLE + newValue);
		}else{
			player.sendMessage(ChatColor.LIGHT_PURPLE + property + ChatColor.RED + " could not be found or your new Argument is invalid.");
			player.sendMessage(ChatColor.RED + "Valid properties are: " + ChatColor.LIGHT_PURPLE + "format, color, prefix, suffix, admin, password");
		}
	}
	
	/**
	 * Changes the Admin of a Channel to a new one.
	 * This is not possible in Password / Private / Public channels.
	 * 
	 * @param oldAdmin, that is removed
	 * @param newAdmin, that is the new Admin
	 * @return
	 */
	private boolean changeAdmin(UUID playerUUID, String newAdmin){
		if(channelLevel == ChannelLevel.PasswordChannel || 
			channelLevel == ChannelLevel.PrivateChannel || 
			channelLevel == ChannelLevel.PublicChannel)
			return false;
		
		UUID newAdminUUID = null;
		boolean isFoundInList = false;
		for(UUID member : participants){
			if(member.equals(newAdmin)){
				isFoundInList = true;
				newAdminUUID = member;
				break;
			}
		}
		
		if(isFoundInList){
			this.sendMessageInChannel(null, "New Admin of this channel is: " + ChatColor.LIGHT_PURPLE + newAdmin, "");
			channelAdmin = newAdminUUID;
			return true;
		}
		
		return false;
	}

	/**
	 * The Event is edited due to the channel.
	 * 
	 * @param event to edit.
	 */
	public void editEvent(AsyncPlayerChatEvent event) {
		List<Player> members = new LinkedList<Player>();
		rescanPartitions(event.getPlayer());
		
		
		for(UUID member : participants){
			Player memberPlayer = Bukkit.getPlayer(member);
			if(memberPlayer != null && memberPlayer.isOnline()) members.add(memberPlayer.getPlayer());
		}
		
		//setting receipients
		event.getRecipients().clear();
		event.getRecipients().addAll(members);
		
		//setting format.
		String format = channelFormat.getFormat();
		format = channelFormat.format(event.getPlayer().getUniqueId(), event.getMessage(), "", false);
		event.setFormat(format);
	}

}
