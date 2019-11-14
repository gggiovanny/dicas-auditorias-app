---
title: Aplicación para auditorias
---

Descripción
===========

Aplicación en Android para validar con un código QR la existencia de los [activos fijos](https://es.m.wikipedia.org/wiki/Activo_fijo) de un consorcio empresarial. La app funciona como una adición a un sistema de inventariado de activos fijos ya existente y está construida para ser interoperativa con él. La validación se llevará a cabo usando la cámara del dispositivo para escanear un sticker pegado en cada activo fijo que contiene un código QR que lo identifica en el sistema de inventarios.

Proyecto asociado
=================

El proyecto sigue el principio [SOLID](https://es.m.wikipedia.org/wiki/SOLID),
por lo que las responsabilidades están divididas. La responsabilidad del modelo
de datos está delegada a una [API](https://es.wikipedia.org/wiki/Web_API)
[RESTful](https://es.wikipedia.org/wiki/Transferencia_de_Estado_Representacional)
y la responsabilidad de la interfaz de usuario y las interacciones con el mismo
están delegadas a la presente aplicación en Android, que básicamente consume los
datos proporcionados por la API y los alimenta con nueva información obtenida
del usuario. Para más información acerca del funcionamiento de esta, ver el
[proyecto adjunto](https://github.com/gggiovanny/dicas-auditorias-api).

Requerimientos funcionales
==========================

-   Ser interoperativo con el sistema web de activos fijos que existe
    actualmente en el consorcio.

-   Permitir especificar en qué empresa y departamento se realizará cada
    auditoria, así como filtrar por la clasificación de los activos fijos.

-   Indicar la última existencia conocida de un activo fijo, cuya información
    proviene de validaciones hechas en auditorías anteriores al mismo activo
    fijo.

-   Validar la existencia de los activos fijos escaneando un código QR que
    contiene una URI con un número que idéntica al activo en el sistema. El
    código QR se encuentra adherido físicamente con etiquetas a cada activo y
    son generados por el sistema de inventariado ya existente.

-   Todos los activos fijos que se incluyan en una auditoría deberán ser
    validados (marcados como que existen o no existen), por lo que está no se
    guardará hasta que sea completada.

-   Permitir marcar manualmente los activos como "No encontrados".

-   Los estatus de existencia de los activos fijos son los siguientes, siendo
    únicamente aptos para guardarse los 2 primeros:

1.  *Encontrado*

2.  *No encontrado*

3.  *Pendiente de validar*

-   La validación de hará de la siguiente manera:

    -   Al comienzo de una nueva auditoría, todos los activos fijos estarán por
        defecto en el estatus de *Pendiente de validar*. Este valor puede ser
        sobrescrito por cualquiera de los otros dos estatus.

    -   Únicamente se podrá marcar como *Encontrado* un activo al que se escanee
        su código QR y este esté incluido en la auditoría en curso. Una vez en
        este estatus, ya no podrá ser cambiado ni sobrescrito por otro.

    -   A un activo en estado de *Pendiente de validar* se le puede cambiar su
        estatus a *No encontrado* con sólo tocar el botón indicador del mismo.
        Una vez en este estado, no puede regresar al anterior, pero un activo
        *No encontrado* puede cambiar su estatus a *Encontrado* si se escanea su
        código QR.

-   Organizar las auditorias en status:

1.  *En progreso*.

2.  *Completada*.

3.  *Guardada*..

-   Las auditorias marcadas como guardadas no permitirán ninguna clase de
    alteración futura, ya que una vez en este estatus, el estatus de sus activos
    es válido para ser referido por auditorías posteriores como "Existencia
    conocida".

-   Las cantidades observadas de los activos en auditorias guardadas serán la
    nueva existencia conocida de dichos activos en auditorias futuras

-   Sistema de autenticación para ingresar a la aplicación usando las mismas
    credenciales existentes ya en el sistema de inventariado.

-   Asociar las auditorías creadas al usuario activo.

-   Asociar las validaciones al usuario activo.

Alcances
========

-   La aplicación será diseñada para ser sencilla de usar.

-   Tendrá un diseño atractivo usando los colores corporativos de la empresa
    (Grupo Dicas).

-   Inicio de sesión con las mismas credenciales usadas en el sistema web de
    activos fijos.

-   Uso de tokens encriptados en cada llamada al servidor para una comunicación
    segura.

-   Uso de Kotlin como lenguaje de programación principal, que proporciona
    ventajas a la hora de programar, y ninguna desventaja al ser interoperativo
    con Java.

-   Uso de prácticas y arquitecturas modernas de programación, que se detallan
    más adelante.

Limitaciones
============

-   Desconocimiento inicial de cómo está hecho el sistema web de activos fijos
    ya existente por parte del desarrollador y del cliente, por lo que habrá que
    realizar un análisis exhaustivo que conlleva tiempo extra invertido.

-   Backend de la aplicación limitado a versiones viejas de PHP (5.4.60) y
    MySQL, ya que es la plataforma sobre la que se ejecuta el sistema de
    inventariado ya existente y cambiarlo conllevaría migrar todo lo ya hecho.

-   Equipo de desarrollo reducido a una persona.

-   Compatible con smartphones con Android mayor a 5.0.

Elección de tecnologías y arquitectura
======================================

-   Backend con una [API RESTful desarrollada en
    PHP](https://github.com/gggiovanny/dicas-auditorias-api) con
    [Laravel](https://laravel.com/).

-   Arquitectura
    [MVVM](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel)
    con [Android Jetpack](https://developer.android.com/jetpack) para la
    aplicación.

-   [JSON Web Tokens](https://jwt.io/) para la autenticación.

-   [Retrofit](https://square.github.io/retrofit/) para el consumo de la API.

-   [Code Scanner](https://github.com/yuriy-budiyev/code-scanner) para la
    lectura de códigos QR.
