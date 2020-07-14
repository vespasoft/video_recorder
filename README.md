El proyecto se ha desarrollado basándose en el patrón Clean Arquitecture el cual divide el código en cuatro capas Independiente, esto facilita el mantenimiento del código y la aplicación de pruebas unitarias.

En el proyecto se han utilizado los siguientes frameworks u/y dependencias

 - rxjava2 y rxandroid: Se utilizo el patrón Observer para las peticiones asíncronas y manejo procesos de larga duración en la cama Domain (UseCase).
 - SQLite: Se ha implementado una capa llamada LocalDataSource donde se implementa la base de datos SQLite sin utilizar ningún framework (no quise utilizar ROOM).
 - Koin: Se utilizó el framework Koin para la inyección de dependencias en ves de Dagger2, ya que Koin es mucho más intuitivo para pequeños proyectos.
 - Dexter: Se utilizó la librería dexter para el manejo de solicitud de permisos al usuario.
 - Glide: Librería para tratamiento de imágenes.
 - ViewModel: La capa presenter se reemplazo por el componente ViewModel de JetPack
 - LiveData: Se ha utilizado LiveData para notificar a la vista y enviar los datos recibidos de la capa Domain.

Todos los esfuerzos para el desarrollo de la aplicación video recorder fueron enfocados en el diseño de la arquitectura de la aplicación y en la re-utilización
de código en el diseño de la vista haciendo uso de custom views, sealed class y abstracción. De esta forma se cumple con unos de los principios SOLID open/close
(toda clase debe estar cerrada para modificar y abierta para extender)

A continuación mostraré algunos diagramas de clases que describe las capas donde se han aplicado este tipo de clases.