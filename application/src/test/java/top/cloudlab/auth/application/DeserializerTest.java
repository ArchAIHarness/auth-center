package top.cloudlab.auth.application;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import top.cloudlab.auth.application.dto.command.AbstractOwnerAccessTokenRequest;
import top.cloudlab.auth.application.dto.command.AbstractOAuthAccessTokenRequest;
import top.cloudlab.auth.application.dto.command.CreateAccessTokenRequest;
import top.cloudlab.auth.application.dto.command.CreateOAuthTokenRequest;
import top.cloudlab.auth.application.dto.command.RefreshOAuthTokenRequest;
import top.cloudlab.auth.application.dto.command.RefreshAccessTokenRequest;

/**
 * 反序列化单元测试
 */
class DeserializerTest {

	ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void deserializeSuccess() throws JsonMappingException, JsonProcessingException {
		String akskJson = "{\"id\":\"AK123456\",\"secret\":\"SK789012\"}";
		AbstractOwnerAccessTokenRequest command = objectMapper.readValue(akskJson,
				AbstractOwnerAccessTokenRequest.class);
		System.out.println("请求类型：" + command.getClass().getSimpleName()); // 输出 CreateOnwerTokenByAksk
		assertInstanceOf(CreateAccessTokenRequest.class, command);

		String refreshJson = "{\"refreshToken\":\"RT567890\"}";
		RefreshAccessTokenRequest refreshCommand = objectMapper.readValue(refreshJson,
				RefreshAccessTokenRequest.class);
		System.out.println("请求类型：" + refreshCommand.getClass().getSimpleName()); // 输出 RefreshOwnerTokenRequest
		assertInstanceOf(RefreshAccessTokenRequest.class, refreshCommand);

		String oauthCodeJson = "{\"grantType\":\"authorization_code\",\"clientId\":\"client-1\",\"secret\":\"secret-1\",\"code\":\"CODE123\"}";
		AbstractOAuthAccessTokenRequest oauthCodeRequest = objectMapper.readValue(oauthCodeJson,
				AbstractOAuthAccessTokenRequest.class);
		System.out.println("请求类型：" + oauthCodeRequest.getClass().getSimpleName()); // 输出 CreateOAuthTokenRequest
		assertInstanceOf(CreateOAuthTokenRequest.class, oauthCodeRequest);

		String oauthRefreshJson = "{\"grantType\":\"refresh_token\",\"clientId\":\"client-1\",\"secret\":\"secret-1\",\"refreshToken\":\"RT123\"}";
		AbstractOAuthAccessTokenRequest oauthRefreshRequest = objectMapper.readValue(oauthRefreshJson,
				AbstractOAuthAccessTokenRequest.class);
		System.out.println("请求类型：" + oauthRefreshRequest.getClass().getSimpleName()); // 输出 RefreshOAuthTokenRequest
		assertInstanceOf(RefreshOAuthTokenRequest.class, oauthRefreshRequest);
	}

	@Test
	void deserializeFailedWhenMissingField() {
		String missingAll = "{}";
		assertThrows(IllegalArgumentException.class,
				() -> objectMapper.readValue(missingAll, AbstractOwnerAccessTokenRequest.class),
				"缺少所有字段时不应该成功反序列化");

		String missingIdJson = "{\"secret\":\"SK789012\"}";
		assertThrows(IllegalArgumentException.class,
				() -> objectMapper.readValue(missingIdJson, AbstractOwnerAccessTokenRequest.class),
				"缺少 id 时不应该成功反序列化");

		String missingSecretJson = "{\"id\":\"AK123456\"}";
		assertThrows(IllegalArgumentException.class,
				() -> objectMapper.readValue(missingSecretJson, AbstractOwnerAccessTokenRequest.class),
				"缺少 secret 时不应该成功反序列化");

		String missingRefreshTokenJson = "{\"scope\":\"AK123456\"}";
		assertThrows(IllegalArgumentException.class,
				() -> objectMapper.readValue(missingRefreshTokenJson,
						AbstractOwnerAccessTokenRequest.class),
				"缺少 refreshToken 时不应该成功反序列化");

		String oauthMissingGrantTypeJson = "{\"clientId\":\"client-1\",\"clientSecret\":\"secret-1\"}";
		assertThrows(JsonMappingException.class,
				() -> objectMapper.readValue(oauthMissingGrantTypeJson,
						AbstractOAuthAccessTokenRequest.class),
				"缺少 grantType 时不应该成功反序列化");

		String oauthInvalidGrantTypeJson = "{\"grantType\":\"invalid\",\"clientId\":\"client-1\",\"clientSecret\":\"secret-1\"}";
		assertThrows(JsonMappingException.class,
				() -> objectMapper.readValue(oauthInvalidGrantTypeJson,
						AbstractOAuthAccessTokenRequest.class),
				"grantType 非法时不应该成功反序列化");

	}

}
