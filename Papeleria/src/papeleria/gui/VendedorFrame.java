package papeleria.gui;

import papeleria.modelo.*;
import papeleria.persistencia.Persistencia;
import papeleria.util.UI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class VendedorFrame extends JFrame {

    private final Usuario vendedor;
    private List<Producto> productos;
    private List<Venta> ventas;

    // Tabla de inventario
    private DefaultTableModel modeloInventario;
    private JTable tablaInventario;
    private JTextField txtBuscar;

    // Carrito
    private DefaultTableModel modeloCarrito;
    private JTable tablaCarrito;

    // Totales
    private JLabel lblSubtotal, lblTotal, lblCambio;
    private JTextField txtEfectivo;

    // Tipo de venta
    private JRadioButton rbMostrador, rbDomicilio;
    private JPanel panelDomicilio;
    private JTextField txtDireccion, txtTelefono;

    // Venta actual
    private Venta ventaActual;

    public VendedorFrame(Usuario vendedor) {
        this.vendedor = vendedor;
        this.productos = Persistencia.cargarProductos();
        this.ventas    = Persistencia.cargarVentas();
        iniciarVenta();
        construirUI();
    }

    private void iniciarVenta() {
        ventaActual = new Venta(vendedor.getUsuario(), "MOSTRADOR", Venta.TipoVenta.MOSTRADOR);
    }

    private void construirUI() {
        setTitle("Papelería El Punto — Vendedor: " + vendedor.getNombre());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UI.GRAY_100);

        // ── Barra superior ────────────────────────────────────────
        root.add(crearBarra(), BorderLayout.NORTH);

        // ── Cuerpo principal ──────────────────────────────────────
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                crearPanelProductos(), crearPanelCarrito());
        split.setDividerLocation(620);
        split.setResizeWeight(0.58);
        split.setBorder(null);
        split.setDividerSize(6);
        root.add(split, BorderLayout.CENTER);

        setContentPane(root);
    }

    // ── Barra superior ────────────────────────────────────────────
    private JPanel crearBarra() {
        JPanel barra = new JPanel(null);
        barra.setBackground(UI.INK);
        barra.setPreferredSize(new Dimension(1100, 56));

        JLabel lblApp = UI.label("El Punto", new Font("Segoe UI", Font.BOLD, 18), UI.GOLD);
        lblApp.setBounds(20, 16, 120, 24);

        JLabel lblVend = UI.label("Vendedor: " + vendedor.getNombre(), UI.F_BODY, UI.CREAM);
        lblVend.setBounds(160, 18, 300, 20);

        JButton btnCerrar = UI.botonGris("Cerrar sesión");
        btnCerrar.setBounds(990, 12, 90, 32);
        btnCerrar.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        barra.add(lblApp); barra.add(lblVend); barra.add(btnCerrar);
        return barra;
    }

    // ── Panel de productos (izquierda) ────────────────────────────
    private JPanel crearPanelProductos() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(UI.GRAY_100);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 6));

        // Búsqueda
        JPanel busqueda = new JPanel(new BorderLayout(8, 0));
        busqueda.setOpaque(false);
        txtBuscar = UI.campo("");
        txtBuscar.setPreferredSize(new Dimension(200, 36));
        txtBuscar.putClientProperty("placeholder", "Buscar por código o nombre...");
        JButton btnBuscar = UI.botonPrimario("Buscar");
        btnBuscar.addActionListener(e -> filtrarProductos());
        txtBuscar.addActionListener(e -> filtrarProductos());
        JButton btnLimpiar = UI.botonGris("Ver todos");
        btnLimpiar.addActionListener(e -> { txtBuscar.setText(""); cargarTablaInventario(); });

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btns.setOpaque(false);
        btns.add(btnBuscar); btns.add(btnLimpiar);

        busqueda.add(txtBuscar, BorderLayout.CENTER);
        busqueda.add(btns, BorderLayout.EAST);

        // Tabla inventario
        String[] cols = {"Código", "Nombre", "Precio Venta", "Stock", "Estado"};
        modeloInventario = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaInventario = new JTable(modeloInventario);
        UI.estilizarTabla(tablaInventario);
        tablaInventario.getColumnModel().getColumn(0).setMaxWidth(80);
        tablaInventario.getColumnModel().getColumn(2).setMaxWidth(110);
        tablaInventario.getColumnModel().getColumn(3).setMaxWidth(70);
        tablaInventario.getColumnModel().getColumn(4).setMaxWidth(100);

        // Doble clic → agregar al carrito
        tablaInventario.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) agregarAlCarrito();
            }
        });

        JScrollPane scroll = new JScrollPane(tablaInventario);
        scroll.setBorder(BorderFactory.createLineBorder(UI.WARM, 1));

        JLabel lblHint = UI.label("Doble clic en un producto para agregar al carrito", UI.F_SMALL, UI.GRAY_400);

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        JLabel lblTit = UI.label("Inventario disponible", UI.F_HEADING, UI.INK);
        cabecera.add(lblTit, BorderLayout.WEST);
        cabecera.add(busqueda, BorderLayout.SOUTH);

        p.add(cabecera, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        p.add(lblHint, BorderLayout.SOUTH);

        cargarTablaInventario();
        return p;
    }

    private void cargarTablaInventario() {
        modeloInventario.setRowCount(0);
        for (Producto prod : productos) {
            if (!prod.isActivo()) continue;
            String estado = prod.getStock() == 0 ? "Sin stock"
                    : prod.isStockBajo() ? "Stock bajo" : "Disponible";
            modeloInventario.addRow(new Object[]{
                prod.getCodigo(), prod.getNombre(),
                UI.fmt(prod.getPrecioVenta()), prod.getStock(), estado
            });
        }
    }

    private void filtrarProductos() {
        String q = txtBuscar.getText().trim().toLowerCase();
        modeloInventario.setRowCount(0);
        for (Producto p : productos) {
            if (!p.isActivo()) continue;
            if (p.getCodigo().toLowerCase().contains(q) || p.getNombre().toLowerCase().contains(q)) {
                String est = p.getStock() == 0 ? "Sin stock" : p.isStockBajo() ? "Stock bajo" : "Disponible";
                modeloInventario.addRow(new Object[]{
                    p.getCodigo(), p.getNombre(), UI.fmt(p.getPrecioVenta()), p.getStock(), est
                });
            }
        }
    }

    // ── Panel carrito + pago (derecha) ────────────────────────────
    private JPanel crearPanelCarrito() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(UI.GRAY_100);
        p.setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 12));

        JLabel lblTit = UI.label("Venta actual", UI.F_HEADING, UI.INK);

        // Tipo de venta
        JPanel tipoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        tipoPanel.setOpaque(false);
        ButtonGroup bg = new ButtonGroup();
        rbMostrador = new JRadioButton("Mostrador"); rbMostrador.setOpaque(false);
        rbDomicilio = new JRadioButton("Domicilio");  rbDomicilio.setOpaque(false);
        rbMostrador.setFont(UI.F_BODY); rbDomicilio.setFont(UI.F_BODY);
        rbMostrador.setSelected(true);
        bg.add(rbMostrador); bg.add(rbDomicilio);
        rbDomicilio.addActionListener(e -> panelDomicilio.setVisible(true));
        rbMostrador.addActionListener(e -> panelDomicilio.setVisible(false));
        tipoPanel.add(new JLabel("Tipo de venta:")); tipoPanel.add(rbMostrador); tipoPanel.add(rbDomicilio);

        // Panel datos domicilio
        panelDomicilio = new JPanel(new GridLayout(2, 2, 8, 4));
        panelDomicilio.setOpaque(false);
        panelDomicilio.setVisible(false);
        txtDireccion = UI.campo(""); txtTelefono = UI.campo("");
        panelDomicilio.add(UI.label("Dirección:", UI.F_LABEL, UI.GRAY_600));
        panelDomicilio.add(txtDireccion);
        panelDomicilio.add(UI.label("Teléfono:", UI.F_LABEL, UI.GRAY_600));
        panelDomicilio.add(txtTelefono);

        // Tabla carrito
        String[] cols = {"Producto", "Precio", "Cant", "Subtotal"};
        modeloCarrito = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaCarrito = new JTable(modeloCarrito);
        UI.estilizarTabla(tablaCarrito);
        tablaCarrito.getColumnModel().getColumn(1).setMaxWidth(90);
        tablaCarrito.getColumnModel().getColumn(2).setMaxWidth(50);
        tablaCarrito.getColumnModel().getColumn(3).setMaxWidth(100);

        JScrollPane scrollCarrito = new JScrollPane(tablaCarrito);
        scrollCarrito.setBorder(BorderFactory.createLineBorder(UI.WARM, 1));
        scrollCarrito.setPreferredSize(new Dimension(400, 200));

        // Botones carrito
        JPanel botonesCarrito = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        botonesCarrito.setOpaque(false);
        JButton btnAgregar  = UI.botonExito("+ Agregar");
        JButton btnEliminar = UI.botonPeligro("− Quitar");
        JButton btnVaciar   = UI.botonGris("Vaciar");
        btnAgregar.addActionListener(e -> agregarAlCarrito());
        btnEliminar.addActionListener(e -> quitarDelCarrito());
        btnVaciar.addActionListener(e -> vaciarCarrito());
        botonesCarrito.add(btnAgregar); botonesCarrito.add(btnEliminar); botonesCarrito.add(btnVaciar);

        // Panel de totales
        JPanel totales = crearPanelTotales();

        JPanel cabecera = new JPanel(new BorderLayout(0, 4));
        cabecera.setOpaque(false);
        cabecera.add(lblTit, BorderLayout.NORTH);
        cabecera.add(tipoPanel, BorderLayout.CENTER);
        cabecera.add(panelDomicilio, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new BorderLayout(0, 4));
        centro.setOpaque(false);
        centro.add(scrollCarrito, BorderLayout.CENTER);
        centro.add(botonesCarrito, BorderLayout.SOUTH);

        p.add(cabecera, BorderLayout.NORTH);
        p.add(centro, BorderLayout.CENTER);
        p.add(totales, BorderLayout.SOUTH);
        return p;
    }

    private JPanel crearPanelTotales() {
        JPanel p = new JPanel(null);
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(
            new LineBorder(UI.WARM, 1, true),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        p.setPreferredSize(new Dimension(400, 220));

        lblSubtotal = UI.label("Subtotal:    $0", UI.F_BODY, UI.GRAY_600);
        lblSubtotal.setBounds(10, 10, 360, 22);
        lblTotal = UI.label("TOTAL:       $0", new Font("Segoe UI", Font.BOLD, 16), UI.INK);
        lblTotal.setBounds(10, 36, 360, 24);

        JLabel lblEfLbl = UI.label("Efectivo recibido:", UI.F_LABEL, UI.GRAY_600);
        lblEfLbl.setBounds(10, 72, 160, 18);
        txtEfectivo = UI.campo("");
        txtEfectivo.setBounds(170, 68, 160, 32);
        txtEfectivo.addActionListener(e -> actualizarCambio());

        lblCambio = UI.label("Cambio: $0", new Font("Segoe UI", Font.BOLD, 14), UI.SAGE);
        lblCambio.setBounds(10, 108, 360, 22);

        JButton btnCobrar = UI.botonDorado("COBRAR Y GENERAR FACTURA");
        btnCobrar.setBounds(10, 140, 360, 44);
        btnCobrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCobrar.addActionListener(e -> cobrarVenta());

        p.add(lblSubtotal); p.add(lblTotal);
        p.add(lblEfLbl); p.add(txtEfectivo);
        p.add(lblCambio); p.add(btnCobrar);
        return p;
    }

    // ── Lógica de venta ───────────────────────────────────────────
    private void agregarAlCarrito() {
        int fila = tablaInventario.getSelectedRow();
        if (fila < 0) { UI.error(this, "Selecciona un producto de la tabla"); return; }

        String codigo = (String) modeloInventario.getValueAt(fila, 0);
        Producto prod = buscarProductoPorCodigo(codigo);
        if (prod == null || prod.getStock() <= 0) {
            UI.error(this, "Producto sin stock disponible"); return;
        }

        String cantStr = UI.pedir(this, "¿Cuántas unidades de\n\"" + prod.getNombre() + "\"?");
        if (cantStr == null) return;
        int cantidad;
        try { cantidad = Integer.parseInt(cantStr.trim()); }
        catch (NumberFormatException ex) { UI.error(this, "Cantidad inválida"); return; }

        if (cantidad <= 0 || cantidad > prod.getStock()) {
            UI.error(this, "Cantidad inválida o supera el stock (" + prod.getStock() + ")"); return;
        }

        try {
            ventaActual.agregarDetalle(prod, cantidad);
            modeloCarrito.addRow(new Object[]{
                prod.getNombre(), UI.fmt(prod.getPrecioVenta()),
                cantidad, UI.fmt(prod.getPrecioVenta() * cantidad)
            });
            actualizarTotales();
            cargarTablaInventario();
            Persistencia.guardarProductos(productos);
        } catch (Exception ex) {
            UI.error(this, ex.getMessage());
        }
    }

    private void quitarDelCarrito() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila < 0) return;
        DetalleVenta det = ventaActual.getDetalles().get(fila);
        // Devolver stock
        Producto p = buscarProductoPorCodigo(det.getCodigoProducto());
        if (p != null) p.setStock(p.getStock() + det.getCantidad());
        ventaActual.getDetalles().remove(fila);
        modeloCarrito.removeRow(fila);
        actualizarTotales();
        cargarTablaInventario();
        Persistencia.guardarProductos(productos);
    }

    private void vaciarCarrito() {
        if (ventaActual.getDetalles().isEmpty()) return;
        if (!UI.confirmar(this, "¿Vaciar el carrito?")) return;
        for (DetalleVenta d : ventaActual.getDetalles()) {
            Producto p = buscarProductoPorCodigo(d.getCodigoProducto());
            if (p != null) p.setStock(p.getStock() + d.getCantidad());
        }
        ventaActual.getDetalles().clear();
        modeloCarrito.setRowCount(0);
        actualizarTotales();
        cargarTablaInventario();
        Persistencia.guardarProductos(productos);
    }

    private void actualizarTotales() {
        lblSubtotal.setText(String.format("Subtotal:    %s", UI.fmt(ventaActual.getSubtotal())));
        boolean domicilio = rbDomicilio.isSelected();
        double total = ventaActual.getSubtotal() + (domicilio ? 5000 : 0);
        lblTotal.setText(String.format("TOTAL:       %s%s",
            UI.fmt(total), domicilio ? "  (inc. domicilio $5.000)" : ""));
        actualizarCambio();
    }

    private void actualizarCambio() {
        try {
            double ef = Double.parseDouble(txtEfectivo.getText().replaceAll("[^0-9.]",""));
            ventaActual.setEfectivoRecibido(ef);
            double cambio = ef - ventaActual.getTotal();
            lblCambio.setText("Cambio: " + UI.fmt(Math.max(0, cambio)));
            lblCambio.setForeground(cambio < 0 ? UI.DANGER : UI.SAGE);
        } catch (Exception ex) {
            lblCambio.setText("Cambio: —");
        }
    }

    private void cobrarVenta() {
        if (ventaActual.getDetalles().isEmpty()) {
            UI.error(this, "El carrito está vacío"); return;
        }
        double ef;
        try { ef = Double.parseDouble(txtEfectivo.getText().replaceAll("[^0-9.]","")); }
        catch (Exception ex) { UI.error(this, "Ingresa el efectivo recibido"); return; }

        if (ef < ventaActual.getTotal()) {
            UI.error(this, "Efectivo insuficiente. Faltan: " +
                UI.fmt(ventaActual.getTotal() - ef)); return;
        }

        ventaActual.setEfectivoRecibido(ef);

        if (rbDomicilio.isSelected()) {
            String dir = txtDireccion.getText().trim();
            String tel = txtTelefono.getText().trim();
            if (dir.isEmpty() || tel.isEmpty()) {
                UI.error(this, "Completa dirección y teléfono para domicilio"); return;
            }
            ventaActual.setDireccionDomicilio(dir);
            ventaActual.setTelefonoDomicilio(tel);
            ventaActual.setCostoDomicilio(5000);
        }

        ventaActual.setEstado(Venta.EstadoVenta.COMPLETADA);
        ventas.add(ventaActual);
        Persistencia.guardarVentas(ventas);
        Persistencia.guardarProductos(productos);

        // Mostrar factura
        new FacturaFrame(ventaActual).setVisible(true);

        // Reiniciar
        iniciarVenta();
        modeloCarrito.setRowCount(0);
        txtEfectivo.setText("");
        rbMostrador.setSelected(true);
        panelDomicilio.setVisible(false);
        txtDireccion.setText(""); txtTelefono.setText("");
        actualizarTotales();
        cargarTablaInventario();
    }

    private Producto buscarProductoPorCodigo(String codigo) {
        return productos.stream().filter(p -> p.getCodigo().equals(codigo)).findFirst().orElse(null);
    }
}
