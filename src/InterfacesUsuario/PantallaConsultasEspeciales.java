//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import excepciones.PersistenciaException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import objetosNegocio.Asiento;
import objetosNegocio.Bus;
import objetosNegocio.Conductor;
import objetosNegocio.Empresa;
import objetosNegocio.Factura;
import objetosNegocio.Pasajero;
import objetosNegocio.Reserva;
import objetosNegocio.Ruta;
import objetosNegocio.Viaje;
import persistencia.Asientos;
import persistencia.Buses;
import persistencia.Conductores;
import persistencia.Empresas;
import persistencia.Facturas;
import persistencia.Pasajeros;
import persistencia.Reservas;
import persistencia.Rutas;
import persistencia.Viajes;

public class PantallaConsultasEspeciales extends JDialog {
    private JComboBox<String> cmbTipoConsulta;
    private JPanel panelParametros;
    private JTextArea areaResultados;
    private JButton btnConsultar;
    private JButton btnLimpiar;
    private JButton btnCerrar;
    private JComboBox<String> cmbParametro;
    private Empresa empresaFiltro;

    public PantallaConsultasEspeciales(JFrame padre, Empresa empresaFiltro) {
        super(padre, "Módulo de Facturación y Consultas", true);
        this.empresaFiltro = empresaFiltro;
        this.initComponents();
    }

    private void initComponents() {
        this.setSize(900, 700);
        this.setLocationRelativeTo(this.getParent());
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel panelControles = new JPanel(new BorderLayout());
        JPanel panelSuperior = new JPanel(new FlowLayout(0));
        panelSuperior.add(new JLabel("Tipo de Consulta:"));
        this.cmbTipoConsulta = new JComboBox<>(new String[]{"-- Seleccione --", "Consultar Factura por ID Reserva", "Consultar Factura por ID Factura", "Viajes por Ruta", "Reservas por Pasajero", "Buses por Empresa", "Viajes por Conductor"});
        this.cmbTipoConsulta.addActionListener((e) -> this.actualizarParametros());
        panelSuperior.add(this.cmbTipoConsulta);
        this.panelParametros = new JPanel(new FlowLayout(0));
        this.panelParametros.setBorder(BorderFactory.createTitledBorder("Parámetros"));
        panelControles.add(panelSuperior, "North");
        panelControles.add(this.panelParametros, "Center");
        this.areaResultados = new JTextArea();
        this.areaResultados.setEditable(false);
        this.areaResultados.setFont(new Font("Monospaced", 0, 12));
        JScrollPane scrollResultados = new JScrollPane(this.areaResultados);
        scrollResultados.setBorder(BorderFactory.createTitledBorder("Resultados"));
        JPanel panelBotones = new JPanel(new FlowLayout(1));
        this.btnConsultar = new JButton("Consultar");
        this.btnLimpiar = new JButton("Limpiar");
        this.btnCerrar = new JButton("Cerrar");
        this.btnConsultar.addActionListener((e) -> this.ejecutarConsulta());
        this.btnLimpiar.addActionListener((e) -> this.limpiar());
        this.btnCerrar.addActionListener((e) -> this.dispose());
        panelBotones.add(this.btnConsultar);
        panelBotones.add(this.btnLimpiar);
        panelBotones.add(this.btnCerrar);
        panelPrincipal.add(panelControles, "North");
        panelPrincipal.add(scrollResultados, "Center");
        this.add(panelPrincipal, "Center");
        this.add(panelBotones, "South");
    }

    private void actualizarParametros() {
        this.panelParametros.removeAll();
        String tipo = (String)this.cmbTipoConsulta.getSelectedItem();
        if (!tipo.equals("Consultar Factura por ID Reserva") && !tipo.equals("Consultar Factura por ID Factura") && !tipo.equals("Viajes por Ruta") && !tipo.equals("Reservas por Pasajero") && !tipo.equals("Buses por Empresa") && !tipo.equals("Viajes por Conductor")) {
            if (!tipo.equals("-- Seleccione --")) {
                this.panelParametros.add(new JLabel("No requiere parámetros"));
            }
        } else {
            JPanel var10000 = this.panelParametros;
            String var10003 = this.obtenerNombreParametro(tipo);
            var10000.add(new JLabel("Seleccione " + var10003 + ":"));
            this.cmbParametro = new JComboBox<>();
            this.cmbParametro.setPreferredSize(new Dimension(400, 25));
            this.cargarDatosCombo(tipo);
            this.panelParametros.add(this.cmbParametro);
        }

        this.panelParametros.revalidate();
        this.panelParametros.repaint();
        this.revalidate();
    }

    private void cargarDatosCombo(String tipo) {
        this.cmbParametro.removeAllItems();

        try {
            if (tipo.equals("Consultar Factura por ID Reserva")) {
                List<Reserva> reservas = (new Reservas("reservas.dat")).lista();
                Map<String, String> mapaPasajeros = new HashMap();

                for(Pasajero p : (new Pasajeros("pasajeros.dat")).lista()) {
                    mapaPasajeros.put(p.getIdPasajero(), p.getNombre());
                }

                for(Reserva r : reservas) {
                    if (this.empresaFiltro == null || r.getEmpresa() != null && r.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {
                        String nombre = "N/A";
                        if (r.getPasajero() != null) {
                            nombre = (String)mapaPasajeros.getOrDefault(r.getPasajero().getIdPasajero(), "N/A");
                        }

                        this.cmbParametro.addItem(r.getIdReserva() + " - " + nombre + " (" + String.valueOf(r.getFechaReserva()) + ")");
                    }
                }
            } else if (tipo.equals("Consultar Factura por ID Factura")) {
                for(Factura f : (new Facturas("facturas.dat")).lista()) {
                    if (this.empresaFiltro == null || f.getEmpresa() != null && f.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {
                        String cliente = f.getDatosPasajeroSnapshot();
                        if (cliente == null || cliente.isEmpty()) {
                            cliente = "Cliente N/A";
                        }

                        this.cmbParametro.addItem(f.getIdFactura() + " - " + cliente + " ($" + f.getTotal() + ")");
                    }
                }
            } else if (tipo.equals("Viajes por Ruta")) {
                for(Ruta r : (new Rutas("rutas.dat")).lista()) {
                    if (this.empresaFiltro == null || r.getEmpresa() != null && r.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {
                        JComboBox var10000 = this.cmbParametro;
                        String var10001 = r.getIdRuta();
                        var10000.addItem(var10001 + " - " + r.getOrigen() + " -> " + r.getDestino());
                    }
                }
            } else if (tipo.equals("Reservas por Pasajero")) {
                for(Pasajero p : (new Pasajeros("pasajeros.dat")).lista()) {
                    if (this.empresaFiltro == null || p.getEmpresa() != null && p.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {
                        JComboBox var27 = this.cmbParametro;
                        String var30 = p.getIdPasajero();
                        var27.addItem(var30 + " - " + p.getNombre() + " (" + p.getCedula() + ")");
                    }
                }
            } else if (tipo.equals("Buses por Empresa")) {
                for(Empresa e : (new Empresas("empresas.dat")).lista()) {
                    if (this.empresaFiltro == null || e.getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {
                        JComboBox var28 = this.cmbParametro;
                        String var31 = e.getIdEmpresa();
                        var28.addItem(var31 + " - " + e.getNombre());
                    }
                }
            } else if (tipo.equals("Viajes por Conductor")) {
                for(Conductor c : (new Conductores("conductores.dat")).lista()) {
                    if (this.empresaFiltro == null || c.getEmpresa() != null && c.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {
                        JComboBox var29 = this.cmbParametro;
                        String var32 = c.getIdConductor();
                        var29.addItem(var32 + " - " + c.getNombre());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String obtenerIdSeleccionado() {
        if (this.cmbParametro != null && this.cmbParametro.getSelectedItem() != null) {
            String seleccion = (String)this.cmbParametro.getSelectedItem();
            return seleccion.contains(" - ") ? seleccion.split(" - ")[0].trim() : seleccion.trim();
        } else {
            return "";
        }
    }

    private String obtenerNombreParametro(String tipo) {
        switch (tipo) {
            case "Consultar Factura por ID Reserva" -> {
                return "ID de Reserva";
            }
            case "Consultar Factura por ID Factura" -> {
                return "ID de Factura";
            }
            case "Viajes por Ruta" -> {
                return "ID de Ruta";
            }
            case "Reservas por Pasajero" -> {
                return "ID de Pasajero";
            }
            case "Buses por Empresa" -> {
                return "ID de Empresa";
            }
            case "Viajes por Conductor" -> {
                return "Conductor";
            }
            default -> {
                return "";
            }
        }
    }

    private void ejecutarConsulta() {
        String tipo = (String)this.cmbTipoConsulta.getSelectedItem();
        if (tipo.equals("-- Seleccione --")) {
            JOptionPane.showMessageDialog(this, "Seleccione un tipo de consulta");
        } else {
            try {
                StringBuilder resultado = new StringBuilder();
                switch (tipo) {
                    case "Consultar Factura por ID Reserva" -> this.consultarDetalleFacturaPorReserva(resultado);
                    case "Consultar Factura por ID Factura" -> this.consultarDetalleFacturaPorId(resultado);
                    case "Viajes por Ruta" -> this.consultarViajesPorRuta(resultado);
                    case "Reservas por Pasajero" -> this.consultarReservasPorPasajero(resultado);
                    case "Buses por Empresa" -> this.consultarBusesPorEmpresa(resultado);
                    case "Viajes por Conductor" -> this.consultarViajesPorConductor(resultado);
                }

                this.areaResultados.setText(resultado.toString());
                this.areaResultados.setCaretPosition(0);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error en consulta: " + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    private void consultarDetalleFacturaPorReserva(StringBuilder resultado) throws PersistenciaException {
        String idReserva = this.obtenerIdSeleccionado();
        if (idReserva.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una Reserva");
        } else {
            Reservas persistenciaReservas = new Reservas("reservas.dat");
            Reserva reserva = persistenciaReservas.obten(new Reserva(idReserva));
            if (reserva != null && this.empresaFiltro != null && (reserva.getEmpresa() == null || !reserva.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa()))) {
                reserva = null;
            }

            if (reserva == null) {
                resultado.append("No se encontró una reserva con ID: ").append(idReserva);
            } else {
                Facturas persistenciaFacturas = new Facturas("facturas.dat");
                List<Factura> todasFacturas = persistenciaFacturas.lista();
                Factura factura = null;

                for(int i = 0; i < todasFacturas.size(); ++i) {
                    Factura f = (Factura)todasFacturas.get(i);
                    if (f.getReserva() != null && f.getReserva().getIdReserva().equals(idReserva)) {
                        factura = f;
                        break;
                    }
                }

                if (factura == null) {
                    resultado.append("No se encontró una factura para la reserva: ").append(idReserva);
                } else {
                    this.prepararDatosFactura(resultado, factura, reserva);
                }
            }
        }
    }

    private void consultarDetalleFacturaPorId(StringBuilder resultado) throws PersistenciaException {
        String idFactura = this.obtenerIdSeleccionado();
        if (idFactura.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una Factura");
        } else {
            Facturas persistenciaFacturas = new Facturas("facturas.dat");
            Factura factura = persistenciaFacturas.obten(new Factura(idFactura));
            if (factura != null && this.empresaFiltro != null && (factura.getEmpresa() == null || !factura.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa()))) {
                factura = null;
            }

            if (factura == null) {
                resultado.append("No se encontró una factura con ID: ").append(idFactura);
            } else {
                Reservas persistenciaReservas = new Reservas("reservas.dat");
                Reserva reserva = null;
                if (factura.getReserva() != null) {
                    reserva = persistenciaReservas.obten(new Reserva(factura.getReserva().getIdReserva()));
                }

                if (reserva == null) {
                    resultado.append("Error: La reserva asociada a esta factura no se encuentra.");
                } else {
                    this.prepararDatosFactura(resultado, factura, reserva);
                }
            }
        }
    }

    private void prepararDatosFactura(StringBuilder resultado, Factura factura, Reserva reserva) throws PersistenciaException {
        Pasajeros persistenciaPasajeros = new Pasajeros("pasajeros.dat");
        Pasajero pasajero = null;
        if (reserva.getPasajero() != null) {
            pasajero = persistenciaPasajeros.obten(reserva.getPasajero());
        }

        Viajes persistenciaViajes = new Viajes("viajes.dat");
        Viaje viaje = null;
        if (reserva.getViaje() != null) {
            viaje = persistenciaViajes.obten(reserva.getViaje());
        }

        Rutas persistenciaRutas = new Rutas("rutas.dat");
        Ruta ruta = null;
        if (viaje != null && viaje.getRuta() != null) {
            ruta = persistenciaRutas.obten(viaje.getRuta());
        }

        Buses persistenciaBuses = new Buses("buses.dat");
        Bus bus = null;
        if (viaje != null && viaje.getBus() != null) {
            bus = persistenciaBuses.obten(viaje.getBus());
        }

        Empresas persistenciaEmpresas = new Empresas("empresas.dat");
        Empresa empresa = null;
        if (bus != null && bus.getEmpresa() != null) {
            empresa = persistenciaEmpresas.obten(bus.getEmpresa());
        }

        List<String> asientosReservados = new ArrayList();
        if (viaje != null) {
            Asientos persistenciaAsientos = new Asientos("asientos.dat");

            for(Asiento a : persistenciaAsientos.listaPorViaje(viaje.getIdViaje())) {
                if (a.getIdReserva() != null && a.getIdReserva().trim().equalsIgnoreCase(reserva.getIdReserva().trim())) {
                    asientosReservados.add(String.valueOf(a.getNumeroAsiento()));
                }
            }

            asientosReservados.sort((s1, s2) -> {
                try {
                    return Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2));
                } catch (NumberFormatException var3) {
                    return s1.compareTo(s2);
                }
            });
        }

        this.generarDetalleFactura(resultado, factura, reserva, pasajero, viaje, ruta, empresa, asientosReservados);
    }

    private void generarDetalleFactura(StringBuilder sb, Factura factura, Reserva reserva, Pasajero pasajero, Viaje viaje, Ruta ruta, Empresa empresa, List<String> asientosReservados) {
        double IVA = 0.15;
        sb.append("═══════════════════════════════════════════════════════════════════════\n");
        sb.append("                   SISTEMA DE GESTIÓN DE BUSES                          \n");
        sb.append("                        FACTURA DETALLADA                               \n");
        sb.append("═══════════════════════════════════════════════════════════════════════\n\n");
        sb.append("───────────────────────── DATOS DE LA EMPRESA ─────────────────────────\n");
        if (empresa != null) {
            sb.append(String.format("Empresa:    %s\n", empresa.getNombre()));
            sb.append(String.format("RUC:        %s\n", empresa.getRuc() != null ? empresa.getRuc() : "N/A"));
            sb.append(String.format("Dirección:  %s\n", empresa.getDireccion()));
            sb.append(String.format("Teléfono:   %s\n", empresa.getTelefono()));
            sb.append(String.format("Email:      %s\n", empresa.getEmail()));
        } else {
            sb.append("Información de empresa no disponible\n");
        }

        sb.append("\n");
        sb.append("──────────────────────── INFORMACIÓN FACTURA ──────────────────────────\n");
        sb.append(String.format("Nº Factura:     %s\n", factura.getIdFactura()));
        sb.append(String.format("Nº Reserva:     %s\n", reserva.getIdReserva()));
        sb.append(String.format("Fecha Emisión:  %s\n", factura.getFechaEmision() != null ? factura.getFechaEmision().toString() : "N/A"));
        sb.append(String.format("Estado:         %s\n", factura.getEstado()));
        sb.append("\n");
        sb.append("──────────────────────── DATOS DEL PASAJERO ───────────────────────────\n");
        if (pasajero != null) {
            sb.append(String.format("Nombre:     %s\n", pasajero.getNombre()));
            sb.append(String.format("Cédula:     %s\n", pasajero.getCedula()));
            sb.append(String.format("Teléfono:   %s\n", pasajero.getTelefono()));
            sb.append(String.format("Email:      %s\n", pasajero.getEmail()));
        } else {
            sb.append("Información de pasajero no disponible\n");
        }

        sb.append("\n");
        sb.append("───────────────────────── DETALLES DEL VIAJE ──────────────────────────\n");
        if (viaje != null && ruta != null) {
            sb.append(String.format("ID Viaje:        %s\n", viaje.getIdViaje()));
            sb.append(String.format("Ruta:            %s → %s\n", ruta.getOrigen(), ruta.getDestino()));
            sb.append(String.format("Distancia:       %.2f km\n", ruta.getDistancia()));
            String fechaSalida = viaje.getFechaSalida() != null ? viaje.getFechaSalida().toString() : "N/A";
            String horaSalida = viaje.getHoraSalida() != null ? viaje.getHoraSalida() : "N/A";
            sb.append(String.format("Salida:          %s  %s\n", fechaSalida, horaSalida));
            String fechaLlegada = viaje.getFechaLlegada() != null ? viaje.getFechaLlegada().toString() : "N/A";
            String horaLlegada = viaje.getHoraLlegada() != null ? viaje.getHoraLlegada() : "N/A";
            sb.append(String.format("Llegada:         %s  %s\n", fechaLlegada, horaLlegada));
            if (ruta.getAdvertencias() != null && !ruta.getAdvertencias().isEmpty()) {
                sb.append(String.format("Avisos Ruta:     %s\n", ruta.getAdvertencias()));
            }
        } else {
            sb.append("Información de viaje no disponible\n");
        }

        sb.append("\n");
        sb.append("───────────────────────── DETALLES RESERVA ────────────────────────────\n");
        sb.append(String.format("Cantidad de Asientos:  %d\n", reserva.getCantidadAsientos()));
        String listaAsientos = asientosReservados.isEmpty() ? "No asignados" : String.join(", ", asientosReservados);
        sb.append(String.format("Asientos:              %s\n", listaAsientos));
        double precioUnitarioRef = reserva.getCantidadAsientos() > 0 ? reserva.getPrecioTotal() / (double)reserva.getCantidadAsientos() : (double)0.0F;
        sb.append(String.format("Precio Promedio/Asiento: $%.2f (Incluye descuentos)\n", precioUnitarioRef));
        sb.append("\n");
        sb.append("────────────────────────── CÁLCULO TOTAL ──────────────────────────────\n");
        double subtotal = reserva.getPrecioTotal();
        if (subtotal == (double)0.0F && ruta != null) {
            subtotal = ruta.getPrecio() * (double)reserva.getCantidadAsientos();
        }

        double montoIVA = subtotal * 0.15;
        double total = subtotal + montoIVA;
        sb.append(String.format("Subtotal 15%%:         $%.2f\n", subtotal));
        sb.append(String.format("Subtotal 0%%:          $0.00\n"));
        sb.append(String.format("Descuento:             $0.00\n"));
        sb.append(String.format("IVA (15%%):             $%.2f\n", montoIVA));
        sb.append("───────────────────────────────────────────────────────────────────────\n");
        sb.append(String.format("TOTAL A PAGAR:         $%.2f\n", total));
        sb.append("═══════════════════════════════════════════════════════════════════════\n\n");
        sb.append("                   Gracias por preferirnos\n");
        sb.append("              Conserve esta factura como comprobante\n");
        sb.append("═══════════════════════════════════════════════════════════════════════\n");
    }

    private void consultarViajesPorRuta(StringBuilder resultado) throws PersistenciaException {
        String idRuta = this.obtenerIdSeleccionado();
        if (idRuta.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una Ruta");
        } else {
            Viajes persistencia = new Viajes("viajes.dat");
            List<Viaje> lista = persistencia.lista();
            resultado.append("VIAJES PARA LA RUTA: ").append(idRuta).append("\n");
            resultado.append("=".repeat(60)).append("\n\n");
            boolean encontrado = false;

            for(Viaje v : lista) {
                if (v.getRuta() != null && v.getRuta().getIdRuta().equalsIgnoreCase(idRuta) && (this.empresaFiltro == null || v.getEmpresa() != null && v.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa()))) {
                    resultado.append(String.format("ID: %s | Fecha: %s %s | Bus: %s | Estado: %s\n", v.getIdViaje(), v.getFechaSalida(), v.getHoraSalida(), v.getBus() != null ? v.getBus().getIdBus() : "N/A", v.getEstado()));
                    encontrado = true;
                }
            }

            if (!encontrado) {
                resultado.append("No se encontraron viajes para esta ruta.");
            }

        }
    }

    private void consultarReservasPorPasajero(StringBuilder resultado) throws PersistenciaException {
        String idPasajero = this.obtenerIdSeleccionado();
        if (idPasajero.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un Pasajero");
        } else {
            Reservas persistencia = new Reservas("reservas.dat");
            List<Reserva> lista = persistencia.lista();
            resultado.append("RESERVAS DEL PASAJERO: ").append(idPasajero).append("\n");
            resultado.append("=".repeat(60)).append("\n\n");
            boolean encontrado = false;

            for(Reserva r : lista) {
                if (r.getPasajero() != null && r.getPasajero().getIdPasajero().equalsIgnoreCase(idPasajero) && (this.empresaFiltro == null || r.getEmpresa() != null && r.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa()))) {
                    resultado.append(String.format("ID: %s | Viaje: %s | Asientos: %d | Fecha: %s | Estado: %s\n", r.getIdReserva(), r.getViaje() != null ? r.getViaje().getIdViaje() : "N/A", r.getCantidadAsientos(), r.getFechaReserva(), r.getEstado()));
                    encontrado = true;
                }
            }

            if (!encontrado) {
                resultado.append("No se encontraron reservas para este pasajero.");
            }

        }
    }

    private void consultarBusesPorEmpresa(StringBuilder resultado) throws PersistenciaException {
        String idEmpresa = this.obtenerIdSeleccionado();
        if (idEmpresa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una Empresa");
        } else {
            Buses persistencia = new Buses("buses.dat");
            List<Bus> lista = persistencia.lista();
            resultado.append("BUSES DE LA EMPRESA: ").append(idEmpresa).append("\n");
            resultado.append("=".repeat(60)).append("\n\n");
            boolean encontrado = false;

            for(Bus b : lista) {
                if (b.getEmpresa() != null && b.getEmpresa().getIdEmpresa().equalsIgnoreCase(idEmpresa)) {
                    resultado.append(String.format("ID: %s | Placa: %s | Marca: %s | Capacidad: %d | Estado: %s\n", b.getIdBus(), b.getPlaca(), b.getMarca(), b.getCapacidad(), b.getEstado()));
                    encontrado = true;
                }
            }

            if (!encontrado) {
                resultado.append("No se encontraron buses para esta empresa.");
            }

        }
    }

    private void consultarViajesPorConductor(StringBuilder resultado) throws PersistenciaException {
        String idConductor = this.obtenerIdSeleccionado();
        if (idConductor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un Conductor");
        } else {
            Viajes persistencia = new Viajes("viajes.dat");
            List<Viaje> lista = persistencia.lista();
            resultado.append("VIAJES ASIGNADOS AL CONDUCTOR: ").append(idConductor).append("\n");
            resultado.append("=".repeat(60)).append("\n\n");
            boolean encontrado = false;

            for(Viaje v : lista) {
                boolean esConductor = v.getConductor1() != null && v.getConductor1().getIdConductor().equalsIgnoreCase(idConductor) || v.getConductor2() != null && v.getConductor2().getIdConductor().equalsIgnoreCase(idConductor);
                if (esConductor && (this.empresaFiltro == null || v.getEmpresa() != null && v.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa()))) {
                    String rol = v.getConductor1() != null && v.getConductor1().getIdConductor().equalsIgnoreCase(idConductor) ? "Principal" : "Relevo";
                    resultado.append(String.format("ID: %s | Fecha: %s %s | Ruta: %s | Rol: %s | Estado: %s\n", v.getIdViaje(), v.getFechaSalida(), v.getHoraSalida(), v.getRuta() != null ? v.getRuta().getIdRuta() : "N/A", rol, v.getEstado()));
                    encontrado = true;
                }
            }

            if (!encontrado) {
                resultado.append("No se encontraron viajes asignados a este conductor.");
            }

        }
    }

    private void limpiar() {
        this.areaResultados.setText("");
    }
}
