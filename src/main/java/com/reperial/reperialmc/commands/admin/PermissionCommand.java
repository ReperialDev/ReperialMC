package com.reperial.reperialmc.commands.admin;

import com.reperial.reperialmc.Server;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentEntity;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.permission.Permission;
import net.minestom.server.utils.entity.EntityFinder;
import org.jetbrains.annotations.NotNull;

import static net.minestom.server.command.builder.arguments.ArgumentType.*;

import java.lang.String;

public class PermissionCommand extends Command {
    public PermissionCommand() {
        super("permission");

        MiniMessage mm = MiniMessage.miniMessage();
        ArgumentEntity player = Entity("player").onlyPlayers(true).singleEntity(true);
        ArgumentString pemissionName = String("permission");

        setDefaultExecutor((sender, context) -> sender.sendMessage("please type /permission help for more information"));

        addConditionalSyntax(Conditions::playerOnly,(sender, context) -> {
            final EntityFinder finder = context.get("player");
            final Player p = finder.findFirstPlayer(sender);

            if (p != null) p.openInventory(new PermissionInventory((Player) sender));
            else sender.sendMessage(Component.text("Player not found !"));

        }, Literal("show"), player);

        addSyntax((sender, context) -> {
            final EntityFinder finder = context.get("player");
            final Player p = finder.findFirstPlayer(sender);

            if (p != null) {
                p.addPermission(new Permission(context.get("permission").toString()));
                p.refreshCommands();
                p.sendMessage("Add permission \"" + context.get("permission") + "\" to " + p.getUsername());
            } else sender.sendMessage(Component.text("Player not found !"));
        }, Literal("add"), player, pemissionName);

        addSyntax((sender, context) -> {
            final EntityFinder finder = context.get("player");
            final Player p = finder.findFirstPlayer(sender);

            if (p != null) {
                p.removePermission(context.get("permission").toString());
                p.refreshCommands();
                p.sendMessage("Add permission \"" + context.get("permission") + "\" to " + p.getUsername());
            } else sender.sendMessage(Component.text("Player not found !"));
        }, Literal("remove"), player, pemissionName);

        addSyntax((sender, context) -> sender.sendMessage(
                mm.deserialize("<yellow>---------[</yellow> Permission Help <yellow>]----------------</yellow>").append(Component.newline())
                        .append(Component.newline()).append(mm.deserialize("<yellow>/Permission show</yellow> <player>"))
                        .append(Component.newline()).append(mm.deserialize("<yellow>/Permission add</yellow> <player> <permission>"))
                        .append(Component.newline()).append(mm.deserialize("<yellow>/Permission remove</yellow> <player> <permission>"))
                        .append(Component.newline())), Literal("help"));
    }

    private static final class PermissionInventory extends Inventory {

        private PermissionInventory(@NotNull Player player) {
            super(InventoryType.CHEST_3_ROW, player.getName().append(Component.text( "'s permission")));

            ItemStack lime = ItemStack.builder(Material.LIME_CONCRETE)
                                .lore(Component.empty(),
                                        Component.text("Click to toggle permission", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                                .build();

            ItemStack red = ItemStack.builder(Material.RED_CONCRETE)
                    .lore(Component.empty(),
                            Component.text("Click to toggle permission", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                    .build();

            for (Permission permission : Server.getPermissions()) {

                final TextColor color = player.hasPermission(permission) ? NamedTextColor.GREEN : NamedTextColor.RED;
                final ItemStack itemStack = color.equals(NamedTextColor.GREEN) ? lime : red;

                addItemStack(itemStack.withDisplayName(Component.text(permission.getPermissionName(), color).decoration(TextDecoration.ITALIC, false)));
            }

            addInventoryCondition((holder, slot, clickType, result) -> {
                if (slot == -999 ||getItemStack(slot).isAir()) return;
                result.setCancel(true);
                final String permissionName = PlainTextComponentSerializer.plainText().serialize(getItemStack(slot).getDisplayName());
                if (!holder.hasPermission(permissionName)) {
                    holder.addPermission(new Permission(permissionName));
                    setItemStack(slot, getItemStack(slot).withMaterial(Material.LIME_CONCRETE).withDisplayName(getItemStack(slot).getDisplayName().color(NamedTextColor.GREEN)));
                } else {
                    holder.removePermission(permissionName);
                    setItemStack(slot, getItemStack(slot).withMaterial(Material.RED_CONCRETE).withDisplayName(getItemStack(slot).getDisplayName().color(NamedTextColor.RED)));
                }
                holder.refreshCommands();
            });
        }
    }
}
