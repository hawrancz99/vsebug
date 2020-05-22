package cz.vse.java.pfej00.tymovyProjekt.Model;

public class TokenDto {
    private static final TokenDto TOKEN = new TokenDto();
    private String TOKEN_VALUE;

    private TokenDto() {
    }

    public static TokenDto getTOKEN() {
        return TOKEN;
    }

    public String getTokenValue() {
        return TOKEN_VALUE;
    }

    public void setTokenValue(String tokenValue) {
        TOKEN_VALUE = tokenValue;
    }
}
