# BTG Pactual - Funds API

API REST para la gestión de fondos de inversión de BTG Pactual. Permite a los clientes vincularse y desvincularse de fondos, consultando su historial de transacciones y saldo disponible.

## Arquitectura

El proyecto sigue una arquitectura hexagonal (puertos y adaptadores) con Java 17 y Spring Boot 3.2.3. Se usa DynamoDB como base de datos bajo un esquema Single-Table Design, y la API se documenta con (Swagger).

### Modelo de datos en DynamoDB

| Entidad       | PK                    | SK              |
|---------------|-----------------------|-----------------|
| Cliente       | `CLIENT#{id}`         | `INFO`          |
| Fondo         | `FUND#{id}`           | `INFO`          |
| Suscripción   | `CLIENT#{clientId}`   | `SUB#{fundId}`  |
| Transacción   | `SUB#{subscriptionId}`| `TX#{txId}`     |

## Cómo ejecutar localmente

Necesitas Java 17, Maven y Docker.

1. Levanta DynamoDB local:
```bash
docker-compose up -d
```

2. Ejecuta la aplicación:
```bash
./mvnw spring-boot:run
```

La app arranca en `http://localhost:8080`. Las tablas y datos de prueba se crean automáticamente al iniciar.

Para ver la documentación de la API:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI spec: http://localhost:8080/v3/api-docs

## Tests

```bash
./mvnw clean test
```

Incluye pruebas unitarias, de integración con Testcontainers, y validaciones de arquitectura.

## Consulta SQL (Parte 2)

Consulta para identificar clientes que tienen inscrito algún producto disponible solo en las sucursales que visitan:

```sql
SELECT DISTINCT c.nombre, c.apellidos
FROM Cliente c
INNER JOIN Inscripcion i ON i.idCliente = c.id
WHERE
    EXISTS (
        SELECT 1 FROM Disponibilidad d
        WHERE d.idProducto = i.idProducto
    )
    AND NOT EXISTS (
        SELECT 1 FROM Disponibilidad d
        WHERE d.idProducto = i.idProducto
          AND d.idSucursal NOT IN (
              SELECT v.idSucursal FROM Visitan v
              WHERE v.idCliente = c.id
          )
    );
```

## Despliegue en AWS

El proyecto incluye una plantilla SAM (`template.yaml`) para desplegarlo como Lambda + API Gateway + DynamoDB. Todo queda dentro del Free Tier.

```bash
sam build
sam deploy --guided   # solo la primera vez, después: sam deploy
```

Una vez desplegado, SAM muestra la URL del API Gateway en la salida. Los endpoints disponibles son:

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/funds` | Lista todos los fondos |
| GET | `/api/v1/clients/{id}` | Consulta un cliente y su saldo |
| POST | `/api/v1/clients/{id}/subscriptions` | Suscribirse a un fondo |
| DELETE | `/api/v1/clients/{id}/subscriptions/{fundId}` | Cancelar suscripción |
| GET | `/api/v1/clients/{id}/subscriptions/{fundId}/history` | Historial de transacciones |

### Documentación de la API en producción

En local, Swagger UI funciona directamente en `http://localhost:8080/swagger-ui.html`. Sin embargo, al desplegar en AWS, API Gateway no sirve correctamente los archivos estáticos de la interfaz UI, por lo que la forma oficial de consultar y probar la API en producción es mediante **Swagger Editor**:

1. Copia o entra a la URL del descriptor JSON de OpenAPI en producción:
   ```text
   https://p6ugk2x683.execute-api.us-east-1.amazonaws.com/Prod/v3/api-docs
   ```
2. Ve a [Swagger Editor](https://editor.swagger.io) y pega el JSON, o impórtalo usando `File > Import URL`.
3. ¡Listo! La API está configurada con CORS completo y el Server URL relativo (`/Prod`), por lo que puedes usar el botón **Try it out** para probar todos los endpoints reales de AWS directamente desde el editor en tu navegador.
