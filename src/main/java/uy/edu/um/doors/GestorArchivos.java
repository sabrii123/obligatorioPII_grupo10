package uy.edu.um.doors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GestorArchivos {

    public void escribirLog(String mensaje) {
        try {
            String fecha = LocalDate.now().toString();

            Path rutaLog = Path.of("DOORS_PROCESS_LOG_" + fecha + ".txt");

            DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = LocalDateTime.now().format(formato);

            BufferedWriter bw = Files.newBufferedWriter(
                    rutaLog,
                    StandardCharsets.UTF_8,//sirve para escribir bien tildes y demas
                    StandardOpenOption.CREATE,//si no existe el archivo lo crea
                    StandardOpenOption.APPEND//cuando el archivo ya existe escribe al final
            );

            bw.write("[" + timestamp + "]: " + mensaje);
            bw.newLine();

            bw.close();

        } catch (IOException e) {
            System.out.println("Error escribiendo en el log");
        }
    }
}
