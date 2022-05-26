# Spaceship

- Es una aplicación escrita en Java 17, JavaFX y compilable con GraalVM para una ejecución nativa.
- Permite hacer peticiones a urls y visualizar la respuesta.
- La idea nació del excesivo consumo que generaba Postman y solo necesitaba hacer un simple request para ver la respuesta.
- La versión inicial permite lo siguiente: 
  - tener varias pestañas de peticiones
  - agregar headers
  - enviar un body
  - ver el response
  - guardar los datos ingresados para la petición
  - recuperar los datos ingresados en la petición
- Compilar la app:

```
mvn clean package
```
- Lanzar la app:
```
java --module-path  target/spaceship-1.0.0.jar:target/lib/  --module com.github.sebastian4j.spaceship/com.github.sebastian4j.spaceship.Requester
```
- Para compilarlo en forma nativa:
  - descargar graalvm-ce-java17-22.1.0 (me imagino que funciona con versiones posteriores)
  - definir GRAALVM_HOME:
```
export GRAALVM_HOME=$HOME/code/fx/graalvm-ce-java17-22.1.0/
```
  - compilarlo:
```
mvn gluonfx:build
```

### Enlaces de interés
- https://openjfx.io/
- https://www.graalvm.org/
