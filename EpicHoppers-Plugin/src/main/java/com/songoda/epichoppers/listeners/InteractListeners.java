package com.songoda.epichoppers.listeners;

import com.songoda.core.chat.AdventureUtils;
import com.songoda.core.hooks.ProtectionManager;
import com.songoda.core.hooks.WorldGuardHook;
import com.songoda.epichoppers.settings.Settings;
import com.songoda.epichoppers.EpicHoppers;
import com.songoda.epichoppers.gui.GUIOverview;
import com.songoda.epichoppers.hopper.HopperImpl;
import com.songoda.epichoppers.hopper.teleport.TeleportTrigger;
import com.songoda.epichoppers.player.PlayerData;
import com.songoda.epichoppers.player.SyncType;
import com.songoda.skyblock.SkyBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.InventoryHolder;

public class InteractListeners implements Listener {
    private final EpicHoppers plugin;

    public InteractListeners(EpicHoppers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking() && this.plugin.getHopperManager().isReady()) {
            Location location = player.getLocation().getBlock().getRelative(BlockFace.SELF).getLocation();
            Location down = location.getBlock().getRelative(BlockFace.DOWN).getLocation();
            if (this.plugin.getHopperManager().isHopper(down)) {
                HopperImpl hopper = this.plugin.getHopperManager().getHopper(down, player.getUniqueId());
                if (hopper.getTeleportTrigger() == TeleportTrigger.SNEAK) {
                    this.plugin.getTeleportHandler().tpEntity(player, hopper);
                }
            } else if (this.plugin.getHopperManager().isHopper(location)) {
                HopperImpl hopper = this.plugin.getHopperManager().getHopper(location, player.getUniqueId());
                if (hopper.getTeleportTrigger() == TeleportTrigger.SNEAK) {
                    this.plugin.getTeleportHandler().tpEntity(player, hopper);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.LEFT_CLICK_BLOCK
                || event.getClickedBlock() == null
                || player.isSneaking()
                || !player.hasPermission("EpicHoppers.overview")
                || !(event.getClickedBlock().getState() instanceof InventoryHolder || event.getClickedBlock().getType() == Material.ENDER_CHEST)) {
            return;
        }

        Boolean flagValue = WorldGuardHook.getBooleanFlag(event.getClickedBlock().getLocation().getChunk(), "use");
        boolean WGCheck = (flagValue == null) || flagValue;

        if (Settings.USE_PROTECTION_PLUGINS.getBoolean() && event.getClickedBlock().getType() == Material.HOPPER) {
            if (!WGCheck) {
                AdventureUtils.sendMessage(this.plugin, this.plugin.getLocale().getMessage("event.general.worldguard").getPrefixedMessage(), player);
                return;
            }

            if (!ProtectionManager.canInteract(player, event.getClickedBlock().getLocation())) {
                AdventureUtils.sendMessage(this.plugin, this.plugin.getLocale().getMessage("event.general.protectedclaim").getPrefixedMessage(), player);
                return;
            }
        }

        if (Bukkit.getPluginManager().isPluginEnabled("FabledSkyBlock")) {
            SkyBlock skyBlock = SkyBlock.getInstance();

            if (skyBlock.getWorldManager().isIslandWorld(event.getPlayer().getWorld()) &&
                    !skyBlock.getPermissionManager().hasPermission(event.getPlayer(), skyBlock.getIslandManager().getIslandAtLocation(event.getClickedBlock().getLocation()), "EpicHoppers")) {
                return;
            }
        }

        PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);

        if (playerData.getSyncType() == null) {
            if (event.getClickedBlock().getType() == Material.HOPPER) {
                if (!this.plugin.getHopperManager().isReady()) {
                    AdventureUtils.sendMessage(this.plugin, this.plugin.getLocale().getMessage("event.hopper.notready").getPrefixedMessage());
                    event.setCancelled(true);
                    return;
                }

                if (Settings.ALLOW_NORMAL_HOPPERS.getBoolean() && !this.plugin.getHopperManager().isHopper(event.getClickedBlock().getLocation())) {
                    return;
                }

                HopperImpl hopper = this.plugin.getHopperManager().getHopper(event.getClickedBlock(), player.getUniqueId());
                if (!player.getInventory().getItemInHand().getType().name().contains("PICKAXE")) {
                    if (hopper.prepareForOpeningOverviewGui(player)) {
                        this.plugin.getGuiManager().showGUI(player, new GUIOverview(this.plugin, hopper, player));
                    }
                    event.setCancelled(true);
                    return;
                }
            }
            return;
        }

        if (event.getClickedBlock().getState() instanceof InventoryHolder ||
                (event.getClickedBlock().getType() == Material.ENDER_CHEST && Settings.ENDERCHESTS.getBoolean())) {
            HopperImpl hopper = (HopperImpl) playerData.getLastHopper();
            if (event.getClickedBlock().getLocation().equals(playerData.getLastHopper().getLocation())) {
                if (!hopper.getLinkedBlocks().isEmpty()) {
                    this.plugin.getLocale().getMessage("event.hopper.syncfinish").sendPrefixedMessage(player);
                } else {
                    this.plugin.getLocale().getMessage("event.hopper.synccanceled").sendPrefixedMessage(player);
                }
                hopper.cancelSync(player);
            } else if (playerData.getSyncType() != null) {
                hopper.link(event.getClickedBlock(), playerData.getSyncType() == SyncType.FILTERED, player);
            }
            event.setCancelled(true);
            int amountLinked = hopper.getLevel().getLinkAmount();
            if (hopper.getLinkedBlocks().size() >= amountLinked) {
                playerData.setSyncType(null);
            }
        }
    }
}
