package InterfacesUsuario;

import control.ControladorBilletera;
import control.ResultadoOperacion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import objetosNegocio.Movimiento;
import objetosNegocio.Usuario;

/** Saldo, solicitud de recargas e historial de movimientos del pasajero. */
public class PanelBilletera extends JPanel {
    private final ControladorBilletera billetera = new ControladorBilletera();
    private final Usuario usuario;
    private final Runnable alCambiar;
    private final JLabel lblSaldo = new JLabel("$0.00");
    private final JLabel lblPendientes = Tema.etiqueta(" ");
    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"Fecha", "Movimiento", "Método", "Monto", "Estado", "Detalle"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    public PanelBilletera(Usuario usuario, Runnable alCambiar) {
        this.usuario = usuario;
        this.alCambiar = alCambiar;
        setLayout(new BorderLayout(0, 12));
        setBackground(Tema.FONDO);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel tarjeta = new JPanel(new BorderLayout(20, 0)) {
            @Override
            protected void paintComponent(java.awt.Graphics g0) {
                java.awt.Graphics2D g = (java.awt.Graphics2D) g0.create();
                g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g.setPaint(new java.awt.GradientPaint(0, 0, Tema.AZUL_OSCURO, getWidth(), 0, Tema.CELESTE));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g.dispose();
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));
        JPanel textos = new JPanel(new BorderLayout());
        textos.setOpaque(false);
        JLabel t = new JLabel("Saldo disponible");
        t.setForeground(new Color(0xCFE3F3));
        t.setFont(Tema.fuente(Font.PLAIN, 13f));
        lblSaldo.setForeground(Color.WHITE);
        lblSaldo.setFont(Tema.fuente(Font.BOLD, 36f));
        lblPendientes.setForeground(new Color(0xFFE2A8));
        textos.add(t, BorderLayout.NORTH);
        textos.add(lblSaldo, BorderLayout.CENTER);
        textos.add(lblPendientes, BorderLayout.SOUTH);
        tarjeta.add(textos, BorderLayout.CENTER);
        javax.swing.JButton recargar = Tema.boton("Solicitar recarga", false);
        recargar.addActionListener(e -> new DialogoRecarga(javax.swing.SwingUtilities.getWindowAncestor(this), usuario, () -> {
            recargar();
            alCambiar.run();
        }).setVisible(true));
        JPanel eje = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 18));
        eje.setOpaque(false);
        eje.add(recargar);
        tarjeta.add(eje, BorderLayout.EAST);
        add(tarjeta, BorderLayout.NORTH);

        Tema.tabla(tabla);
        tabla.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
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
        scroll.setBorder(BorderFactory.createTitledBorder("Historial de movimientos"));
        add(scroll, BorderLayout.CENTER);
        JLabel nota = Tema.etiqueta("<html>Las recargas quedan <b>PENDIENTES</b> hasta que el administrador las apruebe. El saldo solo cambia con recargas aprobadas, compras y reembolsos.</html>");
        nota.setForeground(Tema.TEXTO_SUAVE);
        add(nota, BorderLayout.SOUTH);
        recargar();
    }

    public final void recargar() {
        ResultadoOperacion s = billetera.consultarSaldo(usuario.getIdPerfil());
        lblSaldo.setText(s.isExito() ? Tema.dinero((Double) s.getDatos()) : "—");
        modelo.setRowCount(0);
        ResultadoOperacion h = billetera.historial(usuario.getIdPerfil());
        long pendientes = 0;
        double enEspera = 0;
        if (h.isExito()) {
            @SuppressWarnings("unchecked")
            List<Movimiento> lista = (List<Movimiento>) h.getDatos();
            for (Movimiento m : lista) {
                String fecha = m.getFecha() == null ? "" : String.format("%02d/%02d/%04d %s", m.getFecha().getDia(), m.getFecha().getMes(), m.getFecha().getAnio(), m.getHora() == null ? "" : m.getHora().substring(0, Math.min(5, m.getHora().length())));
                boolean egreso = Movimiento.COMPRA.equals(m.getTipo());
                String detalle = Movimiento.RECHAZADA.equals(m.getEstado()) ? "Motivo: " + m.getMotivo() : (Movimiento.COMPRA.equals(m.getTipo()) ? "Reserva " + m.getReferencia() : m.getReferencia());
                modelo.addRow(new Object[]{fecha, etiquetaTipo(m.getTipo()), m.getMetodo(), (egreso ? "-" : "+") + Tema.dinero(m.getMonto()), m.getEstado(), detalle});
                if (m.esRecargaPendiente()) {
                    pendientes++;
                    enEspera += m.getMonto();
                }
            }
        }
        lblPendientes.setText(pendientes > 0 ? pendientes + " recarga(s) en revisión por " + Tema.dinero(enEspera) : " ");
    }

    private static String etiquetaTipo(String tipo) {
        if (Movimiento.RECARGA.equals(tipo)) return "Recarga";
        if (Movimiento.COMPRA.equals(tipo)) return "Compra de pasaje";
        return "Reembolso";
    }
}
