package org.cuckoo.universal.security;

import java.io.Serializable;

/**
 * AuthUser
 *
 * <p>
 *     class SysUser extends AuthUser
 * </p>
 *
 * <p>
 *     SysUser不必要太在意AuthUser中那么多属性，因为AuthUser并不会影响到SysUser的持久化。另外向上转型的时候，AuthUser中还可以获取到和SysUser相同属性的属性值，因此AuthUser中提供的丰富属性是有好处的
 * </p>
 */
public class AuthUser implements Serializable {

    private String id;
    private String accessToken;
    private String refreshToken;
    private String username;
    private String password;
    private String nickname;
    private String cellphone;
    private String telephone;
    private String email;
    private Integer status;
    private String[] roles = new String[]{};
    private String[] perms = new String[]{};

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String[] getPerms() {
        return perms;
    }

    public void setPerms(String[] perms) {
        this.perms = perms;
    }
}