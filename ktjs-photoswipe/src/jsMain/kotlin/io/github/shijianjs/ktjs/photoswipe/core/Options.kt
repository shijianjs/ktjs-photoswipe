package io.github.shijianjs.ktjs.photoswipe.core

import js.objects.unsafeJso
import kotlin.js.JsAny
import web.html.HTMLElement

/**
 * Options of the core gallery and the lightbox, upstream `PreparedPhotoSwipeOptions`.
 *
 * Upstream declares every field as required inside `PreparedPhotoSwipeOptions` and exposes it as
 * `Partial<…>`: nothing is mandatory on the caller's side, so every field here is nullable and an
 * unset field keeps the library default.
 */
external interface PhotoSwipeOptions {
    /** The items to show, or the gallery element plus its items. */
    var dataSource: DataSource?

    /** Backdrop opacity, `0.8` by default. */
    var bgOpacity: Double?

    /** Spacing between slides, as a ratio of the viewport width. */
    var spacing: Double?

    /** Whether swiping is allowed while zoomed. */
    var allowPanToNext: Boolean?

    /** Whether the last slide loops to the first one. */
    var loop: Boolean?

    /** Whether the mouse wheel zooms instead of requiring ctrl. */
    var wheelToZoom: Boolean?

    /** Whether a pinch gesture closes the gallery. */
    var pinchToClose: Boolean?

    /** Whether a vertical drag closes the gallery. */
    var closeOnVerticalDrag: Boolean?

    /** Padding of the slide area. */
    var padding: Padding?

    /** A function returning the padding, overrides [padding]. */
    var paddingFn: ((Point, SlideData, Double) -> Padding)?

    /** Hide-transition duration in ms, or `false` to disable it. */
    var hideAnimationDuration: JsAny?

    /** Show-transition duration in ms, or `false` to disable it. */
    var showAnimationDuration: JsAny?

    /** Zoom-transition duration in ms, or `false` to disable it. */
    var zoomAnimationDuration: JsAny?

    /** CSS easing of the transitions. */
    var easing: String?

    /** Whether Escape closes the gallery. */
    var escKey: Boolean?

    /** Whether the arrow keys navigate. */
    var arrowKeys: Boolean?

    /** Whether focus stays inside the gallery while it is open. */
    var trapFocus: Boolean?

    /** Whether focus returns to the previous element after closing. */
    var returnFocus: Boolean?

    /** Whether clicking a non-zoomable image closes it. */
    var clickToCloseNonZoomable: Boolean?

    /** Action on an image click: an [ActionType], a custom [ActionFn] or `false`. */
    var imageClickAction: JsAny?

    /** Action on a backdrop click. */
    var bgClickAction: JsAny?

    /** Action on a tap. */
    var tapAction: JsAny?

    /** Action on a double tap. */
    var doubleTapAction: JsAny?

    /** Delay before the loading indicator appears, in ms. */
    var preloaderDelay: Double?

    /** Separator between the current and total index. */
    var indexIndicatorSep: String?

    /** A function returning the viewport size. */
    var getViewportSizeFn: ((PhotoSwipeOptions, PhotoSwipeBase) -> Point)?

    /** Message shown when an image fails to load. */
    var errorMsg: String?

    /** Preload counts before and after the current slide, as `[before, after]`. */
    var preload: Array<Double>?

    /** Extra class names added to the root element. */
    var mainClass: String?

    /** Element the gallery is appended to. */
    var appendToEl: HTMLElement?

    /** Opening animation is skipped above this rendered width. */
    var maxWidthToAnimate: Double?

    /** Label of the close button. */
    var closeTitle: String?

    /** Label of the zoom button. */
    var zoomTitle: String?

    /** Label of the previous-arrow button. */
    var arrowPrevTitle: String?

    /** Label of the next-arrow button. */
    var arrowNextTitle: String?

    /** `"none"`, `"zoom"` or `"fade"`. */
    var showHideAnimationType: String?

    /** Index of the slide shown first. */
    var index: Double?

    /** A function returning the index of the clicked thumbnail. */
    var getClickedIndexFn: ((JsAny) -> Double)?

    /** Whether the previous arrow is shown. */
    var arrowPrev: Boolean?

    /** Whether the next arrow is shown. */
    var arrowNext: Boolean?

    /** Whether the zoom button is shown. */
    var zoom: Boolean?

    /** Whether the close button is shown. */
    var close: Boolean?

    /** Whether the counter is shown. */
    var counter: Boolean?

    /** SVG of the previous arrow. */
    var arrowPrevSVG: String?

    /** SVG of the next arrow. */
    var arrowNextSVG: String?

    /** SVG of the zoom button. */
    var zoomSVG: String?

    /** SVG of the close button. */
    var closeSVG: String?

    /** SVG of the counter. */
    var counterSVG: String?

    /** Title of the counter. */
    var counterTitle: String?

    /** Initial zoom level, a number, `"fit"` or `"fill"`. */
    var initialZoomLevel: JsAny?

    /** Zoom level of a second tap. */
    var secondaryZoomLevel: JsAny?

    /** Maximum zoom level. */
    var maxZoomLevel: JsAny?

    /** Whether dragging pans the image with the mouse. */
    var mouseMovePan: Boolean?

    /** Opening pointer position, `null` for the element centre. */
    var initialPointerPos: Point?

    /** Whether the backdrop fades instead of the image zooming. */
    var showHideOpacity: Boolean?

    /** Module or promise of the core PhotoSwipe the lightbox loads. */
    var pswpModule: JsAny?

    /** A promise the lightbox awaits before opening. */
    var openPromise: (() -> JsAny)?

    /** Whether the first slide is preloaded. */
    var preloadFirstSlide: Boolean?

    /** Gallery element or elements the lightbox reads. */
    var gallery: ElementProvider?

    /** Selector of the gallery element, alternative to [gallery]. */
    var gallerySelector: String?

    /** Thumbnail elements, alternative to [gallery]. */
    var children: ElementProvider?

    /** Selector of the thumbnail elements. */
    var childSelector: String?

    /** Selector of the thumbnail inside the child, or `false` to disable it. */
    var thumbSelector: JsAny?
}

/** Builds a [PhotoSwipeOptions] object. */
fun photoSwipeOptions(build: PhotoSwipeOptions.() -> Unit): PhotoSwipeOptions = unsafeJso(build)
