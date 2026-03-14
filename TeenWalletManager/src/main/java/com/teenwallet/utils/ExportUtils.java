package com.teenwallet.utils;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExportUtils {

    public static boolean exportTableToCSV(JTable table, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            TableModel model = table.getModel();

            // Write headers
            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.print(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) {
                    writer.print(",");
                }
            }
            writer.println();

            // Write data
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    writer.print("\"" + (value != null ? value.toString() : "") + "\"");
                    if (j < model.getColumnCount() - 1) {
                        writer.print(",");
                    }
                }
                writer.println();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean exportTransactionsToCSV(java.util.List<com.teenwallet.model.Transaction> transactions, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write headers
            writer.println("Date,Type,Amount,Category,Description,Balance After");

            // Write data
            for (com.teenwallet.model.Transaction t : transactions) {
                writer.printf("\"%s\",\"%s\",%.2f,\"%s\",\"%s\",%.2f%n",
                        t.getFormattedDate(),
                        t.getType(),
                        t.getAmount(),
                        t.getCategory(),
                        t.getDescription(),
                        t.getBalanceAfter()
                );
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean exportReportToCSV(com.teenwallet.service.ReportService.MonthlyReport report, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            writer.println("TEENWALLET MONTHLY REPORT");
            writer.println("Month: " + report.getMonth() + "/" + report.getYear());
            writer.println("Generated: " + LocalDateTime.now().format(formatter));
            writer.println();

            writer.println("SUMMARY");
            writer.println("Total Spending: ₹" + String.format("%.2f", report.getTotalSpending()));
            writer.println("Total Saved: ₹" + String.format("%.2f", report.getTotalSaved()));
            writer.println("Completed Goals: " + report.getCompletedGoals());
            writer.println();

            writer.println("CATEGORY BREAKDOWN");
            for (var entry : report.getCategorySpending().entrySet()) {
                writer.println(entry.getKey() + ": ₹" + String.format("%.2f", entry.getValue()));
            }
            writer.println();

            writer.println("TRANSACTIONS");
            writer.println("Date,Type,Amount,Category,Description");
            for (var t : report.getTransactions()) {
                writer.printf("\"%s\",\"%s\",%.2f,\"%s\",\"%s\"%n",
                        t.getFormattedDate(),
                        t.getType(),
                        t.getAmount(),
                        t.getCategory(),
                        t.getDescription()
                );
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}