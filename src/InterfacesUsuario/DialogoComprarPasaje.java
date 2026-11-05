package InterfacesUsuario;

import control.ControladorBilletera;
import control.ControladorCompras;
import control.ControladorCompras.Oferta;
import control.ResultadoOperacion;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import objetosNegocio.Usuario;

/** Compra de un pasaje: elegir asientos, ver el total y pagar con el saldo de la billetera. */
public class DialogoComprarPasaje extends JDialog {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ControladorCompras compras = new ControladorCompras();
    private final ControladorBilletera billetera = new ControladorBilletera();
    private final Usuario usuario;
    private final Oferta oferta;
    private final Runnable alCambiar;
    private List<Integer> asientos = new ArrayList<>();

    private final JLabel lblAsientos = Tema.titulo("Ninguno", 15f);
    private final JLabel lblTotal = Tema.titulo("—", 22f);
    private final JLabel lblSaldo = Tema.etiqueta(" ");
    private final JLabel lblAviso = Tema.mensaje();
    private final JButton btnPagar = Tema.boton("Pagar con mi billetera", true);
    private final JButton btnRecargar = Tema.boton("Solicitar recarga", false);

    public DialogoComprarPasaje(Window padre, Usuario usuario, Oferta oferta, Runnable alCambiar) {
        super(padre, "Comprar pasaje", Dialog.ModalityType.APPLICATION_MODAL);
        this.usuario = usuario;
        this.oferta = oferta;
        this.alCambiar = alCambiar;
        setIconImage(IconoRQ.crear(64));
        setLayout(new BorderLayout());
        add(Tema.encabezado(oferta.getOrigen() + " → " + oferta.getDestino(), oferta.getEmpresa()), BorderLayout.NORTH);

        JPanel info = Tema.panelFormulario();
        String llegada = oferta.getViaje().getFechaLlegada() == null ? "—"
                : String.format("%02d/%02d/%04d %s", oferta.getViaje().getFechaLlegada().getDia(), oferta.getViaje().getFechaLlegada().getMes(),
                oferta.getViaje().getFechaLlegada().getAnio(), oferta.getViaje().getHoraLlegada() == null ? "" : oferta.getViaje().getHoraLlegada());
        Tema.fila(info, 0, "Salida", Tema.titulo(oferta.getSalida().format(FORMATO), 14f));
        Tema.fila(info, 1, "Llegada estimada", Tema.etiqueta(llegada));
        Tema.fila(info, 2, "Precio base por asiento", Tema.etiqueta(Tema.dinero(oferta.getPrecioUnitario())));
        Tema.fila(info, 3, "Asientos libres", Tema.etiqueta(String.valueOf(oferta.getViaje().getAsientosDisponibles())));
        JLabel nota = Tema.nota(440, "<html>Las tarifas preferenciales (niños, adolescentes, tercera edad y discapacidad) se aplican automáticamente en el total.</html>");
        nota.setForeground(Tema.TEXTO_SUAVE);

        JButton btnElegir = Tema.boton("Elegir asientos...", false);
        btnElegir.addActionListener(e -> elegirAsientos());
        JPanel seleccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        seleccion.setOpaque(false);
        seleccion.add(btnElegir);
        seleccion.add(lblAsientos);

        JPanel totales = Tema.panelFormulario();
        Tema.fila(totales, 0, "Total a pagar", lblTotal);
        Tema.fila(totales, 1, "Saldo de tu billetera", lblSaldo);

        btnPagar.addActionListener(e -> pagar());
        btnRecargar.addActionListener(e -> new DialogoRecarga(this, usuario, this::actualizar).setVisible(true));
        JButton cancelar = Tema.boton("Cancelar", false);
        cancelar.addActionListener(e -> dispose());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        botones.setOpaque(false);
        botones.add(cancelar);
        botones.add(btnRecargar);
        botones.add(btnPagar);

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setBackground(Tema.FONDO);
        centro.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));
        JPanel medio = new JPanel(new BorderLayout(0, 8));
        medio.setOpaque(false);
        JPanel superior = new JPanel(new BorderLayout(0, 4));
        superior.setOpaque(false);
        superior.add(info, BorderLayout.NORTH);
        superior.add(nota, BorderLayout.CENTER);
        medio.add(superior, BorderLayout.NORTH);
        medio.add(seleccion, BorderLayout.CENTER);
        medio.add(totales, BorderLayout.SOUTH);
        centro.add(medio, BorderLayout.CENTER);
        JPanel sur = new JPanel(new BorderLayout(0, 6));
        sur.setOpaque(false);
        sur.add(lblAviso, BorderLayout.NORTH);
        sur.add(botones, BorderLayout.CENTER);
        centro.add(sur, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);
        actualizar();
        setPreferredSize(new Dimension(620, 580));
        pack();
        setLocationRelativeTo(padre);
    }

    private void elegirAsientos() {
        DialogoMapaAsientos mapa = new DialogoMapaAsientos(this, oferta.getViaje().getIdViaje(), asientos);
        mapa.setVisible(true);
        if (!mapa.isConfirmado()) {
            return;
        }
        List<Integer> elegidos = mapa.getAsientosSeleccionados();
        if (elegidos.size() > ControladorCompras.MAX_ASIENTOS) {
            Tema.error(this, "Puedes comprar hasta " + ControladorCompras.MAX_ASIENTOS + " asientos por compra.");
            return;
        }
        asientos = new ArrayList<>(elegidos);
        actualizar();
    }

    private void actualizar() {
        ResultadoOperacion saldoR = billetera.consultarSaldo(usuario.getIdPerfil());
        double saldo = saldoR.isExito() ? (Double) saldoR.getDatos() : 0;
        lblSaldo.setText(Tema.dinero(saldo));
        lblAsientos.setText(asientos.isEmpty() ? "Ninguno" : asientos.toString().replace("[", "").replace("]", ""));
        btnPagar.setEnabled(false);
        btnRecargar.setVisible(false);
        if (asientos.isEmpty()) {
            lblTotal.setText("—");
            Tema.mostrarMensaje(lblAviso, "Elige al menos un asiento para continuar.", false);
            return;
        }
        ResultadoOperacion cot = compras.cotizar(usuario.getIdPerfil(), oferta.getViaje().getIdViaje(), asientos.size());
        if (!cot.isExito()) {
            lblTotal.setText("—");
            Tema.mostrarMensaje(lblAviso, cot.getMensaje(), false);
            return;
        }
        double total = (Double) cot.getDatos();
        lblTotal.setText(Tema.dinero(total));
        if (saldo + 1e-9 >= total) {
            btnPagar.setEnabled(true);
            Tema.mostrarMensaje(lblAviso, "Listo para pagar.", true);
        } else {
            btnRecargar.setVisible(true);
            Tema.mostrarMensaje(lblAviso, "Saldo insuficiente: te faltan " + Tema.dinero(total - saldo) + ". Solicita una recarga; cuando el administrador la apruebe podrás pagar.", false);
        }
    }

    private void pagar() {
        int opcion = JOptionPane.showConfirmDialog(this, "Se descontarán " + lblTotal.getText() + " de tu billetera.\n¿Confirmar la compra?", "Confirmar compra", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        ResultadoOperacion r = compras.comprar(usuario.getIdPerfil(), oferta.getViaje().getIdViaje(), asientos);
        if (!r.isExito()) {
            Tema.error(this, r.getMensaje());
            asientos = new ArrayList<>();
            actualizar();
            return;
        }
        Tema.info(this, r.getMensaje());
        alCambiar.run();
        dispose();
    }
}
