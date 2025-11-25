import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class test {

    // keep your original state & helpers
    private static String message;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(test::createAndShowGUI);
    }

    public static void print(String message) {
        System.out.println(message);
    }

    public static void setMessage(String msg) {
        message = msg;
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Message Input");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(420, 300);
        frame.setLocationRelativeTo(null);

        JTextArea log = new JTextArea();
        log.setEditable(false);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(log);

        JTextField input = new JTextField();
        JButton sendBtn = new JButton("Enter");

        // shared action for Enter key and button click
        AbstractAction submitAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = input.getText().trim();
                if (userInput.isEmpty()) return;

                setMessage(userInput);

                if ("quit".equals(message)) {
                    // mimic CLI behavior: print final message, then exit
                    print(message);
                    frame.dispose();
                    System.exit(0);
                } else {
                    log.append("> " + message + "\n");
                }
                input.setText("");
                input.requestFocusInWindow();
                // autoscroll
                log.setCaretPosition(log.getDocument().getLength());
            }
        };

        input.addActionListener(submitAction);
        sendBtn.addActionListener(submitAction);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.add(new JLabel("Enter a message:"), BorderLayout.WEST);
        bottom.add(input, BorderLayout.CENTER);
        bottom.add(sendBtn, BorderLayout.EAST);
        bottom.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        frame.setLayout(new BorderLayout());
        frame.add(scroll, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);

        frame.setVisible(true);
        input.requestFocusInWindow();
    }
}

