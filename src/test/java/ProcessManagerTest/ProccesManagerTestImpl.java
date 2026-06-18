//
//package ProcessManagerTest;
//
//import org.junit.jupiter.api.Test;
//import uy.edu.um.doors.ProcessManager;
//import uy.edu.um.doors.ProcessManagerImpl;
//import uy.edu.um.doors.exceptions.ColaVacia;
//import uy.edu.um.doors.exceptions.NoHayProcesoEjecucion;
//import uy.edu.um.doors.exceptions.NoExisteUsusarioConUid;
//import uy.edu.um.doors.exceptions.YaHayProcesoEjecusion;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class ProccesManagerTestImpl {
//
//    private Path crearUsersCsv() throws Exception {
//        Path archivo = Files.createTempFile("users", ".csv");
//
//        String contenido = """
//                uid;alias;type
//                25;Zeus;ADMIN
//                10;Atenea;GENERIC
//                """;
//
//        Files.writeString(archivo, contenido);
//        return archivo;
//    }
//
//    private Path crearProcessCsv() throws Exception {
//        Path archivo = Files.createTempFile("process", ".csv");
//
//        String contenido = """
//                pid;name;uid
//                1;powershell.exe;25
//                2;chrome.exe;10
//                3;java.exe;25
//                """;
//
//        Files.writeString(archivo, contenido);
//        return archivo;
//    }
//
//    @Test
//    void cargarUsuariosYProcesosCorrectamente() throws Exception {
//        ProcessManager mgr = new ProcessManagerImpl();
//
//        Path processCsv = crearProcessCsv();
//        Path usersCsv = crearUsersCsv();
//
//        assertDoesNotThrow(() ->
//                mgr.loadProcessAndUserData(processCsv.toString(), usersCsv.toString())
//        );
//    }
//
//    @Test
//    void prepararProcesosCorrectamente() throws Exception {
//        ProcessManager mgr = new ProcessManagerImpl();
//
//        Path processCsv = crearProcessCsv();
//        Path usersCsv = crearUsersCsv();
//
//        mgr.loadProcessAndUserData(processCsv.toString(), usersCsv.toString());
//
//        assertDoesNotThrow(() ->
//                mgr.prepareProcesses()
//        );
//    }
//
//    @Test
//    void ejecutarProcesoCorrectamente() throws Exception {
//        ProcessManager mgr = new ProcessManagerImpl();
//
//        Path processCsv = crearProcessCsv();
//        Path usersCsv = crearUsersCsv();
//
//        mgr.loadProcessAndUserData(processCsv.toString(), usersCsv.toString());
//        mgr.prepareProcesses();
//
//        assertDoesNotThrow(() ->
//                mgr.executeNextProcess()
//        );
//    }
//
//    @Test
//    void ejecutarProcesoSinPrepararLanzaExcepcion() throws Exception {
//        ProcessManager mgr = new ProcessManagerImpl();
//
//        Path processCsv = crearProcessCsv();
//        Path usersCsv = crearUsersCsv();
//
//        mgr.loadProcessAndUserData(processCsv.toString(), usersCsv.toString());
//
//        assertThrows(YaHayProcesoEjecusion.class,
//                () -> mgr.executeNextProcess()
//        );
//    }
//
//    @Test
//    void noPuedeEjecutarDosProcesosAlMismoTiempo() throws Exception {
//        ProcessManager mgr = new ProcessManagerImpl();
//
//        Path processCsv = crearProcessCsv();
//        Path usersCsv = crearUsersCsv();
//
//        mgr.loadProcessAndUserData(processCsv.toString(), usersCsv.toString());
//        mgr.prepareProcesses();
//
//        mgr.executeNextProcess();
//
//        assertThrows(YaHayProcesoEjecusion.class,
//                () -> mgr.executeNextProcess()
//        );
//    }
//
//    @Test
//    void finalizarProcesoOkCorrectamente() throws Exception {
//        ProcessManager mgr = new ProcessManagerImpl();
//
//        Path processCsv = crearProcessCsv();
//        Path usersCsv = crearUsersCsv();
//
//        mgr.loadProcessAndUserData(processCsv.toString(), usersCsv.toString());
//        mgr.prepareProcesses();
//        mgr.executeNextProcess();
//
//        assertDoesNotThrow(() ->
//                mgr.finishProcessOk()
//        );
//    }
//
//    @Test
//    void finalizarProcesoErrorCorrectamente() throws Exception {
//        ProcessManager mgr = new ProcessManagerImpl();
//
//        Path processCsv = crearProcessCsv();
//        Path usersCsv = crearUsersCsv();
//
//        mgr.loadProcessAndUserData(processCsv.toString(), usersCsv.toString());
//        mgr.prepareProcesses();
//        mgr.executeNextProcess();
//
//        assertDoesNotThrow(() ->
//                mgr.finishProcessError()
//        );
//    }
//
//    @Test
//    void finalizarErrorSinProcesoEnEjecucionLanzaExcepcion() {
//        ProcessManager mgr = new ProcessManagerImpl();
//
//        assertThrows(NoHayProcesoEjecucion.class,
//                () -> mgr.finishProcessError()
//        );
//    }
//
//    @Test
//    void terminarProcesoPorUsuarioCorrectamente() throws Exception {
//        ProcessManager mgr = new ProcessManagerImpl();
//
//        Path processCsv = crearProcessCsv();
//        Path usersCsv = crearUsersCsv();
//
//        mgr.loadProcessAndUserData(processCsv.toString(), usersCsv.toString());
//        mgr.prepareProcesses();
//        mgr.executeNextProcess();
//
//        assertDoesNotThrow(() ->
//                mgr.terminateProcess(25)
//        );
//    }
//
//    @Test
//    void terminarProcesoSinProcesoEnEjecucionLanzaExcepcion() {
//        ProcessManager mgr = new ProcessManagerImpl();
//
//        assertThrows(NoHayProcesoEjecucion.class,
//                () -> mgr.terminateProcess(25)
//        );
//    }
//
//    @Test
//    void printStatusNoRompe() throws Exception {
//        ProcessManager mgr = new ProcessManagerImpl();
//
//        Path processCsv = crearProcessCsv();
//        Path usersCsv = crearUsersCsv();
//
//        mgr.loadProcessAndUserData(processCsv.toString(), usersCsv.toString());
//        mgr.prepareProcesses();
//        mgr.executeNextProcess();
//
//        assertDoesNotThrow(() ->
//                mgr.printStatus()
//        );
//    }
//
//    @Test
//    void printStatusVerboseNoRompe() throws Exception {
//        ProcessManager mgr = new ProcessManagerImpl();
//
//        Path processCsv = crearProcessCsv();
//        Path usersCsv = crearUsersCsv();
//
//        mgr.loadProcessAndUserData(processCsv.toString(), usersCsv.toString());
//        mgr.prepareProcesses();
//        mgr.executeNextProcess();
//
//        assertDoesNotThrow(() ->
//                mgr.printStatusVerbose()
//        );
//    }
//}


//ej de lo que hay que hacer
//package uy.edu.um.buquebus.service;
//
//import org.junit.jupiter.api.Test;
//import uy.edu.um.buquebus.exceptions.*;
//        import uy.edu.um.buquebus.model.Cliente;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class BuquebusMgrImplTest {
//
//    @Test
//    void agregarClienteFidelizadoCorrectamente() throws Exception {
//        BuquebusMgr mgr = new BuquebusMgrImpl();
//        assertDoesNotThrow(() -> mgr.agregarClienteFidelizado(123L, "Ana", true));
//    }
//
//    @Test
//    void agregarClienteDuplicadoLanzaEntidadYaExiste() throws Exception {
//        BuquebusMgr mgr = new BuquebusMgrImpl();
//        mgr.agregarClienteFidelizado(123L, "Ana", true);
//        assertThrows(EntidadYaExiste.class,
//                () -> mgr.agregarClienteFidelizado(123L, "Ana repetida", true));
//    }
//
//    @Test
//    void agregarClienteConDatosInvalidosLanzaInformacionInvalida() {
//        BuquebusMgr mgr = new BuquebusMgrImpl();
//        assertThrows(InformacionEntidadInvalida.class,
//                () -> mgr.agregarClienteFidelizado(null, "Ana", true));
//        assertThrows(InformacionEntidadInvalida.class,
//                () -> mgr.agregarClienteFidelizado(123L, "", true));
//        assertThrows(InformacionEntidadInvalida.class,
//                () -> mgr.agregarClienteFidelizado(123L, "Ana", false));
//    }
//
//    @Test
//    void crearTrayectoCorrectamente() {
//        BuquebusMgr mgr = new BuquebusMgrImpl();
//        assertDoesNotThrow(() -> mgr.crearTrayecto("T1", "Montevideo", "Buenos Aires"));
//    }
//
//    @Test
//    void crearTrayectoDuplicadoLanzaEntidadYaExiste() throws Exception {
//        BuquebusMgr mgr = new BuquebusMgrImpl();
//        mgr.crearTrayecto("T1", "Montevideo", "Buenos Aires");
//        assertThrows(EntidadYaExiste.class,
//                () -> mgr.crearTrayecto("T1", "Montevideo", "Buenos Aires"));
//    }
//
//    @Test
//    void postularClienteNoFidelizadoLanzaExcepcion() throws Exception {
//        BuquebusMgr mgr = new BuquebusMgrImpl();
//        mgr.crearTrayecto("T1", "Montevideo", "Buenos Aires");
//        assertThrows(ClienteNoFidelizado.class,
//                () -> mgr.postularClienteParaActualizacion(999L, "T1"));
//    }
//
//    @Test
//    void postularATrayectoInexistenteLanzaExcepcion() throws Exception {
//        BuquebusMgr mgr = new BuquebusMgrImpl();
//        mgr.agregarClienteFidelizado(123L, "Ana", true);
//        assertThrows(TrayectoNoExiste.class,
//                () -> mgr.postularClienteParaActualizacion(123L, "NO_EXISTE"));
//    }
//
//    @Test
//    void obtenerClientesRespetaCincuentaPorCientoYFifo() throws Exception {
//        BuquebusMgr mgr = new BuquebusMgrImpl();
//        mgr.crearTrayecto("T1", "Montevideo", "Buenos Aires");
//        mgr.agregarClienteFidelizado(1L, "Primero", true);
//        mgr.agregarClienteFidelizado(2L, "Segundo", true);
//        mgr.agregarClienteFidelizado(3L, "Tercero", true);
//
//        mgr.postularClienteParaActualizacion(1L, "T1");
//        mgr.postularClienteParaActualizacion(2L, "T1");
//        mgr.postularClienteParaActualizacion(3L, "T1");
//
//        List<Cliente> asignados = mgr.obtenerClientesParaActualizacion("T1", 4);
//
//        assertEquals(2, asignados.size());
//        assertEquals(1L, asignados.get(0).getNroPasaporte());
//        assertEquals(2L, asignados.get(1).getNroPasaporte());
//    }
//
//    @Test
//    void obtenerTrayectoInexistenteLanzaExcepcion() {
//        BuquebusMgr mgr = new BuquebusMgrImpl();
//        assertThrows(TrayectoNoExiste.class,
//                () -> mgr.obtenerClientesParaActualizacion("NO_EXISTE", 4));
//    }
//
//    @Test
//    void clienteSinUpgradesLuegoDeDosAsignaciones() throws Exception {
//        BuquebusMgr mgr = new BuquebusMgrImpl();
//        mgr.agregarClienteFidelizado(1L, "Ana", true);
//        mgr.crearTrayecto("T1", "Montevideo", "Buenos Aires");
//        mgr.crearTrayecto("T2", "Buenos Aires", "Montevideo");
//
//        mgr.postularClienteParaActualizacion(1L, "T1");
//        mgr.obtenerClientesParaActualizacion("T1", 2);
//
//        mgr.postularClienteParaActualizacion(1L, "T2");
//        mgr.obtenerClientesParaActualizacion("T2", 2);
//
//        assertThrows(ClienteSinUpgradesDisponibles.class,
//                () -> mgr.postularClienteParaActualizacion(1L, "T1"));
//    }
//}