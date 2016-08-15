package de.tobiyas.racesandclasses.hotkeys;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.CooldownApi;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.saving.PlayerSavingData;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithRestrictions;

public class HotKeyInventory {
	
	/**
	 * The Key at start to identify the Item.
	 */
	private static final String BIND_KEY = ChatColor.AQUA + "Trait: ";
	
	
	/**
	 * The Hot-Key Bindings.
	 */
	private final Map<Integer,Trait> traitBindings = new HashMap<Integer, Trait>();
	
	
	/**
	 * The Old Hotkey Bar.
	 */
	private final Map<Integer,ItemStack> oldHotkeyBar = new HashMap<Integer, ItemStack>();
	
	
	/**
	 * The Player how is the Owner of the HotKeys.
	 */
	private final RaCPlayer player;
	
	/**
	 * If the Player is in Skill Mode or not.
	 */
	private boolean isInSkillMode = false;
	
	
	public HotKeyInventory(RaCPlayer player) {
		this.player = player;
		
		loadFromFile();
	}
	
	/**
	 * Loads the HotKeyInventory Bindings from the File.
	 */
	public void loadFromFile(){
		traitBindings.clear();
		
		PlayerSavingData data = player.getPlayerSaveData();
		Map<Integer,String> bindings = data.getHotKeys();
		
		//Read the Data.
		for(Map.Entry<Integer,String> entry : bindings.entrySet()){
			int key = entry.getKey();
			String name = entry.getValue();
			
			for(Trait trait : player.getTraits()){
				if(trait.getDisplayName().equals(name)){
					this.traitBindings.put(key, trait);
				}
			}
		}
	}
	
	
	/**
	 * Binds a trait to the slot passed.
	 * 
	 * @param slot to bind to
	 * @param trait to bind.
	 */
	public void bindTrait(int slot, Trait trait){
		//first check if legit trait.
		if(trait == null || !trait.isBindable()) return;
		
		PlayerSavingData data = player.getPlayerSaveData();
		data.setHotKey(slot, trait.getDisplayName());
		this.traitBindings.put(slot, trait);
	}
	
	/**
	 * Removes any trait from the Slot.
	 * 
	 * @param slot to clear
	 */
	public void clearSlot(int slot){
		if(!traitBindings.containsKey(slot)) return;
		
		PlayerSavingData data = player.getPlayerSaveData();
		data.clearHotKey(slot);
		this.traitBindings.remove(slot);
	}
	
	
	/**
	 * Clears all Trait slots.
	 */
	public void clearAllSlots(){
		traitBindings.clear();
		PlayerSavingData data = player.getPlayerSaveData();
		data.clearHotKeys();
	}
	
	
	/**
	 * Call this regularly to update playerInventory View.
	 */
	public void updatePlayerInventory(){
		if(!player.isOnline()) return;
		
		if(!isInSkillMode) return; //nothing to update.
		if(traitBindings.isEmpty()) return; //nothing to update.
		Material shortcutMat = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_hotkeys_material();
		short maxShortcutDurability = shortcutMat.getMaxDurability();
		
		for(Entry<Integer,Trait> entry : traitBindings.entrySet()){
			int slot = entry.getKey();
			Trait trait = entry.getValue();
			
			ItemStack item = player.getPlayer().getInventory().getItem(slot);
			if(item == null || item.getType() != shortcutMat) continue;
			
			if(trait instanceof TraitWithRestrictions){
				TraitWithRestrictions res = (TraitWithRestrictions) trait;
				
				int maxCD = res.getMaxUplinkTime();
				if(maxCD > 0){
					String cooldownName = ((TraitWithRestrictions) trait).getCooldownName();
					int cd = CooldownApi.getCooldownOfPlayer(player.getName(), cooldownName);
					
					if(cd >= 0){
						float percent = (float)cd / (float)maxCD;
						float val = maxShortcutDurability * percent;
						if(val <= 0) val = 1; if(val >= maxShortcutDurability) val = maxShortcutDurability - 1;
						
						item.setDurability((short) val);
					}
					
					if(cd < 0){
						item.setDurability((short) 0);
					}
				}
			}
		}
	}
	
	/**
	 * Changes the Hotbar to the build Inv.
	 */
	public void changeToBuildInv(){
		if(!isInSkillMode) return;
		
		Player player = this.player.getPlayer();
		if(player == null) return;
		if(!player.isOnline()) return; //can't set offline players items!
		
		Set<Integer> disabled = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_disabledHotkeySlots();
		
		//first clear the old ones.
		for(int i = 0; i < 9; i++) {
			if(!disabled.contains(i) && traitBindings.containsKey(i)) player.getInventory().setItem(i, null);
		}
		
		//now refill with the old ones.
		for(Entry<Integer,ItemStack> entry : oldHotkeyBar.entrySet()){
			int slot = entry.getKey();
			ItemStack item = entry.getValue();
			
			if(disabled.contains(slot)) continue;
			
			player.getInventory().setItem(slot, item);
		}
		
		oldHotkeyBar.clear();

		isInSkillMode = false;
	}
	
	/**
	 * Change the Build-Menu to the Skill Menu.
	 */
	public void changeToSkillInv(){
		if(isInSkillMode) return;
		
		Player player = this.player.getPlayer();
		if(player == null) return;
		
		Set<Integer> disabled = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_disabledHotkeySlots();
		
		//remove in case...
		oldHotkeyBar.clear();
		
		//try to move the stuff away that is in the way!
		for(int i = 0; i < 9; i++){
			if(disabled.contains(i)) continue;
			if(!traitBindings.containsKey(i)) continue;
			
			ItemStack toMove = player.getInventory().getItem(i);
			if(toMove == null || toMove.getType() == Material.AIR) continue;
			
			//now we have to move the Item to a better slot!
			for(int slot = 9; slot < player.getInventory().getSize(); slot++){
				ItemStack toMoveTo = player.getInventory().getItem(slot);
				if(toMoveTo == null || toMoveTo.getType() == Material.AIR){
					
					//we found a good slot!
					player.getInventory().setItem(slot, toMove.clone());
					player.getInventory().setItem(i, null);
					break;
				}
			}
		}
		
		//first save the old items
		for(int i = 0; i < 9; i++){
			if(disabled.contains(i)) continue;
			if(!traitBindings.containsKey(i)) continue;
			
			ItemStack item = player.getInventory().getItem(i);
			if(item != null && item.getType() != Material.AIR) oldHotkeyBar.put(i, item.clone());

			ItemStack newItem = generateItem(traitBindings.get(i));
			player.getInventory().setItem(i, newItem == null ? getEmptyItem() : newItem);
		}
		
		isInSkillMode = true;
	}
	
	
	/**
	 * This forces reseting all Mats when in Skil mode.
	 */
	public void forceUpdateOfInv(){
		if(!isInSkillMode) return;
		
		Player player = this.player.getPlayer();
		if(player == null) return;
		
		Set<Integer> disabled = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_disabledHotkeySlots();
		
		//now set the Items to the quickslot bar.
		for(int i = 0; i < 9; i++){
			if(disabled.contains(i)) continue;
			if(!traitBindings.containsKey(i)) continue;
			
			ItemStack item = generateItem(traitBindings.get(i));
			player.getInventory().setItem(i, item == null ? getEmptyItem() : item);
		}
	}
	
	
	/**
	 * Generates an Item to the Trait.
	 * 
	 * @param trait to generate to
	 * 
	 * @return the item or null if null is passed.
	 */
	public static ItemStack generateItem(Trait trait){
		if(trait == null) return null;
		
		Material shortcutMat = RacesAndClasses.getPlugin().getConfigManager().getGeneralConfig().getConfig_hotkeys_material();
		
		ItemStack item = new ItemStack(shortcutMat);
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setDisplayName(BIND_KEY + trait.getDisplayName());
		
		List<String> lore = new LinkedList<String>();
		lore.add("Switch the item to this slot to cast the Spell,");
		lore.add("or simply right-click with it.");
		lore.add("");
		lore.add(ChatColor.YELLOW + trait.getPrettyConfiguration());
		
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
		
		return item;
	}
	
	
	/**
	 * Generates an Empty Slot itme.
	 * 
	 * @return the item for an empty Slot.
	 */
	public static ItemStack getEmptyItem(){
		ItemStack item = new ItemStack(Material.FLINT);
		ItemMeta itemMeta = item.getItemMeta();
		
		itemMeta.setDisplayName(BIND_KEY + "EMPTY");
		
		List<String> lore = new LinkedList<String>();
		lore.add("Put an Skill to this Slot to to fill it.");
		
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
		
		return item;
	}
	
	
	/**
	 * Returns true if in Building mode.
	 * 
	 * @return true if in building mode.
	 */
	public boolean isInBuildingMode(){
		return !isInSkillMode;
	}
	
	/**
	 * Returns true if in Skill mode.
	 * 
	 * @return true if in Skill mode.
	 */
	public boolean isInSkillMode(){
		return isInSkillMode;
	}


	/**
	 * Returns a copy of the Bindings.
	 * 
	 * @return bindings.
	 */
	public Map<Integer,Trait> getBindings() {
		return new HashMap<Integer,Trait>(this.traitBindings);
	}
	
	
}
