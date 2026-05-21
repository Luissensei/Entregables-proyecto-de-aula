package papeleria.persistencia;

import papeleria.modelo.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class Persistencia {
    private static final String DIR = "datos" + File.separator;
    private static final String USUARIOS  = DIR + "usuarios.txt";
    private static final String PRODUCTOS = DIR + "productos.txt";
    private static final String VENTAS    = DIR + "ventas.txt";
    private static final String PAGOS     = DIR + "pagos.txt";

    static {
        new File(DIR).mkdirs();
        inicializarArchivos();
    }

    private static void inicializarArchivos() {
        crearSiNoExiste(USUARIOS,  datosUsuariosDemo());
        crearSiNoExiste(PRODUCTOS, datosProductosDemo());
        crearSiNoExiste(VENTAS,    "");
        crearSiNoExiste(PAGOS,     "");
    }

    private static void crearSiNoExiste(String ruta, String contenido) {
        File f = new File(ruta);
        if (!f.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                if (!contenido.isEmpty()) pw.print(contenido);
            } catch (IOException ignored) {}
        }
    }

    // ── Usuarios ─────────────────────────────────────────────────
    public static List<Usuario> cargarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(USUARIOS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.isBlank()) {
                    Usuario u = Usuario.fromString(linea.trim());
                    if (u != null) lista.add(u);
                }
            }
        } catch (IOException ignored) {}
        return lista;
    }

    public static void guardarUsuarios(List<Usuario> lista) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USUARIOS))) {
            for (Usuario u : lista) pw.println(u.toString());
        } catch (IOException ignored) {}
    }

    // ── Productos ────────────────────────────────────────────────
    public static List<Producto> cargarProductos() {
        List<Producto> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PRODUCTOS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.isBlank()) {
                    Producto p = Producto.fromString(linea.trim());
                    if (p != null) lista.add(p);
                }
            }
        } catch (IOException ignored) {}
        return lista;
    }

    public static void guardarProductos(List<Producto> lista) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PRODUCTOS))) {
            for (Producto p : lista) pw.println(p.toString());
        } catch (IOException ignored) {}
    }

    // ── Ventas ───────────────────────────────────────────────────
    public static List<Venta> cargarVentas() {
        List<Venta> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(VENTAS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.isBlank()) {
                    Venta v = Venta.fromStorageString(linea.trim());
                    if (v != null) lista.add(v);
                }
            }
        } catch (IOException ignored) {}
        return lista;
    }

    public static void guardarVentas(List<Venta> lista) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(VENTAS))) {
            for (Venta v : lista) pw.println(v.toStorageString());
        } catch (IOException ignored) {}
    }

    // ── Pagos ────────────────────────────────────────────────────
    public static List<PagoEmpleado> cargarPagos() {
        List<PagoEmpleado> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PAGOS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.isBlank()) {
                    PagoEmpleado pe = PagoEmpleado.fromString(linea.trim());
                    if (pe != null) lista.add(pe);
                }
            }
        } catch (IOException ignored) {}
        return lista;
    }

    public static void guardarPagos(List<PagoEmpleado> lista) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PAGOS))) {
            for (PagoEmpleado pe : lista) pw.println(pe.toString());
        } catch (IOException ignored) {}
    }

    // ── Datos demo ───────────────────────────────────────────────
    private static String datosUsuariosDemo() {
        return "1|Carlos Ramírez|admin|admin123|ADMIN|0|Cartagena Centro|3001234567|true\n"
             + "2|Ana González|vendedor1|vend123|VENDEDOR|1800000.0|Calle 5 #10-20|3109876543|true\n"
             + "3|Pedro Martínez|vendedor2|vend456|VENDEDOR|1800000.0|Carrera 8 #3-15|3205551234|true\n"
             + "4|Laura Torres|cliente1|cli123|CLIENTE|0.0|Av. Blas de Lezo #22|3157654321|true\n"
             + "5|Diego Herrera|cliente2|cli456|CLIENTE|0.0|Calle 30 #5-40|3126543210|true\n";
    }

    private static String datosProductosDemo() {
        return "1|P001|Cuaderno Norma Grande|Cuadernos|3500.0|2200.0|45|5|true\n"
             + "2|P002|Lápiz Mongol 2B|Escritura|500.0|300.0|120|10|true\n"
             + "3|P003|Borrador Pelikan|Escritura|800.0|500.0|60|8|true\n"
             + "4|P004|Colores Faber 12und|Arte|8500.0|5500.0|30|5|true\n"
             + "5|P005|Resaltador Stabilo|Marcadores|2500.0|1500.0|4|5|true\n"
             + "6|P006|Carpeta Argollada|Archivos|12000.0|7500.0|25|5|true\n"
             + "7|P007|Tijeras Maped|Manualidades|4500.0|2800.0|20|5|true\n"
             + "8|P008|Pegante Colbón|Adhesivos|3200.0|1800.0|35|5|true\n"
             + "9|P009|Block Iris|Papel|6000.0|3800.0|3|5|true\n"
             + "10|P010|Resma Papel Carta|Papel|14000.0|9000.0|18|5|true\n";
    }
}
