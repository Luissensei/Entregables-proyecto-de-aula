package papeleria.modelo;

import java.io.Serializable;

public class Producto implements Serializable {
    private static int contador = 0;
    private int id;
    private String codigo;
    private String nombre;
    private String categoria;
    private double precioVenta;
    private double precioCosto;
    private int stock;
    private int stockMinimo;
    private boolean activo;

    public Producto(String codigo, String nombre, String categoria,
                    double precioVenta, double precioCosto, int stock) {
        this.id = ++contador;
        setCodigo(codigo);
        setNombre(nombre);
        this.categoria = categoria;
        setPrecioVenta(precioVenta);
        setPrecioCosto(precioCosto);
        setStock(stock);
        this.stockMinimo = 5;
        this.activo = true;
    }

    public int getId() { return id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String c) {
        if (c == null || c.isBlank()) throw new IllegalArgumentException("Código requerido");
        this.codigo = c.trim().toUpperCase();
    }
    public String getNombre() { return nombre; }
    public void setNombre(String n) {
        if (n == null || n.isBlank()) throw new IllegalArgumentException("Nombre requerido");
        this.nombre = n.trim();
    }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double p) {
        if (p < 0) throw new IllegalArgumentException("Precio no puede ser negativo");
        this.precioVenta = p;
    }
    public double getPrecioCosto() { return precioCosto; }
    public void setPrecioCosto(double p) {
        if (p < 0) throw new IllegalArgumentException("Precio costo no puede ser negativo");
        this.precioCosto = p;
    }
    public int getStock() { return stock; }
    public void setStock(int s) {
        if (s < 0) throw new IllegalArgumentException("Stock no puede ser negativo");
        this.stock = s;
    }
    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int s) { this.stockMinimo = s; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public boolean isStockBajo() { return stock <= stockMinimo; }
    public double getMargen() { return precioVenta - precioCosto; }
    public double getValorInventario() { return precioCosto * stock; }

    @Override
    public String toString() {
        return id + "|" + codigo + "|" + nombre + "|" + categoria + "|"
               + precioVenta + "|" + precioCosto + "|" + stock + "|" + stockMinimo + "|" + activo;
    }

    public static Producto fromString(String linea) {
        String[] p = linea.split("\\|", -1);
        if (p.length < 9) return null;
        Producto prod = new Producto(p[1], p[2], p[3],
                Double.parseDouble(p[4]), Double.parseDouble(p[5]), Integer.parseInt(p[6]));
        prod.id = Integer.parseInt(p[0]);
        if (prod.id >= contador) contador = prod.id;
        prod.stockMinimo = Integer.parseInt(p[7]);
        prod.activo = Boolean.parseBoolean(p[8]);
        return prod;
    }
}
