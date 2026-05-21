package papeleria.gui;

import papeleria.modelo.*;
import papeleria.util.UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FacturaFrame extends JFrame implements Printable {

    private final Venta venta;
    private JTextArea areaFactura;
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public FacturaFrame(Venta venta) {
        this.venta = venta;
        construirUI();
        generarTextoFactura();
    }

    private void construirUI() {
        setTitle("Factura Electrónica — " + venta.getNumeroFactura());
        setSize(520, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UI.GRAY_100);

        // ── Cabecera ──────────────────────────────────────────────
        JPanel header = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UI.INK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(UI.GOLD);
                g2.fillRect(0, getHeight() - 4, getWidth(), 4);
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(520, 72));

        JLabel lblTitulo = UI.label("FACTURA ELECTRÓNICA", new Font("Segoe UI", Font.BOLD, 16), UI.GOLD);
        lblTitulo.setBounds(20, 14, 320, 22);
        JLabel lblNum = UI.label(venta.getNumeroFactura(), UI.F_MONO, UI.CREAM);
        lblNum.setBounds(20, 38, 320, 18);

        JLabel lblEmpresa = UI.label("Papelería El Punto", UI.F_SUBHEAD, UI.CREAM);
        lblEmpresa.setBounds(320, 14, 180, 20);
        lblEmpresa.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel lblNit = UI.label("NIT: 900.123.456-7", UI.F_SMALL, UI.GRAY_400);
        lblNit.setBounds(320, 36, 180, 16);
        lblNit.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel lblDir = UI.label("Cartagena, Colombia", UI.F_SMALL, UI.GRAY_400);
        lblDir.setBounds(320, 52, 180, 14);
        lblDir.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(lblTitulo); header.add(lblNum);
        header.add(lblEmpresa); header.add(lblNit); header.add(lblDir);

        // ── Área de texto con la factura ──────────────────────────
        areaFactura = new JTextArea();
        areaFactura.setFont(UI.F_MONO);
        areaFactura.setEditable(false);
        areaFactura.setBackground(Color.WHITE);
        areaFactura.setForeground(UI.INK);
        areaFactura.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        areaFactura.setLineWrap(false);

        JScrollPane scroll = new JScrollPane(areaFactura);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        // ── Barra de botones ──────────────────────────────────────
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        botones.setBackground(UI.CREAM);
        botones.setBorder(new MatteBorder(1, 0, 0, 0, UI.WARM));

        JButton btnImprimir = UI.botonPrimario("🖨  Imprimir");
        JButton btnCerrar   = UI.botonGris("Cerrar");

        btnImprimir.addActionListener(e -> imprimir());
        btnCerrar.addActionListener(e -> dispose());

        botones.add(btnImprimir);
        botones.add(btnCerrar);

        root.add(header, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(botones, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void generarTextoFactura() {
        StringBuilder sb = new StringBuilder();
        String sep  = "─".repeat(54);
        String sep2 = "═".repeat(54);

        sb.append(sep2).append("\n");
        sb.append("         PAPELERÍA EL PUNTO\n");
        sb.append("      Cartagena, Bolívar — Colombia\n");
        sb.append("       Tel: (605) 123-4567\n");
        sb.append(sep2).append("\n\n");

        sb.append(String.format("%-22s %s\n", "Factura N°:", venta.getNumeroFactura()));
        sb.append(String.format("%-22s %s\n", "Fecha:", venta.getFecha().format(FMT)));
        sb.append(String.format("%-22s %s\n", "Vendedor:", venta.getUsuarioVendedor()));
        sb.append(String.format("%-22s %s\n", "Cliente:", venta.getUsuarioCliente()));
        sb.append(String.format("%-22s %s\n", "Tipo:", venta.getTipoVenta() == Venta.TipoVenta.DOMICILIO
                ? "Domicilio" : "Mostrador"));

        if (venta.getTipoVenta() == Venta.TipoVenta.DOMICILIO) {
            sb.append(String.format("%-22s %s\n", "Dirección:", venta.getDireccionDomicilio()));
            sb.append(String.format("%-22s %s\n", "Teléfono:", venta.getTelefonoDomicilio()));
        }

        sb.append("\n").append(sep).append("\n");
        sb.append(String.format("%-28s %6s %10s\n", "PRODUCTO", "CANT", "TOTAL"));
        sb.append(sep).append("\n");

        for (DetalleVenta d : venta.getDetalles()) {
            String nombre = d.getNombreProducto();
            if (nombre.length() > 26) nombre = nombre.substring(0, 23) + "...";
            sb.append(String.format("%-28s %6d %10s\n",
                    nombre, d.getCantidad(), UI.fmt(d.getSubtotal())));
            sb.append(String.format("  %-20s %s c/u\n", "",
                    UI.fmt(d.getPrecioUnitario())));
        }

        sb.append(sep).append("\n");
        sb.append(String.format("%-38s %10s\n", "SUBTOTAL:", UI.fmt(venta.getSubtotal())));

        if (venta.getTipoVenta() == Venta.TipoVenta.DOMICILIO) {
            sb.append(String.format("%-38s %10s\n", "COSTO DOMICILIO:", UI.fmt(venta.getCostoDomicilio())));
        }

        sb.append(sep2).append("\n");
        sb.append(String.format("%-38s %10s\n", "TOTAL A PAGAR:", UI.fmt(venta.getTotal())));
        sb.append(sep2).append("\n");
        sb.append(String.format("%-38s %10s\n", "EFECTIVO RECIBIDO:", UI.fmt(venta.getEfectivoRecibido())));
        sb.append(String.format("%-38s %10s\n", "CAMBIO:", UI.fmt(venta.getCambio())));
        sb.append(sep).append("\n\n");

        sb.append("  Resolución DIAN N° 18760000001 de 2024\n");
        sb.append("  Rango autorizado: FE-0001 al FE-9999\n");
        sb.append("  Vigencia: 01/01/2024 — 31/12/2026\n\n");
        sb.append("  ¡Gracias por su compra!\n");
        sb.append(sep2).append("\n");

        areaFactura.setText(sb.toString());
        areaFactura.setCaretPosition(0);
    }

    private void imprimir() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException ex) {
                UI.error(this, "Error al imprimir: " + ex.getMessage());
            }
        }
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > 0) return NO_SUCH_PAGE;
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(pf.getImageableX(), pf.getImageableY());
        g2.setFont(new Font("Courier New", Font.PLAIN, 9));
        g2.setColor(Color.BLACK);
        String[] lineas = areaFactura.getText().split("\n");
        int y = 12;
        for (String linea : lineas) {
            g2.drawString(linea, 0, y);
            y += 12;
            if (y > pf.getImageableHeight()) break;
        }
        return PAGE_EXISTS;
    }
}
