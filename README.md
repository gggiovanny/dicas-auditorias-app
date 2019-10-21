## Descripción
Aplicación en Android para validar con un código QR la existencia de los [activos fijos][activo_fijo] de un consorcio empresarial. La app funciona como una adición a un sistema de inventariado de activos fijos ya existente y está construida para ser interoperativa con él.

## Objetivo
Hacer una aplicación que permita validar que un activo fijo existe realmente en la ubicación donde se supone debe estar. La validación se llevará a cabo usando la cámara del dispositivo para escanear un sticker pegado en cada activo fijo que contiene un código QR que lo identifica en el sistema de inventarios.

## Antecedentes
Grupo Dicas es un consorcio de empresas [...] quen ya tiene un sistema [...].

### Proyecto asociado
El proyecto siguen el principio [SOLID][solid], por lo que las responsabilidades están divididas.
La responsabilidad del modelo de datos está delegada a una [API][api] [RESTful][rest] y la responsabilidad de la interfaz de usuarioy las interacciones con el mismo están delegadas a la presente aplicación en Android, que básicamente consume los datos proporcionados por la API y los alimenta con nueva información obtenida del usuario. Para más información acerca del funcionamiento de la misma, ver su [proyecto adjunto][auditorias_api].

## Requerimientos funcionales
+ Ser iteroperativo con el sistema web de activos fijos que existe actualmente en el consorcio.
+ Permitir especificar en que empresa y departamento  se realizará cada auditoria, así como filtrar por la clasificación de los activos fijos.
+ Indicar la última existencia conocida de un activo fijo, cuya información proviene de validaciones hechas en auditorías anteriores al mismo activo fijo.
+ Validar la existencia de los activos fijos escaneando un codigo QR que contiene una URI con un número que idéntica al activo en el sistema. El código QR se encuentra adherido físicamente con etiquetas a cada activo y son generados por el sistema de inventariado ya existente.
+ Todos los activos fijos que se incluyan en una auditoría deberán ser validados (marcados como que existen o no existen), por lo que está no se guardará hasta que sea completada.
+ Permitir marcar manualmente los activos como "No encontrados".
+ Los estatus de existencia de los activos fijos son los siguientes, siendo únicamente aptos para guardarse los 2 primeros:
  1. *Encontrado*
  2. *No encontrado*
  3. *Pendiente de validar*
  + La validación de hará de la siguiente manera:
    - Al comienzo de una nueva auditoría, todos los activos fijos estarán por defecto en el estatus de *Pendiente de validar*. Este valor puede ser sobreescrito por cualquiera de los otros dos estatus.
    - Únicamente se podrá marcar como *Encontrado* un activo al que se escanee su código QR y este esté incluído en la auditoría en curso. Una vez en este estatus, ya no podrá ser cambiado ni sobreescrito por otro.
    - A un activo en estado de *Pendiente de validar* se le puede cambiar su estatus a *No encontrado* con sólo tocar el botón indicador del mismo. Una vez en éste estado, no puede regresar al anterior, pero un activo *No encontrado* puede cambiar su estatus a *Encontrado* si se escanea su código QR.
+ Organizar las auditorias en status: 
  1. *En progreso*.
  2. *Completada*.
  3. *Guardada*..
+ Las auditorias marcadas como guardadas no permitirán ninguna clase de alteración futura, ya que una vez en este estatus, el estatus de sus activos es válido para ser referido por auditorías posteriores como "Existencia conocida".
+ Las cantidades observadas de los activos en auditorias guardadas serán la nueva existencia conocida de dichos activos en auditorias futuras
+ Sistema de autenticación para ingresar a la aplicación usando las mismas credenciales existentes ya en el sistema de inventariado.
+ Asociar las auditoría creadas al usuario activo.
+ Asociar las validaciones al usuario activo.

## Alcances


## Limitaciones técnicas 
+ Backend de la aplicación con PHP 5.4.60 y MySQL, ya que es la plataforma sobre la que se ejecuta el sistema de inventariado ya existente.
+ Equipo de desarrollo reducido a una persona (o sea, yo solo :c ).



## Calendario

## Riesgos


# Maquetas
## Casos de Uso

### Tabla de casos de uso

## Diagrama de secuencia
	


## Elección de tecnologías y arquitectura
+ Backend con una [API RESTful desarrollada en PHP](https://github.com/gggiovanny/dicas-auditorias-api) con [Laravel](https://laravel.com/).
+ Arquitectura [MVVM](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel) con  [Android  Jetpack](https://developer.android.com/jetpack) para la aplicación.
+ [Json Web Tokens](https://jwt.io/) para la autenticación.
+ [Retrofit](https://square.github.io/retrofit/) para el consumo de la API.
+ [Code Scanner](https://github.com/yuriy-budiyev/code-scanner) para la lectura de códigos QR.


[activo_fijo]:https://es.m.wikipedia.org/wiki/Activo_fijo
[solid]:https://es.m.wikipedia.org/wiki/SOLID
[auditorias_api]: https://github.com/gggiovanny/dicas-auditorias-api
[api]:https://es.wikipedia.org/wiki/Web_API
[rest]:https://es.wikipedia.org/wiki/Transferencia_de_Estado_Representacional
