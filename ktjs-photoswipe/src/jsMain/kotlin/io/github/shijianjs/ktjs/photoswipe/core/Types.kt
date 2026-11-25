package io.github.shijianjs.ktjs.photoswipe.core

import js.objects.unsafeJso
import js.reflect.Reflect
import kotlin.js.JsAny
import kotlin.js.unsafeCast
import web.html.HTMLElement

/** A point or a size in CSS pixels, upstream `Point`. */
external interface Point {
    /** Horizontal coordinate or width. */
    var x: Double

    /** Vertical coordinate or height. */
    var y: Double

    /** Optional identifier the library attaches when it needs one. */
    var id: JsAny?
}

/** Inner padding of the slide area, upstream `Padding`. */
external interface Padding {
    /** Top padding. */
    var top: Double

    /** Bottom padding. */
    var bottom: Double

    /** Left padding. */
    var left: Double

    /** Right padding. */
    var right: Double
}

/** A rectangle in viewport coordinates, upstream `Bounds`. */
external interface Bounds {
    /** Left edge. */
    var x: Double

    /** Top edge. */
    var y: Double

    /** Width. */
    var w: Double

    /** Height. */
    var h: Double
}

/** Builds a [Point]. */
fun point(x: Double, y: Double): Point = unsafeJso {
    this.x = x
    this.y = y
}

/** Builds an even [Padding]. */
fun padding(all: Double): Padding = padding(all, all, all, all)

/** Builds a [Padding]. */
fun padding(top: Double, right: Double, bottom: Double, left: Double): Padding = unsafeJso {
    this.top = top
    this.right = right
    this.bottom = bottom
    this.left = left
}

/** One item of the data source, upstream `SlideData`. */
external interface SlideData {
    /** Thumbnail element the gallery zooms out of. */
    var element: HTMLElement?

    /** Full-size image URL. */
    var src: String?

    /** `srcset` passed to the image. */
    var srcset: String?

    /** Image width, deprecated alias of [width]. */
    var w: Double?

    /** Image height, deprecated alias of [height]. */
    var h: Double?

    /** Image width in pixels. */
    var width: Double?

    /** Image height in pixels. */
    var height: Double?

    /** Placeholder image shown before the full-size one loads. */
    var msrc: String?

    /** `alt` text of the image. */
    var alt: String?

    /** Whether the thumbnail is cropped client-side. */
    var thumbCropped: Boolean?

    /** `title` of the slide. */
    var title: String?

    /** HTML type of a non-image slide, upstream `html` content option. */
    var html: String?

    /** Whether this item is the video slide. */
    var isVideo: Boolean?
}

/**
 * The data source: an array of items, or a gallery element plus its items, upstream
 * `SlideData[] | { gallery: HTMLElement; items?: HTMLElement[] }`.
 */
external interface DataSource

/** Wraps an array of [SlideData] as a [DataSource]. */
fun DataSource(items: Array<SlideData>): DataSource = items.unsafeCast<DataSource>()

/** The object form of the data source, upstream `{ gallery, items? }`. */
private external interface GalleryDataSource {
    /** The gallery element. */
    var gallery: HTMLElement

    /** The thumbnail elements inside it. */
    var items: Array<HTMLElement>?
}

/** Builds a gallery-object [DataSource]. */
fun DataSource(gallery: HTMLElement, items: Array<HTMLElement>?): DataSource =
    unsafeJso<GalleryDataSource> {
        this.gallery = gallery
        this.items = items
    }.unsafeCast<DataSource>()

/** Elements the lightbox may read thumbnails from, upstream `ElementProvider`. */
external interface ElementProvider

/** Wraps a selector as an [ElementProvider]. */
fun ElementProvider(selector: String): ElementProvider = selector.unsafeCast<ElementProvider>()

/** Wraps one element as an [ElementProvider]. */
fun ElementProvider(element: HTMLElement): ElementProvider = element.unsafeCast<ElementProvider>()

/** Wraps an array of elements as an [ElementProvider]. */
fun ElementProvider(elements: Array<HTMLElement>): ElementProvider = elements.unsafeCast<ElementProvider>()

/** A click action name, upstream `ActionType`. */
external interface ActionType

/** Builds an [ActionType] from its upstream string. */
@Suppress("FunctionName")
fun ActionType(value: String): ActionType = value.unsafeCast<ActionType>()

/** The click actions PhotoSwipe understands. */
object ActionTypes {
    /** Close the gallery. */
    val close: ActionType get() = ActionType("close")

    /** Go to the next slide. */
    val next: ActionType get() = ActionType("next")

    /** Zoom in or out. */
    val zoom: ActionType get() = ActionType("zoom")

    /** Zoom when possible, close otherwise. */
    val zoomOrClose: ActionType get() = ActionType("zoom-or-close")

    /** Show or hide the UI controls. */
    val toggleControls: ActionType get() = ActionType("toggle-controls")
}

/** A custom click action, upstream `ActionFn`. */
typealias ActionFn = (Point, JsAny) -> Unit

/** Builds a [SlideData] object. */
fun slideData(build: SlideData.() -> Unit): SlideData = unsafeJso(build)

/**
 * Sets a key upstream keeps in `Record<string, any>` rather than in the declared shape.
 *
 * `SlideData` is `_SlideData & Record<string, any>`, so extra keys are legal and the library reads
 * them back (for example `isVideo` or a custom key used by an `itemData` filter).
 */
fun SlideData.extra(name: String, value: JsAny) {
    Reflect.set(this, name, value)
}
