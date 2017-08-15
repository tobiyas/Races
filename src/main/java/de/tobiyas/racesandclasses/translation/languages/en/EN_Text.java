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
package de.tobiyas.racesandclasses.translation.languages.en;

import static de.tobiyas.racesandclasses.translation.languages.Keys.*;


public class EN_Text {

	public static final String en_language = ""
			//a
			+ already_are + ": '&cYou already are a &d%HOLDER%&c.'\r\n"
			+ alread_full_mana + ": '&cYou already have full mana.'\r\n"
			+ already_have_class + ": '&cYou already have a class: &d%CLASS%&c. Use &c/class change&c to change your class.'\r\n"
			+ already_have_race + ": '&cYou already have a race: &d%RACE%&c. Use &c/race change&c to change your race.'\r\n"
			+ armor + ": 'Armor'\r\n"
			+ armor_not_allowed + ": '&cYou are not allowed to use &c%MATERIAL%&a.'\r\n"
			+ arrow_change + ": '&aSwitched arrows to: &d%TRAIT_NAME%&a.'\r\n"
			
			//b
			+ bow_selected_message + ": '&a You have a &dBOW &ain your hand. Use &dLEFT &aClick to change through your arrows. Current arrow: &d%CURRENT_ARROW% &a.'\r\n"
			+ buff_activated + ": '&a You activated &d%BUFF%&a.'\r\n"
			+ buff_timeout + ": '&a Buff &d%BUFF% &atimedout.'\r\n"
			+ buff_used + ": '&a Buff &d%BUFF% &aused.'\r\n"
			
			//c
			+ channel_propertie + ": 'channelPropertie'\r\n"
			+ change_to_same_holder + ": '&cYou are already a &d%HOLDER%&c.'\r\n"
			+ "class" + ": 'class'\r\n"
			+ class_not_exist + ": '&cThe class &d%CLASS% &cdoes not exist.'\r\n"
			+ class_changed_to + ": '&cYour Class has been changed to: &d%CLASS%&c.'\r\n"
			+ class_changed_to_other + ": '&aClass of &d%PLAYER%&a changed to: &d%CLASS%&d.'\r\n"
			+ cooldown_is_ready_again + ": '&d%TRAIT_NAME% &ais ready again.'\r\n"
			+ cooldown_is_ready_in + ": '&cYou still have Cooldown. Ready in &d%TIME%&c.'\r\n"
			
			//d
			+ disabled_region + ": '&cYou may not use any Trait here.'\r\n"
			
			//e
			
			//f
			+ failed + ": '&cfailed'\r\n"
			+ food_cancle + ": '&aEating cancled.'\r\n"
			+ food_eat + ": '&cDo not move, or your eating will be cancled.'\r\n"
			+ food_finished + ": '&cEating finished.'\r\n"
			
			//g
			
			//h
			+ healed + ": '&aYou have been healed.'\r\n"
			+ healed_other + ": '&aYou have healed: &d%PLAYER%&a.'\r\n"
			+ health + ": 'Health'\r\n"
			+ health_full + ": 'Your health is full.'\r\n"
			+ held_item_not_air + ": '&aYou may not have an item in your hands.'\r\n"
			+ holder_selectiongui_header + ": '&aSelect your &d%HOLDER%&a, %PLAYER%'\r\n"
			+ holder_not_present + ": '&cYou have to be a %HOLDER% for this.'\r\n"
			
			//i
			+ in_restricted_area + ": '&cYou may not use any Trait here.'\r\n"
			
			//j
			
			
			//k
			
			//l
			+ login_no_race_selected + ": '&cYou have not selected a Race. Please select a race using /race select <racename>'\r\n"
			+ launched_something + ": '&a%NAME% launched.'\r\n"
			+ level_too_low + ": '&cYour level is too low.'\r\n"
			+ level_up_message + ": '&aCongratulations, &d%PLAYER%&a. You leveled up to Level &d%LEVEL%&a.'\r\n"
			+ level_up_new_skill_message + ": '&aYou have learned a new Skill: &d%TRAITNAME%&a.'\r\n"
			
			
			//m			
			+ magic_chaneling_failed + ": '&cChaneling of &d%TRAIT_NAME% &cfailed.'\r\n"
			+ magic_change_spells + ": '&aChanged Spell to: &d%TRAIT_NAME% &aCost: &d%COST% %COST_TYPE%&a.'\r\n"
			+ magic_dont_have_enough + ": '&c You do not have enough &d%COST_TYPE% &cfor &d%TRAIT_NAME% &c.'\r\n"
			+ magic_no_spells + ": '&cYou can not cast any spells.'\r\n"
			+ magic_spell_activated + ": '&d%TRAIT_NAME% &aactivated.'\r\n"
			+ magic_spell_deactivated + ": '&d%TRAIT_NAME% &cdeactivated.'\r\n"
			+ mana_already_full + ": '&cYour Mana is already full.'\r\n"
			+ mana + ": 'mana'\r\n"
			+ member_config_not_found + ": '&cYour config could not be found. Try relogging or contact an Admin.'\r\n"
			+ member_config_changed + ": '&aThe Attribute &d%ATTRIBUTE%&a has been changed to: &d%VALUE%&a.'\r\n"
			+ member_config_attribute_not_found + ": '&cThe Attribute &d%ATTRIBUTE%&c could not be found.'\r\n"
			
			//n
			+ needs_1_arg + ": 'This command needs 1 argument: %COMMAND%'\r\n"
			+ new_value + ": 'new Value'\r\n"
			+ no_class_in_list + ": 'No Classes in the list.'\r\n"
			+ no_class_on_change + ": '@no_class_selected@. Use &d/class select &cto select a class.'\r\n"
			+ no_class_selected_use_info + ": '&cYou have no class selected. Use &d/class info <class name> &cto inspect a class.'\r\n"			
			+ no_class_selected + ": '&cYou have no class selected.'\r\n"			
			+ no_class_to_select + ": '&c You do not have any Classes to select.'\r\n"
			+ no_find_trait + ": '&c Could not find this trait.'\r\n"
			+ no_healthcontainer_found + ": '&cSomething gone Wrong. No healthcontainer found for you.'\r\n"
			+ no_message + ": '&cNo message given.'\r\n"
			+ number_not_readable + ": '&cNumber could not be read.'\r\n"
			+ no_race_selected + ": '&cYou have no race selected.'\r\n"
			+ no_race_to_select + ": '&c You do not have any Race to select.'\r\n"			
			+ no_taget_found + ": '&cNo target found.'\r\n"
			+ no_traits + ": '&cNo Traits.'\r\n"
			
			//o
			+ only_players + ": '&cThis command can only be used by Players.'\r\n"
			+ open_holder + ": '&2Opening %HOLDER% Selection....'\r\n"
			+ open_traits + ": '&a Opening Traits of &d%PLAYER%&a.'\r\n"
			
			//p
			+ password + ": 'password'\r\n"
			+ permission_not_present + ": '&cYou do not have the Permission to do that.'\r\n"
			+ player_not_exist + ": '&cPlayer &d%PLAYER%&c does not exist.'\r\n"
			+ plugin_pre + ": '&4[RaC]'\r\n"
			
			//q
			+ quick_slot_item_lore + ": '&4Throw this item away in the Inventory to remove it.'\r\n"
			+ quick_slot_selected + ": '&4Quickslot item recieved in inventory.'\r\n"
			
			//r
			+ race + ": 'race'\r\n"
			+ race_not_exist + ": '&cThe race &d%RACE% &cdoes not exist.'\r\n"
			+ race_changed_to + ": '&cYour Race has been changed to: &d%RACE%&c.'\r\n"
			+ race_changed_to_other + ": '&aRace of &d%PLAYER%&a changed to: &d%RACE%&d.'\r\n"
			+ race_spawn_created + ": '&aRace-Spawn of &d%RACE% &acreated.'\r\n"
			+ race_spawn_no_spawnpoint + ": '&cThe race &d%RACE% &chas no spawnpoint.'\r\n"
			+ race_spawn_teleport_success + ": '&aWelcome to the Race Spawn of &d%RACE%&a.'\r\n"
			+ reload_message + ": '&aReload of &dRaces &adone successfully. Time taken: &d %TIME% &ams'\r\n"
			+ restrictions_not_met + ": '&aThe Trait still has cooldown or may not be used here..'\r\n"
			
			//restrictions
			+ restrictions_not_met_MinimumLevel + ": '&cYour level is too low.'\r\n"
			+ restrictions_not_met_MaximumLevel + ": '&cYour level is too hight.'\r\n"
			+ restrictions_not_met_Biomes + ": '&cCan not be used in this biome'\r\n"
			+ restrictions_not_met_Wearing + ": '&cYou are not wearing the correct clothings.'\r\n"
			+ restrictions_not_met_OnlyInWater + ": '&cCan be used only in water.'\r\n"
			+ restrictions_not_met_OnlyInWorld + ": '&cCan be used only on another world.'\r\n"
			+ restrictions_not_met_OnlyOnLand + ": '&cCan be used only on land.'\r\n"
			+ restrictions_not_met_OnlyInLava + ": '&cCan be used only in lava.'\r\n"
			+ restrictions_not_met_OnlyOnSnow + ": '&cCan be used only on snow.'\r\n"
			+ restrictions_not_met_OnlyInNight + ": '&cCan be used only in night.'\r\n"
			+ restrictions_not_met_OnlyOnDay + ": '&cCan be used only on day.'\r\n"
			+ restrictions_not_met_Cooldown + ": '&cStill has cooldown.'\r\n"
			+ restrictions_not_met_AboveLevitation + ": '&cYour elevation is too low.'\r\n"
			+ restrictions_not_met_BelowLevitation + ": '&cYour elevation is too high.'\r\n"
			+ restrictions_not_met_OnlyInRain + ": '&cCan be used only in rain.'\r\n"
			+ restrictions_not_met_OnlyAfterDamage + ": '&cCan only be used after beeing damaged.'\r\n"
			+ restrictions_not_met_OnlyAfterNotDamage + ": '&cCan only be used after not beeing damaged.'\r\n"
			+ restrictions_not_met_OnlyOnBlock + ": '&cCan only be used on specific blocks.'\r\n"
			+ restrictions_not_met_Offhand + ": '&cCan only be used on with another item in offhand.'\r\n"
			+ restrictions_not_met_Mainhand + ": '&cCan only be used on with another item in mainhand.'\r\n"
			+ restrictions_not_met_Skilled + ": '&cYou did not skill this trait.'\r\n"
			+ restrictions_not_met_Silence + ": '&cYou are currently silenced.'\r\n"
			+ restrictions_not_met_OnlyWhileSneaking + ": '&cCan only be used while sneaking.'\r\n"
			+ restrictions_not_met_OnlyWhileNotSneaking + ": '&cCan only be used while not sneaking.'\r\n"
			+ restrictions_not_met_NotOnBlock + ": '&cCan not be used on this block.'\r\n"
			+ restrictions_not_met_NeededPermission + ": '&cYou do not have the Permission.'\r\n"
			+ restrictions_not_met_Costs + ": '&cYou do not have the cost.'\r\n"
			+ restrictions_not_met_OutOfRange + ": '&cOut of range.'\r\n"
			+ restrictions_not_met_TargetFriendly + ": '&cTarget is friendly.'\r\n"
			+ restrictions_not_met_NoTarget + ": '&cNo target found.'\r\n"
			
			
			
			//s
			+ send_empty_message + ": '&cYou tried to send an empty Message.'\r\n"
			+ something_disabled + ": '&c%VALUE% are disabled.'\r\n"
			+ stun_ended + ": ' &cYou are not stunned any more.'\r\n"
			+ stun_message + ": ' &cYou are stunned for &d %TIME% &cSeconds.'\r\n"
			+ stun_still + ": ' &cYou are still stunned for &b%TIME% &cSeconds. You can not %ACTION%.'\r\n"
			+ stun_success + ": '&aYou stunned &d%TARGET%&a.'\r\n"
			+ success + ": '&aSuccess.'\r\n"
			

			//t
			+ same_race_team + ": '&c%PLAYER% is in your Race Team.'\r\n"
			+ target_not_exist + ": '&cTarget does not exist or is offline'\r\n"
			+ time_in_seconds + ": 'time in seconds'\r\n"
			+ too_far_away + ": '&cTarget too far away.'\r\n"
			+ traits + ": 'traits'\r\n"
			
			//trait specific
			+ trait_backstab_success + ": '&a%NAME% backstabed.'\r\n"
			+ trait_bash_success + ": '&a%NAME% stunned.'\r\n"
			+ trait_consume_success + ": '&aYou have been given &d%VALUE% &aMana. &d%MATERIAL% &aconsumed.'\r\n"
			+ trait_dodged + ": '&aYou dodged the Attack.'\r\n"
			+ trait_fly_toggle + ": '&aYou can now &dfly &afor &d%DURATION% &aseconds.'\r\n"
			+ trait_invisible_toggle + ": '&aYou are now &dinvisible &afor &d%DURATION% &aseconds.'\r\n"
			+ trait_kick_sucess + ": '&d%NAME% &akicked.'\r\n"
			+ trait_kick_failed + ": '&d%NAME% &akicked but did not abort anything.'\r\n"
			+ trait_laststand_success + ": '&d%NAME% &atoggled. You were healed &d%VALUE%&a.'\r\n"
			+ trait_lifetap_success + ": '&aYou have been given &d%VALUE% &aMana. You take &d%DAMAGE% &adamage.'\r\n"
			+ trait_heal_target_full + ": '&aTarget does not need any healing.'\r\n"			
			+ trait_healed_target_success + ": '&d%TARGET% &ahealed.'\r\n"
			+ trait_healed_other_success + ": '&aHealed by &d%HEALER%&a.'\r\n"
			+ trait_mana_refill + ": '&a%VALUE% mana refilled.'\r\n"
			+ trait_pickup_success + ": '&aItem picked up.'\r\n"
			+ trait_pickup_inv_full + ": '&aInventory is full.'\r\n"
			+ trait_poison_success + ": '&a%TARGET% poisoned.'\r\n"
			+ trait_poison_imun + ": '&a%TARGET% is imun to poisoned.'\r\n"
			+ trait_poison_notify_other + ": '&cYou got Poisoned by %PLAYER%.'\r\n"
			+ trait_pushaway_success + ": '&d%TARGET% &apushed away.'\r\n"
			+ trait_horse_no_tame + ": '&cYou may not tame this Horse.'\r\n"
			+ trait_horse_no_leash + ": '&cYou may not leash this Horse.'\r\n"
			+ trait_horse_no_jump + ": '&cYou may not jump with this Horse.'\r\n"
			+ trait_silence_sucess + ": '&aYou silenced &d%NAME% &afor &d%DURATION% &aSeconds.'\r\n"
			+ trait_stun_arrow_success + ": '&aYou stunned &d%TARGET% &afor &d%DURATION% &aSeconds.'\r\n"
			+ trait_taunt_success + ": '&d%TARGET% &atauneted.'\r\n"
			+ trait_taunt_fade + ": '&aTaunt on &d%TARGET% &afaded.'\r\n"
			+ trait_teleport_solidtarger + ": '&cTarget is solid. Can not teleport.'\r\n"
			+ trait_teleport_success + ": '&aTeleported.'\r\n"
			+ trait_tool_trait_fail + ": '&cYou may not use this Tool.'\r\n"
			+ trait_wall_success + ": '&aWall created.'\r\n"
			+ trait_wall_faded + ": '&aWall disapears.'\r\n"

			//trait general
			+ trait_already_active + ": '&d%NAME% &c is already active.'\r\n"
			+ trait_cooldown + ": '&cYou still have &d%SECONDS% &cseconds cooldown on: &d%NAME%&c.'\r\n"
			+ trait_faded + ": '&d%NAME% &cfaded.'\r\n"
			+ trait_failed + ": '&d%NAME% &cfailed.'\r\n"
			+ trait_toggled + ": '&d%NAME% &atoggled.'\r\n"
			+ trait_kicked + ": '&d%NAME% &cgot kicked.'\r\n"
			
			
			//u
			
			//v
			+ value_0_not_allowed + ": '&cValue of 0 is not allowed.'\r\n"
			
			//w
			+ wand_select_message + ": '&a You have a &d WAND &ain your hand. Use &dLEFT &aClick to cast and &dRIGHT &aClick to switch spells. Current spell: &d%CURRENT_SPELL%&a.'\r\n"
			+ wrong_command_use + ": '&cWrong usage. Use the command like this: &d%COMMAND%&c.'\r\n"
			+ whisper_yourself + ": '&cYou can not whisper yourself.'\r\n"
			
			//x
			
			//y
			+ your + ": 'your'\r\n"
			+ your_class + ": 'your Class'\r\n"
			+ your_race + ": 'your Race'\r\n"
			+ you_would_kill_yourself + ": '&cYou would kill yourself'\r\n"
			
			//z
			
			
			
			

			+ "";

}
