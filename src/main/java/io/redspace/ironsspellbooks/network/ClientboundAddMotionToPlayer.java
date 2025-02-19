package io.redspace.ironsspellbooks.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundAddMotionToPlayer {
    private final double x, y, z;
    private final boolean preserveMomentum;

    public ClientboundAddMotionToPlayer(double x, double y, double z, boolean preserveMomentum) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.preserveMomentum = preserveMomentum;
    }

    public ClientboundAddMotionToPlayer(Vector3d motion, boolean preserveMomentum) {
        this.x = motion.x;
        this.y = motion.y;
        this.z = motion.z;
        this.preserveMomentum = preserveMomentum;
    }

    public ClientboundAddMotionToPlayer(PacketBuffer buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        preserveMomentum = buf.readBoolean();
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeBoolean(preserveMomentum);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
        });
        if(preserveMomentum)
            Minecraft.getInstance().player.push(x,y,z);
        else
            Minecraft.getInstance().player.setDeltaMovement(x,y,z);
        return true;
    }
}
