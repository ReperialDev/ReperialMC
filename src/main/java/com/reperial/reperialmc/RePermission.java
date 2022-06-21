package com.reperial.reperialmc;

import net.minestom.server.permission.Permission;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;

public class RePermission extends Permission {
    public RePermission(@NotNull String id, @NotNull String permissionName) {
        super(permissionName, NBT.Compound(compound -> compound.set("id", NBT.String(id))));
        Server.getPermissions().add(this);
    }
}
