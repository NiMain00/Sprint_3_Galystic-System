package gui;

import dao.LaporanDAO;
import static gui.DashboardFrame.DATE_DB_FORMAT;
import static gui.DashboardFrame.DATE_DISPLAY_FORMAT;
import static gui.PenggunaPanel.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import gui.DateComparator;
import gui.NumberComparator;
import javax.swing.table.TableRowSorter;

public class LaporanPanel extends JPanel {

    private JTextField txtAwal, txtAkhir;
    private JComboBox<String> cmbTipe;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotalPendapatan, lblJumlahTransaksi;
    private LaporanDAO dao = new LaporanDAO();
    private DecimalFormat df = new DecimalFormat("#,##0");
    private List<Object[]> laporanData;

    public LaporanPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(ThemeManager.getBG_COLOR());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));


        // Top - Filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
filterPanel.setBackground(ThemeManager.getCARD_BG());
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBORDER_COLOR()),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        filterPanel.add(createLabel("Periode Awal (yyyy-MM-dd): *"));
        txtAwal = createTextField();
        txtAwal.setColumns(10);
        txtAwal.setText(LocalDate.now().withDayOfMonth(1).toString());
        filterPanel.add(txtAwal);

        filterPanel.add(createLabel("Periode Akhir:"));
        txtAkhir = createTextField();
        txtAkhir.setColumns(10);
        txtAkhir.setText(LocalDate.now().toString());
        filterPanel.add(txtAkhir);

        filterPanel.add(createLabel("Tipe:"));
cmbTipe = new JComboBox<>(new String[]{"Semua", "Campuran", "Barang", "Jasa"});
        cmbTipe.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbTipe.setRenderer(new TransaksiPanel.ComboBoxRenderer());
        filterPanel.add(cmbTipe);

        JButton btnFilter = createButton("Tampilkan", DashboardFrame.PRIMARY);
        btnFilter.addActionListener(e -> loadLaporan());
        filterPanel.add(btnFilter);

        JButton btnCetak = createButton("Cetak", new Color(155, 89, 182));
        btnCetak.addActionListener(e -> cetakLaporan());
        filterPanel.add(btnCetak);

        JButton btnExport = createButton("Export CSV", new Color(46, 204, 113));
        btnExport.addActionListener(e -> exportCSV());
        filterPanel.add(btnExport);

        // Table
tableModel = new DefaultTableModel(new String[]{"No Transaksi", "Tanggal", "Tipe", "Pelanggan", "Total Harga", "Kasir"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        label.setForeground(ThemeManager.getTEXT_PRIMARY());
        label.setBackground(ThemeManager.getCARD_BG()); 
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, ThemeManager.getBORDER_COLOR()));
        label.setOpaque(true);

        return label;
    }
});

        styleTable(table);
table.setRowSorter(new TableRowSorter<>(tableModel));
        ((TableRowSorter)table.getRowSorter()).setComparator(1, new DateComparator());
        ((TableRowSorter)table.getRowSorter()).setComparator(4, new NumberComparator()); // Total Harga
        // Auto-sort by date (index 1) descending
        List<RowSorter.SortKey> laporanSortKeys = new ArrayList<>();
        laporanSortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
        table.getRowSorter().setSortKeys(laporanSortKeys);

        JPanel tablePanel = new JPanel(new BorderLayout());
tablePanel.setBackground(ThemeManager.getCARD_BG());
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBORDER_COLOR()),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        tablePanel.add(new JScrollPane(table));

        // Bottom - Summary
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
summaryPanel.setBackground(ThemeManager.getCARD_BG());
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBORDER_COLOR()),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        lblJumlahTransaksi = new JLabel("Jumlah Transaksi: 0");
        lblJumlahTransaksi.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblJumlahTransaksi.setForeground(new Color(52, 152, 219));

        lblTotalPendapatan = new JLabel("Total Pendapatan: Rp 0");
        lblTotalPendapatan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalPendapatan.setForeground(DashboardFrame.PRIMARY);

        summaryPanel.add(lblJumlahTransaksi);
        summaryPanel.add(lblTotalPendapatan);

        add(filterPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.SOUTH);

        loadLaporan();
    }

private void loadLaporan() {
        String awal = txtAwal.getText().trim();
        String akhir = txtAkhir.getText().trim();
        // Re-apply date sort after load
        List<RowSorter.SortKey> laporanSortKeys = new ArrayList<>();
        laporanSortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
        table.getRowSorter().setSortKeys(laporanSortKeys);
        // Ensure comparators
        ((TableRowSorter)table.getRowSorter()).setComparator(1, new DateComparator());
        ((TableRowSorter)table.getRowSorter()).setComparator(4, new NumberComparator());
        String tipe = (String) cmbTipe.getSelectedItem();

        laporanData = dao.getLaporanTransaksi(awal, akhir, tipe);
        tableModel.setRowCount(0);
        for (Object[] row : laporanData) {
            String rawDate = (String) row[1];
            String formattedDate = LocalDate.parse(rawDate, DATE_DB_FORMAT).format(DATE_DISPLAY_FORMAT);
            row[1] = formattedDate;
            row[4] = df.format((double) row[4]);
            tableModel.addRow(row);
        }

        double total = dao.getTotalPendapatan(awal, akhir, tipe);
        int jumlah = dao.getJumlahTransaksi(awal, akhir);
        lblTotalPendapatan.setText("Total Pendapatan: Rp " + df.format(total));
        lblJumlahTransaksi.setText("Jumlah Transaksi: " + jumlah);
    }

    private void cetakLaporan() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(80)).append("\n");
        sb.append("                    LAPORAN BENGKEL LATHIFAH\n");
        sb.append("=".repeat(80)).append("\n");
        sb.append("Periode: ").append(txtAwal.getText()).append(" s/d ").append(txtAkhir.getText()).append("\n");
        sb.append("Tipe: ").append(cmbTipe.getSelectedItem()).append("\n");
        sb.append("-".repeat(80)).append("\n");
        sb.append(String.format("%-15s %-12s %-12s %-15s %12s %-15s\n",
                "No Transaksi", "Tanggal", "Tipe", "Pelanggan", "Total", "Kasir"));
        sb.append("-".repeat(80)).append("\n");

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            sb.append(String.format("%-15s %-12s %-12s %-15s %12s %-15s\n",
                    tableModel.getValueAt(i, 0), tableModel.getValueAt(i, 1),
                    tableModel.getValueAt(i, 2), tableModel.getValueAt(i, 3),
                    tableModel.getValueAt(i, 4), tableModel.getValueAt(i, 5)));
        }

        sb.append("-".repeat(80)).append("\n");
        sb.append(lblJumlahTransaksi.getText()).append("  |  ").append(lblTotalPendapatan.getText()).append("\n");
        sb.append("=".repeat(80)).append("\n");

        JTextArea ta = new JTextArea(sb.toString());
        ta.setFont(new Font("Consolas", Font.PLAIN, 11));
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(700, 400));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(sp, BorderLayout.CENTER);

        JButton btnPrint = new JButton("Print");
        btnPrint.addActionListener(e -> {
            try { ta.print(); } catch (Exception ex) { ex.printStackTrace(); }
        });
        panel.add(btnPrint, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, panel, "Cetak Laporan", JOptionPane.PLAIN_MESSAGE);
    }

    private void exportCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan CSV");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            // Validasi: pastikan ekstensi .csv
            String filePath = file.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                file = new java.io.File(filePath + ".csv");
            }
            try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(file), StandardCharsets.UTF_8))) {
                // Tulis UTF-8 BOM untuk kompatibilitas Excel
                writer.print('\uFEFF');
                
                // Header dengan validasi
                writer.println("No Transaksi,Tanggal,Tipe,Pelanggan,Total Harga,Kasir");
                
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    writer.printf("%s,%s,%s,%s,%s,%s\n",
                        validateCSVValue(tableModel.getValueAt(i, 0)),
                        validateCSVValue(tableModel.getValueAt(i, 1)),
                        validateCSVValue(tableModel.getValueAt(i, 2)),
                        validateCSVValue(tableModel.getValueAt(i, 3)),
                        validateCSVValue(tableModel.getValueAt(i, 4)),
                        validateCSVValue(tableModel.getValueAt(i, 5)));
                }
                
                // Summary dengan validasi
                writer.println();
                writer.println(validateCSVValue(lblJumlahTransaksi.getText()));
                writer.println(validateCSVValue(lblTotalPendapatan.getText()));
                
                JOptionPane.showMessageDialog(this, "CSV berhasil disimpan di " + file.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan CSV: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    
    // Method validasi nilai untuk CSV - handle null, koma, tanda kutip, baris baru
    private String validateCSVValue(Object value) {
        if (value == null) return "";
        String str = value.toString().trim();
        // Handle koma, tanda kutip, dan baris baru
        if (str.contains(",") || str.contains("\"") || str.contains("\n") || str.contains("\r")) {
            // Escape tanda kutip dengan menggandakan
            str = str.replace("\"", "\"\"");
            // Wrap dengan tanda kutip
            str = "\"" + str + "\"";
        }
        return str;
    }
}
