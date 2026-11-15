package io.github.shijianjs.ktjs.photoswipe

import io.github.shijianjs.ktjs.photoswipe.core.PhotoSwipeCore
import io.github.shijianjs.ktjs.photoswipe.core.PhotoSwipeLightbox
import io.github.shijianjs.ktjs.photoswipe.core.PhotoSwipeOptions
import kotlin.js.JsAny
import kotlin.js.JsModule
import kotlin.js.JsName

/*
 * Entry points of the `photoswipe` 5 package.
 *
 * Both modules of the package have a **default export only** (`dist/photoswipe.esm.js` exports
 * `PhotoSwipe as default`, `dist/photoswipe-lightbox.esm.js` exports `PhotoSwipeLightbox as default`).
 * Kotlin/JS turns an `@JsModule` + `@JsName` *declaration* into a default import. Binding each sub-path
 * as an external object instead compiles to `import * as photoswipe from 'photoswipe'`, and the
 * constructors are read off the namespace.
 *
 * Construction goes through typed `js(...)` lambdas rather than a `JsAny` shim plus `unsafeCast`:
 * a cast back to a class type makes Kotlin/JS emit an `instanceof` against an identifier no module
 * exports, which throws `… is not defined` at run time.
 */

/** The core module, npm `photoswipe`. */
@JsModule("photoswipe")
external object PhotoSwipeModule {
    /** The `PhotoSwipe` constructor, upstream `export { PhotoSwipe as default }`. */
    @JsName("default")
    val default: JsAny
}

/** The lightbox module, npm `photoswipe/lightbox`. */
@JsModule("photoswipe/lightbox")
external object PhotoSwipeLightboxModule {
    /** The `PhotoSwipeLightbox` constructor, upstream `export { PhotoSwipeLightbox as default }`. */
    @JsName("default")
    val default: JsAny
}

/** The stylesheet shipped inside the package; the demo copies it out of `node_modules`. */
const val PHOTOSWIPE_STYLESHEET: String = "photoswipe/dist/photoswipe.css"

/**
 * The `photoswipe/lightbox` module does not bundle the core gallery: it needs the core class in the
 * `pswpModule` option before it can open anything, otherwise `preload()` throws
 * `pswpModule is not valid`.
 *
 * Passing this loader (rather than the class itself) is the form upstream documents for ES modules:
 * a function that resolves the module, so the core is only touched when the lightbox first opens.
 */
fun photoSwipeModuleLoader(): JsAny = moduleLoader(PhotoSwipeModule.default)

/** Plain `new`-less module hand-off used by [photoSwipeModuleLoader]. */
private val moduleLoader: (JsAny) -> JsAny = js("(module) => () => Promise.resolve(module)")

/**
 * Creates a core gallery (no thumbnail handling, no DOM binding) with [options].
 *
 * Most pages want [createPhotoSwipeLightbox] instead: the lightbox binds clicks on thumbnails, reads
 * their sizes and only then opens the core gallery.
 */
fun createPhotoSwipe(options: PhotoSwipeOptions?): PhotoSwipeCore =
    constructPhotoSwipe(PhotoSwipeModule.default, options)

/**
 * Creates the lightbox layer with [options]; call [PhotoSwipeLightbox.init] before using it.
 *
 * `init()` is deliberately not part of the constructor upstream, so filters and event listeners
 * can be registered first — the wrapper keeps that order.
 */
fun createPhotoSwipeLightbox(options: PhotoSwipeOptions?): PhotoSwipeLightbox =
    constructPhotoSwipeLightbox(PhotoSwipeLightboxModule.default, options)

/** `new PhotoSwipe(options)`, with the upstream "no argument" form for a `null` options object. */
private val constructPhotoSwipe: (JsAny, PhotoSwipeOptions?) -> PhotoSwipeCore =
    js("(constructor, options) => (options === null ? new constructor() : new constructor(options))")

/** `new PhotoSwipeLightbox(options)`, with the upstream "no argument" form for a `null` options object. */
private val constructPhotoSwipeLightbox: (JsAny, PhotoSwipeOptions?) -> PhotoSwipeLightbox =
    js("(constructor, options) => (options === null ? new constructor() : new constructor(options))")
