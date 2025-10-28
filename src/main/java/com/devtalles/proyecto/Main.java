package com.devtalles.proyecto;

import com.devtalles.proyecto.log.model.LogEntry;
import com.devtalles.proyecto.log.model.LogSummary;
import com.devtalles.proyecto.log.service.LogProcessorTask;
import com.devtalles.proyecto.log.service.LogService;

import java.io.File;
import java.util.List;
import java.util.concurrent.*;


public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Iniciando análisis de logs...");

        File logsFolder = new File("logs");
        File[] logFiles = logsFolder.listFiles((dir, name) -> name.endsWith(".log"));

        if (logFiles == null || logFiles.length == 0) {
            System.out.println("⚠️ No se encontraron archivos .log en la carpeta 'logs'. Asegúrate de crearla y poner archivos dentro.");
            return;
        }

        LogService service = new LogService();

        ExecutorService executorService = Executors.newCachedThreadPool();

        for (File logFile : logFiles) {
            try{
            List<LogEntry> entries = service.readLogsFromFile(logFile.getAbsolutePath());
            Callable<LogSummary> task = new LogProcessorTask(entries);
            Future<LogSummary> result = executorService.submit(task);
            System.out.println(result.get().toString() + "\n");
            System.out.println("📈 Tarea Completada");

            } catch (ExecutionException | InterruptedException e){
                System.out.println(e.getMessage());
                executorService.shutdown();
            }
        }

        executorService.shutdown();





    }
}