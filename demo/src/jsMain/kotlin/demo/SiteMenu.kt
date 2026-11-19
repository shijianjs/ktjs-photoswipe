package demo

import demo.page.gallery.GalleryPage
import demo.page.overview.OverviewPage
import io.github.shijianjs.ktjs.demo.nav.DemoMenu
import io.github.shijianjs.ktjs.demo.nav.Entry
import io.github.shijianjs.ktjs.demo.nav.Group

/** The PhotoSwipe version this demo is built against; the navbar shows it under the site name. */
const val SITE_VERSION: String = "5.4.4"

/** The documentation site; its pages describe the very demo next to it. */
const val DOCS_URL: String = "https://shijianjs.github.io/ktjs-photoswipe/"

/** Dokka reference assembled by `pagesSite` next to the demo. */
const val API_URL: String = "https://shijianjs.github.io/ktjs-photoswipe/api/"

/** Source repository, linked from the navbar's link menu. */
const val SOURCE_URL: String = "https://github.com/shijianjs/ktjs-photoswipe"

internal val START: Group = Group("start", "lucide:sparkles")

internal val EXAMPLES: Group = Group("examples", "lucide:play")

/**
 * The site: one entry per page. Adding a page means adding one entry here and its titles in
 * [siteTexts] — the sidebar, the breadcrumb, the router and the page cache all derive from this list.
 */
fun siteMenu(): DemoMenu = DemoMenu(
    listOf(
        Entry("overview", START, "lucide:sparkles") { OverviewPage() },
        Entry("gallery", EXAMPLES, "lucide:images") { GalleryPage() },
    ),
)
