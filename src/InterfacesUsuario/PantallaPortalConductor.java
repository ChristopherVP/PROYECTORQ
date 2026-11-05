package InterfacesUsuario;

import control.ControladorNovedades;
import control.ControladorCompras;
import control.ControladorPortalConductor;
import control.ResultadoOperacion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import objetosNegocio.Novedad;
import objetosNegocio.Usuario;
import objetosNegocio.Viaje;

/**
 * Portal del conductor: solo ve los viajes que el administrador le asignó y puede enviarle reportes
 * (no puede realizar el viaje, falla mecánica, queja...). No puede crear ni modificar viajes.
 */
public class PantallaPortalConductor extends JFrame {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Usuario usuario;
    private final ControladorPortalConductor portal = new ControladorPortalConductor();
    private final ControladorNovedades novedades = new ControladorNovedades();
    private final ControladorCompras compras = new ControladorCompras();

    private final DefaultTableModel modeloViajes = new DefaultTableModel(new String[]{"Viaje", "Salida", "Ruta", "Bus", "Pasajeros", "Estado", "Mi función"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JCheckBox chkProximos = new JCheckBox("Solo próximos", true);
    private List<Viaje> viajes = new ArrayList<>();

    private final JComboBox<String> cmbTipo = new JComboBox<>();
    private final List<String> clavesTipo = new ArrayList<>(ControladorNovedades.TIPOS.keySet());
    private final JComboBox<String> cmbViaje = new JComboBox<>();
    private final List<String> idsViaje = new ArrayList<>();
    private final JTextField txtAsunto = Tema.campo(30);
    private final JTextArea txtMensaje = new JTextArea(6, 30);
    private final JLabel lblContador = Tema.etiqueta("0 / 300");
    private final JLabel lblMensaje = Tema.mensaje();
    private javax.swing.JButton btnCorreo;
    private Novedad ultimaNovedad;

    private final DefaultTableModel modeloReportes = new DefaultTableModel(new String[]{"Fecha", "Tipo", "Asunto", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable tablaReportes = new JTable(modeloReportes);
    private final JTextArea detalle = new JTextArea();
    private List<Novedad> reportes = new ArrayList<>();

    public PantallaPortalConductor(Usuario usuario) {
        this.usuario = usuario;
        setTitle("RQ - Conductor: " + usuario.getNombre());
        setIconImage(IconoRQ.crear(64));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(crearCabecera(), BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Tema.fuente(Font.BOLD, 14f));
        tabs.addTab("Mis viajes", crearPanelViajes());
        tabs.addTab("Reportar al administrador", crearPanelReporte());
        tabs.addTab("Mis reportes", crearPanelReportes());
        add(tabs, BorderLayout.CENTER);
        cargarViajes();
        cargarReportes();
        setPreferredSize(new Dimension(1000, 680));
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel crearCabecera() {
        JPanel p = new JPanel(new BorderLayout(14, 0)) {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setPaint(new GradientPaint(0, 0, Tema.AZUL_OSCURO, getWidth(), 0, Tema.AZUL));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.dispose();
            }
        };
        p.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        p.add(new JLabel(IconoRQ.icono(46)), BorderLayout.WEST);
        JLabel saludo = new JLabel("Hola, " + usuario.getNombre());
        saludo.setForeground(Color.WHITE);
        saludo.setFont(Tema.fuente(Font.BOLD, 18f));
        p.add(saludo, BorderLayout.CENTER);
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        derecha.setOpaque(false);
        javax.swing.JButton perfil = Tema.boton("Mi perfil", false);
        perfil.addActionListener(e -> new DialogoPerfil(this, usuario, () -> saludo.setText("Hola, " + usuario.getNombre())).setVisible(true));
        javax.swing.JButton salir = Tema.boton("Cerrar sesión", false);
        salir.addActionListener(e -> Navegador.cerrarSesion(this));
        derecha.add(perfil);
        derecha.add(salir);
        p.add(derecha, BorderLayout.EAST);
        return p;
    }

    // ------------------------------------------------------------------ mis viajes

    private JPanel crearPanelViajes() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        JTable tabla = new JTable(modeloViajes);
        Tema.tabla(tabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(Color.WHITE);
        p.add(scroll, BorderLayout.CENTER);
        chkProximos.setOpaque(false);
        chkProximos.addActionListener(e -> cargarViajes());
        javax.swing.JButton actualizar = Tema.boton("Actualizar", false);
        actualizar.addActionListener(e -> cargarViajes());
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        arriba.setOpaque(false);
        arriba.add(chkProximos);
        arriba.add(actualizar);
        JLabel nota = Tema.etiqueta("Estos viajes los asigna el administrador. Si no puedes realizar alguno, repórtalo en la pestaña \"Reportar al administrador\".");
        nota.setForeground(Tema.TEXTO_SUAVE);
        arriba.add(nota);
        p.add(arriba, BorderLayout.NORTH);
        return p;
    }

    private void cargarViajes() {
        modeloViajes.setRowCount(0);
        ResultadoOperacion r = portal.viajesAsignados(usuario);
        if (!r.isExito()) {
            Tema.error(this, r.getMensaje());
            return;
        }
        @SuppressWarnings("unchecked")
        List<Viaje> todos = (List<Viaje>) r.getDatos();
        viajes = new ArrayList<>();
        idsViaje.clear();
        cmbViaje.removeAllItems();
        cmbViaje.addItem("(Sin viaje específico)");
        idsViaje.add("");
        for (Viaje v : todos) {
            LocalDateTime salida = ControladorCompras.salidaDe(v);
            boolean proximo = salida != null && salida.isAfter(LocalDateTime.now().minusHours(6));
            boolean cancelado = v.getEstado() != null && v.getEstado().toLowerCase().startsWith("cancel");
            if (chkProximos.isSelected() && (!proximo || cancelado)) {
                continue;
            }
            viajes.add(v);
            int ocupados = v.getBus() != null ? Math.max(0, v.getBus().getCapacidad() - v.getAsientosDisponibles()) : 0;
            String funcion = v.getConductor1() != null && usuario.getIdPerfil().equals(v.getConductor1().getIdConductor()) ? "Principal" : "Auxiliar";
            String ruta = compras.describirRuta(v);
            modeloViajes.addRow(new Object[]{v.getIdViaje(), salida == null ? "" : salida.format(FORMATO), ruta,
                    v.getBus() != null ? v.getBus().getPlaca() : "", ocupados + (v.getBus() != null ? " / " + v.getBus().getCapacidad() : ""), v.getEstado(), funcion});
            if (proximo && !cancelado) {
                cmbViaje.addItem(v.getIdViaje() + " · " + ruta + " · " + (salida == null ? "" : salida.format(FORMATO)));
                idsViaje.add(v.getIdViaje());
            }
        }
    }

    // ------------------------------------------------------------------ reportar

    private JPanel crearPanelReporte() {
        for (String clave : clavesTipo) {
            cmbTipo.addItem(ControladorNovedades.TIPOS.get(clave));
        }
        txtMensaje.setLineWrap(true);
        txtMensaje.setWrapStyleWord(true);
        txtMensaje.setFont(Tema.fuente(Font.PLAIN, 14f));
        txtMensaje.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        txtMensaje.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void c() {
                int n = txtMensaje.getText().length();
                lblContador.setText(n + " / 300");
                lblContador.setForeground(n > 300 ? Tema.ERROR : Tema.TEXTO_SUAVE);
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { c(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { c(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { c(); }
        });
        JScrollPane scrollMensaje = new JScrollPane(txtMensaje);
        scrollMensaje.setBorder(BorderFactory.createLineBorder(Tema.BORDE));

        JPanel f = Tema.panelFormulario();
        Tema.fila(f, 0, "Tipo de reporte", cmbTipo);
        Tema.fila(f, 1, "Viaje relacionado", cmbViaje);
        Tema.fila(f, 2, "Asunto", txtAsunto);
        Tema.fila(f, 3, "Mensaje", scrollMensaje);
        Tema.fila(f, 4, "", lblContador);

        javax.swing.JButton enviar = Tema.boton("Enviar al administrador", true);
        enviar.addActionListener(e -> enviarReporte());
        btnCorreo = Tema.boton("Enviar también por correo", false);
        btnCorreo.setEnabled(false);
        btnCorreo.addActionListener(e -> abrirCorreo());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        botones.setOpaque(false);
        botones.add(btnCorreo);
        botones.add(enviar);

        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Tema.FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(18, 40, 18, 40));
        JLabel nota = Tema.nota(740, "<html>Usa este formulario si <b>no puedes realizar un viaje</b>, el bus tiene una falla o quieres presentar una queja. El administrador recibirá el reporte y te responderá aquí.</html>");
        nota.setForeground(Tema.TEXTO_SUAVE);
        p.add(nota, BorderLayout.NORTH);
        p.add(f, BorderLayout.CENTER);
        JPanel sur = new JPanel(new BorderLayout(0, 6));
        sur.setOpaque(false);
        sur.add(lblMensaje, BorderLayout.NORTH);
        sur.add(botones, BorderLayout.CENTER);
        p.add(sur, BorderLayout.SOUTH);
        return p;
    }

    private void enviarReporte() {
        int tipo = cmbTipo.getSelectedIndex();
        int viaje = Math.max(0, cmbViaje.getSelectedIndex());
        ResultadoOperacion r = novedades.reportar(usuario, idsViaje.get(Math.min(viaje, idsViaje.size() - 1)), clavesTipo.get(tipo), txtAsunto.getText(), txtMensaje.getText());
        Tema.mostrarMensaje(lblMensaje, r.getMensaje(), r.isExito());
        if (!r.isExito()) {
            return;
        }
        ultimaNovedad = (Novedad) r.getDatos();
        btnCorreo.setEnabled(!novedades.correosDeDestino(usuario).isEmpty());
        cargarReportes();
    }

    private void abrirCorreo() {
        List<String> destinos = novedades.correosDeDestino(usuario);
        if (ultimaNovedad == null || destinos.isEmpty()) {
            Tema.error(this, "No hay un correo de administrador registrado para este reporte.");
            return;
        }
        try {
            String asunto = "Reporte de conductor: " + ultimaNovedad.getAsunto();
            String cuerpo = usuario.getNombre() + " (" + usuario.getCedula() + ")\n\n" + ultimaNovedad.getMensaje();
            URI uri = new URI("mailto:" + String.join(",", destinos) + "?subject=" + codificar(asunto) + "&body=" + codificar(cuerpo));
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.MAIL)) {
                Desktop.getDesktop().mail(uri);
            } else {
                Tema.error(this, "Este equipo no tiene un programa de correo configurado. El reporte ya quedó registrado en el sistema.");
            }
        } catch (Exception e) {
            Tema.error(this, "No se pudo abrir el programa de correo: " + e.getMessage());
        }
    }

    private static String codificar(String texto) {
        return URLEncoder.encode(texto, StandardCharsets.UTF_8).replace("+", "%20");
    }

    // ------------------------------------------------------------------ mis reportes

    private JPanel crearPanelReportes() {
        Tema.tabla(tablaReportes);
        tablaReportes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tablaReportes.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaReportes.getSelectedRow();
            if (e.getValueIsAdjusting() || fila < 0 || fila >= reportes.size()) {
                return;
            }
            Novedad n = reportes.get(fila);
            detalle.setText("Tu mensaje:\n" + n.getMensaje() + "\n\n"
                    + (n.getRespuesta() == null || n.getRespuesta().isEmpty() ? "El administrador aún no responde." : "Respuesta del administrador:\n" + n.getRespuesta()));
            detalle.setCaretPosition(0);
        });
        detalle.setEditable(false);
        detalle.setLineWrap(true);
        detalle.setWrapStyleWord(true);
        detalle.setFont(Tema.fuente(Font.PLAIN, 14f));
        detalle.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tablaReportes), new JScrollPane(detalle));
        split.setResizeWeight(0.55);
        split.setBorder(null);
        javax.swing.JButton actualizar = Tema.boton("Actualizar", false);
        actualizar.addActionListener(e -> cargarReportes());
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT));
        arriba.setOpaque(false);
        arriba.add(actualizar);
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Tema.FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        p.add(arriba, BorderLayout.NORTH);
        p.add(split, BorderLayout.CENTER);
        return p;
    }

    @SuppressWarnings("unchecked")
    private void cargarReportes() {
        modeloReportes.setRowCount(0);
        detalle.setText("");
        ResultadoOperacion r = novedades.listarDeConductor(usuario);
        if (!r.isExito()) {
            return;
        }
        reportes = (List<Novedad>) r.getDatos();
        for (Novedad n : reportes) {
            String fecha = n.getFecha() == null ? "" : String.format("%02d/%02d/%04d", n.getFecha().getDia(), n.getFecha().getMes(), n.getFecha().getAnio());
            modeloReportes.addRow(new Object[]{fecha, ControladorNovedades.TIPOS.getOrDefault(n.getTipo(), n.getTipo()), n.getAsunto(), n.getEstado()});
        }
    }
}
