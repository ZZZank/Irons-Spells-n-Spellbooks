package io.redspace.ironsspellbooks.network;

import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundUpdateCastingState {

    private final String spellId;
    private final int spellLevel;
    private final int castTime;
    private final CastSource castSource;

    public ClientboundUpdateCastingState(String spellId, int spellLevel, int castTime, CastSource castSource) {
        this.spellId = spellId;
        this.spellLevel = spellLevel;
        this.castTime = castTime;
        this.castSource = castSource;
    }

    public ClientboundUpdateCastingState(PacketBuffer buf) {
        this.spellId = buf.readUtf();
        this.spellLevel = buf.readInt();
        this.castTime = buf.readInt();
        this.castSource = buf.readEnum(CastSource.class);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeUtf(this.spellId);
        buf.writeInt(this.spellLevel);
        buf.writeInt(this.castTime);
        buf.writeEnum(this.castSource);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> ClientMagicData.setClientCastState(spellId, spellLevel, castTime, castSource));
        return true;
    }
}
