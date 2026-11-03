//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
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
import objetosNegocio.Ruta;
import objetosNegocio.Viaje;
import persistencia.Rutas;
import persistencia.Viajes;

public class DialogoBuscarViaje extends JDialog {
    private JTextField txtOrigen;
    private JTextField txtDestino;
    private JTextField txtFecha;
    private JTable tabla;
    private DefaultTableModel modelo;
    private Viaje viajeSeleccionado;
    private Empresa empresaFiltro;
    private List<Viaje> listaCompleta;

    public DialogoBuscarViaje(Window padre, Empresa empresaFiltro) {
        super(padre, "Buscar Viaje Disponible", ModalityType.APPLICATION_MODAL);
        this.empresaFiltro = empresaFiltro;
        this.initComponents();
        this.cargarDatos();
    }

    private void initComponents() {
        this.setSize(700, 500);
        this.setLocationRelativeTo(this.getParent());
        this.setLayout(new BorderLayout(10, 10));
        JPanel pFiltros = new JPanel(new FlowLayout(0));
        pFiltros.setBorder(BorderFactory.createTitledBorder("Filtros de Búsqueda"));
        this.txtOrigen = new JTextField(15);
        this.txtOrigen.putClientProperty("JTextField.placeholderText", "Origen...");
        this.txtOrigen.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                DialogoBuscarViaje.this.filtrar();
            }
        });
        this.txtDestino = new JTextField(15);
        this.txtDestino.putClientProperty("JTextField.placeholderText", "Destino...");
        this.txtDestino.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                DialogoBuscarViaje.this.filtrar();
            }
        });
        this.txtFecha = new JTextField(10);
        this.txtFecha.putClientProperty("JTextField.placeholderText", "Fecha...");
        this.txtFecha.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                DialogoBuscarViaje.this.filtrar();
            }
        });
        pFiltros.add(new JLabel("Origen:"));
        pFiltros.add(this.txtOrigen);
        pFiltros.add(new JLabel("Destino:"));
        pFiltros.add(this.txtDestino);
        pFiltros.add(new JLabel("Fecha:"));
        pFiltros.add(this.txtFecha);
        String[] cols = new String[]{"ID", "Ruta", "Fecha", "Hora", "Asientos", "Precio"};
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
                    DialogoBuscarViaje.this.seleccionar();
                }

            }
        });
        JScrollPane scroll = new JScrollPane(this.tabla);
        JButton btnSelect = new JButton("Seleccionar Viaje");
        btnSelect.addActionListener((e) -> this.seleccionar());
        JPanel pBot = new JPanel();
        pBot.add(btnSelect);
        this.add(pFiltros, "North");
        this.add(scroll, "Center");
        this.add(pBot, "South");
    }

    private void cargarDatos() {
        try {
            Viajes persistencia = new Viajes("viajes.dat");
            this.listaCompleta = persistencia.lista();
            this.filtrar();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar viajes: " + e.getMessage());
        }

    }

    private void filtrar() {
        this.modelo.setRowCount(0);
        String orig = this.txtOrigen.getText().toUpperCase();
        String dest = this.txtDestino.getText().toUpperCase();
        String fechaBusqueda = this.txtFecha.getText().trim();
        if (this.listaCompleta != null) {
            for(Viaje v : this.listaCompleta) {
                if ((this.empresaFiltro == null || v.getEmpresa() != null && v.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) && "Programado".equalsIgnoreCase(v.getEstado()) && v.getAsientosDisponibles() > 0) {
                    String rOrigen = "";
                    String rDestino = "";
                    if (v.getRuta() != null) {
                        try {
                            if (v.getRuta().getOrigen() == null) {
                                Rutas rutasDB = new Rutas("rutas.dat");
                                Ruta rFull = rutasDB.obten(v.getRuta());
                                if (rFull != null) {
                                    v.setRuta(rFull);
                                }
                            }

                            if (v.getRuta().getOrigen() != null) {
                                rOrigen = v.getRuta().getOrigen().toUpperCase();
                            }

                            if (v.getRuta().getDestino() != null) {
                                rDestino = v.getRuta().getDestino().toUpperCase();
                            }
                        } catch (Exception var9) {
                        }
                    }

                    String fechaViaje = v.getFechaSalida() != null ? v.getFechaSalida().toString() : "";
                    boolean coincideFecha = fechaBusqueda.isEmpty() || fechaViaje.contains(fechaBusqueda);

                    if (rOrigen.contains(orig) && rDestino.contains(dest) && coincideFecha) {
                        String rutaStr = rOrigen + " - " + rDestino;
                        String fecha = v.getFechaSalida() != null ? v.getFechaSalida().toString() : "";
                        this.modelo.addRow(new Object[]{v.getIdViaje(), rutaStr, fecha, v.getHoraSalida(), v.getAsientosDisponibles(), v.getPrecio()});
                    }
                }
            }

        }
    }

    private void seleccionar() {
        int row = this.tabla.getSelectedRow();
        if (row != -1) {
            String id = (String)this.modelo.getValueAt(row, 0);

            for(Viaje v : this.listaCompleta) {
                if (v.getIdViaje().equals(id)) {
                    this.viajeSeleccionado = v;
                    this.dispose();
                    break;
                }
            }
        }

    }

    public Viaje getViajeSeleccionado() {
        return this.viajeSeleccionado;
    }
}
