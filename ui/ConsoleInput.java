package supermarket.ui;

import java.util.Scanner;

/** Pembantu membaca input keyboard dengan aman (tidak crash saat salah ketik angka). */
public class ConsoleInput {

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Membaca satu baris teks.
     *
     * @param prompt teks petunjuk yang ditampilkan
     * @return teks yang diketik (sudah di-trim)
     */
    public String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Membaca bilangan bulat; mengulang sampai input valid.
     *
     * @param prompt teks petunjuk yang ditampilkan
     * @return bilangan bulat yang diketik
     */
    public int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readString(prompt));
            } catch (NumberFormatException e) {
                System.out.println("[!] Masukkan angka yang valid.");
            }
        }
    }

    /**
     * Membaca bilangan bulat besar (mis. harga); mengulang sampai input valid.
     *
     * @param prompt teks petunjuk yang ditampilkan
     * @return bilangan yang diketik
     */
    public long readLong(String prompt) {
        while (true) {
            try {
                return Long.parseLong(readString(prompt));
            } catch (NumberFormatException e) {
                System.out.println("[!] Masukkan angka yang valid.");
            }
        }
    }
}
