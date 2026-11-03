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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import objetosNegocio.Empresa;
import objetosNegocio.Ruta;
import persistencia.Empresas;
import persistencia.Rutas;

public class PanelGestionRutas extends JPanel {
    private JTable tabla;
    private DefaultTableModel modelo;
    private JButton btnTodos;
    private JButton btnAgregar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JTextField txtBuscar;
    private Rutas persistencia = new Rutas("rutas.dat");
    private Empresa empresaFiltro;
    private List<Ruta> listaRutasCompleta;

    public PanelGestionRutas() {
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
        this.btnAgregar.addActionListener((e) -> this.agregarRuta());
        this.btnEditar.addActionListener((e) -> this.editarRuta());
        this.btnEliminar.addActionListener((e) -> this.eliminarRuta());
        panelBotones.add(this.btnTodos);
        panelBotones.add(this.btnAgregar);
        panelBotones.add(this.btnEditar);
        panelBotones.add(this.btnEliminar);
        this.txtBuscar = new JTextField(25);
        this.txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar (ej: 'Qui Gua' para Quito-Guayaquil)...");
        this.txtBuscar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                PanelGestionRutas.this.filtrar();
            }
        });
        panelBusqueda.add(new JLabel("\ud83d\udd0d"));
        panelBusqueda.add(this.txtBuscar);
        panelSuperior.add(panelBotones, "West");
        panelSuperior.add(panelBusqueda, "East");
        String[] columnas = new String[]{"ID Ruta", "Origen", "Destino", "Distancia (km)", "Precio", "Empresa", "Estado"};
        this.modelo = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.tabla = new JTable(this.modelo);
        this.tabla.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(this.tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Rutas Registradas"));
        this.add(panelSuperior, "North");
        this.add(scrollPane, "Center");
    }

    private void cargarDatos() {
        try {
            this.listaRutasCompleta = this.persistencia.lista();
            this.filtrar();
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }

    }

    private void filtrar() {
        this.modelo.setRowCount(0);
        if (this.listaRutasCompleta != null) {
            String texto = this.txtBuscar.getText().toUpperCase().trim();
            String[] palabrasClave = texto.split("\\s+");
            Map<String, String> mapaEmpresas = new HashMap();

            try {
                for(Empresa emp : (new Empresas("empresas.dat")).lista()) {
                    mapaEmpresas.put(emp.getIdEmpresa(), emp.getNombre());
                }
            } catch (Exception var12) {
            }

            for(Ruta r : this.listaRutasCompleta) {
                if (this.empresaFiltro == null || r.getEmpresa() != null && r.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {
                    boolean coincide = true;
                    String var10000 = r.getOrigen();
                    String rutaString = (var10000 + " " + r.getDestino() + " " + r.getIdRuta()).toUpperCase();

                    for(String palabra : palabrasClave) {
                        if (!rutaString.contains(palabra)) {
                            coincide = false;
                            break;
                        }
                    }

                    if (coincide) {
                        String nombreEmpresa = "N/A";
                        if (r.getEmpresa() != null) {
                            String id = r.getEmpresa().getIdEmpresa();
                            nombreEmpresa = (String)mapaEmpresas.getOrDefault(id, id);
                        }

                        this.modelo.addRow(new Object[]{r.getIdRuta(), r.getOrigen(), r.getDestino(), r.getDistancia(), r.getPrecio(), nombreEmpresa, r.getEstado()});
                    }
                }
            }

        }
    }

    private void agregarRuta() {
        InterfacesUsuario.DialogoFormularioRuta dialogo = new InterfacesUsuario.DialogoFormularioRuta((JFrame)SwingUtilities.getWindowAncestor(this), "Agregar Ruta", (Ruta)null, this.empresaFiltro);
        dialogo.setVisible(true);
        if (dialogo.isConfirmado()) {
            try {
                this.persistencia.agrega(dialogo.getRuta());
                this.cargarDatos();
                JOptionPane.showMessageDialog(this, "Ruta agregada exitosamente");
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }

    }

    private void editarRuta() {
        int fila = this.tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una ruta de la tabla", "Aviso", 2);
        } else {
            try {
                String id = (String)this.modelo.getValueAt(fila, 0);
                Ruta ruta = this.persistencia.obten(new Ruta(id));
                InterfacesUsuario.DialogoFormularioRuta dialogo = new InterfacesUsuario.DialogoFormularioRuta((JFrame)SwingUtilities.getWindowAncestor(this), "Editar Ruta", ruta, (Empresa)null);
                dialogo.setVisible(true);
                if (dialogo.isConfirmado()) {
                    this.persistencia.actualiza(dialogo.getRuta());
                    this.cargarDatos();
                    JOptionPane.showMessageDialog(this, "Ruta actualizada");
                }
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }

        }
    }

    private void eliminarRuta() {
        int fila = this.tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una ruta de la tabla", "Aviso", 2);
        } else {
            if (JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar esta ruta?", "Confirmar eliminación", 0) == 0) {
                try {
                    this.persistencia.elimina(new Ruta((String)this.modelo.getValueAt(fila, 0)));
                    this.cargarDatos();
                } catch (PersistenciaException e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
            }

        }
    }
}
