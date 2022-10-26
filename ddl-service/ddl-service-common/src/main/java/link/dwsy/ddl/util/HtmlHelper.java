package link.dwsy.ddl.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import com.vladsch.flexmark.ast.AutoLink;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.gitlab.GitLabExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
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

import java.util.HashMap;
import java.util.Map;


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
    static HtmlRenderer renderer = null;
    static String md2 = "> [https://www.cnblogs.com/bmilk/p/13225817.html](https://www.cnblogs.com/bmilk/p/13225817.html)\n" +
            "\n" +
            "[Buffer简介](#buffer简介)\n" +
            "---------------------\n" +
            "\n" +
            "缓冲区(Buffer):本质上是一个数组，用于临时保存、写入以及读取数据。在Java NIO中，该内存块包含在NIO Buffer对象当中，NIO Buffer对象还提供了一组接口来访问该内存块。  \n" +
            "根据数据类型的不同，Java为除了boolean类型之外的其余7种基本类型提供了相应类型的缓冲区，分别是ByteBuffer、CharBuffer、ShortBuffer、IntBuffer、LongBuffer、FloatBuffer、DoubleBuffer。他们都继承自抽象类Buffer类，他们的管理方式也都几乎一样。UML类图如下：  \n" +
            "![image.png](https://cdn.nlark.com/yuque/0/2022/png/2853013/1645865638270-e8a946e6-58e2-4b49-9897-5deb1032dd23.png#clientId=u553119fb-8c4f-4&crop=0&crop=0&crop=1&crop=1&from=paste&id=ub971b1ef&margin=%5Bobject%20Object%5D&name=image.png&originHeight=166&originWidth=1285&originalType=url&ratio=1&rotation=0&showTitle=false&size=16392&status=done&style=none&taskId=u530d4693-6f60-4613-9d14-8be48462aaa&title=)  \n" +
            "\n" +
            "[![image.png](https://cdn.nlark.com/yuque/0/2022/png/2853013/1645866062612-d4a94d0a-72ac-471d-a30b-0f8220867671.png#clientId=u352ceb3e-7f07-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=709&id=u6600b435&margin=%5Bobject%20Object%5D&name=image.png&originHeight=709&originWidth=1173&originalType=binary&ratio=1&rotation=0&showTitle=false&size=1019200&status=done&style=none&taskId=ub49e9b87-8bc0-46cd-8561-1b496a256c9&title=&width=1173)](#imagepng)\n" +
            "----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n" +
            "\n" +
            "![image.png](https://cdn.nlark.com/yuque/0/2022/png/2853013/1645866108724-171b4292-0436-4af6-938b-37889e0e5528.png#clientId=u352ceb3e-7f07-4&crop=0&crop=0&crop=1&crop=1&from=paste&height=363&id=u2e42ab89&margin=%5Bobject%20Object%5D&name=image.png&originHeight=363&originWidth=1184&originalType=binary&ratio=1&rotation=0&showTitle=false&size=729502&status=done&style=none&taskId=u825a8448-2b87-401a-a0eb-83dbae46660&title=&width=1184)  \n" +
            "\n";
    //    "> 0正常的 正常的 正常的 正常的 正常的 `test` http://nuxt.localhost/article/editor/draft?id=16\n" +
//            "\n" +
//            "> @1这是灰色的短代码框，常用来引用资料什么的 http://nuxt.localhost/article/editor/draft?id=16\n" +
//            "\n" +
//            "> !2这是黄色的短代码框，常用来做提示，引起读者注意。 `test`\n" +
//            "\n" +
//            "> x3这是红色的短代码框，用于严重警告什么的。 `test` http://nuxt.localhost/article/editor/draft?id=16 \n" +
//            "\n" +
//            "> i4这是浅蓝色的短代码框，用于显示一些信息。 `test`\n" +
//            "\n" +
    static String hs =
            "> x这是绿色的短代码框，显示一些推荐信息。`test`\n" +
                    "> x这是绿色的短代码框，显示一些推荐信息。`test`\n" +
                    "> x这是绿色的短代码框，显示一些推荐信息。`test`\n" +
                    "> x这是绿色的短代码框，显示一些推荐信息。`test`\n";

    static String hs1 =
            "> 0正常的 正常的 正常的 正常的 正常的 `test` http://nuxt.localhost/article/editor/draft?id=16\n" +
                    "\n" +
                    "> @1这是灰色的短代码框，常用来引用资料什么的 http://nuxt.localhost/article/editor/draft?id=16\n" +
                    "\n" +
                    "> !2这是黄色的短代码框，常用来做提示，引起读者注意。 `test`\n" +
                    "\n" +
                    "> x3这是红色的短代码框，用于严重警告什么的。 `test` http://nuxt.localhost/article/editor/draft?id=16 \n" +
                    "\n" +
                    "> i4这是浅蓝色的短代码框，用于显示一些信息。 `test`\n";


    //    public class BlockQuoteTipExtension implements HtmlRenderer.HtmlRendererExtension {
//
//        @Override
//        public void parserOptions(MutableDataHolder options) {
//
//        }
//
//        @Override
//        public void extend(Parser.Builder parserBuilder) {
////            parserBuilder.customBlockParserFactory(new BlockQuoteTNodePostProcessor());
//        }
//    }
//
//    public class BlockQuoteTNodePostProcessor implements HtmlRenderer.HtmlRendererExtension {
//        @Override
//        public void rendererOptions(@NotNull MutableDataHolder options) {
//
//        }
//
//        @Override
//        public void extend(HtmlRenderer.@NotNull Builder htmlRendererBuilder, @NotNull String rendererType) {
//
//        }
//    }
    private static Map<String, Object> prefixHandle
    (String text, String[] prefix, int length, String[] strings, int i) {
        text = getReplaceString(text, prefix) + '\n';
        for (int j = i + 1; j < length; j++) {
            if (hasTipPrefix(strings[j], prefix)) {
                String temp = strings[j];
                temp = getReplaceString(temp, prefix);
                i++;
                if (j != length - 1) {
                    temp += '\n';
                }
                text += '\n' + temp;
            } else {
                break;
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("text", text);
        map.put("i", i);
        return map;
    }

    public static String filter(String html) {
        return HtmlUtil.filter(html);
    }

    public static String toPure(String html) {
        return HtmlUtil.unescape(HtmlUtil.cleanHtmlTag(html));
    }

    public static String mdToPure(String md) {
        return toPure(toHTML(md));
    }

    //jdk 11 var好像挺好玩的
    public static void main(String[] args) {


        System.out.println(HtmlHelper.toHTML(
                hs
        ));
//        System.out.println(HtmlHelper.toHTML(
//                hs
//        ));
    }

    /**
     * Markdown转HTML
     * <p>
     * <p>
     * <p>
     * 去掉链接自动转换
     */
    public static String toHTML(String markdown) {
        final DataHolder holder = PegdownOptionsAdapter.
                flexmarkOptions(false, Extensions.ALL,
                        LinkRefExtension.create(),
                        EmojiExtension.create(),
                        TablesExtension.create(),
                        GitLabExtension.create(),
                        FootnoteExtension.create(),
                        StrikethroughExtension.create(),
                        TaskListExtension.create(),
                        AutolinkExtension.create()
//                        BlockTipQuoteExtension.create()
                );


        final Parser parser = Parser.builder(holder).build();


        String[] strings = markdown.split("\n");
//        Stupid and stupid approach
        boolean tip = false;
        StringBuilder sb = new StringBuilder();
        var length = strings.length;
        String[] badPrefix = {"> x:", "> X:", "> x", "> X", "> bad:", "> no:", "> error:"};
        String[] goodPrefix = {"> √", "> good:", "> ok:", "> yes:", "> right:"};
        String[] infoPrefix = {"> i:", "> I:", "> i", "> I", "> tip:"};
        String[] warnPrefix = {"> !:", "> ！:", "> !", "> ！", "> warn:", "> warning:"};
        String[] sharePrefix = {"> @:", "> at:", "> @"};

        for (int i = 0; i < length; i++) {
            var text = strings[i];
            if (hasTipPrefix(text, badPrefix)) {
                Map<String, Object> prefixHandleRet = prefixHandle(text, badPrefix, length, strings, i);
                text = (String) prefixHandleRet.get("text");
                i = (int) prefixHandleRet.get("i");
                text = StrUtil.format
                        ("<//blockquote class=\"d-tip d-tip-error\"><//p class=\"mdi mdi-close\">{}<\\p><\\blockquote>", text);
                tip = true;
            } else if (hasTipPrefix(text, goodPrefix)) {
                Map<String, Object> prefixHandleRet = prefixHandle(text, goodPrefix, length, strings, i);
                text = (String) prefixHandleRet.get("text");
                i = (int) prefixHandleRet.get("i");
                text = StrUtil.format
                        ("<//blockquote class=\"d-tip d-tip-success\"><//p class=\"mdi mdi-check\">{}<\\p><\\blockquote>", text);
                tip = true;
            } else if (hasTipPrefix(text, warnPrefix)) {
                Map<String, Object> prefixHandleRet = prefixHandle(text, warnPrefix, length, strings, i);
                text = (String) prefixHandleRet.get("text");
                i = (int) prefixHandleRet.get("i");
                text = StrUtil.format
                        ("<//blockquote class=\"d-tip d-tip-warning\"><//p class=\"mdi mdi-exclamation-thick\">{}<\\p><\\blockquote>", text);
                tip = true;
            } else if (hasTipPrefix(text, sharePrefix)) {
                Map<String, Object> prefixHandleRet = prefixHandle(text, sharePrefix, length, strings, i);
                text = (String) prefixHandleRet.get("text");
                i = (int) prefixHandleRet.get("i");
                text = StrUtil.format
                        ("<//blockquote class=\"d-tip d-tip-share\"><//p class=\"mdi mdi-at\">{}<\\p><\\blockquote>", text);
                tip = true;
            } else if (hasTipPrefix(text, infoPrefix)) {
                Map<String, Object> prefixHandleRet = prefixHandle(text, infoPrefix, length, strings, i);
                text = (String) prefixHandleRet.get("text");
                i = (int) prefixHandleRet.get("i");
                text = StrUtil.format
                        ("<//blockquote class=\"d-tip d-tip-info\"><//p class=\"mdi mdi-information-variant\">{}<\\p><\\blockquote>", text);
                tip = true;
            }
            sb.append(text).append('\n');
        }
        System.out.println(sb);
//        for (String string : strings) {
//            sb.append(string).append('\n');
//        }

        final Node document = parser.parse(sb.toString());


//        final HtmlRenderer renderer = HtmlRenderer.builder(holder).build();
        if (renderer == null) {
            renderer = HtmlRenderer.builder(holder).build();
        }
        System.out.println("--------------------");
        String render = renderer.render(document);
        if (tip) {
            return render.replaceAll("&lt;//", "<")
                    .replaceAll("class=&ldquo;", "class=\"")
                    .replaceAll("&rdquo;&gt;", "\">")
                    .replaceAll("&gt;", ">")
                    .replaceAll("&lt;\\\\", "</");
        }
        return render;
    }

    @NotNull
    private static String getReplaceString(String text, String[] replaceStrList) {
        for (String s : replaceStrList) {
            if (text.startsWith(s)) {
                text = text.replaceFirst(s, "");
            }
        }
        return text;
    }

    private static boolean hasTipPrefix(String text, String[] replaceStrList) {
        for (String s : replaceStrList) {
            if (text.startsWith(s)) {
                return true;
            }
        }
        return false;
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
        public void setAttributes(@NotNull Node node, @NotNull AttributablePart attributablePart,
                                  @NotNull MutableAttributes mutableAttributes) {
            var href = mutableAttributes.get("href");
            if ((node instanceof Link || node instanceof AutoLink) && attributablePart == AttributablePart.LINK) {
                if (href != null && href.getValue() != null && !href.getValue().contains(SITE_HOST)) {
                    mutableAttributes.replaceValue("rel", "nofollow");
                }
                mutableAttributes.addValue("target", "_blank");
            }

            if ((node instanceof Image)) {
                mutableAttributes.addValue("loading", "lazy");
                var src = mutableAttributes.get("src");
                if (src != null && src.getValue().startsWith("https://cdn.nlark")) {
                    mutableAttributes.addValue("referrerPolicy", "no-referrer");
                }

            }
//            StrongEmphasis
            if (node instanceof Heading) {
                mutableAttributes.addValue("id", ((Heading) node).getText());
            }
            //todo https://github.com/vsch/flexmark-java/wiki/Writing-Extensions
//            if (node instanceof BlockQuote) {
//                var text = node.getChars();
//                if (text.startsWith("> x")) {
//                    mutableAttributes.addValue("class", "d-tip d-tip-error");
//                } else if (text.startsWith("> √")) {
//                    mutableAttributes.addValue("class", "d-tip d-tip-success");
//                } else if (text.startsWith("> !")) {
//                    mutableAttributes.addValue("class", "d-tip d-tip-warning");
//                } else if (text.startsWith("> @")) {
//                    mutableAttributes.addValue("class", "d-tip d-tip-share");
//                }
//            }
        }
    }
}


