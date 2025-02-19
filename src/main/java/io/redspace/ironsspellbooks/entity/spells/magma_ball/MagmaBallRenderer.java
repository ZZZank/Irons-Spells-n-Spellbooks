package io.redspace.ironsspellbooks.entity.spells.magma_ball;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Vector3f;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.acid_orb.AcidOrbRenderer;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class MagmaBallRenderer extends EntityRenderer<FireBomb> {

    private static ResourceLocation TEXTURE = IronsSpellbooks.id("textures/entity/fireball/magma.png");
    private static ResourceLocation SWIRL_TEXTURES[] = {
            IronsSpellbooks.id("textures/entity/fireball/swirl_0.png"),
            IronsSpellbooks.id("textures/entity/fireball/swirl_1.png"),
            IronsSpellbooks.id("textures/entity/fireball/swirl_2.png"),
            IronsSpellbooks.id("textures/entity/fireball/swirl_3.png"),
            IronsSpellbooks.id("textures/entity/fireball/swirl_4.png"),
            IronsSpellbooks.id("textures/entity/fireball/swirl_5.png"),
            IronsSpellbooks.id("textures/entity/fireball/swirl_6.png"),
            IronsSpellbooks.id("textures/entity/fireball/swirl_7.png"),
            IronsSpellbooks.id("textures/entity/fireball/swirl_8.png"),
            IronsSpellbooks.id("textures/entity/fireball/swirl_9.png"),
            IronsSpellbooks.id("textures/entity/fireball/swirl_10.png")
    };

    private final ModelRenderer orb;
    private final ModelRenderer swirl;

    public MagmaBallRenderer(Context context) {
        super(context);
        ModelRenderer modelpart = context.bakeLayer(AcidOrbRenderer.MODEL_LAYER_LOCATION);
        this.orb = modelpart.getChild("orb");
        this.swirl = modelpart.getChild("swirl");
    }

    @Override
    public void render(FireBomb entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int light) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBoundingBox().getYsize() * .5f, 0);

        Vector3d motion = entity.getDeltaMovement();
        float xRot = -((float) (MathHelper.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (MathHelper.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(yRot));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(xRot));

        float f = entity.tickCount + partialTicks;
        float swirlX = MathHelper.cos(.08f * f) * 130;
        float swirlY = MathHelper.sin(.08f * f) * 130;
        float swirlZ = MathHelper.cos(.08f * f + 5464) * 130;
        poseStack.mulPose(Vector3f.XP.rotationDegrees(swirlX));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(swirlY));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(swirlZ));
        IVertexBuilder consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        this.orb.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

        poseStack.mulPose(Vector3f.XP.rotationDegrees(swirlX));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(swirlY));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(swirlZ));
        consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getSwirlTextureLocation(entity)));
        poseStack.scale(1.15f, 1.15f, 1.15f);
        this.swirl.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY);


        poseStack.popPose();

        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    @Override
    public ResourceLocation getTextureLocation(FireBomb entity) {
        return TEXTURE;
    }

    private ResourceLocation getSwirlTextureLocation(FireBomb entity) {
        int frame = (entity.tickCount) % SWIRL_TEXTURES.length;
        return SWIRL_TEXTURES[frame];
    }
}