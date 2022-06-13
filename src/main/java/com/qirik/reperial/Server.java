package com.qirik.reperial;

import net.minestom.server.MinecraftServer;
import net.minestom.server.world.Difficulty;

public class Server {

    public static void main(String[] args) {

        MinecraftServer server = MinecraftServer.init();
        server.start("0.0.0.0", 25565);
        server.setBrandName("Reperial");
        server.setDifficulty(Difficulty.NORMAL);
    }
}
