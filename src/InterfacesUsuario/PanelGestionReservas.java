//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import control.ControladorReservas;
import control.ResultadoOperacion;
import fachadas.FachadaArchivos;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.Icon;
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
import objetosNegocio.Asiento;
import objetosNegocio.Empresa;
import objetosNegocio.Reserva;
import objetosNegocio.Ruta;
import objetosNegocio.Viaje;
import persistencia.Asientos;
import persistencia.Empresas;
import persistencia.Rutas;
import persistencia.Viajes;

public class PanelGestionReservas extends JPanel {
    private JTable tabla;
    private DefaultTableModel modelo;
    private Empresa empresaFiltro;
    private ControladorReservas controlador = new ControladorReservas();
    private JTextField txtBuscar;
    private List<Reserva> listaReservasCompleta;

    public PanelGestionReservas() {
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

        JButton[] btns = new JButton[]{new JButton("Todos"), new JButton("Agregar"), new JButton("Editar"), new JButton("Eliminar"), new JButton("Pagar"), new JButton("Abordar"), new JButton("Completar")};
        btns[0].addActionListener((e) -> this.cargarDatos());
        btns[1].addActionListener((e) -> this.agregar());
        btns[2].addActionListener((e) -> this.editar());
        btns[3].addActionListener((e) -> this.eliminar());
        btns[4].addActionListener((e) -> this.pagar());
        btns[5].addActionListener((e) -> this.abordar());
        btns[6].addActionListener((e) -> this.completar());

        for(JButton b : btns) {
            panelBotones.add(b);
        }

        this.txtBuscar = new JTextField(20);
        this.txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar (Pasajero, Origen, Destino)...");
        this.txtBuscar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filtrar();
            }
        });

        panelBusqueda.add(new JLabel("🔍"));
        panelBusqueda.add(this.txtBuscar);

        panelSuperior.add(panelBotones, BorderLayout.WEST);
        panelSuperior.add(panelBusqueda, BorderLayout.EAST);

        String[] cols = new String[]{"ID", "Empresa", "Pasajero", "Origen", "Destino", "Fecha Viaje", "Asientos", "Estado"};
        this.modelo = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        this.tabla = new JTable(this.modelo);
        this.tabla.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(this.tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Reservas Registradas"));
        this.add(panelSuperior, "North");
        this.add(scrollPane, "Center");
    }

    private void cargarDatos() {
        try {
            this.listaReservasCompleta = FachadaArchivos.getInstance().consultaReservas();
            this.filtrar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void filtrar() {
        this.modelo.setRowCount(0);
        if (this.listaReservasCompleta != null) {
            String texto = this.txtBuscar.getText().toUpperCase().trim();
            String[] palabrasClave = texto.split("\\s+");

            Map<String, String> mapaEmpresas = new HashMap();
            Map<String, String> mapaOrigenes = new HashMap();
            Map<String, String> mapaDestinos = new HashMap();
            Map<String, String> mapaFechasViaje = new HashMap();
            Map<String, List<Integer>> mapaAsientosReserva = new HashMap();

            try {
                for(Empresa e : (new Empresas("empresas.dat")).lista()) {
                    mapaEmpresas.put(e.getIdEmpresa(), e.getNombre());
                }

                List<Viaje> lViajes = (new Viajes("viajes.dat")).lista();
                List<Ruta> lRutas = (new Rutas("rutas.dat")).lista();
                Map<String, String> rutaOrigen = new HashMap();
                Map<String, String> rutaDestino = new HashMap();

                for(Ruta r : lRutas) {
                    rutaOrigen.put(r.getIdRuta(), r.getOrigen());
                    rutaDestino.put(r.getIdRuta(), r.getDestino());
                }

                for(Viaje v : lViajes) {
                    if (v.getRuta() != null) {
                        mapaOrigenes.put(v.getIdViaje(), rutaOrigen.getOrDefault(v.getRuta().getIdRuta(), "N/A"));
                        mapaDestinos.put(v.getIdViaje(), rutaDestino.getOrDefault(v.getRuta().getIdRuta(), "N/A"));
                    }

                    if (v.getFechaSalida() != null) {
                        mapaFechasViaje.put(v.getIdViaje(), v.getFechaSalida().toString());
                    }
                }

                for(Asiento a : (new Asientos("asientos.dat")).lista()) {
                    if (a.getIdReserva() != null && !a.getIdReserva().trim().isEmpty()) {
                        String idRes = a.getIdReserva().trim();
                        mapaAsientosReserva.putIfAbsent(idRes, new ArrayList());
                        ((List)mapaAsientosReserva.get(idRes)).add(a.getNumeroAsiento());
                    }
                }
            } catch (Exception var14) {
            }

            for(Reserva r : this.listaReservasCompleta) {
                if (this.empresaFiltro == null || r.getEmpresa() != null && r.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {

                    String origen = "N/A";
                    String destino = "N/A";
                    String fechaViaje = "N/A";

                    if (r.getViaje() != null) {
                        String idV = r.getViaje().getIdViaje();
                        origen = mapaOrigenes.getOrDefault(idV, "N/A");
                        destino = mapaDestinos.getOrDefault(idV, "N/A");
                        fechaViaje = mapaFechasViaje.getOrDefault(idV, "N/A");
                    }

                    String nombrePasajero = r.getPasajero() != null ? r.getPasajero().getNombre() : "N/A";

                    if (!texto.isEmpty()) {
                        String datos = (r.getIdReserva() + " " + nombrePasajero + " " + origen + " " + destino + " " + fechaViaje).toUpperCase();
                        boolean coincide = true;
                        for (String palabra : palabrasClave) {
                            if (!datos.contains(palabra)) {
                                coincide = false;
                                break;
                            }
                        }
                        if (!coincide) continue;
                    }

                    String nombreEmpresa = "N/A";
                    if (r.getEmpresa() != null) {
                        String id = r.getEmpresa().getIdEmpresa();
                        nombreEmpresa = (String)mapaEmpresas.getOrDefault(id, id);
                    }

                    String infoAsientos = String.valueOf(r.getCantidadAsientos());
                    if (mapaAsientosReserva.containsKey(r.getIdReserva())) {
                        List<Integer> nums = (List)mapaAsientosReserva.get(r.getIdReserva());
                        Collections.sort(nums);
                        infoAsientos = infoAsientos + " [" + nums.toString().replace("[", "").replace("]", "") + "]";
                    }

                    this.modelo.addRow(new Object[]{r.getIdReserva(), nombreEmpresa, nombrePasajero, origen, destino, fechaViaje, infoAsientos, r.getEstado()});
                }
            }
        }
    }

    private void agregar() {
        InterfacesUsuario.DialogoFormularioReserva d = new InterfacesUsuario.DialogoFormularioReserva((JFrame)SwingUtilities.getWindowAncestor(this), (Reserva)null, this.empresaFiltro);
        d.setVisible(true);
        if (d.isConfirmado()) {
            this.cargarDatos();
        }

    }

    private void editar() {
        int fila = this.tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una reserva de la tabla", "Aviso", 2);
        } else {
            try {
                String idReserva = (String)this.modelo.getValueAt(fila, 0);
                Reserva reserva = FachadaArchivos.getInstance().obten(new Reserva(idReserva));
                InterfacesUsuario.DialogoFormularioReserva d = new InterfacesUsuario.DialogoFormularioReserva((JFrame)SwingUtilities.getWindowAncestor(this), reserva, this.empresaFiltro);
                d.setVisible(true);
                if (d.isConfirmado()) {
                    this.cargarDatos();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al cargar reserva para edición: " + e.getMessage());
            }

        }
    }

    private void eliminar() {
        int fila = this.tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una reserva de la tabla", "Aviso", 2);
        } else if (JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar esta reserva?", "Confirmar eliminación", 0) == 0) {
            try {
                String idReserva = (String)this.modelo.getValueAt(fila, 0);
                ResultadoOperacion res = this.controlador.eliminarReserva(idReserva);
                if (res.isExito()) {
                    this.cargarDatos();
                    JOptionPane.showMessageDialog(this, res.getMensaje());
                } else {
                    JOptionPane.showMessageDialog(this, res.getMensaje(), "Error", 0);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error inesperado: " + e.getMessage());
            }

        }
    }

    private void pagar() {
        this.procesarCambioEstado("Pagar Reserva", (id) -> this.controlador.pagarReserva(id));
    }

    private void abordar() {
        this.procesarCambioEstado("Abordar Bus", (id) -> this.controlador.abordarReserva(id));
    }

    private void completar() {
        this.procesarCambioEstado("Completar Viaje", (id) -> this.controlador.completarReserva(id));
    }

    private void procesarCambioEstado(String accion, Function<String, ResultadoOperacion> operacion) {
        int fila = this.tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una reserva para " + accion, "Aviso", 2);
        } else {
            String idReserva = (String)this.modelo.getValueAt(fila, 0);
            String estadoActual = (String)this.modelo.getValueAt(fila, 6);
            int confirm = JOptionPane.showConfirmDialog(this, "¿Desea " + accion + "?\nID: " + idReserva + "\nEstado actual: " + estadoActual, "Confirmar " + accion, 0);
            if (confirm == 0) {
                try {
                    ResultadoOperacion res = (ResultadoOperacion)operacion.apply(idReserva);
                    if (res.isExito()) {
                        JOptionPane.showMessageDialog(this, res.getMensaje(), "Éxito", 1);
                        this.cargarDatos();
                    } else {
                        JOptionPane.showMessageDialog(this, res.getMensaje(), "Error", 0);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error inesperado: " + e.getMessage());
                }
            }

        }
    }

    private void buscar() {
        String[] opciones = new String[]{"Por ID Reserva", "Por Nombre Pasajero"};
        int opcion = JOptionPane.showOptionDialog(this, "Seleccione criterio:", "Buscar Reserva", -1, 3, (Icon)null, opciones, opciones[0]);
        if (opcion == 0) {
            String id = JOptionPane.showInputDialog("ID de reserva:");
            if (id != null && !id.trim().isEmpty()) {
                try {
                    Reserva r = FachadaArchivos.getInstance().obten(new Reserva(id.trim().toUpperCase()));
                    if (r != null && this.empresaFiltro != null && (r.getEmpresa() == null || !r.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa()))) {
                        r = null;
                    }

                    if (r != null) {
                        String nombreEmpresa = "N/A";
                        String rutaViaje = "N/A";

                        try {
                            if (r.getEmpresa() != null) {
                                Empresa e = (new Empresas("empresas.dat")).obten(r.getEmpresa());
                                if (e != null) {
                                    nombreEmpresa = e.getNombre();
                                } else {
                                    nombreEmpresa = r.getEmpresa().getIdEmpresa();
                                }
                            }

                            if (r.getViaje() != null) {
                                Viaje v = (new Viajes("viajes.dat")).obten(r.getViaje());
                                if (v != null) {
                                    if (v.getRuta() != null) {
                                        Ruta ruta = (new Rutas("rutas.dat")).obten(v.getRuta());
                                        if (ruta != null) {
                                            String var10000 = ruta.getOrigen();
                                            rutaViaje = var10000 + " - " + ruta.getDestino();
                                        } else {
                                            rutaViaje = v.getRuta().getIdRuta();
                                        }
                                    }
                                } else {
                                    rutaViaje = r.getViaje().getIdViaje();
                                }
                            }
                        } catch (Exception var17) {
                        }

                        String infoAsientos = String.valueOf(r.getCantidadAsientos());

                        try {
                            List<Asiento> allSeats = (new Asientos("asientos.dat")).lista();
                            List<Integer> nums = new ArrayList();

                            for(Asiento a : allSeats) {
                                if (a.getIdReserva() != null && a.getIdReserva().trim().equalsIgnoreCase(r.getIdReserva().trim())) {
                                    nums.add(a.getNumeroAsiento());
                                }
                            }

                            Collections.sort(nums);
                            if (!nums.isEmpty()) {
                                infoAsientos = infoAsientos + " [" + nums.toString().replace("[", "").replace("]", "") + "]";
                            }
                        } catch (Exception var20) {
                        }

                        this.modelo.setRowCount(0);
                        String fechaReserva = "";

                        try {
                            if (r.getFechaReserva() != null) {
                                fechaReserva = r.getFechaReserva().toString();
                            }
                        } catch (Exception var16) {
                            fechaReserva = "Formato incorrecto";
                        }

                        this.modelo.addRow(new Object[]{r.getIdReserva(), nombreEmpresa, r.getPasajero() != null ? r.getPasajero().getNombre() : "N/A", rutaViaje, infoAsientos, fechaReserva, r.getEstado()});
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
            }
        } else if (opcion == 1) {
            String nombre = JOptionPane.showInputDialog("Ingrese Nombre del Pasajero:");
            if (nombre != null && !nombre.trim().isEmpty()) {
                try {
                    Map<String, String> mapaEmpresas = new HashMap();
                    Map<String, String> mapaRutasViaje = new HashMap();
                    Map<String, List<Integer>> mapaAsientosReserva = new HashMap();

                    try {
                        for(Empresa e : (new Empresas("empresas.dat")).lista()) {
                            mapaEmpresas.put(e.getIdEmpresa(), e.getNombre());
                        }

                        List<Viaje> lViajes = (new Viajes("viajes.dat")).lista();
                        List<Ruta> lRutas = (new Rutas("rutas.dat")).lista();
                        Map<String, String> mapaRutas = new HashMap();

                        for(Ruta r : lRutas) {
                            String var10001 = r.getIdRuta();
                            String var10002 = r.getOrigen();
                            mapaRutas.put(var10001, var10002 + " - " + r.getDestino());
                        }

                        for(Viaje v : lViajes) {
                            String nombreRuta = "N/A";
                            if (v.getRuta() != null) {
                                nombreRuta = (String)mapaRutas.getOrDefault(v.getRuta().getIdRuta(), v.getRuta().getIdRuta());
                            }

                            mapaRutasViaje.put(v.getIdViaje(), nombreRuta);
                        }

                        for(Asiento a : (new Asientos("asientos.dat")).lista()) {
                            if (a.getIdReserva() != null && !a.getIdReserva().trim().isEmpty()) {
                                String idRes = a.getIdReserva().trim();
                                mapaAsientosReserva.putIfAbsent(idRes, new ArrayList());
                                ((List)mapaAsientosReserva.get(idRes)).add(a.getNumeroAsiento());
                            }
                        }
                    } catch (Exception var18) {
                    }

                    List<Reserva> lista = FachadaArchivos.getInstance().consultaReservas();
                    this.modelo.setRowCount(0);
                    boolean encontrado = false;

                    for(Reserva r : lista) {
                        if (this.empresaFiltro == null || r.getEmpresa() != null && r.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {
                            String nombrePas = r.getPasajero() != null ? r.getPasajero().getNombre() : "";
                            if (nombrePas.toUpperCase().contains(nombre.toUpperCase())) {
                                String nombreEmpresa = "N/A";
                                if (r.getEmpresa() != null) {
                                    nombreEmpresa = (String)mapaEmpresas.getOrDefault(r.getEmpresa().getIdEmpresa(), r.getEmpresa().getIdEmpresa());
                                }

                                String rutaViaje = "N/A";
                                if (r.getViaje() != null) {
                                    rutaViaje = (String)mapaRutasViaje.getOrDefault(r.getViaje().getIdViaje(), r.getViaje().getIdViaje());
                                }

                                String infoAsientos = String.valueOf(r.getCantidadAsientos());
                                if (mapaAsientosReserva.containsKey(r.getIdReserva())) {
                                    List<Integer> nums = (List)mapaAsientosReserva.get(r.getIdReserva());
                                    Collections.sort(nums);
                                    infoAsientos = infoAsientos + " [" + nums.toString().replace("[", "").replace("]", "") + "]";
                                }

                                String fechaReserva = r.getFechaReserva() != null ? r.getFechaReserva().toString() : "";
                                this.modelo.addRow(new Object[]{r.getIdReserva(), nombreEmpresa, nombrePas, rutaViaje, infoAsientos, fechaReserva, r.getEstado()});
                                encontrado = true;
                            }
                        }
                    }

                    if (!encontrado) {
                        JOptionPane.showMessageDialog(this, "No se encontraron reservas para ese pasajero.");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
                }
            }
        }

    }
}
