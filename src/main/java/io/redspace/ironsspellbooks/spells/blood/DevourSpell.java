package io.redspace.ironsspellbooks.spells.blood;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.capabilities.magic.CastTargetingData;
import io.redspace.ironsspellbooks.damage.ISpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.devour_jaw.DevourJaw;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.util.math.vector.Vector3d;
import java.util.Arrays;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class DevourSpell extends AbstractSpell {
    private final ResourceLocation spellId = new ResourceLocation(IronsSpellbooks.MODID, "devour");
    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
            .setMaxLevel(10)
            .setCooldownSeconds(20)
            .build();

    @Override
    public List<IFormattableTextComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return Arrays.asList(
                new TranslationTextComponent("ui.irons_spellbooks.damage", Utils.stringTruncation(getDamage(spellLevel, caster), 1)),
                new TranslationTextComponent("ui.irons_spellbooks.max_hp_on_kill", getHpBonus(spellLevel, caster))
        );
    }

    public DevourSpell() {
        this.manaCostPerLevel = 4;
        this.baseSpellPower = 6;
        this.spellPowerPerLevel = 1;
        this.castTime = 0;
        this.baseManaCost = 25;
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
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }


    @Override
    public boolean checkPreCastConditions(World level, LivingEntity entity, MagicData playerMagicData) {
        return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 9, .1f);
    }

    @Override
    public void onCast(World world, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (playerMagicData.getAdditionalCastData() instanceof CastTargetingData) {
            CastTargetingData targetData = (CastTargetingData) playerMagicData.getAdditionalCastData();
            LivingEntity targetEntity = targetData.getTarget((ServerWorld) world);
            if (targetEntity != null) {
                DevourJaw devour = new DevourJaw(world, entity, targetEntity);
                Vector3d pos = targetEntity.position();
                devour.setPos(pos.x, pos.y, pos.z);
                devour.yRot = (entity.yRot);
                devour.setDamage(getDamage(spellLevel, entity));
                devour.vigorLevel = (getHpBonus(spellLevel, entity) / 2) - 1;
                world.addFreshEntity(devour);
            }
        }

        super.onCast(world, spellLevel, entity, playerMagicData);
    }

    @Override
    public DamageSource getDamageSource(@Nullable Entity projectile, Entity attacker) {
        return ((ISpellDamageSource) super.getDamageSource(projectile, attacker)).setLifestealPercent(.15f).get();
    }

    public float getDamage(int spellLevel, LivingEntity caster) {
        return getSpellPower(spellLevel, caster);
    }

    public int getHpBonus(int spellLevel, LivingEntity caster) {
        return 2 * (int) (getSpellPower(spellLevel, caster) * .25f);
    }
}
