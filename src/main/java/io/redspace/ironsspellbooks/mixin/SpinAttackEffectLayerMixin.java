package io.redspace.ironsspellbooks.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SpinAttackEffectLayer.class)
public class SpinAttackEffectLayerMixin {
    private static final ResourceLocation FIRE_TEXTURE = new ResourceLocation(IronsSpellbooks.MODID, "textures/entity/fire_riptide.png");

    @ModifyVariable(method = "render", at = @At("STORE"))
    public IVertexBuilder selectSpinAttackTexture(IVertexBuilder original, MatrixStack poseStack, IRenderTypeBuffer buffer, int p_117528_, LivingEntity livingEntity, float f1, float f2, float f3, float f4, float f5, float f6) {
        switch (ClientMagicData.getSyncedSpellData(livingEntity).getSpinAttackType()) {
            case FIRE:
                return buffer.getBuffer(RenderType.entityCutoutNoCull(FIRE_TEXTURE));
            default:
                return original;
        }
    }
}