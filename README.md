# Spaceship :rocket:

![v1.5.0](https://github.com/sebastian4j/spaceship/blob/main/images/v1.5.0.png?raw=true)

- Es una aplicación escrita en Java 17, JavaFX y compilable con GraalVM para una ejecución nativa.
- Permite hacer peticiones a urls y visualizar la respuesta.
- La idea nació del excesivo consumo que generaba Postman y solo necesitaba hacer un simple request para ver la respuesta.
- La versión actual permite lo siguiente: 
  - tener varias pestañas de peticiones
  - agregar headers
  - enviar un body
  - ver el response
  - ver los headers del response
  - guardar los datos ingresados para la petición
  - recuperar los datos ingresados en la petición
  - cancelar la petición solicitada
  - guardar el response obtenido
  - ver los milisegundos y bytes transferidos
  - muestra el código de estado http obtenido
  - guardar el response directo a un archivo sin mostrar el contenido
- Compilar la app:

```
mvn clean package
```
- Lanzar la app:
```
linux:
java --module-path  target/spaceship-1.5.0.jar:target/lib/  --module com.github.sebastian4j.spaceship/com.github.sebastian4j.spaceship.Spaceship

windows:
java --module-path  target/spaceship-1.5.0.jar;target/lib/  --module com.github.sebastian4j.spaceship/com.github.sebastian4j.spaceship.Spaceship
```
- Para compilarlo en forma nativa:
  - descargar graalvm-ce-java17-22.1.0 (me imagino que funciona con versiones posteriores)
  - definir GRAALVM_HOME:
```
export GRAALVM_HOME=$HOME/code/fx/graalvm-ce-java17-22.1.0/
```
  - compilarlo nativo:
```
mvn gluonfx:build
```

### Enlaces de interés
- https://openjfx.io/
- https://www.graalvm.org/
- el nuevo estilo usado es de: https://github.com/JFXtras/jfxtras-styles
- https://docs.gluonhq.com/#_platforms

##### Sobre el nombre
A mi hijo le gustan las cosas relacionadas con el espacio, así que esa es el principal motivo, también sirve como concepto para explicarle cómo viajan y llegan los datos a la aplicación :smiley:


### ícono
el icono lo encontré en: https://www.svgrepo.com/download/148893/rocket.svg
