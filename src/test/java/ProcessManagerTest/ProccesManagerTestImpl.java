package ProcessManagerTest;

import org.junit.jupiter.api.Test;
import uy.edu.um.doors.ProcessManager;
import uy.edu.um.doors.ProcessManagerImpl;
import uy.edu.um.doors.exceptions.ColaVacia;
import uy.edu.um.doors.exceptions.NoHayProcesoEjecucion;
import uy.edu.um.doors.exceptions.YaHayProcesoEjecusion;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ProccesManagerTestImpl {

    // ─── HELPERS ────────────────────────────────────────────────────────────────

    private Path crearUsersCsv() throws Exception {
        Path archivo = Files.createTempFile("users", ".csv");
        String contenido = """
                uid;alias;type
                25;Zeus;ADMIN
                87;Hera;ADMIN
                314;Athena;ADMIN
                1024;Prometheus;ADMIN
                56;Poseidon;GENERIC
                219;Hades;GENERIC
                731;Apollo;GENERIC
                4086;Artemis;GENERIC
                642;Hermes;GENERIC
                98;Ares;GENERIC
                1507;Demeter;GENERIC
                333;Cronos;GENERIC
                """;
        Files.writeString(archivo, contenido);
        return archivo;
    }

    private Path crearProcessCsv() throws Exception {
        Path archivo = Files.createTempFile("process", ".csv");
        String contenido = """
                pid;uid;name;events
                51331;4086;powershell.exe;{DISK:[commit, fsync, truncate, write]# DISK:[read, write, append, commit]# RAM:[malloc, calloc, memcpy]# DISK:[append, commit]# CPU:[pop, sub, mod, inc, call, test, loop]}
                47122;642;services.exe;{DISK:[fsync, sync, sync, rename, flush, rename, fsync]# RAM:[store, free, free, fetch, alloc, memcpy, alloc]# CPU:[sub, mov]# CPU:[div, loop, jump]# CPU:[mul, pop, inc, inc, mov, xor, test]}
                19520;25;taskmgr.exe;{DISK:[append, mount, flush, close, read, sync]# CPU:[add, dec, inc, dec, inc, add, div, test]# DISK:[flush, read, unlink]# DISK:[fsync, close, close, truncate, mount, truncate, sync]}
                """;
        Files.writeString(archivo, contenido);
        return archivo;
    }

    // ─── CARGA ──────────────────────────────────────────────────────────────────

    @Test
    void cargarUsuariosYProcesosCorrectamente() throws Exception {
        ProcessManager mgr = new ProcessManagerImpl();
        assertDoesNotThrow(() ->
                mgr.loadProcessAndUserData(crearProcessCsv().toString(), crearUsersCsv().toString())
        );
    }

    // ─── PREPARAR ───────────────────────────────────────────────────────────────

    @Test
    void prepararProcesosCorrectamente() throws Exception {
        ProcessManager mgr = new ProcessManagerImpl();
        mgr.loadProcessAndUserData(crearProcessCsv().toString(), crearUsersCsv().toString());
        assertDoesNotThrow(() -> mgr.prepareProcesses());
    }

    @Test
    void prepararSinProcesosNoLanzaExcepcion() throws Exception {
        ProcessManager mgr = new ProcessManagerImpl();
        assertDoesNotThrow(() -> mgr.prepareProcesses());
    }

    // ─── EJECUTAR ───────────────────────────────────────────────────────────────

    @Test
    void ejecutarProcesoCorrectamente() throws Exception {
        ProcessManager mgr = new ProcessManagerImpl();
        mgr.loadProcessAndUserData(crearProcessCsv().toString(), crearUsersCsv().toString());
        mgr.prepareProcesses();
        assertDoesNotThrow(() -> mgr.executeNextProcess());
    }

    @Test
    void ejecutarDosProcesosAlMismoTiempoLanzaExcepcion() throws Exception {
        ProcessManager mgr = new ProcessManagerImpl();
        mgr.loadProcessAndUserData(crearProcessCsv().toString(), crearUsersCsv().toString());
        mgr.prepareProcesses();
        mgr.executeNextProcess();
        assertThrows(YaHayProcesoEjecusion.class, () -> mgr.executeNextProcess());
    }

    @Test
    void ejecutarConProcesoYaEnEjecucionLanzaExcepcion() throws Exception {
        ProcessManager mgr = new ProcessManagerImpl();
        mgr.loadProcessAndUserData(crearProcessCsv().toString(), crearUsersCsv().toString());
        mgr.prepareProcesses();
        mgr.executeNextProcess();   // ahora sí hay uno corriendo
        assertThrows(YaHayProcesoEjecusion.class, () -> mgr.executeNextProcess());
    }

    // ─── FINALIZAR OK ───────────────────────────────────────────────────────────

    @Test
    void finalizarProcesoOkCorrectamente() throws Exception {
        ProcessManager mgr = new ProcessManagerImpl();
        mgr.loadProcessAndUserData(crearProcessCsv().toString(), crearUsersCsv().toString());
        mgr.prepareProcesses();
        mgr.executeNextProcess();
        assertDoesNotThrow(() -> mgr.finishProcessOk());
    }

    @Test
    void finalizarOkSinProcesoEnEjecucionLanzaExcepcion() throws Exception {
        ProcessManager mgr = new ProcessManagerImpl();
        mgr.loadProcessAndUserData(crearProcessCsv().toString(), crearUsersCsv().toString());
        mgr.prepareProcesses();
        assertThrows(NoHayProcesoEjecucion.class, () -> mgr.finishProcessOk());
    }

    @Test
    void finalizarOkPermiteEjecutarSiguiente() throws Exception {
        ProcessManager mgr = new ProcessManagerImpl();
        mgr.loadProcessAndUserData(crearProcessCsv().toString(), crearUsersCsv().toString());
        mgr.prepareProcesses();
        mgr.executeNextProcess();
        mgr.finishProcessOk();
        assertDoesNotThrow(() -> mgr.executeNextProcess());
    }

    // ─── FINALIZAR ERROR ────────────────────────────────────────────────────────

    @Test
    void finalizarProcesoErrorCorrectamente() throws Exception {
        ProcessManager mgr = new ProcessManagerImpl();
        mgr.loadProcessAndUserData(crearProcessCsv().toString(), crearUsersCsv().toString());
        mgr.prepareProcesses();
        mgr.executeNextProcess();
        assertDoesNotThrow(() -> mgr.finishProcessError());
    }

    @Test
    void finalizarErrorSinProcesoEnEjecucionLanzaExcepcion() throws Exception {
        ProcessManager mgr = new ProcessManagerImpl();
        assertThrows(NoHayProcesoEjecucion.class, () -> mgr.finishProcessError());
    }

    @Test
    void finalizarErrorPermiteEjecutarSiguiente() throws Exception {
        ProcessManager mgr = new ProcessManagerImpl();
        mgr.loadProcessAndUserData(crearProcessCsv().toString(), crearUsersCsv().toString());
        mgr.prepareProcesses();
        mgr.executeNextProcess();
        mgr.finishProcessError();
        assertDoesNotThrow(() -> mgr.executeNextProcess());
    }

    // ─── TERMINAR POR USUARIO ───────────────────────────────────────────────────

    @Test
    void terminarProcesoCorrectamente() throws Exception {
        ProcessManager mgr = new ProcessManagerImpl();
        mgr.loadProcessAndUserData(crearProcessCsv().toString(), crearUsersCsv().toString());
        mgr.prepareProcesses();
        mgr.executeNextProcess();
        assertDoesNotThrow(() -> mgr.terminateProcess(25));
    }

    @Test
    void terminarProcesoSinProcesoEnEjecucionLanzaExcepcion() throws Exception {
        ProcessManager mgr = new ProcessManagerImpl();
        assertThrows(NoHayProcesoEjecucion.class, () -> mgr.terminateProcess(25));
    }

    // ─── PRINT STATUS ───────────────────────────────────────────────────────────

    @Test
    void printStatusNoRompe() throws Exception {
        ProcessManager mgr = new ProcessManagerImpl();
        mgr.loadProcessAndUserData(crearProcessCsv().toString(), crearUsersCsv().toString());
        mgr.prepareProcesses();
        mgr.executeNextProcess();
        assertDoesNotThrow(() -> mgr.printStatus());
    }

    @Test
    void printStatusVerboseNoRompe() throws Exception {
        ProcessManager mgr = new ProcessManagerImpl();
        mgr.loadProcessAndUserData(crearProcessCsv().toString(), crearUsersCsv().toString());
        mgr.prepareProcesses();
        mgr.executeNextProcess();
        assertDoesNotThrow(() -> mgr.printStatusVerbose());
    }
}