import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class OrthancRestApi {

    private String authentication;
    private final String ipaddress;
    private final String port;
    private final String login;
    private final String password;

    public OrthancRestApi(String ipaddress, String port, String login, String password){
        this.ipaddress = ipaddress;
        this.port = port;
        this.login = login;
        this.password = password;
    }

    HttpURLConnection makeGetConnection() throws Exception {
        HttpURLConnection conn;
        String fulladdress = "http://"+ ipaddress+":"+ port;
        URL url = new URL(fulladdress+ "/statistics");
        authentication = Base64.getEncoder().encodeToString((login+":"+password).getBytes());
        conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("GET");
        if(authentication != null){
            conn.setRequestProperty("Authorization", "Basic " + authentication);
        }
        conn.getResponseMessage();
        return conn;
    }

    public void sendDicom(String apiUrl, byte[] post) {
        HttpURLConnection conn;
        try {
            String fulladdress = "http://" + ipaddress + ":" + port;
            URL url=new URL(fulladdress+apiUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            authentication = Base64.getEncoder().encodeToString((login+":"+password).getBytes());
            if(this.authentication != null){
                conn.setRequestProperty("Authorization", "Basic " + this.authentication);
            }
            conn.setRequestProperty("content-length", Integer.toString(post.length));
            conn.setRequestProperty("content-type", "application/dicom");
            OutputStream os = conn.getOutputStream();
            os.write(post);
            os.flush();
            conn.getResponseMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
