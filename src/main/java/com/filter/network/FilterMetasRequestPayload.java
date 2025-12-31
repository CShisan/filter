package com.filter.network;

import com.filter.FilterMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * @author cshisan
 */
public record FilterMetasRequestPayload() implements CustomPacketPayload {
    public static final FilterMetasRequestPayload INSTANCE = new FilterMetasRequestPayload();
    public static final Type<FilterMetasRequestPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(FilterMod.MOD_ID, "marked_metas_request")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, FilterMetasRequestPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<FilterMetasRequestPayload> type() {
        return TYPE;
    }
}
