// src/gui/KategoriBarangPanel.java
package gui;

import dao.KategoriBarangDAO;
import static gui.PenggunaPanel.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.KategoriBarang;
import model.Pengguna;

public class KategoriBarangPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtNama;
    private KategoriBarangDAO dao = new KategoriBarangDAO();
    private Pengguna currentUser;
    private int selectedId = -1;

    public KategoriBarangPanel(Pengguna currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(0, 0));
        setBackground(ThemeManager.getBG_COLOR());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Form
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

        JLabel lbl = new JLabel("Form Kategori Barang");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(DashboardFrame.PRIMARY); // ThemeManager.getPRIMARY()
        formPanel.add(lbl, gbc);

        gbc.gridwidth = 1; gbc.gridy++;
        formPanel.add(createLabel("Nama Kategori:"), gbc);
        gbc.gridx = 1;
        txtNama = createTextField();
        formPanel.add(txtNama, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        bp.setOpaque(false);

        JButton btnSave = createButton("Simpan", ThemeManager.getPRIMARY());
        JButton btnUpdate = createButton("Update", new Color(52, 152, 219));
        JButton btnDelete = createButton("Hapus", new Color(231, 76, 60));
        JButton btnClear = createButton("Bersihkan", Color.GRAY);

        btnSave.addActionListener(e -> { save(); });
        btnUpdate.addActionListener(e -> { update(); });
        btnDelete.addActionListener(e -> { delete(); });
        btnClear.addActionListener(e -> { clear(); });

        bp.add(btnSave); bp.add(btnUpdate); bp.add(btnDelete); bp.add(btnClear);
        formPanel.add(bp, gbc);

        // Table
tableModel = new DefaultTableModel(new String[]{"ID", "Nama Kategori"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.getTableHeader().setReorderingAllowed(false);
        // Sembunyikan kolom ID (index 0)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Terapkan ke semua kolom
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        label.setForeground(ThemeManager.getTEXT_PRIMARY());
        label.setBackground(ThemeManager.getCARD_BG()); // 🔥 WAJIB
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 1, ThemeManager.getBORDER_COLOR()));
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
                }
            }
        });

        JPanel tp = new JPanel(new BorderLayout());
tp.setBackground(ThemeManager.getCARD_BG());
        tp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBORDER_COLOR()),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        tp.add(new JScrollPane(table));

        add(formPanel, BorderLayout.WEST);
        add(tp, BorderLayout.CENTER);
        load();
    }

    private void load() {
        tableModel.setRowCount(0);
        for (KategoriBarang k : dao.getAll()) {
            tableModel.addRow(new Object[]{k.getId(), k.getNamaKategori()});
        }
    }

    private void save() {
        if (txtNama.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Nama kategori harus diisi!"); return; }
        KategoriBarang k = new KategoriBarang();
        k.setNamaKategori(txtNama.getText().trim());
        if (dao.insert(k)) { JOptionPane.showMessageDialog(this, "Berhasil!"); load(); clear(); }
    }

    private void update() {
        if (selectedId == -1) { JOptionPane.showMessageDialog(this, "Pilih data!"); return; }
        KategoriBarang k = new KategoriBarang();
        k.setId(selectedId);
        k.setNamaKategori(txtNama.getText().trim());
        if (dao.update(k)) { JOptionPane.showMessageDialog(this, "Berhasil!"); load(); clear(); }
    }

    private void delete() {
        if (selectedId == -1) { JOptionPane.showMessageDialog(this, "Pilih data!"); return; }
        if (!"Admin".equals(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this, "Akses ditolak! Hanya Admin yang dapat menghapus data.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Yakin?") == JOptionPane.YES_OPTION) {
            if (dao.delete(selectedId)) { JOptionPane.showMessageDialog(this, "Berhasil!"); load(); clear(); }
        }
    }

    private void clear() { txtNama.setText(""); selectedId = -1; table.clearSelection(); }
}
