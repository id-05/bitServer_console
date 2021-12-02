import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimerTask;
import java.util.stream.Collectors;
import org.dcm4che2.tool.dcmsnd.DcmSnd;

public class MainTask extends TimerTask {

    static ConnectionSettings conSet;
    String pathToFolder;

    MainTask(ConnectionSettings conSet, String pathToFolder){
        MainTask.conSet = conSet;
        this.pathToFolder = pathToFolder;
    }

    @Override
    public void run() {
        try {
            List<File> fileList = Files.walk(Paths.get(pathToFolder))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            for(File bUfFile:fileList){
                Path path = bUfFile.toPath();
                BasicFileAttributes basicFileAttributes = Files.getFileAttributeView(path, BasicFileAttributeView.class)
                        .readAttributes();
                LocalDate nowDate = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(basicFileAttributes.creationTime().toString().substring(0,10),formatter);

                if(true/*nowDate.equals(date)*/){
                    //передать в очередь отправки
                    //connection.sendDicom("/instances", Files.readAllBytes(bUfFile.toPath()));
                    sendDcmFile(bUfFile);
                }
            }
        } catch (IOException e) {
           System.out.println(e.getMessage());
        }
    }

    public static void sendDcmFile(File dcmFile) {
        DcmSnd dcmsnd = new DcmSnd(conSet.getLocalAeTitle());
        dcmsnd.setLocalPort(conSet.getLocalPort());
        dcmsnd.setLocalHost("localhost");
        dcmsnd.setCalledAET(conSet.getRemoteAeTitle());
        dcmsnd.setRemoteHost(conSet.getRemoteIp());
        dcmsnd.setRemotePort(conSet.getRemotePort());
        dcmsnd.setOfferDefaultTransferSyntaxInSeparatePresentationContext(false);
        dcmsnd.setSendFileRef(false);
        dcmsnd.setStorageCommitment(false);
        dcmsnd.setPackPDV(true);
        dcmsnd.setTcpNoDelay(true);

        dcmsnd.addFile(dcmFile);

        dcmsnd.configureTransferCapability();
        try {
            dcmsnd.start();
        } catch (Exception e) {
            System.out.println("ERROR: Failed to start server for receiving " +
                    "Storage Commitment results:" + e.getMessage());
            return;
        }

        try {
            dcmsnd.open();
            dcmsnd.send();
            dcmsnd.close();
            System.out.println("Released connection to " + "PACSWFM01");
        } catch (Exception e) {
            System.out.println("ERROR: Failed to establish association:"
                    + e.getMessage());
        } finally {
            dcmsnd.stop();
        }
    }
}
