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
import objetosNegocio.Reserva;
import persistencia.Reservas;

public class DialogoBuscarReserva extends JDialog {
    private JTextField txtBuscar;
    private JTable tabla;
    private DefaultTableModel modelo;
    private Reserva reservaSeleccionada;
    private Empresa empresaFiltro;
    private List<Reserva> listaCompleta;

    public DialogoBuscarReserva(Window padre, Empresa empresaFiltro) {
        super(padre, "Buscar Reserva", ModalityType.APPLICATION_MODAL);
        this.empresaFiltro = empresaFiltro;
        this.initComponents();
        this.cargarDatos();
    }

    private void initComponents() {
        this.setSize(700, 400);
        this.setLocationRelativeTo(this.getParent());
        this.setLayout(new BorderLayout(10, 10));
        JPanel pTop = new JPanel(new BorderLayout(5, 0));
        pTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        this.txtBuscar = new JTextField();
        this.txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por ID, Pasajero o Fecha...");
        this.txtBuscar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { filtrar(); }
        });
        pTop.add(new JLabel("🔍"), "West");
        pTop.add(this.txtBuscar, "Center");
        String[] cols = new String[]{"ID", "Pasajero", "Viaje", "Fecha", "Estado"};
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
            Reservas persistencia = new Reservas("reservas.dat");
            this.listaCompleta = persistencia.lista();
            this.filtrar();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar reservas: " + e.getMessage());
        }
    }

    private void filtrar() {
        this.modelo.setRowCount(0);
        String txt = this.txtBuscar.getText().toUpperCase();
        if (this.listaCompleta != null) {
            for(Reserva r : this.listaCompleta) {
                if (this.empresaFiltro == null || (r.getEmpresa() != null && r.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa()))) {
                    String pasajero = r.getPasajero() != null ? r.getPasajero().getNombre() : "N/A";
                    String fecha = r.getFechaReserva() != null ? r.getFechaReserva().toString() : "";

                    if (r.getIdReserva().toUpperCase().contains(txt) || pasajero.toUpperCase().contains(txt) || fecha.contains(txt)) {
                        this.modelo.addRow(new Object[]{r.getIdReserva(), pasajero, r.getViaje() != null ? r.getViaje().getIdViaje() : "N/A", fecha, r.getEstado()});
                    }
                }
            }
        }
    }

    private void seleccionar() {
        int row = this.tabla.getSelectedRow();
        if (row != -1) {
            String id = (String)this.modelo.getValueAt(row, 0);
            for(Reserva r : this.listaCompleta) {
                if (r.getIdReserva().equals(id)) {
                    this.reservaSeleccionada = r;
                    this.dispose();
                    break;
                }
            }
        }
    }

    public Reserva getReservaSeleccionada() { return this.reservaSeleccionada; }
}