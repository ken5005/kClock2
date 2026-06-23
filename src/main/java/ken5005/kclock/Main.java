package ken5005.kclock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Main {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("kClock");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true);      // 枠なし
            frame.setAlwaysOnTop(true);      // 最前面固定

            JLabel label = new JLabel("", SwingConstants.CENTER);
            label.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 48));

            // 初回表示
            label.setText(LocalTime.now().format(FMT));

            // 1 秒ごとに EDT 上で時刻を更新
            new Timer(1000, e -> label.setText(LocalTime.now().format(FMT))).start();

            // 枠なしだと閉じるボタンが無いので、右クリックメニューに Exit を用意
            JPopupMenu menu = new JPopupMenu();
            JMenuItem exit = new JMenuItem("Exit");
            exit.addActionListener(e -> System.exit(0));
            menu.add(exit);

            // ドラッグ移動：押した位置（窓内オフセット）を記憶し、ドラッグ中は
            // 画面上のマウス座標からオフセットを引いた位置に窓を動かす
            final Point dragOffset = new Point();
            MouseAdapter mouse = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {           // 環境によっては press 側で発火
                        menu.show(e.getComponent(), e.getX(), e.getY());
                    } else {
                        dragOffset.setLocation(e.getX(), e.getY());
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {           // Windows は release 側で発火
                        menu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            };
            label.addMouseListener(mouse);
            label.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    frame.setLocation(e.getXOnScreen() - dragOffset.x,
                            e.getYOnScreen() - dragOffset.y);
                }
            });

            frame.add(label, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null); // 初期位置は画面中央
            frame.setVisible(true);
        });
    }
}