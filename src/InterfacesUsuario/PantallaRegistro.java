package InterfacesUsuario;

import control.ControladorUsuarios;
import control.ResultadoOperacion;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.time.LocalDate;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import objetosServicio.Fecha;

/** Registro público: la persona elige su rol (pasajero o conductor). Los administradores se crean desde dentro de una cuenta de administrador. */
public class PantallaRegistro extends JDialog {
    private final ControladorUsuarios controlador = new ControladorUsuarios();
    private final JRadioButton rbPasajero = new JRadioButton("Soy pasajero", true);
    private final JRadioButton rbConductor = new JRadioButton("Soy conductor");
    private final JTextField txtLogin = Tema.campo(20);
    private final JTextField txtNombre = Tema.campo(20);
    private final JTextField txtCedula = Tema.campo(20);
    private final JComboBox<Integer> cmbDia = new JComboBox<>();
    private final JComboBox<String> cmbMes = new JComboBox<>(new String[]{"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"});
    private final JComboBox<Integer> cmbAnio = new JComboBox<>();
    private final JTextField txtDireccion = Tema.campo(20);
    private final JTextField txtTelefono = Tema.campo(20);
    private final JTextField txtEmail = Tema.campo(20);
    private final JPasswordField txtClave = Tema.clave(20);
    private final JPasswordField txtConfirmar = Tema.clave(20);
    private final JComboBox<String> cmbPregunta = new JComboBox<>(Tema.PREGUNTAS);
    private final JTextField txtRespuesta = Tema.campo(20);
    private final JLabel lblFortaleza = Tema.mensaje();
    private final JLabel lblMensaje = Tema.mensaje();
    private final JLabel lblAyuda = Tema.etiqueta(" ");
    private JLabel etqNombre;
    private JLabel etqNacimiento;
    private JLabel etqDireccion;
    private JPanel panelNacimiento;
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
        for (int d = 1; d <= 31; d++) cmbDia.addItem(d);
        int anio = LocalDate.now().getYear();
        for (int a = anio - 18; a >= anio - 100; a--) cmbAnio.addItem(a);
        panelNacimiento = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelNacimiento.setOpaque(false);
        panelNacimiento.add(cmbDia);
        panelNacimiento.add(cmbMes);
        panelNacimiento.add(cmbAnio);
        cmbPregunta.setEditable(true);
        cmbPregunta.setFont(Tema.fuente(Font.PLAIN, 13f));

        JPanel formulario = Tema.panelFormulario();
        int f = 0;
        Tema.fila(formulario, f++, "Usuario *", txtLogin);
        etqNombre = Tema.fila(formulario, f++, "Nombre completo *", txtNombre);
        Tema.fila(formulario, f++, "Cédula *", txtCedula);
        etqNacimiento = Tema.fila(formulario, f++, "Fecha de nacimiento *", panelNacimiento);
        etqDireccion = Tema.fila(formulario, f++, "Dirección (opcional)", txtDireccion);
        Tema.fila(formulario, f++, "Teléfono (opcional)", txtTelefono);
        Tema.fila(formulario, f++, "Correo (opcional)", txtEmail);
        Tema.fila(formulario, f++, "Contraseña *", txtClave);
        Tema.fila(formulario, f++, "", lblFortaleza);
        Tema.fila(formulario, f++, "Confirmar contraseña *", txtConfirmar);
        Tema.fila(formulario, f++, "Pregunta de seguridad *", cmbPregunta);
        Tema.fila(formulario, f++, "Respuesta *", txtRespuesta);
        Tema.vincularFortaleza(txtClave, lblFortaleza);

        JPanel roles = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 4));
        roles.setOpaque(false);
        roles.add(rbPasajero);
        roles.add(rbConductor);

        JPanel arriba = new JPanel(new BorderLayout());
        arriba.setOpaque(false);
        arriba.add(roles, BorderLayout.NORTH);
        lblAyuda.setForeground(Tema.TEXTO_SUAVE);
        arriba.add(lblAyuda, BorderLayout.SOUTH);

        javax.swing.JButton btnCrear = Tema.boton("Crear cuenta", true);
        btnCrear.addActionListener(e -> crear());
        javax.swing.JButton btnCancelar = Tema.boton("Cancelar", false);
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
        setPreferredSize(new Dimension(680, 760));
        pack();
        setLocationRelativeTo(padre);
    }

    public String getLoginCreado() {
        return loginCreado;
    }

    private void actualizarRol() {
        boolean pasajero = rbPasajero.isSelected();
        etqNombre.setVisible(pasajero);
        txtNombre.setVisible(pasajero);
        etqNacimiento.setVisible(pasajero);
        panelNacimiento.setVisible(pasajero);
        etqDireccion.setVisible(pasajero);
        txtDireccion.setVisible(pasajero);
        lblAyuda.setText(pasajero
                ? Tema.html(500, "Podrás comprar pasajes con tu billetera. Debes ser mayor de 18 años.")
                : Tema.html(500, "Tu empresa o el administrador deben haberte registrado como conductor antes. Usa tu misma cédula; tu cuenta quedará <b>pendiente de aprobación</b>."));
        revalidate();
        repaint();
    }

    private void crear() {
        String clave = new String(txtClave.getPassword());
        String confirmar = new String(txtConfirmar.getPassword());
        String pregunta = String.valueOf(cmbPregunta.getEditor().getItem());
        ResultadoOperacion r;
        if (rbPasajero.isSelected()) {
            Fecha nacimiento = new Fecha((Integer) cmbDia.getSelectedItem(), cmbMes.getSelectedIndex() + 1, (Integer) cmbAnio.getSelectedItem());
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
