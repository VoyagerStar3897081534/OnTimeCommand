# OnTimeCommand

[![License](https://img.shields.io/github/license/VoyagerStar3897081534/OnTimeCommand)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VoyagerStar3897081534/OnTimeCommand)](https://github.com/VoyagerStar3897081534/OnTimeCommand/releases)
[![Issues](https://img.shields.io/github/issues/VoyagerStar3897081534/OnTimeCommand)](https://github.com/VoyagerStar3897081534/OnTimeCommand/issues)

A powerful Minecraft Paper server plugin that allows you to automatically execute commands at set time intervals.

[:cn: 中文](README.md) | [:fr: Français](README-fr.md) | [:es: Español](README-es.md) | [:ru: Русский](README-ru.md) | [:sa: العربية](README-ar.md)

## 🌟 Features

- ⏰ **Scheduled Command Execution** - Automatically execute Minecraft commands at specified time intervals
- 🔧 **Flexible Command Management** - Support adding, deleting, enabling, and disabling scheduled tasks
- 🎣 **Orbital TNT Feature** - Special fishing rod function that generates TNT explosion effects
- 🔍 **Interactive Interface** - Clickable task list viewing and management
- 🛡️ **Permission System** - Fine-grained permission control
- 🔄 **Hot Reload** - Support configuration file hot reload without server restart
- 📊 **Real-time Monitoring** - View status and details of all scheduled tasks

## 📋 System Requirements

- **Minecraft Version**: 1.21+
- **Server**: Paper 1.21+
- **Java Version**: Java 21+

## 🚀 Installation

1. Download the latest `.jar` file
2. Place the plugin file in the server's `plugins` folder
3. Restart the server
4. The plugin will automatically generate configuration files

## 📖 Usage Guide

### Basic Commands

#### Main Management Commands

```bash
/ontimecommand <subcommand> [parameters...] - Manage scheduled commands (alias: /otc)
/seecommand - View all scheduled command lists
/reloadotc - Reload all configuration files
```

#### Subcommand Details

**Add New Task**

```bash
/ontimecommand add <task-name> <interval-seconds>
# Example: Add a task that executes every 60 seconds
/ontimecommand add welcome-message 60
```

**Add Commands to Task**

```bash
/ontimecommand addcommand <task-name> <command1> [command2] [command3]...
# Example: Add multiple commands to a task
/ontimecommand addcommand welcome-message "say Welcome!" "title @a title Welcome"
```

**Enable/Disable Tasks**

```bash
/ontimecommand enable <task-name>
/ontimecommand disable <task-name>
```

**Delete Commands or Tasks**

```bash
/ontimecommand deletecommand <task-name> <command-number>
/ontimecommand delete <task-name>
```

**View Task Details**

```bash
/ontimecommand seeinfo <task-name>
```

### Permission Nodes

| Permission Node        | Description                    | Default     |
|------------------------|--------------------------------|-------------|
| `ontimecommand.admin`  | Use all OnTimeCommand features | OP          |
| `ontimecommand.player` | View command list only         | All Players |

### Configuration Files

#### Scheduled Commands Configuration (`on-time-command-list.yml`)

```yaml
commands:
  welcome-message:
    interval: 30
    commands:
      - "say Welcome to the server!"
      - "title @a title Welcome!"
    disabled: false

  clean-drops:
    interval: 300
    commands:
      - "kill @e[type=item]"
      - "say Items cleaned up"
    disabled: true
```

#### Orbital TNT Configuration (`orbital-tnt-config.yml`)

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

## 🎣 Orbital TNT Feature

When players use a special fishing rod named "Orbital TNT" to cast, TNT explosion effects will be generated at the
casting position:

1. Generate central TNT at the casting position
2. Generate multiple TNT rings around the center point
3. Each ring is spaced at certain distances and times
4. Create spectacular chain explosion effects

## 🔧 Developer Information

### Building the Project

```bash
# Clone repository
git clone https://github.com/VoyagerStar3897081534/OnTimeCommand.git
cd OnTimeCommand

# Build project
mvn clean package
```

### Maven Configuration

The project uses Maven for build management, supporting multiple build configurations:

```bash
# Publish stable version
mvn clean package -DversionPackageType=release

# Publish beta version
mvn clean package -DversionPackageType=beta

# Development version (default)
mvn clean package
```

### Project Structure

```
src/
├── main/
│   ├── java/org/VoyagerStar/onTimeCommand/
│   │   ├── command/
│   │   │   ├── executor/          # Command executors
│   │   │   └── tabCompleter/      # Command completion
│   │   ├── init/                  # Initialization module
│   │   ├── listener/              # Event listeners
│   │   ├── OnTimeCommand.java     # Main class
│   │   └── RunCommandOnTime.java  # Scheduled task management
│   └── resources/
│       ├── on-time-command-list.yml
│       ├── orbital-tnt-config.yml
│       └── paper-plugin.yml
└── test/
    └── java/                      # Unit tests
```

## 🤝 Contribution Guidelines

Welcome to submit Issues and Pull Requests!

1. Fork this repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

## 📝 License

This project is licensed under the MIT License - see [LICENSE](LICENSE) file for details

## 🆘 Support and Help

- 💬 **Issue Feedback**: [GitHub Issues](https://github.com/VoyagerStar3897081534/OnTimeCommand/issues)
- 📚 **Documentation**: [Wiki](https://github.com/VoyagerStar3897081534/OnTimeCommand/wiki)
- 📧 **Contact Author**: VoyagerStar

## 🙏 Acknowledgements

Thank you to all developers and users who have contributed to this project!

---

<p align="center">
  Made with by VoyagerStar
</p>