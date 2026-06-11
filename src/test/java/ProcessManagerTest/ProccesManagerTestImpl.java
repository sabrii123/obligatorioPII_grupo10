package ProcessManagerTest;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProccesManagerTestImpl {
}


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