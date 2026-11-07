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
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import objetosNegocio.Cargo;
import objetosNegocio.Empresa;
import objetosNegocio.Rol;
import objetosNegocio.Usuario;

/**
 * Alta y edición de colaboradores (administradores y operadores) con su cargo. Solo el administrador principal
 * puede crear o editar administradores; los demás administradores solo gestionan operadores.
 * La contraseña que se asigna es temporal: la persona debe cambiarla en su primer ingreso.
 */
public class DialogoFormularioUsuario extends JDialog {
    private static final String SIN_PREGUNTA = "(Sin pregunta por ahora)";

    private static final class ItemEmpresa {
        final String id;
        final String texto;

        ItemEmpresa(String id, String texto) {
            this.id = id;
            this.texto = texto;
        }

        @Override
        public String toString() {
            return texto;
        }
    }

    private final ControladorUsuarios controlador = new ControladorUsuarios();
    private final Usuario actor;
    private final Usuario editando;
    private final Runnable alGuardar;

    private final JTextField txtLogin = Tema.campo(22);
    private final JTextField txtNombre = Tema.campo(22);
    private final JTextField txtCedula = Tema.campo(22);
    private final JLabel lblProvincia = Tema.etiqueta(" ");
    private final JPasswordField txtClave = Tema.clave(22);
    private final JPasswordField txtConfirmar = Tema.clave(22);
    private final JLabel lblFortaleza = Tema.mensaje();
    private final JTextField txtEmail = Tema.campo(22);
    private final JTextField txtTelefono = Tema.campo(22);
    private final JComboBox<String> cmbRol = new JComboBox<>();
    private final JComboBox<Cargo> cmbCargo = new JComboBox<>();
    private final JComboBox<ItemEmpresa> cmbEmpresa = new JComboBox<>();
    private final JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO", "PENDIENTE"});
    private final JComboBox<String> cmbPregunta = new JComboBox<>();
    private final JTextField txtRespuesta = Tema.campo(22);
    private final JLabel lblMensaje = Tema.mensaje();
    private final ValidacionUI.Reglas reglas = new ValidacionUI.Reglas();

    public DialogoFormularioUsuario(Window padre, Usuario actor, Usuario editando, List<Empresa> empresas, Runnable alGuardar) {
        super(padre, editando == null ? "Nuevo colaborador" : "Editar usuario", Dialog.ModalityType.APPLICATION_MODAL);
        this.actor = actor;
        this.editando = editando;
        this.alGuardar = alGuardar;
        setIconImage(IconoRQ.crear(64));
        setLayout(new BorderLayout());
        add(Tema.encabezado(editando == null ? "Nuevo colaborador" : "Editar usuario",
                editando == null ? "Crea una cuenta de administrador u operador con su cargo" : editando.getLogin() + " · " + editando.getTipo()), BorderLayout.NORTH);

        cargarEmpresas(empresas);
        cargarRoles();
        cmbPregunta.addItem(SIN_PREGUNTA);
        for (String p : Tema.PREGUNTAS) {
            cmbPregunta.addItem(p);
        }
        cmbPregunta.setFont(Tema.fuente(Font.PLAIN, 13f));

        ValidacionUI.usuario(txtLogin, 20);
        ValidacionUI.soloLetras(txtNombre, 50);
        ValidacionUI.soloDigitos(txtCedula, 10);
        ValidacionUI.mostrarProvincia(txtCedula, lblProvincia);
        ValidacionUI.correo(txtEmail, 50);
        ValidacionUI.celular(txtTelefono);
        ValidacionUI.limite(txtRespuesta, 40);
        lblProvincia.setForeground(Tema.TEXTO_SUAVE);

        JLabel errLogin = ValidacionUI.etiquetaError();
        JLabel errNombre = ValidacionUI.etiquetaError();
        JLabel errCedula = ValidacionUI.etiquetaError();
        JLabel errClave = ValidacionUI.etiquetaError();
        JLabel errConfirmar = ValidacionUI.etiquetaError();
        JLabel errEmail = ValidacionUI.etiquetaError();
        JLabel errTelefono = ValidacionUI.etiquetaError();
        JLabel errEmpresa = ValidacionUI.etiquetaError();
        JLabel errPregunta = ValidacionUI.etiquetaError();
        JLabel errRespuesta = ValidacionUI.etiquetaError();

        JPanel f = Tema.panelFormulario();
        int fila = 0;
        fila = ValidacionUI.fila(f, fila, "Usuario *", txtLogin, errLogin);
        fila = ValidacionUI.fila(f, fila, "Nombre completo *", txtNombre, errNombre);
        fila = ValidacionUI.fila(f, fila, "Cédula *", txtCedula, errCedula);
        Tema.fila(f, fila++, "", lblProvincia);
        fila = ValidacionUI.fila(f, fila, editando == null ? "Contraseña temporal *" : "Nueva contraseña", txtClave, errClave);
        Tema.fila(f, fila++, "", lblFortaleza);
        if (editando == null) {
            fila = ValidacionUI.fila(f, fila, "Confirmar contraseña *", txtConfirmar, errConfirmar);
        }
        fila = ValidacionUI.fila(f, fila, "Correo (opcional)", txtEmail, errEmail);
        fila = ValidacionUI.fila(f, fila, "Celular (opcional)", txtTelefono, errTelefono);
        Tema.fila(f, fila++, "Rol *", cmbRol);
        Tema.fila(f, fila++, "Cargo *", cmbCargo);
        fila = ValidacionUI.fila(f, fila, "Empresa", cmbEmpresa, errEmpresa);
        if (editando != null) {
            Tema.fila(f, fila++, "Estado", cmbEstado);
        } else {
            fila = ValidacionUI.fila(f, fila, "Pregunta (opcional)", cmbPregunta, errPregunta);
            ValidacionUI.fila(f, fila, "Respuesta", txtRespuesta, errRespuesta);
        }
        Tema.vincularFortaleza(txtClave, lblFortaleza);
        cmbRol.addActionListener(e -> cargarCargos());
        cargarCargos();

        reglas.agregar(txtLogin, errLogin, () -> Validador.errorLogin(txtLogin.getText()));
        reglas.agregar(txtNombre, errNombre, () -> Validador.errorNombre(txtNombre.getText()));
        reglas.agregar(txtCedula, errCedula, () -> Validador.errorCedula(txtCedula.getText()));
        reglas.agregar(txtClave, errClave, () -> {
            String clave = new String(txtClave.getPassword());
            return editando != null && clave.isEmpty() ? null : Validador.errorContrasena(clave);
        });
        if (editando == null) {
            reglas.agregar(txtConfirmar, errConfirmar, () -> new String(txtClave.getPassword()).equals(new String(txtConfirmar.getPassword()))
                    ? null : "Las contraseñas no coinciden");
        }
        reglas.agregar(txtEmail, errEmail, () -> Validador.errorEmailOpcional(txtEmail.getText()));
        reglas.agregar(txtTelefono, errTelefono, () -> Validador.errorTelefonoOpcional(txtTelefono.getText()));
        reglas.agregar(cmbEmpresa, errEmpresa, () -> {
            ItemEmpresa empresa = (ItemEmpresa) cmbEmpresa.getSelectedItem();
            boolean sinEmpresa = empresa == null || empresa.id.isEmpty();
            return Rol.OPERADOR.getEtiqueta().equals(cmbRol.getSelectedItem()) && sinEmpresa ? "El operador debe pertenecer a una empresa" : null;
        });
        if (editando == null) {
            reglas.agregar(cmbPregunta, errPregunta, () -> cmbPregunta.getSelectedIndex() <= 0 && !txtRespuesta.getText().trim().isEmpty()
                    ? "Elige la pregunta de seguridad" : null);
            reglas.agregar(txtRespuesta, errRespuesta, () -> cmbPregunta.getSelectedIndex() > 0 ? Validador.errorRespuestaSeguridad(txtRespuesta.getText()) : null);
        }

        JLabel nota = Tema.nota(520, "<html>Un <b>administrador sin empresa</b> gestiona toda la plataforma; un operador siempre pertenece a una empresa. "
                + "Solo el administrador principal crea administradores. "
                + (editando == null ? "La persona deberá cambiar la contraseña temporal al ingresar por primera vez." : "Deja la contraseña vacía para no cambiarla.")
                + "</html>");
        nota.setForeground(Tema.TEXTO_SUAVE);

        JButton guardar = Tema.boton("Guardar", true);
        guardar.addActionListener(e -> guardar());
        JButton cancelar = Tema.boton("Cancelar", false);
        cancelar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(guardar);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        botones.setOpaque(false);
        botones.add(cancelar);
        botones.add(guardar);

        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setBackground(Tema.FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28));
        centro.add(nota, BorderLayout.NORTH);
        centro.add(f, BorderLayout.CENTER);
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

        if (editando != null) {
            txtLogin.setText(editando.getLogin());
            txtNombre.setText(editando.getNombre());
            txtCedula.setText(editando.getCedula());
            txtEmail.setText(editando.getEmail());
            txtTelefono.setText(editando.getTelefono());
            cmbEstado.setSelectedItem(editando.getEstado());
            selecionarCargo(editando.getCargo());
            txtLogin.setEnabled(false);
            txtCedula.setEnabled(false);
            cmbRol.setEnabled(false);
            cmbEmpresa.setEnabled(false);
            txtNombre.setEnabled(editando.getRol() != Rol.CONDUCTOR);
            cmbCargo.setEnabled(editando.getRol().esAdministrativo() && !editando.esAdministradorPrincipal());
            cmbEstado.setEnabled(!editando.esAdministradorPrincipal());
        }
        setPreferredSize(new Dimension(700, 780));
        pack();
        setLocationRelativeTo(padre);
    }

    /** Solo el administrador principal ofrece el rol Administrador; los demás solo crean operadores. */
    private void cargarRoles() {
        if (editando != null) {
            cmbRol.setModel(new DefaultComboBoxModel<>(new String[]{editando.getTipo()}));
            return;
        }
        if (actor.esAdministradorPrincipal()) {
            cmbRol.setModel(new DefaultComboBoxModel<>(new String[]{Rol.ADMINISTRADOR.getEtiqueta(), Rol.OPERADOR.getEtiqueta()}));
        } else {
            cmbRol.setModel(new DefaultComboBoxModel<>(new String[]{Rol.OPERADOR.getEtiqueta()}));
        }
    }

    private void cargarCargos() {
        Rol rol = Rol.desdeEtiqueta((String) cmbRol.getSelectedItem());
        DefaultComboBoxModel<Cargo> modelo = new DefaultComboBoxModel<>();
        if (editando != null && editando.esAdministradorPrincipal()) {
            modelo.addElement(Cargo.ADMINISTRADOR_PRINCIPAL);
        } else if (rol != null) {
            for (Cargo c : Cargo.asignablesA(rol)) {
                modelo.addElement(c);
            }
        }
        cmbCargo.setModel(modelo);
    }

    private void selecionarCargo(String etiqueta) {
        Cargo cargo = Cargo.desdeEtiqueta(etiqueta);
        if (cargo != null) {
            cmbCargo.setSelectedItem(cargo);
        }
    }

    private void cargarEmpresas(List<Empresa> empresas) {
        if (actor.tieneEmpresa()) {
            String id = actor.getEmpresa().getIdEmpresa();
            String nombre = actor.getEmpresa().getNombre() != null ? actor.getEmpresa().getNombre() : id;
            cmbEmpresa.addItem(new ItemEmpresa(id, nombre));
            cmbEmpresa.setEnabled(false);
            return;
        }
        cmbEmpresa.addItem(new ItemEmpresa("", "(Ninguna: administrador general)"));
        for (Empresa e : empresas) {
            cmbEmpresa.addItem(new ItemEmpresa(e.getIdEmpresa(), e.getIdEmpresa() + " · " + e.getNombre()));
        }
        if (editando != null && editando.tieneEmpresa()) {
            for (int i = 0; i < cmbEmpresa.getItemCount(); i++) {
                if (cmbEmpresa.getItemAt(i).id.equals(editando.getEmpresa().getIdEmpresa())) {
                    cmbEmpresa.setSelectedIndex(i);
                }
            }
        }
    }

    private void guardar() {
        if (!reglas.validar()) {
            Tema.mostrarMensaje(lblMensaje, "Revisa los campos marcados en rojo.", false);
            return;
        }
        Cargo cargo = (Cargo) cmbCargo.getSelectedItem();
        String etiquetaCargo = cargo == null ? "" : cargo.getEtiqueta();
        ResultadoOperacion r;
        if (editando == null) {
            ItemEmpresa empresa = (ItemEmpresa) cmbEmpresa.getSelectedItem();
            boolean hayPregunta = cmbPregunta.getSelectedIndex() > 0;
            r = controlador.crearUsuarioAdministrativo(actor, (String) cmbRol.getSelectedItem(), etiquetaCargo, txtLogin.getText(), txtNombre.getText(),
                    txtCedula.getText(), new String(txtClave.getPassword()), txtEmail.getText(), txtTelefono.getText(), empresa == null ? "" : empresa.id,
                    hayPregunta ? String.valueOf(cmbPregunta.getSelectedItem()) : "", txtRespuesta.getText());
        } else {
            r = controlador.actualizarUsuarioAdministrativo(actor, editando.getIdUsuario(), etiquetaCargo, txtNombre.getText(), txtEmail.getText(),
                    txtTelefono.getText(), (String) cmbEstado.getSelectedItem(), new String(txtClave.getPassword()));
        }
        if (!r.isExito()) {
            Tema.mostrarMensaje(lblMensaje, r.getMensaje(), false);
            return;
        }
        alGuardar.run();
        dispose();
    }
}