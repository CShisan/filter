package com.filter;

import com.filter.client.model.MarkMeta;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;

final class FilterCommands {
    enum OpType {
        /**
         * 操作类型
         */
        ADD,
        REMOVE,
        STATUS
    }

    static void onRegisterCommands(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(FilterMod.MOD_ID);

        // 按手持/物品id/标签标记
        LiteralArgumentBuilder<CommandSourceStack> add = Commands.literal("add");
        add.then(Commands.literal("held").executes(context ->
                operate(context, false, OpType.ADD, true)
        ));
        add.then(Commands.literal("item").then(Commands.argument(
                "item", ItemArgument.item(event.getBuildContext())
        ).executes(context ->
                operate(context, false, OpType.ADD, false)
        )));
        add.then(Commands.literal("tag").then(Commands.argument(
                "tag", ResourceLocationArgument.id()
        ).executes(context ->
                operate(context, true, OpType.ADD, false)
        )));

        // 按手持/物品id/标签移除
        LiteralArgumentBuilder<CommandSourceStack> remove = Commands.literal("remove");
        remove.then(Commands.literal("held").executes(context ->
                operate(context, false, OpType.REMOVE, true)
        ));
        remove.then(Commands.literal("item").then(Commands.argument(
                "item", ItemArgument.item(event.getBuildContext())
        ).executes(context ->
                operate(context, false, OpType.REMOVE, false)
        )));
        remove.then(Commands.literal("tag").then(Commands.argument(
                "tag", ResourceLocationArgument.id()
        ).executes(context ->
                operate(context, true, OpType.REMOVE, false)
        )));

        // 按手持/物品id状态查询
        LiteralArgumentBuilder<CommandSourceStack> status = Commands.literal("status");
        status.then(Commands.literal("held").executes(context ->
                operate(context, false, OpType.STATUS, true)
        ));
        status.then(Commands.literal("item").then(Commands.argument(
                "item", ItemArgument.item(event.getBuildContext())
        ).executes(context ->
                operate(context, false, OpType.STATUS, false)
        )));

        // 列出已标记物品
        LiteralArgumentBuilder<CommandSourceStack> list = Commands.literal("list").executes(
                context -> list(context.getSource())
        );

        event.getDispatcher().register(builder.then(add).then(remove).then(status).then(list));
    }

    private static int add(CommandSourceStack source, ServerPlayer player, ResourceLocation id, boolean isTag, boolean marked) {
        if (marked) {
            source.sendFailure(Component.literal("该物品类型已被标记"));
            return 0;
        }
        FilterPlayerData.mark(player, id, isTag);
        FilterNetwork.syncToPlayer(player);
        source.sendSuccess(() -> Component.literal("已标记物品类型，标记者本人无法拾取同类物品。"), false);
        return 1;
    }

    private static int remove(CommandSourceStack source, ServerPlayer player, ResourceLocation id, boolean isTag, boolean marked) {
        if (!marked) {
            source.sendFailure(Component.literal("该物品类型未被标记"));
            return 0;
        }
        FilterPlayerData.unmark(player, id, isTag);
        FilterNetwork.syncToPlayer(player);
        source.sendSuccess(() -> Component.literal("已取消该物品类型的标记。"), false);
        return 1;
    }

    private static int operate(CommandContext<CommandSourceStack> context, boolean isTag, OpType type, boolean isHeld) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();

        ResourceLocation id;
        if (isTag) {
            id = ResourceLocationArgument.getId(context, "tag");
        } else if (isHeld) {
            // 检查是否手持物品
            ItemStack stack = player.getMainHandItem();
            if (stack.isEmpty()) {
                source.sendFailure(Component.literal("请手持要操作的物品"));
                return 0;
            }
            id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        } else {
            Item item = ItemArgument.getItem(context, "item").getItem();
            id = BuiltInRegistries.ITEM.getKey(item);
        }

        boolean marked = FilterPlayerData.isMarked(player, id);
        return switch (type) {
            case ADD -> add(source, player, id, isTag, marked);
            case REMOVE -> remove(source, player, id, isTag, marked);
            case STATUS -> {
                String message = marked ? "该物品类型已标记，标记者本人无法拾取同类物品" : "该物品类型未标记";
                source.sendSuccess(() -> Component.literal(message), false);
                yield 1;
            }
        };
    }

    private static int list(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        List<MarkMeta> metas = FilterPlayerData.read(player);
        if (metas.isEmpty()) {
            source.sendFailure(Component.literal("未找到标记物品。"));
            return 0;
        }
        source.sendSuccess(() -> Component.literal("已标记清单："), false);
        metas.forEach(meta -> {
            String prefix = meta.tag() ? "#" : "";
            String id = "- " + prefix + meta.id().toString();
            source.sendSuccess(() -> Component.literal(id), false);
        });
        return metas.size();
    }
}
