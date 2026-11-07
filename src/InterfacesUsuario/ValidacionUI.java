package InterfacesUsuario;

import excepciones.Validador;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.DocumentFilter;

/**
 * Ayudas de formulario al estilo del sistema Correo: filtros que impiden escribir caracteres inválidos
 * (cédula solo dígitos, nombre solo letras, celular 09..., correo en minúsculas, fecha con barras automáticas)
 * y mensajes de error debajo de cada campo con foco en el primero que falla.
 */
public final class ValidacionUI {
    private ValidacionUI() {
    }

    // ------------------------------------------------------------ filtros de escritura

    /** Filtro que normaliza el texto completo del campo cada vez que cambia (también al pegar). */
    private static void filtrar(JTextField campo, UnaryOperator<String> normalizador) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String texto, AttributeSet attrs) throws javax.swing.text.BadLocationException {
                replace(fb, offset, 0, texto, attrs);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int longitud, String texto, AttributeSet attrs) throws javax.swing.text.BadLocationException {
                String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String candidato = actual.substring(0, offset) + (texto == null ? "" : texto) + actual.substring(offset + longitud);
                String aceptado = normalizador.apply(candidato);
                if (aceptado != null) {
                    fb.replace(0, actual.length(), aceptado, attrs);
                }
            }

            @Override
            public void remove(FilterBypass fb, int offset, int longitud) throws javax.swing.text.BadLocationException {
                replace(fb, offset, longitud, "", null);
            }
        });
    }

    private static String recortar(String s, int max) {
        return s.length() > max ? s.substring(0, max) : s;
    }

    public static void soloDigitos(JTextField campo, int max) {
        filtrar(campo, t -> recortar(t.replaceAll("\\D", ""), max));
    }

    /** Letras (con tildes y ñ) y espacios simples; no permite empezar con espacio. */
    public static void soloLetras(JTextField campo, int max) {
        filtrar(campo, t -> recortar(t.replaceAll("[^\\p{L} ]", "").replaceAll(" {2,}", " ").replaceAll("^ ", ""), max));
    }

    /** Nombre de usuario: letras, números, punto, guion y guion bajo. */
    public static void usuario(JTextField campo, int max) {
        filtrar(campo, t -> recortar(t.replaceAll("[^A-Za-z0-9._-]", ""), max));
    }

    /** Correo: solo caracteres válidos y siempre en minúsculas. */
    public static void correo(JTextField campo, int max) {
        filtrar(campo, t -> recortar(t.replaceAll("[^A-Za-z0-9@._%+-]", "").toLowerCase(), max));
    }

    /** Celular ecuatoriano: solo dígitos, empieza con 0, luego 9, máximo 10 dígitos. */
    public static void celular(JTextField campo) {
        filtrar(campo, t -> {
            String d = t.replaceAll("\\D", "");
            if (d.length() >= 1 && d.charAt(0) != '0') {
                return null;
            }
            if (d.length() >= 2 && d.charAt(1) != '9') {
                return null;
            }
            return recortar(d, 10);
        });
    }

    /** Fecha dd/mm/aaaa: escribe solo los dígitos y las barras se ponen solas. */
    public static void fecha(JTextField campo) {
        filtrar(campo, t -> {
            String d = recortar(t.replaceAll("\\D", ""), 8);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < d.length(); i++) {
                if (i == 2 || i == 4) {
                    sb.append('/');
                }
                sb.append(d.charAt(i));
            }
            return sb.toString();
        });
    }

    public static void limite(JTextField campo, int max) {
        filtrar(campo, t -> recortar(t, max));
    }

    /** Muestra la provincia a medida que se escribe la cédula (como en el registro de Correo). */
    public static void mostrarProvincia(JTextField cedula, JLabel destino) {
        DocumentListener oyente = new DocumentListener() {
            private void actualizar() {
                String provincia = Validador.provinciaPorCedula(cedula.getText());
                destino.setForeground(provincia.startsWith("Código") ? Tema.ERROR : Tema.TEXTO_SUAVE);
                destino.setText(provincia.isEmpty() ? " " : "Provincia: " + provincia);
            }

            public void insertUpdate(DocumentEvent e) { actualizar(); }
            public void removeUpdate(DocumentEvent e) { actualizar(); }
            public void changedUpdate(DocumentEvent e) { actualizar(); }
        };
        cedula.getDocument().addDocumentListener(oyente);
    }

    // ------------------------------------------------------------ filas con error propio

    /** Etiqueta de error (roja, pequeña, oculta mientras no haya error). */
    public static JLabel etiquetaError() {
        JLabel l = new JLabel(" ");
        l.setFont(Tema.fuente(Font.PLAIN, 12f));
        l.setForeground(Tema.ERROR);
        l.setVisible(false);
        return l;
    }

    /**
     * Agrega "etiqueta + campo" y debajo (en la misma columna del campo) el mensaje de error.
     * Devuelve el índice de la siguiente fila libre. La etiqueta de texto devuelta en {@code etiquetaSalida[0]}
     * permite ocultar la fila completa.
     */
    public static int fila(JPanel panel, int fila, String etiqueta, JComponent campo, JLabel error, JLabel[] etiquetaSalida) {
        JLabel lbl = Tema.fila(panel, fila, etiqueta, campo);
        if (etiquetaSalida != null) {
            etiquetaSalida[0] = lbl;
        }
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 1;
        g.gridy = fila + 1;
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(0, 2, 2, 0);
        panel.add(error, g);
        return fila + 2;
    }

    public static int fila(JPanel panel, int fila, String etiqueta, JComponent campo, JLabel error) {
        return fila(panel, fila, etiqueta, campo, error, null);
    }

    // ------------------------------------------------------------ reglas por campo

    /** Conjunto de reglas de un formulario: validan al salir del campo y todas juntas al enviar. */
    public static final class Reglas {
        private record Regla(JComponent campo, JLabel error, Supplier<String> validador) {
        }

        private final List<Regla> reglas = new ArrayList<>();

        public void agregar(JComponent campo, JLabel error, Supplier<String> validador) {
            reglas.add(new Regla(campo, error, validador));
            campo.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    if (campo.isVisible() && campo.isEnabled()) {
                        mostrar(error, validador.get());
                    }
                }
            });
        }

        /** Valida todos los campos visibles; marca los errores y da foco al primero. true si todo está bien. */
        public boolean validar() {
            JComponent primero = null;
            for (Regla r : reglas) {
                if (!r.campo().isVisible() || !r.campo().isEnabled()) {
                    mostrar(r.error(), null);
                    continue;
                }
                String problema = r.validador().get();
                mostrar(r.error(), problema);
                if (problema != null && primero == null) {
                    primero = r.campo();
                }
            }
            if (primero != null) {
                primero.requestFocusInWindow();
            }
            return primero == null;
        }

        public void limpiar() {
            for (Regla r : reglas) {
                mostrar(r.error(), null);
            }
        }

        private static void mostrar(JLabel error, String mensaje) {
            boolean hay = mensaje != null;
            error.setText(hay ? "<html>" + mensaje.replace("<", "&lt;") + "</html>" : " ");
            error.setVisible(hay);
            error.revalidate();
        }
    }
}