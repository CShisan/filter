package com.filter.network;

import com.filter.FilterMod;
import com.filter.client.model.MarkMeta;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cshisan
 */
public record FilterMetasPayload(List<MarkMeta> metas) implements CustomPacketPayload {
    public static final Type<FilterMetasPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(FilterMod.MOD_ID, "marked_metas_sync")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, FilterMetasPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, MarkMeta.STREAM_CODEC),
            FilterMetasPayload::metas, FilterMetasPayload::new
    );

    @Override
    public @NotNull Type<FilterMetasPayload> type() {
        return TYPE;
    }
}
