package ken5005.kclock;

import java.awt.Color;
import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class Config {

    private static final Path CONFIG_PATH = buildConfigPath();

    private int     windowWidth  = 290;
    private int     windowHeight = 45;
    private Integer windowX      = null;   // null = 未設定 = 初回は中央
    private Integer windowY      = null;
    private int     fontSize     = 27;
    private String  fontFamily   = "Monospaced";
    private Color   textColor    = Color.WHITE;
    private Color   bgColor      = Color.BLACK;

    private static Path buildConfigPath() {
        String appdata = System.getenv("APPDATA");
        String base = (appdata != null) ? appdata : System.getProperty("user.home");
        return Paths.get(base, "kClock", "config.properties");
    }

    public void load() {
        if (!Files.exists(CONFIG_PATH)) return;
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(CONFIG_PATH)) {
            p.load(in);
        } catch (IOException e) {
            return;
        }
        windowWidth  = parseInt(p,  "window.width",  windowWidth);
        windowHeight = parseInt(p,  "window.height", windowHeight);
        windowX      = parseIntOrNull(p, "window.x");
        windowY      = parseIntOrNull(p, "window.y");
        fontSize     = parseInt(p,  "font.size",     fontSize);
        fontFamily   = p.getProperty("font.family",  fontFamily);
        textColor    = parseColor(p, "text.color",   textColor);
        bgColor      = parseColor(p, "bg.color",     bgColor);
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
        } catch (IOException e) {
            return;
        }
        Properties p = new Properties();
        p.setProperty("window.width",  String.valueOf(windowWidth));
        p.setProperty("window.height", String.valueOf(windowHeight));
        if (windowX != null) p.setProperty("window.x", String.valueOf(windowX));
        if (windowY != null) p.setProperty("window.y", String.valueOf(windowY));
        p.setProperty("font.size",   String.valueOf(fontSize));
        p.setProperty("font.family", fontFamily);
        p.setProperty("text.color",  toHex(textColor));
        p.setProperty("bg.color",    toHex(bgColor));
        try (OutputStream out = Files.newOutputStream(CONFIG_PATH)) {
            p.store(out, "kClock configuration");
        } catch (IOException ignored) {}
    }

    // ---------- helpers ----------

    private static int parseInt(Properties p, String key, int def) {
        try { return Integer.parseInt(p.getProperty(key, "")); }
        catch (NumberFormatException e) { return def; }
    }

    private static Integer parseIntOrNull(Properties p, String key) {
        String v = p.getProperty(key);
        if (v == null) return null;
        try { return Integer.parseInt(v); }
        catch (NumberFormatException e) { return null; }
    }

    private static Color parseColor(Properties p, String key, Color def) {
        String v = p.getProperty(key);
        if (v == null) return def;
        try { return Color.decode(v); }
        catch (NumberFormatException e) { return def; }
    }

    private static String toHex(Color c) {
        return String.format("#%06X", c.getRGB() & 0xFFFFFF);
    }

    // ---------- getters / setters ----------

    public int     getWindowWidth()        { return windowWidth; }
    public void    setWindowWidth(int w)   { windowWidth = w; }
    public int     getWindowHeight()       { return windowHeight; }
    public void    setWindowHeight(int h)  { windowHeight = h; }
    public Integer getWindowX()            { return windowX; }
    public void    setWindowX(Integer x)   { windowX = x; }
    public Integer getWindowY()            { return windowY; }
    public void    setWindowY(Integer y)   { windowY = y; }
    public int     getFontSize()           { return fontSize; }
    public void    setFontSize(int s)      { fontSize = s; }
    public String  getFontFamily()         { return fontFamily; }
    public void    setFontFamily(String f) { fontFamily = f; }
    public Color   getTextColor()          { return textColor; }
    public void    setTextColor(Color c)   { textColor = c; }
    public Color   getBgColor()            { return bgColor; }
    public void    setBgColor(Color c)     { bgColor = c; }
}
