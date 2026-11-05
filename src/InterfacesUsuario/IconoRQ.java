package InterfacesUsuario;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/** Logo de RQ dibujado con Java2D (un bus sobre fondo azul con las letras RQ). */
public final class IconoRQ {
    private IconoRQ() {
    }

    public static BufferedImage crear(int s) {
        BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g.setPaint(new GradientPaint(0, 0, Tema.AZUL_OSCURO, s, s, Tema.CELESTE));
        g.fill(new RoundRectangle2D.Float(0, 0, s, s, s * 0.24f, s * 0.24f));

        float bx = s * 0.13f, by = s * 0.17f, bw = s * 0.74f, bh = s * 0.46f;
        g.setColor(new Color(0, 0, 0, 40));
        g.fill(new RoundRectangle2D.Float(bx + s * 0.012f, by + s * 0.02f, bw, bh, s * 0.12f, s * 0.12f));
        g.setColor(Color.WHITE);
        g.fill(new RoundRectangle2D.Float(bx, by, bw, bh, s * 0.12f, s * 0.12f));

        g.setColor(new Color(0x9CCBEA));
        float vw = bw * 0.17f, vh = bh * 0.34f, vy = by + bh * 0.14f, gap = bw * 0.035f;
        float vx = bx + bw * 0.06f;
        for (int i = 0; i < 4; i++) {
            g.fill(new RoundRectangle2D.Float(vx + i * (vw + gap), vy, vw, vh, s * 0.03f, s * 0.03f));
        }
        g.setColor(new Color(0x7FB8DD));
        g.fill(new RoundRectangle2D.Float(bx + bw * 0.80f, vy, bw * 0.14f, bh * 0.55f, s * 0.03f, s * 0.03f));

        g.setColor(Tema.AMBAR);
        g.fill(new RoundRectangle2D.Float(bx, by + bh * 0.66f, bw, bh * 0.14f, 4, 4));

        g.setColor(Tema.AZUL_OSCURO);
        float r = s * 0.075f, cy = by + bh + s * 0.005f;
        g.fill(new Ellipse2D.Float(bx + bw * 0.16f - r, cy - r, 2 * r, 2 * r));
        g.fill(new Ellipse2D.Float(bx + bw * 0.80f - r, cy - r, 2 * r, 2 * r));
        g.setColor(Color.WHITE);
        float r2 = r * 0.42f;
        g.fill(new Ellipse2D.Float(bx + bw * 0.16f - r2, cy - r2, 2 * r2, 2 * r2));
        g.fill(new Ellipse2D.Float(bx + bw * 0.80f - r2, cy - r2, 2 * r2, 2 * r2));

        g.setStroke(new BasicStroke(Math.max(1f, s * 0.012f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{s * 0.04f, s * 0.04f}, 0f));
        g.setColor(new Color(255, 255, 255, 120));
        g.drawLine((int) (s * 0.13f), (int) (s * 0.80f), (int) (s * 0.87f), (int) (s * 0.80f));

        g.setFont(new Font("SansSerif", Font.BOLD, Math.max(8, (int) (s * 0.19f))));
        FontMetrics fm = g.getFontMetrics();
        String texto = "RQ";
        g.setColor(Tema.AMBAR);
        g.drawString(texto, (s - fm.stringWidth(texto)) / 2f, s * 0.80f + fm.getAscent() * 0.78f);
        g.dispose();
        return img;
    }

    public static ImageIcon icono(int tamano) {
        return new ImageIcon(crear(tamano));
    }
}
