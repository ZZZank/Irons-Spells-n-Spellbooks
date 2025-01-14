package io.redspace.ironsspellbooks.capabilities.magic;

import io.redspace.ironsspellbooks.api.magic.IMagicManager;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.network.ClientboundSyncCooldown;
import io.redspace.ironsspellbooks.network.ClientboundSyncMana;
import io.redspace.ironsspellbooks.setup.Messages;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.particles.IParticleData;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import static io.redspace.ironsspellbooks.api.registry.AttributeRegistry.*;

public class MagicManager implements IMagicManager {
    public static final int MANA_REGEN_TICKS = 10;
    public static final int CONTINUOUS_CAST_TICK_INTERVAL = 10;

    /**
     * Deprecated Helper Method. Use {@link MagicData#setMana(float)} instead.
     */
    @Deprecated
    public void setPlayerCurrentMana(ServerPlayerEntity serverPlayer, int newManaValue) {
        MagicData playerMagicData = MagicData.getPlayerMagicData(serverPlayer);
        playerMagicData.setMana(newManaValue);
    }

    public void regenPlayerMana(ServerPlayerEntity serverPlayer, MagicData playerMagicData) {
        int playerMaxMana = (int) serverPlayer.getAttributeValue(MAX_MANA.get());
        float playerManaRegenMultiplier = (float) serverPlayer.getAttributeValue(MANA_REGEN.get());
        float increment = Math.max(playerMaxMana * playerManaRegenMultiplier * .01f, 1);

        if (playerMagicData.getMana() != playerMaxMana) {
            if (playerMagicData.getMana() + increment < playerMaxMana) {
                playerMagicData.addMana(increment);
            } else {
                playerMagicData.setMana(playerMaxMana);
            }
        }
    }

    public void tick(World level) {
        boolean doManaRegen = level.getServer().getTickCount() % MANA_REGEN_TICKS == 0;
        //IronsSpellbooks.LOGGER.debug("MagicManager.tick: {}, {}, {}, {}, {}", this.hashCode(), level.hashCode(), level.getServer().getTickCount(), level.players().size(), doManaRegen);

        level.players().forEach(player -> {
            if (player instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                MagicData playerMagicData = MagicData.getPlayerMagicData(serverPlayer);
                playerMagicData.getPlayerCooldowns().tick(1);

                if (playerMagicData.isCasting()) {
                    playerMagicData.handleCastDuration();
                    AbstractSpell spell = SpellRegistry.getSpell(playerMagicData.getCastingSpellId());
                    if (spell.getCastType() == CastType.LONG && !serverPlayer.isUsingItem()) {
                        if (playerMagicData.getCastDurationRemaining() <= 0) {
                            spell.castSpell(serverPlayer.level, playerMagicData.getCastingSpellLevel(), serverPlayer, playerMagicData.getCastSource(), true);
                            spell.onServerCastComplete(serverPlayer.level, playerMagicData.getCastingSpellLevel(), serverPlayer, playerMagicData, false);
                            Scroll.attemptRemoveScrollAfterCast(serverPlayer);
                        }
                    } else if (spell.getCastType() == CastType.CONTINUOUS) {
                        if ((playerMagicData.getCastDurationRemaining() + 1) % CONTINUOUS_CAST_TICK_INTERVAL == 0) {
                            if (playerMagicData.getCastDurationRemaining() < CONTINUOUS_CAST_TICK_INTERVAL || (playerMagicData.getCastSource().consumesMana() && playerMagicData.getMana() - spell.getManaCost(playerMagicData.getCastingSpellLevel(), null) * 2 < 0)) {
                                spell.castSpell(serverPlayer.level, playerMagicData.getCastingSpellLevel(), serverPlayer, playerMagicData.getCastSource(), true);

                                if (playerMagicData.getCastSource() == CastSource.SCROLL) {
                                    Scroll.attemptRemoveScrollAfterCast(serverPlayer);
                                }

                                spell.onServerCastComplete(serverPlayer.level, playerMagicData.getCastingSpellLevel(), serverPlayer, playerMagicData, false);

                            } else {
                                spell.castSpell(serverPlayer.level, playerMagicData.getCastingSpellLevel(), serverPlayer, playerMagicData.getCastSource(), false);
                            }
                        }
                    }

                    if (playerMagicData.isCasting()) {
                        spell.onServerCastTick(serverPlayer.level, playerMagicData.getCastingSpellLevel(), serverPlayer, playerMagicData);
                    }
                }

                if (doManaRegen) {
                    regenPlayerMana(serverPlayer, playerMagicData);
                    Messages.sendToPlayer(new ClientboundSyncMana(playerMagicData), serverPlayer);
                }
            }
        });
    }

    public void addCooldown(ServerPlayerEntity serverPlayer, AbstractSpell spell, CastSource castSource) {
        if (castSource == CastSource.SCROLL)
            return;
        int effectiveCooldown = getEffectiveSpellCooldown(spell, serverPlayer, castSource);

        MagicData.getPlayerMagicData(serverPlayer).getPlayerCooldowns().addCooldown(spell, effectiveCooldown);
        Messages.sendToPlayer(new ClientboundSyncCooldown(spell.getSpellId(), effectiveCooldown), serverPlayer);
    }

    public static int getEffectiveSpellCooldown(AbstractSpell spell, PlayerEntity player, CastSource castSource) {
        double playerCooldownModifier = player.getAttributeValue(COOLDOWN_REDUCTION.get());

        float itemCoolDownModifer = 1;
        if (castSource == CastSource.SWORD) {
            itemCoolDownModifer = ServerConfigs.SWORDS_CD_MULTIPLIER.get().floatValue();
        }
        return (int) (spell.getSpellCooldown() * (2 - Utils.softCapFormula(playerCooldownModifier)) * itemCoolDownModifer);
    }

    public static void spawnParticles(World level, IParticleData particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed, boolean force) {
        level.getServer().getPlayerList().getPlayers().forEach(player -> ((ServerWorld) level).sendParticles(player, particle, force, x, y, z, count, deltaX, deltaY, deltaZ, speed));
    }
}
