package papeleria.modelo;

import java.io.Serializable;

public class Usuario implements Serializable {
    public enum Rol { ADMIN, VENDEDOR, CLIENTE }

    private static int contador = 0;
    private int id;
    private String nombre;
    private String usuario;
    private String password;
    private Rol rol;
    private double salario;       // para vendedores
    private String direccion;     // para clientes
    private String telefono;
    private boolean activo;

    public Usuario(String nombre, String usuario, String password, Rol rol) {
        this.id = ++contador;
        this.nombre = nombre;
        this.usuario = usuario;
        this.password = password;
        this.rol = rol;
        this.activo = true;
        this.salario = 0;
        this.direccion = "";
        this.telefono = "";
    }

    // Getters y setters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public boolean verificarPassword(String pwd) {
        return this.password.equals(pwd);
    }

    @Override
    public String toString() {
        return id + "|" + nombre + "|" + usuario + "|" + password + "|"
               + rol.name() + "|" + salario + "|" + direccion + "|" + telefono + "|" + activo;
    }

    public static Usuario fromString(String linea) {
        String[] p = linea.split("\\|", -1);
        if (p.length < 9) return null;
        Usuario u = new Usuario(p[1], p[2], p[3], Rol.valueOf(p[4]));
        u.id = Integer.parseInt(p[0]);
        if (u.id >= contador) contador = u.id;
        u.salario = Double.parseDouble(p[5]);
        u.direccion = p[6];
        u.telefono = p[7];
        u.activo = Boolean.parseBoolean(p[8]);
        return u;
    }
}
