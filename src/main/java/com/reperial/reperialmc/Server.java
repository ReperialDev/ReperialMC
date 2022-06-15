package com.reperial.reperialmc;

import com.reperial.reperialmc.commands.admin.GamemodeCommand;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.world.Difficulty;

public class Server {

    public static void main(String[] args) {

        MinecraftServer server = MinecraftServer.init();
        MinecraftServer.setBrandName("Reperial");
        MinecraftServer.setDifficulty(Difficulty.NORMAL);

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        instanceContainer.setChunkLoader(new AnvilLoader("world"));

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 66, 0));
            player.setGameMode(GameMode.ADVENTURE);
        });

        MinecraftServer.getCommandManager().register(new GamemodeCommand());
        server.start("0.0.0.0", 25565);
    }



}
