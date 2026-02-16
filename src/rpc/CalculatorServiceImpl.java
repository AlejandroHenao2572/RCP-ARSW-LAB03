

/**
 * Implementación real del servicio.
 * Contiene solo la lógica del negocio.
 * No conoce nada de sockets ni red.
 */
public class CalculatorServiceImpl implements CalculatorService {

    @Override
    public int add(int a, int b) {
        // Operacion pura, sin efectos secundarios.
        return a + b;
    }

    @Override
    public int square(int n) {
        // Mantiene el contrato del servicio con logica local simple.
        return n * n;
    }
}
