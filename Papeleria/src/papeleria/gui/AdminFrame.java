package papeleria.gui;

import papeleria.modelo.*;
import papeleria.persistencia.Persistencia;
import papeleria.util.UI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class AdminFrame extends JFrame {

    private final Usuario admin;
    private List<Producto>     productos;
    private List<Usuario>      usuarios;
    private List<Venta>        ventas;
    private List<PagoEmpleado> pagos;

    // Pestañas
    private JTabbedPane pestanas;

    // ── Productos ────────────────────────────────────────────────
    private DefaultTableModel modeloProd;
    private JTable tablaProd;
    private JTextField tfCodigo, tfNombre, tfCategoria, tfPrecioV, tfPrecioCosto, tfStock;

    // ── Ventas ───────────────────────────────────────────────────
    private DefaultTableModel modeloVentas;

    // ── Usuarios ─────────────────────────────────────────────────
    private DefaultTableModel modeloUsuarios;
    private JTextField tfUNombre, tfUUsuario, tfUPassword, tfUSalario, tfUDireccion;
    private JComboBox<String> cmbURol;

    // ── Pagos ────────────────────────────────────────────────────
    private DefaultTableModel modeloPagos;
    private JComboBox<String> cmbEmpleado;
    private JTextField tfPeriodo, tfSalarioBase, tfBonif, tfDeducc, tfConcepto;

    // ── Stats ────────────────────────────────────────────────────
    private JPanel panelStats;
    private JPanel cardIngresos, cardGanancia, cardVentas, cardProductos, cardPagosTotal;

    public AdminFrame(Usuario admin) {
        this.admin    = admin;
        this.productos = Persistencia.cargarProductos();
        this.usuarios  = Persistencia.cargarUsuarios();
        this.ventas    = Persistencia.cargarVentas();
        this.pagos     = Persistencia.cargarPagos();
        construirUI();
    }

    private void construirUI() {
        setTitle("Papeleria MAILETH — Administracion");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UI.GRAY_100);
        root.add(crearBarra(), BorderLayout.NORTH);

        pestanas = new JTabbedPane(JTabbedPane.LEFT);
        pestanas.setFont(UI.F_BODY);
        pestanas.setBackground(Color.WHITE);
        pestanas.addTab("Dashboard",    crearTabDashboard());
        pestanas.addTab("Productos",    crearTabProductos());
        pestanas.addTab("entas",       crearTabVentas());
        pestanas.addTab("Usuarios",     crearTabUsuarios());
        pestanas.addTab("Pagos",        crearTabPagos());
        root.add(pestanas, BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── Barra superior ────────────────────────────────────────────
    private JPanel crearBarra() {
        JPanel b = new JPanel(null);
        b.setBackground(UI.INK);
        b.setPreferredSize(new Dimension(1200, 56));
        JLabel logo = UI.label("MAILETH", new Font("Segoe UI", Font.BOLD, 18), UI.GOLD);
        logo.setBounds(20, 16, 120, 24);
        JLabel lblAdmin = UI.label("Administrador: " + admin.getNombre(), UI.F_BODY, UI.CREAM);
        lblAdmin.setBounds(160, 18, 300, 20);
        JButton btnSalir = UI.botonGris("Cerrar sesion");
        btnSalir.setBounds(1090, 12, 90, 32);
        btnSalir.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        b.add(logo); b.add(lblAdmin); b.add(btnSalir);
        return b;
    }

    // ══════════════════════════════════════════════════════════════
    //  TAB: DASHBOARD
    // ══════════════════════════════════════════════════════════════
    private JPanel crearTabDashboard() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(UI.GRAY_100);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTit = UI.label("Resumen ejecutivo", new Font("Segoe UI", Font.BOLD, 20), UI.INK);

        // Tarjetas de estadísticas (5)
        panelStats = new JPanel(new GridLayout(1, 5, 12, 0));
        panelStats.setOpaque(false);
        cardIngresos   = UI.tarjetaStat("Ingresos totales",  calcIngresos(),   UI.SAGE);
        cardGanancia   = UI.tarjetaStat("Ganancia neta",     calcGanancia(),   UI.SKY);
        cardVentas     = UI.tarjetaStat("Ventas realizadas", String.valueOf(ventas.size()), UI.GOLD);
        cardProductos  = UI.tarjetaStat("Productos activos", String.valueOf(productos.stream().filter(Producto::isActivo).count()), UI.RUST);
        cardPagosTotal = UI.tarjetaStat("Pagos empleados",   calcPagosTotal(), UI.DANGER);
        panelStats.add(cardIngresos); panelStats.add(cardGanancia);
        panelStats.add(cardVentas);   panelStats.add(cardProductos);
        panelStats.add(cardPagosTotal);

        // Tabla top 5 productos más vendidos
        JLabel lblTop = UI.label("Top 5 productos mas vendidos", UI.F_HEADING, UI.INK);
        String[] colsTop = {"Producto", "Unidades vendidas", "Ingresos"};
        DefaultTableModel mTop = new DefaultTableModel(colsTop, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaTop = new JTable(mTop);
        UI.estilizarTabla(tablaTop);
        cargarTopProductos(mTop);

        // Tabla stock bajo
        JLabel lblStk = UI.label("Productos con stock bajo", UI.F_HEADING, UI.RUST);
        String[] colsStk = {"Codigo", "Nombre", "Stock actual", "Stock minimo"};
        DefaultTableModel mStk = new DefaultTableModel(colsStk, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaStk = new JTable(mStk);
        UI.estilizarTabla(tablaStk);
        for (Producto prod : productos) {
            if (prod.isActivo() && prod.isStockBajo()) {
                mStk.addRow(new Object[]{prod.getCodigo(), prod.getNombre(), prod.getStock(), prod.getStockMinimo()});
            }
        }

        JPanel inferior = new JPanel(new GridLayout(1, 2, 16, 0));
        inferior.setOpaque(false);
        JScrollPane st1 = new JScrollPane(tablaTop); st1.setBorder(BorderFactory.createLineBorder(UI.WARM));
        JScrollPane st2 = new JScrollPane(tablaStk); st2.setBorder(BorderFactory.createLineBorder(UI.WARM));
        JPanel p1 = new JPanel(new BorderLayout(0, 6)); p1.setOpaque(false);
        p1.add(lblTop, BorderLayout.NORTH); p1.add(st1, BorderLayout.CENTER);
        JPanel p2 = new JPanel(new BorderLayout(0, 6)); p2.setOpaque(false);
        p2.add(lblStk, BorderLayout.NORTH); p2.add(st2, BorderLayout.CENTER);
        inferior.add(p1); inferior.add(p2);

        p.add(lblTit, BorderLayout.NORTH);
        p.add(panelStats, BorderLayout.CENTER);

        JButton btnRefrescar = UI.botonInfo("Actualizar datos");
        btnRefrescar.addActionListener(e -> refrescarDashboard());

        JPanel sur = new JPanel(new BorderLayout(0, 10));
        sur.setOpaque(false);
        sur.add(btnRefrescar, BorderLayout.NORTH);
        sur.add(inferior, BorderLayout.CENTER);
        p.add(sur, BorderLayout.SOUTH);
        p.setPreferredSize(new Dimension(900, 480));
        return p;
    }

    private void refrescarDashboard() {
        ventas    = Persistencia.cargarVentas();
        pagos     = Persistencia.cargarPagos();
        productos = Persistencia.cargarProductos();
        UI.actualizarTarjetaStat(cardIngresos,   calcIngresos());
        UI.actualizarTarjetaStat(cardGanancia,   calcGanancia());
        UI.actualizarTarjetaStat(cardVentas,     String.valueOf(ventas.size()));
        UI.actualizarTarjetaStat(cardProductos,  String.valueOf(productos.stream().filter(Producto::isActivo).count()));
        UI.actualizarTarjetaStat(cardPagosTotal, calcPagosTotal());
    }

    private String calcIngresos() {
        return UI.fmt(ventas.stream().mapToDouble(Venta::getTotal).sum());
    }
    private String calcGanancia() {
        return UI.fmt(ventas.stream().mapToDouble(Venta::getGananciaTotal).sum());
    }
    private String calcPagosTotal() {
        return UI.fmt(pagos.stream().mapToDouble(PagoEmpleado::getTotalPago).sum());
    }

    private void cargarTopProductos(DefaultTableModel m) {
        Map<String, double[]> acum = new LinkedHashMap<>();
        for (Venta v : ventas) {
            for (DetalleVenta d : v.getDetalles()) {
                acum.computeIfAbsent(d.getNombreProducto(), k -> new double[2]);
                acum.get(d.getNombreProducto())[0] += d.getCantidad();
                acum.get(d.getNombreProducto())[1] += d.getSubtotal();
            }
        }
        acum.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue()[0], a.getValue()[0]))
            .limit(5)
            .forEach(e -> m.addRow(new Object[]{
                e.getKey(),
                (int) e.getValue()[0],
                UI.fmt(e.getValue()[1])
            }));
    }

    // ══════════════════════════════════════════════════════════════
    //  TAB: PRODUCTOS
    // ══════════════════════════════════════════════════════════════
    private JPanel crearTabProductos() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setBackground(UI.GRAY_100);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Tabla
        String[] cols = {"ID", "Codigo", "Nombre", "Categoria", "P.Venta", "P.Costo", "Stock", "Margen", "Activo"};
        modeloProd = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaProd = new JTable(modeloProd);
        UI.estilizarTabla(tablaProd);
        tablaProd.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProd.getSelectionModel().addListSelectionListener(e -> cargarFormProd());
        JScrollPane scroll = new JScrollPane(tablaProd);
        scroll.setBorder(BorderFactory.createLineBorder(UI.WARM));

        // Formulario
        JPanel form = crearFormProducto();
        form.setPreferredSize(new Dimension(280, 500));

        p.add(scroll, BorderLayout.CENTER);
        p.add(form, BorderLayout.EAST);
        cargarTablaProductos();
        return p;
    }

    private JPanel crearFormProducto() {
        JPanel f = new JPanel(null);
        f.setBackground(Color.WHITE);
        f.setBorder(new CompoundBorder(new LineBorder(UI.WARM, 1, true), BorderFactory.createEmptyBorder(12, 14, 12, 14)));

        JLabel tit = UI.label("Gestion de producto", UI.F_SUBHEAD, UI.INK);
        tit.setBounds(0, 0, 260, 24);

        String[] lbls = {"Codigo:", "Nombre:", "Categoria:", "Precio venta:", "Precio costo:", "Stock:"};
        int y = 32;
        tfCodigo    = agregarCampoForm(f, lbls[0], y); y += 52;
        tfNombre    = agregarCampoForm(f, lbls[1], y); y += 52;
        tfCategoria = agregarCampoForm(f, lbls[2], y); y += 52;
        tfPrecioV   = agregarCampoForm(f, lbls[3], y); y += 52;
        tfPrecioCosto = agregarCampoForm(f, lbls[4], y); y += 52;
        tfStock     = agregarCampoForm(f, lbls[5], y); y += 52;

        JButton btnGuardar    = UI.botonExito("Guardar");
        JButton btnEliminar   = UI.botonPeligro("Eliminar");
        JButton btnNuevo      = UI.botonGris("Nuevo");

        btnGuardar.setBounds(0, y, 80, 36);
        btnNuevo.setBounds(88, y, 80, 36);
        btnEliminar.setBounds(176, y, 80, 36);

        btnGuardar.addActionListener(e -> guardarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnNuevo.addActionListener(e -> limpiarFormProd());

        f.add(tit); f.add(btnGuardar); f.add(btnNuevo); f.add(btnEliminar);
        return f;
    }

    private JTextField agregarCampoForm(JPanel f, String lbl, int y) {
        JLabel l = UI.label(lbl, UI.F_LABEL, UI.GRAY_600);
        l.setBounds(0, y, 260, 16);
        JTextField tf = UI.campo("");
        tf.setBounds(0, y + 18, 252, 30);
        f.add(l); f.add(tf);
        return tf;
    }

    private void cargarTablaProductos() {
        modeloProd.setRowCount(0);
        for (Producto pr : productos) {
            modeloProd.addRow(new Object[]{
                pr.getId(), pr.getCodigo(), pr.getNombre(), pr.getCategoria(),
                UI.fmt(pr.getPrecioVenta()), UI.fmt(pr.getPrecioCosto()),
                pr.getStock(), UI.fmt(pr.getMargen()), pr.isActivo() ? "Sí" : "No"
            });
        }
    }

    private void cargarFormProd() {
        int fila = tablaProd.getSelectedRow();
        if (fila < 0) return;
        Producto pr = productos.get(fila);
        tfCodigo.setText(pr.getCodigo()); tfNombre.setText(pr.getNombre());
        tfCategoria.setText(pr.getCategoria());
        tfPrecioV.setText(String.valueOf(pr.getPrecioVenta()));
        tfPrecioCosto.setText(String.valueOf(pr.getPrecioCosto()));
        tfStock.setText(String.valueOf(pr.getStock()));
    }

    private void guardarProducto() {
        try {
            int fila = tablaProd.getSelectedRow();
            if (fila >= 0) {
                Producto pr = productos.get(fila);
                pr.setCodigo(tfCodigo.getText().trim());
                pr.setNombre(tfNombre.getText().trim());
                pr.setCategoria(tfCategoria.getText().trim());
                pr.setPrecioVenta(Double.parseDouble(tfPrecioV.getText().trim()));
                pr.setPrecioCosto(Double.parseDouble(tfPrecioCosto.getText().trim()));
                pr.setStock(Integer.parseInt(tfStock.getText().trim()));
            } else {
                Producto nuevo = new Producto(tfCodigo.getText().trim(), tfNombre.getText().trim(),
                    tfCategoria.getText().trim(),
                    Double.parseDouble(tfPrecioV.getText().trim()),
                    Double.parseDouble(tfPrecioCosto.getText().trim()),
                    Integer.parseInt(tfStock.getText().trim()));
                productos.add(nuevo);
            }
            Persistencia.guardarProductos(productos);
            cargarTablaProductos();
            limpiarFormProd();
            UI.exito(this, "Producto guardado correctamente");
        } catch (Exception ex) {
            UI.error(this, "Error: " + ex.getMessage());
        }
    }

    private void eliminarProducto() {
        int fila = tablaProd.getSelectedRow();
        if (fila < 0) { UI.error(this, "Selecciona un producto"); return; }
        if (!UI.confirmar(this, "¿Desactivar este producto?")) return;
        productos.get(fila).setActivo(false);
        Persistencia.guardarProductos(productos);
        cargarTablaProductos();
    }

    private void limpiarFormProd() {
        tablaProd.clearSelection();
        tfCodigo.setText(""); tfNombre.setText(""); tfCategoria.setText("");
        tfPrecioV.setText(""); tfPrecioCosto.setText(""); tfStock.setText("");
    }

    // ══════════════════════════════════════════════════════════════
    //  TAB: VENTAS
    // ══════════════════════════════════════════════════════════════
    private JPanel crearTabVentas() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(UI.GRAY_100);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel tit = UI.label("Historial de ventas", UI.F_HEADING, UI.INK);

        String[] cols = {"Factura", "Fecha", "Vendedor", "Cliente", "Tipo", "Total", "Ganancia", "Estado"};
        modeloVentas = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaV = new JTable(modeloVentas);
        UI.estilizarTabla(tablaV);
        JScrollPane scroll = new JScrollPane(tablaV);
        scroll.setBorder(BorderFactory.createLineBorder(UI.WARM));

        // Botones
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btns.setOpaque(false);
        JButton btnVerFactura = UI.botonInfo("Ver factura");
        JButton btnActualizar = UI.botonGris("↻ Actualizar");
        btnVerFactura.addActionListener(e -> {
            int fila = tablaV.getSelectedRow();
            if (fila < 0) { UI.error(this, "Selecciona una venta"); return; }
            new FacturaFrame(ventas.get(fila)).setVisible(true);
        });
        btnActualizar.addActionListener(e -> {
            ventas = Persistencia.cargarVentas();
            cargarTablaVentas();
        });
        btns.add(btnVerFactura); btns.add(btnActualizar);

        // Resumen en pie
        JPanel resumen = crearResumenVentas();

        p.add(tit, BorderLayout.NORTH);
        p.add(scroll, BorderLayout.CENTER);
        JPanel sur = new JPanel(new BorderLayout(0, 6));
        sur.setOpaque(false);
        sur.add(btns, BorderLayout.NORTH);
        sur.add(resumen, BorderLayout.CENTER);
        p.add(sur, BorderLayout.SOUTH);
        cargarTablaVentas();
        return p;
    }

    private JPanel crearResumenVentas() {
        JPanel p = new JPanel(new GridLayout(1, 4, 10, 0));
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(900, 70));
        double ingresos  = ventas.stream().mapToDouble(Venta::getTotal).sum();
        double ganancia  = ventas.stream().mapToDouble(Venta::getGananciaTotal).sum();
        double domicilios = ventas.stream().filter(v -> v.getTipoVenta() == Venta.TipoVenta.DOMICILIO).mapToDouble(Venta::getCostoDomicilio).sum();
        p.add(UI.tarjetaStat("Total ingresos",   UI.fmt(ingresos),  UI.SAGE));
        p.add(UI.tarjetaStat("Ganancia neta",     UI.fmt(ganancia), UI.SKY));
        p.add(UI.tarjetaStat("Ingr. domicilios",  UI.fmt(domicilios), UI.GOLD));
        p.add(UI.tarjetaStat("N° de ventas",      String.valueOf(ventas.size()), UI.RUST));
        return p;
    }

    private void cargarTablaVentas() {
        modeloVentas.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
        for (Venta v : ventas) {
            modeloVentas.addRow(new Object[]{
                v.getNumeroFactura(), v.getFecha().format(fmt),
                v.getUsuarioVendedor(), v.getUsuarioCliente(),
                v.getTipoVenta().name(), UI.fmt(v.getTotal()),
                UI.fmt(v.getGananciaTotal()), v.getEstado().name()
            });
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  TAB: USUARIOS
    // ══════════════════════════════════════════════════════════════
    private JPanel crearTabUsuarios() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setBackground(UI.GRAY_100);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        String[] cols = {"ID", "Nombre", "Usuario", "Rol", "Salario", "Activo"};
        modeloUsuarios = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaU = new JTable(modeloUsuarios);
        UI.estilizarTabla(tablaU);
        tablaU.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaU.getSelectedRow();
            if (fila < 0) return;
            Usuario u = usuarios.get(fila);
            tfUNombre.setText(u.getNombre()); tfUUsuario.setText(u.getUsuario());
            tfUPassword.setText(u.getPassword()); tfUSalario.setText(String.valueOf(u.getSalario()));
            tfUDireccion.setText(u.getDireccion());
            cmbURol.setSelectedItem(u.getRol().name());
        });

        JScrollPane scroll = new JScrollPane(tablaU);
        scroll.setBorder(BorderFactory.createLineBorder(UI.WARM));

        // Formulario usuario
        JPanel formU = new JPanel(null);
        formU.setBackground(Color.WHITE);
        formU.setBorder(new CompoundBorder(new LineBorder(UI.WARM, 1, true), BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        formU.setPreferredSize(new Dimension(270, 500));

        JLabel titU = UI.label("Gestion de usuario", UI.F_SUBHEAD, UI.INK);
        titU.setBounds(0, 0, 250, 22);

        int yu = 28;
        tfUNombre   = agregarCampoForm(formU, "Nombre:", yu); yu += 52;
        tfUUsuario  = agregarCampoForm(formU, "Usuario:", yu); yu += 52;
        tfUPassword = agregarCampoForm(formU, "Contraseña:", yu); yu += 52;

        JLabel lblRol = UI.label("Rol:", UI.F_LABEL, UI.GRAY_600);
        lblRol.setBounds(0, yu, 250, 16);
        cmbURol = new JComboBox<>(new String[]{"ADMIN","VENDEDOR","CLIENTE"});
        cmbURol.setBounds(0, yu + 18, 246, 30);
        cmbURol.setFont(UI.F_BODY);
        yu += 52;

        tfUSalario   = agregarCampoForm(formU, "Salario (vendedores):", yu); yu += 52;
        tfUDireccion = agregarCampoForm(formU, "Dirección (clientes):", yu); yu += 52;

        JButton btnGU = UI.botonExito("Guardar");
        JButton btnNU = UI.botonGris("Nuevo");
        JButton btnDA = UI.botonPeligro("Desactivar");
        btnGU.setBounds(0, yu, 76, 34); btnNU.setBounds(82, yu, 76, 34); btnDA.setBounds(164, yu, 80, 34);

        btnGU.addActionListener(e -> guardarUsuario(tablaU));
        btnDA.addActionListener(e -> {
            int f = tablaU.getSelectedRow();
            if (f < 0) return;
            usuarios.get(f).setActivo(false);
            Persistencia.guardarUsuarios(usuarios);
            cargarTablaUsuarios();
        });
        btnNU.addActionListener(e -> tablaU.clearSelection());

        formU.add(titU); formU.add(lblRol); formU.add(cmbURol);
        formU.add(btnGU); formU.add(btnNU); formU.add(btnDA);

        p.add(scroll, BorderLayout.CENTER);
        p.add(formU, BorderLayout.EAST);
        cargarTablaUsuarios();
        return p;
    }

    private void cargarTablaUsuarios() {
        modeloUsuarios.setRowCount(0);
        for (Usuario u : usuarios) {
            modeloUsuarios.addRow(new Object[]{
                u.getId(), u.getNombre(), u.getUsuario(),
                u.getRol().name(), UI.fmt(u.getSalario()),
                u.isActivo() ? "Sí" : "No"
            });
        }
    }

    private void guardarUsuario(JTable tablaU) {
        try {
            int fila = tablaU.getSelectedRow();
            if (fila >= 0) {
                Usuario u = usuarios.get(fila);
                u.setNombre(tfUNombre.getText().trim());
                u.setUsuario(tfUUsuario.getText().trim());
                u.setPassword(tfUPassword.getText().trim());
                u.setRol(Usuario.Rol.valueOf((String) cmbURol.getSelectedItem()));
                u.setSalario(Double.parseDouble(tfUSalario.getText().trim().isEmpty() ? "0" : tfUSalario.getText().trim()));
                u.setDireccion(tfUDireccion.getText().trim());
            } else {
                Usuario nuevo = new Usuario(tfUNombre.getText().trim(), tfUUsuario.getText().trim(),
                    tfUPassword.getText().trim(), Usuario.Rol.valueOf((String) cmbURol.getSelectedItem()));
                nuevo.setSalario(Double.parseDouble(tfUSalario.getText().trim().isEmpty() ? "0" : tfUSalario.getText().trim()));
                nuevo.setDireccion(tfUDireccion.getText().trim());
                usuarios.add(nuevo);
            }
            Persistencia.guardarUsuarios(usuarios);
            cargarTablaUsuarios();
            UI.exito(this, "Usuario guardado");
        } catch (Exception ex) { UI.error(this, "Error: " + ex.getMessage()); }
    }

    // ══════════════════════════════════════════════════════════════
    //  TAB: PAGOS DE EMPLEADOS
    // ══════════════════════════════════════════════════════════════
    private JPanel crearTabPagos() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setBackground(UI.GRAY_100);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        String[] cols = {"ID", "Empleado", "Período", "Salario base", "Bonificación", "Deducciones", "Total pagado", "Fecha"};
        modeloPagos = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaP = new JTable(modeloPagos);
        UI.estilizarTabla(tablaP);
        JScrollPane scroll = new JScrollPane(tablaP);
        scroll.setBorder(BorderFactory.createLineBorder(UI.WARM));

        // Formulario
        JPanel formP = new JPanel(null);
        formP.setBackground(Color.WHITE);
        formP.setBorder(new CompoundBorder(new LineBorder(UI.WARM, 1, true), BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        formP.setPreferredSize(new Dimension(270, 440));

        JLabel titP = UI.label("Registrar pago", UI.F_SUBHEAD, UI.INK);
        titP.setBounds(0, 0, 250, 22);

        // ComboBox empleados (solo vendedores y activos)
        JLabel lblEmp = UI.label("Empleado:", UI.F_LABEL, UI.GRAY_600);
        lblEmp.setBounds(0, 28, 250, 16);
        cmbEmpleado = new JComboBox<>();
        cmbEmpleado.setFont(UI.F_BODY);
        cmbEmpleado.setBounds(0, 46, 246, 30);
        usuarios.stream()
            .filter(u -> u.getRol() == Usuario.Rol.VENDEDOR && u.isActivo())
            .forEach(u -> cmbEmpleado.addItem(u.getUsuario() + " — " + u.getNombre()));

        int yp = 84;
        tfPeriodo    = agregarCampoForm(formP, "Periodo (ej: Mayo 2026):", yp); yp += 52;
        tfSalarioBase = agregarCampoForm(formP, "Salario base ($):", yp); yp += 52;
        tfBonif       = agregarCampoForm(formP, "Bonificación ($):", yp); yp += 52;
        tfDeducc      = agregarCampoForm(formP, "Deducciones ($):", yp); yp += 52;
        tfConcepto    = agregarCampoForm(formP, "Concepto:", yp); yp += 52;

        JButton btnRegPago = UI.botonDorado("Registrar pago");
        btnRegPago.setBounds(0, yp, 246, 38);
        btnRegPago.addActionListener(e -> registrarPago());

        formP.add(titP); formP.add(lblEmp); formP.add(cmbEmpleado);
        formP.add(btnRegPago);

        p.add(scroll, BorderLayout.CENTER);
        p.add(formP, BorderLayout.EAST);
        cargarTablaPagos();
        return p;
    }

    private void cargarTablaPagos() {
        modeloPagos.setRowCount(0);
        for (PagoEmpleado pe : pagos) {
            modeloPagos.addRow(new Object[]{
                pe.getId(), pe.getNombreEmpleado(), pe.getPeriodo(),
                UI.fmt(pe.getSalarioBase()), UI.fmt(pe.getBonificacion()),
                UI.fmt(pe.getDeducciones()), UI.fmt(pe.getTotalPago()),
                pe.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            });
        }
    }

    private void registrarPago() {
        try {
            if (cmbEmpleado.getItemCount() == 0) { UI.error(this, "No hay vendedores activos"); return; }
            String seleccion = (String) cmbEmpleado.getSelectedItem();
            String usuEmp = seleccion.split(" — ")[0];
            String nomEmp = seleccion.split(" — ")[1];
            PagoEmpleado pe = new PagoEmpleado(
                usuEmp, nomEmp, tfPeriodo.getText().trim(),
                Double.parseDouble(tfSalarioBase.getText().trim()),
                Double.parseDouble(tfBonif.getText().trim().isEmpty() ? "0" : tfBonif.getText().trim()),
                Double.parseDouble(tfDeducc.getText().trim().isEmpty() ? "0" : tfDeducc.getText().trim()),
                tfConcepto.getText().trim().isEmpty() ? "Pago de nomina" : tfConcepto.getText().trim()
            );
            pagos.add(pe);
            Persistencia.guardarPagos(pagos);
            cargarTablaPagos();
            tfPeriodo.setText(""); tfSalarioBase.setText(""); tfBonif.setText("");
            tfDeducc.setText(""); tfConcepto.setText("");
            UI.exito(this, "Pago registrado: " + UI.fmt(pe.getTotalPago()) + " a " + nomEmp);
        } catch (Exception ex) { UI.error(this, "Error: " + ex.getMessage()); }
    }
}
