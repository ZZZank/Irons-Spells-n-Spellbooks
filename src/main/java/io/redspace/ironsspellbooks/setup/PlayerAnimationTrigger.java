package io.redspace.ironsspellbooks.setup;
// TODO: no need?
//
//import io.redspace.ironsspellbooks.IronsSpellbooks;
//import dev.kosmx.playerAnim.api.layered.IAnimation;
//import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
//import dev.kosmx.playerAnim.api.layered.ModifierLayer;
//import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
//import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.util.text.ITextComponent;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.text.StringTextComponent;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.client.event.ClientChatReceivedEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//
///**
// * Example, how to trigger animations on specific players
// * Always trigger animation on client-side.  Maybe as a response to a network packet or event
// */
//@Mod.EventBusSubscriber(modid = IronsSpellbooks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
//public class PlayerAnimationTrigger {
//
//    //We need to know when to play an animation
//    //This can be anything depending on your ideas (see Emotecraft, BetterCombat ...)
//    @SubscribeEvent
//    public static void onChatReceived(ClientChatReceivedEvent event) {
//        //Test if it is a player (main or other) and the message
//        if (event.getMessage().contains(new StringTextComponent("waving"))) {
//
//
//            //Get the player from Minecraft, using the chat profile ID. From network packets, you'll receive entity IDs instead of UUIDs
//            PlayerEntity player = Minecraft.getInstance().level.getPlayerByUUID(event.getMessageSigner().profileId());
//
//            if (player == null) return; //The player can be null because it was a system message or because it is not loaded by this player.
//
//            //Get the animation for that player
//            ModifierLayer<IAnimation> animation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData((AbstractClientPlayerEntity) player).get(new ResourceLocation(IronsSpellbooks.MODID, "animation"));
//            if (animation != null) {
//                //You can set an animation from anywhere ON THE CLIENT
//                //Do not attempt to do this on a server, that will only fail
//
//                animation.setAnimation(new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(new ResourceLocation(IronsSpellbooks.MODID, "waving"))));
//                //You might use  animation.replaceAnimationWithFade(); to create fade effect instead of sudden change
//                //See javadoc for details
//            }
//        }
//    }
//
//    //For server-side animation playing, see Emotecraft API
//}