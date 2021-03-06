package de.tobiyas.racesandclasses.util.permissions;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.util.permissions.PermissionChecker.PermState;
import net.milkbowl.vault.permission.Permission;

@SuppressWarnings("deprecation")
public class VaultHook extends Permission implements Listener {
	
	/**
	 * The Hooked Permission.
	 */
	private Permission hooked;
	
	/**
	 * The Plugin to use some stuff.
	 */
	private final RacesAndClasses plugin;
	
	
	protected VaultHook(RacesAndClasses plugin) {
		this.plugin = plugin;
		
		ServicesManager sm = Bukkit.getServicesManager();
		Permission perms = sm.getRegistration(Permission.class).getProvider();
		if(perms == null){
			getVaultRegsterLater();
		}else{
			hooked = perms;
			sm.unregister(perms);
		}
		
		sm.register(Permission.class, this, plugin, ServicePriority.Highest);
	}
	
	@EventHandler
	public void vaultStarted(PluginEnableEvent event){
		if(event.getPlugin().getName().equalsIgnoreCase("Vault")){
			getVaultRegister();
		}
	}
	

	private void getVaultRegister() {
		//no hooking needed if already hooked.
		if(hooked != null) return;
		
		ServicesManager sm = Bukkit.getServicesManager();
		Collection<RegisteredServiceProvider<Permission>> perms = sm.getRegistrations(Permission.class);
		
		Permission toSet = null;
		
		Iterator<RegisteredServiceProvider<Permission>> permsIt = perms.iterator();
		while(permsIt.hasNext()){
			RegisteredServiceProvider<Permission> toObserve = permsIt.next();
			if(toObserve.getProvider() != this){
				toSet = toObserve.getProvider();
				break;
			}
		}
		
		if(this.hooked != null && toSet != null){
			this.hooked = toSet;
			//System.out.println("Starting Vault hook.");
		}
	}
	

	private void getVaultRegsterLater() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				getVaultRegister();
			}
		}, 1);
	}



	public void shutDown(){
		ServicesManager sm = Bukkit.getServicesManager();
		sm.unregister(this);
		
		sm.register(Permission.class, this, Bukkit.getPluginManager().getPlugin("Vault"), ServicePriority.Highest);
	}
	
	
	@Override
	public String[] getGroups() {
		if(hooked == null || !hooked.hasGroupSupport()) return new String[]{};
		return hooked.getGroups();
	}

	@Override
	public String getName() {
		if(hooked == null || !hooked.hasGroupSupport()) return "";
		return hooked.getName();
	}

	@Override
	public String[] getPlayerGroups(String world, String playerName) {
		if(hooked == null || !hooked.hasGroupSupport()) return new String[]{};
		return hooked.getPlayerGroups(world, playerName);
	}

	@Override
	public String getPrimaryGroup(String arg0, String arg1) {
		if(hooked == null || !hooked.hasGroupSupport()) return "";
		return hooked.getPrimaryGroup(arg0, arg1);
	}

	@Override
	public boolean groupAdd(String arg0, String arg1, String arg2) {
		if(hooked == null || !hooked.hasGroupSupport()) return false;
		return hooked.groupAdd(arg0, arg1, arg2);
	}

	@Override
	public boolean groupHas(String arg0, String arg1, String arg2) {
		if(hooked == null || !hooked.hasGroupSupport()) return false;
		return hooked.groupHas(arg0, arg1, arg2);
	}

	@Override
	public boolean groupRemove(String arg0, String arg1, String arg2) {
		if(hooked == null || !hooked.hasGroupSupport()) return false;
		return hooked.groupRemove(arg0, arg1, arg2);
	}

	@Override
	public boolean hasGroupSupport() {
		if(hooked == null) return false;
		return hooked.hasGroupSupport();
	}

	@Override
	public boolean hasSuperPermsCompat() {
		if(hooked == null) return false;
		return hooked.hasSuperPermsCompat();
	}

	@Override
	public boolean isEnabled() {
		if(hooked == null) return false;
		return hooked.isEnabled();
	}

	@Override
	public boolean playerAdd(String arg0, String arg1, String arg2) {
		if(hooked == null || !hooked.hasGroupSupport()) return false;
		return hooked.playerAdd(arg0, arg1, arg2);
	}

	@Override
	public boolean playerAddGroup(String arg0, String arg1, String arg2) {
		if(hooked == null || !hooked.hasGroupSupport()) return false;
		return hooked.playerAddGroup(arg0, arg1, arg2);
	}

	@Override
	public boolean playerHas(String player, String world, String permission) {
		if(hooked == null) return false;
		for(PermissionChecker checker : permChecker){
			PermState state = checker.checkPermissions(player, world, permission);
			if(state == PermState.FORCE_PERMISE) return true;
			if(state == PermState.FORCE_DECLINE) return false;
		}
		
		return hooked.playerHas(player, world, permission);
	}

	@Override
	public boolean playerInGroup(String arg0, String arg1, String arg2) {
		if(hooked == null || !hooked.hasGroupSupport()) return false;
		return hooked.playerInGroup(arg0, arg1, arg2);
	}

	@Override
	public boolean playerRemove(String arg0, String arg1, String arg2) {
		if(hooked == null) return false;
		return hooked.playerRemove(arg0, arg1, arg2);
	}

	@Override
	public boolean playerRemoveGroup(String arg0, String arg1, String arg2) {
		if(hooked == null || !hooked.hasGroupSupport()) return false;
		return hooked.playerRemoveGroup(arg0, arg1, arg2);
	}
	
	
	/**
	 * The List of permissioncheckers
	 */
	private final List<PermissionChecker> permChecker = new LinkedList<PermissionChecker>();
	
	
	/**
	 * Registers a Permissionchecker
	 * 
	 * @param checker to add
	 */
	public void registerPermissionChecker(PermissionChecker checker){
		if(!permChecker.contains(checker)){
			permChecker.add(checker);
		}
	}
	
	
	/**
	 * This removes a {@link PermissionChecker} from the List of checkers
	 * 
	 * @param checker to remove
	 */
	public void unregisterPermissionChecker(PermissionChecker checker){
		if(permChecker.contains(checker)){
			permChecker.remove(checker);
		}
	}
}
