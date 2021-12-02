public class ConnectionSettings {
    private String RemoteIp;
    private int RemotePort;
    private String RemoteAeTitle;
    private String LocalAeTitle;
    private int LocalPort;
    private String Path;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemoteIp() {
        return RemoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        RemoteIp = remoteIp;
    }

    public int getRemotePort() {
        return RemotePort;
    }

    public void setRemotePort(int remotePort) {
        RemotePort = remotePort;
    }

    public String getRemoteAeTitle() {
        return RemoteAeTitle;
    }

    public void setRemoteAeTitle(String remoteAeTitle) {
        RemoteAeTitle = remoteAeTitle;
    }

    public String getLocalAeTitle() {
        return LocalAeTitle;
    }

    public void setLocalAeTitle(String localAeTitle) {
        LocalAeTitle = localAeTitle;
    }

    public int getLocalPort() {
        return LocalPort;
    }

    public void setLocalPort(int localPort) {
        LocalPort = localPort;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    ConnectionSettings(){

    }

    ConnectionSettings(String RemoteIp, int RemotePort, String RemoteAeTitle, String LocalAeTitle, int LocalPort, String Path){
        this.RemoteIp = RemoteIp;
        this.RemotePort = RemotePort;
        this.RemoteAeTitle = RemoteAeTitle;
        this.LocalAeTitle = LocalAeTitle;
        this.LocalPort = LocalPort;
        this.Path = Path;
    }
}
