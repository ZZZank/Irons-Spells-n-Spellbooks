package io.redspace.ironsspellbooks.util;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.spell.SpellData;
import io.redspace.ironsspellbooks.capabilities.spellbook.SpellBookData;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TooltipsUtils {


    public static List<ITextComponent> formatActiveSpellTooltip(ItemStack stack, CastSource castSource, @Nonnull ClientPlayerEntity player) {
        //var player = Minecraft.getInstance().player;
        var spellData = stack.getItem() instanceof SpellBook ? SpellBookData.getSpellBookData(stack).getActiveSpell() : SpellData.getSpellData(stack); //Put me in utils?
        var spell = spellData.getSpell();
//        var title = Component.translatable("tooltip.irons_spellbooks.selected_spell",
//                spellType.getDisplayName().withStyle(spellType.getSchoolType().getDisplayName().getStyle()),
//                Component.literal("" + spell.getLevel()).withStyle(spellType.getRarity(spell.getLevel()).getDisplayName().getStyle()));
//        var title = Component.translatable("tooltip.irons_spellbooks.selected_spell",
//                spellType.getDisplayName().withStyle(spellType.getSchoolType().getDisplayName().getStyle()),
//                Component.literal("" + spell.getLevel())).withStyle(spellType.getRarity(spell.getLevel()).getDisplayName().getStyle());
        var levelText = getLevelComponenet(spellData, player);

        var title = ITextComponent.translatable("tooltip.irons_spellbooks.selected_spell",
                spell.getDisplayName(),
                levelText).withStyle(spell.getSchoolType().getDisplayName().getStyle());
        var uniqueInfo = spell.getUniqueInfo(spellData.getLevel(), player);
        var manaCost = getManaCostComponent(spell.getCastType(), spell.getManaCost(spellData.getLevel(), player)).withStyle(TextFormatting.BLUE);
        var cooldownTime = ITextComponent.translatable("tooltip.irons_spellbooks.cooldown_length_seconds", Utils.timeFromTicks(MagicManager.getEffectiveSpellCooldown(spell, player, castSource), 1)).withStyle(TextFormatting.BLUE);

        List<ITextComponent> lines = new ArrayList<>();
        lines.add(ITextComponent.empty());
        lines.add(title);
        uniqueInfo.forEach((line) -> lines.add(ITextComponent.literal(" ").append(line.withStyle(TextFormatting.DARK_GREEN))));
        if (spell.getCastType() != CastType.INSTANT) {
            lines.add(ITextComponent.literal(" ").append(getCastTimeComponent(spell.getCastType(), Utils.timeFromTicks(spell.getEffectiveCastTime(spellData.getLevel(), player), 1)).withStyle(TextFormatting.BLUE)));
        }
        if (castSource != CastSource.SWORD || ServerConfigs.SWORDS_CONSUME_MANA.get())
            lines.add(manaCost);
        if (castSource != CastSource.SWORD || ServerConfigs.SWORDS_CD_MULTIPLIER.get().floatValue() > 0)
            lines.add(cooldownTime);
        return lines;
    }

    public static List<ITextComponent> formatScrollTooltip(ItemStack stack, @Nonnull ClientPlayerEntity player) {
        var spellData = SpellData.getSpellData(stack);

        if (spellData.equals(SpellData.EMPTY)) {
            return List.of();
        }

        var spell = spellData.getSpell();
        var levelText = getLevelComponenet(spellData, player);
        var title = ITextComponent.translatable("tooltip.irons_spellbooks.level", levelText).append(" ").append(ITextComponent.translatable("tooltip.irons_spellbooks.rarity", spell.getRarity(spellData.getLevel()).getDisplayName()).withStyle(spell.getRarity(spellData.getLevel()).getDisplayName().getStyle())).withStyle(TextFormatting.GRAY);
        var uniqueInfo = spell.getUniqueInfo(spellData.getLevel(), player);
        var whenInSpellBook = ITextComponent.translatable("tooltip.irons_spellbooks.scroll_tooltip").withStyle(TextFormatting.GRAY);
        var manaCost = getManaCostComponent(spell.getCastType(), spell.getManaCost(spellData.getLevel(), player)).withStyle(TextFormatting.BLUE);
        var cooldownTime = ITextComponent.translatable("tooltip.irons_spellbooks.cooldown_length_seconds", Utils.timeFromTicks(MagicManager.getEffectiveSpellCooldown(spell, player, CastSource.SCROLL), 1)).withStyle(TextFormatting.BLUE);

        List<ITextComponent> lines = new ArrayList<>();
        lines.add(ITextComponent.literal(" ").append(title));
        uniqueInfo.forEach((line) -> lines.add(ITextComponent.literal(" ").append(line.withStyle(TextFormatting.DARK_GREEN))));
        if (spell.getCastType() != CastType.INSTANT) {
            lines.add(ITextComponent.literal(" ").append(getCastTimeComponent(spell.getCastType(), Utils.timeFromTicks(spell.getEffectiveCastTime(spellData.getLevel(), player), 1)).withStyle(TextFormatting.BLUE)));
        }
        lines.add(ITextComponent.empty());
        lines.add(whenInSpellBook);
        lines.add(manaCost);
        lines.add(cooldownTime);
        lines.add(spell.getSchoolType().getDisplayName().copy());

        return lines;
    }

    public static IFormattableTextComponent getLevelComponenet(SpellData spellData, LivingEntity caster) {
        int levelTotal = spellData.getSpell().getLevel(spellData.getLevel(), caster);
        int diff = levelTotal - spellData.getLevel();
        if (diff > 0) {
            return ITextComponent.translatable("tooltip.irons_spellbooks.level_plus", levelTotal, diff);
        } else {
            return ITextComponent.literal(String.valueOf(levelTotal));
        }
    }

    public static IFormattableTextComponent getCastTimeComponent(CastType type, String castTime) {
        return switch (type) {
            case CONTINUOUS -> ITextComponent.translatable("tooltip.irons_spellbooks.cast_continuous", castTime);
            case LONG -> ITextComponent.translatable("tooltip.irons_spellbooks.cast_long", castTime);
            default -> ITextComponent.translatable("ui.irons_spellbooks.cast_instant");
        };
    }

    public static IFormattableTextComponent getManaCostComponent(CastType castType, int manaCost) {
        if (castType == CastType.CONTINUOUS) {
            return ITextComponent.translatable("tooltip.irons_spellbooks.mana_cost_per_second", manaCost * (20 / MagicManager.CONTINUOUS_CAST_TICK_INTERVAL));
        } else {
            return ITextComponent.translatable("tooltip.irons_spellbooks.mana_cost", manaCost);
        }
    }

    public static List<IReorderingProcessor> createSpellDescriptionTooltip(AbstractSpell spell, FontRenderer font) {
        var name = spell.getDisplayName();
        var description = font.split(ITextComponent.translatable(String.format("%s.guide", spell.getComponentId())).withStyle(TextFormatting.GRAY), 180);
        var hoverText = new ArrayList<IReorderingProcessor>();
        hoverText.add(IReorderingProcessor.forward(name.getString(), Style.EMPTY.withUnderlined(true)));
        hoverText.addAll(description);
        return hoverText;
    }
}
