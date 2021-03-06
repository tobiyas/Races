package de.tobiyas.racesandclasses.traitcontainer.traits.defaultraits.magic.MagicSilenceTrait;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.APIs.SilenceAndKickAPI;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitInfos;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitRestriction;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractMagicSpellTrait;
import de.tobiyas.racesandclasses.translation.languages.Keys;
import de.tobiyas.racesandclasses.util.MCPrettyName;
import de.tobiyas.racesandclasses.util.entitysearch.SearchEntity;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;

public class MagicSilenceTrait extends AbstractMagicSpellTrait {

	/**
	 * The time to silence after kick.
	 */
	private double duration = 0;
	
	/**
	 * The range to use.
	 */
	private int range = 0;
	
	
	@TraitInfos(category="activate", traitName="MagicSilenceTrait", visible=true)
	@Override
	public void importTrait() {}
	
	
	@TraitEventsUsed()
	@Override
	public void generalInit() {}

	
	@Override
	public String getName() {
		return "MagicSilenceTrait";
	}

	@Override
	public TraitResults trigger(EventWrapper wrapper) {
		return TraitResults.False();
	}
	
	
	@TraitConfigurationNeeded(fields={
			@TraitConfigurationField(fieldName="duration", classToExpect=Double.class, optional=true),
			@TraitConfigurationField(fieldName="range", classToExpect=Integer.class, optional=true)
		})
	@Override
	public void setConfiguration(TraitConfiguration configMap) throws TraitConfigurationFailedException {
		super.setConfiguration(configMap);
		
		this.duration = configMap.getAsDouble("duration", 0);
		this.range = configMap.getAsInt("range", 8);
	}
	

	@Override
	public boolean isBetterThan(Trait trait) {
		if(trait instanceof MagicSilenceTrait) return duration > ((MagicSilenceTrait) trait).duration;
		return false;
	}
	

	@Override
	public boolean canBeTriggered(EventWrapper wrapper) {
		return false;
	}
	

	@Override
	protected String getPrettyConfigIntern() {
		return "silences for " + (int)duration + " seconds";
	}

	@Override
	protected TraitRestriction checkForFurtherRestrictions(EventWrapper wrapper) {
		int range = modifyToPlayer(wrapper.getPlayer(), this.range, "range");
		LivingEntity target = SearchEntity.inLineOfSight(range, wrapper.getPlayer().getPlayer());
		if(target == null) return TraitRestriction.NoTarget;
		
		return null;
	}
	
	
	/**
	 * Returns the name of the Entity.
	 * @param entity to use.
	 * @return the name.
	 */
	private String getNameOfEntity(LivingEntity entity){
		switch(entity.getType()){
			case PLAYER: return ((Player)entity).getDisplayName();
			default: return entity.getCustomName() == null ? MCPrettyName.getPrettyName(entity.getType()) : entity.getCustomName();
		}
	}
	
	
	@Override
	public boolean isBindable() {
		return true;
	}


	@Override
	protected void magicSpellTriggered(RaCPlayer player, TraitResults result) {
		int range = modifyToPlayer(player, this.range, "range");
		LivingEntity target = SearchEntity.inLineOfSight(range, player.getPlayer());
		if(target == null) {
			result.copyFrom(TraitResults.False());
			return;
		}
		
		double duration = modifyToPlayer(player, this.duration, "duration");
		SilenceAndKickAPI.silence(target.getUniqueId(), (long)(duration*1000));
		player.sendTranslatedMessage(Keys.trait_silence_sucess, "name", getNameOfEntity(target), "duration", String.valueOf((int) duration));
		
		result.copyFrom(TraitResults.True());
	}
	

}
