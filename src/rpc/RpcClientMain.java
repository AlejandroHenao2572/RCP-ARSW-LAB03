/**
 * Cliente de prueba del sistema RPC.
 */
public class RpcClientMain {

    public static void main(String[] args) {
        // Stub que encapsula la comunicacion con el servidor.
        CalculatorService calc =
                new CalculatorClientStub("127.0.0.1", 5000);

        // Invocaciones remotas como si fueran locales.
        System.out.println("add(2,3) = " + calc.add(2, 3));
        System.out.println("square(9) = " + calc.square(9));
    }
}
