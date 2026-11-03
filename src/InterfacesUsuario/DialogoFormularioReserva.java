//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import control.ControladorPrincipal;
import control.ControladorReservas;
import control.ControladorViajes;
import control.ResultadoOperacion;
import fachadas.FachadaArchivos;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import objetosNegocio.Asiento;
import objetosNegocio.Empresa;
import objetosNegocio.Pasajero;
import objetosNegocio.Reserva;
import objetosNegocio.Ruta;
import objetosNegocio.Viaje;
import objetosServicio.Fecha;

public class DialogoFormularioReserva extends JDialog {
    private JTextField txtIdReserva;
    private JTextField txtPasajero;
    private JTextField txtViaje;
    private JTextField txtCantidad;
    private JTextField txtTotalEstimado;
    private JTextArea txtAsientosSeleccionados;
    private JButton btnSeleccionarAsientos;
    private JButton btnDestinoIntermedio;
    private JTextField txtDia;
    private JTextField txtMes;
    private JTextField txtAnio;
    private JComboBox<String> cmbMetodoPago;
    private JCheckBox chkNinoBrazos;
    private ControladorReservas controladorReservas;
    private ControladorViajes controladorViajes;
    private List<Ruta> listaRutas;
    private List<Integer> listaAsientosSeleccionados;
    private boolean confirmado;
    private Reserva reservaGenerada;
    private Reserva reservaEdicion;
    private Viaje viajePreseleccionado;
    private Empresa empresaFiltro;
    private String destinoReal;
    private Pasajero pasajeroSeleccionado;
    private Viaje viajeSeleccionado;

    public DialogoFormularioReserva(JFrame padre, Reserva reservaAEditar, Empresa empresaFiltro) {
        super(padre, reservaAEditar == null ? "Nueva Reserva" : "Editar Reserva", true);
        this.confirmado = false;
        this.destinoReal = null;
        this.controladorReservas = new ControladorReservas();
        this.controladorViajes = new ControladorViajes();
        this.listaAsientosSeleccionados = new ArrayList();
        this.listaRutas = new ArrayList();
        this.reservaEdicion = reservaAEditar;
        this.empresaFiltro = empresaFiltro;
        this.cargarRutas();
        this.initComponents();
        if (this.reservaEdicion != null) {
            this.cargarDatosEdicion();
        }

    }

    public DialogoFormularioReserva(JFrame padre, Viaje viaje) {
        this(padre, (Reserva)null, viaje != null ? viaje.getEmpresa() : null);
        this.viajePreseleccionado = viaje;
        this.aplicarPreseleccionViaje();
    }

    private void initComponents() {
        this.setSize(550, 700);
        this.setLocationRelativeTo(this.getParent());
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = 2;
        g.insets = new Insets(8, 8, 8, 8);
        g.gridx = 0;
        g.gridy = 0;
        p.add(new JLabel("ID Reserva:"), g);
        this.txtIdReserva = new JTextField("(Automático)");
        this.txtIdReserva.setEnabled(false);
        g.gridx = 1;
        p.add(this.txtIdReserva, g);
        g.gridx = 0;
        g.gridy = 1;
        p.add(new JLabel("ID Pasajero: *"), g);
        JPanel pPasajero = new JPanel(new BorderLayout(5, 0));
        this.txtPasajero = new JTextField();
        this.txtPasajero.setEditable(false);
        JButton btnBuscarPas = new JButton("\ud83d\udd0d");
        btnBuscarPas.addActionListener((e) -> this.buscarPasajero());
        pPasajero.add(this.txtPasajero, "Center");
        pPasajero.add(btnBuscarPas, "East");
        g.gridx = 1;
        p.add(pPasajero, g);
        g.gridx = 0;
        g.gridy = 2;
        p.add(new JLabel("Viaje: *"), g);
        JPanel pViaje = new JPanel(new BorderLayout(5, 0));
        this.txtViaje = new JTextField();
        this.txtViaje.setEditable(false);
        JButton btnBuscarViaje = new JButton("\ud83d\udd0d");
        btnBuscarViaje.addActionListener((e) -> this.buscarViaje());
        pViaje.add(this.txtViaje, "Center");
        pViaje.add(btnBuscarViaje, "East");
        g.gridx = 1;
        p.add(pViaje, g);
        this.btnDestinoIntermedio = new JButton("Destino Intermedio");
        this.btnDestinoIntermedio.setToolTipText("Seleccionar si el pasajero se baja antes");
        this.btnDestinoIntermedio.addActionListener((e) -> this.seleccionarDestinoIntermedio());
        g.gridx = 0;
        g.gridy = 3;
        g.gridwidth = 2;
        JPanel pBotonesViaje = new JPanel(new GridLayout(1, 2, 5, 0));
        this.btnSeleccionarAsientos = new JButton("Asientos");
        this.btnSeleccionarAsientos.addActionListener((e) -> this.abrirMapaAsientos());
        pBotonesViaje.add(this.btnSeleccionarAsientos);
        pBotonesViaje.add(this.btnDestinoIntermedio);
        p.add(pBotonesViaje, g);
        g.gridx = 0;
        g.gridy = 4;
        g.gridwidth = 1;
        p.add(new JLabel("Asientos:"), g);
        this.txtAsientosSeleccionados = new JTextArea(3, 20);
        this.txtAsientosSeleccionados.setEditable(false);
        this.txtAsientosSeleccionados.setBackground(new Color(240, 240, 240));
        this.txtAsientosSeleccionados.setText("Ninguno seleccionado");
        JScrollPane scrollAsientos = new JScrollPane(this.txtAsientosSeleccionados);
        g.gridx = 1;
        p.add(scrollAsientos, g);
        g.gridx = 0;
        g.gridy = 5;
        p.add(new JLabel("Cantidad:"), g);
        this.txtCantidad = new JTextField("0");
        this.txtCantidad.setEditable(false);
        g.gridx = 1;
        p.add(this.txtCantidad, g);
        g.gridx = 0;
        g.gridy = 6;
        p.add(new JLabel("Total Estimado ($):"), g);
        this.txtTotalEstimado = new JTextField("0.00");
        this.txtTotalEstimado.setEditable(false);
        this.txtTotalEstimado.setFont(new Font("Arial", 1, 12));
        g.gridx = 1;
        p.add(this.txtTotalEstimado, g);
        g.gridx = 0;
        g.gridy = 7;
        p.add(new JLabel("Fecha Reserva:"), g);
        JPanel pFecha = new JPanel(new FlowLayout(0, 5, 0));
        this.txtDia = new JTextField(2);
        this.agregarFiltroNumeros(this.txtDia, 2);
        this.txtMes = new JTextField(2);
        this.agregarFiltroNumeros(this.txtMes, 2);
        this.txtAnio = new JTextField(4);
        this.agregarFiltroNumeros(this.txtAnio, 4);
        Fecha hoy = new Fecha();
        this.txtDia.setText(String.valueOf(hoy.getDia()));
        this.txtMes.setText(String.valueOf(hoy.getMes()));
        this.txtAnio.setText(String.valueOf(hoy.getAnio()));
        pFecha.add(this.txtDia);
        pFecha.add(new JLabel("/"));
        pFecha.add(this.txtMes);
        pFecha.add(new JLabel("/"));
        pFecha.add(this.txtAnio);
        g.gridx = 1;
        p.add(pFecha, g);
        g.gridx = 0;
        g.gridy = 8;
        p.add(new JLabel("Pago:"), g);
        this.cmbMetodoPago = new JComboBox<>(new String[]{"EFECTIVO", "TARJETA", "TRANSFERENCIA"});
        g.gridx = 1;
        p.add(this.cmbMetodoPago, g);
        g.gridx = 0;
        g.gridy = 9;
        g.gridwidth = 2;
        this.chkNinoBrazos = new JCheckBox("¿Lleva niño < 5 años en brazos? (Gratis)");
        p.add(this.chkNinoBrazos, g);
        JPanel pBtn = new JPanel(new FlowLayout(1));
        JButton btnGuardar = new JButton("Guardar Reserva");
        JButton btnCancelar = new JButton("Cancelar");
        btnGuardar.addActionListener((e) -> this.guardar());
        btnCancelar.addActionListener((e) -> this.dispose());
        pBtn.add(btnGuardar);
        pBtn.add(btnCancelar);
        this.add(p, "Center");
        this.add(pBtn, "South");
    }

    private void cargarRutas() {
        ResultadoOperacion res = ControladorPrincipal.getInstance().consultarRutas();
        if (res.isExito()) {
            this.listaRutas = (List)res.getDatos();
        } else {
            this.listaRutas = new ArrayList();
        }

    }

    private void buscarPasajero() {
        DialogoBuscarPasajero d = new DialogoBuscarPasajero(this, this.empresaFiltro);
        d.setVisible(true);
        if (d.getPasajeroSeleccionado() != null) {
            this.pasajeroSeleccionado = d.getPasajeroSeleccionado();
            JTextField var10000 = this.txtPasajero;
            String var10001 = this.pasajeroSeleccionado.getNombre();
            var10000.setText(var10001 + " (" + this.pasajeroSeleccionado.getCedula() + ")");
            this.calcularTotal();
        }

    }

    private void buscarViaje() {
        DialogoBuscarViaje d = new DialogoBuscarViaje(this, this.empresaFiltro);
        d.setVisible(true);
        if (d.getViajeSeleccionado() != null) {
            this.viajeSeleccionado = d.getViajeSeleccionado();
            JTextField var10000 = this.txtViaje;
            String var10001 = this.viajeSeleccionado.getIdViaje();
            var10000.setText(var10001 + " | " + String.valueOf(this.viajeSeleccionado.getFechaSalida()));
            this.limpiarSeleccionAsientos();
        }

    }

    private void cargarDatosEdicion() {
        this.txtIdReserva.setText(this.reservaEdicion.getIdReserva());
        if (this.reservaEdicion.getPasajero() != null) {
            this.pasajeroSeleccionado = this.reservaEdicion.getPasajero();
            this.txtPasajero.setText(this.pasajeroSeleccionado.getNombre());
        }

        if (this.reservaEdicion.getViaje() != null) {
            this.viajeSeleccionado = this.reservaEdicion.getViaje();
            this.txtViaje.setText(this.viajeSeleccionado.getIdViaje());
        }

        this.listaAsientosSeleccionados.clear();
        if (this.reservaEdicion.getViaje() != null) {
            try {
                List<Asiento> asientosViaje = FachadaArchivos.getInstance().consultaAsientosPorViaje(this.reservaEdicion.getViaje().getIdViaje());
                String idResEdicion = this.reservaEdicion.getIdReserva() != null ? this.reservaEdicion.getIdReserva().trim() : "";

                for(Asiento a : asientosViaje) {
                    String idResAsiento = a.getIdReserva() != null ? a.getIdReserva().trim() : "";
                    if (!idResEdicion.isEmpty() && idResEdicion.equalsIgnoreCase(idResAsiento)) {
                        this.listaAsientosSeleccionados.add(a.getNumeroAsiento());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error cargando asientos de reserva: " + e.getMessage());
            }
        }

        this.actualizarInfoAsientos();
        if (this.reservaEdicion.getFechaReserva() != null) {
            this.txtDia.setText(String.valueOf(this.reservaEdicion.getFechaReserva().getDia()));
            this.txtMes.setText(String.valueOf(this.reservaEdicion.getFechaReserva().getMes()));
            this.txtAnio.setText(String.valueOf(this.reservaEdicion.getFechaReserva().getAnio()));
        }

    }

    private void aplicarPreseleccionViaje() {
        if (this.viajePreseleccionado != null) {
            this.viajeSeleccionado = this.viajePreseleccionado;
            this.txtViaje.setText(this.viajeSeleccionado.getIdViaje());
            this.txtViaje.setEnabled(false);
        }

    }

    private void limpiarSeleccionAsientos() {
        if (this.isVisible()) {
            this.listaAsientosSeleccionados.clear();
            this.destinoReal = null;
            if (this.btnDestinoIntermedio != null) {
                this.btnDestinoIntermedio.setText("Destino Intermedio");
                this.btnDestinoIntermedio.setBackground(UIManager.getColor("Button.background"));
            }

            this.actualizarInfoAsientos();
        }

    }

    private void abrirMapaAsientos() {
        Viaje viaje = this.viajeSeleccionado;
        if (viaje == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un viaje primero.");
        } else {
            DialogoMapaAsientos mapa = new DialogoMapaAsientos(this, viaje.getIdViaje(), this.listaAsientosSeleccionados);
            mapa.setVisible(true);
            if (mapa.isConfirmado()) {
                this.listaAsientosSeleccionados = mapa.getAsientosSeleccionados();
                this.actualizarInfoAsientos();
            }

        }
    }

    private void seleccionarDestinoIntermedio() {
        Viaje v = this.viajeSeleccionado;
        if (v != null && v.getRuta() != null) {
            JDialog mapaDialog = new JDialog(this, "Seleccione Destino de Bajada", true);
            mapaDialog.setSize(500, 650);
            mapaDialog.setLocationRelativeTo(this);
            PanelSeleccionDestino panelMapa = new PanelSeleccionDestino(v.getRuta().getOrigen(), v.getRuta().getDestino());
            mapaDialog.add(panelMapa);
            mapaDialog.setVisible(true);
            if (panelMapa.ciudadSeleccionada != null) {
                this.destinoReal = panelMapa.ciudadSeleccionada;
                this.btnDestinoIntermedio.setText("Destino: " + this.destinoReal);
                this.btnDestinoIntermedio.setBackground(new Color(255, 250, 205));
                this.calcularTotal();
            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un viaje válido primero.");
        }
    }

    private void actualizarInfoAsientos() {
        if (this.listaAsientosSeleccionados.isEmpty()) {
            this.txtAsientosSeleccionados.setText("Ninguno seleccionado");
            this.txtCantidad.setText("0");
        } else {
            StringBuilder sb = new StringBuilder();

            for(Integer i : this.listaAsientosSeleccionados) {
                sb.append("Asiento #").append(i).append("\n");
            }

            this.txtAsientosSeleccionados.setText(sb.toString());
            this.txtCantidad.setText(String.valueOf(this.listaAsientosSeleccionados.size()));
        }

        this.calcularTotal();
    }

    private void calcularTotal() {
        Viaje v = this.viajeSeleccionado;
        if (v != null && !this.listaAsientosSeleccionados.isEmpty()) {
            double precioBase = (double)0.0F;
            boolean precioAjustado = false;
            if (this.destinoReal != null && v.getRuta() != null) {
                String origen = v.getRuta().getOrigen();
                DialogoFormularioRuta.ResultadoRuta resRuta = DialogoFormularioRuta.encontrarRutaMasCorta(DialogoFormularioRuta.normalizarCiudadEstatica(origen), DialogoFormularioRuta.normalizarCiudadEstatica(this.destinoReal));
                if (resRuta != null) {
                    precioBase = (double)Math.round(resRuta.distanciaTotal * 0.08 * (double)100.0F) / (double)100.0F;
                    precioAjustado = true;
                } else {
                    precioBase = v.getPrecio() > (double)0.0F ? v.getPrecio() : v.getRuta().getPrecio();
                }
            } else if (v.getPrecio() > (double)0.0F) {
                precioBase = v.getPrecio();
            } else if (v.getRuta() != null) {
                precioBase = v.getRuta().getPrecio();
            }

            if (precioBase <= (double)0.0F) {
                this.txtTotalEstimado.setText("Error: Sin Precio");
                this.txtTotalEstimado.setForeground(Color.RED);
            } else {
                if (precioAjustado) {
                    this.txtTotalEstimado.setForeground(new Color(0, 100, 0));
                    this.txtTotalEstimado.setToolTipText("Precio ajustado por destino intermedio: " + this.destinoReal);
                } else {
                    this.txtTotalEstimado.setForeground(Color.BLACK);
                    this.txtTotalEstimado.setToolTipText((String)null);
                }

                double factor = (double)1.0F;
                if (this.pasajeroSeleccionado != null) {
                    String tipo = this.pasajeroSeleccionado.getTipoPasajero();
                    if ("NIÑO".equals(tipo) || "ADOLESCENTE".equals(tipo) || "TERCERA_EDAD".equals(tipo) || "DISCAPACITADO".equals(tipo)) {
                        factor = (double)0.5F;
                    }
                }

                double total = precioBase * factor * (double)this.listaAsientosSeleccionados.size();
                this.txtTotalEstimado.setText(String.format("%.2f", total).replace(',', '.'));
            }
        } else {
            this.txtTotalEstimado.setText("0.00");
        }
    }

    private void guardar() {
        if (this.pasajeroSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un pasajero.");
        } else if (this.listaAsientosSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar al menos un asiento.");
        } else {
            Viaje viaje = this.viajeSeleccionado;
            if (viaje != null) {
                long var10000 = System.currentTimeMillis();
                String idReservaTemp = "R" + var10000 % 1000000L;
                String idEmpresa = "EMP001";
                if (viaje.getEmpresa() != null) {
                    idEmpresa = viaje.getEmpresa().getIdEmpresa();
                }

                String idPasajero = this.pasajeroSeleccionado.getIdPasajero();

                Fecha fechaReserva;
                try {
                    int d = Integer.parseInt(this.txtDia.getText().trim());
                    int m = Integer.parseInt(this.txtMes.getText().trim());
                    int a = Integer.parseInt(this.txtAnio.getText().trim());
                    fechaReserva = new Fecha(d, m, a);
                } catch (NumberFormatException var14) {
                    JOptionPane.showMessageDialog(this, "Fecha inválida. Use números.");
                    return;
                }

                if (viaje.getFechaSalida() != null && this.viajePreseleccionado == null) {
                    Calendar calSalida = Calendar.getInstance();
                    calSalida.set(viaje.getFechaSalida().getAnio(), viaje.getFechaSalida().getMes() - 1, viaje.getFechaSalida().getDia());
                    Calendar calReserva = Calendar.getInstance();
                    calReserva.set(fechaReserva.getAnio(), fechaReserva.getMes() - 1, fechaReserva.getDia());
                    calSalida.set(11, 0);
                    calSalida.set(12, 0);
                    calSalida.set(13, 0);
                    calSalida.set(14, 0);
                    calReserva.set(11, 0);
                    calReserva.set(12, 0);
                    calReserva.set(13, 0);
                    calReserva.set(14, 0);
                    long diffMillis = calSalida.getTimeInMillis() - calReserva.getTimeInMillis();
                    long diffDays = diffMillis / 86400000L;
                    if (diffDays < 0L) {
                        JOptionPane.showMessageDialog(this, "No se puede reservar para un viaje que ya salió.", "Fecha Inválida", 0);
                        return;
                    }

                    if (diffDays < 5L) {
                        JOptionPane.showMessageDialog(this, "Las reservas deben realizarse con al menos 5 días de anticipación.\nDías restantes: " + diffDays, "Anticipación Requerida", 2);
                        return;
                    }
                }

                String metodo = (String)this.cmbMetodoPago.getSelectedItem();
                int ninos = this.chkNinoBrazos.isSelected() ? 1 : 0;
                if (this.reservaEdicion == null) {
                    ResultadoOperacion res = this.controladorReservas.crearReserva(idReservaTemp, idPasajero, viaje.getIdViaje(), this.listaAsientosSeleccionados.size(), this.listaAsientosSeleccionados, idEmpresa, fechaReserva, metodo, ninos);
                    if (res.isExito()) {
                        try {
                            double totalCalculado = Double.parseDouble(this.txtTotalEstimado.getText().replace(',', '.'));
                            Reserva rGenerada = (Reserva)res.getDatos();
                            rGenerada.setPrecioTotal(totalCalculado);
                            FachadaArchivos.getInstance().actualiza(rGenerada);
                        } catch (Exception var13) {
                        }

                        JOptionPane.showMessageDialog(this, "Reserva creada con éxito!\n" + res.getMensaje());
                        this.reservaGenerada = (Reserva)res.getDatos();
                        this.confirmado = true;
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al crear reserva:\n" + res.getMensaje(), "Error", 0);
                        this.limpiarSeleccionAsientos();
                    }

                } else {
                    ResultadoOperacion res = this.controladorReservas.actualizarReserva(this.reservaEdicion, idPasajero, viaje.getIdViaje(), this.listaAsientosSeleccionados, idEmpresa, fechaReserva);
                    if (this.reservaEdicion != null && res.isExito()) {
                        try {
                            double totalCalculado = Double.parseDouble(this.txtTotalEstimado.getText().replace(',', '.'));
                            this.reservaEdicion.setPrecioTotal(totalCalculado);
                            FachadaArchivos.getInstance().actualiza(this.reservaEdicion);
                        } catch (Exception e) {
                            System.err.println("Error asegurando precio personalizado: " + e.getMessage());
                        }

                        JOptionPane.showMessageDialog(this, "Reserva actualizada correctamente.");
                        this.confirmado = true;
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al actualizar: " + res.getMensaje());
                    }

                }
            }
        }
    }

    public boolean isConfirmado() {
        return this.confirmado;
    }

    public Reserva getReservaGenerada() {
        return this.reservaGenerada;
    }

    private void agregarFiltroNumeros(final JTextField campo, final int maxDigitos) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                this.validar();
            }

            public void removeUpdate(DocumentEvent e) {
                this.validar();
            }

            public void insertUpdate(DocumentEvent e) {
                this.validar();
            }

            public void validar() {
                SwingUtilities.invokeLater(() -> {
                    String text = campo.getText();
                    String filtered = text.replaceAll("[^0-9]", "");
                    if (filtered.length() > maxDigitos) {
                        filtered = filtered.substring(0, maxDigitos);
                    }

                    if (!text.equals(filtered)) {
                        campo.setText(filtered);
                    }

                });
            }
        });
    }

    private class PanelSeleccionDestino extends JPanel {
        String ciudadSeleccionada = null;
        String origenViaje;
        String destinoViaje;
        List<String> rutaActual;
        double scale = (double)1.0F;
        int dx = 0;
        int dy = 0;

        public PanelSeleccionDestino(String origen, String destino) {
            this.origenViaje = DialogoFormularioRuta.normalizarCiudadEstatica(origen);
            this.destinoViaje = DialogoFormularioRuta.normalizarCiudadEstatica(destino);
            DialogoFormularioRuta.ResultadoRuta res = DialogoFormularioRuta.encontrarRutaMasCorta(this.origenViaje, this.destinoViaje);
            if (res != null) {
                this.rutaActual = res.camino;
            }

            this.setBackground(Color.WHITE);
            this.setToolTipText("Haga clic en una ciudad para seleccionarla como destino");
            this.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    Point click = e.getPoint();
                    String ciudadClickeada = null;
                    double minDist = Double.MAX_VALUE;

                    for(Map.Entry<String, Point> entry : DialogoFormularioRuta.COORDENADAS.entrySet()) {
                        String ciudad = (String)entry.getKey();
                        Point pModel = (Point)entry.getValue();
                        int pScreenX = (int)((double)pModel.x * PanelSeleccionDestino.this.scale) + PanelSeleccionDestino.this.dx;
                        int pScreenY = (int)((double)pModel.y * PanelSeleccionDestino.this.scale) + PanelSeleccionDestino.this.dy;
                        double dist = Math.sqrt(Math.pow((double)(click.x - pScreenX), (double)2.0F) + Math.pow((double)(click.y - pScreenY), (double)2.0F));
                        if (dist < (double)15.0F && dist < minDist) {
                            minDist = dist;
                            ciudadClickeada = ciudad;
                        }
                    }

                    if (ciudadClickeada != null) {
                        if (PanelSeleccionDestino.this.rutaActual != null && !PanelSeleccionDestino.this.rutaActual.contains(ciudadClickeada)) {
                            JOptionPane.showMessageDialog(PanelSeleccionDestino.this, "La ciudad seleccionada (" + ciudadClickeada + ") no forma parte de la ruta del viaje.\nSolo puede seleccionar ciudades que estén dentro del trayecto (línea azul).", "Destino Inválido", 2);
                            return;
                        }

                        double precioEstimado = (double)0.0F;
                        DialogoFormularioRuta.ResultadoRuta resRuta = DialogoFormularioRuta.encontrarRutaMasCorta(PanelSeleccionDestino.this.origenViaje, DialogoFormularioRuta.normalizarCiudadEstatica(ciudadClickeada));
                        if (resRuta != null) {
                            precioEstimado = (double)Math.round(resRuta.distanciaTotal * 0.08 * (double)100.0F) / (double)100.0F;
                        }

                        int confirm = JOptionPane.showConfirmDialog(PanelSeleccionDestino.this, String.format("¿Seleccionar %s como destino?\nPrecio estimado del tramo: $%.2f", ciudadClickeada, precioEstimado), "Confirmar Destino", 0);
                        if (confirm == 0) {
                            PanelSeleccionDestino.this.ciudadSeleccionada = ciudadClickeada;
                            Window win = SwingUtilities.getWindowAncestor(PanelSeleccionDestino.this);
                            if (win != null) {
                                win.dispose();
                            }
                        }
                    }

                }
            });
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int panelW = this.getWidth();
            int panelH = this.getHeight();
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE;
            if (!DialogoFormularioRuta.COORDENADAS.isEmpty()) {
                for(Point p : DialogoFormularioRuta.COORDENADAS.values()) {
                    if (p.x < minX) {
                        minX = p.x;
                    }

                    if (p.x > maxX) {
                        maxX = p.x;
                    }

                    if (p.y < minY) {
                        minY = p.y;
                    }

                    if (p.y > maxY) {
                        maxY = p.y;
                    }
                }

                minX -= 20;
                maxX += 20;
                minY -= 20;
                maxY += 20;
            } else {
                minX = 0;
                maxX = 400;
                minY = 0;
                maxY = 600;
            }

            int mapW = maxX - minX;
            int mapH = maxY - minY;
            double scaleX = (double)(panelW - 40) / (double)mapW;
            double scaleY = (double)(panelH - 40) / (double)mapH;
            this.scale = Math.min(scaleX, scaleY);
            this.dx = (panelW - (int)((double)mapW * this.scale)) / 2 - (int)((double)minX * this.scale);
            this.dy = (panelH - (int)((double)mapH * this.scale)) / 2 - (int)((double)minY * this.scale);
            AffineTransform oldTransform = g2.getTransform();
            g2.translate(this.dx, this.dy);
            g2.scale(this.scale, this.scale);
            g2.setColor(new Color(220, 220, 220));
            g2.setStroke(new BasicStroke(1.0F));

            for(String c1 : DialogoFormularioRuta.GRAFO_VIAL.keySet()) {
                Point p1 = (Point)DialogoFormularioRuta.COORDENADAS.get(c1);
                if (p1 != null) {
                    for(Object c2 : ((Map)DialogoFormularioRuta.GRAFO_VIAL.get(c1)).keySet()) {
                        Point p2 = (Point)DialogoFormularioRuta.COORDENADAS.get(c2);
                        if (p2 != null) {
                            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                        }
                    }
                }
            }

            if (this.rutaActual != null && this.rutaActual.size() > 1) {
                g2.setColor(Color.BLUE);
                g2.setStroke(new BasicStroke(3.0F));

                for(int i = 0; i < this.rutaActual.size() - 1; ++i) {
                    Point p1 = (Point)DialogoFormularioRuta.COORDENADAS.get(this.rutaActual.get(i));
                    Point p2 = (Point)DialogoFormularioRuta.COORDENADAS.get(this.rutaActual.get(i + 1));
                    if (p1 != null && p2 != null) {
                        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }
            }

            for(Map.Entry<String, Point> entry : DialogoFormularioRuta.COORDENADAS.entrySet()) {
                String ciudad = (String)entry.getKey();
                Point p = (Point)entry.getValue();
                boolean enRuta = this.rutaActual != null && this.rutaActual.contains(ciudad);
                boolean esInicioFin = this.rutaActual != null && (((String)this.rutaActual.get(0)).equals(ciudad) || ((String)this.rutaActual.get(this.rutaActual.size() - 1)).equals(ciudad));
                if (esInicioFin) {
                    g2.setColor(new Color(0, 128, 0));
                    g2.fillOval(p.x - 5, p.y - 5, 10, 10);
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Arial", 1, 11));
                } else if (enRuta) {
                    g2.setColor(Color.RED);
                    g2.fillOval(p.x - 4, p.y - 4, 8, 8);
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Arial", 1, 10));
                } else {
                    g2.setColor(Color.GRAY);
                    g2.fillOval(p.x - 2, p.y - 2, 4, 4);
                    g2.setFont(new Font("Arial", 0, 9));
                }

                g2.drawString(ciudad, p.x + 6, p.y + 4);
            }

            g2.setTransform(oldTransform);
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", 1, 12));
            g2.drawString("Haga clic en una ciudad para seleccionarla como destino", 10, 20);
        }
    }
}
