package io.redspace.ironsspellbooks.entity.spells.acid_orb;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.matrix.MatrixStack.Entry;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class AcidOrbRenderer extends EntityRenderer<AcidOrb> {

    public static final ModelLayerLocation MODEL_LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(IronsSpellbooks.MODID, "acid_orb_model"), "main");
    private static ResourceLocation ORB_TEXTURE = IronsSpellbooks.id("textures/entity/acid_orb/acid_orb.png");
    private static ResourceLocation SWIRL_TEXTURES[] = {
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_0.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_1.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_2.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_3.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_4.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_5.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_6.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_7.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_8.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_9.png"),
            IronsSpellbooks.id("textures/entity/acid_orb/swirl_10.png")
    };

    private final ModelRenderer orb;
    private final ModelRenderer swirl;

    public AcidOrbRenderer(Context context) {
        super(context);
        ModelRenderer modelpart = context.bakeLayer(MODEL_LAYER_LOCATION);
        this.orb = modelpart.getChild("orb");
        this.swirl = modelpart.getChild("swirl");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("orb", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("swirl", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
        return LayerDefinition.create(meshdefinition, 8, 8);
    }

    @Override
    public void render(AcidOrb entity, float yaw, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int light) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBoundingBox().getYsize() * .5f, 0);

        Entry pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        Vector3d motion = entity.getDeltaMovement();
        float xRot = -((float) (MathHelper.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (MathHelper.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(yRot));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(xRot));
        IVertexBuilder consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        this.orb.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY);

        float f = entity.tickCount + partialTicks;
        float swirlX = MathHelper.cos(.08f * f) * 180;
        float swirlY = MathHelper.sin(.08f * f) * 180;
        float swirlZ = MathHelper.cos(.08f * f + 5464) * 180;
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
    public ResourceLocation getTextureLocation(AcidOrb entity) {
        return ORB_TEXTURE;
    }

    private ResourceLocation getSwirlTextureLocation(AcidOrb entity) {
        int frame = (entity.tickCount / 2) % SWIRL_TEXTURES.length;
        return SWIRL_TEXTURES[frame];
    }
}