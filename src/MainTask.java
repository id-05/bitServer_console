import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class MainTask extends TimerTask {

    OrthancRestApi connection;
    String pathToFolder;

    MainTask(OrthancRestApi connection, String pathToFolder){
        this.connection = connection;
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

                if(nowDate.equals(date)){
                    //передать в очередь отправки
                    HttpURLConnection conn = connection.sendDicom("/instances", Files.readAllBytes(bUfFile.toPath()));
                }
            }
        } catch (IOException e) {
           System.out.println(e.getMessage());
        }
    }
}
