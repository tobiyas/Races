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
package de.tobiyas.racesandclasses.traitcontainer.traits.arrows;

import static de.tobiyas.racesandclasses.translation.languages.Keys.arrow_change;

import java.util.List;
import java.util.concurrent.Callable;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import de.tobiyas.racesandclasses.RacesAndClasses;
import de.tobiyas.racesandclasses.APIs.LanguageAPI;
import de.tobiyas.racesandclasses.datacontainer.arrow.ArrowManager;
import de.tobiyas.racesandclasses.datacontainer.traitholdercontainer.TraitHolderCombinder;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.EventWrapper;
import de.tobiyas.racesandclasses.eventprocessing.eventresolvage.PlayerAction;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import de.tobiyas.racesandclasses.playermanagement.playerdisplay.scoreboard.PlayerRaCScoreboardManager.SBCategory;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.TraitResults;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationField;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitConfigurationNeeded;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.annotations.configuration.TraitEventsUsed;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.CostType;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;
import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.TraitWithCost;
import de.tobiyas.racesandclasses.traitcontainer.traits.magic.AbstractMagicSpellTrait;
import de.tobiyas.racesandclasses.traitcontainer.traits.pattern.AbstractActivatableTrait;
import de.tobiyas.racesandclasses.util.bukkit.versioning.compatibility.CompatibilityModifier;
import de.tobiyas.racesandclasses.util.friend.EnemyChecker;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfiguration;
import de.tobiyas.racesandclasses.util.traitutil.TraitConfigurationFailedException;
import de.tobiyas.racesandclasses.vollotile.ParticleContainer;
import de.tobiyas.racesandclasses.vollotile.Vollotile;
import de.tobiyas.util.schedule.DebugBukkitRunnable;


public abstract class AbstractArrow extends AbstractActivatableTrait implements TraitWithCost {
	
	protected static final String BOUND_TO_BOW_PATH = "boundToBow";
	protected static final String INITIAL_DAMAGE_PATH = "initialDamage";
	protected static final String ARROW_PARTICLE_PATH_PATH = "arrowParticlePath";
	
	
	protected RacesAndClasses plugin = RacesAndClasses.getPlugin();
	
	/**
	 * The duration to use.
	 */
	protected int duration;
	
	/**
	 * Total damage var.
	 */
	protected double totalDamage;
	
	/**
	 * The initial damage to deal.
	 */
	protected double initialDamage = -1;
	
	/**
	 * If the trait is bound to the Bow or on-use.
	 */
	protected boolean boundToBow = true;
	
	
	////Mana part:
	
	/**
	 * The Cost of the Spell.
	 * 
	 * It has the default Cost of 0.
	 */
	protected double cost = 0;
	
	/**
	 * The Material for casting with {@link CostType#ITEM}
	 */
	protected Material materialForCasting = Material.FEATHER;
	
	/**
	 * The CostType of the Spell.
	 * 
	 * It has the Default CostType: {@link CostType#MANA}.
	 */
	protected CostType costType = CostType.MANA;
	
	/**
	 * The Material damage for casting.
	 */
	protected byte materialDamageForCasting = 0;
	
	/**
	 * The Material Name for casting.
	 */
	protected String materialNameForCasting = null;
	
	/**
	 * The Particles to show when following the arrow.
	 */
	protected ParticleContainer arrowPathParticles = null;
	
	
	
	
	@TraitConfigurationNeeded(fields = {
			@TraitConfigurationField(fieldName = BOUND_TO_BOW_PATH, classToExpect = Boolean.class, optional = true),
			@TraitConfigurationField(fieldName = INITIAL_DAMAGE_PATH, classToExpect = double.class, optional = true),
			@TraitConfigurationField(fieldName = ARROW_PARTICLE_PATH_PATH, classToExpect = ParticleContainer.class, optional = true),
			@TraitConfigurationField(fieldName = AbstractMagicSpellTrait.COST_PATH, classToExpect = Double.class, optional = true),
			@TraitConfigurationField(fieldName = AbstractMagicSpellTrait.COST_TYPE_PATH, classToExpect = String.class, optional = true),
			@TraitConfigurationField(fieldName = AbstractMagicSpellTrait.ITEM_TYPE_PATH, classToExpect = Material.class, optional = true),
			@TraitConfigurationField(fieldName = AbstractMagicSpellTrait.ITEM_DAMAGE_PATH, classToExpect = Integer.class, optional = true),
			@TraitConfigurationField(fieldName = AbstractMagicSpellTrait.ITEM_NAME_PATH, classToExpect = String.class, optional = true)
	})
	@Override
	public void setConfiguration(TraitConfiguration configMap)
			throws TraitConfigurationFailedException {
		
		super.setConfiguration(configMap);
		
		//Bow related stuff:
		this.boundToBow = configMap.getAsBool(BOUND_TO_BOW_PATH, true);
		this.initialDamage = configMap.getAsDouble(INITIAL_DAMAGE_PATH, -1);
		this.arrowPathParticles = configMap.getAsParticleContainer(ARROW_PARTICLE_PATH_PATH, null);
		
		
		//Magic costs:
		cost = configMap.getAsDouble(AbstractMagicSpellTrait.COST_PATH, 0);
		if(configMap.containsKey(AbstractMagicSpellTrait.COST_TYPE_PATH)){
			String costTypeName = configMap.getAsString(AbstractMagicSpellTrait.COST_TYPE_PATH);
			costType = CostType.tryParse(costTypeName);
			if(costType == null){
				throw new TraitConfigurationFailedException(getName() + " is incorrect configured. costType could not be read.");
			}
			
			if(costType == CostType.ITEM){
				if(!configMap.containsKey(AbstractMagicSpellTrait.ITEM_TYPE_PATH)){
					throw new TraitConfigurationFailedException(getName() + " is incorrect configured. 'costType' was ITEM but no Item is specified at 'item'.");
				}
				
				materialForCasting = configMap.getAsMaterial(AbstractMagicSpellTrait.ITEM_TYPE_PATH);
				if(materialForCasting == null){
					throw new TraitConfigurationFailedException(getName() + " is incorrect configured."
							+ " 'costType' was ITEM but the item read is not an Item. Items are CAPITAL. "
							+ "See 'https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html' for all Materials. "
							+ "Alternative use an ItemID.");
				}
				
				materialDamageForCasting = (byte) configMap.getAsInt(AbstractMagicSpellTrait.ITEM_DAMAGE_PATH, 0);
				materialNameForCasting = configMap.getAsString(AbstractMagicSpellTrait.ITEM_NAME_PATH, null);
			}
		}
		
	}
	
	
	@Override
	public boolean canBeTriggered(EventWrapper wrapper){
		Event event = wrapper.getEvent();
		if(!(event instanceof PlayerInteractEvent || 
				event instanceof EntityShootBowEvent || 
				event instanceof ProjectileHitEvent ||
				event instanceof EntityDamageByEntityEvent)) return false;
		
		RaCPlayer player = wrapper.getPlayer();
		//Change ArrowType
		if(event instanceof PlayerInteractEvent){
			PlayerInteractEvent Eevent = (PlayerInteractEvent) event;
			if(!(Eevent.getAction() == Action.LEFT_CLICK_AIR)) return false;

			if(!isThisArrow(player)) return false;
			if(!TraitHolderCombinder.checkContainer(player, this)) return false;
			if(player.getPlayer().getItemInHand().getType() != Material.BOW) return false;
	
			return true;
		}
			
		//Projectile launch
		if(event instanceof EntityShootBowEvent){
			EntityShootBowEvent Eevent = (EntityShootBowEvent) event;
			if(Eevent.getEntity().getType() != EntityType.PLAYER) return false;
			if(!TraitHolderCombinder.checkContainer(player, this)) return false;			
			if(!isThisArrow(player)) return false;

			return true;
		}
		
		//Arrow Hit Location
		if(event instanceof ProjectileHitEvent){
			ProjectileHitEvent Eevent = (ProjectileHitEvent) event;
			if(Eevent.getEntityType() != EntityType.ARROW) return false;
			final Projectile arrow = (Projectile) Eevent.getEntity();

			if(CompatibilityModifier.Shooter.getShooter(arrow) == null) return false;
			
			List<MetadataValue> metaValues = arrow.getMetadata(ARROW_META_KEY);
			if(arrow.getMetadata(ARROW_META_KEY).isEmpty()) return false;

			boolean found = false;
			for(MetadataValue value : metaValues){
				if(getDisplayName().equals(value.value())){
					found = true;
					break;
				}
			}
			
			
			if(!found) return false;
			
			//Remove the meta to not leak them.
			//We have to schedule this 1 tick to future, since this event is called BEFORE the damage event in 1.8+.
			removeMetadataNextTick(arrow);
			
			LivingEntity shooter = CompatibilityModifier.Shooter.getShooter(arrow);
			if(shooter.getType() != EntityType.PLAYER) return false;
			
			RaCPlayer realPlayer = RaCPlayerManager.get().getPlayer((Player) shooter);
			if(!TraitHolderCombinder.checkContainer(realPlayer, this)) return false;
			if(!isThisArrow(realPlayer)) return false;
			return true;
		}
		
		//Arrow Hits target
		if(event instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;
			if(Eevent.getDamager().getType() != EntityType.ARROW) return false;
			
			Arrow realArrow = (Arrow) Eevent.getDamager();
			LivingEntity shooter = CompatibilityModifier.Shooter.getShooter(realArrow);
			
			if(shooter == null || realArrow == null || realArrow.isDead()) return false;
			if(shooter.getType() != EntityType.PLAYER) return false;
			
			if(Eevent.getEntity() == shooter && realArrow.getTicksLived() < 5)
				return false;

			if(!TraitHolderCombinder.checkContainer(player, this)) return false;
			
			if(realArrow.getMetadata(ARROW_META_KEY).isEmpty()) return false;
			List<MetadataValue> metaValues = realArrow.getMetadata(ARROW_META_KEY);
			
			boolean found = false;
			for(MetadataValue value : metaValues){
				if(getDisplayName().equals(value.value())){
					found = true;
					break;
				}
			}
			
			if(!found) return false;
			
			//Remove the meta to not leak them.
			realArrow.removeMetadata(ARROW_META_KEY, plugin);
			
			//you can not hit your allies.
			if(EnemyChecker.areAllies(realArrow, Eevent.getEntity())) return false;
			if(!isThisArrow(player)) return false;

			return true;
		}
		
		return false;
	}
	
	
	/**
	 * Checks if the Arrow is active at the moment from the passed player
	 * 
	 * @param player to check
	 * @return true if active, false if not.
	 */
	private boolean isThisArrow(RaCPlayer player){
		ArrowManager arrowManager = player.getArrowManager();
		AbstractArrow arrow = arrowManager.getCurrentArrow();
		if(arrow == null || arrow != this) return false;
		return true;
	}
	
	/**
	 * The Meta Key for the Arrow to search for.
	 */
	private static final String ARROW_META_KEY = "arrowType";
	
	@Override
	public TraitResults trigger(EventWrapper eventWrapper) {
		Event event = eventWrapper.getEvent();
		
		TraitResults result = new TraitResults();
		//Change ArrowType
		if(event instanceof PlayerInteractEvent){
			changeArrowType(eventWrapper.getPlayer());
			return result.setTriggered(false);
		}
		
		//Projectile launch
		if(event instanceof EntityShootBowEvent){
			EntityShootBowEvent Eevent = (EntityShootBowEvent) event;
			
			Arrow arrow = (Arrow) Eevent.getProjectile();
			arrow.setMetadata(ARROW_META_KEY , new LazyMetadataValue(plugin, new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					return getDisplayName();
				}
			}));
			
			boolean triggered = onShoot(Eevent);
			if(triggered){
				//Do not forget to remove the Cost for spells:
				eventWrapper.getPlayer().getSpellManager().removeCost(this);
				addParticleTask(arrow);
			}
			
			return result.setTriggered(triggered).setSetCooldownOnPositiveTrigger(triggered).setRemoveCostsAfterTrigger(triggered);
		}
		
		//Arrow Hit Location
		if(event instanceof ProjectileHitEvent){
			ProjectileHitEvent Eevent = (ProjectileHitEvent) event;
			boolean change = onHitLocation(Eevent);
			return result.setTriggered(change);
		}
		
		//Arrow Hits target
		if(event instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent Eevent = (EntityDamageByEntityEvent) event;
			boolean change = onHitEntity(Eevent);
			Eevent.getDamager().remove();
			
			double modInitDamage = modifyToPlayer(eventWrapper.getPlayer(), initialDamage, "initialDamage");
			if(modInitDamage > 0) Eevent.setDamage(modInitDamage);
			return result.setTriggered(change);
		}
		
		return result.setTriggered(false);
	}
	
	/**
	 * Adds a task to show particles on the Arrow.
	 * @param arrow to show.
	 */
	private void addParticleTask(final Arrow arrow) {
		if(arrow == null || this.arrowPathParticles == null) return;
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if(arrow.isDead() || !arrow.isValid() || arrow.getVelocity().lengthSquared() < 0.2){
					this.cancel();
					return;
				}
				
				Vollotile.get().sendOwnParticleEffectToAll(arrowPathParticles, arrow.getLocation());
			}
		}.runTaskTimer(plugin, 2, 2);
	}


	/**
	 * Changes to the next arrow.
	 */
	protected void changeArrowType(RaCPlayer player){
		ArrowManager arrowManager = player.getArrowManager();
		AbstractArrow arrow = arrowManager.getCurrentArrow();
		if(arrow == null || arrow != this) return;
		
		boolean forward = !player.getPlayer().isSneaking();
		AbstractArrow newArrow = forward ? arrowManager.nextArrow() : arrowManager.previousArrow();
		if(newArrow != null && newArrow != arrow){
			if(!plugin.getConfigManager().getGeneralConfig().isConfig_enable_permanent_scoreboard()){
				player.getScoreboardManager().updateSelectAndShow(SBCategory.Arrows);
			}
			
			LanguageAPI.sendTranslatedMessage(player, arrow_change, "trait_name", newArrow.getDisplayName());
		}
		
	}

	/**
	 * This is called when a Player shoots an Arrow with this ArrowTrait present
	 * 
	 * @param event that was triggered
	 * @return true if a cooldown should be triggered
	 */
	protected abstract boolean onShoot(EntityShootBowEvent event);
	
	/**
	 * This is triggered when the Player Hits an Entity with it's arrow
	 * 
	 * @param event that triggered the event
	 * @return true if an Cooldown should be triggered
	 */
	protected abstract boolean onHitEntity(EntityDamageByEntityEvent event);
	
	/**
	 * This is triggered when the Player hits an Location
	 * 
	 * @param event that triggered the event
	 * @return true if an Cooldown should be triggered
	 */
	protected abstract boolean onHitLocation(ProjectileHitEvent event);
	
	/**
	 * Returns the name of the Arrow type
	 * 
	 * @return
	 */
	protected abstract String getArrowName();
	
	
	@Override
	public String getDisplayName() {
		String superDisplayName = super.getDisplayName();
		if(superDisplayName.equals(getName())){
			return getArrowName();
		}
		
		return superDisplayName;
	}

	@Override
	public boolean isBetterThan(Trait trait){
		if(trait.getClass() != this.getClass()) return false;
		
		//TODO Not sure about this...
		return false;
	}
	
	
	@Override
	public TraitResults trigger(RaCPlayer player) {
		Player realPlayer = player.getPlayer();
		if(realPlayer == null || !realPlayer.isOnline()) return TraitResults.False();
		
		Arrow arrow = realPlayer.launchProjectile(Arrow.class);
		if(arrow != null){
			ItemStack item = new ItemStack(Material.BOW);
			item.addEnchantment(Enchantment.ARROW_INFINITE, 1);
			
			//Do not forget to remove the Cost for spells:
			player.getSpellManager().removeCost(this);
			
			onShoot(new EntityShootBowEvent(realPlayer, item, arrow, 1f));
			return TraitResults.True();
		}
		
		return TraitResults.False();
	}
	
	

	@Override
	public boolean triggerButHasUplink(EventWrapper wrapper) {
		if(wrapper.getPlayerAction() == PlayerAction.INTERACT_BLOCK 
				|| wrapper.getPlayerAction() == PlayerAction.INTERACT_BLOCK){
			changeArrowType(wrapper.getPlayer());
			return true;
		}
		
		if(wrapper.getEvent() instanceof ProjectileHitEvent){
			//Bypass the Uplink.
			if(canBeTriggered(wrapper)) trigger(wrapper);
			return true;
		}

		if(wrapper.getEvent() instanceof EntityDamageByEntityEvent){
			//Bypass the Uplink.
			if(canBeTriggered(wrapper)) trigger(wrapper);
			return true;
		}
		
		if(wrapper.getPlayerAction() == PlayerAction.DO_DAMAGE){
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean isStackable(){
		return false;
	}

	@TraitEventsUsed(registerdClasses = {
			EntityDamageByEntityEvent.class, 
			PlayerInteractEvent.class, 
			EntityShootBowEvent.class,
			ProjectileHitEvent.class
		})
	@Override
	public void generalInit() {
	}

	
	@Override
	public boolean notifyTriggeredUplinkTime(EventWrapper wrapper) {
		if(wrapper.getPlayer().getArrowManager().getCurrentArrow() != this) return false;
		return super.notifyTriggeredUplinkTime(wrapper);
	}
	
	@Override
	public boolean isBindable() {
		return !boundToBow;
	}
	
	@Override
	public double getCost(RaCPlayer player){
		int level = player.getLevelManager().getCurrentLevel();
		return this.skillConfig.getCastCostForLevel(level, modifyToPlayer(player, cost, "cost"));
	}
	
	@Override
	public CostType getCostType(){
		return costType;
	}


	@Override
	public Material getCastMaterialType(RaCPlayer player) {
		int level = player.getLevelManager().getCurrentLevel();
		return this.skillConfig.getCastMaterialForLevel(level, this.materialForCasting);
	}
	
	@Override
	public short getCastMaterialDamage(RaCPlayer player) {
		int level = player.getLevelManager().getCurrentLevel();
		return this.skillConfig.getCastMaterialDamageForLevel(level, this.materialDamageForCasting);
	}
	
	@Override
	public String getCastMaterialName(RaCPlayer player) {
		int level = player.getLevelManager().getCurrentLevel();
		return this.skillConfig.getCastMaterialNameForLevel(level, this.materialNameForCasting);
	}

	
	@Override
	public void triggerButDoesNotHaveEnoghCostType(EventWrapper wrapper) {}

	@Override
	public boolean needsCostCheck(EventWrapper wrapper) {
		return wrapper.getEvent() instanceof EntityShootBowEvent;
	}
	
	
	/**
	 * Removes the Metadata from the Projectile in the Next tick.
	 * @param pro to remove.
	 */
	private void removeMetadataNextTick(final Projectile pro){
		new DebugBukkitRunnable("ArrowMetaRemover") {
			
			@Override
			protected void runIntern() {
				try{
					pro.removeMetadata(ARROW_META_KEY, plugin);
				}catch(Throwable exp){}
			}
		};
	}
	
}
