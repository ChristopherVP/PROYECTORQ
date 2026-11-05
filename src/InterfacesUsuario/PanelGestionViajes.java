//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import control.ControladorViajes;
import control.ResultadoOperacion;
import excepciones.PersistenciaException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import objetosNegocio.Bus;
import objetosNegocio.Conductor;
import objetosNegocio.Empresa;
import objetosNegocio.Ruta;
import objetosNegocio.Usuario;
import objetosNegocio.Viaje;
import persistencia.Buses;
import persistencia.Conductores;
import persistencia.Empresas;
import persistencia.Rutas;
import persistencia.Viajes;

public class PanelGestionViajes extends JPanel {
    private JTable tabla;
    private DefaultTableModel modelo;
    private JButton btnTodos;
    private JButton btnAgregar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JCheckBox chkSoloDisponibles;
    private JComboBox<String> cmbAnio;
    private JComboBox<String> cmbMes;
    private JComboBox<String> cmbEmpresa;
    private Viajes persistencia;
    private ControladorViajes controlador;
    private Empresa empresaFiltro;
    private Usuario usuarioActual;
    private List<Viaje> listaViajesCompleta;
    private Map<String, String> mapaEmpresas = new HashMap<>();
    private Map<String, String> mapaBuses = new HashMap<>();
    private Map<String, String> mapaOrigenes = new HashMap<>();
    private Map<String, String> mapaDestinos = new HashMap<>();
    private Map<String, String> mapaConductores = new HashMap<>();

    public PanelGestionViajes(Usuario usuario) {
        this.usuarioActual = usuario;
        this.persistencia = new Viajes("viajes.dat");
        this.controlador = new ControladorViajes();
        this.initComponents();
        this.cargarDatos();
    }

    public void setEmpresaFiltro(Empresa empresa) {
        this.empresaFiltro = empresa;
        if (this.cmbEmpresa != null) {
            this.cmbEmpresa.setVisible(empresa == null);
            this.cmbEmpresa.setSelectedIndex(0);
        }
        this.cargarDatos();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 0));
        JPanel panelBotones = new JPanel(new FlowLayout(0, 5, 0));
        JPanel panelBusqueda = new JPanel(new FlowLayout(2, 5, 0));
        JButton btnTodos = new JButton("Todos");
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnDetalles = new JButton("Detalles");
        btnTodos.addActionListener((e) -> this.cargarDatos());
        btnAgregar.addActionListener((e) -> this.agregar());
        btnEditar.addActionListener((e) -> this.editar());
        btnEliminar.addActionListener((e) -> this.eliminar());
        btnDetalles.addActionListener((e) -> this.verDetalles());
        panelBotones.add(btnTodos);
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnDetalles);
        this.chkSoloDisponibles = new JCheckBox("Solo Disponibles");
        this.chkSoloDisponibles.setSelected(true);
        this.chkSoloDisponibles.addActionListener((e) -> this.filtrar());

        this.cmbEmpresa = new JComboBox<>();
        this.cmbEmpresa.addItem("Todas las Empresas");
        this.cmbEmpresa.addActionListener((e) -> this.filtrar());

        this.cmbAnio = new JComboBox<>();
        this.cmbAnio.addItem("Año: Todos");
        this.cmbAnio.addActionListener((e) -> this.filtrar());

        this.cmbMes = new JComboBox<>(new String[]{"Mes: Todos", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"});
        this.cmbMes.addActionListener((e) -> this.filtrar());

        this.txtBuscar = new JTextField(20);
        this.txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar (Origen, Destino, Fecha)...");
        this.txtBuscar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                PanelGestionViajes.this.filtrar();
            }
        });

        this.btnBuscar = new JButton("Buscar");
        this.btnBuscar.addActionListener((e) -> this.filtrar());

        panelBusqueda.add(this.cmbEmpresa);
        panelBusqueda.add(this.cmbAnio);
        panelBusqueda.add(this.cmbMes);
        panelBusqueda.add(this.chkSoloDisponibles);
        panelBusqueda.add(new JLabel("\ud83d\udd0d"));
        panelBusqueda.add(this.txtBuscar);
        panelBusqueda.add(this.btnBuscar);
        panelSuperior.add(panelBotones, "West");
        panelSuperior.add(panelBusqueda, "East");
        String[] cols = new String[]{"ID", "Empresa", "Año", "Mes", "Bus", "Ciudad Origen", "Ciudad Destino", "Fecha Salida", "Conductor", "Asientos", "Estado"};
        this.modelo = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.tabla = new JTable(this.modelo);
        this.tabla.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(this.tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Viajes Registrados"));
        this.add(panelSuperior, "North");
        this.add(scrollPane, "Center");
    }

    private void cargarDatos() {
        try {
            this.listaViajesCompleta = this.persistencia.lista();

            // Cargar datos auxiliares una sola vez para optimizar la búsqueda
            this.mapaEmpresas.clear();
            this.mapaBuses.clear();
            this.mapaOrigenes.clear();
            this.mapaDestinos.clear();
            this.mapaConductores.clear();

            try {
                // Desactivar listener temporalmente para evitar filtrados innecesarios durante la carga
                java.awt.event.ActionListener[] listeners = this.cmbEmpresa.getActionListeners();
                for (java.awt.event.ActionListener l : listeners) {
                    this.cmbEmpresa.removeActionListener(l);
                }

                for(Empresa e : (new Empresas("empresas.dat")).lista()) {
                    mapaEmpresas.put(e.getIdEmpresa().trim(), e.getNombre());
                }

                Object selectedEmpresa = this.cmbEmpresa.getSelectedItem();
                this.cmbEmpresa.removeAllItems();
                this.cmbEmpresa.addItem("Todas las Empresas");

                List<String> nombresEmpresas = new java.util.ArrayList<>(mapaEmpresas.values());
                Collections.sort(nombresEmpresas);

                for(String nombre : nombresEmpresas) {
                    this.cmbEmpresa.addItem(nombre);
                }

                if (selectedEmpresa != null) {
                    this.cmbEmpresa.setSelectedItem(selectedEmpresa);
                }

                // Restaurar listeners
                for (java.awt.event.ActionListener l : listeners) {
                    this.cmbEmpresa.addActionListener(l);
                }

                // Actualizar combo de Años dinámicamente
                java.awt.event.ActionListener[] anioListeners = this.cmbAnio.getActionListeners();
                for (java.awt.event.ActionListener l : anioListeners) {
                    this.cmbAnio.removeActionListener(l);
                }

                Object selectedAnio = this.cmbAnio.getSelectedItem();
                this.cmbAnio.removeAllItems();
                this.cmbAnio.addItem("Año: Todos");
                for (int i = 2000; i <= 2026; i++) { // Rango fijo solicitado
                    this.cmbAnio.addItem(String.valueOf(i));
                }

                if (selectedAnio != null) {
                    // Intentar restaurar selección
                    boolean found = false;
                    for (int i = 0; i < this.cmbAnio.getItemCount(); i++) {
                        if (this.cmbAnio.getItemAt(i).equals(selectedAnio.toString())) {
                            this.cmbAnio.setSelectedIndex(i);
                            found = true;
                            break;
                        }
                    }
                    if (!found) this.cmbAnio.setSelectedIndex(0);
                }

                for (java.awt.event.ActionListener l : anioListeners) {
                    this.cmbAnio.addActionListener(l);
                }

                for(Bus b : (new Buses("buses.dat")).lista()) {
                    mapaBuses.put(b.getIdBus(), b.getPlaca());
                }
                for(Ruta r : (new Rutas("rutas.dat")).lista()) {
                    mapaOrigenes.put(r.getIdRuta(), r.getOrigen());
                    mapaDestinos.put(r.getIdRuta(), r.getDestino());
                }
                for(Conductor c : (new Conductores("conductores.dat")).lista()) {
                    mapaConductores.put(c.getIdConductor(), c.getNombre());
                }
            } catch (Exception e) {
                // Ignorar errores de carga de mapas auxiliares si archivos no existen
            }

            this.filtrar();
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }

    }

    private void filtrar() {
        this.modelo.setRowCount(0);
        if (this.listaViajesCompleta == null) {
            return;
        }

        // Validar componentes antes de usarlos
        if (this.cmbAnio == null || this.cmbMes == null || this.cmbEmpresa == null) {
            return;
        }

        java.util.Set<String> procesados = new java.util.HashSet<>();

        // 1. OBTENER VALORES DE LOS FILTROS
        String textoBusqueda = this.txtBuscar.getText().toUpperCase().trim();
        String[] palabrasClave = textoBusqueda.split("\\s+");
        boolean soloDisponibles = this.chkSoloDisponibles.isSelected();

        Object anioObj = this.cmbAnio.getSelectedItem();
        String anioSel = anioObj != null ? anioObj.toString() : "Año: Todos";

        int mesSel = this.cmbMes.getSelectedIndex(); // 0 para "Todos"

        Object empObj = this.cmbEmpresa.getSelectedItem();
        String empresaSel = empObj != null ? empObj.toString() : "Todas las Empresas";

        Integer anioFiltro = null;
        if (!"Año: Todos".equals(anioSel)) {
            try {
                anioFiltro = Integer.parseInt(anioSel);
            } catch (NumberFormatException e) {
                // Si el año no es un número, se ignora el filtro.
            }
        }

        // 2. ITERAR Y APLICAR FILTROS A CADA VIAJE
        for (Viaje v : this.listaViajesCompleta) {
            // Evitar duplicados en la vista
            if (procesados.contains(v.getIdViaje())) {
                continue;
            }

            // FILTRO 1: EMPRESA
            String idEmpresaViaje = v.getEmpresa() != null ? v.getEmpresa().getIdEmpresa().trim() : "";

            if (this.empresaFiltro != null) {
                if (!idEmpresaViaje.equals(this.empresaFiltro.getIdEmpresa().trim())) {
                    continue; // No pertenece a la empresa del usuario
                }
            }
            else if (!"Todas las Empresas".equals(empresaSel)) {
                String nombreEmpresaViaje = mapaEmpresas.getOrDefault(idEmpresaViaje, "");
                if (!nombreEmpresaViaje.equals(empresaSel)) {
                    continue; // No coincide con la empresa seleccionada
                }
            }

            // FILTRO 2: FECHA (AÑO Y MES)
            if (anioFiltro != null || mesSel > 0) {
                if (v.getFechaSalida() == null) {
                    continue;
                }
                if (anioFiltro != null && v.getFechaSalida().getAnio() != anioFiltro) {
                    continue;
                }
                if (mesSel > 0 && v.getFechaSalida().getMes() != mesSel) {
                    continue;
                }
            }

            // FILTRO 3: DISPONIBILIDAD
            if (soloDisponibles) {
                boolean esProgramado = "Programado".equalsIgnoreCase(v.getEstado());
                boolean tieneAsientos = v.getAsientosDisponibles() > 0;
                if (!esProgramado || !tieneAsientos) {
                    continue;
                }
            }

            // FILTRO 4: TEXTO DE BÚSQUEDA
            String origen = (String)this.mapaOrigenes.getOrDefault(v.getRuta() != null ? v.getRuta().getIdRuta() : "", "N/A");
            String destino = (String)this.mapaDestinos.getOrDefault(v.getRuta() != null ? v.getRuta().getIdRuta() : "", "N/A");

            if (!textoBusqueda.isEmpty()) {
                String busStr = v.getBus() != null ? (String)this.mapaBuses.getOrDefault(v.getBus().getIdBus(), "") : "";
                String conductorStr = v.getConductor1() != null ? (String)this.mapaConductores.getOrDefault(v.getConductor1().getIdConductor(), "") : "";
                if (v.getConductor2() != null) {
                    conductorStr += " " + (String)this.mapaConductores.getOrDefault(v.getConductor2().getIdConductor(), "");
                }
                String fechaStr = v.getFechaSalida() != null ? v.getFechaSalida().toString() : "";

                String datosCompletos = (v.getIdViaje() + " " + origen + " " + destino + " " + busStr + " " + conductorStr + " " + fechaStr).toUpperCase();

                boolean coincideTodas = true;
                for (String palabra : palabrasClave) {
                    if (!datosCompletos.contains(palabra)) {
                        coincideTodas = false;
                        break;
                    }
                }
                if (!coincideTodas) {
                    continue;
                }
            }

            // PREPARAR DATOS PARA LA TABLA
            String fechaSalida = "";
            try {
                if (v.getFechaSalida() != null) {
                    fechaSalida = v.getFechaSalida().toString();
                    if (v.getHoraSalida() != null && !v.getHoraSalida().isEmpty()) {
                        fechaSalida += " " + v.getHoraSalida();
                    }
                }
            } catch (Exception var18) {
                fechaSalida = "Formato incorrecto";
            }

            String nombreEmpresa = "N/A";
            if (v.getEmpresa() != null) {
                String id = v.getEmpresa().getIdEmpresa();
                nombreEmpresa = (String)this.mapaEmpresas.getOrDefault(id, id);
            }

            String placaBus = "N/A";
            if (v.getBus() != null) {
                String id = v.getBus().getIdBus();
                placaBus = (String)this.mapaBuses.getOrDefault(id, id);
            }

            String nombreConductor = "N/A";
            if (v.getConductor1() != null) {
                String id = v.getConductor1().getIdConductor();
                nombreConductor = (String)this.mapaConductores.getOrDefault(id, id);
            }

            if (v.getConductor2() != null) {
                String id2 = v.getConductor2().getIdConductor();
                String nombre2 = (String)this.mapaConductores.getOrDefault(id2, id2);
                nombreConductor += " / " + nombre2;
            }

            String anioStr = v.getFechaSalida() != null ? String.valueOf(v.getFechaSalida().getAnio()) : "N/A";
            String mesStr = v.getFechaSalida() != null ? obtenerNombreMes(v.getFechaSalida().getMes()) : "N/A";

            this.modelo.addRow(new Object[]{v.getIdViaje(), nombreEmpresa, anioStr, mesStr, placaBus, origen, destino, fechaSalida, nombreConductor, v.getAsientosDisponibles(), v.getEstado()});
            procesados.add(v.getIdViaje());
        }
    }

    private String obtenerNombreMes(int mes) {
        String[] meses = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        if (mes >= 1 && mes <= 12) {
            return meses[mes];
        }
        return "N/A";
    }

    private void agregar() {
        InterfacesUsuario.DialogoFormularioViaje d = new InterfacesUsuario.DialogoFormularioViaje((JFrame)SwingUtilities.getWindowAncestor(this), "Agregar Viaje", (Viaje)null, this.empresaFiltro);
        d.setVisible(true);
        if (d.isConfirmado()) {
            Viaje v = d.getViaje();
            ResultadoOperacion res = this.controlador.programarViaje(v.getIdViaje(), v.getBus().getIdBus(), v.getRuta().getIdRuta(), v.getFechaSalida(), v.getFechaLlegada(), v.getEmpresa().getIdEmpresa(), v.getEstado(), true, v.getHoraSalida(), v.getHoraLlegada(), v.getPrecio(), v.getConductor1() != null ? v.getConductor1().getIdConductor() : null, v.getConductor2() != null ? v.getConductor2().getIdConductor() : null);
            if (res.isExito()) {
                this.cargarDatos();
                JOptionPane.showMessageDialog(this, "Viaje agregado y asientos generados exitosamente");
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + res.getMensaje());
            }
        }

    }

    private void editar() {
        int fila = this.tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un viaje de la tabla", "Aviso", 2);
        } else {
            try {
                String id = (String)this.modelo.getValueAt(fila, 0);
                Viaje v = this.persistencia.obten(new Viaje(id));
                InterfacesUsuario.DialogoFormularioViaje d = new InterfacesUsuario.DialogoFormularioViaje((JFrame)SwingUtilities.getWindowAncestor(this), "Editar Viaje", v, (Empresa)null);
                d.setVisible(true);
                if (d.isConfirmado()) {
                    Viaje viajeEditado = d.getViaje();
                    ResultadoOperacion res = this.controlador.actualizarViaje(viajeEditado.getIdViaje(), viajeEditado.getBus().getIdBus(), viajeEditado.getRuta().getIdRuta(), viajeEditado.getFechaSalida(), viajeEditado.getFechaLlegada(), viajeEditado.getEmpresa().getIdEmpresa(), viajeEditado.getAsientosDisponibles(), viajeEditado.getEstado(), viajeEditado.getPrecio(), viajeEditado.getHoraSalida(), viajeEditado.getHoraLlegada(), viajeEditado.getConductor1() != null ? viajeEditado.getConductor1().getIdConductor() : null, viajeEditado.getConductor2() != null ? viajeEditado.getConductor2().getIdConductor() : null);
                    if (res.isExito()) {
                        this.cargarDatos();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error: " + res.getMensaje());
                    }
                }
            } catch (PersistenciaException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }

        }
    }

    private void verDetalles() {
        int fila = this.tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un viaje de la tabla para ver sus detalles.", "Aviso", 2);
        } else {
            String id = (String)this.modelo.getValueAt(fila, 0);
            Viaje v = null;
            if (this.listaViajesCompleta != null) {
                for(Viaje viaje : this.listaViajesCompleta) {
                    if (viaje.getIdViaje().equals(id)) {
                        v = viaje;
                        break;
                    }
                }
            }

            if (v != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("=== DETALLES DEL VIAJE ===\n\n");
                sb.append("ID: ").append(v.getIdViaje()).append("\n");
                sb.append("Empresa: ").append(v.getEmpresa() != null ? (String)this.mapaEmpresas.getOrDefault(v.getEmpresa().getIdEmpresa(), "N/A") : "N/A").append("\n");
                sb.append("Estado: ").append(v.getEstado()).append("\n");
                sb.append("--------------------------------\n");
                sb.append("Ruta: ").append(v.getRuta() != null ? (String)this.mapaOrigenes.getOrDefault(v.getRuta().getIdRuta(), "") + " - " + (String)this.mapaDestinos.getOrDefault(v.getRuta().getIdRuta(), "") : "N/A").append("\n");
                sb.append("Bus: ").append(v.getBus() != null ? (String)this.mapaBuses.getOrDefault(v.getBus().getIdBus(), "N/A") : "N/A").append("\n");
                sb.append("--------------------------------\n");
                sb.append("Fecha Salida: ").append(v.getFechaSalida()).append(" ").append(v.getHoraSalida() != null ? v.getHoraSalida() : "").append("\n");
                sb.append("Fecha Llegada: ").append(v.getFechaLlegada()).append(" ").append(v.getHoraLlegada() != null ? v.getHoraLlegada() : "").append("\n");
                sb.append("--------------------------------\n");
                sb.append("Conductor 1: ").append(v.getConductor1() != null ? (String)this.mapaConductores.getOrDefault(v.getConductor1().getIdConductor(), "N/A") : "N/A").append("\n");
                if (v.getConductor2() != null) {
                    sb.append("Conductor 2: ").append((String)this.mapaConductores.getOrDefault(v.getConductor2().getIdConductor(), "N/A")).append("\n");
                }
                sb.append("--------------------------------\n");
                sb.append("Precio: $").append(v.getPrecio()).append("\n");
                sb.append("Asientos Disponibles: ").append(v.getAsientosDisponibles()).append("\n");

                JTextArea area = new JTextArea(sb.toString());
                area.setEditable(false);
                area.setFont(new java.awt.Font("Monospaced", 0, 12));
                JOptionPane.showMessageDialog(this, new JScrollPane(area), "Detalles del Viaje", 1);
            }
        }
    }

    private void eliminar() {
        int fila = this.tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un viaje de la tabla", "Aviso", 2);
        } else if (JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar este viaje?", "Confirmar eliminación", 0) == 0) {
            ResultadoOperacion res = this.controlador.eliminarViaje((String)this.modelo.getValueAt(fila, 0));
            if (res.isExito()) {
                this.cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + res.getMensaje());
            }

        }
    }
}
