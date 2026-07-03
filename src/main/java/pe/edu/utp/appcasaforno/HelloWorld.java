package pe.edu.utp.appcasaforno;

import pe.edu.utp.appcasaforno.presentation.ServerController;

public class HelloWorld {

    public static void main(String[] args) throws Exception {
        var server = new ServerController();
        server.start();
    }
}
