package com.teenwallet.utils;

import com.teenwallet.model.Transaction;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

public class ExportUtils {

    public static boolean exportTransactionsToCSV(List<Transaction> transactions, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write headers
            writer.println("Date,Type,Amount,Category,Description,Balance After");

            // Write data
            for (Transaction t : transactions) {
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
}