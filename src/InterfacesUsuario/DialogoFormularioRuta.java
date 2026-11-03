//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import excepciones.PersistenciaException;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import objetosNegocio.Empresa;
import objetosNegocio.Ruta;
import persistencia.Empresas;

public class DialogoFormularioRuta extends JDialog {
    private JTextField txtId;
    private JTextField txtDistancia;
    private JTextField txtPrecio;
    private JComboBox<String> cmbProvinciaOrigen;
    private JComboBox<String> cmbCiudadOrigen;
    private JComboBox<String> cmbProvinciaDestino;
    private JComboBox<String> cmbCiudadDestino;
    private JTextField txtTiempoEstimado;
    private JTextField txtTiempoDetencion;
    private JTextArea txtParadas;
    private JComboBox<String> cmbEstado;
    private JComboBox<String> cmbEmpresa;
    private PanelMapa panelMapa;
    private Ruta ruta;
    private boolean confirmado = false;
    private List<Empresa> empresas;
    private Empresa empresaPreseleccionada;
    private Map<String, List<String>> datosGeograficos;
    public static final Map<String, Map<String, Double>> GRAFO_VIAL = new HashMap();
    public static final Map<String, Point> COORDENADAS = new HashMap();

    public DialogoFormularioRuta(JFrame padre, String titulo, Ruta r, Empresa empresaPreseleccionada) {
        super(padre, titulo, true);
        this.ruta = r;
        this.empresaPreseleccionada = empresaPreseleccionada;
        this.inicializarDatosGeograficos();
        this.cargarEmpresas();
        this.initComponents();
        if (r != null) {
            this.cargar();
        }

    }

    private void cargarEmpresas() {
        try {
            Empresas persistenciaEmpresas = new Empresas("empresas.dat");
            this.empresas = persistenciaEmpresas.lista();
        } catch (PersistenciaException e) {
            this.empresas = new ArrayList();
            JOptionPane.showMessageDialog(this, "Error al cargar empresas: " + e.getMessage());
        }

    }

    private void inicializarDatosGeograficos() {
        this.datosGeograficos = new TreeMap();
        this.datosGeograficos.put("Azuay", Arrays.asList("Cuenca", "Giron"));
        this.datosGeograficos.put("Bolívar", Arrays.asList("Guaranda"));
        this.datosGeograficos.put("Cañar", Arrays.asList("Azogues", "La Troncal", "Zhud"));
        this.datosGeograficos.put("Carchi", Arrays.asList("Tulcán", "San Gabriel", "Mira"));
        this.datosGeograficos.put("Chimborazo", Arrays.asList("Riobamba", "Alausí", "Chunchi", "Pallatanga", "Guamote"));
        this.datosGeograficos.put("Cotopaxi", Arrays.asList("Latacunga", "La Maná", "Salcedo"));
        this.datosGeograficos.put("Imbabura", Arrays.asList("Ibarra", "Otavalo"));
        this.datosGeograficos.put("Loja", Arrays.asList("Loja", "Catamayo", "Macará", "Saraguro"));
        this.datosGeograficos.put("Pichincha", Arrays.asList("Quito", "Cayambe", "Machachi", "San Miguel de los Bancos"));
        this.datosGeograficos.put("Tungurahua", Arrays.asList("Ambato", "Baños", "Pelileo"));
        this.datosGeograficos.put("El Oro", Arrays.asList("Machala", "Santa Rosa", "Pasaje", "Huaquillas", "Piñas", "Balsas"));
        this.datosGeograficos.put("Esmeraldas", Arrays.asList("Esmeraldas", "Quinindé", "Atacames", "San Lorenzo"));
        this.datosGeograficos.put("Guayas", Arrays.asList("Guayaquil", "Milagro", "Durán", "El Triunfo", "Bucay"));
        this.datosGeograficos.put("Los Ríos", Arrays.asList("Babahoyo", "Quevedo"));
        this.datosGeograficos.put("Manabí", Arrays.asList("Portoviejo", "Manta", "Chone", "El Carmen", "Jipijapa", "Bahía de Caráquez", "Rocafuerte", "Pedernales", "Jama", "San Vicente"));
        this.datosGeograficos.put("Santa Elena", Arrays.asList("Santa Elena", "Salinas", "La Libertad"));
        this.datosGeograficos.put("Santo Domingo", Arrays.asList("Santo Domingo"));
        this.datosGeograficos.put("Morona Santiago", Arrays.asList("Macas", "Sucúa", "Gualaquiza", "Méndez"));
        this.datosGeograficos.put("Napo", Arrays.asList("Tena", "El Chaco", "Baeza", "Papallacta"));
        this.datosGeograficos.put("Orellana", Arrays.asList("Coca", "Joya de los Sachas"));
        this.datosGeograficos.put("Pastaza", Arrays.asList("Puyo"));
        this.datosGeograficos.put("Sucumbíos", Arrays.asList("Lago Agrio"));
        this.datosGeograficos.put("Zamora Chinchipe", Arrays.asList("Zamora", "Yantzaza"));
    }

    private void initComponents() {
        this.setSize(1200, 800);
        this.setLocationRelativeTo(this.getParent());
        this.setLayout(new BorderLayout());
        JPanel pForm = new JPanel(new GridBagLayout());
        pForm.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), BorderFactory.createTitledBorder("Información de la Ruta")));
        pForm.setPreferredSize(new Dimension(480, 0));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = 2;
        g.insets = new Insets(8, 8, 8, 8);
        g.gridx = 0;
        g.gridy = 0;
        g.anchor = 13;
        pForm.add(new JLabel("ID Ruta:"), g);
        g.anchor = 17;
        g.gridx = 1;
        this.txtId = new JTextField(20);
        this.txtId.setEnabled(false);
        this.txtId.setBackground(Color.LIGHT_GRAY);
        this.txtId.setText("(Se generará automáticamente)");
        pForm.add(this.txtId, g);
        g.gridx = 0;
        g.gridy = 1;
        g.anchor = 13;
        pForm.add(new JLabel("Origen: *"), g);
        g.anchor = 17;
        JPanel pOrigen = new JPanel(new FlowLayout(0, 5, 0));
        this.cmbProvinciaOrigen = new JComboBox((String[])this.datosGeograficos.keySet().toArray(new String[0]));
        this.cmbProvinciaOrigen.insertItemAt("-- Provincia --", 0);
        this.cmbProvinciaOrigen.setSelectedIndex(0);
        this.cmbCiudadOrigen = new JComboBox();
        this.cmbCiudadOrigen.addItem("-- Ciudad --");
        this.cmbProvinciaOrigen.addActionListener((ex) -> this.actualizarCiudades(this.cmbProvinciaOrigen, this.cmbCiudadOrigen));
        this.cmbCiudadOrigen.addActionListener((ex) -> this.calcularRutaAutomatica());
        pOrigen.add(this.cmbProvinciaOrigen);
        pOrigen.add(this.cmbCiudadOrigen);
        g.gridx = 1;
        pForm.add(pOrigen, g);
        g.gridx = 0;
        g.gridy = 2;
        g.anchor = 13;
        pForm.add(new JLabel("Destino: *"), g);
        g.anchor = 17;
        JPanel pDestino = new JPanel(new FlowLayout(0, 5, 0));
        this.cmbProvinciaDestino = new JComboBox((String[])this.datosGeograficos.keySet().toArray(new String[0]));
        this.cmbProvinciaDestino.insertItemAt("-- Provincia --", 0);
        this.cmbProvinciaDestino.setSelectedIndex(0);
        this.cmbCiudadDestino = new JComboBox();
        this.cmbCiudadDestino.addItem("-- Ciudad --");
        this.cmbProvinciaDestino.addActionListener((ex) -> this.actualizarCiudades(this.cmbProvinciaDestino, this.cmbCiudadDestino));
        this.cmbCiudadDestino.addActionListener((ex) -> this.calcularRutaAutomatica());
        pDestino.add(this.cmbProvinciaDestino);
        pDestino.add(this.cmbCiudadDestino);
        g.gridx = 1;
        pForm.add(pDestino, g);
        g.gridx = 0;
        g.gridy = 3;
        g.anchor = 13;
        pForm.add(new JLabel("Distancia (km): *"), g);
        g.anchor = 17;
        g.gridx = 1;
        this.txtDistancia = new JTextField(20);
        this.agregarFiltroDecimal(this.txtDistancia);
        pForm.add(this.txtDistancia, g);
        g.gridx = 0;
        g.gridy = 4;
        g.anchor = 13;
        pForm.add(new JLabel("Precio ($): *"), g);
        g.anchor = 17;
        g.gridx = 1;
        this.txtPrecio = new JTextField(20);
        this.agregarFiltroDecimal(this.txtPrecio);
        pForm.add(this.txtPrecio, g);
        g.gridx = 0;
        g.gridy = 5;
        g.anchor = 13;
        pForm.add(new JLabel("Tiempo Est. (horas):"), g);
        g.anchor = 17;
        g.gridx = 1;
        this.txtTiempoEstimado = new JTextField(20);
        this.agregarFiltroDecimal(this.txtTiempoEstimado);
        pForm.add(this.txtTiempoEstimado, g);
        g.gridx = 0;
        g.gridy = 6;
        g.anchor = 13;
        pForm.add(new JLabel("Paradas:"), g);
        g.anchor = 17;
        g.gridx = 1;
        this.txtParadas = new JTextArea(4, 20);
        this.txtParadas.setLineWrap(true);
        this.txtParadas.setWrapStyleWord(true);
        this.txtParadas.setToolTipText("Se generará automáticamente la ruta óptima");
        JScrollPane scrollParadas = new JScrollPane(this.txtParadas);
        pForm.add(scrollParadas, g);
        g.gridx = 0;
        g.gridy = 7;
        g.anchor = 13;
        pForm.add(new JLabel("T. Detención (min):"), g);
        g.anchor = 17;
        g.gridx = 1;
        this.txtTiempoDetencion = new JTextField(20);
        this.txtTiempoDetencion.setText("15");
        pForm.add(this.txtTiempoDetencion, g);
        g.gridx = 0;
        g.gridy = 8;
        g.anchor = 13;
        pForm.add(new JLabel("Estado: *"), g);
        g.anchor = 17;
        g.gridx = 1;
        this.cmbEstado = new JComboBox(new String[]{"ACTIVA", "INACTIVO"});
        pForm.add(this.cmbEstado, g);
        g.gridx = 0;
        g.gridy = 9;
        g.anchor = 13;
        pForm.add(new JLabel("Empresa: *"), g);
        g.anchor = 17;
        g.gridx = 1;
        this.cmbEmpresa = new JComboBox();
        this.cmbEmpresa.addItem("-- Seleccione Empresa --");

        for(Empresa e : this.empresas) {
            JComboBox var10000 = this.cmbEmpresa;
            String var10001 = e.getIdEmpresa();
            var10000.addItem(var10001 + " - " + e.getNombre());
        }

        pForm.add(this.cmbEmpresa, g);
        if (this.empresaPreseleccionada != null) {
            for(int i = 0; i < this.cmbEmpresa.getItemCount(); ++i) {
                if (((String)this.cmbEmpresa.getItemAt(i)).startsWith(this.empresaPreseleccionada.getIdEmpresa())) {
                    this.cmbEmpresa.setSelectedIndex(i);
                    this.cmbEmpresa.setEnabled(false);
                    break;
                }
            }
        }

        this.panelMapa = new PanelMapa();
        JPanel pBtn = new JPanel(new FlowLayout(1, 15, 10));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        btnGuardar.setPreferredSize(new Dimension(120, 35));
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnGuardar.addActionListener((ex) -> this.guardar());
        btnCancelar.addActionListener((ex) -> this.dispose());
        pBtn.add(btnGuardar);
        pBtn.add(btnCancelar);
        this.add(pForm, "West");
        this.add(this.panelMapa, "Center");
        this.add(pBtn, "South");
        this.txtDistancia.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                DialogoFormularioRuta.this.calcularTiempoEstimado();
            }
        });
    }

    private void actualizarCiudades(JComboBox<String> cmbProvincia, JComboBox<String> cmbCiudad) {
        cmbCiudad.removeAllItems();
        cmbCiudad.addItem("-- Ciudad --");
        if (cmbProvincia.getSelectedIndex() > 0) {
            String provincia = (String)cmbProvincia.getSelectedItem();
            List<String> ciudades = (List)this.datosGeograficos.get(provincia);
            if (ciudades != null) {
                Collections.sort(ciudades);

                for(String ciudad : ciudades) {
                    cmbCiudad.addItem(ciudad);
                }
            }
        }

        cmbCiudad.setSelectedIndex(0);
    }

    private void cargar() {
        this.txtId.setText(this.ruta.getIdRuta());
        if (this.ruta.getOrigen() != null) {
            this.seleccionarCiudadEnCombo(this.ruta.getOrigen(), this.cmbProvinciaOrigen, this.cmbCiudadOrigen);
        }

        if (this.ruta.getDestino() != null) {
            this.seleccionarCiudadEnCombo(this.ruta.getDestino(), this.cmbProvinciaDestino, this.cmbCiudadDestino);
        }

        this.txtDistancia.setText(String.valueOf(this.ruta.getDistancia()));
        this.txtPrecio.setText(String.valueOf(this.ruta.getPrecio()));
        this.txtTiempoEstimado.setText(String.valueOf(this.ruta.getTiempoEstimado()));
        this.txtParadas.setText(this.ruta.getParadas());
        this.txtTiempoDetencion.setText(String.valueOf(this.ruta.getTiempoDetencion()));
        this.cmbEstado.setSelectedItem(this.ruta.getEstado());
        if (this.ruta.getEmpresa() != null) {
            for(int i = 0; i < this.cmbEmpresa.getItemCount(); ++i) {
                if (((String)this.cmbEmpresa.getItemAt(i)).startsWith(this.ruta.getEmpresa().getIdEmpresa())) {
                    this.cmbEmpresa.setSelectedIndex(i);
                    break;
                }
            }
        }

        this.calcularRutaAutomatica();
    }

    private void seleccionarCiudadEnCombo(String ciudadBuscada, JComboBox<String> cmbProv, JComboBox<String> cmbCiud) {
        for(Map.Entry<String, List<String>> entry : this.datosGeograficos.entrySet()) {
            for(Object ciudad : (List)entry.getValue()) {
                if (ciudad.equals(ciudadBuscada)) {
                    cmbProv.setSelectedItem(entry.getKey());
                    cmbCiud.setSelectedItem(ciudad);
                    return;
                }
            }
        }

    }

    private static void addTramo(String c1, String c2, Double d) {
        c1 = normalizarCiudadEstatica(c1);
        c2 = normalizarCiudadEstatica(c2);
        ((Map)GRAFO_VIAL.computeIfAbsent(c1, (k) -> new HashMap())).put(c2, d);
        ((Map)GRAFO_VIAL.computeIfAbsent(c2, (k) -> new HashMap())).put(c1, d);
    }

    private static void addCoordenada(String ciudad, int x, int y) {
        COORDENADAS.put(normalizarCiudadEstatica(ciudad), new Point(x, y));
    }

    public static String normalizarCiudadEstatica(String ciudad) {
        if (ciudad == null) {
            return "";
        } else {
            String n = Normalizer.normalize(ciudad, Form.NFD);
            n = n.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
            return n.toUpperCase().trim();
        }
    }

    private String normalizarCiudad(String ciudad) {
        return normalizarCiudadEstatica(ciudad);
    }

    private void calcularRutaAutomatica() {
        if (this.cmbCiudadOrigen.getSelectedIndex() > 0 && this.cmbCiudadDestino.getSelectedIndex() > 0) {
            String origen = this.normalizarCiudad((String)this.cmbCiudadOrigen.getSelectedItem());
            String destino = this.normalizarCiudad((String)this.cmbCiudadDestino.getSelectedItem());
            if (!origen.isEmpty() && !destino.isEmpty() && !origen.equals(destino)) {
                ResultadoRuta resultado = encontrarRutaMasCorta(origen, destino);
                if (resultado != null) {
                    this.txtDistancia.setText(String.format("%.2f", resultado.distanciaTotal).replace(',', '.'));
                    double precioSugerido = (double)Math.round(resultado.distanciaTotal * 0.08 * (double)100.0F) / (double)100.0F;
                    this.txtPrecio.setText(String.format("%.2f", precioSugerido).replace(',', '.'));
                    double tiempo = (double)Math.round(resultado.distanciaTotal / (double)70.0F * (double)10.0F) / (double)10.0F;
                    this.txtTiempoEstimado.setText(String.valueOf(tiempo));
                    if (resultado.camino.size() > 2) {
                        List<String> intermedias = resultado.camino.subList(1, resultado.camino.size() - 1);
                        this.txtParadas.setText(String.join(", ", intermedias));
                    } else {
                        this.txtParadas.setText("Directo (Sin paradas intermedias importantes)");
                    }

                    this.panelMapa.setRuta(resultado.camino);
                } else {
                    this.txtDistancia.setText("");
                    this.txtPrecio.setText("");
                    this.txtParadas.setText("No se encontró una ruta terrestre directa en la base de datos.");
                    this.panelMapa.setRuta((List)null);
                }
            }

        }
    }

    private void calcularTiempoEstimado() {
        try {
            String text = this.txtDistancia.getText().trim().replace(',', '.');
            if (!text.isEmpty()) {
                double distancia = Double.parseDouble(text);
                if (distancia > (double)0.0F) {
                    double tiempo = (double)Math.round(distancia / (double)70.0F * (double)10.0F) / (double)10.0F;
                    this.txtTiempoEstimado.setText(String.valueOf(tiempo));
                }
            }
        } catch (NumberFormatException var6) {
        }

    }

    public static ResultadoRuta encontrarRutaMasCorta(String inicio, String fin) {
        if (GRAFO_VIAL.containsKey(inicio) && GRAFO_VIAL.containsKey(fin)) {
            Map<String, Double> distancias = new HashMap();
            Map<String, String> previos = new HashMap();
            Objects.requireNonNull(distancias);
            PriorityQueue<String> colaPrioridad = new PriorityQueue(Comparator.comparingDouble(distancias::get));
            Set<String> visitados = new HashSet();

            for(String nodo : GRAFO_VIAL.keySet()) {
                distancias.put(nodo, Double.MAX_VALUE);
            }

            distancias.put(inicio, (double)0.0F);
            colaPrioridad.add(inicio);

            while(!colaPrioridad.isEmpty()) {
                String actual = (String)colaPrioridad.poll();
                if (actual.equals(fin)) {
                    break;
                }

                if (!visitados.contains(actual)) {
                    visitados.add(actual);
                    Map<String, Double> vecinos = (Map)GRAFO_VIAL.get(actual);
                    if (vecinos != null) {
                        for(Map.Entry<String, Double> vecinoEntry : vecinos.entrySet()) {
                            String vecino = (String)vecinoEntry.getKey();
                            double peso = (Double)vecinoEntry.getValue();
                            double nuevaDistancia = (Double)distancias.get(actual) + peso;
                            if (nuevaDistancia < (Double)distancias.get(vecino)) {
                                distancias.put(vecino, nuevaDistancia);
                                previos.put(vecino, actual);
                                colaPrioridad.add(vecino);
                            }
                        }
                    }
                }
            }

            if ((Double)distancias.get(fin) == Double.MAX_VALUE) {
                return null;
            } else {
                List<String> camino = new ArrayList();

                for(String paso = fin; paso != null; paso = (String)previos.get(paso)) {
                    camino.add(0, paso);
                }

                return new ResultadoRuta((Double)distancias.get(fin), camino);
            }
        } else {
            return null;
        }
    }

    private void guardar() {
        if (this.cmbCiudadOrigen.getSelectedIndex() > 0 && this.cmbCiudadDestino.getSelectedIndex() > 0 && !this.txtDistancia.getText().trim().isEmpty() && !this.txtPrecio.getText().trim().isEmpty()) {
            String origen = (String)this.cmbCiudadOrigen.getSelectedItem();
            String destino = (String)this.cmbCiudadDestino.getSelectedItem();
            if (origen.equalsIgnoreCase(destino)) {
                JOptionPane.showMessageDialog(this, "Origen y destino deben ser diferentes", "Error", 0);
            } else if (this.cmbEmpresa.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una empresa", "Error", 0);
            } else {
                try {
                    double distancia = Double.parseDouble(this.txtDistancia.getText().trim().replace(',', '.'));
                    double precio = Double.parseDouble(this.txtPrecio.getText().trim().replace(',', '.'));
                    double tiempo = (double)0.0F;
                    if (!this.txtTiempoEstimado.getText().trim().isEmpty()) {
                        tiempo = Double.parseDouble(this.txtTiempoEstimado.getText().trim().replace(',', '.'));
                    }

                    int tiempoDet = 15;
                    if (!this.txtTiempoDetencion.getText().trim().isEmpty()) {
                        tiempoDet = Integer.parseInt(this.txtTiempoDetencion.getText().trim());
                    }

                    if (this.ruta == null) {
                        this.ruta = new Ruta();
                    }

                    this.ruta.setOrigen(origen);
                    this.ruta.setDestino(destino);
                    this.ruta.setDistancia(distancia);
                    this.ruta.setPrecio(precio);
                    this.ruta.setTiempoEstimado(tiempo);
                    this.ruta.setParadas(this.txtParadas.getText().trim());
                    this.ruta.setTiempoDetencion(tiempoDet);
                    this.ruta.setEstado((String)this.cmbEstado.getSelectedItem());
                    String empresaSel = (String)this.cmbEmpresa.getSelectedItem();
                    this.ruta.setEmpresa(new Empresa(empresaSel.split(" - ")[0]));
                    this.confirmado = true;
                    this.dispose();
                } catch (NumberFormatException var11) {
                    JOptionPane.showMessageDialog(this, "Valores numéricos inválidos", "Error", 0);
                }

            }
        } else {
            JOptionPane.showMessageDialog(this, "Complete todos los campos obligatorios", "Error", 0);
        }
    }

    private void agregarFiltroDecimal(final JTextField campo) {
        campo.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != ',' && c != '\b') {
                    evt.consume();
                }

                if ((c == '.' || c == ',') && (campo.getText().contains(".") || campo.getText().contains(","))) {
                    evt.consume();
                }

            }
        });
    }

    public Ruta getRuta() {
        return this.ruta;
    }

    public boolean isConfirmado() {
        return this.confirmado;
    }

    static {
        addTramo("TULCAN", "SAN GABRIEL", (double)45.0F);
        addTramo("SAN GABRIEL", "MIRA", (double)35.0F);
        addTramo("MIRA", "IBARRA", (double)50.0F);
        addTramo("IBARRA", "OTAVALO", (double)25.0F);
        addTramo("OTAVALO", "CAYAMBE", (double)30.0F);
        addTramo("CAYAMBE", "QUITO", (double)68.0F);
        addTramo("QUITO", "MACHACHI", (double)38.0F);
        addTramo("MACHACHI", "LATACUNGA", (double)55.0F);
        addTramo("LATACUNGA", "SALCEDO", (double)15.0F);
        addTramo("SALCEDO", "AMBATO", (double)30.0F);
        addTramo("AMBATO", "PELILEO", (double)15.0F);
        addTramo("AMBATO", "RIOBAMBA", (double)60.0F);
        addTramo("RIOBAMBA", "GUAMOTE", (double)50.0F);
        addTramo("GUAMOTE", "ALAUSI", (double)45.0F);
        addTramo("ALAUSI", "CHUNCHI", (double)35.0F);
        addTramo("CHUNCHI", "ZHUD", (double)40.0F);
        addTramo("ZHUD", "AZOGUES", (double)60.0F);
        addTramo("AZOGUES", "CUENCA", (double)32.0F);
        addTramo("CUENCA", "ONA", (double)105.0F);
        addTramo("ONA", "SARAGURO", (double)45.0F);
        addTramo("SARAGURO", "LOJA", (double)65.0F);
        addTramo("LOJA", "CATAMAYO", (double)38.0F);
        addTramo("CATAMAYO", "MACARA", (double)145.0F);
        addTramo("SAN LORENZO", "ESMERALDAS", (double)160.0F);
        addTramo("ESMERALDAS", "ATACAMES", (double)35.0F);
        addTramo("ATACAMES", "PEDERNALES", (double)185.0F);
        addTramo("PEDERNALES", "JAMA", (double)55.0F);
        addTramo("JAMA", "SAN VICENTE", (double)50.0F);
        addTramo("SAN VICENTE", "BAHIA DE CARAQUEZ", (double)20.0F);
        addTramo("BAHIA DE CARAQUEZ", "ROCAFUERTE", (double)35.0F);
        addTramo("ROCAFUERTE", "PORTOVIEJO", (double)25.0F);
        addTramo("PORTOVIEJO", "MANTA", (double)38.0F);
        addTramo("MANTA", "JIPIJAPA", (double)55.0F);
        addTramo("JIPIJAPA", "SANTA ELENA", (double)145.0F);
        addTramo("SANTA ELENA", "SALINAS", (double)15.0F);
        addTramo("SANTA ELENA", "LA LIBERTAD", (double)10.0F);
        addTramo("SANTA ELENA", "GUAYAQUIL", (double)130.0F);
        addTramo("GUAYAQUIL", "DURAN", (double)10.0F);
        addTramo("DURAN", "MILAGRO", (double)35.0F);
        addTramo("DURAN", "EL TRIUNFO", (double)50.0F);
        addTramo("EL TRIUNFO", "LA TRONCAL", (double)25.0F);
        addTramo("LA TRONCAL", "NARANJAL", (double)40.0F);
        addTramo("NARANJAL", "MACHALA", (double)95.0F);
        addTramo("MACHALA", "SANTA ROSA", (double)30.0F);
        addTramo("SANTA ROSA", "HUAQUILLAS", (double)45.0F);
        addTramo("SANTA ROSA", "PINAS", (double)50.0F);
        addTramo("IBARRA", "SAN LORENZO", (double)190.0F);
        addTramo("QUITO", "SANTO DOMINGO", (double)152.0F);
        addTramo("QUITO", "SAN MIGUEL DE LOS BANCOS", (double)110.0F);
        addTramo("SAN MIGUEL DE LOS BANCOS", "SANTO DOMINGO", (double)80.0F);
        addTramo("SANTO DOMINGO", "QUININDE", (double)90.0F);
        addTramo("QUININDE", "ESMERALDAS", (double)90.0F);
        addTramo("SANTO DOMINGO", "EL CARMEN", (double)35.0F);
        addTramo("EL CARMEN", "CHONE", (double)65.0F);
        addTramo("CHONE", "PORTOVIEJO", (double)80.0F);
        addTramo("SANTO DOMINGO", "QUEVEDO", (double)100.0F);
        addTramo("QUEVEDO", "BABAHOYO", (double)105.0F);
        addTramo("BABAHOYO", "GUAYAQUIL", (double)65.0F);
        addTramo("LATACUNGA", "LA MANA", (double)140.0F);
        addTramo("LA MANA", "QUEVEDO", (double)45.0F);
        addTramo("AMBATO", "GUARANDA", (double)95.0F);
        addTramo("GUARANDA", "BABAHOYO", (double)115.0F);
        addTramo("RIOBAMBA", "PALLATANGA", (double)85.0F);
        addTramo("PALLATANGA", "BUCAY", (double)45.0F);
        addTramo("BUCAY", "EL TRIUNFO", (double)35.0F);
        addTramo("CUENCA", "MOLLETURO", (double)70.0F);
        addTramo("MOLLETURO", "NARANJAL", (double)65.0F);
        addTramo("CUENCA", "GIRON", (double)45.0F);
        addTramo("GIRON", "PASAJE", (double)120.0F);
        addTramo("PASAJE", "MACHALA", (double)25.0F);
        addTramo("LOJA", "BALSAS", (double)160.0F);
        addTramo("BALSAS", "SANTA ROSA", (double)75.0F);
        addTramo("TULCAN", "LAGO AGRIO", (double)190.0F);
        addTramo("QUITO", "PAPALLACTA", (double)70.0F);
        addTramo("PAPALLACTA", "BAEZA", (double)45.0F);
        addTramo("BAEZA", "EL CHACO", (double)25.0F);
        addTramo("EL CHACO", "LAGO AGRIO", (double)120.0F);
        addTramo("BAEZA", "TENA", (double)95.0F);
        addTramo("TENA", "PUYO", (double)80.0F);
        addTramo("PELILEO", "BANOS", (double)25.0F);
        addTramo("BANOS", "PUYO", (double)65.0F);
        addTramo("PUYO", "MACAS", (double)130.0F);
        addTramo("RIOBAMBA", "MACAS", (double)160.0F);
        addTramo("MACAS", "SUCUA", (double)25.0F);
        addTramo("SUCUA", "MENDEZ", (double)45.0F);
        addTramo("MENDEZ", "GUALAQUIZA", (double)115.0F);
        addTramo("GUALAQUIZA", "YANTZAZA", (double)75.0F);
        addTramo("YANTZAZA", "ZAMORA", (double)45.0F);
        addTramo("ZAMORA", "LOJA", (double)65.0F);
        addTramo("LAGO AGRIO", "COCA", (double)85.0F);
        addTramo("COCA", "JOYA DE LOS SACHAS", (double)35.0F);
        addCoordenada("TULCAN", 260, 30);
        addCoordenada("SAN GABRIEL", 250, 50);
        addCoordenada("MIRA", 230, 60);
        addCoordenada("IBARRA", 240, 80);
        addCoordenada("OTAVALO", 235, 100);
        addCoordenada("CAYAMBE", 255, 110);
        addCoordenada("QUITO", 240, 140);
        addCoordenada("MACHACHI", 240, 160);
        addCoordenada("LATACUNGA", 235, 190);
        addCoordenada("SALCEDO", 240, 205);
        addCoordenada("AMBATO", 240, 220);
        addCoordenada("PELILEO", 255, 225);
        addCoordenada("BANOS", 270, 230);
        addCoordenada("RIOBAMBA", 235, 260);
        addCoordenada("GUAMOTE", 230, 290);
        addCoordenada("ALAUSI", 225, 310);
        addCoordenada("CHUNCHI", 225, 330);
        addCoordenada("ZHUD", 220, 350);
        addCoordenada("AZOGUES", 230, 370);
        addCoordenada("CUENCA", 220, 390);
        addCoordenada("ONA", 210, 430);
        addCoordenada("SARAGURO", 205, 450);
        addCoordenada("LOJA", 200, 480);
        addCoordenada("CATAMAYO", 180, 480);
        addCoordenada("MACARA", 160, 520);
        addCoordenada("ZAMORA", 230, 490);
        addCoordenada("YANTZAZA", 250, 470);
        addCoordenada("GUALAQUIZA", 260, 440);
        addCoordenada("SAN LORENZO", 180, 20);
        addCoordenada("ESMERALDAS", 120, 60);
        addCoordenada("ATACAMES", 110, 70);
        addCoordenada("QUININDE", 150, 100);
        addCoordenada("SANTO DOMINGO", 180, 150);
        addCoordenada("SAN MIGUEL DE LOS BANCOS", 200, 130);
        addCoordenada("EL CARMEN", 150, 160);
        addCoordenada("CHONE", 120, 180);
        addCoordenada("PEDERNALES", 80, 150);
        addCoordenada("JAMA", 70, 170);
        addCoordenada("SAN VICENTE", 60, 190);
        addCoordenada("BAHIA DE CARAQUEZ", 60, 200);
        addCoordenada("ROCAFUERTE", 80, 210);
        addCoordenada("PORTOVIEJO", 90, 220);
        addCoordenada("MANTA", 50, 220);
        addCoordenada("JIPIJAPA", 80, 250);
        addCoordenada("QUEVEDO", 150, 210);
        addCoordenada("LA MANA", 180, 200);
        addCoordenada("BABAHOYO", 140, 280);
        addCoordenada("GUARANDA", 190, 250);
        addCoordenada("GUAYAQUIL", 120, 320);
        addCoordenada("DURAN", 135, 320);
        addCoordenada("MILAGRO", 150, 315);
        addCoordenada("DAULE", 120, 300);
        addCoordenada("SAMBORONDON", 130, 310);
        addCoordenada("SANTA ELENA", 40, 320);
        addCoordenada("SALINAS", 30, 320);
        addCoordenada("LA LIBERTAD", 35, 315);
        addCoordenada("EL TRIUNFO", 160, 330);
        addCoordenada("BUCAY", 180, 320);
        addCoordenada("PALLATANGA", 200, 300);
        addCoordenada("LA TRONCAL", 170, 340);
        addCoordenada("NARANJAL", 160, 360);
        addCoordenada("MOLLETURO", 180, 370);
        addCoordenada("MACHALA", 110, 420);
        addCoordenada("PASAJE", 125, 410);
        addCoordenada("SANTA ROSA", 110, 440);
        addCoordenada("HUAQUILLAS", 90, 460);
        addCoordenada("PINAS", 130, 450);
        addCoordenada("BALSAS", 140, 460);
        addCoordenada("GIRON", 190, 410);
        addCoordenada("LAGO AGRIO", 320, 80);
        addCoordenada("EL CHACO", 280, 110);
        addCoordenada("BAEZA", 280, 130);
        addCoordenada("PAPALLACTA", 260, 140);
        addCoordenada("COCA", 340, 120);
        addCoordenada("JOYA DE LOS SACHAS", 350, 110);
        addCoordenada("TENA", 300, 180);
        addCoordenada("ARCHIDONA", 295, 170);
        addCoordenada("PUYO", 300, 230);
        addCoordenada("MERA", 290, 230);
        addCoordenada("MACAS", 280, 320);
        addCoordenada("SUCUA", 275, 340);
        addCoordenada("MENDEZ", 270, 360);
    }

    public static class ResultadoRuta {
        double distanciaTotal;
        List<String> camino;

        public ResultadoRuta(double distanciaTotal, List<String> camino) {
            this.distanciaTotal = distanciaTotal;
            this.camino = camino;
        }
    }

    private class PanelMapa extends JPanel {
        private List<String> rutaActual;

        public PanelMapa() {
            this.setBackground(Color.WHITE);
            this.setBorder(BorderFactory.createTitledBorder("Mapa de Ruta (Esquemático)"));
            this.setPreferredSize(new Dimension(500, 0));
        }

        public void setRuta(List<String> ruta) {
            this.rutaActual = ruta;
            this.repaint();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int panelW = this.getWidth();
            int panelH = this.getHeight();
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE;
            if (!DialogoFormularioRuta.COORDENADAS.isEmpty()) {
                for(Point p : DialogoFormularioRuta.COORDENADAS.values()) {
                    if (p.x < minX) {
                        minX = p.x;
                    }

                    if (p.x > maxX) {
                        maxX = p.x;
                    }

                    if (p.y < minY) {
                        minY = p.y;
                    }

                    if (p.y > maxY) {
                        maxY = p.y;
                    }
                }

                minX -= 20;
                maxX += 20;
                minY -= 20;
                maxY += 20;
            } else {
                minX = 0;
                maxX = 400;
                minY = 0;
                maxY = 600;
            }

            int mapW = maxX - minX;
            int mapH = maxY - minY;
            double scaleX = (double)(panelW - 40) / (double)mapW;
            double scaleY = (double)(panelH - 40) / (double)mapH;
            double scale = Math.min(scaleX, scaleY);
            int dx = (panelW - (int)((double)mapW * scale)) / 2 - (int)((double)minX * scale);
            int dy = (panelH - (int)((double)mapH * scale)) / 2 - (int)((double)minY * scale);
            AffineTransform oldTransform = g2.getTransform();
            g2.translate(dx, dy);
            g2.scale(scale, scale);
            g2.setColor(new Color(220, 220, 220));
            g2.setStroke(new BasicStroke(1.0F));

            for(String c1 : DialogoFormularioRuta.GRAFO_VIAL.keySet()) {
                Point p1 = (Point)DialogoFormularioRuta.COORDENADAS.get(c1);
                if (p1 != null) {
                    for(Object c2 : ((Map)DialogoFormularioRuta.GRAFO_VIAL.get(c1)).keySet()) {
                        Point p2 = (Point)DialogoFormularioRuta.COORDENADAS.get(c2);
                        if (p2 != null) {
                            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                        }
                    }
                }
            }

            if (this.rutaActual != null && this.rutaActual.size() > 1) {
                g2.setColor(Color.BLUE);
                g2.setStroke(new BasicStroke(3.0F));

                for(int i = 0; i < this.rutaActual.size() - 1; ++i) {
                    Point p1 = (Point)DialogoFormularioRuta.COORDENADAS.get(this.rutaActual.get(i));
                    Point p2 = (Point)DialogoFormularioRuta.COORDENADAS.get(this.rutaActual.get(i + 1));
                    if (p1 != null && p2 != null) {
                        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }
            }

            for(Map.Entry<String, Point> entry : DialogoFormularioRuta.COORDENADAS.entrySet()) {
                String ciudad = (String)entry.getKey();
                Point p = (Point)entry.getValue();
                boolean enRuta = this.rutaActual != null && this.rutaActual.contains(ciudad);
                boolean esInicioFin = this.rutaActual != null && (((String)this.rutaActual.get(0)).equals(ciudad) || ((String)this.rutaActual.get(this.rutaActual.size() - 1)).equals(ciudad));
                if (esInicioFin) {
                    g2.setColor(new Color(0, 128, 0));
                    g2.fillOval(p.x - 5, p.y - 5, 10, 10);
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Arial", 1, 11));
                } else if (enRuta) {
                    g2.setColor(Color.RED);
                    g2.fillOval(p.x - 4, p.y - 4, 8, 8);
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Arial", 1, 10));
                } else {
                    g2.setColor(Color.GRAY);
                    g2.fillOval(p.x - 2, p.y - 2, 4, 4);
                    g2.setFont(new Font("Arial", 0, 9));
                }

                g2.drawString(ciudad, p.x + 6, p.y + 4);
            }

            g2.setTransform(oldTransform);
        }
    }
}
