package io.redspace.ironsspellbooks.entity.mobs.dead_king_boss;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.DefaultBipedBoneIdents.PartNames;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;

public class DeadKingRenderer extends AbstractSpellCastingMobRenderer {

    public DeadKingRenderer(EntityRendererManager renderManager) {
        super(renderManager, new DeadKingModel());
        this.addLayer((new DeadKingEmissiveLayer(this)));
    }

    @Override
    public void render(GeoModel model, AbstractSpellCastingMob animatable, float partialTick, RenderType type, MatrixStack poseStack, IRenderTypeBuffer bufferSource, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (animatable instanceof DeadKingBoss && ((DeadKingBoss) animatable).isPhase(DeadKingBoss.Phases.FinalPhase)) {
            model.getBone(PartNames.LEFT_LEG).ifPresent((bone) -> bone.setHidden(true));
            model.getBone(PartNames.RIGHT_LEG).ifPresent((bone) -> bone.setHidden(true));
        } else {
            model.getBone(PartNames.LEFT_LEG).ifPresent((bone) -> bone.setHidden(false));
            model.getBone(PartNames.RIGHT_LEG).ifPresent((bone) -> bone.setHidden(false));
        }
        super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void render(AbstractSpellCastingMob animatable, float entityYaw, float partialTick, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int packedLight) {
        super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void renderEarly(AbstractSpellCastingMob animatable, MatrixStack poseStack, float partialTick, IRenderTypeBuffer bufferSource, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks) {
        if (animatable instanceof DeadKingBoss)
            poseStack.scale(1.3f, 1.3f, 1.3f);
        super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, partialTicks);
    }

    @Override
    public RenderType getRenderType(AbstractSpellCastingMob animatable, float partialTick, MatrixStack poseStack, @Nullable IRenderTypeBuffer bufferSource, @Nullable IVertexBuilder buffer, int packedLight, ResourceLocation texture) {
        return animatable.isInvisible() ? RenderType.entityTranslucent(texture) : RenderType.entityCutoutNoCull(texture);
    }
}
