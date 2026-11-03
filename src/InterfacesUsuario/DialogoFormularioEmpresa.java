//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import excepciones.PersistenciaException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import objetosNegocio.Empresa;
import persistencia.Empresas;

public class DialogoFormularioEmpresa extends JDialog {
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtRuc;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private Empresa empresa;
    private boolean confirmado = false;

    public DialogoFormularioEmpresa(JFrame padre, String titulo, Empresa e) {
        super(padre, titulo, true);
        this.empresa = e;
        this.initComponents();
        if (e != null) {
            this.cargar();
        }

    }

    private void initComponents() {
        this.setSize(500, 400);
        this.setLocationRelativeTo(this.getParent());
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = 2;
        g.insets = new Insets(8, 8, 8, 8);
        g.gridx = 0;
        g.gridy = 0;
        p.add(new JLabel("ID Empresa:"), g);
        g.gridx = 1;
        this.txtId = new JTextField(20);
        this.txtId.setEnabled(false);
        this.txtId.setBackground(Color.LIGHT_GRAY);
        this.txtId.setText("(Se generará automáticamente)");
        p.add(this.txtId, g);
        g.gridx = 0;
        g.gridy = 1;
        p.add(new JLabel("Nombre: *"), g);
        g.gridx = 1;
        this.txtNombre = new JTextField(20);
        p.add(this.txtNombre, g);
        g.gridx = 0;
        g.gridy = 2;
        p.add(new JLabel("RUC: *"), g);
        g.gridx = 1;
        this.txtRuc = new JTextField(20);
        this.agregarFiltroRuc(this.txtRuc);
        p.add(this.txtRuc, g);
        g.gridx = 0;
        g.gridy = 3;
        p.add(new JLabel("Dirección: *"), g);
        g.gridx = 1;
        this.txtDireccion = new JTextField(20);
        p.add(this.txtDireccion, g);
        g.gridx = 0;
        g.gridy = 4;
        p.add(new JLabel("Teléfono:"), g);
        g.gridx = 1;
        this.txtTelefono = new JTextField(20);
        this.agregarFiltroTelefono(this.txtTelefono);
        p.add(this.txtTelefono, g);
        g.gridx = 0;
        g.gridy = 5;
        p.add(new JLabel("Email: *"), g);
        g.gridx = 1;
        this.txtEmail = new JTextField(20);
        this.agregarFiltroEmail(this.txtEmail);
        p.add(this.txtEmail, g);
        g.gridx = 0;
        g.gridy = 6;
        g.gridwidth = 2;
        JLabel lblNota = new JLabel("* Campos obligatorios");
        lblNota.setFont(new Font("Arial", 2, 11));
        lblNota.setForeground(Color.GRAY);
        p.add(lblNota, g);
        JPanel pBtn = new JPanel(new FlowLayout(1, 15, 10));
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setPreferredSize(new Dimension(120, 35));
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnGuardar.addActionListener((e) -> this.guardar());
        btnCancelar.addActionListener((e) -> this.dispose());
        pBtn.add(btnGuardar);
        pBtn.add(btnCancelar);
        this.add(p, "Center");
        this.add(pBtn, "South");
    }

    private void cargar() {
        this.txtId.setText(this.empresa.getIdEmpresa());
        this.txtNombre.setText(this.empresa.getNombre());
        this.txtRuc.setText(this.empresa.getRuc());
        this.txtDireccion.setText(this.empresa.getDireccion());
        this.txtTelefono.setText(this.empresa.getTelefono());
        this.txtEmail.setText(this.empresa.getEmail());
    }

    private void guardar() {
        if (!this.txtNombre.getText().trim().isEmpty() && !this.txtRuc.getText().trim().isEmpty() && !this.txtDireccion.getText().trim().isEmpty() && !this.txtEmail.getText().trim().isEmpty()) {
            if (this.txtDireccion.getText().trim().length() < 5) {
                JOptionPane.showMessageDialog(this, "La dirección es muy corta, debe tener al menos 5 caracteres", "Error de Validación", 0);
            } else if (!this.validarRuc(this.txtRuc.getText().trim())) {
                JOptionPane.showMessageDialog(this, "El RUC es inválido (debe tener 13 dígitos y terminar en 001)", "Error de Validación", 0);
            } else {
                String email = this.txtEmail.getText().trim();
                if (email.contains(" ")) {
                    JOptionPane.showMessageDialog(this, "El email no puede contener espacios", "Error de Validación", 0);
                } else if (!email.equals(email.toLowerCase())) {
                    JOptionPane.showMessageDialog(this, "El email no puede contener mayúsculas", "Error de Validación", 0);
                } else if (!email.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")) {
                    JOptionPane.showMessageDialog(this, "El formato del email no es válido", "Error de Validación", 0);
                } else {
                    String telefono = this.txtTelefono.getText().trim();
                    if (!telefono.isEmpty() && telefono.length() != 10) {
                        JOptionPane.showMessageDialog(this, "El teléfono debe tener exactamente 10 dígitos", "Error de Validación", 0);
                    } else {
                        try {
                            Empresas empresasDB = new Empresas("empresas.dat");
                            List<Empresa> lista = empresasDB.lista();
                            String nuevoNombre = this.txtNombre.getText().trim();
                            String nuevoRuc = this.txtRuc.getText().trim();
                            String nuevoTelefono = this.txtTelefono.getText().trim();

                            for(Empresa e : lista) {
                                if (this.empresa == null || !e.getIdEmpresa().equals(this.empresa.getIdEmpresa())) {
                                    if (e.getNombre().equalsIgnoreCase(nuevoNombre)) {
                                        JOptionPane.showMessageDialog(this, "El nombre de la empresa '" + nuevoNombre + "' ya existe.", "Error de Validación", 0);
                                        return;
                                    }

                                    if (e.getRuc() != null && e.getRuc().equals(nuevoRuc)) {
                                        JOptionPane.showMessageDialog(this, "El RUC '" + nuevoRuc + "' ya está registrado en otra empresa.", "Error de Validación", 0);
                                        return;
                                    }

                                    if (!nuevoTelefono.isEmpty() && e.getTelefono() != null && e.getTelefono().equals(nuevoTelefono)) {
                                        JOptionPane.showMessageDialog(this, "El teléfono '" + nuevoTelefono + "' ya está registrado en otra empresa.", "Error de Validación", 0);
                                        return;
                                    }

                                    if (e.getEmail().equalsIgnoreCase(email)) {
                                        JOptionPane.showMessageDialog(this, "El email '" + email + "' ya está registrado en otra empresa.", "Error de Validación", 0);
                                        return;
                                    }
                                }
                            }
                        } catch (PersistenciaException ex) {
                            JOptionPane.showMessageDialog(this, "Error al verificar duplicados: " + ex.getMessage());
                            return;
                        }

                        if (this.empresa == null) {
                            this.empresa = new Empresa();
                        }

                        this.empresa.setNombre(this.txtNombre.getText().trim());
                        this.empresa.setRuc(this.txtRuc.getText().trim());
                        this.empresa.setDireccion(this.txtDireccion.getText().trim());
                        this.empresa.setTelefono(telefono);
                        this.empresa.setEmail(email);
                        this.confirmado = true;
                        this.dispose();
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Complete todos los campos obligatorios (Nombre, RUC, Dirección, Email)", "Error de Validación", 0);
        }
    }

    private boolean validarRuc(String ruc) {
        return ruc != null && ruc.length() == 13 && ruc.matches("\\d+") && ruc.endsWith("001");
    }

    private void agregarFiltroRuc(final JTextField campo) {
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
                    if (filtered.length() > 13) {
                        filtered = filtered.substring(0, 13);
                    }

                    if (!text.equals(filtered)) {
                        campo.setText(filtered);
                    }

                });
            }
        });
    }

    private void agregarFiltroTelefono(final JTextField campo) {
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
                    if (filtered.length() > 10) {
                        filtered = filtered.substring(0, 10);
                    }

                    if (!text.equals(filtered)) {
                        campo.setText(filtered);
                    }

                });
            }
        });
    }

    private void agregarFiltroEmail(final JTextField campo) {
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
                    String filtered = text.toLowerCase().replaceAll("\\s", "");
                    if (!text.equals(filtered)) {
                        int pos = campo.getCaretPosition();
                        campo.setText(filtered);

                        try {
                            campo.setCaretPosition(Math.min(pos, filtered.length()));
                        } catch (IllegalArgumentException var5) {
                            campo.setCaretPosition(filtered.length());
                        }
                    }

                });
            }
        });
    }

    public Empresa getEmpresa() {
        return this.empresa;
    }

    public boolean isConfirmado() {
        return this.confirmado;
    }
}
