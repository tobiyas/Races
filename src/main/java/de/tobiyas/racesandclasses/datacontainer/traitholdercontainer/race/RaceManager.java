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
package de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractHolderManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.AbstractTraitHolder;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.DefaultContainer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.PlayerHolderAssociation;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.permissionsettings.PermissionRegisterer;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.race.reminder.RaceReminder;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.HolderSelectedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.AfterRaceChangedEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.holderevent.raceevent.AfterRaceSelectedEvent;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.saving.PlayerSavingData;
import de.tobiyas.racesandclasses.util.consts.Consts;
import de.tobiyas.util.config.YAMLConfigExtended;
import de.tobiyas.util.player.PlayerUtils;

public class RaceManager extends AbstractHolderManager {

	public RaceManager() {
		super(Consts.racesYML, "races");
		new RaceReminder();
	}

	@Override
	public void init() {
		DefaultContainer.createSTDRaces();
		super.init();

		checkForPossiblyWrongInitialized();
	}


	/**
	 * This is needed to check if players are online on reloads or inits.
	 * They would not be affected by join events.
	 */
	private void checkForPossiblyWrongInitialized() {
		Collection<? extends Player> players = PlayerUtils.getOnlinePlayers();

		for (Player bPlayer : players) {
			RaCPlayer player = RaCPlayerManager.get().getPlayer(bPlayer);
			
			RaceContainer container = player.getRace();
			if (container == null) {
				addPlayerToHolder(player, Consts.defaultRace, false);
				container = player.getRace();
			}

			container.editTABListEntry(player);
		}

		for (RaCPlayer player : memberList.keySet())
			if (memberList.get(player) == null) {
				addPlayerToHolder(player, Consts.defaultRace, false);
			}
	}


	@Override
	protected AbstractTraitHolder generateTraitHolder(
			YAMLConfigExtended traitHolderConfig, String holderName) {
		
		return RaceContainer.loadRace(traitHolderConfig, holderName);
	}

	@Override
	protected String getConfigPrefix() {
		return "race";
	}

	@Override
	public AbstractTraitHolder getDefaultHolder() {
		return getHolderByName(Consts.defaultRace);
	}

	@Override
	protected void initDefaultHolder() {
		traitHolderList.add(RaceContainer.generateSTDRace());
	}
	
	@Override
	public boolean changePlayerHolder(RaCPlayer player, String newHolderName, boolean callEvent){
		if(getHolderByName(newHolderName) == null) return false;
		
		String oldRace = getHolderOfPlayer(player).getDisplayName();
		
		PermissionRegisterer.removePlayer(player, getContainerTypeAsString());
		
		memberList.remove(player);
		if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
			plugin.getChannelManager().playerLeaveRace(oldRace, player);
		}
		
		if(addPlayerToHolder(player, newHolderName, false)){
			if(callEvent){
				Event event = generateAfterChangeEvent(player, getHolderByName(newHolderName), getHolderByName(oldRace));
				if(event != null) plugin.fireEventToBukkit(event);
			}
			return true;
		}
		
		return false;
	}
	
	
	@Override
	public boolean addPlayerToHolder(RaCPlayer player, String newHolderName, boolean callEvent){		
		boolean worked = super.addPlayerToHolder(player, newHolderName, callEvent);
		if(worked){
			AbstractTraitHolder holder = getHolderOfPlayer(player);
			if(holder instanceof RaceContainer){
				((RaceContainer)holder).editTABListEntry(player);
			}
			
			if(plugin.getConfigManager().getGeneralConfig().isConfig_channels_enable()){
				plugin.getChannelManager().playerJoinRace(holder.getDisplayName(), player);
			}
		}
		
		return worked;
	}

	@Override
	public String getContainerTypeAsString() {
		return "race";
	}

	@Override
	protected String getCorrectFieldFromDBHolder(
			PlayerHolderAssociation container) {
		return container.getRaceName();
	}

	@Override
	protected void saveContainerToDBField(PlayerHolderAssociation container,
			String name) {
		container.setRaceName(name);
	}
	
	@Override
	protected HolderSelectedEvent generateAfterSelectEvent(RaCPlayer player,
			AbstractTraitHolder newHolder) {
		
		return new AfterRaceSelectedEvent(player.getPlayer(), (RaceContainer)newHolder);
	}


	@Override
	protected AbstractTraitHolder getStartingHolder() {
		String race = plugin.getConfigManager().getGeneralConfig().getConfig_takeRaceWhenNoRace();
		if(race == null || "".equals(race)){
			return getDefaultHolder();
		}
		
		AbstractTraitHolder holder = getHolderByName(race);
		
		return holder != null ? holder : getDefaultHolder();
	}

	@Override
	protected HolderSelectedEvent generateAfterChangeEvent(RaCPlayer player, AbstractTraitHolder newHolder, AbstractTraitHolder oldHolder) {
		return new AfterRaceChangedEvent(player.getPlayer(), (RaceContainer) newHolder, (RaceContainer) oldHolder);
	}
	
	@Override
	protected AbstractTraitHolder getHolder(PlayerSavingData data) {
		return getHolderByName(data.getRaceName());
	}

	@Override
	protected void saveToContainer(PlayerSavingData data, AbstractTraitHolder holder) {
		data.setRaceName(holder.getDisplayName());
	}
	

}
