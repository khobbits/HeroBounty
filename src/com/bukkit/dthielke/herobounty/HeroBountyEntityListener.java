package com.bukkit.dthielke.herobounty;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class HeroBountyEntityListener extends EntityListener {
    public static HeroBountyPlugin plugin;
    
    private HashMap<String, String> deathRecords = new HashMap<String, String>();
 
    public HeroBountyEntityListener(HeroBountyPlugin plugin) {
        HeroBountyEntityListener.plugin = plugin;
    }
    
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player))
            return;
        
        Player defender = (Player)entity;
        
        String defenderName = defender.getName();
        String attackerName = deathRecords.get(defenderName);
        
        List<Bounty> bounties = plugin.getBounties();
        
        for (int i = 0; i < bounties.size(); i++) {
            Bounty b = bounties.get(i);
            
            if (b.getTarget().equalsIgnoreCase(defenderName) && b.isHunter(attackerName)) {
                plugin.completeBounty(i, attackerName);
                deathRecords.remove(defenderName);
                return;
            }
        }
    }
    
    public void onEntityDamageByProjectile(EntityDamageByProjectileEvent event) {
        if (event.isCancelled())
            return;
        
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player))
            return;
        
        Player defender = (Player)event.getEntity();
        Player attacker = (Player)event.getDamager();
        tryAddDeathRecord(defender, attacker, event.getDamage());
    }
    
    private void tryAddDeathRecord(Player defender, Player attacker, int damage) {
        String defenderName = defender.getName();
        String attackerName = attacker.getName();
        
        int health = defender.getHealth() - damage;
        
        if (health > 0)
            return;
        
        for (Bounty b : plugin.getBounties()) {
            if (b.isHunter(attackerName) && b.getTarget().equalsIgnoreCase(defenderName)) {                
                deathRecords.put(defenderName, attackerName);
                break;
            }
        }
    }
    
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;
        
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player))
            return;
        
        Player defender = (Player)event.getEntity();
        Player attacker = (Player)event.getDamager();
        tryAddDeathRecord(defender, attacker, event.getDamage());
    }
}
