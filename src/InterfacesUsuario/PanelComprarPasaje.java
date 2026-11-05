package InterfacesUsuario;

import control.ControladorCompras;
import control.ControladorCompras.Oferta;
import control.ResultadoOperacion;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import objetosNegocio.Usuario;

/** Lista de viajes disponibles con filtros de origen y destino. */
public class PanelComprarPasaje extends JPanel {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String TODOS = "Todos";

    private final ControladorCompras compras = new ControladorCompras();
    private final Usuario usuario;
    private final Runnable alCambiar;
    private final JComboBox<String> cmbOrigen = new JComboBox<>();
    private final JComboBox<String> cmbDestino = new JComboBox<>();
    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"Salida", "Origen", "Destino", "Empresa", "Precio base", "Libres"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);
    private final JLabel lblEstado = Tema.mensaje();
    private List<Oferta> todas = new ArrayList<>();
    private List<Oferta> visibles = new ArrayList<>();
    private boolean cargando;

    public PanelComprarPasaje(Usuario usuario, Runnable alCambiar) {
        this.usuario = usuario;
        this.alCambiar = alCambiar;
        setLayout(new BorderLayout(0, 10));
        setBackground(Tema.FONDO);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filtros.setOpaque(false);
        filtros.add(Tema.etiqueta("Origen"));
        filtros.add(cmbOrigen);
        filtros.add(Tema.etiqueta("Destino"));
        filtros.add(cmbDestino);
        javax.swing.JButton actualizar = Tema.boton("Actualizar", false);
        actualizar.addActionListener(e -> recargar());
        filtros.add(actualizar);
        add(filtros, BorderLayout.NORTH);

        Tema.tabla(tabla);
        tabla.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    comprar();
                }
            }
        });
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(java.awt.Color.WHITE);
        add(scroll, BorderLayout.CENTER);

        javax.swing.JButton comprar = Tema.boton("Comprar pasaje", true);
        comprar.addActionListener(e -> comprar());
        JPanel sur = new JPanel(new BorderLayout());
        sur.setOpaque(false);
        sur.add(lblEstado, BorderLayout.CENTER);
        sur.add(comprar, BorderLayout.EAST);
        add(sur, BorderLayout.SOUTH);

        cmbOrigen.addActionListener(e -> filtrar());
        cmbDestino.addActionListener(e -> filtrar());
        recargar();
    }

    public final void recargar() {
        ResultadoOperacion r = compras.listarOfertas();
        if (!r.isExito()) {
            Tema.mostrarMensaje(lblEstado, r.getMensaje(), false);
            return;
        }
        @SuppressWarnings("unchecked")
        List<Oferta> lista = (List<Oferta>) r.getDatos();
        todas = lista;
        cargando = true;
        Object origenActual = cmbOrigen.getSelectedItem();
        Object destinoActual = cmbDestino.getSelectedItem();
        TreeSet<String> origenes = new TreeSet<>();
        TreeSet<String> destinos = new TreeSet<>();
        for (Oferta o : todas) {
            origenes.add(o.getOrigen());
            destinos.add(o.getDestino());
        }
        cmbOrigen.removeAllItems();
        cmbDestino.removeAllItems();
        cmbOrigen.addItem(TODOS);
        cmbDestino.addItem(TODOS);
        origenes.forEach(cmbOrigen::addItem);
        destinos.forEach(cmbDestino::addItem);
        if (origenActual != null) cmbOrigen.setSelectedItem(origenActual);
        if (destinoActual != null) cmbDestino.setSelectedItem(destinoActual);
        cargando = false;
        filtrar();
    }

    private void filtrar() {
        if (cargando) {
            return;
        }
        String origen = (String) cmbOrigen.getSelectedItem();
        String destino = (String) cmbDestino.getSelectedItem();
        visibles = new ArrayList<>();
        modelo.setRowCount(0);
        for (Oferta o : todas) {
            if ((origen == null || TODOS.equals(origen) || origen.equals(o.getOrigen())) && (destino == null || TODOS.equals(destino) || destino.equals(o.getDestino()))) {
                visibles.add(o);
                modelo.addRow(new Object[]{o.getSalida().format(FORMATO), o.getOrigen(), o.getDestino(), o.getEmpresa(), Tema.dinero(o.getPrecioUnitario()), o.getViaje().getAsientosDisponibles()});
            }
        }
        Tema.mostrarMensaje(lblEstado, visibles.isEmpty() ? "No hay viajes disponibles con esos filtros." : visibles.size() + " viaje(s) disponible(s). Haz doble clic o pulsa \"Comprar pasaje\".", !visibles.isEmpty());
    }

    private void comprar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Tema.error(this, "Selecciona un viaje de la lista.");
            return;
        }
        new DialogoComprarPasaje(javax.swing.SwingUtilities.getWindowAncestor(this), usuario, visibles.get(fila), () -> {
            recargar();
            alCambiar.run();
        }).setVisible(true);
    }
}
