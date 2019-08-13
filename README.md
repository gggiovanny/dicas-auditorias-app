# Descripción
Aplicación en Android para hacer auditorias de los activos fijos del consorcio empresarias. La app funciona como una adición a un sistema de inventariado ya existente y está construida para ser interoperativa con él.
### Proyecto asocioado
API RESTful: https://github.com/gggiovanny/dicas-auditorias-api
## Requerimientos funcionales
+ Elegir a qué area se le realizará la auditoria. Puede ser una empresa o un departamento.
+ Filtrar los activos por clasificación, descripción o categoría.
+ Mostrar la cantidad en existencia conocida de los activos.
+ Marcar los activos como revisados escaneando un codigo QR. Dicho código se encuentra adeherido con etiquetas en los activos y son generados por el sistema de inventariado.
+ Permitir ingresar la cantidad observada de los activos.
+ Organizar las auditorias en status: 
  1. *En progreso*.
  2. *Completada*.
  3. *Guardada*.
+ Sistema de autenticación para ingresar a la aplicación usando las mismas credenciales existentes ya en el sistema de inventariado.
+ Asociar la auditoría al usuario activo.
+ Las cantidades observadas de los activos en auditorias guardadas serán la nueva existencia conocida de dichos activos en auditorias futuras.
+ Las auditorias marcadas como guardadas no permitirán ninguna clase de alteración futura.

## Limitaciones técnicas 
+ Backend de la aplicación con PHP 5.4.60 y MySQL, ya que es la plataforma sobre la que se ejecuta el sistema de inventariado ya existente.
+ Equipo de desarrollo reducido a una persona (o sea, yo solo :c ).

## Elección de tecnologías y arquitectura
+ Backend con una [API RESTful desarrollada en PHP](https://github.com/gggiovanny/dicas-auditorias-api) con [Laravel](https://laravel.com/).
+ Arquitectura [MVVM](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel) con  [Android  Jetpack](https://developer.android.com/jetpack) para la aplicación.
+ [Json Web Tokens](https://jwt.io/) para la autenticación.
+ [Retrofit](https://square.github.io/retrofit/) para el consumo de la API.
+ [Code Scanner](https://github.com/yuriy-budiyev/code-scanner) para la lectura de códigos QR.
