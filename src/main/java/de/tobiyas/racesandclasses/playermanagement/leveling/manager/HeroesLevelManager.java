package de.tobiyas.racesandclasses.playermanagement.leveling.manager;

import org.bukkit.Bukkit;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;

import de.tobiyas.racesandclasses.datacontainer.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.PlayerSavingContainer;
import de.tobiyas.racesandclasses.playermanagement.leveling.PlayerLevelManager;

public class HeroesLevelManager implements PlayerLevelManager {

	/**
	 * The Player to use.
	 */
	private final RaCPlayer player;
	
	public HeroesLevelManager(RaCPlayer player) {
		this.player = player;
	}
	
	
	@Override
	public int getCurrentLevel() {
		if(!isHerosActive()) return 1;
		
		return getHero().getLevel();
	}

	@Override
	public int getCurrentExpOfLevel() {
		if(!isHerosActive()) return 0;
		
		return 0;
	}

	@Override
	public RaCPlayer getPlayer() {
		return player;
	}

	@Override
	public void setCurrentLevel(int level) {
		if(!isHerosActive()) return;
		
		//Heroes seems to not support this.
	}

	@Override
	public void setCurrentExpOfLevel(int currentExpOfLevel) {
		if(!isHerosActive()) return;
		
		//Heroes seems to not support this.
	}

	@Override
	public boolean addExp(int exp) {
		if(!isHerosActive()) return false;
		
		getHero().addExp(exp, getHero().getHeroClass(), player.getLocation());
		return true;
	}

	@Override
	public boolean removeExp(int exp) {
		if(!isHerosActive()) return false;

		//Heroes seems to not support this.
		return false;
	}

	@Override
	public void save() {
	}

	@Override
	public void saveTo(PlayerSavingContainer container) {
		container.setPlayerLevel(getCurrentLevel());
		container.setPlayerLevelExp(getCurrentExpOfLevel());
	}

	@Override
	public void reloadFromPlayerSavingContaienr(PlayerSavingContainer container) {
		//nothing to load.
	}

	@Override
	public void checkLevelChanged() {
		//nothing to do.
	}

	@Override
	public void reloadFromYaml() {
		//nothing to da.
	}

	@Override
	public void forceDisplay() {
		//nothing to do.
	}

	@Override
	public boolean canRemove(int toRemove) {
		if(!isHerosActive()) return false;
		
		
		return false;
	}
	
	
	
	private boolean isHerosActive(){
		return Bukkit.getPluginManager().getPlugin("Heroes") != null;
	}
	
	
	private Heroes getHeroes(){
		return Heroes.getInstance();
	}
	
	private Hero getHero(){
		return getHeroes().getCharacterManager().getHero(player.getPlayer());
	}

}