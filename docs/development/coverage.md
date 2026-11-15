# API Coverage

## 来源与范围

- 来源：`photoswipe` 5.4.4（npm），源码克隆 `S:\workspace\github\dimsemenov\photoswipe`
- 声明文件：`dist/types/{photoswipe,lightbox/lightbox,core/{base,eventable},slide/slide,ui/ui-element,main-scroll}.d.ts`
- 核对日期：2026-11-16
- 范围：`photoswipe` 的两个入口（`photoswipe` 与 `photoswipe/lightbox`）对使用者可见的 API ——
  两个类、它们继承的事件/过滤器层、选项、数据类型与常量。
- 不纳入统计：`util/*`、`gestures/*`、`slide/*` 的内部实现模块；它们不是包的入口导出。

## 统计

| 状态 | 数量 |
|---|---:|
| 总计 | 24 |
| 已完成 | 10 |
| 明确不做 | 2 |
| 待完成 | 12 |

已完成组（不逐项列名，实现见源码）：

1. 两个模块的外部绑定与构造（`PhotoSwipe.kt` 的 `PhotoSwipeModule` / `PhotoSwipeLightboxModule` 与
   `constructPhotoSwipe*`）
2. `createPhotoSwipe` / `createPhotoSwipeLightbox` 工厂
3. `photoSwipeModuleLoader()` 与 `PHOTOSWIPE_STYLESHEET` 常量
4. `PhotoSwipeOptions`（约 60 个字段）与 `photoSwipeOptions` 构造器（`core/Options.kt`）
5. `PhotoSwipeLightbox` 的方法面（`init`、`getClickedIndex`、`loadAndOpen`、`preload`、`destroy`、`pswp`）
6. `PhotoSwipeCore` 的导航与缩放面（`goTo`、`next`、`prev`、`zoomTo`、`toggleZoom`、`close`、`destroy`、
   `refreshSlideContent`、`getViewportCenterPoint`、`updateSize`）
7. 事件/过滤器层（`on`、`off`、`addFilter`、`removeFilter`、`applyFilters`、`getNumItems`、`getItemData`）
8. `SlideData` / `DataSource` / `ElementProvider` 与对应工厂（`core/Types.kt`）
9. `Point` / `Padding` / `Bounds` 与 `point` / `padding` 构造器
10. `ActionType` / `ActionTypes` / `ActionFn`

## 明确不做

| 名称 | 排除理由 |
|---|---|
| `util/*`（`animations`、`css-animation`、`dom-events`、`spring-animation`、`spring-easer`、`util`、`viewport-size`） | 内部工具模块，不在 `package.json` 的 `exports` 里，外部无法 import |
| `core/eventable` 的 `_listeners` 字段 | 上游以下划线标记为私有实现细节，事件应通过 `on`/`off` 注册 |

## 待完成

### UI 与过滤器

- [ ] 自定义 UI 元素（`uiRegister` / `uiElementCreate` 的类型化负载）
- [ ] 过滤器类型（`PhotoSwipeFiltersMap` 逐个签名）
- [ ] `UIElementData` 与 `counterIndicator` / `buttonArrow` / `buttonClose` / `buttonZoom`

### 内部句柄

- [ ] `Slide` 与 `Content` 句柄（`contentInit`、`contentLoad` 的 `content` 参数）
- [ ] `MainScroll` 与 `ItemHolder`（`setContent` 的 holder 参数）
- [ ] `Gestures`、`ZoomHandler`、`DragHandler` 的公开成员
- [ ] `Keyboard`、`ScrollWheel`、`Opener` 子模块
- [ ] `Animations` 与 `SpringAnimation`

### 选项与内容

- [ ] `mainClass` 主题所需的按需样式说明（`photoSwipe.css` 之外）
- [ ] `openPromise` 与异步内容加载的类型化封装
- [ ] 视频幻灯片（`content` 自定义 + `isVideo`）
- [ ] `getClickedIndexFn` 的回调参数类型（当前是 `(JsAny) -> Double`）
- [ ] 上游升级后的声明对齐

## 验证依据

| 命令 | 结果 |
|---|---|
| `./gradlew :ktjs-photoswipe:compileKotlinJs` | 通过 |
| `./gradlew :ktjs-photoswipe:jsBrowserTest` | 5 个用例全部通过（Chromium） |
| `./gradlew :demo:jsBrowserDistribution` | 通过 |
| `./gradlew :demo:e2eTest` | 通过（Playwright，点击开灯箱、Escape 关闭、语言切换） |
| `./gradlew :docs:docusaurusBuild` | en 与 zh-Hans 均成功 |
