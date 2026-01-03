package com.filter.common.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * @author cshisan
 */
public class TagHelper {
    public static List<ResourceLocation> item2tags(ResourceLocation itemId) {
        List<ResourceLocation> tags = new ArrayList<>();
        Optional<Holder.Reference<Item>> holder = BuiltInRegistries.ITEM.getHolder(itemId);
        holder.ifPresent(item -> item.tags().forEach(
                tagKey -> tags.add(tagKey.location())
        ));
        tags.sort(Comparator.comparing(ResourceLocation::toString));
        return tags;
    }
}
