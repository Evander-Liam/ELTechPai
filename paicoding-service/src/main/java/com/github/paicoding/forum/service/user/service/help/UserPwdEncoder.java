package com.github.paicoding.forum.service.user.service.help;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 密码加密器，后续接入SpringSecurity之后，可以使用 PasswordEncoder 进行替换
 *
 * @author YiHui
 * @date 2022/12/5
 */
@Component
public class UserPwdEncoder {
    @Value("${security.salt-index}")
    private Integer saltIndex;

    @Value("${security.pepper}")
    private String PEPPER;

    public boolean match(String plainPwd, String encPwd) {
        return Objects.equals(encPwd(plainPwd), encPwd);
    }

    /**
     * 明文密码处理
     *
     * @param plainPwd
     * @return
     */
    public String encPwd(String plainPwd) {
        String salt = DigestUtils.sha256Hex(plainPwd.getBytes(StandardCharsets.UTF_8));
        if (plainPwd.length() > saltIndex) {
            plainPwd = plainPwd.substring(0, saltIndex) + salt + plainPwd.substring(saltIndex) + PEPPER;
        } else {
            plainPwd = plainPwd + salt + PEPPER;
        }

        return DigestUtils.sha256Hex(plainPwd.getBytes(StandardCharsets.UTF_8));
    }
}