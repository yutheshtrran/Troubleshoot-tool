//GROUP-MEMBERS//

/*21/ENG/075 LALITHAMBIKAI.M
21/ENG/088 M.M.MUHTHASEEM
21/ENG/118 R.LAKSHAN
21/ENG/131 S.YUTHESHTRRAN*/


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class NetworkingTestingTools extends JFrame implements ActionListener {

    private JLabel commandLabel;
    private JTextArea inputArea, outputArea;
    private JButton runButton, stopButton;

    private Process process;
    private Thread outputThread;

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
            outputArea.setForeground(Color.WHITE);
            outputArea.setBackground(Color.BLACK);
        } else if (ae.getSource() == stopButton) {
            stopCommand();
        }
    }

    public void executeCommand(String command) {
        try {
            outputArea.append("Command: " + command + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());

            ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();

            outputThread = new Thread(new OutputReader());
            outputThread.start();

        } catch (IOException e) {
            outputArea.append("Error: " + e.getMessage() + "\n");
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
                    outputArea.append(line + "\n");
                    outputArea.setCaretPosition(outputArea.getDocument().getLength());
                }
            } catch (IOException e) {
                outputArea.append("Error: " + e.getMessage() + "\n");
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NetworkingTestingTool tool = new NetworkingTestingTool();
            tool.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }
}
