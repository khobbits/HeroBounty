/**
 * Copyright (C) 2011 DThielke <dave.thielke@gmail.com>
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 **/

package com.herocraftonline.dthielke.herobounty;

import java.util.List;

//import mc.alk.arena.BattleArena;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.herocraftonline.dthielke.herobounty.bounties.Bounty;

public class HeroBountyEntityListener implements Listener {
	public static HeroBounty plugin;

	public HeroBountyEntityListener(HeroBounty plugin) {
		HeroBountyEntityListener.plugin = plugin;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof Player)) {
			return;
		}
		Player defender = (Player) entity;
		String defenderName = defender.getName();

		EntityDamageEvent dmgEvent = entity.getLastDamageCause();
		Player attacker;
		String attackerName;
		if (dmgEvent instanceof EntityDamageByEntityEvent) {
			Entity attackingEnt = ((EntityDamageByEntityEvent) dmgEvent).getDamager();
			if (attackingEnt instanceof Projectile) {
				attackingEnt = ((Projectile) attackingEnt).getShooter();
			}
			if (attackingEnt instanceof Player) {
				attacker = (Player) attackingEnt;
				attackerName = ((Player) attackingEnt).getName();
			}
			else {
				return;
			}
		}
		else {
			return;
		}

		List<Bounty> bounties = plugin.getBountyManager().getBounties();
		for (int i = 0; i < bounties.size(); i++) {
			Bounty b = bounties.get(i);

			// Ensure that the players are not in an arena first
			//if (!(BattleArena.inArena((attacker)) || BattleArena.inArena((defender)))) {

				// Check to see if any players are claiming any bounties
				if (b.getTarget().equalsIgnoreCase(defenderName) && b.isHunter(attackerName)) {
					plugin.getBountyManager().completeBounty(i, attackerName);
					event.getDrops().add(getHeadOfDefender(defenderName));
					return;
				}
			}
		}
	//}

	private ItemStack getHeadOfDefender(String defenderName) {
		ItemStack skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
		skullMeta.setOwner(defenderName);
		skullItem.setItemMeta(skullMeta);
		return skullItem;
	}
}
