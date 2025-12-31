package com.filter.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * @author cshisan
 */
@OnlyIn(Dist.CLIENT)
public final class FilterClientCommands {
    public static void mark(ResourceLocation id, boolean isTag) {
        send("filter add %s %s".formatted(isTag ? "tag" : "item", id));
    }

    public static void unmark(ResourceLocation id, boolean isTag) {
        send("filter remove %s %s".formatted(isTag ? "tag" : "item", id));
    }

    private static void send(String command) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.player.connection.sendCommand(command);
        }
    }
}
