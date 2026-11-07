package InterfacesUsuario;

import control.ControladorUsuarios;
import control.ResultadoOperacion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import objetosNegocio.Empresa;
import objetosNegocio.Rol;
import objetosNegocio.Usuario;
import persistencia.Empresas;

/**
 * Gestión de cuentas dentro de una cuenta de administrador: crear más administradores/operadores, editar,
 * eliminar y aprobar las cuentas de conductores que están pendientes.
 */
public class PanelGestionUsuarios extends JPanel {
    private final ControladorUsuarios controlador = new ControladorUsuarios();
    private final Usuario usuarioActual;
    private final String tipoFiltro;
    private final JTextField txtBuscar = Tema.campo(18);
    private final JLabel lblResumen = Tema.etiqueta(" ");
    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Usuario", "Nombre", "Cédula", "Correo", "Rol", "Cargo", "Empresa", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);
    private List<Usuario> filas = new ArrayList<>();

    public PanelGestionUsuarios(Usuario usuario, String tipoFiltro) {
        this.usuarioActual = usuario;
        this.tipoFiltro = tipoFiltro;
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        setBackground(Tema.FONDO);

        boolean esAdmin = usuario != null && usuario.getRol() == Rol.ADMINISTRADOR;
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        arriba.setOpaque(false);
        javax.swing.JButton agregar = Tema.boton("Agregar", true);
        javax.swing.JButton editar = Tema.boton("Editar", false);
        javax.swing.JButton eliminar = Tema.boton("Eliminar", false);
        javax.swing.JButton aprobar = Tema.boton("Aprobar cuenta", false);
        javax.swing.JButton actualizar = Tema.boton("Actualizar", false);
        agregar.addActionListener(e -> nuevo());
        editar.addActionListener(e -> editar());
        eliminar.addActionListener(e -> eliminar());
        aprobar.addActionListener(e -> aprobar());
        actualizar.addActionListener(e -> cargarDatos());
        for (javax.swing.JButton b : new javax.swing.JButton[]{agregar, editar, eliminar, aprobar}) {
            b.setEnabled(esAdmin);
        }
        arriba.add(agregar);
        arriba.add(editar);
        arriba.add(eliminar);
        arriba.add(aprobar);
        arriba.add(actualizar);
        arriba.add(Tema.etiqueta("Buscar"));
        arriba.add(txtBuscar);
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { cargarDatos(); }
            public void removeUpdate(DocumentEvent e) { cargarDatos(); }
            public void changedUpdate(DocumentEvent e) { cargarDatos(); }
        });
        add(arriba, BorderLayout.NORTH);

        Tema.tabla(tabla);
        tabla.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel, boolean foco, int fila, int col) {
                Component c = super.getTableCellRendererComponent(tb, v, sel, foco, fila, col);
                String s = String.valueOf(v);
                c.setForeground("ACTIVO".equals(s) ? Tema.EXITO : ("PENDIENTE".equals(s) ? new Color(0xB9770E) : Tema.ERROR));
                c.setFont(Tema.fuente(Font.BOLD, 13f));
                return c;
            }
        });
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);
        add(lblResumen, BorderLayout.SOUTH);
        cargarDatos();
    }

    public PanelGestionUsuarios(Usuario usuario) {
        this(usuario, null);
    }

    public final void cargarDatos() {
        modelo.setRowCount(0);
        filas = new ArrayList<>();
        try {
            Map<String, String> empresas = new HashMap<>();
            for (Empresa e : new Empresas("empresas.dat").lista()) {
                empresas.put(e.getIdEmpresa(), e.getNombre());
            }
            String q = txtBuscar.getText().trim().toLowerCase();
            int pendientes = 0;
            for (Usuario u : controlador.listarVisibles(usuarioActual)) {
                if (tipoFiltro != null && !tipoFiltro.equalsIgnoreCase(u.getTipo())) {
                    continue;
                }
                String texto = (u.getIdUsuario() + " " + u.getLogin() + " " + u.getNombre() + " " + u.getCedula() + " " + u.getEmail()).toLowerCase();
                if (!q.isEmpty() && !texto.contains(q)) {
                    continue;
                }
                filas.add(u);
                String empresa = u.tieneEmpresa() ? empresas.getOrDefault(u.getEmpresa().getIdEmpresa(), u.getEmpresa().getIdEmpresa()) : "(plataforma)";
                modelo.addRow(new Object[]{u.getIdUsuario(), u.getLogin(), u.getNombre(), u.getCedula(), u.getEmail(), u.getTipo(),
                        u.getCargo() == null || u.getCargo().isEmpty() ? "—" : u.getCargo(), empresa, u.getEstado()});
                if (Usuario.ESTADO_PENDIENTE.equals(u.getEstado())) {
                    pendientes++;
                }
            }
            lblResumen.setText(filas.size() + " usuario(s)" + (pendientes > 0 ? "  ·  " + pendientes + " cuenta(s) pendiente(s) de aprobación" : ""));
        } catch (Exception e) {
            Tema.error(this, "No se pudieron cargar los usuarios: " + e.getMessage());
        }
    }

    private List<Empresa> empresas() {
        try {
            return new Empresas("empresas.dat").lista();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private Usuario seleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Tema.error(this, "Selecciona un usuario de la lista.");
            return null;
        }
        return filas.get(fila);
    }

    private void nuevo() {
        new DialogoFormularioUsuario(javax.swing.SwingUtilities.getWindowAncestor(this), usuarioActual, null, empresas(), this::cargarDatos).setVisible(true);
    }

    private void editar() {
        Usuario u = seleccionado();
        if (u != null) {
            new DialogoFormularioUsuario(javax.swing.SwingUtilities.getWindowAncestor(this), usuarioActual, u, empresas(), this::cargarDatos).setVisible(true);
        }
    }

    private void eliminar() {
        Usuario u = seleccionado();
        if (u == null) return;
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar a " + u.getNombre() + " (" + u.getLogin() + ")?\nEsta acción no se puede deshacer.", "Confirmar eliminación", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        ResultadoOperacion r = controlador.eliminarUsuario(usuarioActual, u.getIdUsuario());
        if (!r.isExito()) Tema.error(this, r.getMensaje());
        cargarDatos();
    }

    private void aprobar() {
        Usuario u = seleccionado();
        if (u == null) return;
        ResultadoOperacion r = controlador.aprobarUsuario(usuarioActual, u.getIdUsuario());
        if (!r.isExito()) Tema.error(this, r.getMensaje()); else Tema.info(this, r.getMensaje());
        cargarDatos();
    }
}