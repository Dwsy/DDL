package link.dwsy.ddl.util.anchorlink.internal;

import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;
import link.dwsy.ddl.util.HtmlHelper;
import link.dwsy.ddl.util.anchorlink.AnchorLink;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class AnchorLinkNodeRenderer implements NodeRenderer {
    final private AnchorLinkOptions options;

    public AnchorLinkNodeRenderer(DataHolder options) {
        this.options = new AnchorLinkOptions(options);
    }

    @Override
    public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
        HashSet<NodeRenderingHandler<?>> set = new HashSet<>();
        set.add(new NodeRenderingHandler<>(AnchorLink.class, this::render));
        return set;
    }

    private void render(AnchorLink node, NodeRendererContext context, HtmlWriter html) {
        if (context.isDoNotRenderLinks()) {
            if (options.wrapText) {
                context.renderChildren(node);
            }
        } else {
            String id = context.getNodeId(node.getParent());
            var answerId = HtmlHelper.LinkRefAttributeProvider.answerId.get();
            if (id != null) {
                System.out.println(Thread.currentThread().getName());
                if (answerId != null) {
                    html.attr("href", "#" + id + "-" + answerId);
                } else {
                    html.attr("href", "#" + id);
                }

                if (options.setId){
                    if (answerId != null) {
                        html.attr("id", id + "-" + answerId);
                    } else {
                        html.attr("id", id);
                    }
                }

                if (options.setName) html.attr("name", id);
                if (!options.anchorClass.isEmpty()) html.attr("class", options.anchorClass);

                if (!options.wrapText) {
                    html.withAttr().tag("a");
                    if (!options.textPrefix.isEmpty()) html.raw(options.textPrefix);
                    if (!options.textSuffix.isEmpty()) html.raw(options.textSuffix);
                    html.tag("/a");
                } else {
                    html.withAttr().tag("a", false, false, () -> {
                        if (!options.textPrefix.isEmpty()) html.raw(options.textPrefix);
                        context.renderChildren(node);
                        if (!options.textSuffix.isEmpty()) html.raw(options.textSuffix);
                    });
                }
            } else {
                if (options.wrapText) {
                    context.renderChildren(node);
                }
            }
        }
    }

    public static class Factory implements NodeRendererFactory {
        @NotNull
        @Override
        public NodeRenderer apply(@NotNull DataHolder options) {
            return new AnchorLinkNodeRenderer(options);
        }
    }
}
