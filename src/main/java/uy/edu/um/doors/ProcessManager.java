package uy.edu.um.doors;

import uy.edu.um.doors.exceptions.ColaVacia;
import uy.edu.um.doors.exceptions.NoHayProcesoEjecucion;
import uy.edu.um.doors.exceptions.YaHayProcesoEjecusion;

public interface ProcessManager {
    public static final int MAX_FINISHED_PROCESS_ON_RAM = 3;
    public void loadProcessAndUserData(String processCsvPath, String usersCsvPath);
    public void prepareProcesses() throws ColaVacia;
    public void executeNextProcess() throws YaHayProcesoEjecusion;
    public void finishProcessOk() throws YaHayProcesoEjecusion;
    public void finishProcessError() throws NoHayProcesoEjecucion;
    public void terminateProcess(int uid) throws NoHayProcesoEjecucion;
    public void printStatus() throws ColaVacia;
    public void printStatusVerbose() throws ColaVacia;
    public void printStatusByUser(int uid);
    public void printStatusByProcess(int pid);
}
