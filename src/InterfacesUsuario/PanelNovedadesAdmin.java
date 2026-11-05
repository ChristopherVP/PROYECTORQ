package InterfacesUsuario;

import control.ControladorNovedades;
import control.ResultadoOperacion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import objetosNegocio.Novedad;
import objetosNegocio.Usuario;

/** Bandeja de reportes que los conductores envían al administrador. */
public class PanelNovedadesAdmin extends JPanel {
    private final ControladorNovedades controlador = new ControladorNovedades();
    private final Usuario actor;
    private final JComboBox<String> cmbFiltro = new JComboBox<>(new String[]{"Sin resolver", "Todos", "Resueltos"});
    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Fecha", "Conductor", "Viaje", "Tipo", "Asunto", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);
    private final JTextArea detalle = new JTextArea();
    private List<Novedad> filas = new ArrayList<>();

    public PanelNovedadesAdmin(Usuario actor) {
        this.actor = actor;
        setLayout(new BorderLayout(0, 10));
        setBackground(Tema.FONDO);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        arriba.setOpaque(false);
        arriba.add(Tema.etiqueta("Mostrar"));
        arriba.add(cmbFiltro);
        javax.swing.JButton responder = Tema.boton("Responder", true);
        javax.swing.JButton actualizar = Tema.boton("Actualizar", false);
        responder.addActionListener(e -> responder());
        actualizar.addActionListener(e -> recargar());
        cmbFiltro.addActionListener(e -> recargar());
        arriba.add(responder);
        arriba.add(actualizar);
        add(arriba, BorderLayout.NORTH);

        Tema.tabla(tabla);
        tabla.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> mostrarDetalle());
        detalle.setEditable(false);
        detalle.setLineWrap(true);
        detalle.setWrapStyleWord(true);
        detalle.setFont(Tema.fuente(Font.PLAIN, 14f));
        detalle.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JScrollPane sp = new JScrollPane(tabla);
        sp.getViewport().setBackground(Color.WHITE);
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sp, new JScrollPane(detalle));
        split.setResizeWeight(0.6);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);
        recargar();
    }

    @SuppressWarnings("unchecked")
    public final void recargar() {
        modelo.setRowCount(0);
        detalle.setText("");
        ResultadoOperacion r = controlador.listarParaAdministrador(actor);
        if (!r.isExito()) {
            Tema.error(this, r.getMensaje());
            return;
        }
        Map<String, String> nombres = controlador.nombresDeConductores();
        String filtro = (String) cmbFiltro.getSelectedItem();
        filas = new ArrayList<>();
        for (Novedad n : (List<Novedad>) r.getDatos()) {
            boolean resuelta = Novedad.RESUELTA.equals(n.getEstado());
            if ("Sin resolver".equals(filtro) && resuelta || "Resueltos".equals(filtro) && !resuelta) {
                continue;
            }
            filas.add(n);
            String fecha = n.getFecha() == null ? "" : String.format("%02d/%02d/%04d %s", n.getFecha().getDia(), n.getFecha().getMes(), n.getFecha().getAnio(), n.getHora());
            modelo.addRow(new Object[]{n.getIdNovedad(), fecha, nombres.getOrDefault(n.getIdConductor(), n.getIdConductor()), n.getIdViaje(),
                    ControladorNovedades.TIPOS.getOrDefault(n.getTipo(), n.getTipo()), n.getAsunto(), n.getEstado()});
        }
    }

    private void mostrarDetalle() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= filas.size()) {
            return;
        }
        Novedad n = filas.get(fila);
        detalle.setText(n.getAsunto() + "\n\n" + n.getMensaje() + "\n\n"
                + (n.getRespuesta() == null || n.getRespuesta().isEmpty() ? "(Sin respuesta todavía)" : "Respuesta enviada:\n" + n.getRespuesta()));
        detalle.setCaretPosition(0);
    }

    private void responder() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Tema.error(this, "Selecciona un reporte de la lista.");
            return;
        }
        Novedad n = filas.get(fila);
        String respuesta = JOptionPane.showInputDialog(this, "Respuesta para el conductor (5 a 200 caracteres):", "Responder reporte", JOptionPane.QUESTION_MESSAGE);
        if (respuesta == null) {
            return;
        }
        int resuelto = JOptionPane.showConfirmDialog(this, "¿Marcar el reporte como RESUELTO?\n(No = queda EN REVISIÓN)", "Estado del reporte", JOptionPane.YES_NO_CANCEL_OPTION);
        if (resuelto == JOptionPane.CANCEL_OPTION || resuelto == JOptionPane.CLOSED_OPTION) {
            return;
        }
        ResultadoOperacion r = controlador.responder(actor, n.getIdNovedad(), respuesta, resuelto == JOptionPane.YES_OPTION ? Novedad.RESUELTA : Novedad.EN_REVISION);
        if (!r.isExito()) {
            Tema.error(this, r.getMensaje());
        }
        recargar();
    }
}
