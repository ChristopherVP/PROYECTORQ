package InterfacesUsuario;

import control.ControladorBilletera;
import control.ResultadoOperacion;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import objetosNegocio.Usuario;

/** Solicitud de recarga de la billetera por tarjeta o efectivo. Queda pendiente hasta que el administrador la apruebe. */
public class DialogoRecarga extends JDialog {
    private final ControladorBilletera billetera = new ControladorBilletera();
    private final Usuario usuario;
    private final Runnable alEnviar;
    private final JRadioButton rbTarjeta = new JRadioButton("Tarjeta", true);
    private final JRadioButton rbEfectivo = new JRadioButton("Efectivo");
    private final JTextField txtMonto = Tema.campo(14);
    private final JTextField txtTitular = Tema.campo(20);
    private final JTextField txtNumero = Tema.campo(20);
    private final JTextField txtVence = Tema.campo(8);
    private final JPasswordField txtCvv = Tema.clave(6);
    private final JTextField txtComprobante = Tema.campo(20);
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel panelMetodo = new JPanel(tarjetas);
    private final JLabel lblMensaje = Tema.mensaje();

    public DialogoRecarga(Window padre, Usuario usuario, Runnable alEnviar) {
        super(padre, "Solicitar recarga", Dialog.ModalityType.APPLICATION_MODAL);
        this.usuario = usuario;
        this.alEnviar = alEnviar;
        setIconImage(IconoRQ.crear(64));
        setLayout(new BorderLayout());
        add(Tema.encabezado("Recargar billetera", "Tu recarga será revisada por el administrador"), BorderLayout.NORTH);

        ButtonGroup grupo = new ButtonGroup();
        JPanel metodos = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        metodos.setOpaque(false);
        for (JRadioButton rb : new JRadioButton[]{rbTarjeta, rbEfectivo}) {
            rb.setOpaque(false);
            rb.setFont(Tema.fuente(Font.BOLD, 14f));
            grupo.add(rb);
            metodos.add(rb);
            rb.addActionListener(e -> tarjetas.show(panelMetodo, rbTarjeta.isSelected() ? "tarjeta" : "efectivo"));
        }

        JPanel montoPanel = Tema.panelFormulario();
        Tema.fila(montoPanel, 0, String.format("Monto (%s a %s)", Tema.dinero(ControladorBilletera.MONTO_MINIMO), Tema.dinero(ControladorBilletera.MONTO_MAXIMO)), txtMonto);

        JPanel t = Tema.panelFormulario();
        Tema.fila(t, 0, "Titular", txtTitular);
        Tema.fila(t, 1, "Número de tarjeta", txtNumero);
        Tema.fila(t, 2, "Vence (MM/AA)", txtVence);
        Tema.fila(t, 3, "CVV", txtCvv);
        JLabel seguridad = Tema.nota(440, "<html>Por seguridad solo guardamos la marca y los últimos 4 dígitos. El número completo y el CVV <b>no</b> se almacenan.</html>");
        seguridad.setForeground(Tema.TEXTO_SUAVE);
        JPanel pt = new JPanel(new BorderLayout(0, 6));
        pt.setOpaque(false);
        pt.add(t, BorderLayout.NORTH);
        pt.add(seguridad, BorderLayout.CENTER);

        JPanel e = Tema.panelFormulario();
        Tema.fila(e, 0, "N.º de comprobante", txtComprobante);
        JLabel ayuda = Tema.nota(440, "<html>Paga en ventanilla o con un agente autorizado y escribe el número del comprobante (6 a 12 dígitos). "
                + "El administrador verificará el pago antes de acreditar el saldo.</html>");
        ayuda.setForeground(Tema.TEXTO_SUAVE);
        JPanel pe = new JPanel(new BorderLayout(0, 6));
        pe.setOpaque(false);
        pe.add(e, BorderLayout.NORTH);
        pe.add(ayuda, BorderLayout.CENTER);

        panelMetodo.setOpaque(false);
        panelMetodo.add(pt, "tarjeta");
        panelMetodo.add(pe, "efectivo");

        javax.swing.JButton enviar = Tema.boton("Enviar solicitud", true);
        enviar.addActionListener(ev -> enviar());
        javax.swing.JButton cancelar = Tema.boton("Cancelar", false);
        cancelar.addActionListener(ev -> dispose());
        getRootPane().setDefaultButton(enviar);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        botones.setOpaque(false);
        botones.add(cancelar);
        botones.add(enviar);

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setBackground(Tema.FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));
        JPanel arriba = new JPanel(new BorderLayout(0, 6));
        arriba.setOpaque(false);
        arriba.add(metodos, BorderLayout.NORTH);
        arriba.add(montoPanel, BorderLayout.CENTER);
        centro.add(arriba, BorderLayout.NORTH);
        centro.add(panelMetodo, BorderLayout.CENTER);
        JPanel sur = new JPanel(new BorderLayout(0, 6));
        sur.setOpaque(false);
        sur.add(lblMensaje, BorderLayout.NORTH);
        sur.add(botones, BorderLayout.CENTER);
        centro.add(sur, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);
        setPreferredSize(new Dimension(600, 540));
        pack();
        setLocationRelativeTo(padre);
    }

    private void enviar() {
        double monto;
        try {
            monto = Double.parseDouble(txtMonto.getText().trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            Tema.mostrarMensaje(lblMensaje, "Escribe un monto válido, por ejemplo 25.50", false);
            return;
        }
        ResultadoOperacion r = rbTarjeta.isSelected()
                ? billetera.solicitarRecargaTarjeta(usuario.getIdPerfil(), monto, txtTitular.getText(), txtNumero.getText(), txtVence.getText(), new String(txtCvv.getPassword()))
                : billetera.solicitarRecargaEfectivo(usuario.getIdPerfil(), monto, txtComprobante.getText());
        if (!r.isExito()) {
            Tema.mostrarMensaje(lblMensaje, r.getMensaje(), false);
            return;
        }
        txtNumero.setText("");
        txtCvv.setText("");
        Tema.info(this, r.getMensaje());
        alEnviar.run();
        dispose();
    }
}
