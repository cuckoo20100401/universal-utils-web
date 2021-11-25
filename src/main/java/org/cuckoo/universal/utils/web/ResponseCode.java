package org.cuckoo.universal.utils.web;

public interface ResponseCode {

    /**
     * login code
     */
    public static final int LOGIN_USERNAME_IS_EMPTY = 400300;
    public static final int LOGIN_PASSWORD_IS_EMPTY = 400301;
    public static final int LOGIN_USERNAME_IS_NOT_EXIST = 400302;
    public static final int LOGIN_PASSWORD_IS_INCORRECT = 400303;
    public static final int LOGIN_USERNAME_IS_DISABLED = 400304;

    /**
     * auth code
     */
    public static final int AUTH_TOKEN_IS_NOT_EXIST = 400400;
    public static final int AUTH_TOKEN_IS_INVALID = 400401;
    public static final int AUTH_TOKEN_IS_EXPIRED = 400402;
    public static final int AUTH_TOKEN_IS_NO_PERMISSION = 400403;
}