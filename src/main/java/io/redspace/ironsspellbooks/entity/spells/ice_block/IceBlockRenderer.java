package io.redspace.ironsspellbooks.entity.spells.ice_block;


import net.minecraft.client.renderer.entity.EntityRendererManager;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

public class IceBlockRenderer extends GeoProjectilesRenderer<IceBlockProjectile> {

    public IceBlockRenderer(EntityRendererManager context) {
        super(context, new IceBlockModel());
        this.shadowRadius = 1.5f;
    }

//    @Override
//    public void render(IceBlockProjectile animatable, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
//        animatable.xRot = (0);
//        animatable.xRotO = animatable.xRot;
//        if (animatable.getDeltaMovement().horizontalDistanceSqr() > 1)
//            animatable.yRot = ((float) (Mth.atan2(animatable.getDeltaMovement().x, animatable.getDeltaMovement().z) * (double) (180F / (float) Math.PI)));
//
//        double x = (animatable.getX() - animatable.xOld) * partialTick;
//        double y = (animatable.getY() - animatable.yOld) * partialTick;
//        double z = (animatable.getZ() - animatable.zOld) * partialTick;
//        poseStack.translate(-x, -y, -z);
//        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
//    }
}
