package demo.page.gallery

import io.github.shijianjs.ktjs.dom.element.invoke

/** Every string this page shows, in one language. */
internal class GalleryTexts(
    val title: String,
    val summary: String,
    val sectionHeading: String,
    val sectionHint: String,
    val open: String,
    val status: String,
    val close: String,
    val zoom: String,
    val prev: String,
    val next: String,
    val tileLabels: List<String>,
)
