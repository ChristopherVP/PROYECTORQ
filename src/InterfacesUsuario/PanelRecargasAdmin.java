package InterfacesUsuario;

import control.ControladorBilletera;
import control.ResultadoOperacion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import objetosNegocio.Movimiento;
import objetosNegocio.Pasajero;
import objetosNegocio.Usuario;
import persistencia.Pasajeros;

/** Bandeja del administrador general para aprobar o rechazar las recargas que piden los pasajeros. */
public class PanelRecargasAdmin extends JPanel {
    private final ControladorBilletera billetera = new ControladorBilletera();
    private final Usuario actor;
    private final JComboBox<String> cmbFiltro = new JComboBox<>(new String[]{"Pendientes", "Todas"});
    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Fecha", "Pasajero", "Cédula", "Método", "Referencia", "Monto", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);
    private final JLabel lblResumen = Tema.etiqueta(" ");
    private List<Movimiento> filas = new ArrayList<>();

    public PanelRecargasAdmin(Usuario actor) {
        this.actor = actor;
        setLayout(new BorderLayout(0, 10));
        setBackground(Tema.FONDO);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        arriba.setOpaque(false);
        arriba.add(Tema.etiqueta("Mostrar"));
        arriba.add(cmbFiltro);
        javax.swing.JButton aprobar = Tema.boton("Aprobar", true);
        javax.swing.JButton rechazar = Tema.boton("Rechazar", false);
        javax.swing.JButton actualizar = Tema.boton("Actualizar", false);
        aprobar.addActionListener(e -> aprobar());
        rechazar.addActionListener(e -> rechazar());
        actualizar.addActionListener(e -> recargar());
        cmbFiltro.addActionListener(e -> recargar());
        arriba.add(aprobar);
        arriba.add(rechazar);
        arriba.add(actualizar);
        add(arriba, BorderLayout.NORTH);

        Tema.tabla(tabla);
        tabla.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel, boolean foco, int fila, int col) {
                Component c = super.getTableCellRendererComponent(tb, v, sel, foco, fila, col);
                String s = String.valueOf(v);
                c.setForeground("APROBADA".equals(s) ? Tema.EXITO : ("RECHAZADA".equals(s) ? Tema.ERROR : new Color(0xB9770E)));
                c.setFont(Tema.fuente(Font.BOLD, 13f));
                return c;
            }
        });
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);
        JPanel sur = new JPanel(new BorderLayout());
        sur.setOpaque(false);
        sur.add(lblResumen, BorderLayout.WEST);
        JLabel regla = Tema.etiqueta("Verifica el pago (tarjeta o comprobante de efectivo) antes de aprobar. Solo el administrador general puede revisar recargas.");
        regla.setForeground(Tema.TEXTO_SUAVE);
        sur.add(regla, BorderLayout.SOUTH);
        add(sur, BorderLayout.SOUTH);
        recargar();
    }

    @SuppressWarnings("unchecked")
    public final void recargar() {
        modelo.setRowCount(0);
        ResultadoOperacion r = "Pendientes".equals(cmbFiltro.getSelectedItem()) ? billetera.recargasPendientes() : billetera.todasLasRecargas();
        if (!r.isExito()) {
            Tema.error(this, r.getMensaje());
            return;
        }
        filas = (List<Movimiento>) r.getDatos();
        Map<String, Pasajero> pasajeros = new HashMap<>();
        try {
            for (Pasajero p : new Pasajeros("pasajeros.dat").lista()) {
                pasajeros.put(p.getIdPasajero(), p);
            }
        } catch (Exception e) {
            // se muestra el id si no se puede leer la ficha
        }
        double pendiente = 0;
        int cuantas = 0;
        for (Movimiento m : filas) {
            Pasajero p = pasajeros.get(m.getIdPasajero());
            String fecha = m.getFecha() == null ? "" : String.format("%02d/%02d/%04d %s", m.getFecha().getDia(), m.getFecha().getMes(), m.getFecha().getAnio(), m.getHora());
            modelo.addRow(new Object[]{m.getIdMovimiento(), fecha, p != null ? p.getNombre() : m.getIdPasajero(), p != null ? p.getCedula() : "",
                    m.getMetodo(), m.getReferencia(), Tema.dinero(m.getMonto()), m.getEstado()});
            if (m.esRecargaPendiente()) {
                pendiente += m.getMonto();
                cuantas++;
            }
        }
        lblResumen.setText(cuantas + " recarga(s) pendiente(s) por " + Tema.dinero(pendiente));
    }

    private Movimiento seleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Tema.error(this, "Selecciona una recarga de la lista.");
            return null;
        }
        return filas.get(fila);
    }

    private void aprobar() {
        Movimiento m = seleccionada();
        if (m == null) return;
        if (JOptionPane.showConfirmDialog(this, "¿Aprobar la recarga de " + Tema.dinero(m.getMonto()) + " (" + m.getMetodo() + " " + m.getReferencia() + ")?",
                "Confirmar aprobación", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        ResultadoOperacion r = billetera.aprobarRecarga(actor, m.getIdMovimiento());
        if (!r.isExito()) Tema.error(this, r.getMensaje());
        recargar();
    }

    private void rechazar() {
        Movimiento m = seleccionada();
        if (m == null) return;
        String motivo = JOptionPane.showInputDialog(this, "Motivo del rechazo (el pasajero lo verá):", "Rechazar recarga", JOptionPane.QUESTION_MESSAGE);
        if (motivo == null) return;
        ResultadoOperacion r = billetera.rechazarRecarga(actor, m.getIdMovimiento(), motivo);
        if (!r.isExito()) Tema.error(this, r.getMensaje());
        recargar();
    }
}
