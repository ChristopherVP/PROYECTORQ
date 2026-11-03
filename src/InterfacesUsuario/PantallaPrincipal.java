//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import objetosNegocio.Empresa;
import objetosNegocio.Usuario;
import persistencia.Empresas;

public class PantallaPrincipal extends JFrame {
    private Usuario usuarioActual;
    private Empresa empresaActual;
    private JMenuBar menuBar;
    private JTabbedPane tabbedPane;
    private InterfacesUsuario.PanelGestionBuses panelBuses;
    private InterfacesUsuario.PanelGestionRutas panelRutas;
    private InterfacesUsuario.PanelGestionViajes panelViajes;
    private InterfacesUsuario.PanelGestionReservas panelReservas;
    private InterfacesUsuario.PanelGestionFacturas panelFacturas;
    private InterfacesUsuario.PanelGestionPasajeros panelPasajeros;
    private InterfacesUsuario.PanelGestionConductores panelConductores;

    public PantallaPrincipal(Usuario usuario) {
        this.usuarioActual = usuario;
        this.initComponents();
    }

    private void initComponents() {
        this.setTitle("Sistema de Transporte Interprovincial - Usuario: " + this.usuarioActual.getIdUsuario());
        this.setDefaultCloseOperation(3);
        this.setSize(1200, 700);
        this.setExtendedState(6);
        this.setLocationRelativeTo((Component)null);
        this.crearMenu();
        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.setFont(new Font("Arial", 1, 14));
        this.tabbedPane.setOpaque(true);
        this.tabbedPane.addTab("Empresas", (Icon)null, this.createEmpresasPanel(), "Gestión de Empresas");
        this.tabbedPane.addTab("Usuarios", (Icon)null, this.createUsuariosPanel(), "Gestión de Usuarios");
        this.panelConductores = new InterfacesUsuario.PanelGestionConductores();
        this.tabbedPane.addTab("Conductores", (Icon)null, this.panelConductores, "Gestión de Conductores");
        this.tabbedPane.addTab("Pasajeros", (Icon)null, this.createPasajerosPanel(), "Gestión de Pasajeros");
        this.tabbedPane.addTab("Buses", (Icon)null, this.createBusesPanel(), "Gestión de Buses");
        this.tabbedPane.addTab("Rutas", (Icon)null, this.createRutasPanel(), "Gestión de Rutas");
        this.tabbedPane.addTab("Viajes", (Icon)null, this.createViajesPanel(), "Gestión de Viajes");
        this.tabbedPane.addTab("Reservas", (Icon)null, this.createReservasPanel(), "Gestión de Reservas");
        this.tabbedPane.addTab("Facturas", (Icon)null, this.createFacturasPanel(), "Gestión de Facturas");
        this.tabbedPane.setBackgroundAt(0, new Color(220, 220, 220));
        this.tabbedPane.setBackgroundAt(1, new Color(230, 230, 250));
        this.tabbedPane.setBackgroundAt(2, new Color(255, 215, 0));
        this.tabbedPane.setBackgroundAt(3, new Color(255, 228, 196));
        this.tabbedPane.setBackgroundAt(4, new Color(173, 216, 230));
        this.tabbedPane.setBackgroundAt(5, new Color(152, 251, 152));
        this.tabbedPane.setBackgroundAt(6, new Color(255, 255, 224));
        this.tabbedPane.setBackgroundAt(7, new Color(255, 182, 193));
        this.tabbedPane.setBackgroundAt(8, new Color(224, 255, 255));
        if (this.usuarioActual.getEmpresa() != null) {
            try {
                Empresas dao = new Empresas("empresas.dat");
                Empresa empCompleta = dao.obten(this.usuarioActual.getEmpresa());
                if (empCompleta != null) {
                    this.usuarioActual.setEmpresa(empCompleta);
                }
            } catch (Exception var3) {
            }

            if ("Operador".equalsIgnoreCase(this.usuarioActual.getTipo())) {
                this.bloquearModulos();
            } else {
                this.desbloquearModulos(this.usuarioActual.getEmpresa());
            }
        } else if (this.usuarioActual != null && "Administrador".equalsIgnoreCase(this.usuarioActual.getTipo())) {
            this.setTitle("Sistema de Transporte - Usuario: " + this.usuarioActual.getIdUsuario() + " | SUPER ADMINISTRADOR");

            for(int i = 1; i < this.tabbedPane.getTabCount(); ++i) {
                this.tabbedPane.setEnabledAt(i, true);
            }
        } else {
            this.bloquearModulos();
        }

        this.add(this.tabbedPane, "Center");
        JPanel panelInferior = new JPanel(new FlowLayout(0));
        panelInferior.setBorder(BorderFactory.createEtchedBorder());
        JLabel lblInfo = new JLabel("Sistema de Gestión de Buses v2.0 | Usuario: " + this.usuarioActual.getIdUsuario());
        lblInfo.setFont(new Font("Arial", 0, 11));
        panelInferior.add(lblInfo);
        this.add(panelInferior, "South");
    }

    public void bloquearModulos() {
        this.empresaActual = null;

        for(int i = 1; i < this.tabbedPane.getTabCount(); ++i) {
            this.tabbedPane.setEnabledAt(i, false);
            this.tabbedPane.setBackgroundAt(i, Color.LIGHT_GRAY);
        }

        this.tabbedPane.setSelectedIndex(0);
        this.setTitle("Sistema de Transporte - Usuario: " + this.usuarioActual.getIdUsuario() + " | Seleccione una Empresa");
    }

    public void desbloquearModulos(Empresa empresa) {
        this.empresaActual = empresa;
        if (this.panelBuses != null) {
            this.panelBuses.setEmpresaFiltro(empresa);
        }

        if (this.panelRutas != null) {
            this.panelRutas.setEmpresaFiltro(empresa);
        }

        if (this.panelViajes != null) {
            this.panelViajes.setEmpresaFiltro(empresa);
        }

        if (this.panelReservas != null) {
            this.panelReservas.setEmpresaFiltro(empresa);
        }

        if (this.panelFacturas != null) {
            this.panelFacturas.setEmpresaFiltro(empresa);
        }

        if (this.panelPasajeros != null) {
            this.panelPasajeros.setEmpresaFiltro(empresa);
        }

        if (this.panelConductores != null) {
            this.panelConductores.setEmpresaFiltro(empresa);
        }

        for(int i = 1; i < this.tabbedPane.getTabCount(); ++i) {
            this.tabbedPane.setEnabledAt(i, true);
        }

        this.tabbedPane.setBackgroundAt(1, new Color(230, 230, 250));
        this.tabbedPane.setBackgroundAt(2, new Color(255, 215, 0));
        this.tabbedPane.setBackgroundAt(3, new Color(255, 228, 196));
        this.tabbedPane.setBackgroundAt(4, new Color(173, 216, 230));
        this.tabbedPane.setBackgroundAt(5, new Color(152, 251, 152));
        this.tabbedPane.setBackgroundAt(6, new Color(255, 255, 224));
        this.tabbedPane.setBackgroundAt(7, new Color(255, 182, 193));
        this.tabbedPane.setBackgroundAt(8, new Color(224, 255, 255));
        String nombreEmpresa = empresa.getNombre() != null ? empresa.getNombre() : empresa.getIdEmpresa();
        String var10001 = this.usuarioActual.getIdUsuario();
        this.setTitle("Sistema de Transporte - Usuario: " + var10001 + " | Empresa: " + nombreEmpresa);
        this.tabbedPane.setSelectedIndex(1);
        JLabel lblMensaje = new JLabel("Acceso concedido a: " + nombreEmpresa);
        lblMensaje.setFont(new Font("Arial", 1, 18));
        JOptionPane.showMessageDialog(this, lblMensaje, "Bienvenido", 1);
    }

    private void crearMenu() {
        this.menuBar = new JMenuBar();
        JMenu menuGestion = new JMenu("Gestión");
        menuGestion.setMnemonic(71);
        JMenuItem itemEmpresas = new JMenuItem("Empresas", 77);
        itemEmpresas.setAccelerator(KeyStroke.getKeyStroke(77, 128));
        itemEmpresas.addActionListener((e) -> this.tabbedPane.setSelectedIndex(0));
        menuGestion.add(itemEmpresas);
        JMenuItem itemUsuarios = new JMenuItem("Usuarios", 85);
        itemUsuarios.setAccelerator(KeyStroke.getKeyStroke(85, 128));
        itemUsuarios.addActionListener((e) -> this.navegarSiHabilitado(1));
        menuGestion.add(itemUsuarios);
        JMenuItem itemBuses = new JMenuItem("Buses", 66);
        itemBuses.setAccelerator(KeyStroke.getKeyStroke(66, 128));
        itemBuses.addActionListener((e) -> this.navegarSiHabilitado(4));
        menuGestion.add(itemBuses);
        JMenuItem itemRutas = new JMenuItem("Rutas", 82);
        itemRutas.setAccelerator(KeyStroke.getKeyStroke(82, 128));
        itemRutas.addActionListener((e) -> this.navegarSiHabilitado(5));
        menuGestion.add(itemRutas);
        JMenuItem itemViajes = new JMenuItem("Viajes", 86);
        itemViajes.setAccelerator(KeyStroke.getKeyStroke(86, 128));
        itemViajes.addActionListener((e) -> this.navegarSiHabilitado(6));
        menuGestion.add(itemViajes);
        JMenuItem itemPasajeros = new JMenuItem("Pasajeros", 74);
        itemPasajeros.setAccelerator(KeyStroke.getKeyStroke(74, 128));
        itemPasajeros.addActionListener((e) -> this.navegarSiHabilitado(3));
        menuGestion.add(itemPasajeros);
        JMenuItem itemReservas = new JMenuItem("Reservas", 69);
        itemReservas.setAccelerator(KeyStroke.getKeyStroke(69, 128));
        itemReservas.addActionListener((e) -> this.navegarSiHabilitado(7));
        menuGestion.add(itemReservas);
        JMenuItem itemFacturas = new JMenuItem("Facturas", 70);
        itemFacturas.setAccelerator(KeyStroke.getKeyStroke(70, 128));
        itemFacturas.addActionListener((e) -> this.navegarSiHabilitado(8));
        menuGestion.add(itemFacturas);
        menuGestion.addSeparator();
        JMenuItem itemSalir = new JMenuItem("Salir", 83);
        itemSalir.setAccelerator(KeyStroke.getKeyStroke(81, 128));
        itemSalir.addActionListener((e) -> {
            int opcion = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea salir?", "Confirmar salida", 0);
            if (opcion == 0) {
                System.exit(0);
            }

        });
        menuGestion.add(itemSalir);
        JMenu menuConsultas = new JMenu("Consultas");
        menuConsultas.setMnemonic(67);
        JMenuItem itemConsultasEspeciales = new JMenuItem("Consultas Especiales");
        itemConsultasEspeciales.setAccelerator(KeyStroke.getKeyStroke(75, 128));
        itemConsultasEspeciales.addActionListener((e) -> this.abrirConsultasEspeciales());
        menuConsultas.add(itemConsultasEspeciales);
        JMenu menuReportes = new JMenu("Reportes");
        menuReportes.setMnemonic(80);
        JMenuItem itemReportes = new JMenuItem("Reportes y Estadísticas");
        itemReportes.setAccelerator(KeyStroke.getKeyStroke(80, 128));
        itemReportes.addActionListener((e) -> this.abrirReportes());
        menuReportes.add(itemReportes);
        JMenu menuAyuda = new JMenu("Ayuda");
        menuAyuda.setMnemonic(65);
        JMenuItem itemManual = new JMenuItem("Manual de Usuario");
        itemManual.setAccelerator(KeyStroke.getKeyStroke(112, 0));
        itemManual.addActionListener((e) -> this.abrirManualUsuario());
        menuAyuda.add(itemManual);
        JMenuItem itemAcercaDe = new JMenuItem("Acerca de");
        itemAcercaDe.addActionListener((e) -> this.mostrarAcercaDe());
        menuAyuda.add(itemAcercaDe);
        this.menuBar.add(menuGestion);
        this.menuBar.add(menuConsultas);
        this.menuBar.add(menuReportes);
        this.menuBar.add(menuAyuda);
        this.setJMenuBar(this.menuBar);
    }

    private void navegarSiHabilitado(int index) {
        if (this.tabbedPane.isEnabledAt(index)) {
            this.tabbedPane.setSelectedIndex(index);
        } else {
            JOptionPane.showMessageDialog(this, "Debe acceder a una empresa primero.", "Acceso Restringido", 2);
        }

    }

    private JPanel createBusesPanel() {
        this.panelBuses = new InterfacesUsuario.PanelGestionBuses();
        return this.panelBuses;
    }

    private JPanel createRutasPanel() {
        this.panelRutas = new InterfacesUsuario.PanelGestionRutas();
        return this.panelRutas;
    }

    private JPanel createViajesPanel() {
        this.panelViajes = new InterfacesUsuario.PanelGestionViajes(this.usuarioActual);
        return this.panelViajes;
    }

    private JPanel createReservasPanel() {
        this.panelReservas = new InterfacesUsuario.PanelGestionReservas();
        return this.panelReservas;
    }

    private JPanel createFacturasPanel() {
        this.panelFacturas = new InterfacesUsuario.PanelGestionFacturas(this.usuarioActual);
        return this.panelFacturas;
    }

    private JPanel createUsuariosPanel() {
        return new InterfacesUsuario.PanelGestionUsuarios(this.usuarioActual);
    }

    private JPanel createPasajerosPanel() {
        this.panelPasajeros = new InterfacesUsuario.PanelGestionPasajeros();
        return this.panelPasajeros;
    }

    private JPanel createEmpresasPanel() {
        return new InterfacesUsuario.PanelGestionEmpresas(this.usuarioActual);
    }

    private void abrirConsultasEspeciales() {
        InterfacesUsuario.PantallaConsultasEspeciales consultas = new InterfacesUsuario.PantallaConsultasEspeciales(this, this.empresaActual);
        consultas.setVisible(true);
    }

    private void abrirReportes() {
        PantallaReportes reportes = new PantallaReportes(this, this.empresaActual);
        reportes.setVisible(true);
    }

    private void abrirManualUsuario() {
        InterfacesUsuario.PantallaManualUsuario manual = new InterfacesUsuario.PantallaManualUsuario(this);
        manual.setVisible(true);
    }

    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(this, "Sistema de Transporte Interprovincial\nVersión 2.0\n\nSistema integral de gestión de transporte de pasajeros\nDesarrollado para la administración eficiente de buses,\nrutas, viajes, reservas y facturación.\n\n© 2026 - Todos los derechos reservados", "Acerca del Sistema", 1);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario("ADMIN");
                usuario.setTipo("Administrador");
                (new PantallaPrincipal(usuario)).setVisible(true);
            }
        });
    }
}
