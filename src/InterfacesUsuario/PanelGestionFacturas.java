//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import control.ControladorReservas;
import excepciones.PersistenciaException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
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
import objetosNegocio.Bus;
import objetosNegocio.DetalleFactura;
import objetosNegocio.Empresa;
import objetosNegocio.Factura;
import objetosNegocio.Pasajero;
import objetosNegocio.Reserva;
import objetosNegocio.Ruta;
import objetosNegocio.Usuario;
import objetosNegocio.Viaje;
import persistencia.Asientos;
import persistencia.Buses;
import persistencia.DetallesFactura;
import persistencia.Empresas;
import persistencia.Facturas;
import persistencia.Pasajeros;
import persistencia.Reservas;
import persistencia.Rutas;
import persistencia.Viajes;

public class PanelGestionFacturas extends JPanel {
    private JTable tabla;
    private DefaultTableModel modelo;
    private Facturas persistencia;
    private Empresa empresaFiltro;
    private Usuario usuarioActual;
    private JTextField txtBuscar;
    private List<Factura> listaFacturasCompleta;

    public PanelGestionFacturas(Usuario usuario) {
        this.usuarioActual = usuario;
        this.persistencia = new Facturas("facturas.dat");
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
        JButton btnDetalles = new JButton("Detalles");

        btnTodos.addActionListener((e) -> this.cargarDatos());
        btnAgregar.addActionListener((e) -> this.agregar());
        btnEditar.addActionListener((e) -> this.editar());
        btnEliminar.addActionListener((e) -> this.eliminar());
        btnDetalles.addActionListener((e) -> this.verDetalles());

        btnDetalles.setBackground(new Color(70, 130, 180));
        btnDetalles.setForeground(Color.WHITE);
        btnDetalles.setFont(new Font("Arial", 1, 12));

        panelBotones.add(btnTodos);
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnDetalles);

        this.txtBuscar = new JTextField(20);
        this.txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar (ID, Cliente, Fecha)...");
        this.txtBuscar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filtrar();
            }
        });

        panelBusqueda.add(new JLabel("🔍"));
        panelBusqueda.add(this.txtBuscar);

        panelSuperior.add(panelBotones, BorderLayout.WEST);
        panelSuperior.add(panelBusqueda, BorderLayout.EAST);

        String[] cols = new String[]{"ID", "Empresa", "Pasajero", "Origen", "Destino", "Fecha Viaje", "Total", "Estado"};
        this.modelo = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        this.tabla = new JTable(this.modelo);
        this.tabla.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(this.tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Facturas Registradas"));
        this.add(panelSuperior, "North");
        this.add(scrollPane, "Center");
    }

    private void cargarDatos() {
        try {
            this.listaFacturasCompleta = this.persistencia.lista();
            this.filtrar();
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void filtrar() {
        this.modelo.setRowCount(0);
        if (this.listaFacturasCompleta != null) {
            String texto = this.txtBuscar.getText().toUpperCase().trim();
            String[] palabrasClave = texto.split("\\s+");

            Pasajeros persistenciaPasajeros = new Pasajeros("pasajeros.dat");
            Map<String, String> mapaEmpresas = new HashMap();
            Map<String, String> mapaOrigenes = new HashMap();
            Map<String, String> mapaDestinos = new HashMap();
            Map<String, String> mapaAsientos = new HashMap();
            Map<String, String> mapaFechas = new HashMap();

            try {
                for(Empresa e : (new Empresas("empresas.dat")).lista()) {
                    mapaEmpresas.put(e.getIdEmpresa(), e.getNombre());
                }

                List<Ruta> lRutas = (new Rutas("rutas.dat")).lista();
                Map<String, String> rutaOrigen = new HashMap();
                Map<String, String> rutaDestino = new HashMap();

                for(Ruta r : lRutas) {
                    rutaOrigen.put(r.getIdRuta(), r.getOrigen());
                    rutaDestino.put(r.getIdRuta(), r.getDestino());
                }

                List<Viaje> lViajes = (new Viajes("viajes.dat")).lista();
                Map<String, String> viajeOrigen = new HashMap();
                Map<String, String> viajeDestino = new HashMap();
                Map<String, String> viajeFechas = new HashMap();

                for(Viaje v : lViajes) {
                    if (v.getRuta() != null) {
                        viajeOrigen.put(v.getIdViaje(), rutaOrigen.getOrDefault(v.getRuta().getIdRuta(), "N/A"));
                        viajeDestino.put(v.getIdViaje(), rutaDestino.getOrDefault(v.getRuta().getIdRuta(), "N/A"));
                    }

                    String fecha = "N/A";
                    if (v.getFechaSalida() != null) {
                        fecha = v.getFechaSalida().toString();
                        if (v.getHoraSalida() != null) {
                            fecha = fecha + " " + v.getHoraSalida();
                        }
                    }

                    viajeFechas.put(v.getIdViaje(), fecha);
                }

                for(Reserva r : (new Reservas("reservas.dat")).lista()) {
                    if (r.getViaje() != null) {
                        mapaOrigenes.put(r.getIdReserva(), viajeOrigen.getOrDefault(r.getViaje().getIdViaje(), "N/A"));
                        mapaDestinos.put(r.getIdReserva(), viajeDestino.getOrDefault(r.getViaje().getIdViaje(), "N/A"));
                        mapaFechas.put(r.getIdReserva(), (String)viajeFechas.getOrDefault(r.getViaje().getIdViaje(), "N/A"));
                    }
                }

                List<Asiento> lAsientos = (new Asientos("asientos.dat")).lista();
                Map<String, List<Integer>> asientosPorReserva = new HashMap();

                for(Asiento a : lAsientos) {
                    if (a.getIdReserva() != null && !a.getIdReserva().trim().isEmpty()) {
                        ((List)asientosPorReserva.computeIfAbsent(a.getIdReserva().trim(), (k) -> new ArrayList())).add(a.getNumeroAsiento());
                    }
                }

                for(String idRes : asientosPorReserva.keySet()) {
                    List<Integer> nums = (List)asientosPorReserva.get(idRes);
                    Collections.sort(nums);
                    mapaAsientos.put(idRes, nums.toString());
                }
            } catch (Exception var20) {
            }

            for(Factura f : this.listaFacturasCompleta) {
                if (this.empresaFiltro == null || f.getEmpresa() != null && f.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {

                    String idReserva = f.getReserva() != null ? f.getReserva().getIdReserva() : "";
                    String origen = mapaOrigenes.getOrDefault(idReserva, "N/A");
                    String destino = mapaDestinos.getOrDefault(idReserva, "N/A");

                    // Lógica de filtrado inteligente
                    if (!texto.isEmpty()) {
                        String datosCliente = f.getDatosPasajeroSnapshot() != null ? f.getDatosPasajeroSnapshot() : "";
                        String fechaEmisionStr = f.getFechaEmision() != null ? f.getFechaEmision().toString() : "";
                        String datosCompletos = (f.getIdFactura() + " " + datosCliente + " " + fechaEmisionStr + " " + origen + " " + destino).toUpperCase();

                        boolean coincide = true;
                        for (String palabra : palabrasClave) {
                            if (!datosCompletos.contains(palabra)) {
                                coincide = false;
                                break;
                            }
                        }
                        if (!coincide) {
                            continue;
                        }
                    }

                    String fechaEmision = "";

                    try {
                        if (f.getFechaEmision() != null) {
                            fechaEmision = f.getFechaEmision().toString();
                        }
                    } catch (Exception var19) {
                        fechaEmision = "Formato incorrecto";
                    }

                    String nombrePasajero = "N/A";
                    if (f.getPasajero() != null) {
                        try {
                            Pasajero p = persistenciaPasajeros.obten(f.getPasajero());
                            if (p != null) {
                                nombrePasajero = p.getNombre();
                            } else {
                                nombrePasajero = f.getPasajero().getIdPasajero();
                            }
                        } catch (Exception e) {
                            nombrePasajero = f.getPasajero().getIdPasajero();
                        }
                    }

                    String nombreEmpresa = "N/A";
                    if (f.getEmpresa() != null) {
                        String id = f.getEmpresa().getIdEmpresa();
                        nombreEmpresa = (String)mapaEmpresas.getOrDefault(id, id);
                    }

                    String fechaViaje = (String)mapaFechas.getOrDefault(idReserva, "N/A");
                    this.modelo.addRow(new Object[]{f.getIdFactura(), nombreEmpresa, nombrePasajero, origen, destino, fechaViaje, f.getTotal(), f.getEstado()});
                }
            }
        }
    }

    private void agregar() {
        InterfacesUsuario.DialogoFormularioFactura d = new InterfacesUsuario.DialogoFormularioFactura((JFrame)SwingUtilities.getWindowAncestor(this), "Agregar Factura", (Factura)null, this.empresaFiltro, this.usuarioActual);
        d.setVisible(true);
        if (d.isConfirmado()) {
            try {
                this.persistencia.agrega(d.getFactura());
                String nuevoId = d.getFactura().getIdFactura();
                List<DetalleFactura> detalles = d.getDetallesGenerados();
                if (detalles != null && !detalles.isEmpty()) {
                    DetallesFactura daoDetalles = new DetallesFactura("detalles_factura.dat");

                    for(DetalleFactura det : detalles) {
                        det.setIdFactura(nuevoId);
                        daoDetalles.agrega(det);
                    }
                }

                if ("Pagada".equalsIgnoreCase(d.getFactura().getEstado()) && d.getFactura().getReserva() != null) {
                    ControladorReservas ctrlReservas = new ControladorReservas();
                    ctrlReservas.actualizarEstadoReserva(d.getFactura().getReserva().getIdReserva(), "CONFIRMADA");
                }

                this.cargarDatos();
                JOptionPane.showMessageDialog(this, "Factura agregada");
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }

    }

    private void editar() {
        int fila = this.tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una factura de la tabla", "Aviso", 2);
        } else {
            try {
                Factura f = this.persistencia.obten(new Factura((String)this.modelo.getValueAt(fila, 0)));
                InterfacesUsuario.DialogoFormularioFactura d = new InterfacesUsuario.DialogoFormularioFactura((JFrame)SwingUtilities.getWindowAncestor(this), "Editar Factura", f, (Empresa)null, this.usuarioActual);
                d.setVisible(true);
                if (d.isConfirmado()) {
                    this.persistencia.actualiza(d.getFactura());
                    if ("Pagada".equalsIgnoreCase(d.getFactura().getEstado()) && d.getFactura().getReserva() != null) {
                        ControladorReservas ctrlReservas = new ControladorReservas();
                        ctrlReservas.actualizarEstadoReserva(d.getFactura().getReserva().getIdReserva(), "CONFIRMADA");
                    }

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
            JOptionPane.showMessageDialog(this, "Por favor seleccione una factura de la tabla", "Aviso", 2);
        } else if (JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar esta factura?", "Confirmar eliminación", 0) == 0) {
            try {
                String idFactura = (String)this.modelo.getValueAt(fila, 0);
                DetallesFactura daoDetalles = new DetallesFactura("detalles_factura.dat");
                daoDetalles.eliminarPorFactura(idFactura);
                this.persistencia.elimina(new Factura(idFactura));
                this.cargarDatos();
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }

        }
    }

    private void verDetalles() {
        int fila = this.tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura para ver sus detalles.", "Aviso", 2);
        } else {
            try {
                String idFactura = (String)this.modelo.getValueAt(fila, 0);
                Factura f = this.persistencia.obten(new Factura(idFactura));
                DetallesFactura daoDetalles = new DetallesFactura("detalles_factura.dat");
                List<DetalleFactura> detalles = daoDetalles.listaPorFactura(idFactura);
                String origen = "N/A";
                String destino = "N/A";
                String fechaSalida = "N/A";
                String horaSalida = "";
                String busPlaca = "N/A";
                String asientos = "N/A";
                String metodoPago = "Efectivo";
                String cedulaCliente = "N/A";
                String emailCliente = "N/A";
                String telefonoCliente = "N/A";
                String emailEmpresa = "";

                try {
                    if (f.getEmpresa() != null) {
                        Empresas daoEmp = new Empresas("empresas.dat");
                        Empresa emp = daoEmp.obten(new Empresa(f.getEmpresa().getIdEmpresa()));
                        if (emp != null) {
                            emailEmpresa = emp.getEmail();
                            if (f.getNombreEmpresaSnapshot() == null || f.getNombreEmpresaSnapshot().isEmpty() || "null".equals(f.getNombreEmpresaSnapshot())) {
                                f.setNombreEmpresaSnapshot(emp.getNombre());
                                f.setRucEmpresaSnapshot(emp.getRuc());
                                f.setDireccionEmpresaSnapshot(emp.getDireccion());
                            }
                        }
                    }

                    if (f.getReserva() != null) {
                        Reservas daoReservas = new Reservas("reservas.dat");
                        Reserva r = daoReservas.obten(new Reserva(f.getReserva().getIdReserva()));
                        if (r != null) {
                            metodoPago = r.getMetodoPago() != null ? r.getMetodoPago() : "Efectivo";

                            try {
                                if (r.getViaje() != null) {
                                    Asientos daoAsientos = new Asientos("asientos.dat");
                                    List<Asiento> listaAsientos = daoAsientos.listaPorViaje(r.getViaje().getIdViaje());
                                    List<String> nums = new ArrayList();
                                    String idReservaTarget = r.getIdReserva() != null ? r.getIdReserva().trim() : "";

                                    for(Asiento a : listaAsientos) {
                                        String idResAsiento = a.getIdReserva() != null ? a.getIdReserva().trim() : "";
                                        if (!idReservaTarget.isEmpty() && idResAsiento.equalsIgnoreCase(idReservaTarget)) {
                                            nums.add(String.valueOf(a.getNumeroAsiento()));
                                        }
                                    }

                                    if (!nums.isEmpty()) {
                                        nums.sort((s1, s2) -> Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2)));
                                        asientos = String.join(", ", nums);
                                    } else {
                                        asientos = String.valueOf(r.getCantidadAsientos());
                                    }
                                }
                            } catch (Exception var29) {
                                asientos = String.valueOf(r.getCantidadAsientos());
                            }

                            if (r.getViaje() != null) {
                                Viajes daoViajes = new Viajes("viajes.dat");
                                Viaje v = daoViajes.obten(new Viaje(r.getViaje().getIdViaje()));
                                if (v != null) {
                                    fechaSalida = v.getFechaSalida() != null ? v.getFechaSalida().toString() : "N/A";
                                    horaSalida = v.getHoraSalida() != null ? v.getHoraSalida() : "";
                                    if (v.getRuta() != null) {
                                        Rutas daoRutas = new Rutas("rutas.dat");
                                        Ruta ruta = daoRutas.obten(new Ruta(v.getRuta().getIdRuta()));
                                        if (ruta != null) {
                                            origen = ruta.getOrigen();
                                            destino = ruta.getDestino();
                                        }
                                    }

                                    if (v.getBus() != null) {
                                        Buses daoBuses = new Buses("buses.dat");
                                        Bus b = daoBuses.obten(new Bus(v.getBus().getIdBus()));
                                        if (b != null) {
                                            busPlaca = b.getPlaca();
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (f.getPasajero() != null) {
                        Pasajeros daoPasajeros = new Pasajeros("pasajeros.dat");
                        Pasajero p = daoPasajeros.obten(new Pasajero(f.getPasajero().getIdPasajero()));
                        if (p != null) {
                            cedulaCliente = p.getCedula();
                            emailCliente = p.getEmail() != null ? p.getEmail() : "N/A";
                            telefonoCliente = p.getTelefono() != null ? p.getTelefono() : "N/A";
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("No se pudieron cargar detalles extendidos: " + ex.getMessage());
                }

                StringBuilder html = new StringBuilder();
                html.append("<html><body style='font-family: \"Courier New\", monospace; font-size: 12px; background-color: #555; padding: 20px;'>");
                html.append("<div style='width: 320px; margin: 0 auto; background-color: #fff; border: 1px solid #ccc; padding: 15px;'>");
                html.append("<div style='text-align: center;'>");
                html.append("<h2 style='margin: 0; color: #000; font-size: 24px;'>").append(f.getNombreEmpresaSnapshot()).append("</h2>");
                html.append("<p style='margin: 5px; font-size: 14px;'><b>RUC: ").append(f.getRucEmpresaSnapshot()).append("</b></p>");
                html.append("<p style='margin: 2px; font-size: 10px;'>").append(f.getDireccionEmpresaSnapshot()).append("</p>");
                if (emailEmpresa != null && !emailEmpresa.isEmpty()) {
                    html.append("<p style='margin: 2px; font-size: 10px;'>Email: ").append(emailEmpresa).append("</p>");
                }

                html.append("<p style='margin: 2px; font-size: 9px; font-weight: bold;'>OBLIGADO A LLEVAR CONTABILIDAD: SI</p>");
                html.append("</div>");
                html.append("<br/><div style='border-bottom: 2px dashed #000;'></div><br/>");
                String nombreCliente = f.getDatosPasajeroSnapshot();
                if (nombreCliente.contains("(")) {
                    nombreCliente = nombreCliente.substring(0, nombreCliente.lastIndexOf("(")).trim();
                }

                String vendedor = f.getNombreVendedor();
                if (vendedor == null || vendedor.trim().isEmpty()) {
                    vendedor = "Administrador";
                }

                String numAut = String.valueOf(Math.abs(f.getIdFactura().hashCode())) + "123456789012345678901234567890";
                if (numAut.length() > 37) {
                    numAut = numAut.substring(0, 37);
                }

                html.append("<table style='width: 100%; font-size: 12px;'>");
                html.append("<tr><td><b>FACTURA NRO:</b></td><td style='text-align: right;'>").append(f.getIdFactura()).append("</td></tr>");
                html.append("<tr><td><b>AUTORIZACIÓN:</b></td><td style='text-align: right; font-size: 10px;'>").append(numAut).append("</td></tr>");
                html.append("<tr><td><b>FECHA:</b></td><td style='text-align: right;'>").append(f.getFechaEmision().toString()).append("</td></tr>");
                html.append("<tr><td><b>ATENDIDO POR:</b></td><td style='text-align: right;'>").append(vendedor).append("</td></tr>");
                html.append("<tr><td colspan='2'>&nbsp;</td></tr>");
                html.append("<tr><td><b>CLIENTE:</b></td><td style='text-align: right;'>").append(nombreCliente).append("</td></tr>");
                if (!"N/A".equals(cedulaCliente)) {
                    html.append("<tr><td><b>C.I./RUC:</b></td><td style='text-align: right;'>").append(cedulaCliente).append("</td></tr>");
                }

                html.append("<tr><td><b>EMAIL:</b></td><td style='text-align: right; font-size: 10px;'>").append(emailCliente).append("</td></tr>");
                html.append("<tr><td><b>TELÉFONO:</b></td><td style='text-align: right;'>").append(telefonoCliente).append("</td></tr>");
                html.append("</table>");
                html.append("<br/><div style='border-bottom: 2px dashed #000;'></div><br/>");
                if (!"N/A".equals(origen)) {
                    html.append("<div style='text-align: center; margin-bottom: 10px; font-size: 12px;'><b>--- DETALLES DEL VIAJE ---</b></div>");
                    html.append("<table style='width: 100%; font-size: 11px;'>");
                    html.append("<tr><td><b>RUTA (BUS):</b></td><td style='text-align: right;'>").append(origen).append(" - ").append(destino).append("</td></tr>");
                    html.append("<tr><td><b>SALIDA:</b></td><td style='text-align: right;'>").append(fechaSalida).append(" ").append(horaSalida).append("</td></tr>");
                    html.append("<tr><td><b>BUS:</b></td><td style='text-align: right;'>").append(busPlaca).append("</td></tr>");
                    html.append("<tr><td><b>ASIENTOS:</b></td><td style='text-align: right;'><b>").append(asientos).append("</b></td></tr>");
                    html.append("</table>");
                    html.append("<br/><div style='border-bottom: 2px dashed #000;'></div><br/>");
                }

                html.append("<table style='width: 100%; font-size: 11px; border-collapse: collapse;'>");
                html.append("<tr><th style='text-align: left;'>CANT</th><th style='text-align: left;'>DESCRIPCION</th><th style='text-align: right;'>P.UNIT</th><th style='text-align: right;'>TOTAL</th></tr>");
                if (detalles.isEmpty()) {
                    html.append("<tr><td colspan='4' style='text-align: center;'>Sin detalles</td></tr>");
                } else {
                    for(DetalleFactura d : detalles) {
                        html.append("<tr>");
                        html.append("<td style='vertical-align: top;'>").append(d.getCantidad()).append("</td>");
                        html.append("<td>").append(d.getDescripcion()).append("</td>");
                        html.append("<td style='text-align: right; vertical-align: top;'>$").append(String.format("%.2f", d.getPrecioUnitario())).append("</td>");
                        html.append("<td style='text-align: right; vertical-align: top;'>$").append(String.format("%.2f", d.getSubtotal())).append("</td>");
                        html.append("</tr>");
                    }
                }

                html.append("</table>");
                html.append("<br/><div style='border-bottom: 2px dashed #000;'></div><br/>");
                double totalFinal = f.getTotal();
                double subtotalNeto = totalFinal / 1.15;
                double iva = totalFinal - subtotalNeto;
                html.append("<table style='width: 100%; font-size: 12px;'>");
                html.append("<tr><td>SUBTOTAL 15%:</td><td style='text-align: right;'>$").append(String.format("%.2f", subtotalNeto)).append("</td></tr>");
                html.append("<tr><td>SUBTOTAL 0%:</td><td style='text-align: right;'>$0.00</td></tr>");
                html.append("<tr><td>DESCUENTO:</td><td style='text-align: right;'>$0.00</td></tr>");
                html.append("<tr><td>IVA (15%):</td><td style='text-align: right;'>$").append(String.format("%.2f", iva)).append("</td></tr>");
                html.append("<tr><td style='font-size: 16px;'><b>TOTAL:</b></td><td style='text-align: right; font-size: 16px;'><b>$").append(String.format("%.2f", totalFinal)).append("</b></td></tr>");
                html.append("</table>");
                html.append("<br/><div style='text-align: center; font-size: 11px;'>");
                html.append("Forma de Pago: ").append(metodoPago).append("<br/>");
                html.append("Estado: ").append(f.getEstado()).append("<br/><br/>");
                html.append("¡GRACIAS POR SU PREFERENCIA!<br/>");
                html.append("Conserve este documento");
                html.append("</div>");
                html.append("<br/><div style='text-align: center; font-size: 9px; color: #666; border-top: 1px dashed #ccc; padding-top: 5px;'>");
                html.append("Fecha de Emisión/Expedición: ").append(f.getFechaEmision().toString()).append("<br/>");
                html.append("Generado por: Sistema de Gestión de Transportes Interprovincial<br/>");
                html.append("Desarrollado por: Equipo 7");
                html.append("</div>");
                html.append("</div></body></html>");
                JEditorPane editorPane = new JEditorPane();
                editorPane.setContentType("text/html");
                editorPane.setText(html.toString());
                editorPane.setEditable(false);
                editorPane.setCaretPosition(0);
                JScrollPane scroll = new JScrollPane(editorPane);
                scroll.setPreferredSize(new Dimension(600, 700));
                JOptionPane.showMessageDialog(this, scroll, "Detalle de Factura", -1);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al cargar detalles: " + e.getMessage());
            }

        }
    }
}
