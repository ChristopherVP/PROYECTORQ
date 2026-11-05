//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import excepciones.PersistenciaException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import objetosNegocio.Empresa;
import objetosNegocio.Usuario;
import persistencia.Empresas;

public class PanelGestionEmpresas extends JPanel {
    private JTable tabla;
    private DefaultTableModel modelo;
    private Empresas persistencia;
    private Usuario usuarioActual;

    public PanelGestionEmpresas(Usuario usuario) {
        this.usuarioActual = usuario;
        this.persistencia = new Empresas("empresas.dat");
        this.initComponents();
        this.cargarDatos();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel panelBotones = new JPanel(new FlowLayout(0));
        JButton[] btns = new JButton[]{new JButton("Acceder"), new JButton("Todos"), new JButton("Agregar"), new JButton("Editar"), new JButton("Eliminar"), new JButton("Buscar")};
        btns[0].addActionListener((e) -> this.acceder());
        btns[0].setBackground(new Color(144, 238, 144));
        btns[1].addActionListener((e) -> this.cargarDatos());
        btns[2].addActionListener((e) -> this.agregar());
        btns[3].addActionListener((e) -> this.editar());
        btns[4].addActionListener((e) -> this.eliminar());
        btns[5].addActionListener((e) -> this.buscar());

        for(JButton b : btns) {
            panelBotones.add(b);
        }

        boolean esSuperAdmin = this.usuarioActual != null && "Administrador".equalsIgnoreCase(this.usuarioActual.getTipo()) && this.usuarioActual.getEmpresa() == null;
        boolean esAdminEmpresa = this.usuarioActual != null && "Administrador".equalsIgnoreCase(this.usuarioActual.getTipo()) && this.usuarioActual.getEmpresa() != null;
        if (esSuperAdmin) {
            btns[0].setVisible(false);
        } else if (esAdminEmpresa) {
            btns[0].setVisible(false);
            btns[1].setEnabled(false);
            btns[2].setEnabled(false);
            btns[4].setEnabled(false);
        } else {
            btns[2].setEnabled(false);
            btns[3].setEnabled(false);
            btns[4].setEnabled(false);
        }

        String[] cols = new String[]{"ID Empresa", "RUC", "Nombre", "Dirección", "Teléfono", "Email"};
        this.modelo = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        this.tabla = new JTable(this.modelo);
        this.tabla.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(this.tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Empresas Registradas"));
        this.add(panelBotones, "North");
        this.add(scrollPane, "Center");
    }

    private void cargarDatos() {
        try {
            this.modelo.setRowCount(0);
            List<Empresa> lista = this.persistencia.lista();

            for(int i = 0; i < lista.size(); ++i) {
                Empresa e = (Empresa)lista.get(i);
                if (this.usuarioActual.getEmpresa() == null || this.usuarioActual.getEmpresa().getIdEmpresa().equals(e.getIdEmpresa())) {
                    this.modelo.addRow(new Object[]{e.getIdEmpresa(), e.getRuc(), e.getNombre(), e.getDireccion(), e.getTelefono(), e.getEmail()});
                }
            }
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }

    }

    private void acceder() {
        int fila = this.tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una empresa para acceder.", "Aviso", 2);
        } else {
            try {
                String id = (String)this.modelo.getValueAt(fila, 0);
                Empresa empresaSeleccionada = this.persistencia.obten(new Empresa(id));
                if (empresaSeleccionada != null) {
                    JPasswordField pf = new JPasswordField();
                    int okCxl = JOptionPane.showConfirmDialog(this, pf, "Ingrese contraseña de acceso para " + empresaSeleccionada.getNombre() + ":", 2, -1);
                    if (okCxl == 0) {
                        String password = new String(pf.getPassword());
                        if (this.usuarioActual.validarContrasena(password)) {
                            Window parentWindow = SwingUtilities.getWindowAncestor(this);
                            if (parentWindow instanceof PantallaPrincipal) {
                                ((PantallaPrincipal)parentWindow).desbloquearModulos(empresaSeleccionada);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Contraseña incorrecta.", "Error de Acceso", 0);
                        }
                    }
                }
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, "Error al acceder: " + e.getMessage());
            }

        }
    }

    private void agregar() {
        InterfacesUsuario.DialogoFormularioEmpresa d = new InterfacesUsuario.DialogoFormularioEmpresa((JFrame)SwingUtilities.getWindowAncestor(this), "Agregar Empresa", (Empresa)null);
        d.setVisible(true);
        if (d.isConfirmado()) {
            try {
                this.persistencia.agrega(d.getEmpresa());
                this.cargarDatos();
                JOptionPane.showMessageDialog(this, "Empresa agregada");
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }

    }

    private void editar() {
        int fila = this.tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una empresa de la tabla", "Aviso", 2);
        } else {
            try {
                Empresa e = this.persistencia.obten(new Empresa((String)this.modelo.getValueAt(fila, 0)));
                InterfacesUsuario.DialogoFormularioEmpresa d = new InterfacesUsuario.DialogoFormularioEmpresa((JFrame)SwingUtilities.getWindowAncestor(this), "Editar Empresa", e);
                d.setVisible(true);
                if (d.isConfirmado()) {
                    this.persistencia.actualiza(d.getEmpresa());
                    this.cargarDatos();
                }
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }

        }
    }

    private void eliminar() {
        int fila = this.tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una empresa de la tabla", "Aviso", 2);
        } else if (JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar esta empresa?", "Confirmar eliminación", 0) == 0) {
            try {
                this.persistencia.elimina(new Empresa((String)this.modelo.getValueAt(fila, 0)));
                this.cargarDatos();
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }

        }
    }

    private void buscar() {
        String[] opciones = new String[]{"Por ID", "Por Nombre"};
        int opcion = JOptionPane.showOptionDialog(this, "Seleccione criterio:", "Buscar Empresa", -1, 3, (Icon)null, opciones, opciones[0]);
        if (opcion == 0) {
            String id = JOptionPane.showInputDialog("ID de empresa:");
            if (id != null && !id.trim().isEmpty()) {
                try {
                    Empresa e = this.persistencia.obten(new Empresa(id.trim().toUpperCase()));
                    if (e != null && this.usuarioActual.getEmpresa() != null && !this.usuarioActual.getEmpresa().getIdEmpresa().equals(e.getIdEmpresa())) {
                        e = null;
                    }

                    if (e != null) {
                        this.modelo.setRowCount(0);
                        this.modelo.addRow(new Object[]{e.getIdEmpresa(), e.getRuc(), e.getNombre(), e.getDireccion(), e.getTelefono(), e.getEmail()});
                    } else {
                        JOptionPane.showMessageDialog(this, "Empresa no encontrada");
                    }
                } catch (PersistenciaException e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
            }
        } else if (opcion == 1) {
            String criterio = JOptionPane.showInputDialog("Ingrese Nombre:");
            if (criterio != null && !criterio.trim().isEmpty()) {
                try {
                    List<Empresa> lista = this.persistencia.lista();
                    this.modelo.setRowCount(0);
                    boolean encontrado = false;

                    for(Empresa e : lista) {
                        if ((this.usuarioActual.getEmpresa() == null || this.usuarioActual.getEmpresa().getIdEmpresa().equals(e.getIdEmpresa())) && e.getNombre().toUpperCase().contains(criterio.toUpperCase())) {
                            this.modelo.addRow(new Object[]{e.getIdEmpresa(), e.getRuc(), e.getNombre(), e.getDireccion(), e.getTelefono(), e.getEmail()});
                            encontrado = true;
                        }
                    }

                    if (!encontrado) {
                        JOptionPane.showMessageDialog(this, "No se encontraron empresas.");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
            }
        }

    }
}
