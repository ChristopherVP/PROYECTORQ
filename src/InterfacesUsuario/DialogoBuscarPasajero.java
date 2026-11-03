//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

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
import objetosNegocio.Empresa;
import objetosNegocio.Pasajero;
import persistencia.Pasajeros;

public class DialogoBuscarPasajero extends JDialog {
    private JTextField txtBuscar;
    private JTable tabla;
    private DefaultTableModel modelo;
    private Pasajero pasajeroSeleccionado;
    private Empresa empresaFiltro;
    private List<Pasajero> listaCompleta;

    public DialogoBuscarPasajero(Window padre, Empresa empresaFiltro) {
        super(padre, "Buscar Pasajero", ModalityType.APPLICATION_MODAL);
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
        this.txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por Cédula o Nombre...");
        this.txtBuscar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                DialogoBuscarPasajero.this.filtrar();
            }
        });
        pTop.add(new JLabel("\ud83d\udd0d"), "West");
        pTop.add(this.txtBuscar, "Center");
        String[] cols = new String[]{"ID", "Nombre", "Cédula", "Teléfono"};
        this.modelo = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        this.tabla = new JTable(this.modelo);
        this.tabla.setSelectionMode(0);
        this.tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DialogoBuscarPasajero.this.seleccionar();
                }

            }
        });
        JScrollPane scroll = new JScrollPane(this.tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        JButton btnSelect = new JButton("Seleccionar");
        btnSelect.addActionListener((e) -> this.seleccionar());
        JPanel pBot = new JPanel();
        pBot.add(btnSelect);
        this.add(pTop, "North");
        this.add(scroll, "Center");
        this.add(pBot, "South");
    }

    private void cargarDatos() {
        try {
            Pasajeros persistencia = new Pasajeros("pasajeros.dat");
            this.listaCompleta = persistencia.lista();
            this.filtrar();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar pasajeros: " + e.getMessage());
        }

    }

    private void filtrar() {
        this.modelo.setRowCount(0);
        String txt = this.txtBuscar.getText().toUpperCase();
        if (this.listaCompleta != null) {
            for(Pasajero p : this.listaCompleta) {
                if ((this.empresaFiltro == null || p.getEmpresa() != null && p.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) && (p.getNombre().toUpperCase().contains(txt) || p.getCedula().contains(txt))) {
                    this.modelo.addRow(new Object[]{p.getIdPasajero(), p.getNombre(), p.getCedula(), p.getTelefono()});
                }
            }

        }
    }

    private void seleccionar() {
        int row = this.tabla.getSelectedRow();
        if (row != -1) {
            String id = (String)this.modelo.getValueAt(row, 0);

            for(Pasajero p : this.listaCompleta) {
                if (p.getIdPasajero().equals(id)) {
                    this.pasajeroSeleccionado = p;
                    this.dispose();
                    break;
                }
            }
        }

    }

    public Pasajero getPasajeroSeleccionado() {
        return this.pasajeroSeleccionado;
    }
}
