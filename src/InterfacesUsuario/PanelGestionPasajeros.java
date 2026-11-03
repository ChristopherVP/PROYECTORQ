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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import objetosNegocio.Empresa;
import objetosNegocio.Pasajero;
import persistencia.Empresas;
import persistencia.Pasajeros;

public class PanelGestionPasajeros extends JPanel {
    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtBuscar;
    private Pasajeros persistencia = new Pasajeros("pasajeros.dat");
    private Empresa empresaFiltro;
    private List<Pasajero> listaPasajerosCompleta;

    public PanelGestionPasajeros() {
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
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

        JButton btnTodos = new JButton("Todos");
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");

        btnTodos.addActionListener((e) -> this.cargarDatos());
        btnAgregar.addActionListener((e) -> this.agregar());
        btnEditar.addActionListener((e) -> this.editar());
        btnEliminar.addActionListener((e) -> this.eliminar());

        panelBotones.add(btnTodos);
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);

        this.txtBuscar = new JTextField(20);
        this.txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por Nombre, Cédula o ID...");
        this.txtBuscar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filtrar();
            }
        });

        panelBusqueda.add(new JLabel("🔍"));
        panelBusqueda.add(this.txtBuscar);

        panelSuperior.add(panelBotones, BorderLayout.WEST);
        panelSuperior.add(panelBusqueda, BorderLayout.EAST);

        String[] cols = new String[]{"ID", "Nombre", "Cédula", "Edad", "Tipo", "Teléfono", "Email", "Empresa"};
        this.modelo = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        this.tabla = new JTable(this.modelo);
        this.tabla.setRowHeight(25);
        JScrollPane scroll = new JScrollPane(this.tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Pasajeros Registrados"));
        this.add(panelSuperior, "North");
        this.add(scroll, "Center");
    }

    private void cargarDatos() {
        try {
            this.listaPasajerosCompleta = this.persistencia.lista();
            this.filtrar();
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void filtrar() {
        this.modelo.setRowCount(0);
        if (this.listaPasajerosCompleta != null) {
            String texto = this.txtBuscar.getText().toUpperCase().trim();
            Map<String, String> mapaEmpresas = new HashMap();

            try {
                for(Empresa emp : (new Empresas("empresas.dat")).lista()) {
                    mapaEmpresas.put(emp.getIdEmpresa(), emp.getNombre());
                }
            } catch (Exception var7) {
            }

            for(Pasajero p : this.listaPasajerosCompleta) {
                if (this.empresaFiltro == null || p.getEmpresa() != null && p.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {
                    
                    if (!texto.isEmpty()) {
                        boolean coincide = p.getNombre().toUpperCase().contains(texto) || 
                                         p.getCedula().contains(texto) || 
                                         p.getIdPasajero().toUpperCase().contains(texto);
                        if (!coincide) {
                            continue;
                        }
                    }

                    String nombreEmpresa = "N/A";
                    if (p.getEmpresa() != null) {
                        String id = p.getEmpresa().getIdEmpresa();
                        nombreEmpresa = (String)mapaEmpresas.getOrDefault(id, id);
                    }

                    this.modelo.addRow(new Object[]{p.getIdPasajero(), p.getNombre(), p.getCedula(), p.calcularEdad() + " años", p.getTipoPasajero(), p.getTelefono(), p.getEmail(), nombreEmpresa});
                }
            }
        }
    }

    private void agregar() {
        InterfacesUsuario.DialogoFormularioPasajero d = new InterfacesUsuario.DialogoFormularioPasajero(SwingUtilities.getWindowAncestor(this), "Nuevo Pasajero", (Pasajero)null, this.empresaFiltro);
        d.setVisible(true);
        if (d.isConfirmado()) {
            try {
                this.persistencia.agrega(d.getPasajero());
                this.cargarDatos();
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }

    }

    private void editar() {
        int fila = this.tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un pasajero de la tabla", "Aviso", 2);
        } else {
            try {
                String id = (String)this.modelo.getValueAt(fila, 0);
                Pasajero p = this.persistencia.obten(new Pasajero(id));
                InterfacesUsuario.DialogoFormularioPasajero d = new InterfacesUsuario.DialogoFormularioPasajero(SwingUtilities.getWindowAncestor(this), "Editar Pasajero", p, (Empresa)null);
                d.setVisible(true);
                if (d.isConfirmado()) {
                    this.persistencia.actualiza(d.getPasajero());
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
            JOptionPane.showMessageDialog(this, "Por favor seleccione un pasajero de la tabla", "Aviso", 2);
        } else {
            if (JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar este pasajero?", "Confirmar eliminación", 0) == 0) {
                try {
                    this.persistencia.elimina(new Pasajero((String)this.modelo.getValueAt(fila, 0)));
                    this.cargarDatos();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
            }

        }
    }
}
