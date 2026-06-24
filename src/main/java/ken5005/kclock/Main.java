package ken5005.kclock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Main {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss M/d(E)", Locale.JAPANESE);

    public static void main(String[] args) {
        Config config = new Config();
        config.load();

        // JVM 終了時（System.exit 含む）に位置・設定を保存。I/O のみで EDT に触らない。
        Runtime.getRuntime().addShutdownHook(new Thread(config::save));

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("kClock");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true);
            frame.setAlwaysOnTop(true);

            JLabel label = new JLabel("", SwingConstants.CENTER);
            label.setFont(new Font(config.getFontFamily(), Font.PLAIN, config.getFontSize()));
            label.setForeground(config.getTextColor());

            label.setText(LocalDateTime.now().format(FMT));
            new Timer(1000, e -> label.setText(LocalDateTime.now().format(FMT))).start();

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            panel.setBackground(config.getBgColor());
            panel.add(label, BorderLayout.CENTER);

            JPopupMenu menu = new JPopupMenu();
            JMenuItem settingsItem = new JMenuItem("Settings…");
            JMenuItem exitItem     = new JMenuItem("Exit");
            settingsItem.addActionListener(e -> showSettingsDialog(frame, config, label, panel));
            exitItem.addActionListener(e -> System.exit(0));
            menu.add(settingsItem);
            menu.add(exitItem);

            final Point dragOffset = new Point();
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        menu.show(e.getComponent(), e.getX(), e.getY());
                    } else {
                        dragOffset.setLocation(e.getX(), e.getY());
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        menu.show(e.getComponent(), e.getX(), e.getY());
                    } else {
                        // ドラッグ完了後に現在位置を config に記録（保存は終了時）
                        config.setWindowX(frame.getX());
                        config.setWindowY(frame.getY());
                    }
                }
            });
            label.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    frame.setLocation(e.getXOnScreen() - dragOffset.x,
                            e.getYOnScreen() - dragOffset.y);
                }
            });

            frame.setContentPane(panel);
            frame.setSize(config.getWindowWidth(), config.getWindowHeight());

            Integer savedX = config.getWindowX();
            Integer savedY = config.getWindowY();
            if (savedX != null && savedY != null) {
                frame.setLocation(savedX, savedY);
            } else {
                frame.setLocationRelativeTo(null);
            }

            frame.setVisible(true);
        });
    }

    private static void showSettingsDialog(JFrame frame, Config config, JLabel label, JPanel panel) {
        JDialog dialog = new JDialog(frame, "Settings", true);
        dialog.setAlwaysOnTop(true);

        JSpinner fontSizeSpinner = new JSpinner(
                new SpinnerNumberModel(config.getFontSize(), 8, 200, 1));

        String[] families = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();
        JComboBox<String> fontFamilyCombo = new JComboBox<>(families);
        fontFamilyCombo.setSelectedItem(config.getFontFamily());

        JButton textColorBtn = makeColorButton(config.getTextColor());
        textColorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(dialog, "Text Color", textColorBtn.getBackground());
            if (c != null) textColorBtn.setBackground(c);
        });

        JButton bgColorBtn = makeColorButton(config.getBgColor());
        bgColorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(dialog, "Background Color", bgColorBtn.getBackground());
            if (c != null) bgColorBtn.setBackground(c);
        });

        JSpinner widthSpinner  = new JSpinner(
                new SpinnerNumberModel(config.getWindowWidth(),  10, 2000, 1));
        JSpinner heightSpinner = new JSpinner(
                new SpinnerNumberModel(config.getWindowHeight(), 10, 2000, 1));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
        GridBagConstraints lc = labelGbc();
        GridBagConstraints fc = fieldGbc();
        int row = 0;
        addRow(form, lc, fc, row++, "Font size:",     fontSizeSpinner);
        addRow(form, lc, fc, row++, "Font family:",   fontFamilyCombo);
        addRow(form, lc, fc, row++, "Text color:",    textColorBtn);
        addRow(form, lc, fc, row++, "Background:",    bgColorBtn);
        addRow(form, lc, fc, row++, "Window width:",  widthSpinner);
        addRow(form, lc, fc, row,   "Window height:", heightSpinner);

        Runnable apply = () -> {
            config.setFontSize((int) fontSizeSpinner.getValue());
            config.setFontFamily((String) fontFamilyCombo.getSelectedItem());
            config.setTextColor(textColorBtn.getBackground());
            config.setBgColor(bgColorBtn.getBackground());
            config.setWindowWidth((int) widthSpinner.getValue());
            config.setWindowHeight((int) heightSpinner.getValue());
            label.setFont(new Font(config.getFontFamily(), Font.PLAIN, config.getFontSize()));
            label.setForeground(config.getTextColor());
            panel.setBackground(config.getBgColor());
            frame.setSize(config.getWindowWidth(), config.getWindowHeight());
            frame.revalidate();
            frame.repaint();
        };

        JButton okBtn     = new JButton("OK");
        JButton applyBtn  = new JButton("Apply");
        JButton cancelBtn = new JButton("Cancel");
        okBtn.addActionListener(e -> { apply.run(); config.save(); dialog.dispose(); });
        applyBtn.addActionListener(e -> apply.run());
        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 4));
        btnPanel.add(okBtn);
        btnPanel.add(applyBtn);
        btnPanel.add(cancelBtn);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    // カラースウォッチボタン：paintComponent をオーバーライドして L&F に依存せず色を表示
    private static JButton makeColorButton(Color color) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        btn.setBackground(color);
        btn.setBorderPainted(true);
        btn.setPreferredSize(new Dimension(60, 22));
        return btn;
    }

    private static GridBagConstraints labelGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(3, 0, 3, 8);
        return gbc;
    }

    private static GridBagConstraints fieldGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(3, 0, 3, 0);
        return gbc;
    }

    private static void addRow(JPanel form, GridBagConstraints lc, GridBagConstraints fc,
                               int row, String text, JComponent field) {
        lc.gridy = row;
        fc.gridy = row;
        form.add(new JLabel(text), lc);
        form.add(field, fc);
    }
}
