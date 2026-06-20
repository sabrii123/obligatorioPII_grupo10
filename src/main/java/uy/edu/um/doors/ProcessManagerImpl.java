package uy.edu.um.doors;

import uy.edu.um.doors.entities.Process;
import uy.edu.um.doors.exceptions.ColaVacia;
import uy.edu.um.tad.hash.MyHashImpl;
import uy.edu.um.tad.heap.MyHeap;
import uy.edu.um.tad.heap.MyHeapImpl;
import uy.edu.um.tad.list.MyLinkedListImpl;
import uy.edu.um.tad.list.MyList;
import uy.edu.um.tad.queue.EmptyQueueException;
import uy.edu.um.tad.queue.MyQueue;
import uy.edu.um.tad.queue.MyQueueImpl;
import uy.edu.um.tad.stack.EmptyStackException;
import uy.edu.um.tad.stack.MyStack;
import uy.edu.um.tad.hash.MyHash;
import uy.edu.um.doors.entities.*;
import uy.edu.um.tad.stack.MyStackImpl;
import uy.edu.um.doors.exceptions.*;

import java.io.BufferedReader; //leer texto linea por linea
import java.io.FileReader; //abrir archivo para lectura
import java.io.IOException; //errores al leer archivos
import java.util.List;

public class ProcessManagerImpl implements ProcessManager {

    //EL DISEÑO DE LA ESTRUCTURA DE ALMACENAMIENTO DEBE IMPLEMENTARSE EN ESTA CLASE EN RELACIÓN CON LAS ENTIDADES QUE DEFINA
    private final MyQueue<Process> newProcesses;
    private final MyHeap<Process> pendingProcesses;
    private Process runningProcess;
    private final MyStack<Process> finishedProcesses;
    private final MyHash<Integer, User> users;

    public ProcessManagerImpl() {
        this.newProcesses = new MyQueueImpl<>();
        this.pendingProcesses = new MyHeapImpl<>(false);
        this.finishedProcesses = new MyStackImpl<>();
        this.users = new MyHashImpl<>();
        this.runningProcess = null;
    }
    //private int MAX_FINISHED = 7; // o el valor que les pidan/definan
    GestorArchivos ga = new GestorArchivos();




    @Override
    public void loadProcessAndUserData(String processCsvPath, String usersCsvPath) {
        cargarUsuarios(usersCsvPath);
        cargarProcesos(processCsvPath);
    }


    @Override
    public void prepareProcesses() throws ColaVacia {
        while (!newProcesses.isEmpty()) {
            try {
                Process process = newProcesses.dequeue();

                int priority = process.calculatePriority();
                process.setPriority(priority);
                process.setState("PENDING");

                pendingProcesses.insert(process);

                ga.escribirLog("NEW PENDING PROCESS: PID=" + process.getPid()
                        + " | " + process.getName()
                        + " | USER:" + process.getUser().getAlias() + " UID:" + process.getUser().getUid()
                        + " | P=" + process.getPriority());
            } catch (EmptyQueueException e) {
                throw new ColaVacia("La cola de procesos nuevos está vacía");
            }
        }
    }

    @Override
    public void executeNextProcess() throws YaHayProcesoEjecusion, ColaVacia {
        if (runningProcess != null) {
            throw new YaHayProcesoEjecusion("Ya hay un proceso en ejecución (PID=" + runningProcess.getPid() + ").");
        }
        if (pendingProcesses.isEmpty()) {
            throw new ColaVacia("No hay procesos pendientes para ejecutar.");
        }

        Process process = pendingProcesses.remove();
        process.setState("RUNNING");
        runningProcess = process;

        System.out.println("Ejecutando proceso PID=" + process.getPid());

        StringBuilder bloque = new StringBuilder();
        bloque.append("EXECUTING PROCESS: PID=" + process.getPid()
                + " | USER:" + process.getUser().getAlias()
                + " UID:" + process.getUser().getUid());

        for (int i = 0; i < process.getEvents().size(); i++) {
            Event event = process.getEvents().get(i);
            bloque.append(System.lineSeparator());
            bloque.append("EVENT: " + event.getType() + " | Instructions [");
            for (int j = 0; j < event.getInstructions().size(); j++) {
                bloque.append(event.getInstructions().get(j));
                if (j < event.getInstructions().size() - 1) {
                    bloque.append(", ");
                }
            }
            bloque.append("]");
        }

        ga.escribirLog(bloque.toString());
    }

    @Override
    public void finishProcessOk() throws NoHayProcesoEjecucion {
        if (runningProcess != null) {
            runningProcess.setState("FINISHED");
            runningProcess.setFinishType("OK");
            ga.escribirLog("ENDING PROCESS: PID=" + runningProcess.getPid()
                    + " | STATE: " + runningProcess.getFinishType());
            if (finishedProcesses.size() == MAX_FINISHED) {
                ga.escribirLog("Finished process stack overflow");
                while (true) {
                    try {
                        Process finished = finishedProcesses.pop();
                        ga.escribirLog("PID=" + finished.getPid()
                                + " " + finished.getName()
                                + " | STATE: " + finished.getFinishType()
                                + " | USER:" + finished.getUser().getAlias()
                                + " UID:" + finished.getUser().getUid());
                    } catch (Exception e) {
                        break;
                    }
                }
            }
            finishedProcesses.push(runningProcess);
            runningProcess = null;
        } else {
            throw new NoHayProcesoEjecucion("No hay ningún proceso en ejecución para finalizar");
        }
    }

    @Override
    public void finishProcessError() throws NoHayProcesoEjecucion {
        if (runningProcess != null) {
            runningProcess.setState("FINISHED");
            runningProcess.setFinishType("ERROR");

            ga.escribirLog("ENDING PROCESS: PID=" + runningProcess.getPid()
                    + " | STATE: " + runningProcess.getFinishType());

            if (finishedProcesses.size() == MAX_FINISHED) {
                ga.escribirLog("Finished process stack overflow");
                while (true) {
                    try {
                        Process finished = finishedProcesses.pop();
                        ga.escribirLog("PID=" + finished.getPid()
                                + " " + finished.getName()
                                + " | STATE: " + finished.getFinishType()
                                + " | USER:" + finished.getUser().getAlias()
                                + " UID:" + finished.getUser().getUid());
                    } catch (Exception e) {
                        break;
                    }
                }
            }

            finishedProcesses.push(runningProcess);
            runningProcess = null;

        } else {
            throw new NoHayProcesoEjecucion("No hay ningún proceso en ejecución para finalizar");
        }

    }

    @Override
    public void terminateProcess(int uid) throws NoHayProcesoEjecucion {
        if (runningProcess != null) {
            User responsibleUser = getUser(uid);
            if (responsibleUser == null) {
                System.out.println("No existe usuario con UID: " + uid);
                return;
            }
            runningProcess.setState("FINISHED");
            runningProcess.setFinishType("TERMINATED");
            ga.escribirLog("ENDING PROCESS: PID=" + runningProcess.getPid()
                    + " | STATE: TERMINATED by USER:" + responsibleUser.getAlias()
                    + " UID:" + responsibleUser.getUid());
            if (finishedProcesses.size() == MAX_FINISHED) {
                ga.escribirLog("Finished process stack overflow");
                while (true) {
                    try {
                        Process finished = finishedProcesses.pop();
                        ga.escribirLog("PID=" + finished.getPid()
                                + " " + finished.getName()
                                + " | STATE: " + finished.getFinishType()
                                + " | USER:" + finished.getUser().getAlias()
                                + " UID:" + finished.getUser().getUid());
                    } catch (Exception e) {
                        break;
                    }
                }
            }
            finishedProcesses.push(runningProcess);
            runningProcess = null;
        } else {
            throw new NoHayProcesoEjecucion("No hay ningún proceso en ejecución para terminar");
        }
    }

    @Override
    public void printStatus() throws ColaVacia {
        System.out.println("PROCESS STATUS");

        // EXECUTING
        System.out.println("EXECUTING:");
        if (runningProcess != null) {
            System.out.println("\tPID=" + runningProcess.getPid()
                    + " | " + runningProcess.getName()
                    + " | USER:" + runningProcess.getUser().getAlias()
                    + " UID:" + runningProcess.getUser().getUid()
                    + " | P=" + runningProcess.getPriority());
        }

        // PENDING - heap sin destruirlo usando el array interno
        System.out.println("PENDING:");
        for (int i = 0; i < pendingProcesses.size(); i++) {
            try {
                Process p = pendingProcesses.remove();
                System.out.println("\tPID=" + p.getPid()
                        + " | " + p.getName()
                        + " | USER:" + p.getUser().getAlias()
                        + " UID:" + p.getUser().getUid()
                        + " | P=" + p.getPriority());
                pendingProcesses.insert(p);
            } catch (Exception e) {
                break;
            }
        }

        // FINISHED - iteramos el stack sin destruirlo
        System.out.println("FINISHED:");
        MyStackImpl<Process> aux = new MyStackImpl<>();

        while (true) {
            try {
                Process p = finishedProcesses.pop();
                System.out.println("PID=" + p.getPid()
                        + " " + p.getName()
                        + " | STATE: " + p.getFinishType()
                        + " | USER:" + p.getUser().getAlias()
                        + " UID:" + p.getUser().getUid());
                aux.push(p);
            } catch (Exception e) {
                break;
            }
        }
        while (true) {
            try {
                finishedProcesses.push(aux.pop());
            } catch (Exception e) {
                break;
            }
        }
    }

    @Override
    public void printStatusVerbose() throws ColaVacia {
        System.out.println("PROCESS STATUS");

        // EXECUTING
        System.out.println("EXECUTING:");
        if (runningProcess != null) {
            System.out.println("\tPID=" + runningProcess.getPid()
                    + " | " + runningProcess.getName()
                    + " | USER:" + runningProcess.getUser().getAlias()
                    + " UID:" + runningProcess.getUser().getUid()
                    + " | P=" + runningProcess.getPriority());
            printEvents(runningProcess);
        }

        // PENDING
        System.out.println("PENDING:");
        for (int i = 0; i < pendingProcesses.size(); i++) {
            try {
                Process p = pendingProcesses.remove();
                System.out.println("\tPID=" + p.getPid()
                        + " | " + p.getName()
                        + " | USER:" + p.getUser().getAlias()
                        + " UID:" + p.getUser().getUid()
                        + " | P=" + p.getPriority());
                printEvents(p);
                pendingProcesses.insert(p);
            } catch (Exception e) {
                break;
            }
        }

        // FINISHED
        System.out.println("FINISHED:");
        MyStackImpl<Process> aux = new MyStackImpl<>();
        while (true) {
            try {
                Process p = finishedProcesses.pop();
                System.out.println("\tPID=" + p.getPid()
                        + " " + p.getName()
                        + " | STATE: " + p.getFinishType()
                        + " | USER:" + p.getUser().getAlias()
                        + " UID:" + p.getUser().getUid());
                printEvents(p);
                aux.push(p);

            } catch (Exception e) {
                break;

            }
        }

        try {
            while (!aux.isEmpty()) {
                finishedProcesses.push(aux.pop());
            }
        } catch (EmptyStackException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void printStatusByUser(int uid) {
        User user = getUser(uid);
        if (user == null) {
            throw new EntidadNoExiste(
                    "No existe el usuario con UID = " + uid);
        }
        boolean estado = false;
        System.out.println("PROCESS STATUS BY USER");
        System.out.println(
                "USER: " + user.getAlias()
                        + " | UID: " + user.getUid());
        // NEW
        boolean estadoNew = false;
        for (int i = 0; i < newProcesses.size(); i++) {
            Process process = newProcesses.get(i);
            if (process.getUser().getUid() == uid) {
                if (!estadoNew) {
                    System.out.println("NEW:");
                    estadoNew = true;
                }
                System.out.println(
                        "PID=" + process.getPid()
                                + " | " + process.getName()
                                + " | USER: " + process.getUser().getAlias()
                                + " | UID: " + process.getUser().getUid()
                                + " | STATE: " + process.getState());
                estado = true;
            }
        }
        // RUNNING
        if (runningProcess != null
                && runningProcess.getUser().getUid() == uid) {
            System.out.println("EXECUTING:");
            System.out.println(
                    "PID=" + runningProcess.getPid()
                            + " | " + runningProcess.getName()
                            + " | USER: " + runningProcess.getUser().getAlias()
                            + " | UID: " + runningProcess.getUser().getUid()
                            + " | STATE: " + runningProcess.getState()
                            + " | P=" + runningProcess.getPriority()
            );
            estado = true;
        }
        // PENDING
        MyHeap<Process> heapAuxiliar = new MyHeapImpl<>(false);
        int cantEstadosPending = pendingProcesses.size();
        boolean estadoPending = false;
        for (int i = 0; i < cantEstadosPending; i++) {
            Process process = pendingProcesses.remove();
            if (process.getUser().getUid() == uid) {
                if (!estadoPending) {
                    System.out.println("PENDING:");
                    estadoPending = true;
                }
                System.out.println(
                        "PID=" + process.getPid()
                                + " | " + process.getName()
                                + " | USER: " + process.getUser().getAlias()
                                + " | UID: " + process.getUser().getUid()
                                + " | STATE: " + process.getState()
                                + " | P=" + process.getPriority());
                estado = true;
            }
            heapAuxiliar.insert(process);
        }
        while (!heapAuxiliar.isEmpty()) {
            Process process = heapAuxiliar.remove();
            pendingProcesses.insert(process);
        }
        // FINISHED
        MyStack<Process> stackAuxiliar = new MyStackImpl<>();
        int cantEstadosFinished = finishedProcesses.size();
        boolean estadoFinished = false;
        try {
            for (int i = 0; i < cantEstadosFinished; i++) {
                Process process = finishedProcesses.pop();
                if (process.getUser().getUid() == uid) {
                    if (!estadoFinished) {
                        System.out.println("FINISHED:");
                        estadoFinished = true;
                    }
                    System.out.println(
                            "PID=" + process.getPid()
                                    + " | " + process.getName()
                                    + " | USER: " + process.getUser().getAlias()
                                    + " | UID: " + process.getUser().getUid()
                                    + " | STATE: " + process.getFinishType());
                    estado = true;
                }
                stackAuxiliar.push(process);
            }
            while (!stackAuxiliar.isEmpty()) {
                finishedProcesses.push(stackAuxiliar.pop());
            }
        } catch (EmptyStackException e) {
            System.out.println("Error al recorrer los procesos finalizados.");
        }
        if (!estado) {
            throw new EntidadNoExiste("No existen procesos cargados en memoria para el usuario con UID = " + uid);
        }
    }

    @Override
    public void printStatusByProcess(int pid){
        System.out.println("PROCESS STATUS BY PID");
        boolean encontrado = false;
        //NEW
        for (int i=0; i< newProcesses.size(); i++){
            Process process = newProcesses.get(i);
            if (process.getPid()==pid){
                System.out.println(
                        "PID=" + process.getPid()
                                + " | " + process.getName()
                                + " | USER:" + process.getUser().getAlias()
                                + " UID:" + process.getUser().getUid()
                                + " | STATE: " + process.getState());
                System.out.println(" ");
                printEvents(process);
                encontrado = true;
            }
        }
        //RUNNING
        if (!encontrado && runningProcess!= null && runningProcess.getPid()==pid){
            System.out.println(
                    "PID=" + runningProcess.getPid()
                            + " | " + runningProcess.getName()
                            + " | USER:" +runningProcess.getUser().getAlias()
                            + " UID:" + runningProcess.getUser().getUid()
                            + " | STATE: " + runningProcess.getState()
                            + " | P=" + runningProcess.getPriority());
            System.out.println(" ");
            printEvents(runningProcess);
            encontrado=true;
        }
        //PENDING
        if (!encontrado) {
            MyHeap<Process> heapAuxiliar = new MyHeapImpl<>(false);
            int cantidadEstadoPending=pendingProcesses.size();
            for (int i =0;i<cantidadEstadoPending;i++){
                Process process = pendingProcesses.remove();
                if (process.getPid()==pid){
                    System.out.print(
                            "PID=" + process.getPid()
                                    + " | " + process.getName()
                                    + " | USER:" + process.getUser().getAlias()
                                    + " UID:" + process.getUser().getUid()
                                    + " | STATE: " + process.getState()
                                    + " | P=" + process.getPriority());
                    System.out.println(" ");
                    printEvents(process);
                    encontrado=true;
                }
                heapAuxiliar.insert(process);
            }
            while(!heapAuxiliar.isEmpty()){
                Process process=heapAuxiliar.remove();
                pendingProcesses.insert(process);
            }
        }
        //FINISHED
        if(!encontrado){
            MyStack<Process> stackAuxiliar= new MyStackImpl<>();
            int cantEstadoFinished=finishedProcesses.size();
            try{
                for(int i=0;i<cantEstadoFinished;i++){
                    Process process= finishedProcesses.pop();
                    if(process.getPid()==pid){
                        System.out.println(
                                "PID=" + process.getPid()
                                        + " | " + process.getName()
                                        + " | USER:" + process.getUser().getAlias()
                                        + " UID:" + process.getUser().getUid()
                                        + " | STATE: " + process.getFinishType());
                        System.out.println(" ");
                        printEvents(process);
                        encontrado=true;
                    }
                    stackAuxiliar.push(process);
                }
                while(!stackAuxiliar.isEmpty()){
                    Process process=stackAuxiliar.pop();
                    finishedProcesses.push(process);
                }
            } catch(EmptyStackException e){
                System.out.println(
                        "Error al recorrer los procesos finalizados.");
            }
        }
        if (!encontrado){
            throw new EntidadNoExiste("No existe un proceso cargado en memoria con PID = " + pid);}
    }

    /// FUNCIONES AUXILIARES
    private void cargarUsuarios(String usersCsvPath) {
        try (BufferedReader br = new BufferedReader(new FileReader(usersCsvPath))) {
            String linea;
            br.readLine();
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] datos = linea.split(";");
                int uid = Integer.parseInt(datos[0].trim());
                String alias = datos[1].trim();
                String type = datos[2].trim();
                User user = new User(uid, alias, type);
                users.put(uid, user);
            }
        } catch (IOException e) {
            System.out.println("Error cargando los usuarios");
        }
    }

    private void cargarProcesos(String processCsvPath){
        try (BufferedReader br = new BufferedReader(new FileReader(processCsvPath))) {
            String linea;
            br.readLine();
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] datos = linea.split(";", 4);

                int pid = Integer.parseInt(datos[0].trim());
                int uid = Integer.parseInt(datos[1].trim());
                String name = datos[2].trim();

                String eventosTexto = datos[3].trim();
                MyList<Event> events = convertirStringAEventos(eventosTexto);

                User user = users.get(uid);

                Process process = new Process(pid, name, user, events);
                newProcesses.enqueue(process);
            }
        } catch (IOException e) {
            System.out.println("Error cargando los procesos");
        }

    }

    private User getUser(int uid) {
        if (users.contains(uid)) {
            return users.get(uid);
        }
        System.out.println("No existe usuario con UID: " + uid);
        return null;
    }

    private MyList<Event> convertirStringAEventos(String eventosTexto) {
        MyList<Event> events = new MyLinkedListImpl<>();
        // Sacamos las llaves { }
        eventosTexto = eventosTexto.replace("{", "").replace("}", "");
        // Separamos cada evento por #
        String[] eventosSeparados = eventosTexto.split("#");
        for (String eventoStr : eventosSeparados) {
            if (eventoStr.trim().isEmpty()) {
                continue;
            }
            // Ejemplo eventoStr: DISK:[commit,write]
            String[] partes = eventoStr.split(":");
            String tipo = partes[0].trim();
            String instruccionesTexto = partes[1]
                    .replace("[", "")
                    .replace("]", "")
                    .trim();
            String[] instruccionesSeparadas = instruccionesTexto.split(",");
            MyList<String> instructions = new MyLinkedListImpl<>();
            for (String instruccion : instruccionesSeparadas) {
                if (!instruccion.trim().isEmpty()) {
                    instructions.add(instruccion.trim());
                }
            }
            Event event = new Event(tipo, instructions);
            events.add(event);
        }
        return events;
    }

    private void printEvents(Process p) {
        for (int i = 0; i < p.getEvents().size(); i++) {
            Event event = p.getEvents().get(i);
            String instructions = "";
            for (int j = 0; j < event.getInstructions().size(); j++) {
                if (j > 0) {
                    instructions += ", ";
                }
                instructions += event.getInstructions().get(j);
            }
            System.out.println("\t\tEVENT: " + event.getType()
                    + " | Instructions [" + instructions + "]");
        }
    }

}


