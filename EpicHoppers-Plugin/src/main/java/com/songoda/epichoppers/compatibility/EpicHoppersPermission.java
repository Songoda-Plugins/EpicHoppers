package com.songoda.epichoppers.compatibility;

import com.songoda.skyblock.permission.BasicPermission;
import com.songoda.skyblock.permission.PermissionType;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;

public class EpicHoppersPermission extends BasicPermission {
    public EpicHoppersPermission() {
        super("EpicHoppers", XMaterial.HOPPER, PermissionType.GENERIC);
    }
}
