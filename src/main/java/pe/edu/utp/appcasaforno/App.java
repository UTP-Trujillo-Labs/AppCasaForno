package pe.edu.utp.appcasaforno;

import pe.edu.utp.appcasaforno.infraestructure.server.ServerFacade;

public class App {

    public static void main(String[] args) throws Exception {
        var server = new ServerFacade();
        server.start();
    }
}
