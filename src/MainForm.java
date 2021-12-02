import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

public class MainForm extends TrayFrame{
    private ConnectionSettings conSet;
    private JPanel mainPane;
    private JTextField ipaddress;
    private JTextField port;
    private JTextField rAeTitle;
    private JTextField locAeTitle;
    private JTextField path;
    private JButton browseButton;
    private JButton saveButton;
    private JButton stopButton;
    private JButton startButton;
    public JLabel status;
    private JSpinner spinner1;
    private JTextField locPort;
    public static Timer timer;
    public static TimerTask timerTask;
    public static Integer timeToUpdate = 1;
    public static Preferences userPrefs;
    boolean state;

    public MainForm() throws Exception {
        super();

        userPrefs = Preferences.userRoot().node("bitServerConsole");
        state = userPrefs.getBoolean("state", false);
        ipaddress.setText(userPrefs.get("remoteip", "192.168.1.58"));
        port.setText(userPrefs.get("remoteport", "4242"));
        rAeTitle.setText(userPrefs.get("remoteae", "ORTHANC"));
        locAeTitle.setText(userPrefs.get("localae", "CLIENT"));
        locPort.setText(userPrefs.get("localport", "4243"));
        path.setText(userPrefs.get("path", "C:\\"));
        timeToUpdate = userPrefs.getInt("timeToUpdate",60);
        spinner1.setValue(timeToUpdate);
        userPrefs.putInt("timeToUpdate", (Integer) spinner1.getValue());
        conSet = new ConnectionSettings(ipaddress.getText(),Integer.parseInt(port.getText()),rAeTitle.getText(),locAeTitle.getText(),
                Integer.parseInt(locPort.getText()),path.getText());
        startButton.setEnabled(!state);
        stopButton.setEnabled(state);
        setContentPane(mainPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(250,250,400,325);
        setLocationRelativeTo(null);
        mainPane.setBackground(Color.lightGray);
        mainPane.setForeground(Color.white);
        Font font = new Font("Verdana", Font.BOLD, 18);
        mainPane.setFont(font);
        setTitle("bitServer console");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        startButton.addActionListener(e -> {
            state = true;
            stopButton.setEnabled(true);
            startButton.setEnabled(false);
            userPrefs.putBoolean("state", state);
            try {
                timerStart();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        stopButton.addActionListener(e -> {
            state = false;
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            userPrefs.putBoolean("state", state);
            timerStop();
        });

        if(state){
            timerStart();
        }else{
            timerStop();
        }

        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Выбор директории");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showOpenDialog(MainForm.this);
            if (result == JFileChooser.APPROVE_OPTION )
                path.setText(fileChooser.getSelectedFile().getPath());
        });

        saveButton.addActionListener(e -> {
            userPrefs.put("remoteip", ipaddress.getText());
            userPrefs.put("remoteport", port.getText());
            userPrefs.put("remoteae", rAeTitle.getText());
            userPrefs.put("localae", locAeTitle.getText());
            userPrefs.put("localport", locPort.getText());
            userPrefs.put("path", path.getText());
            timeToUpdate = (Integer) spinner1.getValue();
            userPrefs.putInt("timeToUpdate", timeToUpdate);
            conSet = new ConnectionSettings(ipaddress.getText(),Integer.parseInt(port.getText()),
                    rAeTitle.getText(),locAeTitle.getText(),Integer.parseInt(locPort.getText()),path.getText());
        });

        SpinnerModel spinnerModel = new SpinnerNumberModel(60,5,1000,1);
        spinner1.setModel(spinnerModel);
        spinnerModel.setValue(timeToUpdate);
        createNewDatabase("db");
    }

    public void timerStart() throws Exception {
        timerTask = null;
        timer = null;
        if(!ipaddress.getText().equals("")&&(!port.getText().equals(""))&&(!rAeTitle.getText().equals(""))&&
                (!locAeTitle.getText().equals(""))&&(!locPort.getText().equals(""))&&(!path.getText().equals(""))) {
            try {
                timerTask = new MainTask(conSet, path.getText());
                timer = new Timer(true);
                timer.scheduleAtFixedRate(timerTask, 0, timeToUpdate * 60 * 1000);
                status.setText("Status: OK");
            }catch (Exception e){
                status.setText("Status: Error "+e.getMessage());
            }
        }else{
            status.setText("Status: One or more setting fields is empty");
        }
    }

    public void timerStop(){
        timerTask = null;
        timer = null;
    }

    public static void createNewDatabase(String fileName) throws URISyntaxException {

        String url = "jdbc:sqlite:"+Paths.get(MainForm.class.getResource(".").toURI()) +"_"+ fileName;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        final MainForm MainFormNew = new  MainForm();
        Image icon = ImageIO.read(MainForm.class.getResourceAsStream("/icon.png"));
        MainFormNew.setIconImage(icon);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
