## Descripción
Aplicación en Android para validar con un código QR la existencia de los activos fijos de un consorcio empresarial. La app funciona como una adición a un sistema de inventariado de activos fijos ya existente y está construida para ser interoperativa con él.

## Objetivo
Hacer una aplicación que permita validar que un activo fijo existe realmente en la ubicación donde se supone debe estar. La validación se llevará a cabo usando la cámara del dispositivo para escanear un sticker pegado en cada activo fijo que contiene un código QR que lo identifica en el sistema de inventarios.

## Antecedentes
Grupo Dicas es un consorcio de empresas [...] quen ya tiene un sistema [...].

### Proyecto asocioado
API RESTful: https://github.com/gggiovanny/dicas-auditorias-api

## Requerimientos funcionales
+ Ser iteroperativo con el sistema web de activos fijos que existe actualmente en el consorcio.
+ Permitir especificar en que empresa y departamento  se realizará cada auditoria, así como filtrar por la clasificación de los activos fijos.
+ Indicar la última existencia conocida de un activo fijo.
+ Validar la existencia de los activos fijos escaneando un codigo QR que contiene una URI que lleva al la información de dicho artículo en el sistema web de activos fijos ya existente. Dicha URI contiene un número que idéntica al activo en el sistema. El código QR se encuentra adeherido con etiquetas en los activos y son generados por el sistema de inventariado.
+ Permitir marcar los activos como "No encontrados".
+ Organizar las auditorias en status: 
  1. *En progreso*.
  2. *Completada*.
  3. *Guardada*..
+ Las auditorias marcadas como guardadas no permitirán ninguna clase de alteración futura, ya que...
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
+ Arquitectura [MVVM](https://en.wikiped