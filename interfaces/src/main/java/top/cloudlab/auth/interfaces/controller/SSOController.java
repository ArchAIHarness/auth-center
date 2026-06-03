package top.cloudlab.auth.interfaces.controller;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import top.cloudlab.auth.interfaces.config.annotation.IgnoreToken;
import top.cloudlab.auth.application.dto.command.SSORequest;
import top.cloudlab.auth.application.dto.response.SSOResponse;
import top.cloudlab.auth.application.service.SSOAppService;

/**
 * 单点登录控制器。
 *
 * <h3>⚠️ 强制约束 [P0] — 禁止改回 response.addCookie()</h3>
 * <p>
 * SSO Cookie 必须通过 JavaScript {@code document.cookie} 写入，
 * 禁止使用 Servlet {@link jakarta.servlet.http.Cookie} + {@code response.addCookie()}。
 *
 * <p>原因：Tomcat 9.0.58+ 启用 {@code Rfc6265CookieProcessor} 严格校验 cookie domain，
 * 而 {@code .example.com}（前导点）会被直接拒绝并返回
 * <pre>{"code":400,"message":"An invalid domain [.example.com] was specified for this cookie"}</pre>
 *
 * <p>但前端跨多个 {@code *.example.com} 子域必须共享 cookie，且接入方 JS 需主动读取
 * （HttpOnly 用不了），因此唯一可行方案是 JS {@code document.cookie}。
 *
 * <p>历史上至少 2 次有人改回 {@code addCookie()} 导致线上 SSO 全部 400。
 * 详见 {@code docs/adr/0001-sso-cookie-via-js.md}。
 *
 * @see SSOAppService
 */
@Slf4j
@Tag(name = "单点登录")
@IgnoreToken
@RestController
@RequestMapping("/sso")
public class SSOController {

    private final String domain;
    private final Boolean secure;
    private final String path;
    private final String authorization;
    private final SSOAppService ssoService;

    public SSOController(
            @Value("${sso.cookie.domain:.example.com}") String domain,
            @Value("${sso.cookie.secure:true}") Boolean secure,
            @Value("${sso.cookie.path:/}") String path,
            @Value("${sso.cookie.authorization:token}") String authorization,
            SSOAppService ssoService) {
        this.domain = domain;
        this.secure = secure;
        this.path = path;
        this.authorization = authorization;
        this.ssoService = ssoService;
    }

    @IgnoreToken
    @Operation(summary = "单点登录")
    @GetMapping("/login")
    public void login(SSORequest request, HttpServletResponse response) throws java.io.IOException {
        log.info("sso request = {}", request);
        Optional<SSOResponse> responseOpt = ssoService.login(request);

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("text/html;charset=utf-8");

        String redirectUri = request.getRedirectUri();
        SSOResponse ssoResponse = responseOpt.orElseThrow(() -> new IllegalArgumentException("登录失败"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        long expireTime = Instant.from(formatter.parse(ssoResponse.getExpire())).getEpochSecond();
        long currentTime = Instant.now().getEpochSecond();
        int maxAge = (int) Math.max(0, expireTime - currentTime);

        String token = ssoResponse.getToken() != null ? ssoResponse.getToken() : "";
        String userId = ssoResponse.getUserId() != null ? ssoResponse.getUserId() : "";
        String partner = ssoResponse.getPartner() != null ? ssoResponse.getPartner() : "";
        String sign = ssoResponse.getSign() != null ? ssoResponse.getSign() : "";
        String answerToken = ssoResponse.getAnswerToken() != null ? ssoResponse.getAnswerToken() : "";
        String answerUser = ssoResponse.getAnswerUser() != null ? ssoResponse.getAnswerUser() : "";

        StringBuilder cookieJs = new StringBuilder();
        cookieJs.append("document.cookie = '")
                .append(authorization).append("=").append(token).append("; ")
                .append("userId=").append(userId).append("; ")
                .append("partner=").append(partner).append("; ")
                .append("sign=").append(sign).append("; ")
                .append("answerToken=").append(answerToken).append("; ")
                .append("answerUser=").append(answerUser).append("; ")
                .append("max-age=").append(maxAge).append("; ")
                .append("path=").append(path).append("; ")
                .append("domain=").append(domain).append("; ");
        if (secure) {
            cookieJs.append("secure; ");
        }
        cookieJs.append("SameSite=None; ")
                .append("'");

        String html = "<html><head><script type='text/javascript'>" +
                cookieJs + ";" +
                "setTimeout(function(){" +
                "    window.location.href='%s';" +
                "},1000);</script></head></html>";

        response.getWriter().write(String.format(html, redirectUri));
    }

}
