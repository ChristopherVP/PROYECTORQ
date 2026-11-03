//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class PantallaBienvenida extends JFrame {
    private JButton btnIngresar;

    public PantallaBienvenida() {
        this.initComponents();
    }

    private void initComponents() {
        this.setTitle("Sistema de Transporte Interprovincial");
        this.setDefaultCloseOperation(3);
        this.setExtendedState(6);
        this.setLocationRelativeTo((Component)null);
        JPanel panelFondo = new JPanel(new GridBagLayout());
        panelFondo.setBackground(new Color(245, 245, 245));
        JPanel panelCard = new JPanel();
        panelCard.setLayout(new BoxLayout(panelCard, 1));
        panelCard.setBackground(Color.WHITE);
        panelCard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1), BorderFactory.createEmptyBorder(50, 80, 50, 80)));
        JLabel lblTitulo1 = new JLabel("BIENVENIDO AL SISTEMA DE");
        lblTitulo1.setFont(new Font("Segoe UI", 1, 28));
        lblTitulo1.setForeground(new Color(44, 62, 80));
        lblTitulo1.setAlignmentX(0.5F);
        JLabel lblTitulo2 = new JLabel("TRANSPORTE INTERPROVINCIAL");
        lblTitulo2.setFont(new Font("Segoe UI", 1, 28));
        lblTitulo2.setForeground(new Color(44, 62, 80));
        lblTitulo2.setAlignmentX(0.5F);
        panelCard.add(lblTitulo1);
        panelCard.add(Box.createRigidArea(new Dimension(0, 10)));
        panelCard.add(lblTitulo2);
        panelCard.add(Box.createRigidArea(new Dimension(0, 40)));
        this.btnIngresar = new JButton("Ingresar al Sistema");
        this.btnIngresar.setPreferredSize(new Dimension(220, 50));
        this.btnIngresar.setMaximumSize(new Dimension(220, 50));
        this.btnIngresar.setFont(new Font("Segoe UI", 1, 16));
        this.btnIngresar.setBackground(new Color(41, 128, 185));
        this.btnIngresar.setForeground(Color.WHITE);
        this.btnIngresar.setFocusPainted(false);
        this.btnIngresar.setBorderPainted(false);
        this.btnIngresar.setOpaque(true);
        this.btnIngresar.setCursor(new Cursor(12));
        this.btnIngresar.setAlignmentX(0.5F);
        this.btnIngresar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PantallaBienvenida.this.abrirInicioSesion();
            }
        });
        panelCard.add(this.btnIngresar);
        panelCard.add(Box.createRigidArea(new Dimension(0, 40)));
        JLabel lblVersion = new JLabel("Sistema de Gestión de Buses v2.0");
        lblVersion.setFont(new Font("Segoe UI", 0, 12));
        lblVersion.setForeground(Color.GRAY);
        lblVersion.setAlignmentX(0.5F);
        panelCard.add(lblVersion);
        panelFondo.add(panelCard);
        this.add(panelFondo);
    }

    private void abrirInicioSesion() {
        this.setVisible(false);
        PantallaInicioSesion inicioSesion = new PantallaInicioSesion(this);
        inicioSesion.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                (new PantallaBienvenida()).setVisible(true);
            }
        });
    }
}
