package io.redspace.ironsspellbooks.spells.ender;


import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.entity.spells.AbstractConeProjectile;
import io.redspace.ironsspellbooks.entity.spells.dragon_breath.DragonBreathProjectile;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.util.math.vector.Vector3d;
import java.util.Arrays;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class DragonBreathSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "dragon_breath");

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return Arrays.asList(new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(spellLevel, caster), 1)));
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .setMaxLevel(10)
            .setCooldownSeconds(12)
            .build();

    public DragonBreathSpell() {
        this.manaCostPerLevel = 1;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = 100;
        this.baseManaCost = 5;
    }

    @Override
    public CastType getCastType() {
        return CastType.CONTINUOUS;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.ENDER_DRAGON_GROWL);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundRegistry.FIRE_BREATH_LOOP.get());
    }

    @Override
    public void onCast(World world, int spellLevel, LivingEntity entity, MagicData playerMagicData) {

        if (playerMagicData.isCasting() && playerMagicData.getCastingSpell().getSpell().equals(this)
                && playerMagicData.getAdditionalCastData() instanceof EntityCastData
                && ((EntityCastData) playerMagicData.getAdditionalCastData()).getCastingEntity() instanceof AbstractConeProjectile) {
            EntityCastData entityCastData = (EntityCastData) playerMagicData.getAdditionalCastData();
            AbstractConeProjectile cone = (AbstractConeProjectile) entityCastData.getCastingEntity();
            cone.setDealDamageActive();
        } else {
            DragonBreathProjectile dragonBreathProjectile = new DragonBreathProjectile(world, entity);
            dragonBreathProjectile.moveTo(entity.position().add(0, entity.getEyeHeight() * .7, 0));
            dragonBreathProjectile.setDamage(getDamage(spellLevel, entity));
            world.addFreshEntity(dragonBreathProjectile);

            playerMagicData.setAdditionalCastData(new EntityCastData(dragonBreathProjectile));
        }
        super.onCast(world, spellLevel, entity, playerMagicData);
    }

    public float getDamage(int spellLevel, LivingEntity caster) {
        return 1 + getSpellPower(spellLevel, caster) * .75f;
    }

    @Override
    public boolean shouldAIStopCasting(int spellLevel, MobEntity mob, LivingEntity target) {
        return mob.distanceToSqr(target) > (10 * 10) * 1.2;
    }

}
