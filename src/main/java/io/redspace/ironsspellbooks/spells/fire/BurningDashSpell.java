package io.redspace.ironsspellbooks.spells.fire;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.capabilities.magic.*;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.ISpellDamageSource;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.player.SpinAttackType;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import io.redspace.ironsspellbooks.api.util.Utils;
import java.util.Arrays;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@AutoSpellConfig
public class BurningDashSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "burning_dash");

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return Arrays.asList(new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(spellLevel, caster), 1)));
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
            .setMaxLevel(10)
            .setCooldownSeconds(10)
            .build();

    public BurningDashSpell() {
        this.manaCostPerLevel = 2;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = 0;
        this.baseManaCost = 20;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
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
    public void onClientCast(World level, int spellLevel, LivingEntity entity, ICastData castData) {
        if (castData instanceof ImpulseCastData) {
            ImpulseCastData bdcd = (ImpulseCastData) castData;
            entity.hasImpulse = bdcd.hasImpulse;
            entity.setDeltaMovement(entity.getDeltaMovement().add(bdcd.x, bdcd.y, bdcd.z));
        }

        super.onClientCast(level, spellLevel, entity, castData);
    }

    @Override
    public ICastDataSerializable getEmptyCastData() {
        return new ImpulseCastData();
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public void onCast(World world, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        entity.hasImpulse = true;
        float multiplier = (15 + getSpellPower(spellLevel, entity)) / 12f;

        //Direction for Mobs to cast in
        Vector3d forward = entity.getLookAngle();
        if (playerMagicData.getAdditionalCastData() instanceof BurningDashDirectionOverrideCastData) {
            if (Utils.random.nextBoolean())
                forward = forward.yRot(90);
            else
                forward = forward.yRot(-90);

        }

        //Create Dashing Movement Impulse
        Vector3d vec = forward.multiply(3, 1, 3).normalize().add(0, .25, 0).scale(multiplier);
        playerMagicData.setAdditionalCastData(new ImpulseCastData((float) vec.x, (float) vec.y, (float) vec.z, true));
        entity.setDeltaMovement(entity.getDeltaMovement().add(vec));

        //Start Spin Attack
        if (entity.isOnGround())
            entity.moveTo(entity.position().add(0, 1.2, 0));
        startSpinAttack(entity, 10);

        //Deal Shockwave Damage and particles
        world.getEntities(entity, entity.getBoundingBox().inflate(4)).forEach((target) -> {
            if (target.distanceToSqr(entity) < 16) {
                DamageSources.applyDamage(target, getDamage(spellLevel, entity), getDamageSource(entity), getSchoolType());
            }
        });
        MagicManager.spawnParticles(world, ParticleHelper.FIRE, entity.getX(), entity.getY(), entity.getZ(), 75, 1, 0, 1, .08, false);

        playerMagicData.getSyncedData().setSpinAttackType(SpinAttackType.FIRE);
        super.onCast(world, spellLevel, entity, playerMagicData);
    }

    @Override
    public DamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
        return ((ISpellDamageSource) super.getDamageSource(projectile, attacker)).setFireTime(4).get();
    }

    private float getDamage(int spellLevel, LivingEntity caster) {
        return 5 + getSpellPower(spellLevel, caster) / 2;
    }

    private void startSpinAttack(LivingEntity entity, int durationInTicks) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            player.startAutoSpinAttack(durationInTicks);
        } else if (entity instanceof AbstractSpellCastingMob) {
            AbstractSpellCastingMob mob = (AbstractSpellCastingMob) entity;
            mob.startAutoSpinAttack(durationInTicks);
        }
    }

    public static class BurningDashDirectionOverrideCastData implements ICastData {
        @Override
        public void reset() {

        }
    }
}
