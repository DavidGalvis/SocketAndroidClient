# SocketAndroidClient
Este demo ha sido creado para establecer un hilo de comunicación por socket entre un[ servidor de socket.io en nodeJS](https://github.com/DavidGalvis/SocketNodeServer) y esta app de Android.

**Nota:** Este proyecto ha sido creado con android studio 3.0

Las funcionalidades incluidas son:

* Comunicación con el servidor.
* Servicio en background que se queda a la escucha de los eventos emitidos por el servidor.
* Emisión de mensajes Broadcast desde el servicio para notificar al usuario del estado de la comunicación y la recepción de nuevos mensajes desde el servior.
* Renderizado por pantalla de los datos recibidos desde el servidor.

### Prerequisitos

* Tener un dispositivo o emulador android con una versión igual o mayor a android 4.1 API 16.
* Estar ejecutando el servidor NodeJs para establecer la comunicación.
* Estar conectado a una red que pueda tener acceso a la ip del servidor.

### Ejecución del proyecto

Ejecutar el proyecto en un emulador o generar el APK e instalarlo en un dispositivo android 4.1 API 16 o superior.

**Nota:** Para que el proyecto pueda tener comunicación con el servidor, se debe reescribir la ip definida en el archivo **server_address.xml** por la ip con la que ustedes ejecuten su propio seridor.

## Licencia

Este proyecto está creado bajo licencia MIT - revisa el archivo [LICENSE.md](LICENSE.md) para más detalles.