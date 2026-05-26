package eggcatcher;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class BitmapFont {

    private record Glyph(int x, int y, int w, int h, int xoff, int yoff, int xadv) {}

    private final BufferedImage sheet;
    private final Map<Integer, Glyph> glyphs = new HashMap<>();
    private final int scale;

    public BitmapFont(String fntPath, String pngPath, int scale) throws IOException {
        this.scale = scale;

        try (InputStream is = getResourceStream(pngPath)) {
            sheet = ImageIO.read(is);
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(getResourceStream(fntPath)))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("char ")) continue;
                int id  = parseIntAttr(line, "id");
                int x   = parseIntAttr(line, "x");
                int y   = parseIntAttr(line, "y");
                int w   = parseIntAttr(line, "width");
                int h   = parseIntAttr(line, "height");
                int xo  = parseIntAttr(line, "xoffset");
                int yo  = parseIntAttr(line, "yoffset");
                int xa  = parseIntAttr(line, "xadvance");
                glyphs.put(id, new Glyph(x, y, w, h, xo, yo, xa));
            }
        }
    }


    public void draw(Graphics2D g, String text, int worldX, int worldY) {
        int cx = worldX;
        for (char ch : text.toCharArray()) {
            Glyph gl = glyphs.get((int) ch);
            if (gl == null) { cx += 6 * scale; continue; }
            if (gl.w() > 0 && gl.h() > 0) {
                g.drawImage(sheet,
                        cx + gl.xoff() * scale,         
                        worldY + gl.yoff() * scale + 2, 
                        cx + gl.xoff() * scale + gl.w() * scale,
                        worldY + gl.yoff() * scale + 2 + gl.h() * scale,
                        gl.x(), gl.y(),                
                        gl.x() + gl.w(), gl.y() + gl.h(),
                        null);
            }
            cx += gl.xadv() * scale;
        }
    }
    public int measure(String text) {
        int w = 0;
        for (char ch : text.toCharArray()) {
            Glyph gl = glyphs.get((int) ch);
            w += (gl != null) ? gl.xadv() * scale : 6 * scale;
        }
        return w;
    }

    private static int parseIntAttr(String line, String key) {
        int idx = line.indexOf(key + "=");
        if (idx < 0) return 0;
        int start = idx + key.length() + 1;
        int end = start;
        if (end < line.length() && line.charAt(end) == '-') end++;
        while (end < line.length() && Character.isDigit(line.charAt(end))) end++;
        try { return Integer.parseInt(line.substring(start, end)); } catch (NumberFormatException e) { return 0; }
    }

    private static InputStream getResourceStream(String path) throws IOException {
        InputStream is = BitmapFont.class.getResourceAsStream("/" + path);
        if (is == null) is = BitmapFont.class.getResourceAsStream(path);
        if (is == null) is = new FileInputStream(path);
        return is;
    }
}
