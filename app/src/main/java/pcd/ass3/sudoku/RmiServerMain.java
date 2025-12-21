package pcd.ass3.sudoku;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import pcd.ass3.sudoku.communication.rmi.server.RmiServer;
import pcd.ass3.sudoku.communication.rmi.server.RmiServerImpl;

public class RmiServerMain {

    private final static int OBJ_PORT = 0;
    public static void main(String[] args) {
        RmiServer server;
        try {
            LocateRegistry.createRegistry(1099);
            server = new RmiServerImpl();
            var stubServer = (RmiServer) UnicastRemoteObject.exportObject(server, OBJ_PORT);
            Naming.rebind("rmi://localhost:1099/RmiServer", stubServer);
            System.out.println("Server RMI pronto!");
        } catch (RemoteException | MalformedURLException ex) {
            System.getLogger(RmiServerMain.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}
