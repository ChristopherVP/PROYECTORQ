//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import control.ControladorConductores;
import control.ResultadoOperacion;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import objetosNegocio.Conductor;
import objetosNegocio.Empresa;
import persistencia.Empresas;

public class PanelGestionConductores extends JPanel {
    private JTable tabla;
    private DefaultTableModel modelo;
    private ControladorConductores controlador = new ControladorConductores();
    private Empresa empresaFiltro;

    public PanelGestionConductores() {
        this.initComponents();
        this.cargarDatos();
    }

    public void setEmpresaFiltro(Empresa empresa) {
        this.empresaFiltro = empresa;
        this.cargarDatos();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel panelBotones = new JPanel(new FlowLayout(0));
        JButton btnTodos = new JButton("Todos");
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnBuscar = new JButton("Buscar");
        btnTodos.addActionListener((e) -> this.cargarDatos());
        btnAgregar.addActionListener((e) -> this.agregar());
        btnEditar.addActionListener((e) -> this.editar());
        btnEliminar.addActionListener((e) -> this.eliminar());
        btnBuscar.addActionListener((e) -> this.buscar());
        panelBotones.add(btnTodos);
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnBuscar);
        String[] cols = new String[]{"ID", "Nombre", "Cédula", "Licencia", "Teléfono", "Empresa", "Estado"};
        this.modelo = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        this.tabla = new JTable(this.modelo);
        this.tabla.setRowHeight(25);
        JScrollPane scroll = new JScrollPane(this.tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Conductores Registrados"));
        this.add(panelBotones, "North");
        this.add(scroll, "Center");
    }

    private void cargarDatos() {
        this.modelo.setRowCount(0);
        ResultadoOperacion res = this.controlador.listarConductores();
        if (res.isExito()) {
            List<Conductor> lista = (List)res.getDatos();
            Map<String, String> mapaEmpresas = new HashMap();

            try {
                for(Empresa emp : (new Empresas("empresas.dat")).lista()) {
                    mapaEmpresas.put(emp.getIdEmpresa(), emp.getNombre());
                }
            } catch (Exception var8) {
            }

            for(Conductor c : lista) {
                if (!"INACTIVO".equalsIgnoreCase(c.getEstado()) && (this.empresaFiltro == null || c.getEmpresa() != null && c.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa()))) {
                    String nombreEmpresa = "N/A";
                    if (c.getEmpresa() != null) {
                        String id = c.getEmpresa().getIdEmpresa();
                        nombreEmpresa = (String)mapaEmpresas.getOrDefault(id, id);
                    }

                    this.modelo.addRow(new Object[]{c.getIdConductor(), c.getNombre(), c.getCedula(), c.getLicencia(), c.getTelefono(), nombreEmpresa, c.getEstado()});
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error: " + res.getMensaje());
        }

    }

    private void agregar() {
        InterfacesUsuario.DialogoFormularioConductor d = new InterfacesUsuario.DialogoFormularioConductor(SwingUtilities.getWindowAncestor(this), "Nuevo Conductor", (Conductor)null, this.empresaFiltro);
        d.setVisible(true);
        if (d.isConfirmado()) {
            Conductor c = d.getConductor();
            ResultadoOperacion res = this.controlador.agregarConductor(c.getNombre(), c.getCedula(), c.getTelefono(), c.getLicencia(), c.getEmpresa().getIdEmpresa());
            if (res.isExito()) {
                this.cargarDatos();
                JOptionPane.showMessageDialog(this, "Conductor agregado exitosamente");
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + res.getMensaje());
            }
        }

    }

    private void editar() {
        int fila = this.tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un conductor", "Aviso", 2);
        } else {
            try {
                String id = (String)this.modelo.getValueAt(fila, 0);
                Conductor c = this.controlador.buscarConductor(id);
                InterfacesUsuario.DialogoFormularioConductor d = new InterfacesUsuario.DialogoFormularioConductor(SwingUtilities.getWindowAncestor(this), "Editar Conductor", c, (Empresa)null);
                d.setVisible(true);
                if (d.isConfirmado()) {
                    Conductor mod = d.getConductor();
                    this.controlador.actualizarConductor(mod.getIdConductor(), mod.getNombre(), mod.getCedula(), mod.getTelefono(), mod.getLicencia(), mod.getEmpresa().getIdEmpresa(), mod.getEstado());
                    this.cargarDatos();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }

        }
    }

    private void eliminar() {
        int fila = this.tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un conductor", "Aviso", 2);
        } else {
            if (JOptionPane.showConfirmDialog(this, "¿Eliminar conductor?", "Confirmar", 0) == 0) {
                String id = (String)this.modelo.getValueAt(fila, 0);
                this.controlador.eliminarConductor(id);
                this.cargarDatos();
            }

        }
    }

    private void buscar() {
        String[] opciones = new String[]{"Por ID", "Por Nombre", "Por Cédula"};
        int opcion = JOptionPane.showOptionDialog(this, "Seleccione criterio:", "Buscar Conductor", -1, 3, (Icon)null, opciones, opciones[0]);
        if (opcion == 0) {
            String id = JOptionPane.showInputDialog("ID de conductor:");
            if (id != null && !id.trim().isEmpty()) {
                try {
                    Conductor c = this.controlador.buscarConductor(id.trim().toUpperCase());
                    if (c != null && this.empresaFiltro != null && (c.getEmpresa() == null || !c.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa()))) {
                        c = null;
                    }

                    if (c != null) {
                        String nombreEmpresa = "N/A";
                        if (c.getEmpresa() != null) {
                            try {
                                Empresas daoEmp = new Empresas("empresas.dat");
                                Empresa emp = daoEmp.obten(c.getEmpresa());
                                if (emp != null) {
                                    nombreEmpresa = emp.getNombre();
                                } else {
                                    nombreEmpresa = c.getEmpresa().getIdEmpresa();
                                }
                            } catch (Exception var13) {
                                nombreEmpresa = c.getEmpresa().getIdEmpresa();
                            }
                        }

                        this.modelo.setRowCount(0);
                        this.modelo.addRow(new Object[]{c.getIdConductor(), c.getNombre(), c.getCedula(), c.getLicencia(), c.getTelefono(), nombreEmpresa, c.getEstado()});
                    } else {
                        JOptionPane.showMessageDialog(this, "Conductor no encontrado");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
            }
        } else if (opcion == 1 || opcion == 2) {
            String criterio = JOptionPane.showInputDialog(opcion == 1 ? "Ingrese Nombre:" : "Ingrese Cédula:");
            if (criterio != null && !criterio.trim().isEmpty()) {
                ResultadoOperacion res = this.controlador.listarConductores();
                if (res.isExito()) {
                    Map<String, String> mapaEmpresas = new HashMap();

                    try {
                        for(Empresa emp : (new Empresas("empresas.dat")).lista()) {
                            mapaEmpresas.put(emp.getIdEmpresa(), emp.getNombre());
                        }
                    } catch (Exception var14) {
                    }

                    List<Conductor> lista = (List)res.getDatos();
                    this.modelo.setRowCount(0);
                    boolean encontrado = false;

                    for(Conductor c : lista) {
                        if (this.empresaFiltro == null || c.getEmpresa() != null && c.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {
                            String valor = opcion == 1 ? c.getNombre() : c.getCedula();
                            if (valor.toUpperCase().contains(criterio.toUpperCase())) {
                                String nombreEmpresa = "N/A";
                                if (c.getEmpresa() != null) {
                                    String id = c.getEmpresa().getIdEmpresa();
                                    nombreEmpresa = (String)mapaEmpresas.getOrDefault(id, id);
                                }

                                this.modelo.addRow(new Object[]{c.getIdConductor(), c.getNombre(), c.getCedula(), c.getLicencia(), c.getTelefono(), nombreEmpresa, c.getEstado()});
                                encontrado = true;
                            }
                        }
                    }

                    if (!encontrado) {
                        JOptionPane.showMessageDialog(this, "No se encontraron conductores.");
                    }
                }
            }
        }

    }
}
