package link.dwsy.ddl.util.BlockTipQuoteExtension;

import com.vladsch.flexmark.util.ast.VisitHandler;

public class BlockTipQuoteVisitorExt {
    public static <V extends BlockTipQuoteVisitor> VisitHandler<?>[] VISIT_HANDLERS(V visitor) {
        return new VisitHandler<?>[] {
                new VisitHandler<>(BlockTipQuoteBlock.class, visitor::visit),
        };
    }
}
