//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import control.ControladorReservas;
import control.ResultadoOperacion;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import objetosNegocio.Asiento;

public class DialogoMapaAsientos extends JDialog {
    private String idViaje;
    private ControladorReservas controlador;
    private List<Integer> asientosSeleccionados;
    private List<Integer> asientosPropios;
    private JPanel panelAsientos;
    private JLabel lblInfo;
    private boolean confirmado = false;

    public DialogoMapaAsientos(Dialog padre, String idViaje, List<Integer> asientosPreviamenteSeleccionados) {
        super(padre, "Selección de Asientos - Viaje " + idViaje, true);
        this.idViaje = idViaje;
        this.controlador = new ControladorReservas();
        this.asientosPropios = asientosPreviamenteSeleccionados != null ? new ArrayList(asientosPreviamenteSeleccionados) : new ArrayList();
        this.asientosSeleccionados = new ArrayList(this.asientosPropios);
        this.initComponents();
        this.cargarAsientos();
        this.actualizarEtiquetaInfo();
    }

    private void initComponents() {
        this.setSize(550, 650);
        this.setLocationRelativeTo(this.getParent());
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(new Color(245, 245, 245));
        JPanel panelLeyenda = new JPanel(new FlowLayout(1, 15, 10));
        panelLeyenda.setBackground(Color.WHITE);
        panelLeyenda.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        panelLeyenda.add(this.crearItemLeyenda(new Color(46, 204, 113), "Disponible"));
        panelLeyenda.add(this.crearItemLeyenda(new Color(52, 152, 219), "Seleccionado"));
        panelLeyenda.add(this.crearItemLeyenda(new Color(231, 76, 60), "No Disponible"));
        this.add(panelLeyenda, "North");
        JPanel panelContenedorBus = new JPanel(new BorderLayout());
        panelContenedorBus.setBackground(new Color(245, 245, 245));
        panelContenedorBus.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel panelCabina = new JPanel();
        panelCabina.setBackground(new Color(220, 220, 220));
        panelCabina.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
        panelCabina.setPreferredSize(new Dimension(300, 40));
        JLabel lblConductor = new JLabel("⎈ CONDUCTOR / FRENTE", 0);
        lblConductor.setFont(new Font("Arial", 1, 12));
        lblConductor.setForeground(Color.DARK_GRAY);
        panelCabina.add(lblConductor);
        this.panelAsientos = new JPanel();
        this.panelAsientos.setOpaque(false);
        JPanel panelCentro = new JPanel(new BorderLayout(0, 10));
        panelCentro.setOpaque(false);
        panelCentro.add(panelCabina, "North");
        panelCentro.add(this.panelAsientos, "Center");
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 245));
        wrapper.add(panelCentro);
        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder((Border)null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        this.add(scroll, "Center");
        JPanel panelBotones = new JPanel(new FlowLayout(2));
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        this.lblInfo = new JLabel("<html>Seleccionados: <b>0</b><br/>Asientos: -</html>");
        this.lblInfo.setFont(new Font("Arial", 1, 14));
        this.lblInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        JButton btnConfirmar = new JButton("Confirmar Selección");
        btnConfirmar.setBackground(new Color(41, 128, 185));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setFont(new Font("Arial", 1, 12));
        btnConfirmar.addActionListener((e) -> {
            this.confirmado = true;
            this.dispose();
        });
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener((e) -> this.dispose());
        panelBotones.add(this.lblInfo);
        panelBotones.add(btnConfirmar);
        panelBotones.add(btnCancelar);
        this.add(panelBotones, "South");
    }

    private JPanel crearItemLeyenda(Color color, String texto) {
        JPanel p = new JPanel(new FlowLayout(0, 5, 0));
        JPanel box = new JPanel();
        box.setBackground(color);
        box.setPreferredSize(new Dimension(15, 15));
        box.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        p.setOpaque(false);
        p.add(box);
        p.add(new JLabel(texto));
        return p;
    }

    private void cargarAsientos() {
        ResultadoOperacion res = this.controlador.obtenerMapaAsientos(this.idViaje);
        if (!res.isExito()) {
            JOptionPane.showMessageDialog(this, "Error al cargar asientos: " + res.getMensaje());
        } else {
            ControladorReservas.MapaAsientos mapa = (ControladorReservas.MapaAsientos)res.getDatos();
            List<Asiento> listaAsientos = mapa.getAsientos();
            if (listaAsientos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay asientos registrados para este viaje.");
            } else {
                Map<Integer, Asiento> mapaAsientos = new HashMap();
                int maxNumeroAsiento = 0;

                for(Asiento a : listaAsientos) {
                    mapaAsientos.put(a.getNumeroAsiento(), a);
                    if (a.getNumeroAsiento() > maxNumeroAsiento) {
                        maxNumeroAsiento = a.getNumeroAsiento();
                    }
                }

                this.panelAsientos.removeAll();
                this.panelAsientos.setLayout(new GridLayout(0, 5, 10, 10));
                this.panelAsientos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                if (maxNumeroAsiento == 0) {
                    maxNumeroAsiento = 45;
                }

                int filas = (int)Math.ceil((double)maxNumeroAsiento / (double)4.0F);
                int numeroAsientoActual = 1;

                for(int i = 0; i < filas * 5; ++i) {
                    int col = i % 5;
                    if (col == 2) {
                        this.panelAsientos.add(new JLabel(""));
                    } else {
                        Asiento asiento = (Asiento)mapaAsientos.get(numeroAsientoActual);
                        if (asiento != null) {
                            JToggleButton btn = this.crearBotonAsiento(asiento);
                            this.panelAsientos.add(btn);
                        } else {
                            JLabel lblVacio = new JLabel(String.valueOf(numeroAsientoActual), 0);
                            lblVacio.setForeground(Color.LIGHT_GRAY);
                            lblVacio.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                            this.panelAsientos.add(lblVacio);
                        }

                        ++numeroAsientoActual;
                    }
                }

                this.panelAsientos.revalidate();
                this.panelAsientos.repaint();
            }
        }
    }

    private JToggleButton crearBotonAsiento(Asiento asiento) {
        JToggleButton btn = new BotonAsiento(String.valueOf(asiento.getNumeroAsiento()));
        String estado = asiento.getEstado() != null ? asiento.getEstado().trim() : "";
        boolean esPropio = this.asientosPropios.contains(asiento.getNumeroAsiento());
        if (esPropio) {
            btn.setBackground(new Color(52, 152, 219));
            btn.setForeground(Color.WHITE);
            btn.setSelected(true);
            btn.addActionListener((e) -> this.toggleSeleccion(asiento.getNumeroAsiento(), btn));
        } else if ("Disponible".equalsIgnoreCase(estado)) {
            btn.setBackground(new Color(46, 204, 113));
            btn.addActionListener((e) -> this.toggleSeleccion(asiento.getNumeroAsiento(), btn));
        } else if (!"Reservado".equalsIgnoreCase(estado) && !"Ocupado".equalsIgnoreCase(estado) && !"Confirmada".equalsIgnoreCase(estado) && !"Usada".equalsIgnoreCase(estado)) {
            btn.setBackground(Color.GRAY);
            btn.setEnabled(false);
        } else {
            btn.setBackground(new Color(231, 76, 60));
            btn.setEnabled(false);
            btn.setToolTipText("No Disponible");
        }

        return btn;
    }

    private void toggleSeleccion(int numeroAsiento, JToggleButton btn) {
        if (btn.isSelected()) {
            this.asientosSeleccionados.add(numeroAsiento);
            btn.setBackground(new Color(52, 152, 219));
            btn.setForeground(Color.WHITE);
        } else {
            this.asientosSeleccionados.remove(numeroAsiento);
            btn.setBackground(new Color(46, 204, 113));
            btn.setForeground(Color.BLACK);
        }

        this.actualizarEtiquetaInfo();
    }

    private void actualizarEtiquetaInfo() {
        this.asientosSeleccionados.sort(Integer::compareTo);
        String lista = this.asientosSeleccionados.toString().replace("[", "").replace("]", "");
        if (lista.isEmpty()) {
            lista = "-";
        }

        JLabel var10000 = this.lblInfo;
        int var10001 = this.asientosSeleccionados.size();
        var10000.setText("<html>Seleccionados: <b>" + var10001 + "</b><br/>Asientos: " + lista + "</html>");
    }

    public List<Integer> getAsientosSeleccionados() {
        return (List<Integer>)(this.confirmado ? this.asientosSeleccionados : new ArrayList());
    }

    public boolean isConfirmado() {
        return this.confirmado;
    }

    private static class BotonAsiento extends JToggleButton {
        public BotonAsiento(String text) {
            super(text);
            this.setPreferredSize(new Dimension(50, 50));
            this.setFocusPainted(false);
            this.setContentAreaFilled(false);
            this.setBorderPainted(false);
            this.setFont(new Font("Arial", 1, 14));
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (!this.getModel().isPressed() && !this.isSelected()) {
                if (!this.isEnabled()) {
                    g2.setColor(this.getBackground());
                } else {
                    g2.setColor(this.getBackground());
                }
            } else {
                g2.setColor(this.getBackground().darker());
            }

            g2.fillRoundRect(2, 2, this.getWidth() - 4, this.getHeight() - 4, 15, 15);
            g2.setColor(Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(1.5F));
            g2.drawRoundRect(2, 2, this.getWidth() - 4, this.getHeight() - 4, 15, 15);
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}
