package link.dwsy.ddl.util.BlockTipQuoteExtension;

import com.vladsch.flexmark.util.ast.Block;
import com.vladsch.flexmark.util.ast.BlockContent;
import com.vladsch.flexmark.util.ast.BlockQuoteLike;
import com.vladsch.flexmark.util.ast.KeepTrailingBlankLineContainer;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A ExtBlockTipQuote block node
 */
public class BlockTipQuoteBlock extends Block implements BlockQuoteLike, KeepTrailingBlankLineContainer {
    private BasedSequence openingMarker = BasedSequence.NULL;

    @Override
    public void getAstExtra(@NotNull StringBuilder out) {
        segmentSpanChars(out, openingMarker, "marker");
    }

    @NotNull
    @Override
    public BasedSequence[] getSegments() {
        return new BasedSequence[] { openingMarker };
    }

    public BlockTipQuoteBlock() {
    }

    public BlockTipQuoteBlock(BasedSequence chars) {
        super(chars);
    }

    public BlockTipQuoteBlock(BasedSequence chars, List<BasedSequence> segments) {
        super(chars, segments);
    }

    public BlockTipQuoteBlock(BlockContent blockContent) {
        super(blockContent);
    }

    public BasedSequence getOpeningMarker() {
        return openingMarker;
    }

    public void setOpeningMarker(BasedSequence openingMarker) {
        this.openingMarker = openingMarker;
    }
}
