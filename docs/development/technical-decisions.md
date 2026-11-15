# Technical decisions

Decisions taken while wrapping `photoswipe` 5.4.4. Each one is either forced by the upstream
declarations or by a Kotlin/JS limitation; none of them is incidental.

## 1. Both entry points are bound as external objects

`photoswipe` has two sub-paths, and each has a **default export only**:

- `photoswipe` → `export { PhotoSwipe as default }` (`dist/photoswipe.esm.js`)
- `photoswipe/lightbox` → `export { PhotoSwipeLightbox as default }` (`dist/photoswipe-lightbox.esm.js`)

Kotlin/JS compiles an `@JsModule` + `@JsName` *declaration* into a default import. Binding the sub-path
as an `@JsModule` external *object* compiles to `import * as photoswipe from 'photoswipe'` instead,
which is the form that keeps working when a package only exports a default.

## 2. Construction goes through typed `js(…)` lambdas, never through a cast

The first draft read the constructor off the namespace as `JsAny` and did
`.unsafeCast<PhotoSwipeLightbox>()`. Kotlin/JS compiled that into a *checked* cast
(`tmp instanceof PhotoSwipeLightbox ? tmp : THROW_CCE()`), and since no module exports an identifier
called `PhotoSwipeLightbox`, every call threw `ReferenceError: PhotoSwipeLightbox is not defined`.

The shims are now declared as typed function values
(`(JsAny, PhotoSwipeOptions?) -> PhotoSwipeCore`), so the JavaScript body is trusted and no `instanceof`
is emitted. The test suite catches a regression here immediately: the very first call would throw.

## 3. `null` options mean “call the constructor with no argument”

Both upstream constructors take `options?` and treat “no argument” differently from `undefined` in a
few places (falsy checks). The shims therefore emit `options === null ? new constructor() : new
constructor(options)`, and the Kotlin factories take `PhotoSwipeOptions?` so a caller can ask for the
defaults explicitly.

## 4. The shared layer is repeated, not inherited

Upstream is `PhotoSwipeLightbox extends PhotoSwipeBase`, `PhotoSwipe extends PhotoSwipeBase extends
Eventable`. An `external class` cannot take declared members from an external interface: the compiler
rejects it as “not abstract and does not implement abstract members”. `PhotoSwipeBase` is therefore
declared for documentation (a function can accept it when it only registers listeners), and
`PhotoSwipeCore` / `PhotoSwipeLightbox` repeat the six shared members — the same JavaScript methods.

## 5. The lightbox needs the core module, and the wrapper hands it over

The lightbox module does **not** bundle the core gallery. `preload()` throws
`pswpModule is not valid` unless `pswpModule` is a class, a module object, a promise or a loader
function. `photoSwipeModuleLoader()` returns the loader form (`() => Promise.resolve(module)`), which
is what upstream documents for ES modules and keeps the core out of the first paint.

## 6. `dataSource` and `gallery`/`children` are two different things

`getNumItems()` and `getItemData()` read `options.dataSource` (`{ gallery, items? }` or an array);
`gallery` + `childSelector` only drive the *click binding* in `init()`. The demo and the tests set
`dataSource = DataSource(gallery, null)` so the item list is available before the first click —
getting this wrong silently yields `0` items, which is exactly what the first test run showed.

## 7. Options are an `unsafeJso` builder

`photoSwipeOptions { … }` leaves every unset field absent. That distinction matters: upstream checks
`'dataSource' in dataSource` and falls back to DOM parsing only when the object form is present, and
`padding` is read as “use the default when undefined”.

## 8. Deliberately loose types

| Member | Kotlin type | Reason |
| --- | --- | --- |
| `hideAnimationDuration`, `showAnimationDuration`, `zoomAnimationDuration` | `JsAny?` | upstream is `number \| false` |
| `imageClickAction`, `bgClickAction`, `tapAction`, `doubleTapAction` | `JsAny?` | upstream is `ActionType \| ActionFn \| false` |
| `initialZoomLevel`, `secondaryZoomLevel`, `maxZoomLevel` | `JsAny?` | upstream is `number \| "fit" \| "fill" \| function` |
| `thumbSelector` | `JsAny?` | upstream is `string \| false` |
| `preload` | `Array<Double>?` | upstream is the tuple `[number, number]`, a plain array in JavaScript |
| `pswpModule` | `JsAny?` | four acceptable shapes, see decision 5 |
| `on` / `off` listeners | `(JsAny) -> Unit` | each event has a different augmented payload |
| `applyFilters` | `vararg JsAny` | upstream is generic over the filter's parameters |

Every row is a boundary of the upstream `any`/union, not a shortcut.

## 9. `SlideData` keeps the index-signature half explicit

`SlideData` is `_SlideData & Record<string, any>` upstream. The declared fields are typed, and the
extra keys go through `SlideData.extra(name, value)`, a one-line assignment shim — the same rule the
`ktjs-type-mapping` skill states for index signatures.

## 10. Tests run in Chromium against the real package

The jsTest suite builds real galleries out of anchors, then reads `getNumItems()`, `getItemData()`
and a filtered `alt` back. A wrapper that binds the wrong module or drops the constructor shim fails
those assertions instead of compiling quietly.

## 11. The demo uses inline SVG data URIs

The demo has to open a lightbox without touching the network: each slide is a `data:image/svg+xml`
URI whose width and height are mirrored into `data-pswp-width` / `data-pswp-height`, so
`getItemData()` parses real dimensions and the Playwright suite never waits on an image request.

## 12. Styles come from the npm package, not from a CDN

`photoSwipeStyles` copies `photoswipe/dist/photoswipe.css` out of the npm workspace
(`build/js/node_modules`) into the demo resources, so the page renders identically in the browser
tests. It runs alongside `kotlinNpmInstall`, because that is what populates the workspace.

## 13. The lock file is regenerated against the official registry

`kotlin-js-store/package-lock.json` is committed, so CI (which has no mirror) resolves exactly what a
local build resolves. Adding `photoswipe` required `gradlew --stop` followed by
`kotlinUpgradePackageLock` with `npm_config_registry=https://registry.npmjs.org`.

## 14. Localised documentation lives directly under `current/`

Docusaurus resolves a translated doc by its **doc id**, so
`i18n/zh-Hans/docusaurus-plugin-content-docs/current/guide/api-overview.md` is the only correct
location for `guide/api-overview`; an extra `docs/` segment silently falls back to the English page.
