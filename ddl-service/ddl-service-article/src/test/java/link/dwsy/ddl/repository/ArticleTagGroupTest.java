package link.dwsy.ddl.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.entity.Article.ArticleGroup;
import link.dwsy.ddl.entity.Article.ArticleTag;
import link.dwsy.ddl.entity.QA.QaTag;
import link.dwsy.ddl.repository.Article.ArticleContentRepository;
import link.dwsy.ddl.repository.Article.ArticleFieldRepository;
import link.dwsy.ddl.repository.Article.ArticleGroupRepository;
import link.dwsy.ddl.repository.Article.ArticleTagRepository;
import link.dwsy.ddl.repository.QA.QaQuestionTagRepository;
import link.dwsy.ddl.repository.User.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @Author Dwsy
 * @Date 2022/8/24
 */
@SpringBootTest
public class ArticleTagGroupTest {


    @Resource
    ArticleTagRepository articleTagRepository;
    @Resource
    ArticleContentRepository articleContentRepository;

    @Resource
    UserRepository userRepository;

    @Resource

    ArticleFieldRepository articleFieldRepository;

    @Resource
    ArticleGroupRepository articleGroupRepository;

    @Test
    public void saveTag() {
        ArticleTag t1 = ArticleTag.builder().name("Java").tagInfo("info").build();
        ArticleTag t2 = ArticleTag.builder().name("C++").tagInfo("info").build();
        ArticleTag t3 = ArticleTag.builder().name("Rust").tagInfo("info").build();
        ArticleTag t4 = ArticleTag.builder().name("GO").tagInfo("info").build();
        List<ArticleTag> articleTags = Arrays.asList(t1, t2, t3, t4);
        articleTagRepository.saveAll(articleTags);
        articleTagRepository.findAll().forEach(System.out::println);
    }

//    @Test
//    public void ProjectionsTest() {
//        testVo firstById = articleTagRepository.findTopById(1L);
//        System.out.println(firstById.getId());
//    }

    @Test
    public void updateTag() {
        ArticleTag articleTag = articleTagRepository.findById(1580212312462065672L).get();
        articleTag.setTagInfo("{1}");
        articleTagRepository.save(articleTag);
    }

    @Test
    public void lmtTest() {
        articleGroupRepository.logicallyDeleteById(1L);
    }

    @Test
    public void saveContent() {
//        Long[] tagSid = {65L, 66L};
//        Set<ArticleTag> tags = new HashSet<>(articleTagRepository.findAllById(Arrays.asList(tagSid)));
//        ArticleGroup articleGroup = articleGroupRepository.findById(63L).get();
//        User user = userRepository.findById(18L).get();
//        ArticleContent articleContent = ArticleContent.builder().textMd("#TEST").textHtml("html").textPure("pure").build();
//        ArticleField build1 = ArticleField.builder().user(user).articleContent(articleContent)
//.summary("summary").articleGroup(articleGroup)
//.title("title").banner("banner.png").articleTags(tags).build();
//        ArticleField build2 = ArticleField.builder().user(user).articleContent(articleContent)
//.summary("summary").articleGroup(articleGroup)
//.title("title2").banner("banner.png").articleTags(tags).build();
//        articleFieldRepository.save(build1);

    }

    @Test
    public void getFieldListByTagId() {
//        articleContentRepository.findById(39L);
        Collection<Long> ids = articleTagRepository.findArticleContentIdListById(1L);

        Page<fieldVO> fieldVO = articleFieldRepository
                .findAllByIdInAndDeletedIsFalseAndArticleState
                        (ids, ArticleState.published, PageRequest.of(0, 10));
        System.out.printf(fieldVO.getContent().toString());

    }

    @Test
    public void getFieldListByGroupId() throws JsonProcessingException {
//        articleContentRepository.findById(39L);
        Page<fieldVO> fieldVO = articleFieldRepository
                .findAllByDeletedIsFalseAndArticleGroupIdAndArticleState
                        (2L, ArticleState.published, PageRequest.of(0, 10));
        System.out.println(new ObjectMapper().writeValueAsString(fieldVO));
//        fieldVO.forEach(System.out::println);
//        articleTagRepository.getFieldListByTagId();
//        articleContentRepository.findAll().forEach(System.out::println);

    }

    @Test
    public void group() {
        articleGroupRepository.save(ArticleGroup.builder().name("前端").build());
        articleGroupRepository.save(ArticleGroup.builder().name("后端").build());
    }

    @Test
    public void listByTag() {
        for (fieldVO articleField : articleFieldRepository.findByDeletedFalseAndArticleStateAndArticleTags_Id(ArticleState.published, 1L, PageRequest.of(0, 10))) {
            articleField.getArticleTags().forEach(t -> {
                System.out.println(t.getName());
                System.out.println(t.getCreateTime());
            });
            System.out.println("----------");
        }

    }

    @Test
    public void test1() {
        AddJuejinTagList();
    }

    @Test
    public void test2() {
        AddJuejinTagList();
    }

    public void AddJuejinTagList() {
        //数据来源掘金
        var tagList = new String[]{
                "前端",
                "后端",
                "JavaScript",
                "GitHub",
                "面试",
                "Vue.js",
                "架构",
                "算法",
//                "Java",
                "代码规范",
                "CSS",
                "Node.js",
                "数据库",
                "程序员",
                "前端框架",
                "设计模式",
                "HTML",
                "Android",
                "React.js",
                "Linux",
                "微信小程序",
                "Git",
                "Python",
                "Webpack",
                "开源",
                "MySQL",
                "人工智能",
                "设计",
                "产品",
                "HTTP",
                "ECMAScript 6",
                "iOS",
                "全栈",
                "Redis",
                "微信",
                "Nginx",
                "正则表达式",
                "Google",
                "机器学习",
                "Docker",
                "黑客",
                "jQuery",
                "Chrome",
                "响应式设计",
                "编程语言",
                "APP",
                "命令行",
                "创业",
                "Spring",
                "React Native",
                "Android Studio",
                "Angular.js",
                "Mac",
                "产品经理",
//                "Go",
                "Bootstrap",
                "Vuex",
                "Apple",
                "数据可视化",
                "PHP",
                "Photoshop",
                "操作系统",
                "API",
                "图片资源",
                "MongoDB",
                "阿里巴巴",
                "TypeScript",
                "数据挖掘",
                "深度学习",
                "安全",
                "Sublime Text",
                "运维",
                "设计师",
                "gradle",
                "微服务",
                "Canvas",
                "招聘",
                "爬虫",
                "MVVM",
                "Material Design",
                "Swift",
                "源码",
                "云计算",
                "Markdown",
                "敏捷开发",
//                "C++",
                "Visual Studio Code",
                "Xcode",
                "物联网",
                "RxJava",
                "Spring Boot",
                "腾讯",
                "动效",
                "HTTPS",
                "Objective-C",
                "NPM",
                "字体",
                "Flutter",
                "运营",
                "JSON",
                "Ajax",
                "Icon",
                "测试",
                "JVM",
                "虚拟现实",
                "DOM",
                "Debug",
                "电子书",
                "Redux",
                "浏览器",
                "Ubuntu",
                "Eclipse",
                "SQL",
                "掘金翻译计划",
                "负载均衡",
                "LeetCode",
                "数据结构",
                "SCSS",
                "maven",
                "MyBatis",
                "配色",
                "Kotlin",
                "Promise",
                "Sketch",
                "C",
                "数据分析",
                "游戏",
                "IntelliJ IDEA",
                "函数式编程",
                "vue-router",
                "SVG",
                "区块链",
                "VIM",
                "Apache",
                "性能优化",
                "Windows",
                "Facebook",
                "支付宝",
                "Element",
                "稀土",
                "SEO",
                "神经网络",
                "Kubernetes",
                "Spring Cloud",
                "Unity3D",
                "axios",
                "Kafka",
                "TCP/IP",
                "Elasticsearch",
                "Express",
                "Java EE",
                "响应式编程",
                "Microsoft",
                "增强现实",
                "分布式",
                "大数据",
                "Gulp",
                "TensorFlow",
                "单元测试",
                "计算机视觉",
                "ECharts",
                "Hadoop",
                "SQLite",
                "远程工作",
                "Tomcat",
                "Vite",
                "WebSocket",
                "嵌入式",
                "Firefox",
                "求职",
                "APK",
                "服务器",
                "WebGL",
                "机器人",
                "Django",
                "Webkit",
                "投资",
                "比特币",
                "NoSQL",
                "编译器",
                "Atom",
                "MVC",
                "uni-app",
                "百度",
                "科幻",
                "RabbitMQ",
                "Shell",
                "ZooKeeper",
                "Electron",
                "three.js",
                "flexbox",
                "连续集成",
                "CentOS",
                "V2EX",
                "Spark",
                "d3.js",
                "GitLab",
                "Postman",
                "UI Kit",
                "Less",
                "掘金日报",
                "Safari",
                "Dubbo",
                ".NET",
                "交互设计",
                "Laravel",
                "Weex",
                "Twitter",
                "Netty",
                "ORM",
                "SSH",
                "Wireshark",
                "PostgreSQL",
                "网络协议",
                "Jenkins",
                "Ruby",
                "Sea.js",
                "UML",
                "RocketMQ",
                "JetBrains",
                "ionic",
                "如何当个好爸爸",
                "状态机",
                "macOS",
                "Grunt",
                "koa",
                "线下活动",
                "NLP",
                "搜索引擎",
                "Oracle",
                "掘金技术征文",
                "SVN",
                "直播",
                "Flask",
                "Hacker News",
                "增长黑客",
                "容器",
                "Babel",
                "云原生",
                "PostCSS",
                "ESLint",
                "CDN",
                "DNS",
                "Scala",
                "Backbone.js",
                "消息队列",
//                "Rust",
                "沸点",
                "Lua",
                "Flux",
                "小程序·云开发",
                "MVP",
                "视觉设计",
                "Retrofit",
                "C#",
                "Ant Design",
                "树莓派",
                "OKHttp",
                "CMS",
                "笔记",
                "PyCharm",
                "GraphQL",
                "ECMAScript 8",
                "Yarn",
                "Medium",
                "逆向",
                "ReactiveX",
                "音视频开发",
                "排序算法",
                "Underscore.js",
                "Apple Watch",
                "V8",
                "DNodeJS",
                "午夜话题",
                "Cocoa",
                "Web Components",
                "Instagram",
                "Meteor.js",
                "Excel",
                "WebAssembly",
                "Keynote",
                "汇编语言",
                "Android Wear",
                "WebRTC",
                "Uber",
                "RxJS",
                "CoffeeScript",
                "游戏开发",
                "iView",
                "SaaS",
                "七牛云",
                "ThinkPHP",
                "Ember.js",
                "Bower",
                "Swagger",
                "WebP",
                "OpenCV",
                "年终总结",
                "自动化运维",
                "Zepto.js",
                "LLVM",
                "掘金社区",
                "Flink",
                "Egg.js",
                "HBase",
                "Android Jetpack",
                "AB测试",
                "XSS",
                "mpvue",
                "蓝牙",
                "JUnit",
                "Dart",
                "PyTorch",
                "fir.im",
                "IPython",
                "RequireJS",
                "PhpStorm",
                "WeUI",
                "Chart.js",
                "Rails",
                "Shiro",
                "ReactiveCocoa",
                "Surge",
                "Nuxt.js",
                "SAMSUNG",
                "WWDC",
                "Trello",
                "OpenGL",
                "以太坊",
                "LaTex",
                "CTO",
                "RxSwift",
                "Hibernate",
                "Travis CI",
                "资讯",
                "图像识别",
                "DaoCloud",
                "PWA",
                "强化学习",
                "Slack",
                "VirtualBox",
                "PyQt",
                "EventBus",
                "FFmpeg",
                "Hexo",
                "Solr",
                "Apache Hive",
                "Arduino",
                "iTerm",
                "Y Combinator",
                "监控",
                "数学",
                "Apache ActiveMQ",
                "编译原理",
                "Amazon",
                "Glide",
                "自动驾驶",
                "MariaDB",
                "Memcached",
                "Core ML",
                "图形学",
                "HDFS",
                "NumPy",
                "HarmonyOS",
                "MATLAB",
                "ARKit",
                "团队管理",
                "WordPress",
                "莆田",
                "Dagger",
                "JMeter",
                "Vonic",
                "360",
                "gRPC",
                "Gson",
                "Workflow",
                "Fiddler",
                "Polymer",
                "Scrapy",
                "数字货币",
                "播客",
                "王者荣耀",
                "Serverless",
                "RESTful",
                "CircleCI",
                "Curl",
                "Cocos2d-x",
                "5G",
                "OpenStack",
                "Cython",
                "Axure",
                "Pixate",
                "Fastjson",
                "pandas",
                "PhantomJS",
                "SwiftUI",
                "计算机图形学",
                "HTTP3",
                "Charles",
                "HotFix",
                "Apache Log4j",
                "Elm",
                "Picasso",
                "Emacs",
                "Taro",
                "CI/CD",
                "Google I/O",
                "Unicode",
                "Yii",
                "Qt",
                "Lucene",
                "领域驱动设计",
                "AWS",
                "Kibana",
                "Apache Storm",
                "Selenium",
                "PM2",
                "protobuf",
                "Cordova",
                "Realm",
                "bpython",
                "Tornado",
                "智能合约",
                "快应用",
                "R",
                "Groovy",
                "Vant",
                "MobX",
                "etcd",
                "智能小程序",
                "Linkedin",
                "Swoole",
                "华为",
                "RSS",
                "Logstash",
                "Touch bar",
                "Firebase",
                "web.py",
                "DBA",
                "OpenAI",
                "KVM",
                "边缘计算",
                "Grafana",
                "AndroidAnnotations",
                "Debian",
                "Highlight.js",
                "rollup.js",
                "VuePress",
                "量子计算",
                "Airbnb",
                "TiDB",
                "PyPy",
                "NativeScript",
                "greenDAO",
                "Stylus",
                "Kaggle",
                "Apache Flume",
                "FreeMarker",
                "ZeroMQ",
                "Elixir",
                "scikit-learn",
                "Apache Thrift",
                "Keras",
                "Ansible",
                "Jest",
                "Volley",
                "Caffe",
                "SciPy",
                "Composer",
                "Immutable.js",
                "Akka",
                "ButterKnife",
                "ZXing",
                "Project Lombok",
                "Apache Kylin",
                "Mocha",
                "C语言",
                "Unreal Engine",
                "Erlang",
                "JCenter",
                "Karma",
                "CocoaPods",
                "NestJS",
                "AFNetworking",
                "VisualVM",
                "RPC",
                "Perl",
                "Vagrant",
                "Apache Cassandra",
                "PhoneGap",
                "Apache Mesos",
                "JSPatch",
                "Tinker",
                "WebVR",
                "Yeoman",
                "LeakCanary",
                "WebView",
                "deno",
                "Swarm",
                "WebStorm",
                "Lisp",
                "SDWebImage",
                "掘金圆桌",
                "有赞",
                "无人机",
                "Daydream",
                "Haskell",
                "Preact",
                "LevelDB",
                "GPU",
                "pyspider",
                "客户端",
                "DevOps",
                "Fresco",
                "Apache Ant",
                "Service Mesh",
                "Fedora",
                "Gevent",
                "SonarQube",
                "SymPy",
                "Bluebird.js",
                "Raft",
                "Browserify",
                "Gunicorn",
                "Clojure",
                "uWSGI",
                "Jieba",
                "Snapchat",
                "MPAndroidChart",
                "Xposed",
                "E2E",
                "AMP",
                "Omi",
                "Mockito",
                "NSQ",
                "Twisted",
                "Brython",
                "Bintray",
                "Istio",
                "Fluentd",
                "芯片",
                "Symfony",
                "低代码",
                "Jekyll",
                "Puppeteer",
                "Agera ",
                "CasperJS",
                "计算机组成原理",
                "QUnit",
                "reCAPTCHA",
                "Unix",
                "ORMLite",
                "Android Things",
                "Jasmine",
                "FMDB",
                "Natural Language Toolkit",
                "IndexedDB",
                "ThinkJS",
                "DeepStack",
                "Alamofire",
                "MJRefresh",
                "SaltStack",
                "ReactOS",
                "AsyncDisplayKit",
                "Traefik",
                "marked",
                "Fuchsia",
                "fastlane",
                "Mongoose",
                "Monolog",
                "SnapKit",
                "IGListKit",
                "Nvidia",
                "Jupyter",
                "Perfect",
                "Chrome OS",
                "Bulma",
                "GIS",
                "Anko",
                "Caddy",
                "Knockout",
                "AChartEngine",
                "Julia",
                "StatsD",
                "Parcel",
                "Theano",
                "Vapor",
                "Polycode",
                "libGDX",
                "Feathers",
                "ARCore",
                "mlpack",
                "DroidMVP",
                "Espresso",
                "AIOps",
                "Phabricator",
                "Gin",
                "Classyshark",
                "Carthage",
                "AVA",
                "JitPack",
                "Vuforia",
                "RoboSpice",
                "Stetho",
                "FlatBuffers",
                "Solidity",
                "推广",
                "MessagePack",
                "Buck",
                "Marko",
                "Libratus",
                "EazeGraph",
                "DbInspector",
                "RoboGuic",
                "Fossil",
                "Microsoft Edge",
                "HTM",
                "PyCon",
                "GAN",
                "RTC",
                "Mozilla",
                "Smartisan OS",
                "FoundationDB",
                "iPadOS",
                "掘金·日新计划",
                "mPaaS",
                "NuGet",
                "Svelte",
                "CMake",
                "笔记测评",
                "Godot",
                "NEO",
                "京东小程序",
                "AMA",
                "GWT",
                "Chameleon",
                "tvOS",
                "视频编码",
                "Ramda",
                "Libra",
                "TLA+",
                "D",
                "Debezium",
                "greenplum",
                "SQL Server",
                "Visual Studio",
                "Cocos Creator",
                "arco design",
                "Modern.js",
                "Semi Design",
                "轻服务",
                "Fes.js",
                "青训营",
                "单片机",
                "kerberos"};
//        List<ArticleTag> ArticleTagList = new ArrayList<>();
//
//        for (String s : tagList) {
//            var tag = ArticleTag.builder().name(s).tagInfo(s).build();
//            ArticleTagList.add(tag);
//        }
//        articleTagRepository.saveAll(ArticleTagList);
        List<QaTag> qaTagArrayList = new ArrayList<>();

        for (String s : tagList) {
            var tag = QaTag.builder().name(s).tagInfo(s).build();
            qaTagArrayList.add(tag);
        }
        qaQuestionTagRepository.saveAll(qaTagArrayList);
    }

    @Resource
    QaQuestionTagRepository qaQuestionTagRepository;
}
