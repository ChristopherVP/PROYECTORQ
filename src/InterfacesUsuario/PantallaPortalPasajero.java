package InterfacesUsuario;

import control.ControladorBilletera;
import control.ResultadoOperacion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import objetosNegocio.Usuario;

/** Portal del pasajero: comprar pasajes, administrar la billetera y ver sus reservas. */
public class PantallaPortalPasajero extends JFrame {
    private final Usuario usuario;
    private final ControladorBilletera billetera = new ControladorBilletera();
    private final JLabel lblSaldo = new JLabel();
    private final JLabel lblSaludo = new JLabel();
    private PanelComprarPasaje panelComprar;
    private PanelBilletera panelBilletera;
    private PanelMisPasajes panelPasajes;

    public PantallaPortalPasajero(Usuario usuario) {
        this.usuario = usuario;
        setTitle("RQ - Pasajero: " + usuario.getNombre());
        setIconImage(IconoRQ.crear(64));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(crearCabecera(), BorderLayout.NORTH);

        Runnable refrescar = this::refrescarTodo;
        panelComprar = new PanelComprarPasaje(usuario, refrescar);
        panelBilletera = new PanelBilletera(usuario, refrescar);
        panelPasajes = new PanelMisPasajes(usuario, refrescar);
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Tema.fuente(Font.BOLD, 14f));
        tabs.addTab("Comprar pasaje", panelComprar);
        tabs.addTab("Mi billetera", panelBilletera);
        tabs.addTab("Mis pasajes", panelPasajes);
        add(tabs, BorderLayout.CENTER);
        actualizarCabecera();
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
        lblSaludo.setForeground(Color.WHITE);
        lblSaludo.setFont(Tema.fuente(Font.BOLD, 18f));
        p.add(lblSaludo, BorderLayout.CENTER);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        derecha.setOpaque(false);
        lblSaldo.setForeground(Tema.AMBAR);
        lblSaldo.setFont(Tema.fuente(Font.BOLD, 16f));
        derecha.add(lblSaldo);
        javax.swing.JButton perfil = Tema.boton("Mi perfil", false);
        perfil.addActionListener(e -> new DialogoPerfil(this, usuario, this::actualizarCabecera).setVisible(true));
        javax.swing.JButton salir = Tema.boton("Cerrar sesión", false);
        salir.addActionListener(e -> Navegador.cerrarSesion(this));
        derecha.add(perfil);
        derecha.add(salir);
        p.add(derecha, BorderLayout.EAST);
        return p;
    }

    private void actualizarCabecera() {
        lblSaludo.setText("Hola, " + usuario.getNombre());
        ResultadoOperacion s = billetera.consultarSaldo(usuario.getIdPerfil());
        lblSaldo.setText("Saldo: " + (s.isExito() ? Tema.dinero((Double) s.getDatos()) : "—"));
    }

    private void refrescarTodo() {
        actualizarCabecera();
        panelComprar.recargar();
        panelBilletera.recargar();
        panelPasajes.recargar();
    }
}
