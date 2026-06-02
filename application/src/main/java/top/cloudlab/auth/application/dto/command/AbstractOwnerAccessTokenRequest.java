package top.cloudlab.auth.application.dto.command;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * 个人访问凭证请求
 */
@JsonDeserialize(using = AbstractOwnerAccessTokenRequest.OwnerAccessTokenRequestDeserializer.class)
public abstract class AbstractOwnerAccessTokenRequest implements AbstractAccessTokenRequest {

    public static class OwnerAccessTokenRequestDeserializer extends StdDeserializer<AbstractOwnerAccessTokenRequest> {

        public OwnerAccessTokenRequestDeserializer() {
            this(null);
        }

        public OwnerAccessTokenRequestDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public AbstractOwnerAccessTokenRequest deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException {
            JsonNode node = jp.getCodec().readTree(jp);

            if (node.has("refreshToken") || node.has("refresh_token")) {
                RefreshAccessTokenRequest request = new RefreshAccessTokenRequest();
                JsonNode refreshNode = node.get("refreshToken");
                if (refreshNode == null || refreshNode.isNull()) {
                    refreshNode = node.get("refresh_token");
                }
                if (refreshNode != null && !refreshNode.isNull()) {
                    request.setRefreshToken(refreshNode.asText());
                }
                return request;
            }

            if (node.has("id") && node.has("secret")) {
                return parseCreateRequest(node);
            } else if (node.has("ak") && node.has("sk")) {
                return parseCreateRequest(node);
            } else if (node.has("refreshToken")) {
                RefreshAccessTokenRequest request = new RefreshAccessTokenRequest();
                JsonNode refreshTokenNode = node.get("refreshToken");
                if (refreshTokenNode != null && !refreshTokenNode.isNull()) {
                    request.setRefreshToken(refreshTokenNode.asText());
                }
                return request;
            } else {
                throw new IllegalArgumentException("无法识别的请求类型：缺少id/secret或refreshToken字段");
            }
        }

        private CreateAccessTokenRequest parseCreateRequest(JsonNode node) {
            CreateAccessTokenRequest request = new CreateAccessTokenRequest();

            JsonNode idNode = node.get("id");
            if (idNode != null && !idNode.isNull()) {
                request.setAk(idNode.asText());
            }

            JsonNode secretNode = node.get("secret");
            if (secretNode != null && !secretNode.isNull()) {
                request.setSk(secretNode.asText());
            }

            JsonNode akNode = node.get("ak");
            if (akNode != null && !akNode.isNull() && request.getAk() == null) {
                request.setAk(akNode.asText());
            }

            JsonNode skNode = node.get("sk");
            if (skNode != null && !skNode.isNull() && request.getSk() == null) {
                request.setSk(skNode.asText());
            }

            JsonNode tenantIdNode = node.get("tenantId");
            if (tenantIdNode != null && !tenantIdNode.isNull()) {
                request.setTenantId(tenantIdNode.asText());
            } else {
                JsonNode scopeNode = node.get("scope");
                if (scopeNode == null || scopeNode.isNull()) {
                    scopeNode = node.get("scopes");
                    if (scopeNode == null || scopeNode.isNull()) {
                        scopeNode = node.get("tenantIds");
                    }
                }
                if (scopeNode != null && scopeNode.isArray()) {
                    for (JsonNode item : scopeNode) {
                        if (item != null && !item.isNull() && item.isTextual()) {
                            String scopeValue = item.asText();
                            if (!scopeValue.isBlank()) {
                                request.setTenantId(scopeValue);
                                break;
                            }
                        }
                    }
                }
            }

            return request;
        }
    }

}