package InterfacesUsuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import excepciones.Validador;

/** Colores, tipografías y componentes comunes para que todas las pantallas nuevas se vean iguales. */
public final class Tema {
    public static final Color AZUL_OSCURO = new Color(0x0A2E4A);
    public static final Color AZUL = new Color(0x14588A);
    public static final Color CELESTE = new Color(0x2E86C1);
    public static final Color AMBAR = new Color(0xF5A623);
    public static final Color FONDO = new Color(0xF3F6F9);
    public static final Color TEXTO = new Color(0x1F2D3D);
    public static final Color TEXTO_SUAVE = new Color(0x6B7C8C);
    public static final Color BORDE = new Color(0xD6DEE6);
    public static final Color ERROR = new Color(0xC0392B);
    public static final Color EXITO = new Color(0x1E8449);

    public static final String[] PREGUNTAS = {
            "¿Cómo se llamaba tu primera mascota?",
            "¿En qué ciudad nació tu madre?",
            "¿Cuál es el nombre de tu escuela primaria?",
            "¿Cuál era tu apodo de la infancia?",
            "¿Cuál fue el primer lugar al que viajaste en bus?"
    };

    private Tema() {
    }

    public static void aplicarAspecto() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // si el aspecto del sistema no está disponible se usa el predeterminado de Java
        }
    }

    public static Font fuente(int estilo, float tamano) {
        return new Font("Segoe UI", estilo, 1).deriveFont(estilo, tamano);
    }

    public static String dinero(double valor) {
        return String.format(Locale.US, "$%.2f", valor);
    }

    public static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "No se pudo completar", JOptionPane.WARNING_MESSAGE);
    }

    public static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "RQ", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Botón redondeado que se pinta solo (los colores se ven igual en cualquier sistema). */
    public static class BotonRQ extends JButton {
        private final boolean primario;
        private boolean encima;

        BotonRQ(String texto, boolean primario) {
            super(texto);
            this.primario = primario;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setFont(fuente(Font.BOLD, 13f));
            setForeground(primario ? Color.WHITE : AZUL);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMargin(new Insets(8, 18, 8, 18));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { encima = true; repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { encima = false; repaint(); }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width + 12, Math.max(36, d.height + 6));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color base;
            if (!isEnabled()) {
                base = new Color(0xC9D3DC);
            } else if (primario) {
                base = encima ? CELESTE : AZUL;
            } else {
                base = encima ? new Color(0xE3EEF7) : Color.WHITE;
            }
            g.setColor(base);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            if (!primario) {
                g.setColor(isEnabled() ? CELESTE : BORDE);
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
            g.dispose();
            setForeground(!isEnabled() ? new Color(0x7B8A97) : (primario ? Color.WHITE : AZUL));
            super.paintComponent(g0);
        }
    }

    public static JButton boton(String texto, boolean primario) {
        return new BotonRQ(texto, primario);
    }

    public static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(Font.PLAIN, 13f));
        l.setForeground(TEXTO);
        return l;
    }

    /** Texto HTML con ancho fijo para que se ajuste en varias líneas sin cortarse. */
    public static String html(int ancho, String contenido) {
        return "<html><div style='width:" + ancho + "px'>" + contenido + "</div></html>";
    }

    /** Etiqueta de varias líneas; "htmlCompleto" debe empezar con &lt;html&gt; y terminar con &lt;/html&gt;. */
    public static JLabel nota(int ancho, String htmlCompleto) {
        String interior = htmlCompleto.replaceFirst("(?is)^<html>", "").replaceFirst("(?is)</html>$", "");
        JLabel l = etiqueta(html(ancho, interior));
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16));
        return l;
    }

    public static JLabel titulo(String texto, float tamano) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente(Font.BOLD, tamano));
        l.setForeground(AZUL_OSCURO);
        return l;
    }

    public static JLabel mensaje() {
        JLabel l = new JLabel(" ");
        l.setFont(fuente(Font.PLAIN, 12f));
        l.setForeground(ERROR);
        return l;
    }

    public static void mostrarMensaje(JLabel etiqueta, String texto, boolean exito) {
        etiqueta.setForeground(exito ? EXITO : ERROR);
        etiqueta.setText("<html>" + texto.replace("<", "&lt;") + "</html>");
    }

    private static javax.swing.border.Border bordeCampo() {
        return BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDE), BorderFactory.createEmptyBorder(6, 8, 6, 8));
    }

    public static JTextField campo(int columnas) {
        JTextField c = new JTextField(columnas);
        c.setFont(fuente(Font.PLAIN, 14f));
        c.setBorder(bordeCampo());
        return c;
    }

    public static JPasswordField clave(int columnas) {
        JPasswordField c = new JPasswordField(columnas);
        c.setFont(fuente(Font.PLAIN, 14f));
        c.setBorder(bordeCampo());
        return c;
    }

    /** Agrega una fila "etiqueta + campo" a un panel con GridBagLayout. */
    public static JLabel fila(JPanel panel, int fila, String etiqueta, JComponent campo) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = fila;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(5, 0, 5, 12);
        JLabel lbl = etiqueta(etiqueta);
        panel.add(lbl, g);
        g = new GridBagConstraints();
        g.gridx = 1;
        g.gridy = fila;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(5, 0, 5, 0);
        panel.add(campo, g);
        return lbl;
    }

    public static JPanel panelFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        return p;
    }

    public static JPanel encabezado(String titulo, String subtitulo) {
        JPanel p = new JPanel(new BorderLayout(14, 0)) {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setPaint(new GradientPaint(0, 0, AZUL_OSCURO, getWidth(), 0, AZUL));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.dispose();
            }
        };
        p.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        p.add(new JLabel(IconoRQ.icono(48)), BorderLayout.WEST);
        JPanel textos = new JPanel(new GridBagLayout());
        textos.setOpaque(false);
        JLabel t = new JLabel(titulo);
        t.setFont(fuente(Font.BOLD, 20f));
        t.setForeground(Color.WHITE);
        JLabel s = new JLabel(subtitulo);
        s.setFont(fuente(Font.PLAIN, 12f));
        s.setForeground(new Color(0xCFE3F3));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = 0;
        g.anchor = GridBagConstraints.WEST;
        textos.add(t, g);
        g.gridy = 1;
        textos.add(s, g);
        p.add(textos, BorderLayout.CENTER);
        return p;
    }

    public static void tabla(JTable t) {
        t.setRowHeight(28);
        t.setFont(fuente(Font.PLAIN, 13f));
        t.setGridColor(new Color(0xE8EEF3));
        t.setShowVerticalLines(false);
        t.setSelectionBackground(new Color(0xD6E9F8));
        t.setSelectionForeground(TEXTO);
        t.setFillsViewportHeight(true);
        JTableHeader h = t.getTableHeader();
        h.setFont(fuente(Font.BOLD, 13f));
        h.setBackground(AZUL_OSCURO);
        h.setForeground(Color.WHITE);
        h.setReorderingAllowed(false);
        DefaultTableCellRenderer izquierda = new DefaultTableCellRenderer();
        izquierda.setHorizontalAlignment(SwingConstants.LEFT);
        ((DefaultTableCellRenderer) h.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
    }

    /** Muestra debajo de un campo de contraseña si es débil, media o fuerte mientras se escribe. */
    public static void vincularFortaleza(JPasswordField campo, JLabel etiqueta) {
        DocumentListener oyente = new DocumentListener() {
            private void actualizar() {
                String clave = new String(campo.getPassword());
                if (clave.isEmpty()) {
                    etiqueta.setText(" ");
                    return;
                }
                String error = Validador.errorContrasena(clave);
                int f = Validador.fortaleza(clave);
                String texto = f == 0 ? "Débil" : (f == 1 ? "Media" : "Fuerte");
                etiqueta.setForeground(error != null ? ERROR : (f == 2 ? EXITO : new Color(0xB9770E)));
                etiqueta.setText(error != null ? error : "Seguridad: " + texto);
            }

            public void insertUpdate(DocumentEvent e) { actualizar(); }
            public void removeUpdate(DocumentEvent e) { actualizar(); }
            public void changedUpdate(DocumentEvent e) { actualizar(); }
        };
        campo.getDocument().addDocumentListener(oyente);
    }
}
