╔══════════════════════════════════════════════════════╗
║        PAPELERÍA EL PUNTO — Sistema de Gestión      ║
╠══════════════════════════════════════════════════════╣
║                                                      ║
║  ESTRUCTURA DEL PROYECTO                             ║
║                                                      ║
║  src/papeleria/                                      ║
║  ├── Main.java                                       ║
║  ├── modelo/                                         ║
║  │   ├── Usuario.java                                ║
║  │   ├── Producto.java                               ║
║  │   ├── Venta.java                                  ║
║  │   ├── DetalleVenta.java                           ║
║  │   └── PagoEmpleado.java                           ║
║  ├── persistencia/                                   ║
║  │   └── Persistencia.java                           ║
║  ├── gui/                                            ║
║  │   ├── LoginFrame.java                             ║
║  │   ├── AdminFrame.java                             ║
║  │   ├── VendedorFrame.java                          ║
║  │   ├── ClienteFrame.java                           ║
║  │   └── FacturaFrame.java                           ║
║  └── util/                                           ║
║      └── UI.java                                     ║
║                                                      ║
║  REQUISITOS                                          ║
║  - Java 17 o superior                                ║
║                                                      ║
║  COMPILAR (desde la carpeta raíz del proyecto)       ║
║  javac -encoding UTF-8 -d out \                      ║
║    $(find src -name "*.java")                        ║
║                                                      ║
║  EJECUTAR                                            ║
║  java -cp out papeleria.Main                         ║
║                                                      ║
║  CREDENCIALES DE PRUEBA                              ║
║  Admin    → admin    / admin123                      ║
║  Vendedor → vendedor1 / vend123                      ║
║  Cliente  → cliente1  / cli123                       ║
║                                                      ║
║  DATOS                                               ║
║  Al ejecutar se crea la carpeta "datos/" con:        ║
║  - usuarios.txt   (usuarios y roles)                 ║
║  - productos.txt  (inventario)                       ║
║  - ventas.txt     (historial de ventas)              ║
║  - pagos.txt      (nómina de empleados)              ║
╚══════════════════════════════════════════════════════╝
