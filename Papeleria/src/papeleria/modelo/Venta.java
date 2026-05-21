package papeleria.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Venta implements Serializable {
    public enum TipoVenta { MOSTRADOR, DOMICILIO }
    public enum EstadoVenta { PENDIENTE, COMPLETADA, CANCELADA, EN_CAMINO }

    private static int contador = 0;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private int id;
    private String numeroFactura;
    private LocalDateTime fecha;
    private String usuarioVendedor;
    private String usuarioCliente;
    private List<DetalleVenta> detalles;
    private double efectivoRecibido;
    private TipoVenta tipoVenta;
    private EstadoVenta estado;
    private String direccionDomicilio;
    private String telefonoDomicilio;
    private double costoDomicilio;

    public Venta(String vendedor, String cliente, TipoVenta tipo) {
        this.id = ++contador;
        this.numeroFactura = generarNumeroFactura();
        this.fecha = LocalDateTime.now();
        this.usuarioVendedor = vendedor;
        this.usuarioCliente = (cliente != null) ? cliente : "MOSTRADOR";
        this.detalles = new ArrayList<>();
        this.tipoVenta = tipo;
        this.estado = EstadoVenta.PENDIENTE;
        this.costoDomicilio = (tipo == TipoVenta.DOMICILIO) ? 5000.0 : 0.0;
    }

    private String generarNumeroFactura() {
        return String.format("FE-%04d-%d", id,
                System.currentTimeMillis() % 10000);
    }

    public void agregarDetalle(Producto producto, int cantidad) {
        if (producto.getStock() < cantidad)
            throw new IllegalStateException("Stock insuficiente para: " + producto.getNombre());
        producto.setStock(producto.getStock() - cantidad);
        detalles.add(new DetalleVenta(producto, cantidad));
    }

    public double getSubtotal() {
        return detalles.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
    }

    public double getTotal() {
        return getSubtotal() + costoDomicilio;
    }

    public double getCostoTotal() {
        return detalles.stream().mapToDouble(DetalleVenta::getCostoTotal).sum();
    }

    public double getGananciaTotal() {
        return getSubtotal() - getCostoTotal();
    }

    public double getCambio() {
        return Math.max(0, efectivoRecibido - getTotal());
    }

    // Getters y setters
    public int getId() { return id; }
    public String getNumeroFactura() { return numeroFactura; }
    public LocalDateTime getFecha() { return fecha; }
    public String getFechaFormateada() { return fecha.format(FMT); }
    public String getUsuarioVendedor() { return usuarioVendedor; }
    public String getUsuarioCliente() { return usuarioCliente; }
    public List<DetalleVenta> getDetalles() { return detalles; }
    public double getEfectivoRecibido() { return efectivoRecibido; }
    public void setEfectivoRecibido(double e) { this.efectivoRecibido = e; }
    public TipoVenta getTipoVenta() { return tipoVenta; }
    public EstadoVenta getEstado() { return estado; }
    public void setEstado(EstadoVenta estado) { this.estado = estado; }
    public String getDireccionDomicilio() { return direccionDomicilio; }
    public void setDireccionDomicilio(String d) { this.direccionDomicilio = d; }
    public String getTelefonoDomicilio() { return telefonoDomicilio; }
    public void setTelefonoDomicilio(String t) { this.telefonoDomicilio = t; }
    public double getCostoDomicilio() { return costoDomicilio; }
    public void setCostoDomicilio(double c) { this.costoDomicilio = c; }

    // Serialización
    public String toStorageString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append("|").append(numeroFactura).append("|")
          .append(fecha.format(FMT)).append("|").append(usuarioVendedor).append("|")
          .append(usuarioCliente).append("|").append(efectivoRecibido).append("|")
          .append(tipoVenta.name()).append("|").append(estado.name()).append("|")
          .append(direccionDomicilio != null ? direccionDomicilio : "").append("|")
          .append(telefonoDomicilio != null ? telefonoDomicilio : "").append("|")
          .append(costoDomicilio).append("|");
        // detalles separados por ";"
        List<String> dets = new ArrayList<>();
        for (DetalleVenta d : detalles) dets.add(d.toString());
        sb.append(String.join(";", dets));
        return sb.toString();
    }

    public static Venta fromStorageString(String linea) {
        String[] p = linea.split("\\|", -1);
        if (p.length < 12) return null;
        Venta v = new Venta(p[3], p[4], TipoVenta.valueOf(p[6]));
        v.id = Integer.parseInt(p[0]);
        if (v.id >= contador) contador = v.id;
        v.numeroFactura = p[1];
        v.fecha = LocalDateTime.parse(p[2], FMT);
        v.efectivoRecibido = Double.parseDouble(p[5]);
        v.estado = EstadoVenta.valueOf(p[7]);
        v.direccionDomicilio = p[8];
        v.telefonoDomicilio = p[9];
        v.costoDomicilio = Double.parseDouble(p[10]);
        if (!p[11].isEmpty()) {
            for (String det : p[11].split(";")) {
                v.detalles.add(DetalleVenta.fromString(det));
            }
        }
        return v;
    }
}
