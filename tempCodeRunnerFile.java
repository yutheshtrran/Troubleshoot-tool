import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.regex.*;

public class NetworkingTestingTools extends JFrame implements ActionListener {

    private JLabel commandLabel;
    private JTextArea inputArea, outputArea;
    private JButton runButton, stopButton;

    private Process process;
    private Thread outputThread;

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_WHITE = "\u001B[37m";

    public NetworkingTestingTools() {
        super("Networking Testing Tools");

        commandLabel = new JLabel("Command:");
        inputArea = new JTextArea(5, 30);
        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(outputArea);

        runButton = new JButton("Run");
        runButton.addActionListener(this);
        stopButton = new JButton("Stop");
        stopButton.addActionListener(this);

        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel1.add(commandLabel);
        panel1.add(inputArea);

        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel2.add(runButton);
        panel2.add(stopButton);

        JPanel panel3 = new JPanel(new BorderLayout());
        panel3.add(panel1, BorderLayout.NORTH);
        panel3.add(scrollPane, BorderLayout.CENTER);
        panel3.add(panel2, BorderLayout.SOUTH);

        setContentPane(panel3);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == runButton) {
            String command = inputArea.getText().trim();
            executeCommand(command);
        } else if (ae.getSource() == stopButton) {
            stopCommand();
        }
    }

    public void executeCommand(String command) {
        try {
            outputArea.append(ANSI_BLUE + "Command: " + command + ANSI_RESET + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());

            ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();

            outputThread = new Thread(new OutputReader());
            outputThread.start();

        } catch (IOException e) {
            outputArea.append(ANSI_WHITE + "Error: " + e.getMessage() + ANSI_RESET + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        }
    }

    public void stopCommand() {
        if (process != null) {
            process.destroy();
        }
    }

    class OutputReader implements Runnable {
        public void run() {
            try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = inputReader.readLine()) != null) {
                    // Check if the line contains an IP address
                    if (containsIPAddress(line)) {
                        outputArea.append(ANSI_YELLOW + line + ANSI_RESET + "\n");
                    } else {
                        outputArea.append(ANSI_WHITE + line + ANSI_RESET + "\n");
                    }
                    outputArea.setCaretPosition(outputArea.getDocument().getLength());
                }
            } catch (IOException e) {
                outputArea.append(ANSI_WHITE + "Error: " + e.getMessage() + ANSI_RESET + "\n");
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            }
        }
    }

    // Helper method to check if a line contains an IP address
    private boolean containsIPAddress(String line) {
        String regex = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        return matcher.find();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NetworkingTestingTools tool = new NetworkingTestingTools();
            tool.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }
}
