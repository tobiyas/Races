package de.tobiyas.races.datacontainer.traitcontainer.traits.passive;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.classes.ClassManager;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceContainer;
import de.tobiyas.races.datacontainer.traitholdercontainer.race.RaceManager;
import de.tobiyas.races.datacontainer.traitcontainer.eventmanagement.TraitEventManager;
import de.tobiyas.races.datacontainer.traitcontainer.traits.Trait;

public class DamageReduce implements Trait{
	
	private double value;
	private String Operation;
	
	private RaceContainer raceContainer = null;
	private ClassContainer classContainer = null;

	public DamageReduce(RaceContainer raceContainer){
		this.raceContainer = raceContainer;
	}
	
	public DamageReduce(ClassContainer classContainer){
		this.classContainer = classContainer;
	}
	
	@Override
	public void generalInit(){
		HashSet<Class<?>> listenedEvents = new HashSet<Class<?>>();
		listenedEvents.add(EntityDamageByEntityEvent.class);
		TraitEventManager.getInstance().registerTrait(this, listenedEvents);
	}

	@Override
	public String getName() {
		return "DamageReduceTrait";
	}
	
	@Override
	public RaceContainer getRace(){
		return raceContainer;
	}

	@Override
	public Object getValue() {
		return value;
	}
	
	@Override
	public String getValueString(){
		return Operation + " " + String.valueOf(value);
	}

	@Override
	public void setValue(Object obj) {
		String opAndVal = String.valueOf(obj);
		value = evaluateValue(opAndVal);
	}
	
	private double evaluateValue(String val){
		char firstChar = val.charAt(0);
		
		Operation = "";
		
		if(firstChar == '+')
			Operation = "+";
		
		if(firstChar == '*')
			Operation = "*";
		
		if(firstChar == '-')
			Operation = "-";
		
		if(Operation == "")
			Operation = "*";
		else
			val = val.substring(1, val.length());
		
		return Double.valueOf(val);
	}

	@Override
	public boolean modify(Event event) {
		if(!(event instanceof EntityDamageEvent)) return false;
		EntityDamageEvent Eevent = (EntityDamageEvent) event;
		
		if(Eevent.getEntityType() != EntityType.PLAYER) return false;
		Player target = (Player) Eevent.getEntity();
		
		if(checkContainer(target.getName())){
			int newValue = (int) Math.ceil(getNewValue(Eevent.getDamage()));
			Eevent.setDamage(newValue);
			return true;
		}
		return false;
	}
	
	private boolean checkContainer(String playerName){
		if(raceContainer != null){
			RaceContainer container = RaceManager.getManager().getRaceOfPlayer(playerName);
			if(container == null) return true;
			return raceContainer == container;
		}
		if(classContainer != null){
			ClassContainer container = ClassManager.getInstance().getClassOfPlayer(playerName);
			if(container == null) return true;
			return classContainer == container;
		}
		
		return false;
	}
	
	private double getNewValue(int oldDmg){
		double newDmg = 0;
		switch(Operation){
			case "": newDmg = oldDmg * value; break;
			case "+": newDmg = oldDmg + value; break;
			case "-" : newDmg = oldDmg - value; break;
			case "*": newDmg = oldDmg * value; break;
			default:  newDmg = oldDmg * value; break;
		}
		
		if(newDmg < 0) newDmg = 0;
		return newDmg;
	}
	
	public static void pasteHelpForTrait(CommandSender sender) {
		sender.sendMessage(ChatColor.YELLOW + "Your taken damage will be decreased or divided by an value.");
		return;
	}
	
	@Override
	public boolean isVisible() {
		return true;
	}
}