package top.cloudlab.auth.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import top.cloudlab.auth.application.dto.response.AuthCodeResponse;
import top.cloudlab.auth.application.service.impl.OAuthAppServiceImpl;
import top.cloudlab.auth.domain.oauth.AuthCode;
import top.cloudlab.auth.domain.oauth.AuthCodeRepository;
import top.cloudlab.auth.domain.oauth.CodeGenerator;

class OAuthAppServiceTest {

    private CodeGenerator generator;
    private AuthCodeRepository authCodeRepository;
    private OAuthAppServiceImpl oAuthAppService;

    @BeforeEach
    void setUp() {
        generator = mock(CodeGenerator.class);
        authCodeRepository = mock(AuthCodeRepository.class);
        oAuthAppService = new OAuthAppServiceImpl(generator, authCodeRepository);
    }

    @Test
    void testAuthorize() {
        String clientId = "client-123";
        String userId = "user-456";
        
        when(generator.generate(any(), any())).thenReturn("auth-code-123");

        AuthCodeResponse response = oAuthAppService.authorize(clientId, userId);

        assertNotNull(response);
        assertEquals("auth-code-123", response.getCode());
        
        verify(authCodeRepository).revokeAll(clientId, userId);
        
        ArgumentCaptor<AuthCode> captor = ArgumentCaptor.forClass(AuthCode.class);
        verify(authCodeRepository).save(captor.capture());
        assertEquals(clientId, captor.getValue().getClientId());
        assertEquals(userId, captor.getValue().getUserId());
    }
}