package com.reperial.reperialmc.commands.admin;

import com.reperial.reperialmc.Server;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

import static net.minestom.server.command.builder.arguments.ArgumentType.Entity;

public class GamemodeCommand extends Command {
    public GamemodeCommand() {
        super("gamemode");

        setCondition((sender, commandString) -> sender instanceof ConsoleSender || sender.hasPermission(Server.registerPermission("reperial.command.gamemode")));
        setDefaultExecutor((sender, context) -> sender.sendMessage("Usage : /gamemode <gamemode>"));

        ArgumentEnum<GameMode> gamemode = ArgumentType.Enum("gamemode", GameMode.class).setFormat(ArgumentEnum.Format.LOWER_CASED);

        addSyntax(this::setGamemode, gamemode);
        addSyntax(this::setGamemode, gamemode, Entity("player").onlyPlayers(true).singleEntity(true));
    }

    private void setGamemode(CommandSender sender, CommandContext context) {
        if (context.get("player") == null) {
            if (sender instanceof Player player) {
                player.setGameMode(GameMode.valueOf(context.get("gamemode").toString()));
                sender.sendMessage(context.get("gamemode").toString());
                return;
            }
        }
        final EntityFinder finder = context.get("player");
        final Player player = finder.findFirstPlayer(sender);

        if (player != null) {
            player.setGameMode(GameMode.valueOf(context.get("gamemode").toString()));
            sender.sendMessage(context.get("gamemode").toString());
        }
        else sender.sendMessage(Component.text("Player not found !"));
    }

}
