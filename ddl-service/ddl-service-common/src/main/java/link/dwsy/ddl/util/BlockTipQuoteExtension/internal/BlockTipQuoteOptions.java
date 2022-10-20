package link.dwsy.ddl.util.BlockTipQuoteExtension.internal;

import com.vladsch.flexmark.util.data.DataHolder;
import link.dwsy.ddl.util.BlockTipQuoteExtension.BlockTipQuoteExtension;

class BlockTipQuoteOptions {
    final public boolean extendToBlankLine;
    final public boolean ignoreBlankLine;
    final public boolean allowLeadingSpace;
    final public boolean interruptsParagraph;
    final public boolean interruptsItemParagraph;
    final public boolean withLeadSpacesInterruptsItemParagraph;

    public BlockTipQuoteOptions(DataHolder options) {
        this.extendToBlankLine = BlockTipQuoteExtension.EXTEND_TO_BLANK_LINE.get(options);
        this.ignoreBlankLine = BlockTipQuoteExtension.IGNORE_BLANK_LINE.get(options);
        this.allowLeadingSpace = BlockTipQuoteExtension.ALLOW_LEADING_SPACE.get(options);
        this.interruptsParagraph = BlockTipQuoteExtension.INTERRUPTS_PARAGRAPH.get(options);
        this.interruptsItemParagraph = BlockTipQuoteExtension.INTERRUPTS_ITEM_PARAGRAPH.get(options);
        this.withLeadSpacesInterruptsItemParagraph = BlockTipQuoteExtension.WITH_LEAD_SPACES_INTERRUPTS_ITEM_PARAGRAPH.get(options);
    }
}
