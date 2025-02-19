package io.redspace.ironsspellbooks.api.spells;

import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

public class SchoolType implements IForgeRegistryEntry<SchoolType> {
//    FIRE(0),
//    ICE(1),
//    LIGHTNING(2),
//    HOLY(3),
//    ENDER(4),
//    BLOOD(5),
//    EVOCATION(6),
//    //VOID(7),
//    NATURE(8);

    final ResourceLocation id;
    final Tags.IOptionalNamedTag<Item> focus;
    final IFormattableTextComponent displayName;
    final Style displayStyle;
//    final PlaceholderDamageType damageType;
    final LazyOptional<Attribute> powerAttribute;
    final LazyOptional<Attribute> resistanceAttribute;
    final LazyOptional<SoundEvent> defaultCastSound;

    public SchoolType(ResourceLocation id, Tags.IOptionalNamedTag<Item> focus, IFormattableTextComponent displayName, LazyOptional<Attribute> powerAttribute, LazyOptional<Attribute> resistanceAttribute, LazyOptional<SoundEvent> defaultCastSound) {
        this.id = id;
        this.focus = focus;
        this.displayName = displayName;
        this.displayStyle = displayName.getStyle();
        this.powerAttribute = powerAttribute;
        this.resistanceAttribute = resistanceAttribute;
        this.defaultCastSound = defaultCastSound;
    }

    public double getResistanceFor(LivingEntity livingEntity) {
        Attribute resistanceAttribute = this.resistanceAttribute.orElse(null);
        if (resistanceAttribute != null) {
            return livingEntity.getAttributeValue(resistanceAttribute);
        } else {
            return 1;
        }
    }

    public double getPowerFor(LivingEntity livingEntity) {
        Attribute powerAttribute = this.powerAttribute.orElse(null);
        if (powerAttribute != null) {
            return livingEntity.getAttributeValue(powerAttribute);
        } else {
            return 1;
        }
    }

    public SoundEvent getCastSound() {
        return defaultCastSound.resolve().get();
    }

    public ResourceLocation getId() {
        return id;
    }

    public IFormattableTextComponent getDisplayName() {
        return displayName;
    }

    public boolean isFocus(ItemStack itemStack) {
        return focus.contains(itemStack.getItem());
    }

    public Vector3f getTargetingColor() {
        int decimal = this.displayStyle.getColor().getValue();
        //Copied from potion utils
        return new Vector3f(
                ((decimal >> 16) & 0xFF) / 255.0f,
                ((decimal >> 8) & 0xFF) / 255.0f,
                (decimal & 0xFF) / 255.0f
        );
    }

    @Override
    public SchoolType setRegistryName(ResourceLocation name) {
        assert name.equals(this.id);
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return id;
    }

    @Override
    public Class<SchoolType> getRegistryType() {
        return SchoolType.class;
    }
}
