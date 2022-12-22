package com.example.testmod;

import com.example.testmod.item.SpellBook;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class StaticEventHandler {
    /*
    @SubscribeEvent
    public void pickupItem(EntityItemPickupEvent event) {
        System.out.println("Item picked up!");
    }
     */
    //public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, TestMod.MODID);
    //public static final RegistryObject<Attribute> MAX_MANA = registerAttribute(ATTRIBUTES,TestMod.MODID,"max_mana",(id)->new RangedAttribute(id,100,0,2048).setSyncable(true));
    @SubscribeEvent
    public static void RightClickItem(PlayerInteractEvent.RightClickItem event) {

        if(!event.getWorld().isClientSide())
            return;
        System.out.println("click");
        Player player = event.getPlayer();
        ItemStack stack = player.getItemInHand(player.getUsedItemHand());
        if(stack.getItem() instanceof SpellBook){
            System.out.println("spellbook");

        }
        //System.out.println(player);
        //System.out.println(stack);
    }
    /*moved to non-static event handler

    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent e){
        e.getTypes().forEach(entity->{
            e.add(entity, MAX_MANA.get());
            System.out.println("modifying: "+entity);
        });

    }
    public static RegistryObject<Attribute> registerAttribute(DeferredRegister<Attribute> registry, String modId, String name, Function<String, Attribute> attribute) {
        RegistryObject<Attribute> registryObject = registry.register(name, () -> attribute.apply("attribute.name." + modId + "." + name));
        return registryObject;
    }
     */
}