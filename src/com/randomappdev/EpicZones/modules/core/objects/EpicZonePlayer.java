/*

This file is part of EpicZones

Copyright (C) 2011 by Team ESO

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

 */

/**
 * @author jblaske@gmail.com
 * @license MIT License
 */

package com.randomappdev.EpicZones.modules.core.objects;

import com.randomappdev.EpicZones.modules.core.permissionsManager;
import com.randomappdev.EpicZones.modules.herochat.herochatManager;
import com.randomappdev.EpicZones.modules.spout.spoutManager;
import com.randomappdev.EpicZones.utilities.Config;
import com.randomappdev.EpicZones.utilities.Globals;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.Date;

public class EpicZonePlayer
{

    private EpicZone currentZone;
    private Player player;
    private String name;
    private Location currentLocation;
    private Date lastWarned = new Date();
    private boolean teleporting = false;
    private Date lastCheck = new Date();
    private EpicZoneMode mode = EpicZoneMode.None;
    private EpicZone editZone = null;
    private boolean pastBorder = false;
    private Date enteredZone = new Date();
    private String previousZoneTag = "";
    private boolean hasMoved = false;
    private Date lastMoved = new Date();
    private boolean admin = false;
    private SpoutPlayer spoutPlayer = null;
    public EpicZonePlayerUI UI = new EpicZonePlayerUI();

    public EpicZonePlayer(Player newplayer)
    {
        this.player = newplayer;
        this.name = newplayer.getName();
        setCurrentLocation(newplayer.getWorld().getSpawnLocation());
        setCurrentZone(Globals.myGlobalZones.get(newplayer.getWorld().getName().toLowerCase()));
    }

    public EpicZonePlayer(String username)
    {
        this.player = null;
        this.name = username;
        this.admin = true;
        setCurrentLocation(Globals.plugin.getServer().getWorlds().get(0).getSpawnLocation());
        setCurrentZone(Globals.myGlobalZones.get(Globals.plugin.getServer().getWorlds().get(0).getName().toLowerCase()));
    }

    public SpoutPlayer getSpoutPlayer()
    {
        if (Globals.plugin.getServer().getPluginManager().isPluginEnabled("Spout"))
        {
            if (spoutPlayer == null)
            {
                if (this.player != null)
                {
                    spoutPlayer = SpoutManager.getPlayer(this.player);
                } else
                {
                    return null;
                }
            }
            return spoutPlayer;
        } else
        {
            return null;
        }
    }

    public EpicZone getCurrentZone()
    {
        return currentZone;
    }

    public String getName()
    {
        return name;
    }

    public Location getCurrentLocation()
    {
        return currentLocation;
    }

    public Date getLastWarned()
    {
        return lastWarned;
    }

    public Date getLastCheck()
    {
        return lastCheck;
    }

    public boolean isTeleporting()
    {
        return teleporting;
    }

    public EpicZoneMode getMode()
    {
        return mode;
    }

    public EpicZone getEditZone()
    {
        return editZone;
    }

    public boolean getPastBorder()
    {
        return pastBorder;
    }

    public Date getEnteredZone()
    {
        return enteredZone;
    }

    public String getPreviousZoneTag()
    {
        return previousZoneTag;
    }

    public boolean getHasMoved()
    {
        return hasMoved;
    }

    public Date getLastMoved()
    {
        return lastMoved;
    }

    public boolean getAdmin()
    {
        return admin;
    }

    public enum EpicZoneMode
    {
        None, ZoneDraw, ZoneEdit, ZoneDrawConfirm, ZoneDeleteConfirm
    }

    public void setHasMoved(boolean value)
    {
        this.hasMoved = value;
    }

    public void setPreviousZoneTag(String value)
    {
        previousZoneTag = value;
    }

    public void setPastBorder(boolean value)
    {
        this.pastBorder = value;
    }

    public void setMode(EpicZoneMode value)
    {
        this.mode = value;
    }

    public void setEditZone(@Nullable EpicZone value)
    {
        this.editZone = value;
    }

    public void setAdmin(boolean value)
    {
        this.admin = value;
    }

    public void setCurrentZone(EpicZone z)
    {

        if (this.currentZone != null && Config.enableHeroChat)
        {
            herochatManager.leaveChat(this.currentZone.getTag(), this);
        }

        this.currentZone = z;
        this.enteredZone = new Date();

        if (Config.enableHeroChat && Globals.herochatEnabled)
        {
            herochatManager.joinChat(z.getTag(), this);
        }

        if (Config.enableSpout && Globals.spoutEnabled)
        {
            spoutManager.UpdatePlayerZone(this, z);
        }

    }

    public void setCurrentLocation(Location l)
    {
        this.currentLocation = l.clone();
    }

    public void Warn()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 2);
        this.lastWarned = cal.getTime();
    }

    public void Check()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, 500);
        this.lastCheck = cal.getTime();
        this.lastMoved = cal.getTime();
    }

    public boolean shouldCheck()
    {
        boolean result = false;
        if (this.getLastCheck().before(new Date()))
        {
            result = true;
        }
        return result;
    }


    public void setIsTeleporting(boolean value)
    {
        teleporting = value;
    }

    public void updateSecurity()
    {
        if (permissionsManager.hasPermission(this.player, "epiczones.admin"))
        {
            this.setAdmin(true);
        } else if (player.isOp())
        {
            this.setAdmin(true);
        } else
        {
            this.setAdmin(false);
        }
    }

}
