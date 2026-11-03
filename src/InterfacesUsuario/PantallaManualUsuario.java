//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

public class PantallaManualUsuario extends JDialog {
    private JTabbedPane tabbedPane;

    public PantallaManualUsuario(JFrame padre) {
        super(padre, "Ayuda del Sistema", true);
        this.initComponents();
    }

    private void initComponents() {
        this.setSize(850, 650);
        this.setLocationRelativeTo(this.getParent());
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(41, 128, 185));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel lblTitulo = new JLabel("Manual de Usuario - Sistema de Transporte Interprovincial");
        lblTitulo.setFont(new Font("Arial", 1, 18));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);
        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.addTab("Introducción", this.crearPanelIntroduccion());
        this.tabbedPane.addTab("Gestión de Datos", this.crearPanelGestionDatos());
        this.tabbedPane.addTab("Consultas", this.crearPanelConsultas());
        this.tabbedPane.addTab("Reportes", this.crearPanelReportes());
        this.tabbedPane.addTab("Solución de Problemas", this.crearPanelSolucion());
        JPanel panelBotones = new JPanel();
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener((e) -> this.dispose());
        panelBotones.add(btnCerrar);
        panelPrincipal.add(panelTitulo, "North");
        panelPrincipal.add(this.tabbedPane, "Center");
        panelPrincipal.add(panelBotones, "South");
        this.add(panelPrincipal);
    }

    private JScrollPane crearPanelIntroduccion() {
        JTextArea texto = new JTextArea();
        texto.setEditable(false);
        texto.setWrapStyleWord(true);
        texto.setLineWrap(true);
        texto.setMargin(new Insets(15, 15, 15, 15));
        texto.setFont(new Font("Arial", 0, 13));
        texto.setText("BIENVENIDO AL SISTEMA DE TRANSPORTE INTERPROVINCIAL\n\nEste sistema está diseñado exclusivamente para OPERADORES y ADMINISTRADORES de transporte.\nPermite la gestión integral de flotas, personal, venta de pasajes y administración interna.\nNOTA: Este software no está destinado para el uso directo por parte de los pasajeros.\n\nCARACTERÍSTICAS PRINCIPALES:\n\n• Gestión de Flota (Buses y Conductores):\n  Administre vehículos, asigne conductores y controle su estado.\n\n• Logística (Rutas y Viajes):\n  Defina trayectos, programe salidas y asigne recursos (Bus + Conductores).\n\n• Gestión de Pasajeros:\n  Base de datos de clientes que viajan (independiente de los usuarios del sistema).\n\n• Venta y Reservas:\n  Venta de pasajes, selección visual de asientos y control de ocupación.\n\n• Facturación y Caja:\n  Emisión de facturas a pasajeros y registro de pagos.\n\n• Seguridad y Usuarios:\n  Control de acceso para Administradores y Operadores del sistema.\n\n• Gestión de Empresas: Registro de empresas de transporte\n  Mantenga información actualizada de las empresas operadoras.\n\nPRIMEROS PASOS:\n\n1. Registre las empresas de transporte en el sistema\n2. Registre Buses y Conductores (Personal de la flota)\n3. Configure las Rutas disponibles\n4. Programe los Viajes asignando Bus y Conductores\n5. Registre Pasajeros al momento de la venta\n6. Gestione Reservas y Facturación\n\nPara más información sobre cada módulo, consulte las pestañas siguientes.");
        return new JScrollPane(texto);
    }

    private JScrollPane crearPanelGestionDatos() {
        JTextArea texto = new JTextArea();
        texto.setEditable(false);
        texto.setWrapStyleWord(true);
        texto.setLineWrap(true);
        texto.setMargin(new Insets(15, 15, 15, 15));
        texto.setFont(new Font("Arial", 0, 13));
        texto.setText("GESTIÓN DE DATOS\n\nEl sistema cuenta con 7 módulos principales de gestión de datos, cada uno accesible a través de pestañas en la pantalla principal.\n\nOPERACIONES COMUNES:\n\nBOTÓN 'TODOS':\nMuestra todos los registros almacenados en el módulo actual.\nÚtil para tener una vista completa de la información.\n\nBOTÓN 'AGREGAR':\nAbre un formulario para crear un nuevo registro.\nComplete todos los campos obligatorios y presione 'Guardar'.\n\nBOTÓN 'EDITAR':\nPermite modificar un registro existente.\nIMPORTANTE: Primero debe seleccionar una fila de la tabla.\nEl sistema abrirá el formulario con los datos actuales.\nModifique solo los campos necesarios y presione 'Guardar'.\n\nBOTÓN 'ELIMINAR':\nElimina permanentemente un registro del sistema.\nIMPORTANTE: Primero debe seleccionar una fila de la tabla.\nEl sistema pedirá confirmación antes de eliminar.\nPRECAUCIÓN: Esta acción no se puede deshacer.\n\nBOTÓN 'BUSCAR':\nPermite buscar un registro específico por su ID.\nIngrese el identificador cuando se le solicite.\nSi existe, el registro se filtrará en la tabla.\n\nMÓDULOS ESPECÍFICOS:\n\n1. BUSES:\n   - ID Bus: Identificador único (ej: BUS001)\n   - Placa, Marca, Capacidad, Estado, Empresa\n\n2. CONDUCTORES:\n   - ID Conductor: Identificador único (ej: CND001)\n   - Nombre, Cédula, Licencia, Teléfono\n   - Asignación a empresa específica\n\n3. RUTAS:\n   - ID Ruta: Identificador único (ej: RUTA001)\n   - Origen, Destino, Distancia, Precio Base\n   - Tiempo Estimado y Paradas\n\n4. VIAJES:\n   - Asignación de Bus y Conductores (Principal y Relevo)\n   - Fechas y Horas de Salida/Llegada\n   - Control de Asientos Disponibles\n   - Estado: Programado, En Curso, Finalizado, Cancelado\n\n5. PASAJEROS:\n   - Registro de clientes que viajan\n   - Datos personales, discapacidad (para descuentos)\n   - Historial de contacto\n\n6. RESERVAS:\n   - Vinculación Pasajero - Viaje\n   - Selección gráfica de asientos (Mapa del Bus)\n   - Estado: Confirmada, Pendiente, Cancelada, Abordado\n\n7. FACTURAS:\n   - Emisión de comprobantes para Pasajeros\n   - Cálculo de IVA y totales\n   - Estado: Pagada, Pendiente, Cancelada\n\n8. USUARIOS (DEL SISTEMA):\n   - Personal que opera el software\n   - Tipos: \n     * Administrador: Acceso total y configuración\n     * Operador: Venta de pasajes y gestión diaria\n   - Requiere credenciales de acceso\n\n9. EMPRESAS:\n   - ID Empresa: Identificador único\n   - Nombre: Nombre de la empresa\n   - Dirección: Dirección física\n   - Teléfono: Teléfono de contacto\n   - Email: Correo electrónico");
        return new JScrollPane(texto);
    }

    private JScrollPane crearPanelConsultas() {
        JTextArea texto = new JTextArea();
        texto.setEditable(false);
        texto.setWrapStyleWord(true);
        texto.setLineWrap(true);
        texto.setMargin(new Insets(15, 15, 15, 15));
        texto.setFont(new Font("Arial", 0, 13));
        texto.setText("CONSULTAS ESPECIALES\n\nAcceda a las consultas especiales desde el menú 'Consultas' > 'Consultas Especiales'.\n\nTIPOS DE CONSULTAS DISPONIBLES:\n\n1. CONSULTAR FACTURA POR ID RESERVA:\n   Busca y muestra el detalle de la factura asociada a una reserva.\n   Parámetro requerido: ID de la Reserva\n   Útil para: Recuperar facturas cuando solo se tiene el número de reserva\n\n2. CONSULTAR FACTURA POR ID FACTURA:\n   Busca y muestra el detalle de una factura específica.\n   Parámetro requerido: ID de la Factura\n   Útil para: Reimpresión o verificación de detalles fiscales\n\n3. VIAJES POR RUTA:\n   Muestra todos los viajes programados para una ruta específica.\n   Parámetro requerido: ID de la Ruta\n   Útil para: Planificar disponibilidad por destino\n\n4. RESERVAS POR PASAJERO:\n   Muestra todas las reservas de un pasajero específico.\n   Parámetro requerido: ID del Pasajero\n   Útil para: Atención al cliente, historial de compras\n\n5. BUSES POR EMPRESA:\n   Lista todos los buses de una empresa de transporte.\n   Parámetro requerido: ID de la Empresa\n   Útil para: Gestión de flota por empresa\n\nCÓMO USAR LAS CONSULTAS:\n\n1. Seleccione el tipo de consulta del menú desplegable\n2. Si se requiere, ingrese el parámetro solicitado\n3. Presione el botón 'Consultar'\n4. Los resultados se mostrarán en el área de resultados\n5. Use 'Limpiar' para borrar los resultados y hacer una nueva consulta");
        return new JScrollPane(texto);
    }

    private JScrollPane crearPanelReportes() {
        JTextArea texto = new JTextArea();
        texto.setEditable(false);
        texto.setWrapStyleWord(true);
        texto.setLineWrap(true);
        texto.setMargin(new Insets(15, 15, 15, 15));
        texto.setFont(new Font("Arial", 0, 13));
        texto.setText("REPORTES Y ESTADÍSTICAS\n\nAcceda a los reportes desde el menú 'Reportes' > 'Reportes y Estadísticas'.\n\nREPORTES DISPONIBLES:\n\n1. RESUMEN GENERAL DEL SISTEMA:\n   Vista consolidada de todos los módulos.\n   Muestra totales de buses, rutas, viajes, reservas, facturas, usuarios y empresas.\n\n2. ESTADÍSTICAS DE VIAJES:\n   Análisis detallado del estado de los viajes.\n   Incluye porcentajes de viajes programados, en curso, finalizados y cancelados.\n\n3. ESTADÍSTICAS DE RESERVAS:\n   Información sobre reservas confirmadas, pendientes y canceladas.\n   Muestra total de asientos reservados y promedio por reserva.\n\n4. ESTADÍSTICAS DE FACTURAS:\n   Análisis financiero con montos pagados y pendientes.\n   Útil para control de ingresos y cuentas por cobrar.\n\n5. ESTADÍSTICAS DE BUSES:\n   Información sobre el estado operativo de la flota.\n   Incluye capacidad total y promedio de la flota.\n\n6. ESTADÍSTICAS DE USUARIOS:\n   Distribución del personal del sistema (Administradores vs Operadores).\n\n7. ESTADÍSTICAS DE EMPRESAS:\n   Información sobre empresas registradas.\n   Incluye cantidad de buses por empresa.\n\n8. TOP 5 RUTAS MÁS POPULARES:\n   Ranking de las rutas con mayor número de viajes.\n   Útil para decisiones de planificación y expansión.\n\n9. TOP 5 PASAJEROS MÁS ACTIVOS:\n   Pasajeros con mayor número de reservas/viajes.\n   Útil para programas de fidelización.\n\n10. INGRESOS TOTALES:\n    Reporte financiero completo.\n    Muestra ingresos recibidos, pendientes y totales esperados.\n\nCÓMO GENERAR REPORTES:\n\n1. Abra la ventana de Reportes y Estadísticas\n2. Seleccione el reporte deseado de la lista izquierda\n3. El reporte se generará automáticamente\n4. Revise los resultados en el panel derecho\n5. Use el botón 'Limpiar' para borrar el contenido antes de generar otro reporte");
        return new JScrollPane(texto);
    }

    private JScrollPane crearPanelSolucion() {
        JTextArea texto = new JTextArea();
        texto.setEditable(false);
        texto.setWrapStyleWord(true);
        texto.setLineWrap(true);
        texto.setMargin(new Insets(15, 15, 15, 15));
        texto.setFont(new Font("Arial", 0, 13));
        texto.setText("SOLUCIÓN DE PROBLEMAS\n\nPROBLEMAS COMUNES Y SOLUCIONES:\n\n1. NO PUEDO EDITAR O ELIMINAR UN REGISTRO:\n   Solución: Asegúrese de seleccionar una fila de la tabla antes de presionar\n   el botón 'Editar' o 'Eliminar'. La fila debe estar resaltada.\n\n2. NO APARECEN DATOS EN LA TABLA:\n   Solución: Presione el botón 'Todos' para cargar todos los registros.\n   Si aún no aparecen datos, es posible que no haya registros almacenados.\n\n3. ERROR AL GUARDAR UN REGISTRO:\n   Posibles causas:\n   - Campos obligatorios vacíos\n   - Formato incorrecto en campos numéricos o de fecha\n   - ID duplicado (al agregar un nuevo registro)\n   Solución: Verifique que todos los campos estén completos y correctos.\n\n4. NO PUEDO INICIAR SESIÓN:\n   Solución: Verifique que el usuario y contraseña sean correctos.\n   Si olvidó su contraseña, contacte al administrador del sistema.\n\n5. ERROR AL BUSCAR POR ID:\n   Solución: Asegúrese de escribir el ID exactamente como está registrado.\n   Los IDs distinguen entre mayúsculas y minúsculas.\n\n6. NO SE CARGAN LAS EMPRESAS EN EL FORMULARIO DE BUSES:\n   Solución: Primero debe registrar al menos una empresa en el módulo de Empresas.\n\n7. ERROR AL GENERAR REPORTES O CONSULTAS:\n   Solución: Verifique que existan datos en los módulos relacionados.\n   Algunos reportes requieren información de múltiples módulos.\n\n8. LOS DATOS NO SE ACTUALIZAN EN LA TABLA:\n   Solución: Presione el botón 'Todos' para refrescar la vista.\n\nMENSAJES DE ERROR:\n\n• 'Archivo inexistente': El sistema está creando los archivos de datos.\n  Este mensaje es normal en la primera ejecución.\n\n• 'El registro no existe': El ID buscado no se encuentra en la base de datos.\n\n• 'Error al acceder al archivo': Puede haber un problema de permisos.\n  Contacte al administrador del sistema.\n\n• 'Complete todos los campos': Hay campos obligatorios sin llenar.\n\nRECOMENDACIONES:\n\n1. Realice copias de seguridad periódicas de los archivos .dat\n2. No modifique los archivos .dat manualmente\n3. Cierre el sistema correctamente usando la opción 'Salir'\n4. Mantenga actualizada la información de contacto de los usuarios\n5. Revise periódicamente los reportes financieros\n\nCONTACTO DE SOPORTE:\n\nSi el problema persiste, contacte con el departamento de soporte técnico.\nPrepare la siguiente información:\n- Descripción detallada del problema\n- Mensaje de error (si aparece)\n- Pasos que realizó antes del error\n- Módulo donde ocurrió el problema");
        return new JScrollPane(texto);
    }
}
