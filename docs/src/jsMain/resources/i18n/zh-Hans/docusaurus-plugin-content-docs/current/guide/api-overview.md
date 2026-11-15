---
sidebar_position: 3
title: API 概览
---

# API 概览

## 包结构

| 包 | 内容 |
| --- | --- |
| `io.github.shijianjs.ktjs.photoswipe` | `PhotoSwipeModule`、`PhotoSwipeLightboxModule`、`createPhotoSwipe`、`createPhotoSwipeLightbox`、`photoSwipeModuleLoader`、`PHOTOSWIPE_STYLESHEET` |
| `…photoswipe.core` | `PhotoSwipeLightbox`、`PhotoSwipeCore`、`PhotoSwipeBase`、`PhotoSwipeOptions`、`photoSwipeOptions` |
| `…photoswipe.core` | `SlideData`、`slideData`、`DataSource`、`ElementProvider`、`ActionType`、`ActionTypes`、`ActionFn`、`Point`、`Padding`、`Bounds`、`point`、`padding` |

## 本 wrapper 的约定

- 上游两个模块都是**只有 default** 的导出，而 Kotlin/JS 会把 `@JsModule` + `@JsName` 声明编译成默认
  导入。wrapper 改为把每个子路径绑定成 external object（`import * as photoswipe from 'photoswipe'`），
  并用带类型的 `js(…)` lambda 构造实例。
- 把值转回 `external class` 类型会让 Kotlin/JS 生成对某个未导出标识符的 `instanceof`
  （运行时报 `PhotoSwipeLightbox is not defined`）；shim 通过声明 lambda 的返回类型而不是强转来避开它。
- `PhotoSwipeCore` 与 `PhotoSwipeLightbox` 重复了上游从 `PhotoSwipeBase` 继承的成员：`external` 类
  不能从 external interface 取声明成员。`PhotoSwipeBase` 仍然保留，用于把共享形状写在一处。
- 联合类型的入参是不透明接口 + 同名工厂：`DataSource(items)` / `DataSource(gallery, items)`、
  `ElementProvider(selector | element | elements)`、`ActionType("zoom-or-close")`。
- 选项用 `unsafeJso` 构造器：未设置的字段保持缺失 —— 这很重要，因为上游对 `dataSource`、`padding`
  区分「缺失」与显式 `null`。

## 有意放宽的类型

| 成员 | Kotlin 类型 | 原因 |
| --- | --- | --- |
| `hideAnimationDuration` / `showAnimationDuration` / `zoomAnimationDuration` | `JsAny?` | 上游是 `number \| false` |
| `imageClickAction` / `bgClickAction` / `tapAction` / `doubleTapAction` | `JsAny?` | 上游是 `ActionType \| ActionFn \| false` |
| `initialZoomLevel` / `secondaryZoomLevel` / `maxZoomLevel` | `JsAny?` | 上游是 `number \| "fit" \| "fill" \| function` |
| `thumbSelector` | `JsAny?` | 上游是 `string \| false` |
| `pswpModule` | `JsAny?` | 上游接受类、模块对象、promise 或 loader |
| 事件监听（`on(name, listener)`） | `(JsAny) -> Unit` | 每个事件携带的增强负载不同 |

每一条都记在 `docs/development/technical-decisions.md`。
