package eggcatcher;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SpriteAtlas {

    private final Map<String, BufferedImage> regions = new HashMap<>();

    public SpriteAtlas(String atlasPath, String imagePath) throws IOException {
        // Load the PNG sheet
        BufferedImage sheet;
        try (InputStream is = getResourceStream(imagePath)) {
            sheet = ImageIO.read(is);
        }
        int sheetH = sheet.getHeight();

        // Parse the atlas file
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(getResourceStream(atlasPath)))) {

            String line;
            String currentName = null;
            int rx = 0, ry = 0, rw = 0, rh = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Lines with a colon are key:value pairs
                if (line.contains(":")) {
                    String key = line.substring(0, line.indexOf(':')).trim();
                    String val = line.substring(line.indexOf(':') + 1).trim();

                    switch (key) {
                        case "xy" -> {
                            String[] parts = val.split(",");
                            rx = Integer.parseInt(parts[0].trim());
                            ry = Integer.parseInt(parts[1].trim());
                        }
                        case "size" -> {
                            String[] parts = val.split(",");
                            rw = Integer.parseInt(parts[0].trim());
                            rh = Integer.parseInt(parts[1].trim());
                        }
                        case "index" -> {
                            if (currentName != null) {
                                BufferedImage region = sheet.getSubimage(rx, ry, rw, rh);
                                regions.put(currentName, region);
                            }
                        }
                    }
                } else if (!line.startsWith("size") && !line.startsWith("format")
                        && !line.startsWith("filter") && !line.startsWith("repeat")
                        && !line.endsWith(".png")) {
                    // It's a sprite name
                    currentName = line;
                }
            }
        }
    }

    public BufferedImage get(String name) {
        BufferedImage img = regions.get(name);
        if (img == null) {
            throw new IllegalArgumentException("Sprite not found in atlas: " + name);
        }
        return img;
    }

    public BufferedImage getScaled(String name, int scale) {
        BufferedImage src = get(name);
        int w = src.getWidth() * scale;
        int h = src.getHeight() * scale;
        BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        return scaled;
    }

    private static InputStream getResourceStream(String path) throws IOException {
        InputStream is = SpriteAtlas.class.getResourceAsStream("/" + path);
        if (is == null) is = SpriteAtlas.class.getResourceAsStream(path);
        if (is == null) is = new FileInputStream(path);
        return is;
    }
}
