package papeleria.gui;

import papeleria.modelo.*;
import papeleria.persistencia.Persistencia;
import papeleria.util.UI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ClienteFrame extends JFrame {

    private final Usuario cliente;
    private List<Producto> productos;
    private List<Venta> ventas;

    private DefaultTableModel modeloCatalogo;
    private JTable tablaCatalogo;
    private JTextField txtBuscar;

    private DefaultTableModel modeloCarrito;
    private JTable tablaCarrito;
    private JLabel lblTotal, lblCambio;
    private JTextField txtEfectivo;
    private JTextField txtDireccion, txtTelefono;
    private JRadioButton rbRetiro, rbDomicilio;
    private JPanel panelDomicilio;

    private Venta ventaActual;

    public ClienteFrame(Usuario cliente) {
        this.cliente  = cliente;
        this.productos = Persistencia.cargarProductos();
        this.ventas   = Persistencia.cargarVentas();
        iniciarVenta();
        construirUI();
    }

    private void iniciarVenta() {
        ventaActual = new Venta("TIENDA_ONLINE", cliente.getUsuario(), Venta.TipoVenta.MOSTRADOR);
    }

    private void construirUI() {
        setTitle("Papeleria MAILETH - Tienda Online - " + cliente.getNombre());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1060, 660);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UI.GRAY_100);
        root.add(crearBarra(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                crearPanelCatalogo(), crearPanelPedido());
        split.setDividerLocation(600);
        split.setResizeWeight(0.56);
        split.setBorder(null);
        split.setDividerSize(5);
        root.add(split, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel crearBarra() {
        JPanel barra = new JPanel(null);
        barra.setBackground(UI.INK);
        barra.setPreferredSize(new Dimension(1060, 56));

        JLabel logo = UI.label("MAILETH", new Font("Segoe UI", Font.BOLD, 18), UI.GOLD);
        logo.setBounds(20, 16, 120, 24);
        JLabel lblSaludo = UI.label("Hola, " + cliente.getNombre() + " - Catalogo", UI.F_BODY, UI.CREAM);
        lblSaludo.setBounds(160, 18, 400, 20);

        JButton btnSalir = UI.botonGris("Cerrar sesion");
        btnSalir.setBounds(950, 12, 90, 32);
        btnSalir.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });

        barra.add(logo); barra.add(lblSaludo); barra.add(btnSalir);
        return barra;
    }

    private JPanel crearPanelCatalogo() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(UI.GRAY_100);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 6));

        JLabel lblTit = UI.label("Catalogo de productos", UI.F_HEADING, UI.INK);

        txtBuscar = UI.campo("");
        JButton btnBuscar = UI.botonPrimario("Buscar");
        JButton btnTodos  = UI.botonGris("Todos");
        btnBuscar.addActionListener(e -> filtrar());
        txtBuscar.addActionListener(e -> filtrar());
        btnTodos.addActionListener(e -> { txtBuscar.setText(""); cargarCatalogo(); });

        JPanel busqPanel = new JPanel(new BorderLayout(6, 0));
        busqPanel.setOpaque(false);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        btns.setOpaque(false);
        btns.add(btnBuscar); btns.add(btnTodos);
        busqPanel.add(txtBuscar, BorderLayout.CENTER);
        busqPanel.add(btns, BorderLayout.EAST);

        String[] cols = {"Codigo", "Nombre", "Precio", "Disponible"};
        modeloCatalogo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaCatalogo = new JTable(modeloCatalogo);
        UI.estilizarTabla(tablaCatalogo);
        tablaCatalogo.getColumnModel().getColumn(0).setMaxWidth(80);
        tablaCatalogo.getColumnModel().getColumn(2).setMaxWidth(100);
        tablaCatalogo.getColumnModel().getColumn(3).setMaxWidth(80);
        tablaCatalogo.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) agregarAlCarrito();
            }
        });

        JScrollPane scroll = new JScrollPane(tablaCatalogo);
        scroll.setBorder(BorderFactory.createLineBorder(UI.WARM, 1));

        JLabel hint = UI.label("Doble clic para anadir al pedido", UI.F_SMALL, UI.GRAY_400);

        JPanel cab = new JPanel(new BorderLayout(0, 6));
        cab.setOpaque(false);
        cab.add(lblTit, BorderLayout.NORTH);
        cab.add(busqPanel, BorderLayout.SOUTH);

        p.add(cab, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        p.add(hint, BorderLayout.SOUTH);
        cargarCatalogo();
        return p;
    }

    private void cargarCatalogo() {
        modeloCatalogo.setRowCount(0);
        for (Producto prod : productos) {
            if (!prod.isActivo() || prod.getStock() <= 0) continue;
            modeloCatalogo.addRow(new Object[]{
                prod.getCodigo(), prod.getNombre(),
                UI.fmt(prod.getPrecioVenta()), prod.getStock() + " uds"
            });
        }
    }

    private void filtrar() {
        String q = txtBuscar.getText().trim().toLowerCase();
        modeloCatalogo.setRowCount(0);
        for (Producto prod : productos) {
            if (!prod.isActivo() || prod.getStock() <= 0) continue;
            if (prod.getCodigo().toLowerCase().contains(q) || prod.getNombre().toLowerCase().contains(q)) {
                modeloCatalogo.addRow(new Object[]{
                    prod.getCodigo(), prod.getNombre(),
                    UI.fmt(prod.getPrecioVenta()), prod.getStock() + " uds"
                });
            }
        }
    }

    private JPanel crearPanelPedido() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(UI.GRAY_100);
        p.setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 12));

        JLabel lblTit = UI.label("Mi pedido", UI.F_HEADING, UI.INK);

        JPanel tipoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        tipoPanel.setOpaque(false);
        ButtonGroup bg = new ButtonGroup();
        rbRetiro   = new JRadioButton("Retiro en tienda"); rbRetiro.setOpaque(false);
        rbDomicilio = new JRadioButton("Domicilio (+$5.000)"); rbDomicilio.setOpaque(false);
        rbRetiro.setFont(UI.F_BODY); rbDomicilio.setFont(UI.F_BODY);
        rbRetiro.setSelected(true);
        bg.add(rbRetiro); bg.add(rbDomicilio);
        rbDomicilio.addActionListener(e -> { panelDomicilio.setVisible(true); actualizarTotal(); });
        rbRetiro.addActionListener(e -> { panelDomicilio.setVisible(false); actualizarTotal(); });
        tipoPanel.add(rbRetiro); tipoPanel.add(rbDomicilio);

        panelDomicilio = new JPanel(new GridLayout(2, 2, 8, 4));
        panelDomicilio.setOpaque(false);
        panelDomicilio.setVisible(false);
        txtDireccion = UI.campo(""); txtTelefono = UI.campo("");
        panelDomicilio.add(UI.label("Direccion:", UI.F_LABEL, UI.GRAY_600));
        panelDomicilio.add(txtDireccion);
        panelDomicilio.add(UI.label("Telefono:", UI.F_LABEL, UI.GRAY_600));
        panelDomicilio.add(txtTelefono);

        String[] cols = {"Producto", "Precio", "Qty", "Subtotal"};
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

        JPanel btnCarrito = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        btnCarrito.setOpaque(false);
        JButton btnAgregar  = UI.botonExito("+ Agregar");
        JButton btnEliminar = UI.botonPeligro("- Quitar");
        JButton btnVaciar   = UI.botonGris("Vaciar");
        btnAgregar.addActionListener(e -> agregarAlCarrito());
        btnEliminar.addActionListener(e -> quitarItem());
        btnVaciar.addActionListener(e -> vaciarCarrito());
        btnCarrito.add(btnAgregar); btnCarrito.add(btnEliminar); btnCarrito.add(btnVaciar);

        JPanel pagos = crearPanelPago();

        JPanel cab = new JPanel(new BorderLayout(0, 4));
        cab.setOpaque(false);
        cab.add(lblTit, BorderLayout.NORTH);
        cab.add(tipoPanel, BorderLayout.CENTER);
        cab.add(panelDomicilio, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new BorderLayout(0, 4));
        centro.setOpaque(false);
        centro.add(scrollCarrito, BorderLayout.CENTER);
        centro.add(btnCarrito, BorderLayout.SOUTH);

        p.add(cab, BorderLayout.NORTH);
        p.add(centro, BorderLayout.CENTER);
        p.add(pagos, BorderLayout.SOUTH);
        return p;
    }

    private JPanel crearPanelPago() {
        JPanel p = new JPanel(null);
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(
            new LineBorder(UI.WARM, 1, true),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        p.setPreferredSize(new Dimension(380, 200));

        lblTotal = UI.label("TOTAL: $0", new Font("Segoe UI", Font.BOLD, 15), UI.INK);
        lblTotal.setBounds(8, 8, 340, 22);

        JLabel lblEf = UI.label("Efectivo:", UI.F_LABEL, UI.GRAY_600);
        lblEf.setBounds(8, 40, 90, 18);
        txtEfectivo = UI.campo("");
        txtEfectivo.setBounds(100, 36, 150, 32);
        txtEfectivo.addActionListener(e -> calcularCambio());

        lblCambio = UI.label("Cambio: --", UI.F_BODY, UI.SAGE);
        lblCambio.setBounds(8, 76, 340, 20);

        JButton btnComprar = UI.botonDorado("CONFIRMAR COMPRA Y PAGAR");
        btnComprar.setBounds(8, 106, 340, 44);
        btnComprar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnComprar.addActionListener(e -> confirmarCompra());

        p.add(lblTotal); p.add(lblEf); p.add(txtEfectivo);
        p.add(lblCambio); p.add(btnComprar);
        return p;
    }

    private void agregarAlCarrito() {
        int fila = tablaCatalogo.getSelectedRow();
        if (fila < 0) { UI.error(this, "Selecciona un producto"); return; }
        String codigo = (String) modeloCatalogo.getValueAt(fila, 0);
        Producto prod = productos.stream().filter(x -> x.getCodigo().equals(codigo)).findFirst().orElse(null);
        if (prod == null) return;

        String cantStr = UI.pedir(this, "Cantidad de \"" + prod.getNombre() + "\":");
        if (cantStr == null) return;
        int cant;
        try { cant = Integer.parseInt(cantStr.trim()); }
        catch (Exception ex) { UI.error(this, "Cantidad invalida"); return; }
        if (cant <= 0 || cant > prod.getStock()) {
            UI.error(this, "Cantidad no valida (max " + prod.getStock() + ")"); return;
        }
        try {
            ventaActual.agregarDetalle(prod, cant);
            modeloCarrito.addRow(new Object[]{
                prod.getNombre(), UI.fmt(prod.getPrecioVenta()),
                cant, UI.fmt(prod.getPrecioVenta() * cant)
            });
            actualizarTotal();
            cargarCatalogo();
            Persistencia.guardarProductos(productos);
        } catch (Exception ex) { UI.error(this, ex.getMessage()); }
    }

    private void quitarItem() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila < 0) return;
        DetalleVenta det = ventaActual.getDetalles().get(fila);
        Producto p = productos.stream().filter(x -> x.getCodigo().equals(det.getCodigoProducto())).findFirst().orElse(null);
        if (p != null) p.setStock(p.getStock() + det.getCantidad());
        ventaActual.getDetalles().remove(fila);
        modeloCarrito.removeRow(fila);
        actualizarTotal();
        cargarCatalogo();
        Persistencia.guardarProductos(productos);
    }

    private void vaciarCarrito() {
        for (DetalleVenta d : ventaActual.getDetalles()) {
            Producto p = productos.stream().filter(x -> x.getCodigo().equals(d.getCodigoProducto())).findFirst().orElse(null);
            if (p != null) p.setStock(p.getStock() + d.getCantidad());
        }
        ventaActual.getDetalles().clear();
        modeloCarrito.setRowCount(0);
        actualizarTotal();
        cargarCatalogo();
        Persistencia.guardarProductos(productos);
    }

    private void actualizarTotal() {
        double total = ventaActual.getSubtotal() + (rbDomicilio.isSelected() ? 5000 : 0);
        lblTotal.setText("TOTAL: " + UI.fmt(total) + (rbDomicilio.isSelected() ? "  (incl. domicilio)" : ""));
        calcularCambio();
    }

    private void calcularCambio() {
        try {
            double ef = Double.parseDouble(txtEfectivo.getText().replaceAll("[^0-9.]",""));
            ventaActual.setEfectivoRecibido(ef);
            double total = ventaActual.getSubtotal() + (rbDomicilio.isSelected() ? 5000 : 0);
            double cambio = ef - total;
            lblCambio.setText("Cambio: " + UI.fmt(Math.max(0, cambio)));
            lblCambio.setForeground(cambio < 0 ? UI.DANGER : UI.SAGE);
        } catch (Exception ignored) {}
    }

    private void confirmarCompra() {
        if (ventaActual.getDetalles().isEmpty()) { UI.error(this, "Tu carrito esta vacio"); return; }
        double ef;
        try { ef = Double.parseDouble(txtEfectivo.getText().replaceAll("[^0-9.]","")); }
        catch (Exception ex) { UI.error(this, "Ingresa el efectivo"); return; }

        double total = ventaActual.getSubtotal() + (rbDomicilio.isSelected() ? 5000 : 0);
        if (ef < total) { UI.error(this, "Efectivo insuficiente"); return; }

        ventaActual.setEfectivoRecibido(ef);

        if (rbDomicilio.isSelected()) {
            String dir = txtDireccion.getText().trim();
            String tel = txtTelefono.getText().trim();
            if (dir.isEmpty() || tel.isEmpty()) { UI.error(this, "Completa direccion y telefono"); return; }
            ventaActual = new Venta("TIENDA_ONLINE", cliente.getUsuario(), Venta.TipoVenta.DOMICILIO);
            ventaActual.setDireccionDomicilio(dir);
            ventaActual.setTelefonoDomicilio(tel);
            ventaActual.setCostoDomicilio(5000);
            ventaActual.setEfectivoRecibido(ef);
        }

        Venta ventaFinal = new Venta("TIENDA_ONLINE", cliente.getUsuario(),
                rbDomicilio.isSelected() ? Venta.TipoVenta.DOMICILIO : Venta.TipoVenta.MOSTRADOR);
        ventaFinal.setEfectivoRecibido(ef);
        if (rbDomicilio.isSelected()) {
            ventaFinal.setDireccionDomicilio(txtDireccion.getText().trim());
            ventaFinal.setTelefonoDomicilio(txtTelefono.getText().trim());
            ventaFinal.setCostoDomicilio(5000);
        }
        for (DetalleVenta d : ventaActual.getDetalles()) {
            ventaFinal.getDetalles().add(d);
        }
        ventaFinal.setEstado(Venta.EstadoVenta.COMPLETADA);
        ventas.add(ventaFinal);
        Persistencia.guardarVentas(ventas);
        Persistencia.guardarProductos(productos);

        new FacturaFrame(ventaFinal).setVisible(true);

        iniciarVenta();
        modeloCarrito.setRowCount(0);
        txtEfectivo.setText("");
        rbRetiro.setSelected(true);
        panelDomicilio.setVisible(false);
        actualizarTotal();
        cargarCatalogo();
    }
}
