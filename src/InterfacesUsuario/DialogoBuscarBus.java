package InterfacesUsuario;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import objetosNegocio.Bus;
import objetosNegocio.Empresa;
import persistencia.Buses;

public class DialogoBuscarBus extends JDialog {
    private JTextField txtBuscar;
    private JTable tabla;
    private DefaultTableModel modelo;
    private Bus busSeleccionado;
    private Empresa empresaFiltro;
    private List<Bus> listaCompleta;

    public DialogoBuscarBus(Window padre, Empresa empresaFiltro) {
        super(padre, "Buscar Bus", ModalityType.APPLICATION_MODAL);
        this.empresaFiltro = empresaFiltro;
        this.initComponents();
        this.cargarDatos();
    }

    private void initComponents() {
        this.setSize(600, 400);
        this.setLocationRelativeTo(this.getParent());
        this.setLayout(new BorderLayout(10, 10));
        JPanel pTop = new JPanel(new BorderLayout(5, 0));
        pTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        this.txtBuscar = new JTextField();
        this.txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por Placa, Marca o ID...");
        this.txtBuscar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filtrar();
            }
        });
        pTop.add(new JLabel("🔍"), "West");
        pTop.add(this.txtBuscar, "Center");
        String[] cols = new String[]{"ID", "Placa", "Marca", "Capacidad", "Estado"};
        this.modelo = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        this.tabla = new JTable(this.modelo);
        this.tabla.setSelectionMode(0);
        this.tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) seleccionar();
            }
        });
        JScrollPane scroll = new JScrollPane(this.tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        JButton btnSelect = new JButton("Seleccionar");
        btnSelect.addActionListener((e) -> seleccionar());
        JPanel pBot = new JPanel();
        pBot.add(btnSelect);
        this.add(pTop, "North");
        this.add(scroll, "Center");
        this.add(pBot, "South");
    }

    private void cargarDatos() {
        try {
            Buses persistencia = new Buses("buses.dat");
            this.listaCompleta = persistencia.lista();
            this.filtrar();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar buses: " + e.getMessage());
        }
    }

    private void filtrar() {
        this.modelo.setRowCount(0);
        String txt = this.txtBuscar.getText().toUpperCase();
        if (this.listaCompleta != null) {
            for(Bus b : this.listaCompleta) {
                if (this.empresaFiltro == null || (b.getEmpresa() != null && b.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa()))) {
                    if (b.getPlaca().toUpperCase().contains(txt) || b.getMarca().toUpperCase().contains(txt) || b.getIdBus().toUpperCase().contains(txt)) {
                        this.modelo.addRow(new Object[]{b.getIdBus(), b.getPlaca(), b.getMarca(), b.getCapacidad(), b.getEstado()});
                    }
                }
            }
        }
    }

    private void seleccionar() {
        int row = this.tabla.getSelectedRow();
        if (row != -1) {
            String id = (String)this.modelo.getValueAt(row, 0);
            for(Bus b : this.listaCompleta) {
                if (b.getIdBus().equals(id)) {
                    this.busSeleccionado = b;
                    this.dispose();
                    break;
                }
            }
        }
    }

    public Bus getBusSeleccionado() { return this.busSeleccionado; }
}