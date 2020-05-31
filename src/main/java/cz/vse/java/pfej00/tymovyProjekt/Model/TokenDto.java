package cz.vse.java.pfej00.tymovyProjekt.Model;

public class TokenDto {
    private static final TokenDto TOKEN = new TokenDto();
    private String TOKEN_VALUE;


    /**
     * Konstruktor pro singleton, představující TOKEN,
     * který uživatel získá, pokud přijde přes LOGIN obrazovku.
     * K veškerým dalším voláním je potřeba TOKEN přidat do hlavičky
     */
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
