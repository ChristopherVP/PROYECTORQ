package InterfacesUsuario;

import control.ControladorUsuarios;
import control.ResultadoOperacion;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/** Primer uso: crea la cuenta del administrador general (el sistema no trae contraseñas escritas en el código). */
public class PantallaConfiguracionInicial extends JFrame {
    private final ControladorUsuarios controlador = new ControladorUsuarios();
    private final JTextField txtLogin = Tema.campo(20);
    private final JTextField txtNombre = Tema.campo(20);
    private final JTextField txtCedula = Tema.campo(20);
    private final JPasswordField txtClave = Tema.clave(20);
    private final JPasswordField txtConfirmar = Tema.clave(20);
    private final JTextField txtEmail = Tema.campo(20);
    private final JTextField txtTelefono = Tema.campo(20);
    private final JComboBox<String> cmbPregunta = new JComboBox<>(Tema.PREGUNTAS);
    private final JTextField txtRespuesta = Tema.campo(20);
    private final JLabel lblFortaleza = Tema.mensaje();
    private final JLabel lblMensaje = Tema.mensaje();

    public PantallaConfiguracionInicial() {
        setTitle("RQ - Configuración inicial");
        setIconImage(IconoRQ.crear(64));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(Tema.encabezado("Bienvenido a RQ", "Configuración inicial: crea la cuenta del administrador general"), BorderLayout.NORTH);

        JPanel formulario = Tema.panelFormulario();
        cmbPregunta.setEditable(true);
        cmbPregunta.setFont(Tema.fuente(Font.PLAIN, 13f));
        int f = 0;
        Tema.fila(formulario, f++, "Usuario *", txtLogin);
        Tema.fila(formulario, f++, "Nombre completo *", txtNombre);
        Tema.fila(formulario, f++, "Cédula *", txtCedula);
        Tema.fila(formulario, f++, "Contraseña *", txtClave);
        Tema.fila(formulario, f++, "", lblFortaleza);
        Tema.fila(formulario, f++, "Confirmar contraseña *", txtConfirmar);
        Tema.fila(formulario, f++, "Correo (opcional)", txtEmail);
        Tema.fila(formulario, f++, "Teléfono (opcional)", txtTelefono);
        Tema.fila(formulario, f++, "Pregunta de seguridad *", cmbPregunta);
        Tema.fila(formulario, f++, "Respuesta *", txtRespuesta);
        Tema.vincularFortaleza(txtClave, lblFortaleza);

        JLabel nota = Tema.nota(470, "<html><b>Esta es la cuenta principal.</b> Desde dentro de ella podrás crear más administradores y operadores. "
                + "La pregunta de seguridad te permitirá recuperar la contraseña con tu cédula.</html>");
        nota.setForeground(Tema.TEXTO_SUAVE);

        JButton btnCrear = Tema.boton("Crear administrador", true);
        btnCrear.addActionListener(e -> crear());
        getRootPane().setDefaultButton(btnCrear);

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setBackground(Tema.FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));
        centro.add(nota, BorderLayout.NORTH);
        centro.add(formulario, BorderLayout.CENTER);
        JPanel sur = new JPanel(new BorderLayout(0, 6));
        sur.setOpaque(false);
        sur.add(lblMensaje, BorderLayout.NORTH);
        JPanel botones = new JPanel();
        botones.setOpaque(false);
        botones.add(btnCrear);
        sur.add(botones, BorderLayout.CENTER);
        centro.add(sur, BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(centro);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
        setPreferredSize(new Dimension(680, 740));
        pack();
        setLocationRelativeTo(null);
    }

    private void crear() {
        ResultadoOperacion r = controlador.crearSuperAdministradorInicial(txtLogin.getText(), txtNombre.getText(), txtCedula.getText(),
                new String(txtClave.getPassword()), new String(txtConfirmar.getPassword()), txtEmail.getText(), txtTelefono.getText(),
                String.valueOf(cmbPregunta.getEditor().getItem()), txtRespuesta.getText());
        if (!r.isExito()) {
            Tema.mostrarMensaje(lblMensaje, r.getMensaje(), false);
            return;
        }
        Tema.info(this, "Administrador creado. Ahora inicia sesión con tu usuario y contraseña.");
        dispose();
        new PantallaInicioSesion().setVisible(true);
    }
}
