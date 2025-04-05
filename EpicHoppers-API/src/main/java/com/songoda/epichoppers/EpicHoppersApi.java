package com.songoda.epichoppers;

import com.songoda.core.database.DataManager;
import com.songoda.epichoppers.boost.BoostManager;
import com.songoda.epichoppers.containers.ContainerManager;
import com.songoda.epichoppers.hopper.teleport.TeleportHandler;
import com.songoda.epichoppers.player.PlayerDataManager;
import com.songoda.epichoppers.hopper.levels.LevelManager;

public class EpicHoppersApi {
    private static EpicHoppersApi instance;

    private final LevelManager levelManager;
    private final BoostManager boostManager;
    private final ContainerManager containerManager;
    private final TeleportHandler teleportHandler;
    private final PlayerDataManager playerDataManager;
    private EpicHoppersApi(LevelManager levelManager,
                           BoostManager boostManager,
                           ContainerManager containerManager,
                           TeleportHandler teleportHandler,
                           PlayerDataManager playerDataManager) {
        this.levelManager = levelManager;
        this.boostManager = boostManager;
        this.containerManager = containerManager;
        this.teleportHandler = teleportHandler;
        this.playerDataManager = playerDataManager;
    }

    public LevelManager getLevelManager() {
        return this.levelManager;
    }

    public BoostManager getBoostManager() {
        return this.boostManager;
    }

    public ContainerManager getContainerManager() {
        return this.containerManager;
    }

    public TeleportHandler getTeleportHandler() {
        return this.teleportHandler;
    }

    public PlayerDataManager getPlayerDataManager() {
        return this.playerDataManager;
    }

    public static EpicHoppersApi getApi() {
        return instance;
    }

    static void initApi(LevelManager levelManager, BoostManager boostManager, ContainerManager containerManager, TeleportHandler teleportHandler, PlayerDataManager playerDataManager) {
        if (instance != null) {
            throw new IllegalStateException(EpicHoppersApi.class.getSimpleName() + " already initialized");
        }
        instance = new EpicHoppersApi(levelManager, boostManager, containerManager, teleportHandler, playerDataManager);
    }
}
