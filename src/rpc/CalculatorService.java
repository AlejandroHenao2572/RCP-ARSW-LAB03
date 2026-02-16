/**
 * Contrato RPC.
 * Define los métodos que pueden ser invocados remotamente.
 * Cliente y servidor deben compartir esta interfaz.
 */
public interface CalculatorService {

    /**
     * Suma dos enteros.
     */
    int add(int a, int b);

    /**
     * Calcula el cuadrado de un número.
     */
    int square(int n);
}
