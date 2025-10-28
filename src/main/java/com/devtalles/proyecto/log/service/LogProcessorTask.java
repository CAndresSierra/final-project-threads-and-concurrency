package com.devtalles.proyecto.log.service;

import com.devtalles.proyecto.log.model.LogEntry;
import com.devtalles.proyecto.log.model.LogSummary;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogProcessorTask implements Callable<LogSummary> {
     private final List<LogEntry> logEntries;

    public LogProcessorTask(List<LogEntry> logEntries) {
        this.logEntries = logEntries;
    }

    @Override
    public LogSummary call() throws Exception {

        System.out.println("Procesando: " + logEntries.size() + " " +
                "🚀 Entradas de log en hilo " + Thread.currentThread().getName() + "\n");

        List<LogEntry> errorList = logEntries.stream().filter( entry -> entry.getStatusCode() >= 400).toList();

        int totalEntries = logEntries.size();

        int errorCount = (int) logEntries.stream().filter(entry -> entry.getStatusCode() >= 400).count();

        Set<String> uniqueUsers = logEntries.stream()
                .map(LogEntry::getUser)
                .collect(Collectors.toSet());

        double averageResponseTime = logEntries.stream()
                .mapToDouble(LogEntry::getResponseTimeMs)
                .average()
                .orElse(0.0);

        Map<Integer, Long> errorCountByCode = errorList.stream().collect(Collectors.groupingBy(
                LogEntry::getStatusCode,
                Collectors.counting()
        ));
        System.out.println("Finalizando: " + logEntries.size() + " " +
                "🚀 Entradas de log en hilo " + Thread.currentThread().getName() + "\n");

        return new LogSummary(totalEntries, errorCount, uniqueUsers, averageResponseTime, errorCountByCode);
    }
}
