//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import excepciones.PersistenciaException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import objetosNegocio.Empresa;
import objetosNegocio.Usuario;
import persistencia.Empresas;
import persistencia.Usuarios;

public class PanelGestionUsuarios extends JPanel {
    private JTable tabla;
    private DefaultTableModel modelo;
    private Usuarios persistencia;
    private Usuario usuarioActual;
    private String tipoFiltro;

    public PanelGestionUsuarios(Usuario usuario, String tipoFiltro) {
        this.usuarioActual = usuario;
        this.tipoFiltro = tipoFiltro;
        this.persistencia = new Usuarios("usuarios.dat");
        this.initComponents();
        this.cargarDatos();
        if (tipoFiltro != null) {
            ((TitledBorder)((JScrollPane)this.getComponent(1)).getBorder()).setTitle(tipoFiltro + "s Registrados");
        }

    }

    public PanelGestionUsuarios(Usuario usuario) {
        this(usuario, (String)null);
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel panelBotones = new JPanel(new FlowLayout(0));
        JButton[] btns = new JButton[]{new JButton("Todos"), new JButton("Agregar"), new JButton("Editar"), new JButton("Eliminar"), new JButton("Buscar")};
        btns[0].addActionListener((e) -> this.cargarDatos());
        btns[1].addActionListener((e) -> this.agregar());
        btns[2].addActionListener((e) -> this.editar());
        btns[3].addActionListener((e) -> this.eliminar());
        btns[4].addActionListener((e) -> this.buscar());

        for(JButton b : btns) {
            panelBotones.add(b);
        }

        if (this.usuarioActual != null && !this.usuarioActual.getTipo().equalsIgnoreCase("Administrador")) {
            btns[1].setEnabled(false);
            btns[2].setEnabled(false);
            btns[3].setEnabled(false);
        }

        String[] cols = new String[]{"ID Usuario", "Nombre", "Cédula", "Email", "Tipo", "Empresa", "Estado"};
        this.modelo = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        this.tabla = new JTable(this.modelo);
        this.tabla.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(this.tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Usuarios Registrados"));
        this.add(panelBotones, "North");
        this.add(scrollPane, "Center");
    }

    private void cargarDatos() {
        try {
            this.modelo.setRowCount(0);
            List<Usuario> lista = this.persistencia.lista();
            Map<String, String> mapaEmpresas = new HashMap();

            try {
                for(Empresa emp : (new Empresas("empresas.dat")).lista()) {
                    mapaEmpresas.put(emp.getIdEmpresa(), emp.getNombre());
                }
            } catch (Exception var7) {
            }

            for(int i = 0; i < lista.size(); ++i) {
                Usuario u = (Usuario)lista.get(i);
                if (!"INACTIVO".equalsIgnoreCase(u.getEstado())) {
                    if (this.tipoFiltro != null) {
                        String tipoUsuario = u.getTipo() != null ? u.getTipo().trim() : "";
                        if (!this.tipoFiltro.equalsIgnoreCase(tipoUsuario)) {
                            continue;
                        }
                    }

                    if (this.usuarioActual.getEmpresa() == null || u.getEmpresa() != null && u.getEmpresa().getIdEmpresa().equals(this.usuarioActual.getEmpresa().getIdEmpresa())) {
                        String nombreEmpresa = "N/A";
                        if (u.getEmpresa() != null) {
                            String id = u.getEmpresa().getIdEmpresa();
                            nombreEmpresa = (String)mapaEmpresas.getOrDefault(id, id);
                        }

                        this.modelo.addRow(new Object[]{u.getIdUsuario(), u.getNombre(), u.getCedula(), u.getEmail(), u.getTipo(), nombreEmpresa, u.getEstado()});
                    }
                }
            }
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }

        this.tabla.revalidate();
        this.tabla.repaint();
    }

    private void agregar() {
        String titulo = this.tipoFiltro != null ? "Agregar " + this.tipoFiltro : "Agregar Usuario";
        InterfacesUsuario.DialogoFormularioUsuario d = new InterfacesUsuario.DialogoFormularioUsuario((JFrame)SwingUtilities.getWindowAncestor(this), titulo, (Usuario)null, this.tipoFiltro, this.usuarioActual.getEmpresa());
        d.setVisible(true);
        if (d.isConfirmado()) {
            try {
                this.persistencia.agrega(d.getUsuario());
                this.cargarDatos();
                String msg = this.tipoFiltro != null ? this.tipoFiltro + " agregado correctamente" : "Usuario agregado correctamente";
                JOptionPane.showMessageDialog(this, msg);
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }

    }

    private void editar() {
        int fila = this.tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un usuario de la tabla", "Aviso", 2);
        } else {
            try {
                Usuario u = this.persistencia.obten(new Usuario((String)this.modelo.getValueAt(fila, 0)));
                String titulo = this.tipoFiltro != null ? "Editar " + this.tipoFiltro : "Editar Usuario";
                InterfacesUsuario.DialogoFormularioUsuario d = new InterfacesUsuario.DialogoFormularioUsuario((JFrame)SwingUtilities.getWindowAncestor(this), titulo, u, this.tipoFiltro, this.usuarioActual.getEmpresa());
                d.setVisible(true);
                if (d.isConfirmado()) {
                    this.persistencia.actualiza(d.getUsuario());
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
            JOptionPane.showMessageDialog(this, "Por favor seleccione un usuario de la tabla", "Aviso", 2);
        } else if (JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar este usuario?", "Confirmar eliminación", 0) == 0) {
            try {
                this.persistencia.elimina(new Usuario((String)this.modelo.getValueAt(fila, 0)));
                this.cargarDatos();
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }

        }
    }

    private void buscar() {
        String[] opciones = new String[]{"Por ID", "Por Nombre", "Por Cédula"};
        int opcion = JOptionPane.showOptionDialog(this, "Seleccione criterio:", "Buscar Usuario", -1, 3, (Icon)null, opciones, opciones[0]);
        if (opcion == 0) {
            String id = JOptionPane.showInputDialog("ID de usuario:");
            if (id != null && !id.trim().isEmpty()) {
                try {
                    Map<String, String> mapaEmpresas = new HashMap();

                    try {
                        for(Empresa emp : (new Empresas("empresas.dat")).lista()) {
                            mapaEmpresas.put(emp.getIdEmpresa(), emp.getNombre());
                        }
                    } catch (Exception var14) {
                    }

                    Usuario u = this.persistencia.obten(new Usuario(id.trim().toUpperCase()));
                    if (u != null && this.usuarioActual.getEmpresa() != null && (u.getEmpresa() == null || !u.getEmpresa().getIdEmpresa().equals(this.usuarioActual.getEmpresa().getIdEmpresa()))) {
                        u = null;
                    }

                    if (u != null) {
                        String nombreEmpresa = "N/A";
                        if (u.getEmpresa() != null) {
                            String idEmp = u.getEmpresa().getIdEmpresa();
                            nombreEmpresa = (String)mapaEmpresas.getOrDefault(idEmp, idEmp);
                        }

                        this.modelo.setRowCount(0);
                        this.modelo.addRow(new Object[]{u.getIdUsuario(), u.getNombre(), u.getCedula(), u.getEmail(), u.getTipo(), nombreEmpresa, u.getEstado()});
                    }
                } catch (PersistenciaException e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
            }
        } else if (opcion == 1 || opcion == 2) {
            String criterio = JOptionPane.showInputDialog(opcion == 1 ? "Ingrese Nombre:" : "Ingrese Cédula:");
            if (criterio != null && !criterio.trim().isEmpty()) {
                try {
                    Map<String, String> mapaEmpresas = new HashMap();

                    try {
                        for(Empresa emp : (new Empresas("empresas.dat")).lista()) {
                            mapaEmpresas.put(emp.getIdEmpresa(), emp.getNombre());
                        }
                    } catch (Exception var12) {
                    }

                    List<Usuario> lista = this.persistencia.lista();
                    this.modelo.setRowCount(0);
                    boolean encontrado = false;

                    for(Usuario u : lista) {
                        if (this.usuarioActual.getEmpresa() == null || u.getEmpresa() != null && u.getEmpresa().getIdEmpresa().equals(this.usuarioActual.getEmpresa().getIdEmpresa())) {
                            String valor = opcion == 1 ? u.getNombre() : u.getCedula();
                            if (valor.toUpperCase().contains(criterio.toUpperCase())) {
                                String nombreEmpresa = "N/A";
                                if (u.getEmpresa() != null) {
                                    String idEmp = u.getEmpresa().getIdEmpresa();
                                    nombreEmpresa = (String)mapaEmpresas.getOrDefault(idEmp, idEmp);
                                }

                                this.modelo.addRow(new Object[]{u.getIdUsuario(), u.getNombre(), u.getCedula(), u.getEmail(), u.getTipo(), nombreEmpresa, u.getEstado()});
                                encontrado = true;
                            }
                        }
                    }

                    if (!encontrado) {
                        JOptionPane.showMessageDialog(this, "No se encontraron usuarios.");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
            }
        }

    }
}
