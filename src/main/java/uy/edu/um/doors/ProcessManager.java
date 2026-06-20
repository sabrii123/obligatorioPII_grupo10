package uy.edu.um.doors;

import uy.edu.um.doors.exceptions.*;

public interface ProcessManager {
    public static final int MAX_FINISHED = 3;
    public void loadProcessAndUserData(String processCsvPath, String usersCsvPath) throws DataLoadExeption;
    public void prepareProcesses() throws ColaVacia;
    public void executeNextProcess() throws YaHayProcesoEjecusion;
    public void finishProcessOk() throws  NoHayProcesoEjecucion;
    public void finishProcessError() throws NoHayProcesoEjecucion;
    public void terminateProcess(int uid) throws NoHayProcesoEjecucion, NoExisteUsusarioConUid;
    public void printStatus() throws ColaVacia;
    public void printStatusVerbose() throws ColaVacia;
    public void printStatusByUser(int uid);
    public void printStatusByProcess(int pid);
}
