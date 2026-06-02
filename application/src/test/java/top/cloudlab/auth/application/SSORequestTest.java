package top.cloudlab.auth.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.application.dto.command.SSORequest;
import top.cloudlab.auth.application.dto.command.SSOSignatureRequest;
import top.cloudlab.auth.domain.user.AccessSecret;

class SSORequestTest {

// 测试专用凭据，仅用于单元测试，不应与生产环境相同
  	private static final String TEST_CLIENT_ID = "oMCw9-zhJ6AmdJ1mYjmbXLvyv-VOpA1x7S7jBUSeSxc";
  	// 测试专用凭据，仅用于单元测试，不应与生产环境相同
  	private static final String TEST_SK = "d3hl2P-CVKgkXEkx58_Y5X7yIqnQU1eaeFBoPFWQ74n72Jhmpze4ZgTuABuF9qyj2iVAhpod7QhpiTcyM1Q_VA";
	private static final String TEST_USER_ID = "16645192323286622080";
	// private static final String TEST_LAB_CODE = "yixue";
	private static final String TEST_LAB_CODE = "shipinvedio";
	private static final String TEST_NICKNAME = "测试001";
	private static final String TEST_AVATAR = "";
	private static final String TEST_PHONE = "";

	@Test
	void decodeParamsSuccess() {
		String signString = "avatar=https://example.com/avatar.png&labCode=lab-001&nickname=test-user&phone=13800138000&userId=user-123";
		String paramsEncoded = Base64.getUrlEncoder().encodeToString(signString.getBytes());

		SSORequest request = SSORequest.builder()
				.clientId("client-1")
				.params(paramsEncoded)
				.sign("signature-value")
				.build();

		assertNotNull(request);
		assertEquals("client-1", request.getClientId());
		assertEquals("signature-value", request.getSign());
		assertNotNull(request.getParams());
		
		SSOSignatureRequest params = request.decodeParamsToObject();
		assertEquals("lab-001", params.getLabCode());
		assertEquals("user-123", params.getUserId());
		assertEquals("test-user", params.getNickname());
		assertEquals("https://example.com/avatar.png", params.getAvatar());
		assertEquals("13800138000", params.getPhone());
	}

	@Test
	void decodeParamsToStringSuccess() {
		String signString = "avatar=https://example.com/avatar.png&labCode=lab-001&nickname=test-user";
		String paramsEncoded = Base64.getUrlEncoder().encodeToString(signString.getBytes());

		SSORequest request = SSORequest.builder()
				.clientId("client-1")
				.params(paramsEncoded)
				.build();

		assertEquals(signString, request.decodeParamsToString());
	}

	@Test
	void decodeParamsFailedWhenInvalidBase64() {
		SSORequest request = SSORequest.builder()
				.clientId("client-1")
				.params("not-valid-base64!!!")
				.build();

		assertThrows(RuntimeException.class,
				() -> request.decodeParamsToString(),
				"非法的 Base64 字符串应该抛出异常");
	}

	@Test
	void signAndVerifyWithTestData() {
		SSOSignatureRequest params = new SSOSignatureRequest();
		params.setLabCode(TEST_LAB_CODE);
		params.setUserId(TEST_USER_ID);
		params.setNickname(TEST_NICKNAME);
		params.setAvatar(TEST_AVATAR);
		params.setPhone(TEST_PHONE);

		String toSignString = params.toSignatureString();
		String paramsEncoded = Base64.getUrlEncoder().encodeToString(toSignString.getBytes());
		System.out.println("签名字符串: " + toSignString);
		System.out.println("params Base64: " + paramsEncoded);

		AccessSecret secret = AccessSecret.builder()
				.userId(TEST_USER_ID)
				.ak(TEST_CLIENT_ID)
				.sk(TEST_SK)
				.build();

		String sign = secret.sign(toSignString);
		System.out.println("签名结果: " + sign);
		System.out.println("签名长度: " + sign.length());

		assertTrue(secret.verify(toSignString, sign));
		assertEquals(44, sign.length());

		assertFalse(secret.verify(toSignString, "wrong_sign"));
		
		SSORequest request = SSORequest.builder()
				.clientId(TEST_CLIENT_ID)
				.params(paramsEncoded)
				.sign(sign)
				.build();
		
		assertEquals(toSignString, request.decodeParamsToString());
		assertTrue(secret.verify(request.decodeParamsToString(), request.getSign()));
	}

	@Test
	void signatureAndURL() {
		SSOSignatureRequest params = new SSOSignatureRequest();
		params.setLabCode(TEST_LAB_CODE);
		params.setUserId(TEST_USER_ID);
		params.setNickname(TEST_NICKNAME);
		params.setAvatar(TEST_AVATAR);
		// params.setRole(Role.teacher);

		String toSignString = params.toSignatureString();
		String paramsEncoded = Base64.getUrlEncoder().encodeToString(toSignString.getBytes());
		System.out.println("签名字符串: " + toSignString);
		System.out.println("params Base64: " + paramsEncoded);

		AccessSecret secret = AccessSecret.builder()
				.userId(TEST_USER_ID)
				.ak(TEST_CLIENT_ID)
				.sk(TEST_SK)
				.build();

		String sign = secret.sign(toSignString);
		System.out.println("签名结果: " + sign);

		String signEncoded = URLEncoder.encode(sign, StandardCharsets.UTF_8);
		System.out.println("签名 URL 编码: " + signEncoded);

		SSORequest request = SSORequest.builder()
				.clientId(TEST_CLIENT_ID)
				.params(paramsEncoded)
				.sign(sign)
				.redirectUri("https://" + TEST_LAB_CODE + ".test.example.com/os")
				.build();

		System.out.println(
			String.format("https://gateway.example.com/api/v2/auth/sso/login?clientId=%s&params=%s&sign=%s&redirectUri=%s",
				URLEncoder.encode(request.getClientId(), StandardCharsets.UTF_8),
				request.getParams(),
				signEncoded,
				URLEncoder.encode(request.getRedirectUri(), StandardCharsets.UTF_8)
			)
		);
	}

}
