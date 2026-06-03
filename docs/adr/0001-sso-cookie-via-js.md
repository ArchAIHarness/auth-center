# ADR 0001：SSO Cookie 写入必须用 JS `document.cookie`，禁止 `response.addCookie()`

| 项 | 内容 |
|---|---|
| 状态 | **强制 [P0]** |
| 决策日期 | 2026-05-25（tag `v26052501`） |
| 复发日期 | 2026-06-02（被改回 `addCookie()` 又触发 400，再次还原） |
| 影响范围 | `SSOController#login` 及所有跨 `*.example.com` 子域 cookie 下发场景 |
| 决策人 | ArchAIHarness Maintainers |

---

## 背景

SSO 登录需把 `token / userId / partner / sign / answerToken / answerUser` 6 个 Cookie 下发给浏览器，并作用于所有 `*.example.com` 子域（前端域名可分布在多个独立子域）。

## 问题

历史上至少两次有人把实现改回 Servlet 的 `response.addCookie(Cookie)` + `cookie.setDomain(".example.com")`，结果在线上立刻报：

```json
{"code":400,"message":"An invalid domain [.example.com] was specified for this cookie","success":false}
```

---

## 根因

Tomcat 9.0.58+ 默认启用 `Rfc6265CookieProcessor`，严格按 RFC6265 校验 `Set-Cookie` 头：

- 不允许 `Domain` 以 `.` 开头（`.example.com` 直接拒绝）
- 不允许设置「公共后缀」相关的 domain

但前端跨多个子域必须共享同一份认证 Cookie，而老的 Set-Cookie 语法 `domain=.example.com`（带前导点）**仍然是浏览器最广泛支持的写法**，比 `example.com`（无前导点）兼容性更好——尤其是某些老版本 webkit 处理子域回退时。

## 决策

**SSO Cookie 必须通过 JavaScript `document.cookie` 写入，禁止使用 Servlet Cookie API。**

```java
// ✅ 正确：HTML + JS
String html = "<html><head><script type='text/javascript'>" +
        "document.cookie = 'token=...; domain=.example.com; path=/; SameSite=None; secure; max-age=...; ';" +
        "setTimeout(function(){ window.location.href='%s'; }, 1000);" +
        "</script></head></html>";
response.getWriter().write(String.format(html, redirectUri));
```

```java
// ❌ 禁止：服务端 Set-Cookie
Cookie cookie = new Cookie(name, value);
cookie.setDomain(".example.com");   // ← Tomcat 直接 400
response.addCookie(cookie);
```

## 理由

| 维度 | JS `document.cookie` | Servlet `addCookie()` |
|---|---|---|
| `.example.com` 前导点 | ✅ 浏览器接受 | ❌ Tomcat 拒绝 400 |
| HttpOnly | ❌ 不支持（JS 设的 cookie 必然非 HttpOnly） | ✅ 支持 |
| 跨子域共享 | ✅ 浏览器按 cookie domain 自动覆盖 | ✅ 同 |
| 调试可见 | ✅ HTML 源码可见 | ✅ Set-Cookie 头可见 |

权衡：**牺牲 HttpOnly 换取跨子域兼容性**。因为接入方前端 JS 需主动读 cookie（如读 `userId`、`partner`、`sign` 用于 API 调用签名），无论如何 HttpOnly 都用不了。

## 后果

- ✅ `*.example.com` 所有子域可读到登录 Cookie
- ✅ 通过 RFC6265 校验
- ⚠️ Cookie 非 HttpOnly，可被页面 JS 读取（接入方约束）
- ⚠️ 用户禁用 JavaScript 时 SSO 不可用（接入方约束）

## 红线规则（违反 = P0）

1. **`SSOController.java` 中禁止 `import jakarta.servlet.http.Cookie;`**
2. **禁止任何 `response.addCookie(...)` / `new Cookie(...)` 调用**
3. **`@Value("${sso.cookie.domain:.example.com}")` 默认值保留前导点**
4. **修改 SSO Cookie 写入方式前必须先看本 ADR**

## 关联代码

- `interfaces/src/main/java/top/cloudlab/auth/interfaces/controller/SSOController.java`（类注释引用本 ADR）
- 历史 tag：`v26052501`（首次确立此方案）

## 复发预防

- `SSOController` 类注释顶部加 ADR 引用
- `AGENTS.md` 列入 P0 红线区
- 任何想改 SSO Cookie 方式的 PR 必须在描述里引用本 ADR 并说明为什么破坏决策仍可接受
