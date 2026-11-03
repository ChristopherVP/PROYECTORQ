//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import excepciones.PersistenciaException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import objetosNegocio.Bus;
import objetosNegocio.Empresa;
import persistencia.Buses;
import persistencia.Empresas;

public class PanelGestionBuses extends JPanel {
    private JTable tablaBuses;
    private DefaultTableModel modeloTabla;
    private JButton btnTodos;
    private JButton btnAgregar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JTextField txtBuscar;
    private JCheckBox chkSoloActivos;
    private Buses persistenciaBuses = new Buses("buses.dat");
    private Empresa empresaFiltro;
    private List<Bus> listaBusesCompleta;

    public PanelGestionBuses() {
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
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 0));
        JPanel panelBotones = new JPanel(new FlowLayout(0, 5, 0));
        JPanel panelBusqueda = new JPanel(new FlowLayout(2, 5, 0));
        this.btnTodos = new JButton("Todos");
        this.btnAgregar = new JButton("Agregar");
        this.btnEditar = new JButton("Editar");
        this.btnEliminar = new JButton("Eliminar");
        this.btnTodos.addActionListener((e) -> this.cargarDatos());
        this.btnAgregar.addActionListener((e) -> this.agregarBus());
        this.btnEditar.addActionListener((e) -> this.editarBus());
        this.btnEliminar.addActionListener((e) -> this.eliminarBus());
        panelBotones.add(this.btnTodos);
        panelBotones.add(this.btnAgregar);
        panelBotones.add(this.btnEditar);
        panelBotones.add(this.btnEliminar);
        this.chkSoloActivos = new JCheckBox("Solo Activos");
        this.chkSoloActivos.setSelected(true);
        this.chkSoloActivos.addActionListener((e) -> this.filtrar());
        this.txtBuscar = new JTextField(20);
        this.txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar (Placa, Marca, ID)...");
        this.txtBuscar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                PanelGestionBuses.this.filtrar();
            }
        });
        panelBusqueda.add(this.chkSoloActivos);
        panelBusqueda.add(new JLabel("\ud83d\udd0d"));
        panelBusqueda.add(this.txtBuscar);
        panelSuperior.add(panelBotones, "West");
        panelSuperior.add(panelBusqueda, "East");
        String[] columnas = new String[]{"ID Bus", "Placa", "Marca", "Año", "Capacidad", "Empresa", "Estado"};
        this.modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.tablaBuses = new JTable(this.modeloTabla);
        this.tablaBuses.setSelectionMode(0);
        this.tablaBuses.setRowHeight(25);
        this.tablaBuses.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(this.tablaBuses);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Buses Registrados"));
        this.add(panelSuperior, "North");
        this.add(scrollPane, "Center");
    }

    private void cargarDatos() {
        try {
            this.listaBusesCompleta = this.persistenciaBuses.lista();
            this.filtrar();
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar buses: " + e.getMessage(), "Error", 0);
        }

    }

    private void filtrar() {
        this.modeloTabla.setRowCount(0);
        if (this.listaBusesCompleta != null) {
            String texto = this.txtBuscar.getText().toUpperCase().trim();
            String[] palabrasClave = texto.split("\\s+");
            boolean soloActivos = this.chkSoloActivos.isSelected();
            Map<String, String> mapaEmpresas = new HashMap();

            try {
                for(Empresa emp : (new Empresas("empresas.dat")).lista()) {
                    mapaEmpresas.put(emp.getIdEmpresa(), emp.getNombre());
                }
            } catch (Exception var8) {
            }

            for(Bus bus : this.listaBusesCompleta) {
                if ((this.empresaFiltro == null || bus.getEmpresa() != null && bus.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) && (!soloActivos || "ACTIVO".equalsIgnoreCase(bus.getEstado()))) {
                    if (!texto.isEmpty()) {
                        String datosBus = (bus.getPlaca() + " " + bus.getMarca() + " " + bus.getIdBus()).toUpperCase();
                        boolean coincideTodas = true;
                        for (String palabra : palabrasClave) {
                            if (!datosBus.contains(palabra)) {
                                coincideTodas = false;
                                break;
                            }
                        }
                        if (!coincideTodas) {
                            continue;
                        }
                    }

                    String nombreEmpresa = "N/A";
                    if (bus.getEmpresa() != null) {
                        String id = bus.getEmpresa().getIdEmpresa();
                        nombreEmpresa = (String)mapaEmpresas.getOrDefault(id, id);
                    }

                    Object[] fila = new Object[]{bus.getIdBus(), bus.getPlaca(), bus.getMarca(), bus.getAnioFabricacion(), bus.getCapacidad(), nombreEmpresa, bus.getEstado()};
                    this.modeloTabla.addRow(fila);
                }
            }

        }
    }

    private void agregarBus() {
        InterfacesUsuario.DialogoFormularioBus dialogo = new InterfacesUsuario.DialogoFormularioBus((JFrame)SwingUtilities.getWindowAncestor(this), "Agregar Bus", (Bus)null, this.empresaFiltro);
        dialogo.setVisible(true);
        if (dialogo.isConfirmado()) {
            try {
                Bus bus = dialogo.getBus();
                this.persistenciaBuses.agrega(bus);
                this.cargarDatos();
                JOptionPane.showMessageDialog(this, "Bus agregado exitosamente", "Éxito", 1);
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, "Error al agregar bus: " + e.getMessage(), "Error", 0);
            }
        }

    }

    private void editarBus() {
        int filaSeleccionada = this.tablaBuses.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un bus de la tabla", "Aviso", 2);
        } else {
            try {
                String idBus = (String)this.modeloTabla.getValueAt(filaSeleccionada, 0);
                Bus busActual = new Bus(idBus);
                busActual = this.persistenciaBuses.obten(busActual);
                if (busActual == null) {
                    JOptionPane.showMessageDialog(this, "No se encontró el bus", "Error", 0);
                    return;
                }

                InterfacesUsuario.DialogoFormularioBus dialogo = new InterfacesUsuario.DialogoFormularioBus((JFrame)SwingUtilities.getWindowAncestor(this), "Editar Bus", busActual, (Empresa)null);
                dialogo.setVisible(true);
                if (dialogo.isConfirmado()) {
                    Bus busEditado = dialogo.getBus();
                    this.persistenciaBuses.actualiza(busEditado);
                    this.cargarDatos();
                    JOptionPane.showMessageDialog(this, "Bus actualizado exitosamente", "Éxito", 1);
                }
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, "Error al editar bus: " + e.getMessage(), "Error", 0);
            }

        }
    }

    private void eliminarBus() {
        int filaSeleccionada = this.tablaBuses.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un bus de la tabla", "Aviso", 2);
        } else {
            int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar este bus?", "Confirmar eliminación", 0, 2);
            if (confirmacion == 0) {
                try {
                    String idBus = (String)this.modeloTabla.getValueAt(filaSeleccionada, 0);
                    Bus bus = new Bus(idBus);
                    this.persistenciaBuses.elimina(bus);
                    this.cargarDatos();
                    JOptionPane.showMessageDialog(this, "Bus eliminado exitosamente", "Éxito", 1);
                } catch (PersistenciaException e) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar bus: " + e.getMessage(), "Error", 0);
                }
            }

        }
    }
}
