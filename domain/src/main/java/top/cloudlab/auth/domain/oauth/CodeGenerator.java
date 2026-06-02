package top.cloudlab.auth.domain.oauth;

/**
 * 授权码生成器
 */
public interface CodeGenerator {

    /**
     * 生成授权码
     * 
     * @param clientId
     * @param userId
     * @return
     */
    String generate(String clientId, String userId);

}
