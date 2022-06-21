package com.reperial.reperialmc.commands.admin;

import com.reperial.reperialmc.RePermission;
import com.reperial.reperialmc.Server;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.permission.Permission;
import net.minestom.server.utils.entity.EntityFinder;
import org.jetbrains.annotations.NotNull;

import static net.minestom.server.command.builder.arguments.ArgumentType.String;
import static net.minestom.server.command.builder.arguments.ArgumentType.*;

import java.lang.String;

public class PermissionCommand extends Command {
    public PermissionCommand() {
        super("permission");

        setDefaultExecutor((sender, context) -> sender.sendMessage("please type /permission help for more information"));

        addConditionalSyntax(Conditions::playerOnly,(sender, context) -> {
            final EntityFinder finder = context.get("player");
            final Player player = finder.findFirstPlayer(sender);

            if (player != null) player.openInventory(new PermissionInventory((Player) sender));
            else sender.sendMessage(Component.text("Player not found !"));

        }, Literal("Show"), Entity("player").onlyPlayers(true).singleEntity(true));

        addSyntax((sender, context) -> {
            final EntityFinder finder = context.get("player");
            final Player player = finder.findFirstPlayer(sender);

            if (player != null) {
                player.addPermission(new Permission(context.get("permission").toString()));
                player.sendMessage("Add permission \"" + context.get("permission") + "\" to " + player.getUsername());
            } else sender.sendMessage(Component.text("Player not found !"));
        }, Literal("add"), Entity("player").onlyPlayers(true).singleEntity(true), String("permission"));

        addSyntax((sender, context) -> {
            final EntityFinder finder = context.get("player");
            final Player player = finder.findFirstPlayer(sender);

            if (player != null) {
                player.removePermission(context.get("permission").toString());
                player.sendMessage("Add permission \"" + context.get("permission") + "\" to " + player.getUsername());
            } else sender.sendMessage(Component.text("Player not found !"));
        }, Literal("remove"), Entity("player").onlyPlayers(true).singleEntity(true), String("permission"));

        addSyntax((sender, context) -> sender.sendMessage(
                Component.newline()
                        .append(Component.text("[ --  -- Permission Help -- -- ]"))
                        .append(Component.newline()).append(Component.text("/Permission show <player>", NamedTextColor.YELLOW))
                        .append(Component.newline()).append(Component.text("/Permission add <player> <permission>", NamedTextColor.YELLOW))
                        .append(Component.newline()).append(Component.text("/Permission remove <player> <permission>", NamedTextColor.YELLOW))), Literal("help"));
    }

    private static final class PermissionInventory extends Inventory {

        private PermissionInventory(@NotNull Player player) {
            super(InventoryType.CHEST_3_ROW, player.getName().append(Component.text( "'s permission")));
            for (RePermission permission : Server.getPermissions()) {
                final Material material = player.hasPermission(permission.getPermissionName()) ? Material.LIME_CONCRETE  : Material.RED_CONCRETE;
                final TextColor color = material.equals(Material.LIME_CONCRETE) ? NamedTextColor.GREEN : NamedTextColor.RED;

                addItemStack( ItemStack.builder(material)
                        .displayName(Component.text(permission.getNBTData().getString("id"), color).decoration(TextDecoration.ITALIC, false))
                        .lore(Component.text(permission.getPermissionName(), NamedTextColor.GRAY) , Component.text("Click to toggle permission", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build());
            }
            addInventoryCondition((holder, slot, clickType, result) -> {
                if (slot == -999 ||getItemStack(slot).isAir()) return;
                result.setCancel(true);
                final String permissionName = PlainTextComponentSerializer.plainText().serialize(getItemStack(slot).getLore().get(0));
                if (!holder.hasPermission(permissionName)) holder.addPermission(new Permission(permissionName));
                else holder.removePermission(permissionName);
                holder.refreshCommands();

                final Material material = getItemStack(slot).material().equals(Material.RED_CONCRETE) ? Material.LIME_CONCRETE : Material.RED_CONCRETE;
                final TextColor color = material.equals(Material.LIME_CONCRETE) ? NamedTextColor.GREEN : NamedTextColor.RED;
                setItemStack(slot, ItemStack.builder(material)
                        .displayName(getItemStack(slot).getDisplayName().color(color))
                        .lore(Component.text("Click to toggle permission", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                        .build());
            });
        }
    }
}
