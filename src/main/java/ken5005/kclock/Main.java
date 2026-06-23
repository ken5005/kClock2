package ken5005.kclock;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Main {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("kClock");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JLabel label = new JLabel("", SwingConstants.CENTER);
            label.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 48));

            // 初回表示
            label.setText(LocalTime.now().format(FMT));

            // 1 秒ごとに EDT 上で時刻を更新
            new Timer(1000, e -> label.setText(LocalTime.now().format(FMT))).start();

            frame.add(label, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null); // 画面中央に配置
            frame.setVisible(true);
        });
    }
}
