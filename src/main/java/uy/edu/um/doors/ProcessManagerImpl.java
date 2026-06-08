package uy.edu.um.doors;

import uy.edu.um.doors.entities.Process;
import uy.edu.um.tad.heap.MyHeap;
import uy.edu.um.tad.queue.EmptyQueueException;
import uy.edu.um.tad.queue.MyQueue;
import uy.edu.um.tad.stack.MyStack;
import uy.edu.um.tad.hash.MyHash;
import uy.edu.um.doors.entities.*;
import java.io.BufferedReader; //leer texto linea por linea
import java.io.FileReader; //abrir archivo para lectura
import java.io.IOException; //errores al leer archivos
import java.util.List;

public class ProcessManagerImpl implements ProcessManager{

    //EL DISEÑO DE LA ESTRUCTURA DE ALMACENAMIENTO DEBE IMPLEMENTARSE EN ESTA CLASE EN RELACIÓN CON LAS ENTIDADES QUE DEFINA
    private MyQueue<Process> newProcesses;
    private MyHeap<Process> pendingProcesses;
    private Process runningProcess;
    private MyStack<Process> finishedProcesses;
    private MyHash<Integer, User> users;

    private static final int MAX_FINISHED = 3; // o el valor que les pidan/definan
    GestorArchivos ga= new GestorArchivos();
    @Override
    public void loadProcessAndUserData(String processCsvPath, String usersCsvPath) {
        cargarUsuarios(usersCsvPath);
        cargarProcesos(processCsvPath);
    }
    private void cargarUsuarios(String usersCsvPath) {
        try (BufferedReader br = new BufferedReader(new FileReader(usersCsvPath))) {
            String linea;
            br.readLine();
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] datos = linea.split(",");
                int iud = Integer.parseInt(datos[0].trim());
                String alias = datos[1].trim();
                String type = datos[2].trim();
                User user = new User(iud, alias, type);
                users.put(iud, user);
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
                String[] datos = linea.split(",");
                int pid = Integer.parseInt(datos[0].trim());
                String name = datos[1].trim();


            }
        } catch (IOException e) {
            System.out.println("Error cargando los procesos");
        }
    }

    @Override
    public void prepareProcesses()  {
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
                } catch ( EmptyQueueException e) {
                    System.out.println("La cola de procesos nuevos está vacía");
                    break;
                }
            }
    }

    @Override
    public void executeNextProcess() {
        if (runningProcess != null) {
            System.out.println("Ya hay un proceso en ejecución");
            return;
        }


        if (pendingProcesses.isEmpty()) {
            System.out.println("No hay procesos pendientes");
            return;
        }


        Process process = pendingProcesses.remove();


        process.setState("RUNNING");

        runningProcess = process;

        System.out.println("Ejecutando proceso PID=" + process.getPid());
    }

    @Override
    public void finishProcessOk() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void finishProcessError() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void terminateProcess(int uid) {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatus() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusVerbose() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusByUser(int uid) {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusByProcess(int pid) {
        System.out.println("IMPLEMENTAR");
    }
}
