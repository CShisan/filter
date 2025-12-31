package com.filter.client.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author cshisan
 */
public record MarkMeta(boolean tag, ResourceLocation id, List<ItemStack> icons) {
    public static final Codec<MarkMeta> CODEC;
    public static final StreamCodec<ByteBuf, MarkMeta> STREAM_CODEC;

    static {
        CODEC = RecordCodecBuilder.<MarkMeta>create(instance -> instance.group(
                Codec.BOOL.fieldOf("tag").forGetter(MarkMeta::tag),
                ResourceLocation.CODEC.fieldOf("id").forGetter(MarkMeta::id)
        ).apply(instance, MarkMeta::create)).stable();
        STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, MarkMeta::tag,
                ResourceLocation.STREAM_CODEC, MarkMeta::id,
                MarkMeta::create
        );
    }

    public static MarkMeta create(boolean isTag, ResourceLocation id) {
        List<ItemStack> stacks = new ArrayList<>();

        if (isTag) {
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, id);
            Optional<HolderSet.Named<Item>> tag = BuiltInRegistries.ITEM.getTag(tagKey);
            tag.ifPresent(t -> stacks.addAll(
                    t.stream().map(ItemStack::new).toList()
            ));
        } else {
            var item = BuiltInRegistries.ITEM.getOptional(id);
            item.ifPresent(i -> stacks.add(new ItemStack(i)));
        }

        return new MarkMeta(isTag, id, stacks);
    }

    public ItemStack icon() {
        if (icons == null || icons.isEmpty()) {
            return ItemStack.EMPTY;
        }
        long second = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        return icons.get((int) (second % icons.size()));
    }
}
