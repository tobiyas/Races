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
package de.tobiyas.racesandclasses.util.consts;

import java.io.File;

import de.tobiyas.racesandclasses.RacesAndClasses;

public class Consts {
	
	private static final String racesPath = RacesAndClasses.getPlugin().getDataFolder() + File.separator;
	
	//Paths
	public static final String channelConfigPathYML = racesPath + "channels" + File.separator;
	public static final String traitConfigDir = RacesAndClasses.getPlugin().getDataFolder() + File.separator + "TraitConfig" + File.separator;
	public static String playerDataPath = racesPath + "PlayerData" + File.separator; //Not final because of testing!
	
	
	//Files
	public static final String racesYML = racesPath + "races.yml";
	public static final String classesYML = racesPath +"classes.yml";
	public static final String channelsYML = channelConfigPathYML + "channels.yml";
	public static final String channelConfigYML = channelConfigPathYML + "config.yml";
	
	//Health
	public static final int displayBarLength = 20;
	
	//Debugging
	public static final int timingLength = 10;
	
	//Version
	public static final String currentDevStage = "R S";
	public static final String detailedVersionString = "1.2.0";
	
	/**
	 * V1.0.3 RC 2: 1.3
	 * V1.0.4 RC 1: 1.4
	 * V1.1.0 : 1.5
	 * V1.1.1 : 1.6
	 * V1.1.2 : 1.8
	 * V1.1.3 : 1.9
	 * v1.1.4 : 1.10
	 * v1.1.5 : 1.11
	 * v1.1.7 : 1.12
	 * v1.1.8 : 1.13
	 * v1.1.9 : 1.14
	 * v1.1.10 : 1.15
	 * v1.1.11 : 1.16
	 * v1.1.12 : 1.17
	 * v1.1.13 : 1.18
	 * v1.1.14 : 19
	 * v1.2.0  : 20
	 */
	public static final String configVersion = "20";
	
	
	//Races
	public static String defaultRace = "DefaultRace";
	
	
	//Versioning
	public static final String minimalBukkitVersionString = "1.6.2";
	public static final int minimalBukkitMainVersion = 1;
	public static final int minimalBukkitSubVersion = 6;
	public static final int minimalBukkitRevVersion = 2;
	
	public static final int CURSE_ID_FOR_PLUGIN = 58896;
}
