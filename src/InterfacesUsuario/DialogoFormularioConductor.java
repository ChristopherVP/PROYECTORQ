//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import excepciones.PersistenciaException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import objetosNegocio.Conductor;
import objetosNegocio.Empresa;
import persistencia.Conductores;
import persistencia.Empresas;

public class DialogoFormularioConductor extends JDialog {
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtCedula;
    private JTextField txtTelefono;
    private JTextField txtLicencia;
    private JComboBox<String> cmbEmpresa;
    private JComboBox<String> cmbEstado;
    private Conductor conductor;
    private boolean confirmado = false;
    private List<Empresa> empresas;
    private Empresa empresaPreseleccionada;

    public DialogoFormularioConductor(Window padre, String titulo, Conductor c, Empresa empresaPreseleccionada) {
        super(padre, titulo, ModalityType.APPLICATION_MODAL);
        this.conductor = c;
        this.empresaPreseleccionada = empresaPreseleccionada;
        this.cargarEmpresas();
        this.initComponents();
        if (c != null) {
            this.cargarDatos();
        }

    }

    private void cargarEmpresas() {
        try {
            this.empresas = (new Empresas("empresas.dat")).lista();
        } catch (PersistenciaException e) {
            this.empresas = new ArrayList();
            JOptionPane.showMessageDialog(this, "Error al cargar empresas: " + e.getMessage());
        }

    }

    private void initComponents() {
        this.setSize(500, 450);
        this.setLocationRelativeTo(this.getParent());
        this.setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = 2;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("ID Conductor:"), gbc);
        gbc.gridx = 1;
        this.txtId = new JTextField(20);
        this.txtId.setEnabled(false);
        this.txtId.setBackground(Color.LIGHT_GRAY);
        this.txtId.setText("(Se generará automáticamente)");
        panel.add(this.txtId, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Nombre: *"), gbc);
        gbc.gridx = 1;
        this.txtNombre = new JTextField(20);
        panel.add(this.txtNombre, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Cédula: *"), gbc);
        gbc.gridx = 1;
        this.txtCedula = new JTextField(20);
        panel.add(this.txtCedula, gbc);
        this.agregarFiltroNumeros(this.txtCedula, 10);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        this.txtTelefono = new JTextField(20);
        panel.add(this.txtTelefono, gbc);
        this.agregarFiltroNumeros(this.txtTelefono, 10);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Licencia: *"), gbc);
        gbc.gridx = 1;
        this.txtLicencia = new JTextField(20);
        panel.add(this.txtLicencia, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Empresa: *"), gbc);
        gbc.gridx = 1;
        this.cmbEmpresa = new JComboBox<>();
        this.cmbEmpresa.addItem("-- Seleccione Empresa --");

        for(Empresa e : this.empresas) {
            JComboBox var10000 = this.cmbEmpresa;
            String var10001 = e.getIdEmpresa();
            var10000.addItem(var10001 + " - " + e.getNombre());
        }

        panel.add(this.cmbEmpresa, gbc);
        if (this.empresaPreseleccionada != null) {
            for(int i = 0; i < this.cmbEmpresa.getItemCount(); ++i) {
                if (((String)this.cmbEmpresa.getItemAt(i)).startsWith(this.empresaPreseleccionada.getIdEmpresa())) {
                    this.cmbEmpresa.setSelectedIndex(i);
                    this.cmbEmpresa.setEnabled(false);
                    break;
                }
            }
        }

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Estado: *"), gbc);
        gbc.gridx = 1;
        this.cmbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"});
        panel.add(this.cmbEstado, gbc);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        JLabel lblNota = new JLabel("* Campos obligatorios");
        lblNota.setFont(new Font("Arial", 2, 11));
        lblNota.setForeground(Color.GRAY);
        panel.add(lblNota, gbc);
        JPanel botones = new JPanel(new FlowLayout(1, 15, 10));
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setPreferredSize(new Dimension(120, 35));
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnGuardar.addActionListener((ex) -> this.guardar());
        btnCancelar.addActionListener((ex) -> this.dispose());
        botones.add(btnGuardar);
        botones.add(btnCancelar);
        this.add(panel, "Center");
        this.add(botones, "South");
    }

    private void cargarDatos() {
        this.txtId.setText(this.conductor.getIdConductor());
        this.txtNombre.setText(this.conductor.getNombre());
        this.txtCedula.setText(this.conductor.getCedula());
        this.txtTelefono.setText(this.conductor.getTelefono());
        this.txtLicencia.setText(this.conductor.getLicencia());
        this.cmbEstado.setSelectedItem(this.conductor.getEstado());
        if (this.conductor.getEmpresa() != null) {
            for(int i = 0; i < this.cmbEmpresa.getItemCount(); ++i) {
                if (((String)this.cmbEmpresa.getItemAt(i)).startsWith(this.conductor.getEmpresa().getIdEmpresa())) {
                    this.cmbEmpresa.setSelectedIndex(i);
                    break;
                }
            }
        }

    }

    private void guardar() {
        if (!this.txtNombre.getText().trim().isEmpty() && !this.txtCedula.getText().trim().isEmpty() && !this.txtLicencia.getText().trim().isEmpty()) {
            try {
                Conductores conductoresDB = new Conductores("conductores.dat");
                List<Conductor> lista = conductoresDB.lista();
                String nuevoNombre = this.txtNombre.getText().trim();

                for(Conductor c : lista) {
                    if (c.getNombre().equalsIgnoreCase(nuevoNombre) && (this.conductor == null || !c.getIdConductor().equals(this.conductor.getIdConductor()))) {
                        JOptionPane.showMessageDialog(this, "El nombre del conductor ya está registrado.", "Error de Validación", 0);
                        return;
                    }
                }
            } catch (PersistenciaException var6) {
            }

            if (this.txtCedula.getText().trim().length() != 10) {
                JOptionPane.showMessageDialog(this, "La cédula debe tener 10 dígitos", "Error de Validación", 0);
            } else if (this.cmbEmpresa.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar una empresa", "Error de Validación", 0);
            } else {
                if (this.conductor == null) {
                    this.conductor = new Conductor();
                }

                this.conductor.setNombre(this.txtNombre.getText().trim());
                this.conductor.setCedula(this.txtCedula.getText().trim());
                this.conductor.setTelefono(this.txtTelefono.getText().trim());
                this.conductor.setLicencia(this.txtLicencia.getText().trim());
                this.conductor.setEstado((String)this.cmbEstado.getSelectedItem());
                String empresaSeleccionada = (String)this.cmbEmpresa.getSelectedItem();
                String idEmpresa = empresaSeleccionada.split(" - ")[0].trim();
                this.conductor.setEmpresa(new Empresa(idEmpresa));
                this.confirmado = true;
                this.dispose();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Complete todos los campos obligatorios", "Error de Validación", 0);
        }
    }

    private void agregarFiltroNumeros(final JTextField campo, final int maxDigitos) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                this.validar();
            }

            public void removeUpdate(DocumentEvent e) {
                this.validar();
            }

            public void insertUpdate(DocumentEvent e) {
                this.validar();
            }

            public void validar() {
                SwingUtilities.invokeLater(() -> {
                    String text = campo.getText();
                    String filtered = text.replaceAll("[^0-9]", "");
                    if (filtered.length() > maxDigitos) {
                        filtered = filtered.substring(0, maxDigitos);
                    }

                    if (!text.equals(filtered)) {
                        campo.setText(filtered);
                    }

                });
            }
        });
    }

    public Conductor getConductor() {
        return this.conductor;
    }

    public boolean isConfirmado() {
        return this.confirmado;
    }
}
