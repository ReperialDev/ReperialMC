package com.reperial.reperialmc;

import com.reperial.reperialmc.commands.admin.ChatCommand;
import com.reperial.reperialmc.commands.admin.GamemodeCommand;
import com.reperial.reperialmc.commands.admin.PermissionCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.permission.Permission;
import net.minestom.server.tag.Tag;
import net.minestom.server.world.Difficulty;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Server {

    private static final Set<Permission> permissions = new HashSet<>();

    public static void main(String[] args) {

        MinecraftServer server = MinecraftServer.init();

        CommandManager manager =  MinecraftServer.getCommandManager();
        manager.register(new GamemodeCommand());
        manager.register(new PermissionCommand());
        manager.register(new ChatCommand());

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
            player.setTag(Tag.Integer("colorchat"), NamedTextColor.WHITE.value());
        });
        globalEventHandler.addListener(PlayerChatEvent.class, event -> event.setChatFormat((e) ->
            Component.text(event.getEntity().getUsername()).append(Component.text(": ")
                    .append(Component.text(event.getMessage(), Style.empty().color(TextColor.color(e.getPlayer().getTag(Tag.Integer("colorchat")))))))));
        server.start("0.0.0.0", 25565);
    }

    public static @UnmodifiableView Set<Permission> getPermissions() { return Collections.unmodifiableSet(permissions); }

    public static Permission registerPermission(String permissionName) {
        Permission permission = new Permission(permissionName);
        permissions.add(permission);
        return permission;
    }

}
