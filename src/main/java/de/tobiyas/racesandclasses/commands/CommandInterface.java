package de.tobiyas.racesandclasses.commands;

import java.util.Collection;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public interface CommandInterface extends CommandExecutor, TabCompleter {

	
	/**
	 * Returns the Command name.
	 */
	public Collection<String> getCommandNames();
	
	/**
	 * Returns the aliases
	 */
	public String[] getAliases();
	
	/**
	 * if the Command has aliases.
	 * 
	 * @return
	 */
	public boolean hasAliases();
	
	
	/**
	 * Removes everything disabled.
	 * 
	 * @param disabled to remove.
	 */
	public void filterToDisabledCommands(Collection<String> disabled);
	
	/**
	 * if there is any command to activate.
	 * 
	 * @return true if there is any command.
	 */
	public boolean hasAnyCommand();
	
}
