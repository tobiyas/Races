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
/*
 * Races - by tobiyas
 * http://
 *
 * powered by Kickstarter
 */

package de.tobiyas.racesandclasses.listeners.generallisteners;


import static de.tobiyas.racesandclasses.translation.languages.Keys.login_no_race_selected;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.gui.HolderInventory;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.LevelUpEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.PlayerReceiveEXPEvent;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.playermanagement.playerdisplay.scoreboard.PlayerRaCScoreboardManager.SBCategory;
import de.tobiyas.racesandclasses.saving.PlayerSavingData;
import de.tobiyas.racesandclasses.saving.PlayerSavingManager;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithRestrictions;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.bukkit.versioning.CertainVersionChecker;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.schedule.DebugBukkitRunnable;


public class Listener_Player implements Listener {
	private RacesAndClasses plugin;

	public Listener_Player(){
		plugin = RacesAndClasses.getPlugin();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLeave(PlayerQuitEvent event){
		if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			plugin.getChannelManager().playerQuit(RaCPlayerManager.get().getPlayer(event.getPlayer()));
		}
	}
	 

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent event){
		System.out.println("Call Player Join!!");
		
		final RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		boolean racesActive = plugin.getConfigManager().getGeneralConfig().isConfig_enableRaces();
		
		//Loads the Container:
		plugin.getPlayerManager().getContainer(player);
		
		RaceContainer container = player.getRace();
		if((container == null || container == plugin.getRaceManager().getDefaultHolder()) && racesActive){
			String defaultName = plugin.getConfigManager().getGeneralConfig().getConfig_takeRaceWhenNoRace();
			if(defaultName == null || defaultName.isEmpty()) {
				defaultName = Consts.defaultRace;
				LanguageAPI.sendTranslatedMessage(player, login_no_race_selected);
			}
			
			plugin.getRaceManager().addPlayerToHolder(player, defaultName, true);
		}
		
		//just to load them.
		if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			plugin.getChannelManager().playerLogin(player);
		}
		
		if(racesActive && plugin.getConfigManager().getGeneralConfig().isConfig_adaptListName()){
			RaceContainer racContainer = player.getRace();
			if(racContainer != null) racContainer.editTABListEntry(player);
		}
		
		boolean forceSelectOfRace = plugin.getConfigManager().getGeneralConfig().isConfig_openRaceSelectionOnJoinWhenNoRace();
		boolean playerHasNoRace = player.getRace() == null || player.getRace() == plugin.getRaceManager().getDefaultHolder();
		int scheduledTimeToOpen = plugin.getConfigManager().getGeneralConfig().getConfig_debugTimeAfterLoginOpening();
		
		if(playerHasNoRace && forceSelectOfRace && racesActive){
			final HolderInventory raceInv = new HolderInventory(player.getPlayer(), plugin.getRaceManager());
			if(raceInv.getNumberOfHolder() > 0){
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@Override
					public void run() {
						if(player.isOnline()) player.getPlayer().openInventory(raceInv);
					}
				}, scheduledTimeToOpen * 20);
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoinCheck(PlayerLoginEvent event){
		checkPlayer(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoinCheck(PlayerJoinEvent event){
		checkPlayer(event.getPlayer());
	}
	
	
	private void checkPlayer(Player player){
		final RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		PlayerSavingData data = PlayerSavingManager.get().getPlayerData(player.getUniqueId());
		
		plugin.getRaceManager().getHolderOfPlayer(racPlayer);
		plugin.getClassManager().getHolderOfPlayer(racPlayer);
		
		//Inits the Player after start!
		plugin.getPlayerManager().getContainer(racPlayer).init();
		
		data.setLastLogin(System.currentTimeMillis());
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event){
		if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			World oldWorld = event.getFrom();
			plugin.getChannelManager().playerChangedWorld(oldWorld, RaCPlayerManager.get().getPlayer(event.getPlayer()));
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChatEarly(AsyncPlayerChatEvent event){
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()) return;
		String orgMsg = event.getMessage();
		RaCPlayer player = RaCPlayerManager.get().getPlayer(event.getPlayer());
		
		//check if we have a fake command.
		if(orgMsg != null && orgMsg.length() > 0 && orgMsg.charAt(0) == '/') return;
		
		String channel = player.getCurrentChatChannel();
		if(!plugin.getChannelManager().isMember(player, channel)){
			player.sendMessage(ChatColor.RED + "You are writing in a channel, you don't have access to. Please change your channel with " + 
								ChatColor.LIGHT_PURPLE + "/channel change" + ChatColor.YELLOW + " [channelname]");
			
			event.setCancelled(true);
			return;
		}
		
		plugin.getChannelManager().editToChannel(channel, event);
	}
	

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChatLate(AsyncPlayerChatEvent event){
		if(!plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()) return;
		
		String message = event.getMessage();
		if(message != null && message.length() > 0 && message.charAt(0) == '/') return;
		
		String format = event.getFormat();
		format = format.replace("{msg}", "%2$s");
		
		event.setFormat(format);
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void playerGotEXPEvent(PlayerReceiveEXPEvent event){
		if(event.isCancelled() || event.getExp() <= 0) return;
		
		final RaCPlayer player = event.getRaCPlayer();
		
		//schedule to 1 tick in future.
		new DebugBukkitRunnable("1TickFutureScoreboardScheduler") {
			@Override
			protected void runIntern() {
				player.getScoreboardManager().updateSelectAndShow(SBCategory.General, 1000 * 4);
			}
		}.runTaskLater(plugin, 1);
	}
	
	
	@EventHandler
	public void playerLevelUpEvent(LevelUpEvent event){
		int oldLevel = event.getFromLevel();
		int newLevel = event.getToLevel();
		
		if(newLevel < oldLevel) return;
		
		Player player = event.getPlayer();
		//just to be sure.
		if(player == null || !player.isOnline()) return;
		RaCPlayer racPlayer = RaCPlayerManager.get().getPlayer(player);
		
		
		//Recheck max Health / Mana:
		racPlayer.getHealthManager().rescanPlayer();
		racPlayer.getHealthManager().checkMaxHealth();
		
		//Recheck Mana
		racPlayer.getManaManager().rescanPlayer();
		
		//Resync Spells
		racPlayer.getSpellManager().rescan();
		
		
		if(plugin.getConfigManager().getGeneralConfig().isConfig_use_levelup_message()){
			LanguageAPI.sendTranslatedMessage(racPlayer, Keys.level_up_message, 
					"player", player.getDisplayName(), 
					"level", String.valueOf(newLevel));
			
			//check for new Skills and tell.
			for(Trait trait : racPlayer.getTraits()){
				if(trait instanceof TraitWithRestrictions){
					TraitWithRestrictions twrTrait = (TraitWithRestrictions) trait;
					if(!twrTrait.isInLevelRange(oldLevel) && twrTrait.isInLevelRange(newLevel)){
						LanguageAPI.sendTranslatedMessage(racPlayer, Keys.level_up_new_skill_message, 
								"player", player.getDisplayName(),
								"level", String.valueOf(newLevel),
								"traitname", trait.getDisplayName());
					}
				}
			}
		}

		//Spawn firework if MC > 1.7 and wanted.
		if(plugin.getConfigManager().getGeneralConfig().isConfig_use_fireworks_on_level_up()
				&& CertainVersionChecker.isAbove1_7()) {
			
			for(int i = 0; i < 4; i++){
				Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
				fw.setFireworkMeta(randomizeFireworkMeta(fw.getFireworkMeta()));
			}
		}
		
	}
	
	/**
	 * The Random Generator to use.
	 */
	private final Random random = new Random();
	
	
	/**
	 * Randomizes the Firework Meta.
	 * 
	 * @param meta to randomize.
	 */
	private FireworkMeta randomizeFireworkMeta(FireworkMeta meta){
		int rt = random.nextInt(5) ;
        
        Type type = Type.BALL;      
        if (rt == 0) type = Type.BALL;
        if (rt == 1) type = Type.BALL_LARGE;
        if (rt == 2) type = Type.BURST;
        if (rt == 3) type = Type.CREEPER;
        if (rt == 4) type = Type.STAR;
       
        //Get our random colours 
        Color color1 = Color.fromBGR(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        Color color2 = Color.fromBGR(random.nextInt(255), random.nextInt(255), random.nextInt(255));
       
        //Create our effect with this
        FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(color1).withFade(color2).with(type).trail(random.nextBoolean()).build();
       
        //Then apply the effect to the meta
        meta.addEffect(effect);
       
        //Generate some random power and set it
        meta.setPower(0);
        
        return meta;
	}

}
