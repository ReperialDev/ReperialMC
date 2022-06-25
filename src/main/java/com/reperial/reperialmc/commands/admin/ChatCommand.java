package com.reperial.reperialmc.commands.admin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;

import java.lang.Integer;
import java.lang.String;

import static net.minestom.server.command.builder.arguments.ArgumentType.*;

public class ChatCommand extends Command {

    public ChatCommand() {
        super("chat");

        addConditionalSyntax(Conditions::playerOnly, (sender, context) -> {
            if (sender instanceof Player player) {
                String color = "<" + ((Style) context.get("textcolor")).color()  +">";
                player.setTag(Tag.Integer("colorchat"), ((Style) context.get("textcolor")).color().value());
                player.sendMessage(MiniMessage.miniMessage().deserialize(color + "[ <reset>Chat " + color + "] <reset>" + "Change color message to " + color  + NamedTextColor.namedColor(((Style) context.get("textcolor")).color().value())));
            }
        }, Literal("color"), Color("textcolor"));

        ArgumentString hex = String("hex");
        Component invalidArgument = Component.text("Invalid argument. Please input a valid hex !");
        hex.setCallback((sender, exception) -> sender.sendMessage(invalidArgument));

        addConditionalSyntax(Conditions::playerOnly, (sender, context) -> {
            Player player = (Player) sender;
            String input = context.get("hex");
            if (input.startsWith("#") && input.length() == 7) {
                try {
                    int hexcolor = Integer.decode(input.replaceFirst("#", "0x"));
                    player.setTag(Tag.Integer("colorchat"), hexcolor);
                    String color = "<" + input + ">";
                    player.sendMessage(MiniMessage.miniMessage().deserialize(color +"[ <reset>Chat "+ color +"]<reset> " + "Change color message to" + color + " this"));
                } catch (NumberFormatException e) { player.sendMessage(invalidArgument);}
            } else player.sendMessage(invalidArgument);
        }, Literal("color"), hex);

        addConditionalSyntax(Conditions::playerOnly, (sender, context) -> {
            Player player = (Player) sender;
            player.setTag(Tag.String("prefix"), context.get("value"));
        }, Literal("prefix"), String("value"));

    }
}
