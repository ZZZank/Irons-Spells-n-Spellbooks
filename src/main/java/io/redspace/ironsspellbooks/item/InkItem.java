package io.redspace.ironsspellbooks.item;

import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.util.SpellbookModCreativeTabs;
import net.minecraft.util.text.TextFormatting;
import java.util.Arrays;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InkItem extends Item {
    private SpellRarity rarity;

    public InkItem(SpellRarity rarity) {
        super(new Item.Properties().tab(SpellbookModCreativeTabs.SPELL_MATERIALS_TAB));
        this.rarity = rarity;
    }

    public SpellRarity getRarity() {
        return rarity;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable World pLevel, List<ITextComponent> lines, ITooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, lines, pIsAdvanced);
        lines.add(new TranslationTextComponent("tooltip.irons_spellbooks.ink_tooltip", rarity.getDisplayName()).withStyle(TextFormatting.GRAY));
    }
}
