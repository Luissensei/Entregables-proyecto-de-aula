package papeleria.modelo;

import java.io.Serializable;

public class DetalleVenta implements Serializable {
    private String codigoProducto;
    private String nombreProducto;
    private int cantidad;
    private double precioUnitario;
    private double precioCosto;

    public DetalleVenta(Producto producto, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser > 0");
        this.codigoProducto = producto.getCodigo();
        this.nombreProducto = producto.getNombre();
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecioVenta();
        this.precioCosto = producto.getPrecioCosto();
    }

    // Constructor para carga desde archivo
    public DetalleVenta(String codigo, String nombre, int cantidad, double precio, double costo) {
        this.codigoProducto = codigo;
        this.nombreProducto = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precio;
        this.precioCosto = costo;
    }

    public String getCodigoProducto() { return codigoProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getPrecioCosto() { return precioCosto; }
    public double getSubtotal() { return precioUnitario * cantidad; }
    public double getCostoTotal() { return precioCosto * cantidad; }
    public double getGanancia() { return getSubtotal() - getCostoTotal(); }

    @Override
    public String toString() {
        return codigoProducto + "~" + nombreProducto + "~" + cantidad
               + "~" + precioUnitario + "~" + precioCosto;
    }

    public static DetalleVenta fromString(String s) {
        String[] p = s.split("~");
        return new DetalleVenta(p[0], p[1], Integer.parseInt(p[2]),
                Double.parseDouble(p[3]), Double.parseDouble(p[4]));
    }
}
