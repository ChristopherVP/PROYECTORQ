package InterfacesUsuario;

import control.ControladorUsuarios;
import control.ResultadoOperacion;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import objetosNegocio.Empresa;
import objetosNegocio.Usuario;

/**
 * Alta y edición de cuentas administrativas desde dentro de una cuenta de administrador.
 * Aquí es donde se crean más administradores y operadores.
 */
public class DialogoFormularioUsuario extends JDialog {
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
    private final JPasswordField txtClave = Tema.clave(22);
    private final JPasswordField txtConfirmar = Tema.clave(22);
    private final JLabel lblFortaleza = Tema.mensaje();
    private final JTextField txtEmail = Tema.campo(22);
    private final JTextField txtTelefono = Tema.campo(22);
    private final JComboBox<String> cmbRol = new JComboBox<>(new String[]{"Administrador", "Operador"});
    private final JComboBox<ItemEmpresa> cmbEmpresa = new JComboBox<>();
    private final JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO", "PENDIENTE"});
    private final JComboBox<String> cmbPregunta = new JComboBox<>(Tema.PREGUNTAS);
    private final JTextField txtRespuesta = Tema.campo(22);
    private final JLabel lblMensaje = Tema.mensaje();

    public DialogoFormularioUsuario(Window padre, Usuario actor, Usuario editando, List<Empresa> empresas, Runnable alGuardar) {
        super(padre, editando == null ? "Nuevo usuario" : "Editar usuario", Dialog.ModalityType.APPLICATION_MODAL);
        this.actor = actor;
        this.editando = editando;
        this.alGuardar = alGuardar;
        setIconImage(IconoRQ.crear(64));
        setLayout(new BorderLayout());
        add(Tema.encabezado(editando == null ? "Nuevo usuario" : "Editar usuario",
                editando == null ? "Crea una cuenta de administrador u operador" : editando.getLogin() + " · " + editando.getTipo()), BorderLayout.NORTH);

        cargarEmpresas(empresas);
        cmbPregunta.setEditable(true);
        cmbPregunta.setFont(Tema.fuente(Font.PLAIN, 13f));
        cmbPregunta.setSelectedItem("");

        JPanel f = Tema.panelFormulario();
        int fila = 0;
        Tema.fila(f, fila++, "Usuario *", txtLogin);
        Tema.fila(f, fila++, "Nombre completo *", txtNombre);
        Tema.fila(f, fila++, "Cédula *", txtCedula);
        Tema.fila(f, fila++, editando == null ? "Contraseña *" : "Nueva contraseña", txtClave);
        Tema.fila(f, fila++, "", lblFortaleza);
        if (editando == null) {
            Tema.fila(f, fila++, "Confirmar contraseña *", txtConfirmar);
        }
        Tema.fila(f, fila++, "Correo (opcional)", txtEmail);
        Tema.fila(f, fila++, "Teléfono (opcional)", txtTelefono);
        Tema.fila(f, fila++, "Rol *", cmbRol);
        Tema.fila(f, fila++, "Empresa", cmbEmpresa);
        if (editando != null) {
            Tema.fila(f, fila++, "Estado", cmbEstado);
        } else {
            Tema.fila(f, fila++, "Pregunta (opcional)", cmbPregunta);
            Tema.fila(f, fila++, "Respuesta", txtRespuesta);
        }
        Tema.vincularFortaleza(txtClave, lblFortaleza);

        JLabel nota = Tema.nota(500, "<html>Un <b>Administrador sin empresa</b> gestiona toda la plataforma (recargas, reportes, todas las empresas). "
                + "Un operador siempre pertenece a una empresa." + (editando != null ? "<br>Deja la contraseña vacía para no cambiarla." : "") + "</html>");
        nota.setForeground(Tema.TEXTO_SUAVE);

        javax.swing.JButton guardar = Tema.boton("Guardar", true);
        guardar.addActionListener(e -> guardar());
        javax.swing.JButton cancelar = Tema.boton("Cancelar", false);
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
        add(scroll, BorderLayout.CENTER);

        if (editando != null) {
            txtLogin.setText(editando.getLogin());
            txtNombre.setText(editando.getNombre());
            txtCedula.setText(editando.getCedula());
            txtEmail.setText(editando.getEmail());
            txtTelefono.setText(editando.getTelefono());
            cmbRol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{editando.getTipo()}));
            cmbEstado.setSelectedItem(editando.getEstado());
            txtLogin.setEnabled(false);
            txtCedula.setEnabled(false);
            cmbRol.setEnabled(false);
            cmbEmpresa.setEnabled(false);
            txtNombre.setEnabled(editando.getRol() != objetosNegocio.Rol.CONDUCTOR);
        }
        setPreferredSize(new Dimension(680, 700));
        pack();
        setLocationRelativeTo(padre);
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
        ResultadoOperacion r;
        if (editando == null) {
            if (!new String(txtClave.getPassword()).equals(new String(txtConfirmar.getPassword()))) {
                Tema.mostrarMensaje(lblMensaje, "Las contraseñas no coinciden", false);
                return;
            }
            ItemEmpresa empresa = (ItemEmpresa) cmbEmpresa.getSelectedItem();
            String pregunta = String.valueOf(cmbPregunta.getEditor().getItem()).trim();
            r = controlador.crearUsuarioAdministrativo(actor, (String) cmbRol.getSelectedItem(), txtLogin.getText(), txtNombre.getText(), txtCedula.getText(),
                    new String(txtClave.getPassword()), txtEmail.getText(), txtTelefono.getText(), empresa == null ? "" : empresa.id,
                    txtRespuesta.getText().trim().isEmpty() ? "" : pregunta, txtRespuesta.getText());
        } else {
            r = controlador.actualizarUsuarioAdministrativo(actor, editando.getIdUsuario(), txtNombre.getText(), txtEmail.getText(), txtTelefono.getText(),
                    (String) cmbEstado.getSelectedItem(), new String(txtClave.getPassword()));
        }
        if (!r.isExito()) {
            Tema.mostrarMensaje(lblMensaje, r.getMensaje(), false);
            return;
        }
        alGuardar.run();
        dispose();
    }
}
