//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import control.ControladorConductores;
import control.ResultadoOperacion;
import excepciones.PersistenciaException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import objetosNegocio.Bus;
import objetosNegocio.Conductor;
import objetosNegocio.Empresa;
import objetosNegocio.Ruta;
import objetosNegocio.Viaje;
import objetosServicio.Fecha;
import persistencia.Buses;
import persistencia.Empresas;
import persistencia.Rutas;
import persistencia.Viajes;

public class DialogoFormularioViaje extends JDialog {
    private JTextField txtId;
    private JTextField txtDiaSalida;
    private JTextField txtMesSalida;
    private JTextField txtAnioSalida;
    private JTextField txtHoraSalida;
    private JTextField txtMinutoSalida;
    private JTextField txtDiaLlegada;
    private JTextField txtMesLlegada;
    private JTextField txtAnioLlegada;
    private JTextField txtHoraLlegada;
    private JTextField txtAsientos;
    private JTextField txtPrecio;
    private JTextField txtEmpresa;
    private JTextField txtBus;
    private JComboBox<String> cmbOrigen;
    private JComboBox<String> cmbDestino;
    private JComboBox<String> cmbEstado;
    private JComboBox<String> cmbConductor1;
    private JComboBox<String> cmbConductor2;
    private Viaje viaje;
    private boolean confirmado = false;
    private List<Empresa> empresas;
    private List<Bus> buses;
    private List<Ruta> rutas;
    private List<Conductor> conductores;
    private Empresa empresaPreseleccionada;
    private Empresa empresaSeleccionadaObj;
    private Bus busSeleccionadoObj;
    private Ruta rutaSeleccionadaObj;

    public DialogoFormularioViaje(JFrame padre, String titulo, Viaje v, Empresa empresaPreseleccionada) {
        super(padre, titulo, true);
        this.viaje = v;
        this.empresaPreseleccionada = empresaPreseleccionada;
        this.cargarDatos();
        this.initComponents();
        if (v != null) {
            this.cargarViaje();
        }

    }

    private void cargarDatos() {
        try {
            this.empresas = (new Empresas("empresas.dat")).lista();
            this.buses = (new Buses("buses.dat")).lista();
            this.rutas = (new Rutas("rutas.dat")).lista();
            ResultadoOperacion res = (new ControladorConductores()).listarConductores();
            if (res.isExito() && res.getDatos() != null) {
                this.conductores = (List)res.getDatos();
            } else {
                this.conductores = new ArrayList();
            }
        } catch (PersistenciaException e) {
            this.empresas = new ArrayList();
            this.buses = new ArrayList();
            this.rutas = new ArrayList();
            this.conductores = new ArrayList();
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage(), "Error", 0);
        }

    }

    private void initComponents() {
        this.setSize(800, 850);
        this.setLocationRelativeTo(this.getParent());
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = 2;
        g.insets = new Insets(8, 8, 8, 8);
        g.gridx = 0;
        g.gridy = 0;
        g.anchor = 17;
        p.add(new JLabel("ID Viaje:"), g);
        g.gridx = 1;
        this.txtId = new JTextField(20);
        this.txtId.setEnabled(false);
        this.txtId.setBackground(Color.LIGHT_GRAY);
        this.txtId.setText("(Se generará automáticamente)");
        p.add(this.txtId, g);
        g.gridx = 0;
        g.gridy = 1;
        p.add(new JLabel("Empresa: *"), g);
        g.gridx = 1;
        
        JPanel pEmpresa = new JPanel(new BorderLayout(5, 0));
        this.txtEmpresa = new JTextField();
        this.txtEmpresa.setEditable(false);
        JButton btnBuscarEmpresa = new JButton("🔍");
        btnBuscarEmpresa.addActionListener(e -> buscarEmpresa());
        pEmpresa.add(this.txtEmpresa, BorderLayout.CENTER);
        pEmpresa.add(btnBuscarEmpresa, BorderLayout.EAST);
        p.add(pEmpresa, g);

        g.gridx = 0;
        g.gridy = 2;
        p.add(new JLabel("Bus: *"), g);
        g.gridx = 1;
        
        JPanel pBus = new JPanel(new BorderLayout(5, 0));
        this.txtBus = new JTextField();
        this.txtBus.setEditable(false);
        JButton btnBuscarBus = new JButton("🔍");
        btnBuscarBus.addActionListener(e -> buscarBus());
        pBus.add(this.txtBus, BorderLayout.CENTER);
        pBus.add(btnBuscarBus, BorderLayout.EAST);
        p.add(pBus, g);

        g.gridx = 0;
        g.gridy = 3;
        p.add(new JLabel("Ciudad Origen: *"), g);
        g.gridx = 1;
        this.cmbOrigen = new JComboBox<>();
        this.cmbOrigen.addItem("-- Seleccione Origen --");
        this.cmbOrigen.addActionListener(e -> actualizarDestinos());
        p.add(this.cmbOrigen, g);

        g.gridx = 0;
        g.gridy = 4;
        p.add(new JLabel("Ciudad Destino: *"), g);
        g.gridx = 1;
        this.cmbDestino = new JComboBox<>();
        this.cmbDestino.addItem("-- Seleccione Destino --");
        this.cmbDestino.addActionListener(e -> buscarRutaAutomatica());
        p.add(this.cmbDestino, g);

        g.gridx = 0;
        g.gridy = 5;
        g.anchor = 18;
        JLabel lblFechaSalida = new JLabel("Fecha de Salida: *");
        lblFechaSalida.setFont(lblFechaSalida.getFont().deriveFont(1));
        p.add(lblFechaSalida, g);
        JPanel pFechaSalida = new JPanel(new FlowLayout(0, 8, 0));
        pFechaSalida.add(new JLabel("Día:"));
        this.txtDiaSalida = new JTextField(4);
        this.txtDiaSalida.setFont(new Font("Monospaced", 0, 14));
        this.agregarFiltroNumeros(this.txtDiaSalida, 2);
        this.agregarPlaceholder(this.txtDiaSalida, "DD");
        pFechaSalida.add(this.txtDiaSalida);
        pFechaSalida.add(new JLabel("/"));
        pFechaSalida.add(new JLabel("Mes:"));
        this.txtMesSalida = new JTextField(4);
        this.txtMesSalida.setFont(new Font("Monospaced", 0, 14));
        this.agregarFiltroNumeros(this.txtMesSalida, 2);
        this.agregarPlaceholder(this.txtMesSalida, "MM");
        pFechaSalida.add(this.txtMesSalida);
        pFechaSalida.add(new JLabel("/"));
        pFechaSalida.add(new JLabel("Año:"));
        this.txtAnioSalida = new JTextField(6);
        this.txtAnioSalida.setFont(new Font("Monospaced", 0, 14));
        this.agregarFiltroNumeros(this.txtAnioSalida, 4);
        this.agregarPlaceholder(this.txtAnioSalida, "AAAA");
        pFechaSalida.add(this.txtAnioSalida);
        pFechaSalida.add(new JLabel(" Hora:"));
        this.txtHoraSalida = new JTextField(2);
        this.agregarFiltroNumeros(this.txtHoraSalida, 2);
        pFechaSalida.add(this.txtHoraSalida);
        pFechaSalida.add(new JLabel(":"));
        this.txtMinutoSalida = new JTextField(2);
        this.agregarFiltroNumeros(this.txtMinutoSalida, 2);
        pFechaSalida.add(this.txtMinutoSalida);
        DocumentListener calcListener = new SimpleDocumentListener(() -> this.calcularLlegada());
        this.txtDiaSalida.getDocument().addDocumentListener(calcListener);
        this.txtMesSalida.getDocument().addDocumentListener(calcListener);
        this.txtAnioSalida.getDocument().addDocumentListener(calcListener);
        this.txtHoraSalida.getDocument().addDocumentListener(calcListener);
        this.txtMinutoSalida.getDocument().addDocumentListener(calcListener);
        g.gridx = 1;
        g.gridy = 5;
        g.anchor = 17;
        p.add(pFechaSalida, g);
        g.gridx = 0;
        g.gridy = 6;
        g.anchor = 18;
        JLabel lblFechaLlegada = new JLabel("Fecha de Llegada: *");
        p.add(lblFechaLlegada, g);
        JPanel pFechaLlegada = new JPanel(new FlowLayout(0, 8, 0));
        pFechaLlegada.add(new JLabel("Día:"));
        this.txtDiaLlegada = new JTextField(4);
        this.agregarFiltroNumeros(this.txtDiaLlegada, 2);
        pFechaLlegada.add(this.txtDiaLlegada);
        pFechaLlegada.add(new JLabel("/"));
        pFechaLlegada.add(new JLabel("Mes:"));
        this.txtMesLlegada = new JTextField(4);
        this.agregarFiltroNumeros(this.txtMesLlegada, 2);
        pFechaLlegada.add(this.txtMesLlegada);
        pFechaLlegada.add(new JLabel("/"));
        pFechaLlegada.add(new JLabel("Año:"));
        this.txtAnioLlegada = new JTextField(6);
        this.agregarFiltroNumeros(this.txtAnioLlegada, 4);
        pFechaLlegada.add(this.txtAnioLlegada);
        pFechaLlegada.add(new JLabel(" Hora:"));
        this.txtHoraLlegada = new JTextField(5);
        pFechaLlegada.add(this.txtHoraLlegada);
        JLabel lblAuto = new JLabel("(Calculado/Editable)");
        lblAuto.setFont(new Font("Arial", 2, 10));
        pFechaLlegada.add(lblAuto);
        g.gridx = 1;
        g.gridy = 6;
        g.anchor = 17;
        p.add(pFechaLlegada, g);
        g.gridx = 0;
        g.gridy = 7;
        p.add(new JLabel("Precio Especial ($):"), g);
        g.gridx = 1;
        this.txtPrecio = new JTextField("0.0");
        this.txtPrecio.setToolTipText("Deje en 0.0 para usar el precio de la Ruta");
        p.add(this.txtPrecio, g);
        g.gridx = 0;
        g.gridy = 8;
        g.anchor = 17;
        p.add(new JLabel("Asientos Disponibles: *"), g);
        g.gridx = 1;
        this.txtAsientos = new JTextField(20);
        this.txtAsientos.setEditable(false);
        p.add(this.txtAsientos, g);
        g.gridx = 0;
        g.gridy = 9;
        p.add(new JLabel("Conductor 1: *"), g);
        g.gridx = 1;
        this.cmbConductor1 = new JComboBox<>();
        this.cmbConductor1.addItem("-- Seleccione Conductor Principal --");
        p.add(this.cmbConductor1, g);
        g.gridx = 0;
        g.gridy = 10;
        p.add(new JLabel("Conductor 2:"), g);
        g.gridx = 1;
        this.cmbConductor2 = new JComboBox<>();
        this.cmbConductor2.addItem("-- Seleccione Conductor Relevo --");
        this.cmbConductor2.setEnabled(false);
        p.add(this.cmbConductor2, g);
        g.gridx = 0;
        g.gridy = 11;
        p.add(new JLabel("Estado: *"), g);
        g.gridx = 1;
        this.cmbEstado = new JComboBox<>(new String[]{"Programado", "En Curso", "Finalizado", "Cancelado"});
        p.add(this.cmbEstado, g);
        g.gridx = 0;
        g.gridy = 12;
        g.gridwidth = 2;
        JLabel lblNota = new JLabel("* Campos obligatorios");
        lblNota.setFont(new Font("Arial", 2, 11));
        lblNota.setForeground(Color.GRAY);
        p.add(lblNota, g);
        JPanel pBtn = new JPanel(new FlowLayout(1, 15, 10));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        btnGuardar.setPreferredSize(new Dimension(120, 35));
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnGuardar.addActionListener((ex) -> this.guardar());
        btnCancelar.addActionListener((ex) -> this.dispose());
        pBtn.add(btnGuardar);
        pBtn.add(btnCancelar);
        if (this.empresaPreseleccionada != null) {
            this.empresaSeleccionadaObj = this.empresaPreseleccionada;
            this.txtEmpresa.setText(this.empresaPreseleccionada.getNombre());
            btnBuscarEmpresa.setEnabled(false);
            this.cargarOrigenes();
            this.filtrarListasPorEmpresa();
        }

        this.add(p, "Center");
        this.add(pBtn, "South");
    }

    private void buscarEmpresa() {
        DialogoBuscarEmpresa d = new DialogoBuscarEmpresa(this);
        d.setVisible(true);
        if (d.getEmpresaSeleccionada() != null) {
            this.empresaSeleccionadaObj = d.getEmpresaSeleccionada();
            this.txtEmpresa.setText(this.empresaSeleccionadaObj.getNombre());
            // Limpiar selecciones dependientes
            this.busSeleccionadoObj = null;
            this.txtBus.setText("");
            this.rutaSeleccionadaObj = null;
            this.cargarOrigenes();
            this.filtrarListasPorEmpresa();
        }
    }

    private void buscarBus() {
        if (this.empresaSeleccionadaObj == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una empresa primero.");
            return;
        }
        DialogoBuscarBus d = new DialogoBuscarBus(this, this.empresaSeleccionadaObj);
        d.setVisible(true);
        if (d.getBusSeleccionado() != null) {
            this.busSeleccionadoObj = d.getBusSeleccionado();
            this.txtBus.setText(this.busSeleccionadoObj.getPlaca() + " - " + this.busSeleccionadoObj.getMarca());
            this.actualizarCapacidadBus();
        }
    }

    private void cargarOrigenes() {
        this.cmbOrigen.removeAllItems();
        this.cmbOrigen.addItem("-- Seleccione Origen --");
        this.cmbDestino.removeAllItems();
        this.cmbDestino.addItem("-- Seleccione Destino --");

        if (this.empresaSeleccionadaObj != null) {
            Set<String> origenes = new HashSet<>();
            for (Ruta r : this.rutas) {
                if (r.getEmpresa() != null && r.getEmpresa().getIdEmpresa().equals(this.empresaSeleccionadaObj.getIdEmpresa())) {
                    origenes.add(r.getOrigen());
                }
            }
            for (String o : origenes) {
                this.cmbOrigen.addItem(o);
            }
        }
    }

    private void actualizarDestinos() {
        this.cmbDestino.removeAllItems();
        this.cmbDestino.addItem("-- Seleccione Destino --");

        if (this.empresaSeleccionadaObj != null && this.cmbOrigen.getSelectedIndex() > 0) {
            String origenSel = (String) this.cmbOrigen.getSelectedItem();
            Set<String> destinos = new HashSet<>();
            for (Ruta r : this.rutas) {
                if (r.getEmpresa() != null && r.getEmpresa().getIdEmpresa().equals(this.empresaSeleccionadaObj.getIdEmpresa()) &&
                        r.getOrigen().equals(origenSel)) {
                    destinos.add(r.getDestino());
                }
            }
            for (String d : destinos) {
                this.cmbDestino.addItem(d);
            }
        }
    }

    private void buscarRutaAutomatica() {
        this.rutaSeleccionadaObj = null;
        if (this.cmbOrigen.getSelectedIndex() > 0 && this.cmbDestino.getSelectedIndex() > 0) {
            String origen = (String) this.cmbOrigen.getSelectedItem();
            String destino = (String) this.cmbDestino.getSelectedItem();

            for (Ruta r : this.rutas) {
                if (r.getEmpresa() != null && r.getEmpresa().getIdEmpresa().equals(this.empresaSeleccionadaObj.getIdEmpresa()) &&
                        r.getOrigen().equals(origen) && r.getDestino().equals(destino)) {
                    this.rutaSeleccionadaObj = r;
                    break;
                }
            }

            if (this.rutaSeleccionadaObj != null) {
                this.calcularLlegada();
            }
        }
    }

    private void filtrarListasPorEmpresa() {
        this.cmbConductor1.removeAllItems();
        this.cmbConductor1.addItem("-- Seleccione Conductor Principal --");
        this.cmbConductor2.removeAllItems();
        this.cmbConductor2.addItem("-- Seleccione Conductor Relevo --");
        if (this.empresaSeleccionadaObj != null) {
            String idEmpresa = this.empresaSeleccionadaObj.getIdEmpresa();

            for(Conductor c : this.conductores) {
                if (c.getEmpresa() != null && c.getEmpresa().getIdEmpresa() != null && c.getEmpresa().getIdEmpresa().trim().equalsIgnoreCase(idEmpresa)) {
                    String var13 = c.getIdConductor();
                    String item = var13 + " - " + c.getNombre();
                    this.cmbConductor1.addItem(item);
                    this.cmbConductor2.addItem(item);
                }
            }

        }
    }

    private void actualizarCapacidadBus() {
        if (this.busSeleccionadoObj != null) {
            Bus b = this.busSeleccionadoObj;
            this.txtAsientos.setText(String.valueOf(b.getCapacidad()));
            if ("Mantenimiento".equalsIgnoreCase(b.getEstado()) || "INACTIVO".equalsIgnoreCase(b.getEstado())) {
                JOptionPane.showMessageDialog(this, "¡ADVERTENCIA! El bus seleccionado está en " + b.getEstado() + ".\nNo se puede asignar a un viaje.", "Bus no disponible", 2);
                this.busSeleccionadoObj = null;
                this.txtBus.setText("");
                this.txtAsientos.setText("");
            }
        } else {
            this.txtAsientos.setText("");
        }

    }

    private void calcularLlegada() {
        try {
            if (this.rutaSeleccionadaObj == null) {
                return;
            }

            if (this.txtDiaSalida.getText().isEmpty() || this.txtMesSalida.getText().isEmpty() || this.txtAnioSalida.getText().isEmpty()) {
                return;
            }
            
            double distancia = this.rutaSeleccionadaObj.getDistancia();
            double tiempoEstimado = this.rutaSeleccionadaObj.getTiempoEstimado();

            int minutosDuracion = tiempoEstimado > (double)0.0F ? (int)(tiempoEstimado * (double)60.0F) : (int)(distancia / (double)80.0F * (double)60.0F);
            int d = Integer.parseInt(this.txtDiaSalida.getText());
            int m = Integer.parseInt(this.txtMesSalida.getText()) - 1;
            int a = Integer.parseInt(this.txtAnioSalida.getText());
            int hora = this.txtHoraSalida.getText().isEmpty() ? 0 : Integer.parseInt(this.txtHoraSalida.getText());
            int min = this.txtMinutoSalida.getText().isEmpty() ? 0 : Integer.parseInt(this.txtMinutoSalida.getText());
            Calendar cal = Calendar.getInstance();
            cal.set(a, m, d, hora, min);
            cal.add(12, minutosDuracion);
            this.txtDiaLlegada.setText(String.valueOf(cal.get(5)));
            this.txtMesLlegada.setText(String.valueOf(cal.get(2) + 1));
            this.txtAnioLlegada.setText(String.valueOf(cal.get(1)));
            this.txtHoraLlegada.setText(String.format("%02d:%02d", cal.get(11), cal.get(12)));
            if (minutosDuracion > 480) {
                this.cmbConductor2.setEnabled(true);
                this.cmbConductor2.setToolTipText("Requerido por ley para viajes > 8 horas");
            } else {
                this.cmbConductor2.setEnabled(false);
                this.cmbConductor2.setSelectedIndex(0);
            }
        } catch (Exception var14) {
        }

    }

    private void cargarViaje() {
        this.txtId.setText(this.viaje.getIdViaje());
        this.txtId.setEnabled(false);
        if (this.viaje.getEmpresa() != null) {
            this.empresaSeleccionadaObj = this.viaje.getEmpresa();
            this.txtEmpresa.setText(this.empresaSeleccionadaObj.getNombre());
            this.cargarOrigenes();
            this.filtrarListasPorEmpresa();
        }

        if (this.viaje.getBus() != null) {
            this.busSeleccionadoObj = this.viaje.getBus();
            this.txtBus.setText(this.busSeleccionadoObj.getPlaca() + " - " + this.busSeleccionadoObj.getMarca());
        }

        if (this.viaje.getRuta() != null) {
            this.rutaSeleccionadaObj = this.viaje.getRuta();
            this.cmbOrigen.setSelectedItem(this.rutaSeleccionadaObj.getOrigen());
            this.cmbDestino.setSelectedItem(this.rutaSeleccionadaObj.getDestino());
        }

        this.txtAsientos.setText(String.valueOf(this.viaje.getAsientosDisponibles()));
        this.txtPrecio.setText(String.valueOf(this.viaje.getPrecio()));
        this.cmbEstado.setSelectedItem(this.viaje.getEstado());
        if (this.viaje.getHoraSalida() != null && this.viaje.getHoraSalida().contains(":")) {
            String[] partes = this.viaje.getHoraSalida().split(":");
            this.txtHoraSalida.setText(partes[0]);
            this.txtMinutoSalida.setText(partes[1]);
        }

        this.txtHoraLlegada.setText(this.viaje.getHoraLlegada());
        if (this.viaje.getFechaSalida() != null) {
            Fecha f = this.viaje.getFechaSalida();
            this.txtDiaSalida.setText(String.format("%02d", f.getDia()));
            this.txtMesSalida.setText(String.format("%02d", f.getMes()));
            this.txtAnioSalida.setText(String.valueOf(f.getAnio()));
        }

        if (this.viaje.getFechaLlegada() != null) {
            Fecha f = this.viaje.getFechaLlegada();
            this.txtDiaLlegada.setText(String.format("%02d", f.getDia()));
            this.txtMesLlegada.setText(String.format("%02d", f.getMes()));
            this.txtAnioLlegada.setText(String.valueOf(f.getAnio()));
        }

        if (this.viaje.getConductor1() != null) {
            String c1 = this.viaje.getConductor1().getIdConductor();

            for(int i = 0; i < this.cmbConductor1.getItemCount(); ++i) {
                if (((String)this.cmbConductor1.getItemAt(i)).startsWith(c1)) {
                    this.cmbConductor1.setSelectedIndex(i);
                    break;
                }
            }
        }

        if (this.viaje.getConductor2() != null) {
            String c2 = this.viaje.getConductor2().getIdConductor();

            for(int i = 0; i < this.cmbConductor2.getItemCount(); ++i) {
                if (((String)this.cmbConductor2.getItemAt(i)).startsWith(c2)) {
                    this.cmbConductor2.setSelectedIndex(i);
                    break;
                }
            }

            this.cmbConductor2.setEnabled(true);
        }

    }

    private void guardar() {
        if (this.empresaSeleccionadaObj == null) {
            this.mostrarError("Seleccione una Empresa válida");
            // Focus logic if needed
        } else if (this.busSeleccionadoObj == null) {
            this.mostrarError("Seleccione un Bus válido");
            // Focus logic
        } else if (this.rutaSeleccionadaObj == null) {
            this.mostrarError("Seleccione una Ruta válida");
            // Focus logic
        } else if (this.cmbConductor1.getSelectedIndex() == 0) {
            this.mostrarError("Debe asignar al menos un Conductor Principal");
            this.cmbConductor1.requestFocus();
        } else {
            String diaSalida = this.txtDiaSalida.getText().trim();
            String mesSalida = this.txtMesSalida.getText().trim();
            String anioSalida = this.txtAnioSalida.getText().trim();
            String horaSalida = this.txtHoraSalida.getText().trim();
            if (!diaSalida.isEmpty() && !mesSalida.isEmpty() && !anioSalida.isEmpty() && !horaSalida.isEmpty()) {
                this.calcularLlegada();
                String diaLlegada = this.txtDiaLlegada.getText();
                String mesLlegada = this.txtMesLlegada.getText();
                String anioLlegada = this.txtAnioLlegada.getText();

                try {
                    int diaSal = Integer.parseInt(diaSalida);
                    int mesSal = Integer.parseInt(mesSalida);
                    int anioSal = Integer.parseInt(anioSalida);
                    if (!this.validarFecha(diaSal, mesSal, anioSal)) {
                        this.mostrarError("Fecha de salida inválida\nDía: 1-31, Mes: 1-12, Año: mayor a 1900");
                        return;
                    }

                    int diaLleg = Integer.parseInt(diaLlegada);
                    int mesLleg = Integer.parseInt(mesLlegada);
                    int anioLleg = Integer.parseInt(anioLlegada);
                    if (!this.esFechaPosterior(anioSal, mesSal, diaSal, anioLleg, mesLleg, diaLleg)) {
                        this.mostrarError("La fecha de llegada debe ser posterior o igual a la fecha de salida");
                        return;
                    }

                    try {
                        String idBus = this.busSeleccionadoObj.getIdBus();
                        for(Bus b : this.buses) { // Or use busSeleccionadoObj directly
                            if (b.getIdBus().equals(idBus) && "Mantenimiento".equalsIgnoreCase(b.getEstado())) {
                                this.mostrarError("El bus seleccionado está en Mantenimiento.");
                                return;
                            }
                        }

                        Fecha fSalida = new Fecha(diaSal, mesSal, anioSal);
                        Fecha fLlegada = new Fecha(diaLleg, mesLleg, anioLleg);
                        Viajes viajesDB = new Viajes("viajes.dat");

                        for(Viaje v : viajesDB.lista()) {
                            if ((this.viaje == null || !v.getIdViaje().equals(this.viaje.getIdViaje())) && v.getBus() != null && v.getBus().getIdBus().equals(idBus) && !"Cancelado".equals(v.getEstado()) && this.haySolapamiento(fSalida, fLlegada, v.getFechaSalida(), v.getFechaLlegada())) {
                                this.mostrarError("El bus seleccionado ya tiene un viaje programado en ese rango de fechas.\nConflicto con Viaje: " + v.getIdViaje());
                                return;
                            }
                        }
                    } catch (Exception var30) {
                    }

                    if (this.cmbConductor2.isEnabled() && this.cmbConductor2.getSelectedIndex() == 0) {
                        this.mostrarError("ATENCIÓN: Este viaje supera las 8 horas.\nPor ley, debe asignar un segundo conductor.");
                        this.cmbConductor2.requestFocus();
                        return;
                    }

                    int asientos = 0;

                    try {
                        asientos = Integer.parseInt(this.txtAsientos.getText().trim());
                    } catch (Exception var29) {
                        this.mostrarError("Error al obtener capacidad del bus");
                        return;
                    }

                    double precio = (double)0.0F;

                    try {
                        precio = Double.parseDouble(this.txtPrecio.getText().trim());
                    } catch (Exception var28) {
                    }

                    if (this.viaje == null) {
                        this.viaje = new Viaje();
                    }

                    String idEmpresa = this.empresaSeleccionadaObj.getIdEmpresa();
                    String idBus = this.busSeleccionadoObj.getIdBus();
                    String idRuta = this.rutaSeleccionadaObj.getIdRuta();
                    String idCond1 = ((String)this.cmbConductor1.getSelectedItem()).split(" - ")[0].trim();
                    this.viaje.setConductor1(new Conductor(idCond1));
                    if (this.cmbConductor2.getSelectedIndex() > 0) {
                        String idCond2 = ((String)this.cmbConductor2.getSelectedItem()).split(" - ")[0].trim();
                        this.viaje.setConductor2(new Conductor(idCond2));
                    } else {
                        this.viaje.setConductor2((Conductor)null);
                    }

                    this.viaje.setEmpresa(new Empresa(idEmpresa));
                    this.viaje.setBus(new Bus(idBus));
                    this.viaje.setRuta(new Ruta(idRuta));
                    this.viaje.setAsientosDisponibles(asientos);
                    this.viaje.setEstado((String)this.cmbEstado.getSelectedItem());
                    this.viaje.setPrecio(precio);
                    String minSal = this.txtMinutoSalida.getText().isEmpty() ? "00" : String.format("%02d", Integer.parseInt(this.txtMinutoSalida.getText()));
                    String horaSal = this.txtHoraSalida.getText().isEmpty() ? "00" : String.format("%02d", Integer.parseInt(this.txtHoraSalida.getText()));
                    this.viaje.setHoraSalida(horaSal + ":" + minSal);
                    this.viaje.setHoraLlegada(this.txtHoraLlegada.getText());
                    Fecha fechaSalida = new Fecha(diaSal, mesSal, anioSal);
                    Fecha fechaLlegada = new Fecha(diaLleg, mesLleg, anioLleg);
                    this.viaje.setFechaSalida(fechaSalida);
                    this.viaje.setFechaLlegada(fechaLlegada);
                    this.confirmado = true;
                    this.dispose();
                } catch (NumberFormatException var31) {
                    this.mostrarError("Los valores de fecha y asientos deben ser números válidos");
                } catch (Exception e) {
                    this.mostrarError("Error al guardar: " + e.getMessage());
                }

            } else {
                this.mostrarError("Complete todos los campos de Fecha de Salida (Día, Mes, Año)");
                if (diaSalida.isEmpty()) {
                    this.txtDiaSalida.requestFocus();
                } else if (mesSalida.isEmpty()) {
                    this.txtMesSalida.requestFocus();
                } else {
                    this.txtAnioSalida.requestFocus();
                }

            }
        }
    }

    private boolean validarFecha(int dia, int mes, int anio) {
        if (dia >= 1 && dia <= 31) {
            if (mes >= 1 && mes <= 12) {
                if (anio >= 1900 && anio <= 2100) {
                    if (mes == 2 && dia > 29) {
                        return false;
                    } else {
                        return mes != 4 && mes != 6 && mes != 9 && mes != 11 || dia <= 30;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean esFechaPosterior(int anio1, int mes1, int dia1, int anio2, int mes2, int dia2) {
        if (anio2 > anio1) {
            return true;
        } else if (anio2 < anio1) {
            return false;
        } else if (mes2 > mes1) {
            return true;
        } else if (mes2 < mes1) {
            return false;
        } else {
            return dia2 >= dia1;
        }
    }

    private boolean haySolapamiento(Fecha inicio1, Fecha fin1, Fecha inicio2, Fecha fin2) {
        if (inicio1 != null && fin1 != null && inicio2 != null && fin2 != null) {
            long i1 = this.fechaALong(inicio1);
            long f1 = this.fechaALong(fin1);
            long i2 = this.fechaALong(inicio2);
            long f2 = this.fechaALong(fin2);
            return f1 >= i2 && f2 >= i1;
        } else {
            return false;
        }
    }

    private long fechaALong(Fecha f) {
        return (long)f.getAnio() * 10000L + (long)f.getMes() * 100L + (long)f.getDia();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de Validación", 0);
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
                        int pos = campo.getCaretPosition();
                        campo.setText(filtered);

                        try {
                            campo.setCaretPosition(Math.min(pos, filtered.length()));
                        } catch (IllegalArgumentException var6) {
                            campo.setCaretPosition(filtered.length());
                        }
                    }

                });
            }
        });
    }

    private void agregarPlaceholder(JTextField campo, String placeholder) {
        campo.setToolTipText(placeholder);
    }

    public Viaje getViaje() {
        return this.viaje;
    }

    public boolean isConfirmado() {
        return this.confirmado;
    }

    private static class SimpleDocumentListener implements DocumentListener {
        private final Runnable action;

        public SimpleDocumentListener(Runnable action) {
            this.action = action;
        }

        public void insertUpdate(DocumentEvent e) {
            this.action.run();
        }

        public void removeUpdate(DocumentEvent e) {
            this.action.run();
        }

        public void changedUpdate(DocumentEvent e) {
            this.action.run();
        }
    }
}
