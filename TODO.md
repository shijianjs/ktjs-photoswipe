# TODO

## ktjs-photoswipe 上游 API 对齐

把上游 `photoswipe` 5.4.4-kt.1 的类型声明逐个落成 Kotlin/JS 声明，并让 demo、文档、
测试与之一致。

- 总计：24
- 已完成：10
- 明确不做：2
- 待完成：12

详细清单：[coverage.md](docs/development/coverage.md)
技术决策：[technical-decisions.md](docs/development/technical-decisions.md)

### 当前待办

- [ ] 自定义 UI 元素（`uiRegister` / `uiElementCreate` 事件的类型化负载）
- [ ] 过滤器类型（`PhotoSwipeFiltersMap` 的逐个签收）
- [ ] `Slide` 与 `Content` 句柄（`contentInit`、`contentLoad` 的 `content` 参数）
- [ ] `MainScroll` 与 `ItemHolder`（`setContent` 的 holder 参数）
- [ ] `Gestures`、`ZoomHandler`、`DragHandler` 的公开成员
- [ ] `Keyboard`、`ScrollWheel`、`Opener` 子模块
- [ ] `Animations` 与 `SpringAnimation`
- [ ] `photoSwipe.css` 之外的按需样式（`mainClass` 主题）
- [ ] `openPromise` 与异步内容加载的类型化封装
- [ ] `isVideo` 之外的视频幻灯片（`content` 自定义）
- [ ] `getClickedIndexFn` 的回调参数类型（当前是 `(JsAny) -> Double`）
- [ ] 上游升级后的声明对齐

### 注意事项

- 只封装 MIT 授权的 `photoswipe` 包；没有把任何导出项判为「不在范围内」以外的简化。
- 灯箱必须配 `pswpModule`，这一点写在快速开始与 API 概览里，否则第一次点击就会抛错。
- 有意放宽的类型逐条记在 `technical-decisions.md`，不得悄悄退回 `JsAny`。
