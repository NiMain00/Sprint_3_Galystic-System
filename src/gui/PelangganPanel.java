package gui;

import dao.PelangganDAO;
import model.Pengguna;
import static gui.PenggunaPanel.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.Pelanggan;

public class PelangganPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNama, txtTelp, txtAlamat, txtPlat;
    private PelangganDAO dao = new PelangganDAO();
    private int selectedId = -1;
    private Pengguna currentUser;
    private List<Pelanggan> allPelanggan;

    public PelangganPanel(Pengguna currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(0, 0));
setBackground(ThemeManager.getBG_COLOR());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
formPanel.setBackground(ThemeManager.getCARD_BG());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBORDER_COLOR()),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        formPanel.setPreferredSize(new Dimension(500, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;

        JLabel lbl = new JLabel("Form Pelanggan");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
lbl.setForeground(ThemeManager.getPRIMARY());
        formPanel.add(lbl, gbc);

        gbc.gridwidth = 1; gbc.gridy++;
        formPanel.add(createLabel("Nama:"), gbc); gbc.gridx = 1;
        txtNama = createTextField(); formPanel.add(txtNama, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(createLabel("No. Telp:"), gbc); gbc.gridx = 1;
        txtTelp = createTextField(); formPanel.add(txtTelp, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(createLabel("Alamat:"), gbc); gbc.gridx = 1;
        txtAlamat = createTextField(); formPanel.add(txtAlamat, gbc);

        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(createLabel("No. Plat:"), gbc); gbc.gridx = 1;
        txtPlat = createTextField(); formPanel.add(txtPlat, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        bp.setOpaque(false);

JButton btnSave = createButton("Simpan", ThemeManager.getPRIMARY());
        JButton btnUpdate = createButton("Update", new Color(52, 152, 219));
        JButton btnDelete = createButton("Hapus", new Color(231, 76, 60));
        JButton btnClear = createButton("Bersihkan", Color.GRAY);

        btnSave.addActionListener(e -> save());
        btnUpdate.addActionListener(e -> update());
        btnDelete.addActionListener(e -> delete());
        btnClear.addActionListener(e -> clear());

        bp.add(btnSave); bp.add(btnUpdate); bp.add(btnDelete); bp.add(btnClear);
        formPanel.add(bp, gbc);

tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "No. Telp", "Alamat", "No. Plat"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        label.setForeground(ThemeManager.getTEXT_PRIMARY());
        label.setBackground(ThemeManager.getCARD_BG()); // 🔥 WAJIB
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, ThemeManager.getBORDER_COLOR()));
        label.setOpaque(true);

        return label;
    }
});

        styleTable(table);
        table.setRowSorter(new TableRowSorter<>(tableModel));
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    selectedId = (int) tableModel.getValueAt(row, 0);
                    txtNama.setText((String) tableModel.getValueAt(row, 1));
                    txtTelp.setText(tableModel.getValueAt(row, 2) != null ? (String) tableModel.getValueAt(row, 2) : "");
                    txtAlamat.setText(tableModel.getValueAt(row, 3) != null ? (String) tableModel.getValueAt(row, 3) : "");
                    txtPlat.setText(tableModel.getValueAt(row, 4) != null ? (String) tableModel.getValueAt(row, 4) : "");
                }
            }
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        JLabel lblSearch = createLabel("Cari: ");
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterTable(searchField.getText().toLowerCase());
            }
        });
        JButton btnClearSearch = createButton("Clear", Color.GRAY);
        btnClearSearch.addActionListener(e -> {
            searchField.setText("");
            filterTable("");
        });
        searchPanel.add(lblSearch);
        searchPanel.add(searchField);
        searchPanel.add(btnClearSearch);

        // Quick select panel (like Transaksi refresh buttons)
        JPanel tableBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tableBtnPanel.setOpaque(false);
        JButton btnSelectTransaksi = createButton("Pilih", new Color(52, 152, 219));
        btnSelectTransaksi.addActionListener(e -> selectForTransaksi());
        tableBtnPanel.add(btnSelectTransaksi);
        


        JPanel tp = new JPanel(new BorderLayout());
tp.setBackground(ThemeManager.getCARD_BG());
        tp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBORDER_COLOR()),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        tp.add(searchPanel, BorderLayout.NORTH);
        tp.add(tableBtnPanel, BorderLayout.SOUTH);
        tp.add(new JScrollPane(table), BorderLayout.CENTER);

        add(formPanel, BorderLayout.WEST);
        add(tp, BorderLayout.CENTER);
        load();
    }

    private void load() {
        allPelanggan = dao.getAll();
        tableModel.setRowCount(0);
        for (Pelanggan p : allPelanggan) {
            tableModel.addRow(new Object[]{p.getId(), p.getNama(), p.getNoTelp(), p.getAlamat(), p.getNoPlat()});
        }
        filterTable("");
    }

    private void filterTable(String query) {
        tableModel.setRowCount(0);
        for (Pelanggan p : allPelanggan) {
            if (p.getNama().toLowerCase().contains(query) ||
                (p.getNoTelp() != null && p.getNoTelp().toLowerCase().contains(query)) ||
                (p.getNoPlat() != null && p.getNoPlat().toLowerCase().contains(query)) ||
                (p.getAlamat() != null && p.getAlamat().toLowerCase().contains(query))) {
                tableModel.addRow(new Object[]{p.getId(), p.getNama(), p.getNoTelp(), p.getAlamat(), p.getNoPlat()});
            }
        }
    }

    private void save() {
        if (txtNama.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama harus diisi!");
            return;
        }
        Pelanggan p = new Pelanggan();
        p.setNama(txtNama.getText().trim());
        p.setNoTelp(txtTelp.getText().trim());
        p.setAlamat(txtAlamat.getText().trim());
        p.setNoPlat(txtPlat.getText().trim());
        if (dao.insert(p)) {
            JOptionPane.showMessageDialog(this, "Berhasil!");
            load();
            clear();
        }
    }

    private void update() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data!");
            return;
        }
        Pelanggan p = new Pelanggan();
        p.setId(selectedId);
        p.setNama(txtNama.getText().trim());
        p.setNoTelp(txtTelp.getText().trim());
        p.setAlamat(txtAlamat.getText().trim());
        p.setNoPlat(txtPlat.getText().trim());
        if (dao.update(p)) {
            JOptionPane.showMessageDialog(this, "Berhasil!");
            load();
            clear();
        }
    }

    private void delete() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data!");
            return;
        }
        if (!"Admin".equals(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this, "Akses ditolak! Hanya Admin yang dapat menghapus data.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Yakin?") == JOptionPane.YES_OPTION) {
            if (dao.delete(selectedId)) {
                JOptionPane.showMessageDialog(this, "Berhasil!");
                load();
                clear();
            }
        }
    }

    private void clear() {
        txtNama.setText("");
        txtTelp.setText("");
        txtAlamat.setText("");
        txtPlat.setText("");
        selectedId = -1;
        table.clearSelection();
    }

    private void selectForTransaksi() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pelanggan dulu!");
            return;
        }
        Pelanggan selected = allPelanggan.stream().filter(p -> p.getId() == selectedId).findFirst().orElse(null);
        if (selected != null) {
            TransaksiPanel.setSelectedPelanggan(selected);
            ((DashboardFrame) SwingUtilities.getWindowAncestor(this)).showPanel("Transaksi");
            JOptionPane.showMessageDialog(this, "Pelanggan '" + selected.getNama() + "' Terpilih\nSilahkan Lanjutkan Proses Transaksi");
        }
    }
}

