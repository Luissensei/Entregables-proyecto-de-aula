package papeleria.gui;

import papeleria.modelo.Usuario;
import papeleria.persistencia.Persistencia;
import papeleria.util.UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;

public class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRol;
    private List<Usuario> usuarios;

    public LoginFrame() {
        usuarios = Persistencia.cargarUsuarios();
        UI.configurarLookAndFeel();
        construirUI();
    }

    private void construirUI() {
        setTitle("Papeleria MAILETH — Iniciar sesion");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel raíz con fondo decorativo
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo degradado oscuro
                GradientPaint gp = new GradientPaint(0, 0, UI.INK, getWidth(), getHeight(), UI.INK_SOFT);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Círculos decorativos de fondo
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.08f));
                g2.setColor(UI.GOLD);
                g2.fill(new Ellipse2D.Float(-80, -80, 380, 380));
                g2.fill(new Ellipse2D.Float(getWidth() - 200, getHeight() - 200, 350, 350));

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
                g2.setColor(UI.SAGE);
                g2.fill(new Ellipse2D.Float(100, getHeight() - 150, 250, 250));

                // Patrón de puntos
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));
                g2.setColor(UI.GOLD);
                for (int x = 0; x < getWidth(); x += 28) {
                    for (int y = 0; y < getHeight(); y += 28) {
                        g2.fill(new Ellipse2D.Float(x, y, 2, 2));
                    }
                }
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
        };
        setContentPane(root);

        // ── Panel izquierdo: branding ──────────────────────────────
        JPanel izquierdo = new JPanel(null);
        izquierdo.setOpaque(false);
        izquierdo.setPreferredSize(new Dimension(420, 560));

        // Logo hexágono decorativo
        JPanel logo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2, r = 46;
                int[] xs = new int[6], ys = new int[6];
                for (int i = 0; i < 6; i++) {
                    xs[i] = (int)(cx + r * Math.cos(Math.PI / 6 + i * Math.PI / 3));
                    ys[i] = (int)(cy + r * Math.sin(Math.PI / 6 + i * Math.PI / 3));
                }
                g2.setColor(UI.GOLD);
                g2.fillPolygon(xs, ys, 6);
                g2.setColor(UI.GOLD_LIGHT);
                g2.setStroke(new BasicStroke(2f));
                r = 52;
                for (int i = 0; i < 6; i++) {
                    xs[i] = (int)(cx + r * Math.cos(Math.PI / 6 + i * Math.PI / 3));
                    ys[i] = (int)(cy + r * Math.sin(Math.PI / 6 + i * Math.PI / 3));
                }
                g2.drawPolygon(xs, ys, 6);
                // Letra P
                g2.setColor(UI.INK);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("P", cx - fm.stringWidth("P") / 2, cy + fm.getAscent() / 2 - 2);
                g2.dispose();
            }
        };
        logo.setOpaque(false);
        logo.setBounds(150, 80, 120, 120);

        JLabel lblNombre = UI.label("MAILETH", new Font("Segoe UI", Font.BOLD, 38), UI.CREAM);
        lblNombre.setBounds(100, 210, 220, 50);
        lblNombre.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblSub = UI.label("Papeleria & Más", UI.F_BODY, UI.GOLD);
        lblSub.setBounds(100, 258, 220, 24);
        lblSub.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblDesc = UI.label("<html><center>Sistema integrado de ventas,<br>inventario y administracion</center></html>",
                UI.F_SMALL, new Color(245, 240, 232, 160));
        lblDesc.setBounds(60, 295, 300, 50);
        lblDesc.setHorizontalAlignment(SwingConstants.CENTER);

        // Credenciales demo
        JPanel demo = crearPanelDemo();
        demo.setBounds(40, 370, 340, 130);

        izquierdo.add(logo);
        izquierdo.add(lblNombre);
        izquierdo.add(lblSub);
        izquierdo.add(lblDesc);
        izquierdo.add(demo);

        // ── Panel derecho: formulario ──────────────────────────────
        JPanel derecho = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 24, 24));
                g2.dispose();
            }
        };
        derecho.setOpaque(false);
        derecho.setPreferredSize(new Dimension(480, 560));

        JLabel lblBienvenido = UI.label("Bienvenido", new Font("Segoe UI", Font.BOLD, 26), UI.INK);
        lblBienvenido.setBounds(60, 60, 360, 36);

        JLabel lblInstr = UI.label("Ingresa tus credenciales para continuar", UI.F_BODY, UI.GRAY_600);
        lblInstr.setBounds(60, 98, 360, 22);

        // Selector de rol
        JLabel lblRolTit = UI.label("ROL", UI.F_LABEL, UI.GRAY_400);
        lblRolTit.setBounds(60, 144, 200, 18);

        cmbRol = new JComboBox<>(new String[]{"Administrador", "Vendedor", "Cliente"});
        cmbRol.setFont(UI.F_BODY);
        cmbRol.setBounds(60, 165, 360, 38);
        cmbRol.setBackground(UI.GRAY_100);
        cmbRol.setBorder(new LineBorder(UI.GRAY_200, 1, true));

        // Usuario
        JLabel lblUsuTit = UI.label("USUARIO", UI.F_LABEL, UI.GRAY_400);
        lblUsuTit.setBounds(60, 224, 200, 18);
        txtUsuario = UI.campo("");
        txtUsuario.setBounds(60, 244, 360, 38);

        // Password
        JLabel lblPwdTit = UI.label("CONTRASEÑA", UI.F_LABEL, UI.GRAY_400);
        lblPwdTit.setBounds(60, 302, 200, 18);
        txtPassword = UI.campoPassword();
        txtPassword.setBounds(60, 322, 360, 38);

        // Botón ingresar
        JButton btnLogin = UI.botonPrimario("Ingresar al sistema →");
        btnLogin.setBounds(60, 386, 360, 46);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));

        // Mensaje de error
        JLabel lblError = UI.label("", UI.F_SMALL, UI.DANGER);
        lblError.setBounds(60, 440, 360, 20);
        lblError.setHorizontalAlignment(SwingConstants.CENTER);

        btnLogin.addActionListener(e -> autenticar(lblError));
        txtPassword.addActionListener(e -> autenticar(lblError));

        // Línea decorativa
        JPanel lineaDeco = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(UI.GOLD);
                g.fillRect(0, 0, 40, 3);
            }
        };
        lineaDeco.setOpaque(false);
        lineaDeco.setBounds(60, 50, 40, 3);

        derecho.add(lineaDeco);
        derecho.add(lblBienvenido);
        derecho.add(lblInstr);
        derecho.add(lblRolTit);
        derecho.add(cmbRol);
        derecho.add(lblUsuTit);
        derecho.add(txtUsuario);
        derecho.add(lblPwdTit);
        derecho.add(txtPassword);
        derecho.add(btnLogin);
        derecho.add(lblError);

        root.add(izquierdo, BorderLayout.WEST);
        root.add(derecho, BorderLayout.CENTER);
    }

    private JPanel crearPanelDemo() {
        JPanel p = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 15));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(new Color(201, 168, 76, 60));
                g2.setStroke(new BasicStroke(0.8f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
            }
        };
        p.setOpaque(false);

        JLabel tit = UI.label("CREDENCIALES DE PRUEBA", UI.F_LABEL, new Color(201, 168, 76, 200));
        tit.setBounds(12, 8, 300, 16);

        String[][] creds = {
            {"Admin", "admin", "admin123"},
            {"Vendedor", "vendedor1", "vend123"},
            {"Cliente", "cliente1", "cli123"}
        };
        Color[] colores = {UI.GOLD, UI.SAGE_LIGHT, UI.SKY_LIGHT};

        for (int i = 0; i < creds.length; i++) {
            final String usr = creds[i][1], pwd = creds[i][2];
            final int rol = i;
            JPanel fila = new JPanel(null) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 20));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                    g2.dispose();
                }
            };
            fila.setOpaque(false);
            fila.setBounds(8, 28 + i * 30, 320, 24);
            fila.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel badge = new JLabel(creds[i][0]);
            badge.setFont(UI.F_LABEL);
            badge.setForeground(colores[i]);
            badge.setBounds(4, 4, 70, 16);

            JLabel info = new JLabel(usr + " / " + pwd);
            info.setFont(UI.F_MONO);
            info.setForeground(new Color(245, 240, 232, 160));
            info.setBounds(80, 4, 200, 16);

            fila.add(badge);
            fila.add(info);
            fila.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    txtUsuario.setText(usr);
                    txtPassword.setText(pwd);
                    cmbRol.setSelectedIndex(rol);
                }
            });
            p.add(fila);
        }
        return p;
    }

    private void autenticar(JLabel lblError) {
        String usr = txtUsuario.getText().trim();
        String pwd = new String(txtPassword.getPassword()).trim();
        int rolIdx = cmbRol.getSelectedIndex();

        if (usr.isEmpty() || pwd.isEmpty()) {
            lblError.setText("Completa todos los campos");
            return;
        }

        Usuario.Rol rolEsperado = switch (rolIdx) {
            case 0 -> Usuario.Rol.ADMIN;
            case 1 -> Usuario.Rol.VENDEDOR;
            default -> Usuario.Rol.CLIENTE;
        };

        for (Usuario u : usuarios) {
            if (u.getUsuario().equals(usr) && u.verificarPassword(pwd)
                    && u.getRol() == rolEsperado && u.isActivo()) {
                abrirPanel(u);
                return;
            }
        }
        lblError.setText("Usuario, contraseña o rol incorrectos");
        txtPassword.setText("");
    }

    private void abrirPanel(Usuario usuario) {
        dispose();
        switch (usuario.getRol()) {
            case ADMIN    -> new AdminFrame(usuario).setVisible(true);
            case VENDEDOR -> new VendedorFrame(usuario).setVisible(true);
            case CLIENTE  -> new ClienteFrame(usuario).setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
