package link.dwsy.ddl.util.BlockTipQuoteExtension.internal;

import com.vladsch.flexmark.formatter.*;
import com.vladsch.flexmark.util.data.DataHolder;
import link.dwsy.ddl.util.BlockTipQuoteExtension.BlockTipQuoteBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class BlockTipQuoteNodeFormatter implements NodeFormatter {

    public BlockTipQuoteNodeFormatter(DataHolder options) {

    }

    @Nullable
    @Override
    public Set<Class<?>> getNodeClasses() {
        return null;
    }

    @Override
    public char getBlockQuoteLikePrefixChar() {
        return '|';
    }

    // only registered if assignTextAttributes is enabled
    @Nullable
    @Override
    public Set<NodeFormattingHandler<?>> getNodeFormattingHandlers() {
        HashSet<NodeFormattingHandler<?>> set = new HashSet<>();
        set.add(new NodeFormattingHandler<>(BlockTipQuoteBlock.class, BlockTipQuoteNodeFormatter.this::render));
        return set;
    }

    private void render(BlockTipQuoteBlock node, NodeFormatterContext context, MarkdownWriter markdown) {
        FormatterUtils.renderBlockQuoteLike(node, context, markdown);
    }

    public static class Factory implements NodeFormatterFactory {
        @NotNull
        @Override
        public NodeFormatter create(@NotNull DataHolder options) {
            return new BlockTipQuoteNodeFormatter(options);
        }
    }
}
