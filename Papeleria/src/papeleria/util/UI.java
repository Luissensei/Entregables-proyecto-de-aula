package papeleria.util;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;
import java.text.NumberFormat;
import java.util.Locale;

public class UI {
    // ── Paleta de colores ─────────────────────────────────────────
    public static final Color INK        = new Color(26, 26, 46);
    public static final Color INK_SOFT   = new Color(45, 45, 68);
    public static final Color CREAM      = new Color(245, 240, 232);
    public static final Color WARM       = new Color(232, 220, 200);
    public static final Color GOLD       = new Color(201, 168, 76);
    public static final Color GOLD_LIGHT = new Color(232, 201, 106);
    public static final Color SAGE       = new Color(74, 124, 89);
    public static final Color SAGE_LIGHT = new Color(106, 173, 126);
    public static final Color SKY        = new Color(58, 123, 213);
    public static final Color SKY_LIGHT  = new Color(90, 155, 245);
    public static final Color RUST       = new Color(181, 69, 27);
    public static final Color DANGER     = new Color(229, 62, 62);
    public static final Color DANGER_BG  = new Color(254, 178, 178);
    public static final Color SUCCESS    = new Color(56, 161, 105);
    public static final Color SUCCESS_BG = new Color(198, 246, 213);
    public static final Color WARNING    = new Color(214, 158, 46);
    public static final Color WARNING_BG = new Color(254, 235, 200);
    public static final Color WHITE      = Color.WHITE;
    public static final Color GRAY_100   = new Color(247, 250, 252);
    public static final Color GRAY_200   = new Color(237, 242, 247);
    public static final Color GRAY_400   = new Color(160, 174, 192);
    public static final Color GRAY_600   = new Color(74, 85, 104);

    // ── Fuentes ───────────────────────────────────────────────────
    public static final Font F_TITLE    = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font F_HEADING  = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font F_SUBHEAD  = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font F_BODY     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font F_SMALL    = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font F_MONO     = new Font("Consolas", Font.PLAIN, 12);
    public static final Font F_LABEL    = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font F_BIG_NUM  = new Font("Segoe UI", Font.BOLD, 28);

    // ── Formato moneda COP ────────────────────────────────────────
    private static final NumberFormat NF = NumberFormat.getInstance(new Locale("es","CO"));
    static { NF.setMaximumFractionDigits(0); }

    public static String fmt(double v) { return "$" + NF.format(v); }

    // ── Configuración global L&F ──────────────────────────────────
    public static void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        UIManager.put("Button.font", F_BODY);
        UIManager.put("Label.font", F_BODY);
        UIManager.put("TextField.font", F_BODY);
        UIManager.put("TextArea.font", F_BODY);
        UIManager.put("ComboBox.font", F_BODY);
        UIManager.put("Table.font", F_BODY);
        UIManager.put("TableHeader.font", F_SUBHEAD);
        UIManager.put("OptionPane.messageFont", F_BODY);
        UIManager.put("OptionPane.buttonFont", F_BODY);
    }

    // ── Botones ───────────────────────────────────────────────────
    public static JButton boton(String texto, Color fondo, Color texto2) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed())
                    g2.setColor(fondo.darker());
                else if (getModel().isRollover())
                    g2.setColor(fondo.brighter());
                else
                    g2.setColor(fondo);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(texto2);
        btn.setFont(F_SUBHEAD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 20, 38));
        return btn;
    }

    public static JButton botonPrimario(String texto) { return boton(texto, INK, WHITE); }
    public static JButton botonExito(String texto)    { return boton(texto, SAGE, WHITE); }
    public static JButton botonPeligro(String texto)  { return boton(texto, DANGER, WHITE); }
    public static JButton botonDorado(String texto)   { return boton(texto, GOLD, INK); }
    public static JButton botonInfo(String texto)     { return boton(texto, SKY, WHITE); }
    public static JButton botonGris(String texto)     { return boton(texto, GRAY_400, INK); }

    // ── Campos de texto ───────────────────────────────────────────
    public static JTextField campo(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(F_BODY);
        tf.setPreferredSize(new Dimension(200, 36));
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(GRAY_200, 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        tf.setBackground(GRAY_100);
        return tf;
    }

    public static JPasswordField campoPassword() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(F_BODY);
        pf.setPreferredSize(new Dimension(200, 36));
        pf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(GRAY_200, 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        pf.setBackground(GRAY_100);
        return pf;
    }

    // ── Etiquetas ─────────────────────────────────────────────────
    public static JLabel label(String texto, Font fuente, Color color) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(fuente);
        lbl.setForeground(color);
        return lbl;
    }

    // ── Tarjeta estadística ───────────────────────────────────────
    public static JPanel tarjetaStat(String titulo, String valor, Color colorAcento) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(colorAcento);
                g2.fillRect(0, getHeight() - 4, getWidth(), 4);
                g2.setColor(new Color(colorAcento.getRed(), colorAcento.getGreen(),
                        colorAcento.getBlue(), 18));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(F_SMALL);
        lblTitulo.setForeground(GRAY_600);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(F_BIG_NUM);
        lblValor.setForeground(INK);
        lblValor.putClientProperty("__valor", true);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        return card;
    }

    public static void actualizarTarjetaStat(JPanel card, String nuevoValor) {
        for (Component c : card.getComponents()) {
            if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                if (Boolean.TRUE.equals(lbl.getClientProperty("__valor"))) {
                    lbl.setText(nuevoValor);
                    break;
                }
            }
        }
    }

    // ── Tabla estilizada ──────────────────────────────────────────
    public static void estilizarTabla(JTable tabla) {
        tabla.setFont(F_BODY);
        tabla.setRowHeight(34);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(new Color(INK.getRed(), INK.getGreen(), INK.getBlue(), 30));
        tabla.setSelectionForeground(INK);
        tabla.setBackground(WHITE);
        tabla.getTableHeader().setFont(F_LABEL);
        tabla.getTableHeader().setBackground(CREAM);
        tabla.getTableHeader().setForeground(GRAY_600);
        tabla.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, WARM));
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? WHITE : GRAY_100);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return c;
            }
        });
    }

    // ── Panel con sombra ──────────────────────────────────────────
    public static JPanel panelTarjeta() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fill(new RoundRectangle2D.Float(2, 2, getWidth()-4, getHeight()-4, 14, 14));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    // ── Separator decorativo ──────────────────────────────────────
    public static JSeparator separador() {
        JSeparator sep = new JSeparator();
        sep.setForeground(WARM);
        return sep;
    }

    // ── Diálogo de mensaje ────────────────────────────────────────
    public static void exito(Component padre, String msg) {
        JOptionPane.showMessageDialog(padre, msg, "✓ Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
    public static void error(Component padre, String msg) {
        JOptionPane.showMessageDialog(padre, msg, "✗ Error", JOptionPane.ERROR_MESSAGE);
    }
    public static boolean confirmar(Component padre, String msg) {
        return JOptionPane.showConfirmDialog(padre, msg, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    public static String pedir(Component padre, String msg) {
        return JOptionPane.showInputDialog(padre, msg);
    }
}
