# RCP-ARSW-LAB03

## Realizado por
- David Alejandro Patacon Henao
- Daniel Felipe Hueso Rueda

## Implementacion RPC
Este laboratorio incluye una implementacion simple de RPC sobre sockets TCP.
La idea central es separar la logica del negocio de los detalles de red:

- El cliente expone una interfaz local (`CalculatorService`) pero en realidad
  envia requests por red usando un stub.
- El servidor escucha conexiones TCP, interpreta el request y ejecuta el metodo
  correspondiente en una implementacion real del servicio.
- Cliente y servidor comparten el contrato para asegurar compatibilidad.

### Componentes principales
1. **`CalculatorService`**
	Interfaz que define los metodos disponibles para invocacion remota.
	Es el contrato que comparten cliente y servidor.

2. **`CalculatorServiceImpl`**
	Implementacion real del servicio. Contiene la logica de negocio y no tiene
	dependencias de red. Esto permite probar la logica de forma aislada.

3. **`CalculatorClientStub`**
	Stub del cliente. Empaqueta la llamada en un request de texto, se conecta
	al servidor, envia la solicitud y parsea la respuesta.

4. **`RcpServer`**
	Servidor TCP que acepta clientes y despacha metodos en hilos independientes.
	Interpreta el request, ejecuta el metodo y construye la respuesta.

5. **`RpcProtocol`**
	Utilidad para parsear requests y construir responses con un formato comun.

### Protocolo de mensajes
El protocolo usa pares clave=valor separados por `;`.
Las claves esperadas son:

- `id`: identificador unico para correlacionar request y response.
- `method`: nombre del metodo a invocar.
- `params`: parametros separados por coma.

Ejemplo de request:

```
id=123;method=add;params=2,3
```

Ejemplo de response exitosa:

```
id=123;ok=true;result=5
```

Ejemplo de response con error:

```
id=123;ok=false;error=Unknown method: foo
```

### Flujo de una llamada RPC
1. El cliente invoca `add(2,3)` en el stub.
2. El stub crea un request con `id`, `method` y `params`.
3. El request se envia por un socket TCP al servidor.
4. El servidor parsea el request y ejecuta el metodo en `CalculatorServiceImpl`.
5. El servidor construye la respuesta y la envia de vuelta.
6. El cliente parsea la respuesta, valida el `id` y retorna el resultado.

## Ejemplo de uso
1. Inicia el servidor:

```bash
cd src\rpc
javac *.java
java RcpServer
```

2. En otra terminal, ejecuta el cliente:

```bash
cd src\rpc
java RpcClientMain
```

Salida esperada:

```text
add(2,3) = 5
square(9) = 81
```

## Implementacion P2P
Esta seccion describe una topologia P2P simple con un tracker central que
mantiene el registro de peers activos. Cada peer actua como cliente y servidor:
se registra en el tracker, escucha conexiones entrantes y puede consultar la
lista de peers disponibles.

### Componentes principales
1. **`TrackerServer`**
	Servidor central que acepta comandos de registro y consulta. Mantiene un
	mapa concurrente de peers y sus direcciones.

2. **`TrackerClient`**
	Cliente liviano que se comunica con el tracker. Expone operaciones como
	`register()` y `listPeers()`.

3. **`PeerNode`**
	Nodo P2P. Al iniciar, se registra en el tracker, abre un puerto de escucha
	y permite consultar peers desde consola.

4. **`PeerMain`**
	Punto de entrada que crea el peer con parametros de ejecucion.

5. **`P2PProtocol`**
	Placeholder para constantes del protocolo. Puede extenderse con comandos o
	formatos mas estructurados.

### Protocolo de mensajes con el tracker
El tracker trabaja con mensajes de texto y comandos simples:

- `REGISTER <peerId> <peerPort>`: registra un peer con su puerto de escucha.
- `LIST`: solicita el listado de peers.

Respuesta esperada:

- `OK` para registros exitosos.
- `PEERS peer1@ip:port,peer2@ip:port` al listar.

### Flujo de inicio de un peer
1. Se inicia el tracker.
2. El peer se registra con `REGISTER` y su puerto local.
3. El peer abre un `ServerSocket` y queda esperando conexiones.
4. Desde la consola se puede escribir `peers` para consultar el tracker.

## Ejemplo de uso P2P
1. Inicia el tracker:

```bash
cd src\p2p
javac *.java
java TrackerServer
```

2. En otra terminal, inicia un peer:

```bash
cd src\p2p
java PeerMain peerA 7001 127.0.0.1
```

3. En otra terminal, inicia un segundo peer:

```bash
cd src\p2p
java PeerMain peerB 7002 127.0.0.1
```

4. En la consola del peer, escribe:

```text
peers
```

Salida esperada (ejemplo):

```text
peerA -> 127.0.0.1:7001
peerB -> 127.0.0.1:7002
```



