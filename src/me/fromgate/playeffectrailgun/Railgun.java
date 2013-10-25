package me.fromgate.playeffectrailgun;

import java.util.List;

import me.fromgate.playeffect.PlayEffect;
import me.fromgate.playeffect.Util;
import me.fromgate.playeffect.VisualEffect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class Railgun extends JavaPlugin implements Listener {
    
    
    @Override
    public void onEnable(){
        if (getServer().getPluginManager().getPlugin("PlayEffect")==null){
            getLogger().info("[PlayEffectRailgun] PlayEffect plugin required!");
            return;
        }
        
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void railShot(PlayerInteractEvent event){
        if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
        Player p = event.getPlayer();
        if (p.getItemInHand()==null) return;
        if (p.getItemInHand().getType()!= Material.BLAZE_ROD) return;
        Location loc1 = p.getEyeLocation();
        @SuppressWarnings("deprecation")
        Location loc2 = p.getTargetBlock(null, 100).getLocation();
        if (loc1 == null) return;
        if (loc2 == null) return;
        List<Location> line = Util.buildLine(loc1, loc2); // provided by PlayEffect
        if (line.size()<=2) return;
        line.remove(0);
        
        if (p.hasMetadata("railgun")&&((System.currentTimeMillis()-p.getMetadata("railgun").get(0).asLong())<=15000)){
                PlayEffect.play(VisualEffect.CLOUD, line.get(1), "num:5");
                PlayEffect.play(VisualEffect.SOUND, line.get(1),"type:FUSE");
                return;
        }
              
        p.setMetadata("railgun", new FixedMetadataValue (this, System.currentTimeMillis()));
        
        for (Location l : line){
            // PlayEffect: play firework effect to simulate railgun trails
            String param = "loc:"+Util.locationToStrLoc(loc1)+" loc2:"+Util.locationToStrLoc(loc2)+" draw:line color:aqua type:ball";
            PlayEffect.play(VisualEffect.FIREWORK, param);
            
            for (Entity e : l.getChunk().getEntities()){
                if (!(e instanceof LivingEntity)) continue;
                LivingEntity le = (LivingEntity) e;
                if (le.getLocation().distance(l)<=1.5) le.damage(le.getMaxHealth()*0.55);
            }
        }
        
        
    }
    
    

}
