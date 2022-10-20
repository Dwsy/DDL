package link.dwsy.ddl.util.BlockTipQuoteExtension;

import com.vladsch.flexmark.formatter.Formatter;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import link.dwsy.ddl.util.BlockTipQuoteExtension.internal.BlockTipQuoteBlockParser;
import link.dwsy.ddl.util.BlockTipQuoteExtension.internal.BlockTipQuoteNodeFormatter;
import link.dwsy.ddl.util.BlockTipQuoteExtension.internal.BlockTipQuoteNodeRenderer;
import org.jetbrains.annotations.NotNull;

/**
 * Extension for ext_BlockTipQuotes
 * <p>
 * Create it with {@link #create()} and then configure it on the builders
 * <p>
 * The parsed pipe prefixed text is turned into {@link BlockTipQuoteBlock} nodes.
 */
public class BlockTipQuoteExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension, Formatter.FormatterExtension {
    final public static DataKey<Boolean> EXTEND_TO_BLANK_LINE = new DataKey<>("EXTEND_TO_BLANK_LINE", Parser.BLOCK_QUOTE_EXTEND_TO_BLANK_LINE);
    final public static DataKey<Boolean> IGNORE_BLANK_LINE = new DataKey<>("IGNORE_BLANK_LINE", Parser.BLOCK_QUOTE_IGNORE_BLANK_LINE);
    final public static DataKey<Boolean> ALLOW_LEADING_SPACE = new DataKey<>("ALLOW_LEADING_SPACE", Parser.BLOCK_QUOTE_ALLOW_LEADING_SPACE);
    final public static DataKey<Boolean> INTERRUPTS_PARAGRAPH = new DataKey<>("INTERRUPTS_PARAGRAPH", Parser.BLOCK_QUOTE_INTERRUPTS_PARAGRAPH);
    final public static DataKey<Boolean> INTERRUPTS_ITEM_PARAGRAPH = new DataKey<>("INTERRUPTS_ITEM_PARAGRAPH", Parser.BLOCK_QUOTE_INTERRUPTS_ITEM_PARAGRAPH);
    final public static DataKey<Boolean> WITH_LEAD_SPACES_INTERRUPTS_ITEM_PARAGRAPH =
            new DataKey<>("WITH_LEAD_SPACES_INTERRUPTS_ITEM_PARAGRAPH", Parser.BLOCK_QUOTE_WITH_LEAD_SPACES_INTERRUPTS_ITEM_PARAGRAPH);

    private BlockTipQuoteExtension() {
    }

    public static BlockTipQuoteExtension create() {
        return new BlockTipQuoteExtension();
    }

    @Override
    public void rendererOptions(@NotNull MutableDataHolder options) {

    }

    @Override
    public void parserOptions(MutableDataHolder options) {

    }

    @Override
    public void extend(Formatter.Builder formatterBuilder) {
        formatterBuilder.nodeFormatterFactory(new BlockTipQuoteNodeFormatter.Factory());
    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.customBlockParserFactory(new BlockTipQuoteBlockParser.Factory());
    }

    @Override
    public void extend(@NotNull HtmlRenderer.Builder htmlRendererBuilder, @NotNull String rendererType) {
        if (htmlRendererBuilder.isRendererType("HTML")) {
            htmlRendererBuilder.nodeRendererFactory(new BlockTipQuoteNodeRenderer.Factory());
        } else if (htmlRendererBuilder.isRendererType("JIRA")) {
        }
    }
}
