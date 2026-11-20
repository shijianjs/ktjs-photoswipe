package demo.page.overview

import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import demo.API_URL
import demo.DOCS_URL
import demo.SOURCE_URL
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.anchor
import io.github.shijianjs.ktjs.demo.page.codeText
import io.github.shijianjs.ktjs.demo.page.paragraph
import io.github.shijianjs.ktjs.demo.page.row
import io.github.shijianjs.ktjs.demo.page.sectionBlock
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.element.unaryPlus
import web.html.HtmlTagName

/** Every string this page shows, in one language. */
private class OverviewTexts(
    val title: String,
    val summary: String,
    val docsLink: String,
    val sourceLink: String,
    val apiLink: String,
    val purposeHeading: String,
    val body: String,
    val bullets: List<String>,
    val coordinatesHeading: String,
    val coordinatesNote: String,
    val nextHeading: String,
    val nextBody: String,
)

private val overviewEnglish: OverviewTexts = OverviewTexts(
    title = "Overview",
    summary = "A Kotlin/JS wrapper for PhotoSwipe 5: the options object is typed, the module " +
        "loader is a function, and the lightbox keeps its own state.",
    docsLink = "Documentation",
    sourceLink = "Source repository",
    apiLink = "API reference",
    purposeHeading = "What it wraps",
    body = "PhotoSwipe builds its UI only when it opens, so a wrapper mostly has to declare one " +
        "large options object. Here every option is a typed field: a misspelled `showHideAnimationType` " +
        "string fails at compile time, `pswpModule` is the loader function upstream expects, and " +
        "the callbacks are typed.",
    bullets = listOf(
        "photoSwipeOptions { … } — the options object, one property per upstream option.",
        "createPhotoSwipeLightbox(options) — builds the lightbox over a gallery element.",
        "photoSwipeModuleLoader() / DataSource — the two values the lightbox cannot declare itself.",
    ),
    coordinatesHeading = "Coordinates",
    coordinatesNote = "The demo next to this site is built with the wrapper itself, so every page " +
        "doubles as an integration test.",
    nextHeading = "Reading order",
    nextBody = "The overview, then the gallery: click a tile, pan the open image, and watch the " +
        "status line follow the slide index.",
)

private val overviewChinese: OverviewTexts = OverviewTexts(
    title = "概览",
    summary = "PhotoSwipe 5 的 Kotlin/JS 封装：选项对象有类型、模块加载器是一个函数、灯箱自己持有状态。",
    docsLink = "使用文档",
    sourceLink = "源码仓库",
    apiLink = "API 文档",
    purposeHeading = "它封装了什么",
    body = "PhotoSwipe 只在打开时才构建界面，所以封装里最主要的是一份很大的选项对象。这里每个选项都是" +
        "有类型的字段：`showHideAnimationType` 这类字符串拼错在编译期就报错，`pswpModule` 就是上游要求的" +
        "加载器函数，回调也有类型。",
    bullets = listOf(
        "photoSwipeOptions { … } —— 选项对象，每个上游选项对应一个属性。",
        "createPhotoSwipeLightbox(options) —— 在一个画廊元素上建灯箱。",
        "photoSwipeModuleLoader() / DataSource —— 灯箱自己声明不了的两个值。",
    ),
    coordinatesHeading = "坐标",
    coordinatesNote = "这个站点旁边的 demo 本身就用这个封装搭建，每页同时充当它的集成测试。",
    nextHeading = "建议顺序",
    nextBody = "先看概览，再看画廊：点一个方块、拖动打开后的图片，看状态行跟着索引变化。",
)

private val overviewTexts: Translations<OverviewTexts> = texts(
    Langs.EN to overviewEnglish,
    Langs.ZH_HANS to overviewChinese,
)

/**
 * Landing page: what the library wraps, how it is consumed, where the reference lives.
 *
 * The sections are plain blocks, not components: they have nothing to swap at run time, so a
 * component per section would only add indirection.
 */
internal class OverviewPage : DemoPage() {

    private val texts = Binder(overviewTexts)


    init {
        addLink(texts.bind(anchor("", DOCS_URL, external = true), { it.docsLink }))
        addLink(texts.bind(anchor("", API_URL, external = true), { it.apiLink }))
        addLink(texts.bind(anchor("", SOURCE_URL, external = true), { it.sourceLink }))

        val purpose = sectionBlock()
        texts.bind(purpose.heading, { it.purposeHeading })
        val bullets = HtmlTagName.ul {
            List(3) { index -> texts.bind(+HtmlTagName.li()) { it.bullets.getOrNull(index).orEmpty() } }
        }
        purpose.body.append(texts.bind(paragraph(), { it.body }), bullets)

        val coordinates = sectionBlock()
        texts.bind(coordinates.heading, { it.coordinatesHeading })
        texts.bind(coordinates.hint, { it.coordinatesNote })
        coordinates.body.append(
            row(codeText("implementation(\"io.github.shijianjs:ktjs-photoswipe:5.4.4-kt.1\")")),
            row(texts.bind(anchor("", SOURCE_URL, external = true), { it.sourceLink })),
        )

        val next = sectionBlock()
        texts.bind(next.heading, { it.nextHeading })
        next.body.append(texts.bind(paragraph(), { it.nextBody }))

        body.append(purpose.root, coordinates.root, next.root)
    }

    override fun applyTranslation(lang: Lang) {
        val current = overviewTexts[lang]
        setTitle(current.title)
        setSummary(current.summary)
        texts.apply(lang)
    }

    companion object : CustomElementReference<OverviewPage>(OverviewPage::class.js) {
        val NAME: TagName<OverviewPage> = TagName("photoswipe-overview-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<photoswipe-overview-page>`. */
internal fun newOverviewPage(): OverviewPage {
    OverviewPage.define()
    return OverviewPage.NAME()
}
