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
package de.tobiyas.racesandclasses.configuration.global;

import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.chat_channel_enable;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.chat_disable_channel_join_leave_messages;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.chat_race_encryptForOthers;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.chat_whisper_enable;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.classes_cancleGUIExitWhenNoClassPresent_enable;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.classes_change_uplinkInSeconds;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.classes_enable;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.classes_gui_enable;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.classes_openClassSelectionAfterRaceSelectionWhenNoClass_enable;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.classes_permissions_usePermissionsForEachClasses;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.classes_takeClassWhenNoClass;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.classes_useRaceClassSelectionMatrix;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.debug_outputs_enable;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.debug_outputs_errorUpload;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.debug_outputs_writethrough;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.disable_health_modifications;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.general_armor_disableArmorChecking;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.general_convert_database_on_startup;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.general_copyDefaultTraitsOnStartup;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.general_disable_commands;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.gui_also_use_leftclick_in_guis;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.gui_disableAllChatBars;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.gui_scoreboard_disableAllOutputs;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.health_bar_inChat_enable;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.health_defaultHealth;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.keep_max_hp_on_disabled_worlds;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.language_used;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.level_mapExpPerLevelCalculationString;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.level_useLevelSystem;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.magic_wandId;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.metrics_enable;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.races_cancleGUIExitWhenNoRacePresent_enable;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.races_change_uplinkInSeconds;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.races_defaultrace_name;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.races_defaultrace_tag;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.races_display_adaptListName;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.races_drops_enable;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.races_enable;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.races_gui_enable;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.races_openRaceSelectionOnJoinWhenNoRace_enable;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.races_openRaceSelectionOnJoinWhenNoRace_timeToOpenAfterLoginInSeconds;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.races_permissions_usePermissionsForEachRace;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.races_remindDefaultRace_enable;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.races_remindDefaultRace_interval;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.races_takeRaceWhenNoRace;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.tutorials_enable;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.updater_enableAutoUpdates;
import static de.tobiyas.racesandclasses.configuration.global.GeneralConfigFields.worlds_disableOn;
import de.tobiyas.racesandclasses.util.consts.Consts;


public class GeneralConfigText {

	
	/**
	 * The Text of the Global ConfigTotal
	 */
	public static final String GlobalConfigText = ""
			+ "#ConfigTotal for RacesAndClasses\n"
			+ "#TemplateVersion " + Consts.configVersion 
			+ "\n\n"
			
			//Chat
			+ "\n"
			+ "######\n"
			+ "#CHAT#\n"
			+ "######\n"
			+ "\n"
			
			+ "# Tells if the Whispersystem is enabled. \n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ chat_whisper_enable + ": true\n\n"
			
			+ "# Shows if Races should talk with encrypted messages. \n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ "# NOTICE: Not implemented yet!\n"
			+ chat_race_encryptForOthers + ": false\n\n"
			
			+ "# Tells if the Channels System is enabled. \n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ chat_channel_enable + ": true\n\n"
						
			+ "# Disables the channel join / leave messages. \n"
			+ "# boolean: true or false \n"
			+ "# default: false \n"
			+ chat_disable_channel_join_leave_messages + ": false\n\n"
			
			//Health
			+ "\n"
			+ "########\n"
			+ "#HEALTH#\n"
			+ "########\n"
			+ "\n"
			
			+ "# The default Health a player has when he has no Race. \n"
			+ "# This can be a double (number with comma)\n"
			+ "# default: 20 \n"
			+ health_defaultHealth + ": 20\n\n"
			
			+ "# Tells to use a Chat message as health Bar. \n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ health_bar_inChat_enable + ": true\n\n"
			
			+ "# Tells the plugin to NOT use health modifications. \n"
			+ "# boolean: true or false \n"
			+ "# default: false \n"
			+ disable_health_modifications + ": false\n\n"
			
			//DEBUG
			+ "\n"
			+ "#######\n"
			+ "#DEBUG#\n"
			+ "#######\n"
			+ "\n"
			
			+ "# Tells the Plugin to enable Debug outputs. \n"
			+ "# If activating, beware of console spam. \n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ debug_outputs_enable + ": true\n\n"
			
			+ "# Tells the Plugin to upload errors that happened to tobiyas's Error Server. \n"
			+ "# This server is located at: tobiyas.tk . \n"
			+ "# boolean: true or false \n"
			+ "# default: false \n"
			+ debug_outputs_errorUpload + ": false\n\n"
			
			+ "# Tells the Plugin to log all stuff to the console. \n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ debug_outputs_writethrough + ": true\n\n"
			
			//Tutorials
			+ "\n"
			+ "###########\n"
			+ "#TUTORIALS#\n"
			+ "###########\n"
			+ "\n"
			
			+ "# Tells the Plugin if Tutorials should be used. \n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ tutorials_enable + ": true\n\n"

			//Metrics
			+ "\n"
			+ "#########\n"
			+ "#METRICS#\n"
			+ "#########\n"
			+ "\n"
			
			+ "# Tells the Plugin if The Server should participate on Metrics. \n"
			+ "# To get more infos on Metrics, see 'www.mcstats.org'\n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ metrics_enable + ": true\n\n"
			
			//UPDATER
			+ "\n"
			+ "#########\n"
			+ "#UPDATER#\n"
			+ "#########\n"
			+ "\n"
			
			+ "# Tells the Plugin to auto-update when there is a new Release Version of the Plugin. \n"
			+ "# The Updater used is from h31ix.\n"
			+ "# link: 'https://github.com/h31ix/Updater/blob/master/src/net/h31ix/updater/Updater.java'\n"
			+ "# boolean: true or false \n"
			+ "# default: false \n"
			+ updater_enableAutoUpdates + ": false\n\n"
			
			//LANGUAGE
			+ "\n"
			+ "##########\n"
			+ "#LANGUAGE#\n"
			+ "##########\n"
			+ "\n"
			
			+ "# Tells the Plugin which language file to use. \n"
			+ "# The language files are located at: 'plugins/RacesAndClasses/language/'.\n"
			+ "# String representing the language: en, de, es, it ...\n"
			+ "# default: en \n"
			+ "# NOTICE: Not implemented yet.\n"
			+ language_used + ": en\n\n"
			
			//MULTIWORLD
			+ "\n"
			+ "############\n"
			+ "#MULTIWORLD#\n"
			+ "############\n"
			+ "\n"
			
			+ "# All worlds to disable the Traits on. \n"
			+ "# String list: example: [world1337,nonexistingWorld,...]\n"
			+ "# default: [] \n"
			+ worlds_disableOn + ": []\n\n"
			
			+ "# Tells the Plugin to reset the HP on change to disabled world. \n"
			+ "# boolean: true or false\n"
			+ "# default: true \n"
			+ keep_max_hp_on_disabled_worlds + ": true\n\n"
			
			
			//GENERAL
			+ "\n"
			+ "#########\n"
			+ "#GENERAL#\n"
			+ "#########\n"
			+ "\n"
			
			+ "# Copys all Traits to the Trait folder on Plugin loading. \n"
			+ "# If you modify Traits used by the Plugin by code, turn this off.\n"
			+ "# boolean: true or false\n"
			+ "# default: true \n"
			+ general_copyDefaultTraitsOnStartup + ": true\n\n"

			//+ "# This tells the Plugin to use the Build in DB service to save data. \n"
			//+ "# If turned to false, a flat file (yml) will be used. THIS IS VERY SLOW!!!\n"
			//+ "# boolean: true or false\n"
			//+ "# default: true \n"
			//+ general_saving_savePlayerDataToDB + ": false\n\n"
			
			+ "# This tells the plugin to look at conversion at startup.. \n"
			+ "# If turned to false, no conversion of playerdata will be done on startup!\n"
			+ "# boolean: true or false\n"
			+ "# default: false \n"
			+ general_convert_database_on_startup + ": false\n\n"
			
			+ "# This tells the Plugin to NOT check the Armor if turned off. \n"
			+ "# If turned to true, the armor Checking of Races / Classes is Disabled.\n"
			+ "# boolean: true or false\n"
			+ "# default: false \n"
			+ general_armor_disableArmorChecking + ": false\n\n"
			
			+ "# This tells the plugin to NOT register the following commands. \n"
			+ "# This is a list of commands that are disabled for the plugin.\n"
			+ "# String list: [command1,command2,...]\n"
			+ "# default: [] \n"
			+ general_disable_commands + ": []\n\n"
						
			
			//GUI
			+ "\n"
			+ "#####\n"
			+ "#GUI#\n"
			+ "#####\n"
			+ "\n"
			
			+ "# Enables to also use left clicks in Inventory GUIS.\n"
			+ "# boolean: true or false\n"
			+ "# default: false \n"
			+ gui_also_use_leftclick_in_guis + ": false\n\n"

			+ "# Setting this to true will disable all Outputs to scoreboards from the plugin.\n"
			+ "# boolean: true or false\n"
			+ "# default: false \n"
			+ gui_scoreboard_disableAllOutputs + ": false\n\n"
			
			+ "# Setting this to true will disable all Outputs of bars to the chat of the players.\n"
			+ "# boolean: true or false\n"
			+ "# default: false \n"
			+ gui_disableAllChatBars + ": false\n\n"
			
			
			//MAGIC
			+ "\n"
			+ "#######\n"
			+ "#MAGIC#\n"
			+ "#######\n"
			+ "\n"
			
			+ "# Tells the plugin which Item to use as wand (magic spell cast item). \n"
			+ "# int: itemID of the Item OR\n"
			+ "# String: The canonical Name of the Item. See: 'https://github.com/Bukkit/Bukkit/blob/master/src/main/java/org/bukkit/Material.java' \n"
			+ "# default: STICK \n"
			+ magic_wandId + ": STICK\n\n"

			
			//LEVELING
			+ "\n"
			+ "##########\n"
			+ "#LEVELING#\n"
			+ "##########\n"
			+ "\n"
			
			+ "# This is the Generator Polynome of the Level calculation. \n"
			+ "# String: a string representing a polynome \n"
			+ "# {level} will be replaced by the level of calculation.\n"
			+ "# default: '{level} * {level} * {level} * 1000' \n"
			+ "# By default, the the next level needs (level^3) * 1000 exp.\n"
			+ level_mapExpPerLevelCalculationString + ": '{level} * {level} * {level} * 1000'\n\n"

			+ "# This tells the Plugin which level system to use. \n"
			+ "# It can use one of the following:.\n"
			+ "# - RaC: Races and Classes leveling System (currently not usable).\n"
			+ "# - MC: Minecraft Levels.\n"
			+ "# - SkillAPI: Leveling System from SkillAPI.\n"
			+ "# String: RaC or MC or SkillAPI\n"
			+ "# default: true \n"
			+ level_useLevelSystem + ": RaC\n\n"

			
			//RACES
			+ "\n"
			+ "#######\n"
			+ "#RACES#\n"
			+ "#######\n"
			+ "\n"
			
			+ "# Tells the plugin to use the Race System. \n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ races_enable + ": true\n\n"
			
			+ "# Tells the plugin to remember everyone not having a Race to get one. \n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ races_remindDefaultRace_enable + ": true\n\n"
			
			+ "# This is the interval the plugin remembers the user to get a race. \n"
			+ "# The Value is in Minutes \n"
			+ "# integer: value without comma \n"
			+ "# default: 10 \n"
			+ races_remindDefaultRace_interval + ": true\n\n"
			
			+ "# Tells the plugin to replace the Name of the Player with a prefix of the race in the 'TAB' player list. \n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ races_display_adaptListName + ": true\n\n"
			
			+ "# Tells the Plugin to use it's own Drop list for Mobs. \n"
			+ "# This list is located at:  'plugins/RacesAndClasses/TraitConfig/DropRates.yml'\n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ races_drops_enable + ": true\n\n"
			
			+ "# Tells the plugin to check for Permission for Each Race. \n"
			+ "# The Permission is: 'RaC.races.RACENAME' \n"
			+ "# boolean: true or false \n"
			+ "# default: false \n"
			+ races_permissions_usePermissionsForEachRace + ": false\n\n"
			
			+ "# Tells the plugin to give the '/race change' Command an Uplink. \n"
			+ "# Values <= 0 say to give NO uplink \n"
			+ "# int: value without comma \n"
			+ "# default: 3600 \n"
			+ races_change_uplinkInSeconds + ": 3600\n\n"
			
			//Default Race stuff
			+ "# The name the Default Race should have. \n"
			+ "# String: a text. Preferable a name \n"
			+ "# default: DefaultRace \n"
			+ races_defaultrace_name + ": DefaultRace\n\n"
			
			+ "# The tag the Default Race should have. \n"
			+ "# String: a text. Preferable a name \n"
			+ "# default: '[NoRace]' \n"
			+ races_defaultrace_tag + ": '[NoRace]'\n\n"
			
			+ "# When this is set (not ''), whenever a player has no race,  \n"
			+ "# he will be pushed into the Race mentioned. \n"
			+ "# Example: races_takeRaceWhenNoRace: 'Elf' would push the player to the Elf Race if he has no Race. \n"
			+ "# HINT: leaving this empty (use ''), the default race will be selected \n"
			+ "# String: a text. Preferable a race name or an empty String \n"
			+ "# default: '' \n"
			+ races_takeRaceWhenNoRace + ": ''\n\n"			
			
			+ "# When a player logs in and does not have a Race, the Race selection screen is shown. \n"
			+ "# To disable this, set this option to: false \n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ races_openRaceSelectionOnJoinWhenNoRace_enable + ": true\n\n"

			+ "# When a player logs in and does not have a Race, the Race selection screen is shown. \n"
			+ "# When this option is true, the screen is reopened as long as he hasn't selected a Race. \n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ races_cancleGUIExitWhenNoRacePresent_enable + ""
					+ ": true\n\n"
			
			+ "# The time between login and showing of the selection screen in secods. \n"
			+ "# int: number without comma \n"
			+ "# default: 2 \n"
			+ races_openRaceSelectionOnJoinWhenNoRace_timeToOpenAfterLoginInSeconds + ": 2\n\n"
			
			+ "# This indicates if the GUI selection via Inventory menu should be used or the command selection. \n"
			+ "# true = gui, false = command\n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ races_gui_enable + ": true\n\n"
			
			
			//CLASSES
			+ "\n"
			+ "#########\n"
			+ "#CLASSES#\n"
			+ "#########\n"
			+ "\n"
			
			+ "# Tells the plugin to enable classes. \n"
			+ "# Setting this to false will deactivate the complete Class system.\n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ classes_enable + ": true\n\n"
			
			+ "# Tells the plugin to check for Permission for Each Class. \n"
			+ "# The Permission is: 'RaC.classes.CLASSNAME' \n"
			+ "# boolean: true or false \n"
			+ "# default: false \n"
			+ classes_permissions_usePermissionsForEachClasses + ": false\n\n"
			
			+ "# Tells the plugin to check every class selection against a Race / Class matrix. \n"
			+ "# The matrix can be found in: 'plugins/RacesAndClasses/racesClassesSelectionMatrix.yml'\n"
			+ "# Adding an entry looks like this: \n"
			+ "# RACENAME: [CLASS1, CLASS2, CLASS3]\n"
			+ "# This means the race RACENAME can only select CLASS1, ClASS2 or CLASS3.\n"
			+ "# boolean: true or false \n"
			+ "# default: false \n"
			+ classes_useRaceClassSelectionMatrix + ": false\n\n"
			
			+ "# When this is set (not ''), whenever a player has no class,  \n"
			+ "# he will be pushed into the class mentioned. \n"
			+ "# Example: classes_takeClassWhenNoClass: 'Warrior' would push the player to the Warrior Class if he has no Class. \n"
			+ "# HINT: leaving this empty (use ''), the default class will be selected \n"
			+ "# String: a text. Preferable a Class name or an empty String \n"
			+ "# default: '' \n"
			+ classes_takeClassWhenNoClass + ": ''\n\n"
			
			+ "# Tells the plugin to give the '/class change' Command an Uplink. \n"
			+ "# Values <= 0 say to give NO uplink \n"
			+ "# int: value without comma \n"
			+ "# default: 3600 \n"
			+ classes_change_uplinkInSeconds + ": 3600\n\n"
			
			+ "# When a player logs in and does not have a Class, the Class selection screen is shown. \n"
			+ "# To disable this, set this option to: false \n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ classes_openClassSelectionAfterRaceSelectionWhenNoClass_enable + ": true\n\n"

			+ "# When a player logs in and does not have a Class, the Class selection screen is shown. \n"
			+ "# When this option is true, the screen is reopened as long as he hasn't selected a Class. \n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ classes_cancleGUIExitWhenNoClassPresent_enable + ": true\n\n"
			
			+ "# This indicates if the GUI selection via Inventory menu should be used or the command selection. \n"
			+ "# true = gui, false = command\n"
			+ "# boolean: true or false \n"
			+ "# default: true \n"
			+ classes_gui_enable + ": true\n\n"
	
			+ "";

}
