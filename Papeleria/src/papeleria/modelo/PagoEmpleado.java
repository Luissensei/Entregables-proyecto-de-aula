package papeleria.modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PagoEmpleado {
    private static int contador = 0;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private int id;
    private String usuarioEmpleado;
    private String nombreEmpleado;
    private LocalDate fecha;
    private String periodo;
    private double salarioBase;
    private double bonificacion;
    private double deducciones;
    private String concepto;

    public PagoEmpleado(String usuario, String nombre, String periodo,
                        double salarioBase, double bonificacion, double deducciones, String concepto) {
        this.id = ++contador;
        this.usuarioEmpleado = usuario;
        this.nombreEmpleado = nombre;
        this.fecha = LocalDate.now();
        this.periodo = periodo;
        this.salarioBase = salarioBase;
        this.bonificacion = bonificacion;
        this.deducciones = deducciones;
        this.concepto = concepto;
    }

    public double getTotalPago() { return salarioBase + bonificacion - deducciones; }

    public int getId() { return id; }
    public String getUsuarioEmpleado() { return usuarioEmpleado; }
    public String getNombreEmpleado() { return nombreEmpleado; }
    public LocalDate getFecha() { return fecha; }
    public String getPeriodo() { return periodo; }
    public double getSalarioBase() { return salarioBase; }
    public void setSalarioBase(double s) { this.salarioBase = s; }
    public double getBonificacion() { return bonificacion; }
    public void setBonificacion(double b) { this.bonificacion = b; }
    public double getDeducciones() { return deducciones; }
    public void setDeducciones(double d) { this.deducciones = d; }
    public String getConcepto() { return concepto; }

    @Override
    public String toString() {
        return id + "|" + usuarioEmpleado + "|" + nombreEmpleado + "|"
               + fecha.format(FMT) + "|" + periodo + "|" + salarioBase + "|"
               + bonificacion + "|" + deducciones + "|" + concepto;
    }

    public static PagoEmpleado fromString(String linea) {
        String[] p = linea.split("\\|", -1);
        if (p.length < 9) return null;
        PagoEmpleado pe = new PagoEmpleado(p[1], p[2], p[4],
                Double.parseDouble(p[5]), Double.parseDouble(p[6]),
                Double.parseDouble(p[7]), p[8]);
        pe.id = Integer.parseInt(p[0]);
        if (pe.id >= contador) contador = pe.id;
        pe.fecha = LocalDate.parse(p[3], FMT);
        return pe;
    }
}
