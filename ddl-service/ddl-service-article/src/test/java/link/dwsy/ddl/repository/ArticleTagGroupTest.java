package link.dwsy.ddl.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import link.dwsy.ddl.XO.Enum.Article.ArticleState;
import link.dwsy.ddl.XO.VO.fieldVO;
import link.dwsy.ddl.XO.VO.testVo;
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

    @Test
    public void ProjectionsTest() {
        testVo firstById = articleTagRepository.findTopById(1L);
        System.out.println(firstById.getId());
    }

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
        articleGroupRepository.save(ArticleGroup.builder().name("??????").build());
        articleGroupRepository.save(ArticleGroup.builder().name("??????").build());
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
        //??????????????????
        var tagList = new String[]{
                "??????",
                "??????",
                "JavaScript",
                "GitHub",
                "??????",
                "Vue.js",
                "??????",
                "??????",
//                "Java",
                "????????????",
                "CSS",
                "Node.js",
                "?????????",
                "?????????",
                "????????????",
                "????????????",
                "HTML",
                "Android",
                "React.js",
                "Linux",
                "???????????????",
                "Git",
                "Python",
                "Webpack",
                "??????",
                "MySQL",
                "????????????",
                "??????",
                "??????",
                "HTTP",
                "ECMAScript 6",
                "iOS",
                "??????",
                "Redis",
                "??????",
                "Nginx",
                "???????????????",
                "Google",
                "????????????",
                "Docker",
                "??????",
                "jQuery",
                "Chrome",
                "???????????????",
                "????????????",
                "APP",
                "?????????",
                "??????",
                "Spring",
                "React Native",
                "Android Studio",
                "Angular.js",
                "Mac",
                "????????????",
//                "Go",
                "Bootstrap",
                "Vuex",
                "Apple",
                "???????????????",
                "PHP",
                "Photoshop",
                "????????????",
                "API",
                "????????????",
                "MongoDB",
                "????????????",
                "TypeScript",
                "????????????",
                "????????????",
                "??????",
                "Sublime Text",
                "??????",
                "?????????",
                "gradle",
                "?????????",
                "Canvas",
                "??????",
                "??????",
                "MVVM",
                "Material Design",
                "Swift",
                "??????",
                "?????????",
                "Markdown",
                "????????????",
//                "C++",
                "Visual Studio Code",
                "Xcode",
                "?????????",
                "RxJava",
                "Spring Boot",
                "??????",
                "??????",
                "HTTPS",
                "Objective-C",
                "NPM",
                "??????",
                "Flutter",
                "??????",
                "JSON",
                "Ajax",
                "Icon",
                "??????",
                "JVM",
                "????????????",
                "DOM",
                "Debug",
                "?????????",
                "Redux",
                "?????????",
                "Ubuntu",
                "Eclipse",
                "SQL",
                "??????????????????",
                "????????????",
                "LeetCode",
                "????????????",
                "SCSS",
                "maven",
                "MyBatis",
                "??????",
                "Kotlin",
                "Promise",
                "Sketch",
                "C",
                "????????????",
                "??????",
                "IntelliJ IDEA",
                "???????????????",
                "vue-router",
                "SVG",
                "?????????",
                "VIM",
                "Apache",
                "????????????",
                "Windows",
                "Facebook",
                "?????????",
                "Element",
                "??????",
                "SEO",
                "????????????",
                "Kubernetes",
                "Spring Cloud",
                "Unity3D",
                "axios",
                "Kafka",
                "TCP/IP",
                "Elasticsearch",
                "Express",
                "Java EE",
                "???????????????",
                "Microsoft",
                "????????????",
                "?????????",
                "?????????",
                "Gulp",
                "TensorFlow",
                "????????????",
                "???????????????",
                "ECharts",
                "Hadoop",
                "SQLite",
                "????????????",
                "Tomcat",
                "Vite",
                "WebSocket",
                "?????????",
                "Firefox",
                "??????",
                "APK",
                "?????????",
                "WebGL",
                "?????????",
                "Django",
                "Webkit",
                "??????",
                "?????????",
                "NoSQL",
                "?????????",
                "Atom",
                "MVC",
                "uni-app",
                "??????",
                "??????",
                "RabbitMQ",
                "Shell",
                "ZooKeeper",
                "Electron",
                "three.js",
                "flexbox",
                "????????????",
                "CentOS",
                "V2EX",
                "Spark",
                "d3.js",
                "GitLab",
                "Postman",
                "UI Kit",
                "Less",
                "????????????",
                "Safari",
                "Dubbo",
                ".NET",
                "????????????",
                "Laravel",
                "Weex",
                "Twitter",
                "Netty",
                "ORM",
                "SSH",
                "Wireshark",
                "PostgreSQL",
                "????????????",
                "Jenkins",
                "Ruby",
                "Sea.js",
                "UML",
                "RocketMQ",
                "JetBrains",
                "ionic",
                "?????????????????????",
                "?????????",
                "macOS",
                "Grunt",
                "koa",
                "????????????",
                "NLP",
                "????????????",
                "Oracle",
                "??????????????????",
                "SVN",
                "??????",
                "Flask",
                "Hacker News",
                "????????????",
                "??????",
                "Babel",
                "?????????",
                "PostCSS",
                "ESLint",
                "CDN",
                "DNS",
                "Scala",
                "Backbone.js",
                "????????????",
//                "Rust",
                "??????",
                "Lua",
                "Flux",
                "????????????????????",
                "MVP",
                "????????????",
                "Retrofit",
                "C#",
                "Ant Design",
                "?????????",
                "OKHttp",
                "CMS",
                "??????",
                "PyCharm",
                "GraphQL",
                "ECMAScript 8",
                "Yarn",
                "Medium",
                "??????",
                "ReactiveX",
                "???????????????",
                "????????????",
                "Underscore.js",
                "Apple Watch",
                "V8",
                "DNodeJS",
                "????????????",
                "Cocoa",
                "Web Components",
                "Instagram",
                "Meteor.js",
                "Excel",
                "WebAssembly",
                "Keynote",
                "????????????",
                "Android Wear",
                "WebRTC",
                "Uber",
                "RxJS",
                "CoffeeScript",
                "????????????",
                "iView",
                "SaaS",
                "?????????",
                "ThinkPHP",
                "Ember.js",
                "Bower",
                "Swagger",
                "WebP",
                "OpenCV",
                "????????????",
                "???????????????",
                "Zepto.js",
                "LLVM",
                "????????????",
                "Flink",
                "Egg.js",
                "HBase",
                "Android Jetpack",
                "AB??????",
                "XSS",
                "mpvue",
                "??????",
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
                "?????????",
                "LaTex",
                "CTO",
                "RxSwift",
                "Hibernate",
                "Travis CI",
                "??????",
                "????????????",
                "DaoCloud",
                "PWA",
                "????????????",
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
                "??????",
                "??????",
                "Apache ActiveMQ",
                "????????????",
                "Amazon",
                "Glide",
                "????????????",
                "MariaDB",
                "Memcached",
                "Core ML",
                "?????????",
                "HDFS",
                "NumPy",
                "HarmonyOS",
                "MATLAB",
                "ARKit",
                "????????????",
                "WordPress",
                "??????",
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
                "????????????",
                "??????",
                "????????????",
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
                "??????????????????",
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
                "??????????????????",
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
                "????????????",
                "?????????",
                "R",
                "Groovy",
                "Vant",
                "MobX",
                "etcd",
                "???????????????",
                "Linkedin",
                "Swoole",
                "??????",
                "RSS",
                "Logstash",
                "Touch bar",
                "Firebase",
                "web.py",
                "DBA",
                "OpenAI",
                "KVM",
                "????????????",
                "Grafana",
                "AndroidAnnotations",
                "Debian",
                "Highlight.js",
                "rollup.js",
                "VuePress",
                "????????????",
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
                "C??????",
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
                "????????????",
                "??????",
                "?????????",
                "Daydream",
                "Haskell",
                "Preact",
                "LevelDB",
                "GPU",
                "pyspider",
                "?????????",
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
                "??????",
                "Symfony",
                "?????????",
                "Jekyll",
                "Puppeteer",
                "Agera ",
                "CasperJS",
                "?????????????????????",
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
                "??????",
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
                "????????????????????",
                "mPaaS",
                "NuGet",
                "Svelte",
                "CMake",
                "????????????",
                "Godot",
                "NEO",
                "???????????????",
                "AMA",
                "GWT",
                "Chameleon",
                "tvOS",
                "????????????",
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
                "?????????",
                "Fes.js",
                "?????????",
                "?????????",
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
