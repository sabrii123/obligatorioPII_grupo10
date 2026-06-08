package uy.edu.um.doors;
import uy.edu.um.doors.entities.Process;
import uy.edu.um.tad.heap.MyHeap;
import uy.edu.um.tad.queue.EmptyQueueException;
import uy.edu.um.tad.queue.MyQueue;
import uy.edu.um.tad.stack.MyStack;
import uy.edu.um.doors.entities.*;

public class ProcessManagerImpl implements ProcessManager{

    //EL DISEÑO DE LA ESTRUCTURA DE ALMACENAMIENTO DEBE IMPLEMENTARSE EN ESTA CLASE EN RELACIÓN CON LAS ENTIDADES QUE DEFINA
    private MyQueue<Process> newProcesses;
    private MyHeap<Process> pendingProcesses;
    private Process runningProcess;
    private MyStack<Process> finishedProcesses;

    private static final int MAX_FINISHED = 3; // o el valor que les pidan/definan
    GestorArchivos ga= new GestorArchivos();
    @Override
    public void loadProcessAndUserData(String processCsvPath, String usersCsvPath) {
        Process proceso_nuevo = new Process();

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
