package top.cloudlab.auth.application.dto.command;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SSOSignatureRequest {

    public static enum Role {

        teacher(10),
        student(0),
        ;

        private final Integer code;

        Role(Integer code) {
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }
    }

    @Schema(description = "角色")
    private Role role;

    @Schema(description = "实验室ID")
    private String labCode;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "手机号")
    private String phone;

    public String toSignatureString() {
        Map<String, String> sortedMap = new TreeMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(this);
                if (value != null) {
                    sortedMap.put(field.getName(), value.toString());
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to read field: " + field.getName(), e);
            }
        }
        return sortedMap.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
    }
    
}
