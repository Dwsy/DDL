package link.dwsy.ddl.util.BlockTipQuoteExtension.internal;

import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;
import link.dwsy.ddl.util.BlockTipQuoteExtension.BlockTipQuoteBlock;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class BlockTipQuoteNodeRenderer implements NodeRenderer {
    public BlockTipQuoteNodeRenderer(DataHolder options) {

    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        HashSet<NodeRenderingHandler<?>> set = new HashSet<>();
        set.add(new NodeRenderingHandler<>(BlockTipQuoteBlock.class, this::render));
        return set;
    }

    private void render(BlockTipQuoteBlock node, NodeRendererContext context, HtmlWriter html) {
        //tag(node, context, html, "blockquote");
        html.withAttr().withCondIndent().tagLine("xxxx", () -> context.renderChildren(node));
    }

    public static class Factory implements NodeRendererFactory {
        @NotNull
        @Override
        public NodeRenderer apply(@NotNull DataHolder options) {
            return new BlockTipQuoteNodeRenderer(options);
        }
    }
}
