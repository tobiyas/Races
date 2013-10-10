package de.tobiyas.racesandclasses.playermanagement.health.display;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.tobiyas.racesandclasses.util.consts.Consts;

public class ChatDisplayBar extends AbstractDisplay{

	
	
	/**
	 * Inits the display with a Player to post to.
	 * 
	 * @param player to display to
	 * @param displayInfo the type of display to show
	 */
	public ChatDisplayBar(String playerName, DisplayInfos displayInfos) {
		super(playerName, displayInfos);
	}

	
	@Override
	public void display(double currentAmount, double maxAmount) {
		String barString = calcForHealth(currentAmount, maxAmount, Consts.displayBarLength);
		
		int pre = (int) Math.floor(currentAmount);
		int after = (int) Math.floor(currentAmount * 100D) % 100;
		
		String healthAsNumbers = colorMedium + " " + getColorOfPercent(currentAmount, maxAmount) + 
				pre + "." + after + colorMedium + "/" + colorHigh + maxAmount;
		
		
		Player player = Bukkit.getPlayer(playerName);
		if(player != null && player.isOnline()){
			player.sendMessage(displayInfo.getMidValueColor() + displayInfo.getName() + ": " + barString + healthAsNumbers);
		}
	}
	
	
	
}
