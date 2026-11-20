package demo.page.gallery

import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.Blocks.row
import io.github.shijianjs.ktjs.demo.page.Grid
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.demo.style.Palette
import io.github.shijianjs.ktjs.dom.element.inlineStyle
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.photoswipe.createPhotoSwipeLightbox
import io.github.shijianjs.ktjs.photoswipe.core.DataSource
import io.github.shijianjs.ktjs.photoswipe.core.ElementProvider
import io.github.shijianjs.ktjs.photoswipe.core.PhotoSwipeLightbox
import io.github.shijianjs.ktjs.photoswipe.core.padding
import io.github.shijianjs.ktjs.photoswipe.core.photoSwipeOptions
import io.github.shijianjs.ktjs.photoswipe.photoSwipeModuleLoader
import io.github.shijianjs.ktjs.vaadin.button.ButtonElement
import io.github.shijianjs.ktjs.vaadin.button.createButton
import web.animations.requestAnimationFrame
import web.cssom.AlignItems
import web.cssom.Color
import web.cssom.Display
import web.cssom.JustifyContent
import web.cssom.None
import web.cssom.px
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement
import web.html.HtmlTagName

/**
 * A tile: colour and size of the generated slide, plus its index into [GalleryTexts.tileLabels].
 *
 * The label is an index rather than a word, so a language switch relabels the tiles instead of
 * rebuilding them.
 */

/**
 * The gallery page: six tiles, a status line, and the lightbox that reads both.
 *
 * The lightbox is created once — its options object is the thing being demonstrated, and rebuilding
 * it on every language switch would drop the open slide.
 */
internal class GalleryPage : DemoPage() {

    private val texts = Binder(galleryTexts)

    /** The strings the lightbox reads when it builds its UI; refreshed on every switch. */
    private var current: GalleryTexts = galleryEnglish

    /** Each tile's label node, kept so a switch can rewrite it without touching the structure. */
    private val tileLabels: List<HTMLElement> = tiles.map { tile ->
        HtmlTagName.span { textContent = current.tileLabels.getOrElse(tile.index) { "" } }
    }

    private val tileLinks: List<HTMLElement> = tiles.mapIndexed { index, tile ->
        HtmlTagName.a {
            href = svgDataUri(tile)
            setAttribute("data-pswp-width", "${tile.width}")
            setAttribute("data-pswp-height", "${tile.height}")
            setAttribute("data-role", "tile")
            inlineStyle.apply {
                display = Display.block
                color = Palette.text
                // A tile is a link only because PhotoSwipe reads its children's `href`s.
                textDecoration = None.none
            }
        }.also { anchor ->
            anchor.append(
                HtmlTagName.div {
                    setAttribute("data-role", "tile-body")
                    inlineStyle.apply {
                        display = Display.flex
                        alignItems = AlignItems.center
                        justifyContent = JustifyContent.center
                        minHeight = 160.px
                        backgroundColor = Color(tile.colour)
                        borderRadius = 10.px
                    }
                }.also { body -> body.append(tileLabels[index]) },
            )
        }
    }

    /**
     * The gallery the lightbox reads its items from.
     *
     * Named `galleryElement`, not `gallery`: the options receiver declares `gallery` as an
     * `ElementProvider?`, and an inner receiver wins over this page's own field inside that lambda.
     */
    private val galleryElement: HTMLElement = Grid.NAME { show(260) }

    /** The status line, marked for the end-to-end suite: it mirrors the open slide index. */
    private val status: HTMLElement = HtmlTagName.p {
        setAttribute("data-status", "")
    }

    private var lightbox: PhotoSwipeLightbox? = null

    private var started = false

    private val openButton: ButtonElement = createButton {
        setAttribute("data-action", "open")
        clickEvent.addHandler { lightbox?.loadAndOpen(0.0, null, null) }
    }

    init {
        texts.bind(openButton, { it.open })

        val section = SectionBlock.NAME { show() }
        texts.bind(section.heading, { it.sectionHeading })
        texts.bind(section.hint, { it.sectionHint })
        section.body.append(row(openButton), status, galleryElement)

        body.append(section.root)
        galleryElement.append(*tileLinks.toTypedArray())

        startOnceMounted()
    }

    /**
     * Builds the lightbox once the gallery is in the document.
     *
     * `init()` binds a click handler over the gallery's children, and it appends the lightbox's own
     * root to the page — both want a mounted tree, while a page's initialiser runs before the router
     * attaches the node.
     */
    private fun startOnceMounted() {
        if (started) return
        if (!galleryElement.isConnected) {
            requestAnimationFrame { _ -> startOnceMounted() }
            return
        }
        started = true

        val lightbox = createPhotoSwipeLightbox(
            photoSwipeOptions {
                this.gallery = ElementProvider(galleryElement)
                childSelector = "a"
                dataSource = DataSource(galleryElement, null)
                bgOpacity = 0.9
                showHideAnimationType = "fade"
                loop = true
                padding = padding(0.0)
                // The lightbox module ships without the core gallery; upstream needs the loader.
                pswpModule = photoSwipeModuleLoader()
                closeTitle = current.close
                zoomTitle = current.zoom
                arrowPrevTitle = current.prev
                arrowNextTitle = current.next
            },
        )
        lightbox.on("change") { renderStatus() }
        lightbox.on("close") { status.textContent = "" }
        lightbox.init()
        this.lightbox = lightbox
    }

    /**
     * Reads the open slide off the lightbox core.
     *
     * The core only exists after the first open, and `isOpen` is false between close and the next
     * open, so both are checked before touching the index.
     */
    private fun renderStatus() {
        val core = lightbox?.pswp ?: return
        if (!core.isOpen) return
        status.textContent = "${current.status}: ${core.currIndex + 1} / ${core.getNumItems()}"
    }

    override fun applyTranslation(lang: Lang) {
        current = galleryTexts[lang]
        setTitle(current.title)
        setSummary(current.summary)
        texts.apply(lang)
        tiles.forEachIndexed { index, tile ->
            tileLabels[index].textContent = current.tileLabels.getOrElse(tile.index) { "" }
        }
        lightbox?.options?.let { options ->
            options.closeTitle = current.close
            options.zoomTitle = current.zoom
            options.arrowPrevTitle = current.prev
            options.arrowNextTitle = current.next
        }
        renderStatus()
    }

    companion object : CustomElementReferenceAdapter<GalleryPage>(GalleryPage::class.js, "photoswipe-gallery-page") {

        private val galleryEnglish: GalleryTexts = GalleryTexts(
            title = "Gallery",
            summary = "Six generated slides in a real PhotoSwipe lightbox: the same options object a " +
                "consumer hands over, opened from a tile or from the first slide.",
            sectionHeading = "Lightbox over a gallery",
            sectionHint = "Click any tile to open it; the lightbox is created lazily, so the first click " +
                "also pays for the module load.",
            open = "Open first slide",
            status = "Current slide",
            close = "Close",
            zoom = "Zoom",
            prev = "Previous",
            next = "Next",
            tileLabels = listOf("Mint", "Sky", "Amber", "Rose", "Violet", "Slate"),
        )
        private val galleryChinese: GalleryTexts = GalleryTexts(
            title = "画廊",
            summary = "六张生成的幻灯片装在一个真实 PhotoSwipe 灯箱里：用的就是使用者交出去的那份选项对象，" +
                "可以从方块点开，也可以从第一张打开。",
            sectionHeading = "画廊上的灯箱",
            sectionHint = "点任意方块打开；灯箱是懒建的，所以第一次点击还要付模块加载的代价。",
            open = "从第一张打开",
            status = "当前幻灯片",
            close = "关闭",
            zoom = "缩放",
            prev = "上一张",
            next = "下一张",
            tileLabels = listOf("薄荷", "天蓝", "琥珀", "玫瑰", "紫罗兰", "石板"),
        )
        private val galleryTexts: Translations<GalleryTexts> = texts(
            Langs.EN to galleryEnglish,
            Langs.ZH_HANS to galleryChinese,
        )
        private val tiles = listOf(
            Tile(0, "#9ae6b4", 1200, 800),
            Tile(1, "#90cdf4", 800, 1200),
            Tile(2, "#fbd38d", 1200, 900),
            Tile(3, "#fbb6ce", 1000, 1000),
            Tile(4, "#d6bcfa", 1400, 800),
            Tile(5, "#cbd5e0", 900, 1200),
        )
        /** An inline SVG so each slide has a real image to load, without any network request. */
        private fun svgDataUri(tile: Tile): String {
            val svg = "<svg xmlns='http://www.w3.org/2000/svg' width='${tile.width}' height='${tile.height}'>" +
                "<rect width='100%' height='100%' fill='${tile.colour}'/>" +
                "<text x='50%' y='50%' font-family='sans-serif' font-size='96' fill='#2d3748' " +
                "text-anchor='middle' dominant-baseline='middle'>${tile.index + 1}</text></svg>"
            return "data:image/svg+xml;charset=utf-8," + encodeURIComponent(svg)
        }
        /** `encodeURIComponent` for the data URI; `js(...)` keeps the escaping rules identical to the browser. */
        private val encodeURIComponent: (String) -> String = js("encodeURIComponent")
    }
}
