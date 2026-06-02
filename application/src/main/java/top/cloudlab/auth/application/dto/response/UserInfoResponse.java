package top.cloudlab.auth.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.cloudlab.auth.domain.user.UserInfo;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "用户手机号")
    private String phone;

    public static UserInfoResponse convert(UserInfo info) {
        return UserInfoResponse.builder()
                .userId(info.getUserId())
                .nickname(info.getNickname())
                .avatar(info.getAvatar())
                .phone(info.getPhone())
                .build();
    }

}
