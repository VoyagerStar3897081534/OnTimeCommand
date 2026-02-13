# OnTimeCommand

[![License](https://img.shields.io/github/license/VoyagerStar3897081534/OnTimeCommand)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VoyagerStar3897081534/OnTimeCommand)](https://github.com/VoyagerStar3897081534/OnTimeCommand/releases)
[![Issues](https://img.shields.io/github/issues/VoyagerStar3897081534/OnTimeCommand)](https://github.com/VoyagerStar3897081534/OnTimeCommand/issues)

Un potente complemento para servidores Minecraft Paper que te permite ejecutar automáticamente comandos en intervalos de
tiempo establecidos.

[:cn: 中文](README.md) | [:gb: English](README-en.md) | [:fr: Français](README-fr.md) | [:ru: Русский](README-ru.md) | [:sa: العربية](README-ar.md)

## 🌟 Características

- ⏰ **Ejecución de comandos programados** - Ejecuta automáticamente comandos de Minecraft en intervalos de tiempo
  especificados
- 🔧 **Gestión flexible de comandos** - Soporta agregar, eliminar, habilitar y deshabilitar tareas programadas
- 🎣 **Función Orbital TNT** - Función especial de caña de pescar que genera efectos de explosión TNT
- 🔍 **Interfaz interactiva** - Lista de tareas cliqueable para consulta y gestión
- 🛡️ **Sistema de permisos** - Control de permisos granular
- 🔄 **Recarga en caliente** - Soporta recarga en caliente de archivos de configuración sin reiniciar el servidor
- 📊 **Monitoreo en tiempo real** - Visualización del estado y detalles de todas las tareas programadas

## 📋 Requisitos del sistema

- **Versión de Minecraft**: 1.21+
- **Servidor**: Paper 1.21+
- **Versión de Java**: Java 21+

## 🚀 Instalación

1. Descarga el último archivo `.jar`
2. Coloca el archivo del complemento en la carpeta `plugins` del servidor
3. Reinicia el servidor
4. El complemento generará automáticamente los archivos de configuración

## 📖 Guía de uso

### Comandos básicos

#### Comandos de gestión principales

```bash
/ontimecommand <subcomando> [parámetros...] - Gestionar comandos programados (alias: /otc)
/seecommand - Ver todas las listas de comandos programados
/reloadotc - Recargar todos los archivos de configuración
```

#### Detalles de subcomandos

**Agregar nueva tarea**

```bash
/ontimecommand add <nombre-tarea> <segundos-intervalo>
# Ejemplo: Agregar una tarea que se ejecuta cada 60 segundos
/ontimecommand add welcome-message 60
```

**Agregar comandos a tarea**

```bash
/ontimecommand addcommand <nombre-tarea> <comando1> [comando2] [comando3]...
# Ejemplo: Agregar múltiples comandos a una tarea
/ontimecommand addcommand welcome-message "say ¡Bienvenido!" "title @a title Bienvenido"
```

**Habilitar/Deshabilitar tareas**

```bash
/ontimecommand enable <nombre-tarea>
/ontimecommand disable <nombre-tarea>
```

**Eliminar comandos o tareas**

```bash
/ontimecommand deletecommand <nombre-tarea> <número-comando>
/ontimecommand delete <nombre-tarea>
```

**Ver detalles de tarea**

```bash
/ontimecommand seeinfo <nombre-tarea>
```

### Nodos de permiso

| Nodo de permiso        | Descripción                               | Por defecto         |
|------------------------|-------------------------------------------|---------------------|
| `ontimecommand.admin`  | Usar todas las funciones de OnTimeCommand | OP                  |
| `ontimecommand.player` | Solo ver lista de comandos                | Todos los jugadores |

### Archivos de configuración

#### Configuración de comandos programados (`on-time-command-list.yml`)

```yaml
commands:
  welcome-message:
    interval: 30
    commands:
      - "say ¡Bienvenido al servidor!"
      - "title @a title ¡Bienvenido!"
    disabled: false

  clean-drops:
    interval: 300
    commands:
      - "kill @e[type=item]"
      - "say Objetos limpiados"
    disabled: true
```

#### Configuración Orbital TNT (`orbital-tnt-config.yml`)

```yaml
orbital-tnt:
  enabled: true
  fishing-rod-name: "Orbital TNT"
  wait-time: 5000
  circle-count: 5
  circle-interval: 2
  circle-height: 5
  per-circle-wait-time: 100
```

## 🎣 Función Orbital TNT

Cuando los jugadores usan una caña de pescar especial llamada "Orbital TNT" para lanzar, se generarán efectos de
explosión TNT en la posición de lanzamiento:

1. Generar TNT central en la posición de lanzamiento
2. Generar múltiples anillos TNT alrededor del punto central
3. Cada anillo está espaciado a ciertas distancias y momentos
4. Crear efectos espectaculares de explosiones en cadena

## 🔧 Información del desarrollador

### Construcción del proyecto

```bash
# Clonar repositorio
git clone https://github.com/VoyagerStar3897081534/OnTimeCommand.git
cd OnTimeCommand

# Construir proyecto
mvn clean package
```

### Configuración Maven

El proyecto usa Maven para gestión de construcción, soportando múltiples configuraciones de construcción:

```bash
# Publicar versión estable
mvn clean package -DversionPackageType=release

# Publicar versión beta
mvn clean package -DversionPackageType=beta

# Versión desarrollo (por defecto)
mvn clean package
```

### Estructura del proyecto

```
src/
├── main/
│   ├── java/org/VoyagerStar/onTimeCommand/
│   │   ├── command/
│   │   │   ├── executor/          # Ejecutores de comandos
│   │   │   └── tabCompleter/      # Completado de comandos
│   │   ├── init/                  # Módulo de inicialización
│   │   ├── listener/              # Escuchadores de eventos
│   │   ├── OnTimeCommand.java     # Clase principal
│   │   └── RunCommandOnTime.java  # Gestión de tareas programadas
│   └── resources/
│       ├── on-time-command-list.yml
│       ├── orbital-tnt-config.yml
│       └── paper-plugin.yml
└── test/
    └── java/                      # Pruebas unitarias
```

## 🤝 Directrices de contribución

¡Bienvenido a enviar Issues y Pull Requests!

1. Haz fork de este repositorio
2. Crea rama de funcionalidad (`git checkout -b feature/AmazingFeature`)
3. Confirma cambios (`git commit -m 'Add some AmazingFeature'`)
4. Empuja a la rama (`git push origin feature/AmazingFeature`)
5. Abre Pull Request

## 📝 Licencia

Este proyecto está bajo licencia MIT - ver archivo [LICENSE](LICENSE) para más detalles

## 🆘 Soporte y ayuda

- 💬 **Retroalimentación de problemas**: [GitHub Issues](https://github.com/VoyagerStar3897081534/OnTimeCommand/issues)
- 📚 **Documentación**: [Wiki](https://github.com/VoyagerStar3897081534/OnTimeCommand/wiki)
- 📧 **Contactar autor**: VoyagerStar

## 🙏 Agradecimientos

¡Gracias a todos los desarrolladores y usuarios que han contribuido a este proyecto!

---

<p align="center">
  Hecho con por VoyagerStar
</p>