package demo

import io.github.shijianjs.ktjs.photoswipe.createPhotoSwipeLightbox
import io.github.shijianjs.ktjs.photoswipe.core.DataSource
import io.github.shijianjs.ktjs.photoswipe.core.ElementProvider
import io.github.shijianjs.ktjs.photoswipe.core.PhotoSwipeLightbox
import io.github.shijianjs.ktjs.photoswipe.core.padding
import io.github.shijianjs.ktjs.photoswipe.core.photoSwipeOptions
import io.github.shijianjs.ktjs.photoswipe.photoSwipeModuleLoader
import web.cssom.ClassName
import web.dom.document
import web.events.EventHandler
import web.html.HTMLElement
import web.html.HtmlTagName

private const val PROJECT_NAME = "ktjs-photoswipe"

private val texts = Texts(
    mapOf<String, Pair<String, String>>(
        "h1" to ("$PROJECT_NAME demo" to "$PROJECT_NAME 演示"),
        "intro" to Pair(
            "Click any tile to open the lightbox. The gallery is the real PhotoSwipe 5 lightbox " +
                "created through the wrapper; the images are inline SVG data URIs, so the page " +
                "needs no network.",
            "点击任意方块打开灯箱。这个画廊是通过 wrapper 创建的真实 PhotoSwipe 5 灯箱；图片是内联 SVG " +
                "data URI，因此页面不需要网络。",
        ),
        "open" to ("Open first slide" to "从第一张打开"),
        "status" to ("Current slide" to "当前幻灯片"),
        "hint" to (
            "Drag the open image to pan, double-click to zoom, Escape closes it." to
                "打开后拖动图片可平移，双击缩放，按 Escape 关闭。"
        ),
        "close" to ("Close" to "关闭"),
        "zoom" to ("Zoom" to "缩放"),
        "prev" to ("Previous" to "上一张"),
        "next" to ("Next" to "下一张"),
    ),
)

/** The six tiles: colour, size and label of each generated slide. */
private data class Tile(val label: String, val colour: String, val width: Int, val height: Int)

private val tiles = listOf(
    Tile("Mint", "#9ae6b4", 1200, 800),
    Tile("Sky", "#90cdf4", 800, 1200),
    Tile("Amber", "#fbd38d", 1200, 900),
    Tile("Rose", "#fbb6ce", 1000, 1000),
    Tile("Violet", "#d6bcfa", 1400, 800),
    Tile("Slate", "#cbd5e0", 900, 1200),
)

/**
 * Entry point of the demo page.
 *
 * DOM nodes are created and mutated in place — no HTML strings, no `innerHTML` (see the UI
 * conventions in AGENTS.md).
 */
fun main() {
    val root = document.createElement(HtmlTagName.div).apply { className = ClassName("page") }
    val heading = document.createElement(HtmlTagName.h1)
    val intro = document.createElement(HtmlTagName.p).apply { className = ClassName("hint") }
    val hint = document.createElement(HtmlTagName.p).apply { className = ClassName("hint small") }

    val logo = document.createElement(HtmlTagName.img).apply {
        className = ClassName("logo")
        setAttribute("src", "img/$PROJECT_NAME-logo.png")
        setAttribute("alt", "$PROJECT_NAME logo")
    }
    val header = document.createElement(HtmlTagName.header).apply {
        className = ClassName("header")
        append(logo, heading, languageSwitch(texts))
    }

    val gallery = document.createElement(HtmlTagName.div).apply {
        className = ClassName("gallery")
        setAttribute("data-role", "gallery")
    }

    tiles.forEach { tile ->
        val anchor = document.createElement(HtmlTagName.a) as HTMLElement
        anchor.setAttribute("href", svgDataUri(tile))
        anchor.setAttribute("data-pswp-width", "${tile.width}")
        anchor.setAttribute("data-pswp-height", "${tile.height}")
        anchor.setAttribute("data-role", "tile")
        val thumb = document.createElement(HtmlTagName.div).apply {
            className = ClassName("tile")
            setAttribute("style", "background:${tile.colour}")
        }
        val label = document.createElement(HtmlTagName.span).apply {
            className = ClassName("tile-label")
            textContent = tile.label
        }
        thumb.append(label)
        anchor.append(thumb)
        gallery.append(anchor)
    }

    val status = document.createElement(HtmlTagName.p).apply { className = ClassName("view") }

    val lightbox: PhotoSwipeLightbox = createPhotoSwipeLightbox(
        photoSwipeOptions {
            this.gallery = ElementProvider(gallery)
            childSelector = "a"
            dataSource = DataSource(gallery, null)
            bgOpacity = 0.9
            showHideAnimationType = "fade"
            loop = true
            padding = padding(0.0)
            // The lightbox module ships without the core gallery; upstream requires the loader here.
            pswpModule = photoSwipeModuleLoader()
            closeTitle = texts["close"]
            zoomTitle = texts["zoom"]
            arrowPrevTitle = texts["prev"]
            arrowNextTitle = texts["next"]
        },
    )
    lightbox.on("change") { renderStatus(status, lightbox) }
    lightbox.on("close") { status.textContent = "" }
    lightbox.init()

    val openButton = document.createElement(HtmlTagName.button).apply { setAttribute("type", "button") }
    openButton.onclick = EventHandler { lightbox.loadAndOpen(0.0, null, null) }

    val row = document.createElement(HtmlTagName.div).apply {
        className = ClassName("row")
        append(openButton)
    }

    val content = document.createElement(HtmlTagName.main).apply {
        className = ClassName("content")
        append(row, status, gallery)
    }

    fun render() {
        heading.textContent = texts["h1"]
        intro.textContent = texts["intro"]
        hint.textContent = texts["hint"]
        openButton.textContent = texts["open"]
        // The button labels above live inside the lightbox UI, which is only built when it opens,
        // so they are read fresh from `texts` on each click instead of being frozen here.
        lightbox.options.closeTitle = texts["close"]
        lightbox.options.zoomTitle = texts["zoom"]
        lightbox.options.arrowPrevTitle = texts["prev"]
        lightbox.options.arrowNextTitle = texts["next"]
        renderStatus(status, lightbox)
    }
    texts.onChange { render() }
    render()

    root.append(header, intro, hint, content)
    document.body.append(root)
}

private fun renderStatus(status: HTMLElement, lightbox: PhotoSwipeLightbox) {
    val core = lightbox.pswp ?: return
    if (!core.isOpen) return
    status.textContent = "${texts["status"]}: ${core.currIndex + 1} / ${core.getNumItems()}"
}

/** An inline SVG so each slide has a real image to load, without any network request. */
private fun svgDataUri(tile: Tile): String {
    val svg = "<svg xmlns='http://www.w3.org/2000/svg' width='${tile.width}' height='${tile.height}'>" +
        "<rect width='100%' height='100%' fill='${tile.colour}'/>" +
        "<text x='50%' y='50%' font-family='sans-serif' font-size='96' fill='#2d3748' " +
        "text-anchor='middle' dominant-baseline='middle'>${tile.label}</text></svg>"
    return "data:image/svg+xml;charset=utf-8," + encodeURIComponent(svg)
}

/** `encodeURIComponent` for the data URI; `js(...)` keeps the escaping rules identical to the browser. */
private val encodeURIComponent: (String) -> String = js("encodeURIComponent")
