package InterfacesUsuario;

import control.InicializadorSistema;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingWorker;
import javax.swing.Timer;

/**
 * Pantalla de inicio animada: muestra el logo, explica de qué trata el sistema con frases que rotan y avanza
 * la barra de progreso según el trabajo real de preparación de los datos.
 */
public class SplashRQ extends JWindow {
    private static final int ANCHO = 760;
    private static final int ALTO = 420;
    private static final long DURACION_MINIMA_MS = 4200;
    private static final long CAMBIO_FRASE_MS = 1500;

    private static final String[][] FRASES = {
            {"Reserva tu asiento en buses interprovinciales", "Elige el viaje, escoge tu asiento y viaja sin filas."},
            {"Recarga tu billetera con tarjeta o efectivo", "El administrador aprueba tu recarga y el saldo queda listo para comprar."},
            {"Conductores siempre informados", "Cada conductor ve sus viajes asignados y reporta novedades al administrador."},
            {"Administración completa del transporte", "Empresas, buses, rutas, viajes, reservas y facturas en un solo lugar."},
            {"Tus datos quedan guardados", "Contraseñas protegidas y respaldo automático cada vez que inicias."}
    };

    private final Runnable alTerminar;
    private final BufferedImage logo = IconoRQ.crear(170);
    private final long inicio = System.currentTimeMillis();
    private long inicioFrase = inicio;
    private int frase;
    private float alfaLogo;
    private double progresoMostrado;
    private volatile int progresoObjetivo;
    private volatile String estado = "Iniciando...";
    private Timer animacion;
    private Throwable fallo;

    private final JPanel lienzo = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            dibujar((Graphics2D) g);
        }
    };

    public SplashRQ(Runnable alTerminar) {
        this.alTerminar = alTerminar;
        setSize(ANCHO, ALTO);
        setLocationRelativeTo(null);
        setContentPane(lienzo);
        setIconImage(logo);
        try {
            setShape(new RoundRectangle2D.Float(0, 0, ANCHO, ALTO, 28, 28));
        } catch (UnsupportedOperationException e) {
            // plataformas sin ventanas con forma: se muestra rectangular
        }
    }

    public void iniciar() {
        setVisible(true);
        animacion = new Timer(25, e -> {
            long ahora = System.currentTimeMillis();
            alfaLogo = Math.min(1f, alfaLogo + 0.045f);
            progresoMostrado += (progresoObjetivo - progresoMostrado) * 0.10;
            if (ahora - inicioFrase > CAMBIO_FRASE_MS) {
                frase = (frase + 1) % FRASES.length;
                inicioFrase = ahora;
            }
            lienzo.repaint();
        });
        animacion.start();

        new SwingWorker<Void, Object[]>() {
            @Override
            protected Void doInBackground() {
                try {
                    InicializadorSistema.preparar((p, m) -> publish(new Object[]{p, m}));
                } catch (Throwable t) {
                    fallo = t;
                    return null;
                }
                while (System.currentTimeMillis() - inicio < DURACION_MINIMA_MS) {
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                publish(new Object[]{100, "Todo listo"});
                return null;
            }

            @Override
            protected void process(List<Object[]> trozos) {
                Object[] ultimo = trozos.get(trozos.size() - 1);
                progresoObjetivo = (Integer) ultimo[0];
                estado = (String) ultimo[1];
            }

            @Override
            protected void done() {
                Timer espera = new Timer(30, null);
                espera.addActionListener(ev -> {
                    if (fallo != null || progresoMostrado >= 99.3) {
                        espera.stop();
                        terminar();
                    }
                });
                espera.start();
            }
        }.execute();
    }

    private void terminar() {
        animacion.stop();
        dispose();
        if (fallo != null) {
            JOptionPane.showMessageDialog(null, "No se pudieron preparar los datos del sistema:\n" + fallo, "Error al iniciar", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        alTerminar.run();
    }

    private void dibujar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        long ahora = System.currentTimeMillis();

        g.setPaint(new GradientPaint(0, 0, Tema.AZUL_OSCURO, ANCHO, ALTO, new Color(0x1B6CA8)));
        g.fillRect(0, 0, ANCHO, ALTO);
        g.setColor(new Color(255, 255, 255, 14));
        g.fillOval(-120, -140, 380, 380);
        g.fillOval(ANCHO - 260, ALTO - 220, 420, 420);

        float escala = 0.82f + 0.18f * alfaLogo;
        int lado = (int) (170 * escala);
        Graphics2D gl = (Graphics2D) g.create();
        gl.setComposite(AlphaComposite.SrcOver.derive(alfaLogo));
        gl.drawImage(logo, 52 + (170 - lado) / 2, 92 + (170 - lado) / 2, lado, lado, null);
        gl.dispose();

        int x = 270;
        g.setColor(Color.WHITE);
        g.setFont(Tema.fuente(Font.BOLD, 54f));
        g.drawString("RQ", x, 128);
        g.setColor(Tema.AMBAR);
        g.fillRoundRect(x, 140, 56, 5, 5, 5);
        g.setColor(new Color(0xCFE3F3));
        g.setFont(Tema.fuente(Font.PLAIN, 16f));
        g.drawString("Sistema de Transporte Interprovincial", x, 175);

        long t = ahora - inicioFrase;
        float alfa = (float) Math.min(1.0, Math.min(t / 350.0, (CAMBIO_FRASE_MS - t) / 350.0 + 0.15));
        alfa = Math.max(0f, Math.min(1f, alfa));
        Graphics2D gt = (Graphics2D) g.create();
        gt.setComposite(AlphaComposite.SrcOver.derive(alfa));
        gt.setColor(Color.WHITE);
        gt.setFont(Tema.fuente(Font.BOLD, 20f));
        gt.drawString(FRASES[frase][0], x, 238);
        gt.setColor(new Color(0xCFE3F3));
        gt.setFont(Tema.fuente(Font.PLAIN, 14f));
        dibujarAjustado(gt, FRASES[frase][1], x, 266, ANCHO - x - 40);
        gt.dispose();

        for (int i = 0; i < FRASES.length; i++) {
            g.setColor(i == frase ? Tema.AMBAR : new Color(255, 255, 255, 70));
            g.fillOval(x + i * 16, 308, 8, 8);
        }

        int bx = 52, by = 352, bw = ANCHO - 104, bh = 12;
        g.setColor(new Color(255, 255, 255, 40));
        g.fillRoundRect(bx, by, bw, bh, bh, bh);
        int relleno = (int) (bw * Math.min(100, progresoMostrado) / 100.0);
        if (relleno > 0) {
            g.setPaint(new GradientPaint(bx, 0, Tema.AMBAR, bx + bw, 0, new Color(0xFFD27A)));
            g.fillRoundRect(bx, by, Math.max(relleno, bh), bh, bh, bh);
            Graphics2D gr = (Graphics2D) g.create();
            gr.setClip(new RoundRectangle2D.Float(bx, by, Math.max(relleno, bh), bh, bh, bh));
            gr.setColor(new Color(255, 255, 255, 90));
            gr.setStroke(new BasicStroke(3f));
            int desplazamiento = (int) ((ahora / 25) % 24);
            for (int px = bx - 24 + desplazamiento; px < bx + relleno; px += 24) {
                gr.drawLine(px, by + bh, px + 12, by);
            }
            gr.dispose();
        }
        g.setColor(new Color(0xCFE3F3));
        g.setFont(Tema.fuente(Font.PLAIN, 12f));
        g.drawString(estado, bx, by + 34);
        String porcentaje = (int) Math.min(100, progresoMostrado) + "%";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(porcentaje, bx + bw - fm.stringWidth(porcentaje), by + 34);
    }

    private static void dibujarAjustado(Graphics2D g, String texto, int x, int y, int ancho) {
        FontMetrics fm = g.getFontMetrics();
        StringBuilder linea = new StringBuilder();
        int yy = y;
        for (String palabra : texto.split(" ")) {
            if (fm.stringWidth(linea + palabra) > ancho && linea.length() > 0) {
                g.drawString(linea.toString().trim(), x, yy);
                yy += fm.getHeight() + 2;
                linea.setLength(0);
            }
            linea.append(palabra).append(' ');
        }
        g.drawString(linea.toString().trim(), x, yy);
    }
}
