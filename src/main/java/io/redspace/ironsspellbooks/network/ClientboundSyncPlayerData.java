package io.redspace.ironsspellbooks.network;


import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSyncPlayerData {
    SyncedSpellData syncedSpellData;

    public ClientboundSyncPlayerData(SyncedSpellData playerSyncedData) {
        this.syncedSpellData = playerSyncedData;
    }

    public ClientboundSyncPlayerData(PacketBuffer buf) {
        syncedSpellData = SyncedSpellData.SYNCED_SPELL_DATA.read(buf);
    }

    public void toBytes(PacketBuffer buf) {
        SyncedSpellData.SYNCED_SPELL_DATA.write(buf, syncedSpellData);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ClientMagicData.handlePlayerSyncedData(syncedSpellData);
        });

        return true;
    }
}