package com.filter;

import com.filter.client.FilterClientState;
import com.filter.client.model.MarkMeta;
import com.filter.network.FilterMetasPayload;
import com.filter.network.FilterMetasRequestPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

/**
 * @author cshisan
 */
public final class FilterNetwork {
    private static final String NETWORK_VERSION = "1";

    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(NETWORK_VERSION).optional();
        registrar.playToClient(FilterMetasPayload.TYPE, FilterMetasPayload.STREAM_CODEC, FilterNetwork::handleMarkMetasSync);
        registrar.playToServer(FilterMetasRequestPayload.TYPE, FilterMetasRequestPayload.STREAM_CODEC, FilterNetwork::handleMarkMetasRequest);
    }

    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncToPlayer(player);
        }
    }

    public static void syncToPlayer(ServerPlayer player) {
        // 将服务端记录的标记列表同步到客户端界面
        List<MarkMeta> metas = FilterPlayerData.read(player);
        PacketDistributor.sendToPlayer(player, new FilterMetasPayload(metas));
    }

    public static void requestSyncOnClient() {
        PacketDistributor.sendToServer(FilterMetasRequestPayload.INSTANCE);
    }

    private static void handleMarkMetasSync(FilterMetasPayload payload, IPayloadContext context) {
        FilterClientState.setMetas(payload.metas());
    }

    private static void handleMarkMetasRequest(FilterMetasRequestPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            syncToPlayer(player);
        }
    }
}
