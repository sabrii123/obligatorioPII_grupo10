package uy.edu.um.doors;

import uy.edu.um.doors.entities.Process;
import uy.edu.um.doors.exceptions.ColaVacia;
import uy.edu.um.tad.heap.MyHeap;
import uy.edu.um.tad.list.MyLinkedListImpl;
import uy.edu.um.tad.list.MyList;
import uy.edu.um.tad.queue.EmptyQueueException;
import uy.edu.um.tad.queue.MyQueue;
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
    private MyQueue<Process> newProcesses;
    private MyHeap<Process> pendingProcesses;
    private Process runningProcess;
    private MyStack<Process> finishedProcesses;
    private MyHash<Integer, User> users;
    private int MAX_FINISHED = 3; // o el valor que les pidan/definan
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
                        + " | USER:" + process.getUser().getAlias()
                        + " UID:" + process.getUser().getUid()
                        + " | P=" + process.getPriority());
            } catch (EmptyQueueException e) {
                throw new ColaVacia("e");
            }
        }
    }

    @Override
    public void executeNextProcess() throws YaHayProcesoEjecusion, YaHayProcesoEjecusion {
        if (runningProcess != null) {
            throw new YaHayProcesoEjecusion("e");
        }


        if (pendingProcesses.isEmpty()) {
            throw new YaHayProcesoEjecusion("e");
        }


        Process process = pendingProcesses.remove();


        process.setState("RUNNING");

        runningProcess = process;

        System.out.println("Ejecutando proceso PID=" + process.getPid());
    }

    @Override
    public void finishProcessOk() throws YaHayProcesoEjecusion {
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
            throw new YaHayProcesoEjecusion("e");
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
            throw new NoHayProcesoEjecucion("e");
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
            throw new NoHayProcesoEjecucion("e");
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

    @Override
    public void printStatusByUser(int uid) {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusByProcess(int pid) {
        System.out.println("IMPLEMENTAR");
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
                String[] datos = linea.split(",");
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

    private void cargarProcesos(String processCsvPath){ //poner el nombre en inglés
        try (BufferedReader br = new BufferedReader(new FileReader(processCsvPath))) {
            String linea;
            br.readLine();
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] datos = linea.split(",");
                int pid = Integer.parseInt(datos[0].trim());
                String name = datos[1].trim();
                int uid = Integer.parseInt(datos[2].trim());
                try{
                    User user = users.get(uid);
                    if (user == null){
                        throw new EntidadNoExiste("No existe un usuario con UID " + uid); //cambiar los mensajes
                    }
                    MyList<Event> events = new MyLinkedListImpl<>();
                    Process process = new Process(pid, name, user, events);
                    newProcesses.enqueue(process);
            } catch (EntidadNoExiste e) {
                System.out.println(
                        "No se pudo cargar el proceso PID ");
        } } } catch (IOException e) {
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
}


