package InterfacesUsuario;

import control.ControladorUsuarios;
import control.ResultadoOperacion;
import excepciones.Validador;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import objetosServicio.Fecha;

/**
 * Registro público de pasajeros y conductores. Cada campo se valida igual que en el registro del sistema Correo:
 * no deja escribir caracteres inválidos, muestra el error debajo del campo y da foco al primero que falla.
 */
public class PantallaRegistro extends JDialog {
    private static final String SIN_PREGUNTA = "Selecciona una pregunta...";

    private final ControladorUsuarios controlador = new ControladorUsuarios();
    private final JRadioButton rbPasajero = new JRadioButton("Soy pasajero", true);
    private final JRadioButton rbConductor = new JRadioButton("Soy conductor");
    private final JTextField txtLogin = Tema.campo(20);
    private final JTextField txtNombre = Tema.campo(20);
    private final JTextField txtCedula = Tema.campo(20);
    private final JLabel lblProvincia = Tema.etiqueta(" ");
    private final JTextField txtNacimiento = Tema.campo(20);
    private final JTextField txtDireccion = Tema.campo(20);
    private final JTextField txtTelefono = Tema.campo(20);
    private final JTextField txtEmail = Tema.campo(20);
    private final JPasswordField txtClave = Tema.clave(20);
    private final JPasswordField txtConfirmar = Tema.clave(20);
    private final JComboBox<String> cmbPregunta = new JComboBox<>();
    private final JTextField txtRespuesta = Tema.campo(20);
    private final JLabel lblFortaleza = Tema.mensaje();
    private final JLabel lblMensaje = Tema.mensaje();
    private final JLabel lblAyuda = Tema.etiqueta(" ");
    private final ValidacionUI.Reglas reglas = new ValidacionUI.Reglas();
    private final JLabel[] filaNombre = new JLabel[1];
    private final JLabel[] filaNacimiento = new JLabel[1];
    private final JLabel[] filaDireccion = new JLabel[1];
    private final JLabel errNombre = ValidacionUI.etiquetaError();
    private final JLabel errNacimiento = ValidacionUI.etiquetaError();
    private final JLabel errDireccion = ValidacionUI.etiquetaError();
    private String loginCreado;

    public PantallaRegistro(Window padre) {
        super(padre, "Crear cuenta", Dialog.ModalityType.APPLICATION_MODAL);
        setIconImage(IconoRQ.crear(64));
        setLayout(new BorderLayout());
        add(Tema.encabezado("Crear cuenta", "Elige tu rol y completa tus datos"), BorderLayout.NORTH);

        ButtonGroup grupo = new ButtonGroup();
        for (JRadioButton rb : new JRadioButton[]{rbPasajero, rbConductor}) {
            rb.setOpaque(false);
            rb.setFont(Tema.fuente(Font.BOLD, 14f));
            grupo.add(rb);
            rb.addActionListener(e -> actualizarRol());
        }
        cmbPregunta.addItem(SIN_PREGUNTA);
        for (String p : Tema.PREGUNTAS) {
            cmbPregunta.addItem(p);
        }
        cmbPregunta.setFont(Tema.fuente(Font.PLAIN, 13f));

        // filtros de escritura
        ValidacionUI.usuario(txtLogin, 20);
        ValidacionUI.soloLetras(txtNombre, 50);
        ValidacionUI.soloDigitos(txtCedula, 10);
        ValidacionUI.mostrarProvincia(txtCedula, lblProvincia);
        ValidacionUI.fecha(txtNacimiento);
        ValidacionUI.limite(txtDireccion, 80);
        ValidacionUI.celular(txtTelefono);
        ValidacionUI.correo(txtEmail, 50);
        ValidacionUI.limite(txtRespuesta, 40);
        lblProvincia.setForeground(Tema.TEXTO_SUAVE);
        txtNacimiento.setToolTipText("dd/mm/aaaa (ejemplo: 15/03/2000)");

        JLabel errLogin = ValidacionUI.etiquetaError();
        JLabel errCedula = ValidacionUI.etiquetaError();
        JLabel errTelefono = ValidacionUI.etiquetaError();
        JLabel errEmail = ValidacionUI.etiquetaError();
        JLabel errClave = ValidacionUI.etiquetaError();
        JLabel errConfirmar = ValidacionUI.etiquetaError();
        JLabel errPregunta = ValidacionUI.etiquetaError();
        JLabel errRespuesta = ValidacionUI.etiquetaError();

        JPanel formulario = Tema.panelFormulario();
        int f = 0;
        f = ValidacionUI.fila(formulario, f, "Usuario *", txtLogin, errLogin);
        f = ValidacionUI.fila(formulario, f, "Nombre completo *", txtNombre, errNombre, filaNombre);
        f = ValidacionUI.fila(formulario, f, "Cédula *", txtCedula, errCedula);
        Tema.fila(formulario, f++, "", lblProvincia);
        f = ValidacionUI.fila(formulario, f, "Fecha de nacimiento *", txtNacimiento, errNacimiento, filaNacimiento);
        f = ValidacionUI.fila(formulario, f, "Dirección (opcional)", txtDireccion, errDireccion, filaDireccion);
        f = ValidacionUI.fila(formulario, f, "Teléfono celular *", txtTelefono, errTelefono);
        f = ValidacionUI.fila(formulario, f, "Correo *", txtEmail, errEmail);
        f = ValidacionUI.fila(formulario, f, "Contraseña *", txtClave, errClave);
        Tema.fila(formulario, f++, "", lblFortaleza);
        f = ValidacionUI.fila(formulario, f, "Confirmar contraseña *", txtConfirmar, errConfirmar);
        JCheckBox chkMostrar = new JCheckBox("Mostrar contraseñas");
        chkMostrar.setOpaque(false);
        chkMostrar.setFont(Tema.fuente(Font.PLAIN, 12f));
        char eco = txtClave.getEchoChar();
        chkMostrar.addActionListener(e -> {
            char c = chkMostrar.isSelected() ? (char) 0 : eco;
            txtClave.setEchoChar(c);
            txtConfirmar.setEchoChar(c);
        });
        Tema.fila(formulario, f++, "", chkMostrar);
        f = ValidacionUI.fila(formulario, f, "Pregunta de seguridad *", cmbPregunta, errPregunta);
        ValidacionUI.fila(formulario, f, "Respuesta *", txtRespuesta, errRespuesta);
        Tema.vincularFortaleza(txtClave, lblFortaleza);

        // reglas, en el orden del formulario
        reglas.agregar(txtLogin, errLogin, () -> Validador.errorLogin(txtLogin.getText()));
        reglas.agregar(txtNombre, errNombre, () -> Validador.errorNombre(txtNombre.getText()));
        reglas.agregar(txtCedula, errCedula, () -> Validador.errorCedula(txtCedula.getText()));
        reglas.agregar(txtNacimiento, errNacimiento, () -> Validador.errorMayorDeEdad(txtNacimiento.getText()));
        reglas.agregar(txtTelefono, errTelefono, () -> Validador.errorTelefonoCelular(txtTelefono.getText()));
        reglas.agregar(txtEmail, errEmail, () -> Validador.errorEmailCuenta(txtEmail.getText()));
        reglas.agregar(txtClave, errClave, () -> Validador.errorContrasena(new String(txtClave.getPassword())));
        reglas.agregar(txtConfirmar, errConfirmar, () -> new String(txtClave.getPassword()).equals(new String(txtConfirmar.getPassword()))
                ? null : "Las contraseñas no coinciden");
        reglas.agregar(cmbPregunta, errPregunta, () -> cmbPregunta.getSelectedIndex() <= 0 ? "Elige una pregunta de seguridad" : null);
        reglas.agregar(txtRespuesta, errRespuesta, () -> Validador.errorRespuestaSeguridad(txtRespuesta.getText()));

        JPanel roles = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 4));
        roles.setOpaque(false);
        roles.add(rbPasajero);
        roles.add(rbConductor);

        JPanel arriba = new JPanel(new BorderLayout());
        arriba.setOpaque(false);
        arriba.add(roles, BorderLayout.NORTH);
        lblAyuda.setForeground(Tema.TEXTO_SUAVE);
        arriba.add(lblAyuda, BorderLayout.SOUTH);

        JButton btnCrear = Tema.boton("Crear cuenta", true);
        btnCrear.addActionListener(e -> crear());
        JButton btnCancelar = Tema.boton("Cancelar", false);
        btnCancelar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnCrear);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        botones.setOpaque(false);
        botones.add(btnCancelar);
        botones.add(btnCrear);

        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setBackground(Tema.FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28));
        centro.add(arriba, BorderLayout.NORTH);
        centro.add(formulario, BorderLayout.CENTER);
        JPanel sur = new JPanel(new BorderLayout(0, 6));
        sur.setOpaque(false);
        sur.add(lblMensaje, BorderLayout.NORTH);
        sur.add(botones, BorderLayout.CENTER);
        centro.add(sur, BorderLayout.SOUTH);

        JScrollPane scroll = new JScrollPane(centro);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
        actualizarRol();
        setPreferredSize(new Dimension(700, 780));
        pack();
        setLocationRelativeTo(padre);
    }

    public String getLoginCreado() {
        return loginCreado;
    }

    private void actualizarRol() {
        boolean pasajero = rbPasajero.isSelected();
        filaNombre[0].setVisible(pasajero);
        txtNombre.setVisible(pasajero);
        filaNacimiento[0].setVisible(pasajero);
        txtNacimiento.setVisible(pasajero);
        filaDireccion[0].setVisible(pasajero);
        txtDireccion.setVisible(pasajero);
        reglas.limpiar();
        Tema.mostrarMensaje(lblMensaje, " ", true);
        lblAyuda.setText(pasajero
                ? Tema.html(520, "Podrás comprar pasajes con tu billetera. Debes ser mayor de 18 años.")
                : Tema.html(520, "Tu empresa o el administrador deben haberte registrado como conductor antes. Usa tu misma cédula; tu cuenta quedará <b>pendiente de aprobación</b>."));
        revalidate();
        repaint();
    }

    private void crear() {
        if (!reglas.validar()) {
            Tema.mostrarMensaje(lblMensaje, "Revisa los campos marcados en rojo.", false);
            return;
        }
        String clave = new String(txtClave.getPassword());
        String confirmar = new String(txtConfirmar.getPassword());
        String pregunta = String.valueOf(cmbPregunta.getSelectedItem());
        ResultadoOperacion r;
        if (rbPasajero.isSelected()) {
            Fecha nacimiento = Validador.fechaDesdeTexto(txtNacimiento.getText());
            r = controlador.registrarPasajero(txtLogin.getText(), txtNombre.getText(), txtCedula.getText(), clave, confirmar, txtEmail.getText(),
                    txtTelefono.getText(), txtDireccion.getText(), nacimiento, false, pregunta, txtRespuesta.getText());
        } else {
            r = controlador.registrarConductor(txtLogin.getText(), txtCedula.getText(), clave, confirmar, txtEmail.getText(), txtTelefono.getText(),
                    pregunta, txtRespuesta.getText());
        }
        if (!r.isExito()) {
            Tema.mostrarMensaje(lblMensaje, r.getMensaje(), false);
            return;
        }
        loginCreado = txtLogin.getText().trim();
        Tema.info(this, r.getMensaje());
        dispose();
    }
}