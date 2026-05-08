package gui;

import dao.PelangganDAO;
import dao.ProdukDAO;
import dao.TransaksiDAO;
import static gui.DashboardFrame.DATE_DB_FORMAT;
import static gui.DashboardFrame.DATE_DISPLAY_FORMAT;
import static gui.PenggunaPanel.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import gui.DateComparator;
import gui.NumberComparator;
import javax.swing.table.TableRowSorter;
import model.*;

public class TransaksiPanel extends JPanel {

    public static class ComboBoxRenderer extends JLabel implements ListCellRenderer<Object> {
        public ComboBoxRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Object> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Produk) {
                setText(((Produk) value).getNama());
            } else if (value instanceof Jasa) {
                setText(((Jasa) value).getNama());
            } else if (value instanceof Pelanggan) {
                setText(((Pelanggan) value).getNama());
            } else if (value instanceof KategoriBarang) {
                setText(((KategoriBarang) value).getNamaKategori());
            } else {
                setText(value != null ? value.toString() : "");
            }
            if (isSelected) {
                setBackground(ThemeManager.getSIDEBAR_ACTIVE());
                setForeground(Color.WHITE);
            } else {
                setBackground(ThemeManager.getCARD_BG());
                setForeground(ThemeManager.getTEXT_PRIMARY());
            }
            return this;
        }
    }

    private Pengguna currentUser;
    private JComboBox<String> cmbTipe;
    private JComboBox<Pelanggan> cmbPelanggan;
    private JComboBox cmbProduk;
    private JTextField txtQty, txtNoTransaksi;
    private JLabel lblHarga, lblSubtotal, lblTotal;
    private JTable tableDetail, tableHistory;
    private DefaultTableModel detailModel, historyModel;
    private TransaksiDAO trxDAO = new TransaksiDAO();
    private ProdukDAO produkDAO = new ProdukDAO();
    private PelangganDAO pelangganDAO = new PelangganDAO();
    private List<DetailTransaksi> detailList = new ArrayList<>();
    private double totalHarga = 0;
    private DecimalFormat df = new DecimalFormat("#,##0");
    private List<Transaksi> allTransaksi;

    public TransaksiPanel(Pengguna user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 0));
        setBackground(ThemeManager.getBG_COLOR());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Left panel - Form transaksi
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(ThemeManager.getCARD_BG());
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBORDER_COLOR()),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        leftPanel.setPreferredSize(new Dimension(450, 0));

        // Header transaksi
        JLabel lblHeader = new JLabel("Transaksi Baru");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(ThemeManager.getPRIMARY());
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(lblHeader);
        leftPanel.add(Box.createVerticalStrut(15));

        // Tipe + No Transaksi
        JPanel tipePanel = new JPanel(new GridLayout(2, 2, 8, 5));
        tipePanel.setOpaque(false);
        tipePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        tipePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        tipePanel.add(createLabel("Tipe Transaksi:"));
        cmbTipe = new JComboBox<>(new String[]{"Campuran", "Jasa", "Barang"});
        cmbTipe.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbTipe.setRenderer(new ComboBoxRenderer());
        cmbTipe.addActionListener(e -> {
            generateNoTransaksi();
            loadProduk();
        });
        tipePanel.add(cmbTipe);

        tipePanel.add(createLabel("No. Transaksi:"));
        txtNoTransaksi = createTextField();
        txtNoTransaksi.setEditable(false);
        tipePanel.add(txtNoTransaksi);

        leftPanel.add(tipePanel);
        leftPanel.add(Box.createVerticalStrut(10));

        // Pelanggan
        JPanel pelPanel = new JPanel(new GridLayout(1, 2, 8, 5));
        pelPanel.setOpaque(false);
        pelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        pelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        pelPanel.add(createLabel("Pelanggan:"));
        cmbPelanggan = new JComboBox<>();
        cmbPelanggan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbPelanggan.setRenderer(new ComboBoxRenderer());
        pelPanel.add(cmbPelanggan);
        loadPelanggan();
        leftPanel.add(pelPanel);
        leftPanel.add(Box.createVerticalStrut(15));

        // Separator
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        leftPanel.add(sep);
        leftPanel.add(Box.createVerticalStrut(10));

        JLabel lblItem = new JLabel("Tambah Item");
        lblItem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblItem.setForeground(new Color(80, 80, 80));
        lblItem.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(lblItem);
        leftPanel.add(Box.createVerticalStrut(8));

        // Produk selection
        JPanel prodPanel = new JPanel(new GridLayout(3, 2, 8, 5));
        prodPanel.setOpaque(false);
        prodPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));
        prodPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

prodPanel.add(createLabel("Produk/Jasa:"));
        cmbProduk = new JComboBox<>();
        cmbProduk.setFont(new Font("Segoe UI", Font.PLAIN, 13));
cmbProduk.addActionListener(e -> {
            Object selected = cmbProduk.getSelectedItem();
            if (selected instanceof Produk) {
                Produk p = (Produk) selected;
                lblHarga.setText("Rp " + df.format(p.getHarga()));
            } else if (selected instanceof Jasa) {
                Jasa j = (Jasa) selected;
                lblHarga.setText("Rp " + df.format(j.getHarga()));
            }
            calculateSubtotal();
        });
        prodPanel.add(cmbProduk);

        prodPanel.add(createLabel("Harga:"));
        lblHarga = new JLabel("Rp 0");
        lblHarga.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHarga.setForeground(ThemeManager.getPRIMARY());
        prodPanel.add(lblHarga);

        prodPanel.add(createLabel("Qty:"));
        txtQty = createTextField();
        txtQty.setText("1");
        txtQty.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { calculateSubtotal(); }
        });
        prodPanel.add(txtQty);

        leftPanel.add(prodPanel);
        leftPanel.add(Box.createVerticalStrut(5));

        // Subtotal
        JPanel subPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        subPanel.setOpaque(false);
        subPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subPanel.add(createLabel("Subtotal: "));
        lblSubtotal = new JLabel("Rp 0");
        lblSubtotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSubtotal.setForeground(new Color(46, 204, 113));
        subPanel.add(lblSubtotal);
        leftPanel.add(subPanel);
        leftPanel.add(Box.createVerticalStrut(8));

        // Add button
        JButton btnAddItem = createButton("+ Tambah Item", ThemeManager.getPRIMARY());
        btnAddItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btnAddItem.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAddItem.addActionListener(e -> addItem());
        leftPanel.add(btnAddItem);
        leftPanel.add(Box.createVerticalStrut(10));

        // Detail table
detailModel = new DefaultTableModel(new String[]{"Produk", "Qty", "Harga", "Subtotal"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tblDetail = new JTable(detailModel);
        tblDetail.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tblDetail.getColumnCount(); i++) {
            tblDetail.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        tblDetail.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setForeground(ThemeManager.getTABLE_TEXT());
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
                label.setOpaque(true);
                return label;
            }
        });
        styleTable(tblDetail);
        tblDetail.setRowSorter(new TableRowSorter<>(detailModel));
        tblDetail.getColumnModel().getColumn(1).setPreferredWidth(40);
        tblDetail.getColumnModel().getColumn(1).setMaxWidth(50);
        tblDetail.getColumnModel().getColumn(1).setMinWidth(35);
        JScrollPane spDetail = new JScrollPane(tblDetail);
        spDetail.setPreferredSize(new Dimension(0, 120));
        spDetail.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(spDetail);
        leftPanel.add(Box.createVerticalStrut(10));

        // Remove selected item
        JButton btnRemove = createButton("Hapus Item", new Color(231, 76, 60));
        btnRemove.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        btnRemove.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRemove.addActionListener(e -> {
            int row = tblDetail.getSelectedRow();
            if (row >= 0) {
                detailList.remove(row);
                refreshDetail();
            }
        });
        leftPanel.add(btnRemove);
        leftPanel.add(Box.createVerticalStrut(10));

        // Total
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setOpaque(false);
        totalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalPanel.add(new JLabel("TOTAL: "));
        lblTotal = new JLabel("Rp 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotal.setForeground(ThemeManager.getPRIMARY());
        totalPanel.add(lblTotal);
        leftPanel.add(totalPanel);
        leftPanel.add(Box.createVerticalStrut(10));

        // Save transaction
        JButton btnSave = new JButton("SIMPAN TRANSAKSI");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnSave.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSave.addActionListener(e -> saveTransaksi());
        leftPanel.add(btnSave);

        // Right panel - History
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setBackground(ThemeManager.getCARD_BG());
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBORDER_COLOR()),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel lblHistory = new JLabel("Riwayat Transaksi");
        lblHistory.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHistory.setForeground(ThemeManager.getPRIMARY());
        topPanel.add(lblHistory, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        JLabel lblSearch = createLabel("Cari: ");
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterHistory(searchField.getText().toLowerCase());
            }
        });
        JButton btnClearSearch = createButton("Clear", Color.GRAY);
        btnClearSearch.addActionListener(e -> {
            searchField.setText("");
            filterHistory("");
        });
        searchPanel.add(lblSearch);
        searchPanel.add(searchField);
        searchPanel.add(btnClearSearch);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        rightPanel.add(topPanel, BorderLayout.NORTH);

historyModel = new DefaultTableModel(new String[]{"ID", "No Transaksi", "Tanggal", "Tipe", "Pelanggan", "Total", "Kasir"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableHistory = new JTable(historyModel);
        tableHistory.getTableHeader().setReorderingAllowed(false);
        tableHistory.getColumnModel().getColumn(0).setMinWidth(0);
        tableHistory.getColumnModel().getColumn(0).setMaxWidth(0);
        tableHistory.getColumnModel().getColumn(0).setWidth(0);
        DefaultTableCellRenderer centerRenderer2 = new DefaultTableCellRenderer();
        centerRenderer2.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tableHistory.getColumnCount(); i++) {
            tableHistory.getColumnModel().getColumn(i).setCellRenderer(centerRenderer2);
        }
        tableHistory.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setForeground(ThemeManager.getTEXT_PRIMARY());
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, ThemeManager.getTABLE_HEADER_BORDER()));
                label.setOpaque(true);
                return label;
            }
        });

        styleTable(tableHistory);
        tableHistory.setRowSorter(new TableRowSorter<>(historyModel));
        ((TableRowSorter)tableHistory.getRowSorter()).setComparator(2, new DateComparator());
        ((TableRowSorter)tableHistory.getRowSorter()).setComparator(5, new NumberComparator());
        // Auto-sort by date (index 2) descending on load
        java.util.List<javax.swing.RowSorter.SortKey> historySortKeys = new java.util.ArrayList<>();
        historySortKeys.add(new javax.swing.RowSorter.SortKey(2, javax.swing.SortOrder.DESCENDING));
        tableHistory.getRowSorter().setSortKeys(historySortKeys);

        JPanel histBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        histBtnPanel.setOpaque(false);
        JButton btnRefresh = createButton("Refresh", new Color(52, 152, 219));
        btnRefresh.addActionListener(e -> loadHistory());
        JButton btnDetailView = createButton("Detail", ThemeManager.getPRIMARY());
        btnDetailView.addActionListener(e -> viewDetail());
        JButton btnDeleteTrx = createButton("Hapus", new Color(231, 76, 60));
        btnDeleteTrx.addActionListener(e -> deleteTransaction());
        JButton btnCetak = createButton("Struk", new Color(155, 89, 182));
        btnCetak.addActionListener(e -> cetakStruk());
        histBtnPanel.add(btnRefresh);
        histBtnPanel.add(btnDetailView);
        histBtnPanel.add(btnDeleteTrx);
        histBtnPanel.add(btnCetak);

        rightPanel.add(new JScrollPane(tableHistory), BorderLayout.CENTER);
        rightPanel.add(histBtnPanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        generateNoTransaksi();
        loadProduk();
        loadHistory();
    }

    private void generateNoTransaksi() {
        String tipe = (String) cmbTipe.getSelectedItem();
        txtNoTransaksi.setText(trxDAO.generateNoTransaksi(tipe));
    }

    private static Pelanggan selectedPelangganForTransaksi;
    
    public static void setSelectedPelanggan(Pelanggan p) {
        selectedPelangganForTransaksi = p;
    }
    
    private void loadPelanggan() {
        cmbPelanggan.removeAllItems();
        cmbPelanggan.addItem(null);
        for (Pelanggan p : pelangganDAO.getAll()) {
            cmbPelanggan.addItem(p);
        }
        // Auto-select if coming from PelangganPanel
        if (selectedPelangganForTransaksi != null) {
            for (int i = 0; i < cmbPelanggan.getItemCount(); i++) {
                Pelanggan item = cmbPelanggan.getItemAt(i);
                if (item != null && item.getId() == selectedPelangganForTransaksi.getId()) {
                    cmbPelanggan.setSelectedIndex(i);
                    break;
                }
            }
            selectedPelangganForTransaksi = null; // Clear after use
        }
    }

    private void loadProduk() {
        cmbProduk.removeAllItems();
        String tipe = (String) cmbTipe.getSelectedItem();

        if (tipe.equals("Barang")) {
            for (Produk p : produkDAO.getAllProduk()) {
                cmbProduk.addItem(p);
            }
        } else if (tipe.equals("Jasa")) {
            for (Jasa j : produkDAO.getAllJasa()) {
                cmbProduk.addItem(j);
            }
        } else if (tipe.equals("Campuran")) {
            for (Produk p : produkDAO.getAllProduk()) {
                cmbProduk.addItem(p);
            }
            for (Jasa j : produkDAO.getAllJasa()) {
                cmbProduk.addItem(j);
            }
        }
        cmbProduk.setRenderer(new ComboBoxRenderer());
    }

    private void calculateSubtotal() {
        try {
            Object selected = cmbProduk.getSelectedItem();
            int qty = Integer.parseInt(txtQty.getText().trim());
            double harga = 0;
            if (selected instanceof Produk) {
                harga = ((Produk) selected).getHarga();
            } else if (selected instanceof Jasa) {
                harga = ((Jasa) selected).getHarga();
            }
            if (harga > 0 && qty > 0) {
                double sub = harga * qty;
                lblSubtotal.setText("Rp " + df.format(sub));
            }
        } catch (NumberFormatException ex) {
            lblSubtotal.setText("Rp 0");
        }
    }

private void addItem() {
        Object selected = cmbProduk.getSelectedItem();
        if (selected == null) { JOptionPane.showMessageDialog(this, "Pilih produk!"); return; }
        try {
            int qty = Integer.parseInt(txtQty.getText().trim());
            if (qty <= 0) { JOptionPane.showMessageDialog(this, "Qty harus > 0!"); return; }

            DetailTransaksi d = new DetailTransaksi();
            if (selected instanceof Produk) {
                Produk p = (Produk) selected;
                d.setProdukId(p.getId());
                d.setNamaProduk(p.getNama());
                d.setHargaSatuan(p.getHarga());
            } else if (selected instanceof Jasa) {
                Jasa j = (Jasa) selected;
                d.setProdukId(j.getId());
                d.setNamaProduk(j.getNama());
                d.setHargaSatuan(j.getHarga());
            }
            d.setQty(qty);
            d.hitungSubtotal();
            detailList.add(d);
            refreshDetail();
            txtQty.setText("1");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Qty harus berupa angka!");
        }
    }
    
private void refreshDetail() {
        detailModel.setRowCount(0);
        totalHarga = 0;
        for (DetailTransaksi d : detailList) {
            detailModel.addRow(new Object[]{d.getNamaProduk(), d.getQty(), "Rp " + df.format(d.getHargaSatuan()), "Rp " + df.format(d.getSubtotal())});
            totalHarga += d.getSubtotal();
        }
        lblTotal.setText("Rp " + df.format(totalHarga));
    }

    private void saveTransaksi() {
        if (detailList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tambahkan minimal 1 item!");
            return;
        }

        Transaksi t = new Transaksi();
        t.setNoTransaksi(txtNoTransaksi.getText());
        t.setTanggal(LocalDate.now().toString());
        t.setTipeTransaksi((String) cmbTipe.getSelectedItem());
        t.setTotalHarga(totalHarga);
        t.setPenggunaId(currentUser.getId());

        Pelanggan pel = (Pelanggan) cmbPelanggan.getSelectedItem();
        t.setPelangganId(pel != null ? pel.getId() : 0);
        t.setDetailList(detailList);

        if (trxDAO.insertTransaksi(t)) {
            JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan!\nNo: " + t.getNoTransaksi() +
                    "\nTotal: Rp " + df.format(totalHarga));
            detailList = new ArrayList<>();
            refreshDetail();
            generateNoTransaksi();
            loadHistory();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi!");
        }
    }

private void loadHistory() {
        allTransaksi = trxDAO.getAll();

        historyModel.setRowCount(0);
        // Re-apply sort after refresh
        java.util.List<javax.swing.RowSorter.SortKey> historySortKeys = new java.util.ArrayList<>();
        historySortKeys.add(new javax.swing.RowSorter.SortKey(2, javax.swing.SortOrder.DESCENDING));
        tableHistory.getRowSorter().setSortKeys(historySortKeys);
        // Ensure comparators are set
        ((TableRowSorter)tableHistory.getRowSorter()).setComparator(2, new DateComparator());
        ((TableRowSorter)tableHistory.getRowSorter()).setComparator(5, new NumberComparator());
        DateTimeFormatter input = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter output = DateTimeFormatter.ofPattern("dd-MM-yyyy");
for (Transaksi t : allTransaksi) {
            String tgl = LocalDate.parse(t.getTanggal(), input).format(output);
            historyModel.addRow(new Object[]{t.getId(), t.getNoTransaksi(), tgl,
                t.getTipeTransaksi(), t.getNamaPelanggan(), "Rp " + df.format(t.getTotalHarga()), t.getNamaPengguna()});
        }
        filterHistory("");

    }

private void filterHistory(String query) {
        historyModel.setRowCount(0);
        // Re-apply sort after filter
        List<RowSorter.SortKey> historySortKeys = new ArrayList<>();
        historySortKeys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
        tableHistory.getRowSorter().setSortKeys(historySortKeys);
        // Ensure comparators are set
        ((TableRowSorter)tableHistory.getRowSorter()).setComparator(2, new DateComparator());
        ((TableRowSorter)tableHistory.getRowSorter()).setComparator(5, new NumberComparator());
        for (Transaksi t : allTransaksi) {
            if (t.getNoTransaksi().toLowerCase().contains(query) ||
                t.getNamaPelanggan().toLowerCase().contains(query) ||
                t.getTipeTransaksi().toLowerCase().contains(query)) {
                String tgl = LocalDate.parse(t.getTanggal(), DATE_DB_FORMAT).format(DATE_DISPLAY_FORMAT);
                historyModel.addRow(new Object[]{t.getId(), t.getNoTransaksi(), tgl,
                    t.getTipeTransaksi(), t.getNamaPelanggan(), "Rp " + df.format(t.getTotalHarga()), t.getNamaPengguna()});
            }
        }
    }

    private void viewDetail() {
        int row = tableHistory.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih transaksi!"); return; }
        int trxId = (int) historyModel.getValueAt(row, 0);
        List<DetailTransaksi> details = trxDAO.getDetailByTransaksiId(trxId);

        StringBuilder sb = new StringBuilder();
        sb.append("No Transaksi: ").append(historyModel.getValueAt(row, 1)).append("\n");
        sb.append("Tanggal: ").append(historyModel.getValueAt(row, 2)).append("\n");
        sb.append("Tipe: ").append(historyModel.getValueAt(row, 3)).append("\n");
        sb.append("Pelanggan: ").append(historyModel.getValueAt(row, 4)).append("\n\n");
        sb.append(String.format("%-25s %5s %12s %12s\n", "Produk", "Qty", "Harga", "Subtotal"));
        sb.append("─".repeat(55)).append("\n");
        for (DetailTransaksi d : details) {
            sb.append(String.format("%-25s %5d %12s %12s\n", d.getNamaProduk(), d.getQty(),
                df.format(d.getHargaSatuan()), df.format(d.getSubtotal())));
        }
        sb.append("─".repeat(55)).append("\n");
        sb.append(String.format("%43s %12s", "TOTAL:", historyModel.getValueAt(row, 5)));

        JTextArea ta = new JTextArea(sb.toString());
        ta.setFont(new Font("Consolas", Font.PLAIN, 12));
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(500, 300));
        JOptionPane.showMessageDialog(this, sp, "Detail Transaksi", JOptionPane.INFORMATION_MESSAGE);
    }

private void deleteTransaction() {
        int row = tableHistory.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih transaksi!"); return; }
        if (!"Admin".equals(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this, "Akses ditolak! Hanya Admin yang dapat menghapus transaksi.");
            return;
        }
        int trxId = (int) historyModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Yakin hapus transaksi ini?") == JOptionPane.YES_OPTION) {
            if (trxDAO.deleteTransaksi(trxId)) {
                JOptionPane.showMessageDialog(this, "Berhasil dihapus!");
                loadHistory();
            }
        }
    }

    private void cetakStruk() {
        int row = tableHistory.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih transaksi!"); return; }
        int trxId = (int) historyModel.getValueAt(row, 0);
        List<DetailTransaksi> details = trxDAO.getDetailByTransaksiId(trxId);

        StringBuilder sb = new StringBuilder();
        sb.append("═".repeat(45)).append("\n");
        sb.append(String.format("%20s\n", "BENGKEL LATHIFAH"));
        sb.append("    Sistem Manajemen Penjualan\n");
        sb.append("═".repeat(45)).append("\n");
        sb.append("No Transaksi : ").append(historyModel.getValueAt(row, 1)).append("\n");
        sb.append("Tanggal      : ").append(historyModel.getValueAt(row, 2)).append("\n");
        sb.append("Tipe         : ").append(historyModel.getValueAt(row, 3)).append("\n");
        sb.append("Pelanggan    : ").append(historyModel.getValueAt(row, 4)).append("\n");
        sb.append("Kasir        : ").append(historyModel.getValueAt(row, 6)).append("\n");
        sb.append("─".repeat(45)).append("\n");
        for (DetailTransaksi d : details) {
            sb.append(d.getNamaProduk()).append("\n");
            sb.append(String.format("  %d x Rp %s = Rp %s\n", d.getQty(),
                df.format(d.getHargaSatuan()), df.format(d.getSubtotal())));
        }
        sb.append("─".repeat(45)).append("\n");
        sb.append(String.format("TOTAL: Rp %s\n", historyModel.getValueAt(row, 5)));
        sb.append("═".repeat(45)).append("\n");
        sb.append("     Terima kasih atas kunjungan Anda!\n");
        sb.append("═".repeat(45)).append("\n");

        JTextArea ta = new JTextArea(sb.toString());
        ta.setFont(new Font("Consolas", Font.PLAIN, 12));
        ta.setEditable(false);
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(400, 400));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(sp, BorderLayout.CENTER);

        JButton btnPrint = new JButton("Print");
        btnPrint.addActionListener(e -> {
            try { ta.print(); } catch (Exception ex) { ex.printStackTrace(); }
        });
        panel.add(btnPrint, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, panel, "Struk Transaksi", JOptionPane.PLAIN_MESSAGE);
    }
}

