package link.dwsy.ddl.util;

import cn.hutool.http.HtmlUtil;
import com.vladsch.flexmark.ast.AutoLink;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.html.AttributeProvider;
import com.vladsch.flexmark.html.AttributeProviderFactory;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.IndependentAttributeProviderFactory;
import com.vladsch.flexmark.html.renderer.AttributablePart;
import com.vladsch.flexmark.html.renderer.LinkResolverContext;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profile.pegdown.Extensions;
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.html.MutableAttributes;
import org.jetbrains.annotations.NotNull;


/**
 * Markdown工具
 *
 * @author acgist
 */

public class HtmlHelper {
    /**
     * 本站域名
     */

    private static final String SITE_HOST = "ddl.dwsy.clink";
    public static String filter(String html) {
        return HtmlUtil.filter(html);
    }

    public static String toPure(String html) {
        return HtmlUtil.cleanHtmlTag(html);
    }

    public static String mdToPure(String md) {
        return toPure(toHTML(md));
    }

    /**
     * Markdown转HTML
     * <p>
     * <p>
     * <p>
     * 去掉链接自动转换
     */
    public static String toHTML(String markdown) {

        final DataHolder holder = PegdownOptionsAdapter.flexmarkOptions(true, Extensions.ALL ^ Extensions.AUTOLINKS, LinkRefExtension.create());

        final Parser parser = Parser.builder(holder).build();

        final Node document = parser.parse(markdown);

        final HtmlRenderer renderer = HtmlRenderer.builder(holder).build();

        return renderer.render(document);

    }

    //jdk 11 var好像挺好玩的
    public static void main(String[] args) {
        var md2 = "# 分布式搜索引擎03\n" +
                "\n" +
                "\n" +
                "\n" +
                "# 0.学习目标\n" +
                "\n" +
                "\n" +
                "\n" +
                "# 1.数据聚合\n" +
                "\n" +
                "**[聚合（](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html)[aggregations](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html)[）](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html)**可以让我们极其方便的实现对数据的统计、分析、运算。例如：\n" +
                "\n" +
                "- 什么品牌的手机最受欢迎？\n" +
                "- 这些手机的平均价格、最高价格、最低价格？\n" +
                "- 这些手机每月的销售情况如何？\n" +
                "\n" +
                "实现这些统计功能的比数据库的sql要方便的多，而且查询速度非常快，可以实现近实时搜索效果。\n" +
                "\n" +
                "## 1.1.聚合的种类\n" +
                "\n" +
                "聚合常见的有三类：\n" +
                "\n" +
                "- **桶（Bucket）**聚合：用来对文档做分组\n" +
                "  - TermAggregation：按照文档字段值分组，例如按照品牌值分组、按照国家分组\n" +
                "  - Date Histogram：按照日期阶梯分组，例如一周为一组，或者一月为一组\n" +
                "\n" +
                "- **度量（Metric）**聚合：用以计算一些值，比如：最大值、最小值、平均值等\n" +
                "  - Avg：求平均值\n" +
                "  - Max：求最大值\n" +
                "  - Min：求最小值\n" +
                "  - Stats：同时求max、min、avg、sum等\n" +
                "- **管道（pipeline）**聚合：其它聚合的结果为基础做聚合\n" +
                "\n" +
                "\n" +
                "\n" +
                "> **注意：**参加聚合的字段必须是keyword、日期、数值、布尔类型\n";
        var html2 = HtmlHelper.toHTML("```c\n" +
                "#include <stdio.h>\n" +
                "\n" +
                "    union data{\n" +
                "        int n;\n" +
                "        char ch;\n" +
                "        short m;\n" +
                "    };\n" +
                "    \n" +
                "    int main(){\n" +
                "        union data a;\n" +
                "        printf(\"%d, %d\\n\", sizeof(a), sizeof(union data) );\n" +
                "        a.n = 0x40;\n" +
                "        printf(\"%X, %c, %hX\\n\", a.n, a.ch, a.m);\n" +
                "        a.ch = '9';\n" +
                "        printf(\"%X, %c, %hX\\n\", a.n, a.ch, a.m);\n" +
                "        a.m = 0x2059;\n" +
                "        printf(\"%X, %c, %hX\\n\", a.n, a.ch, a.m);\n" +
                "        a.n = 0x3E25AD54;\n" +
                "        printf(\"%X, %c, %hX\\n\", a.n, a.ch, a.m);\n" +
                "       \n" +
                "        return 0;\n" +
                "    }\n" +
                "```");
        System.out.println(HtmlHelper.toHTML(md2));
        System.out.println(html2);
    }


    static class LinkRefExtension implements HtmlRenderer.HtmlRendererExtension {

        static LinkRefExtension create() {

            return new LinkRefExtension();

        }

        @Override

        public void rendererOptions(@NotNull MutableDataHolder holder) {

        }

        @Override

        public void extend(HtmlRenderer.Builder rendererBuilder, @NotNull String rendererType) {

            rendererBuilder.attributeProviderFactory(LinkRefAttributeProvider.Factory());

        }

    }

    static class LinkRefAttributeProvider implements AttributeProvider {

        static AttributeProviderFactory Factory() {

            return new IndependentAttributeProviderFactory() {

                @Override

                public @NotNull AttributeProvider apply(@NotNull LinkResolverContext context) {

                    return new LinkRefAttributeProvider();

                }

            };

        }


        @Override
        public void setAttributes(@NotNull Node node, @NotNull AttributablePart attributablePart, @NotNull MutableAttributes mutableAttributes) {
            if ((node instanceof Link || node instanceof AutoLink) && attributablePart == AttributablePart.LINK) {

// 如果非本站地址添加：ref="nofollow"
                var href = mutableAttributes.get("href");
//                final var href = attributes.get("href");

                if (href != null && href.getValue() != null && !href.getValue().contains(SITE_HOST)) {
                    mutableAttributes.replaceValue("rel", "nofollow");
                }

            }
        }
    }

}
