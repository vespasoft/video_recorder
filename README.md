Video Recorder
============
Grabadora de video con funcionalidades básicas desarrollada en Android nativo con el lenguaje de programación Kotlin.

### Arquitectura ###

El proyecto se ha desarrollado basándose en el patrón Clean Arquitecture el cual divide el código en cuatro capas Independiente, esto facilita el mantenimiento del código y la aplicación de pruebas unitarias.

![Clean Arquitecture Logo](https://res.cloudinary.com/practicaldev/image/fetch/s--T7GIdw6s--/c_limit%2Cf_auto%2Cfl_progressive%2Cq_auto%2Cw_880/https://miro.medium.com/max/1488/1%2AD1EvAeK74Gry46JMZM4oOQ.png "An exemplary image")

*Application's layers*
  * Data
  * Domain
  * Presentation
  * View

### Frameworks y Dependencias ###
 * rxjava2 y rxandroid: Se utilizo el patrón Observer para las peticiones asíncronas y manejo procesos de larga duración en la cama Domain (UseCase).
 * SQLite: Se ha implementado una capa llamada LocalDataSource donde se implementa la base de datos SQLite sin utilizar ningún framework (no quise utilizar ROOM).
 * Koin: Se utilizó el framework Koin para la inyección de dependencias en ves de Dagger2, ya que Koin es mucho más intuitivo para pequeños proyectos.
 * Dexter: Se utilizó la librería dexter para el manejo de solicitud de permisos al usuario.
 * Glide: Librería para tratamiento de imágenes.
 * ViewModel: La capa presenter se reemplazo por el componente ViewModel de JetPack
 * LiveData: Se ha utilizado LiveData para notificar a la vista desde el ViewModel y enviar los datos recibidos de la capa Domain.
 
 ### Patrones ###
  * Repository: Se ha implementado el patrón repository en la capa de datos, de esta forma se oculta el origen y la lógica de los datos al resto de capas.
  * Dependency Injection: Se ha implementado el patrón Inyección de dependencias usando el framework Koin y cumpliendo con el **Dependency Inversion Principle**
  * ViewModelFactory: Extendiendo de la clase ViewModelProvider.Factory de jetPack se ha implementado el patrón Factory para la instanciación de los ViewModel. 

### Análisis y Desarrollo ###  
Todos los esfuerzos para el desarrollo de la aplicación fueron enfocados en el diseño de la arquitectura de la aplicación y en la re-utilización
de código tanto en el diseño de la vista haciendo uso de custom views como en el resto capas haciendo uso de sealed class y abstracción. De esta forma se cumple con unos de los principios SOLID open/close
(toda clase debe estar cerrada para modificar y abierta para extender)

A continuación mostraré algunos diagramas de clases que describe las capas donde se han aplicado este tipo de clases.

 * UseCase (abstract class): 
 ![Abstract Class Diagram](https://github.com/vespasoft/video_recorder/blob/master/domain_diagram_class.png?raw=true "Abstract Class Use Case")

 * VMLibraryAdapter (sealed class)
 * BaseFragment (Fragment)
 * CameraFragment (BaseFragment)
 * AutoFitTextureView
 * VoiceModVideoView
 * VoiceModVideoThumbnailView
 
 ### Escalabilidad de la Aplicación ###
  * Gracias a la arquitectura utilizada cada una de las capas de la aplicación estan totalmente desacopladas y pueden ser refactorizadas facilmente sin afectar al resto. 
  * El Recycler View que muestra la grilla de videos queda abierto a extender, así que para añadir nuevos tipos de celdas a la grilla no hará falta modificar el Adapter ya que como la clase de tipos LibraryItem es una sealed class, bastará con añadir un nuevo tipo como por ejemplo: Photo, Effect, etc..
  * Cada lógica de negocio queda centralizada en un UseCase, por lo que es mas facil de mantener y de anñadir nuevas funcionalidades sin necesidad de modificar las existentes, por ejemplo: Para añadir una nueva funcionalidad llamada *Eliminar videos en cascada* no será necesario modificar el DeleteVideoUseCase, sino que se crea un nuevo UseCase y de esta forma se evita modificar las funcionalidades existentes.
  * Los Custom Views son una buena forma de reutilizar código en la capa de la vista, en este caso en el custom view VoiceModVideoView se encapsula la lógica del reproductor y se reutiliza tanto en la clase VideoRecorderFragment como en la clase CameraActivity.
 
 ### Posibles Mejoras ###
 * Es muy problable que a nivel de diseño y de UX la aplicacion sea bastante mejorable, ya que los esfueros se centraron en la calidad del codigo y no en la experiencia visual del usuario, por lo que se puede mejorar con animaciones en las transiciones de objetos y en la navegacion.
 * En la capa de la vista el CameraFragment se puede mejorar haciendolo mas comun para que se pueda reutilizar en otras funcionalidades como tomar foto u otra.
