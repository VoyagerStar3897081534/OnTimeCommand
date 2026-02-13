# OnTimeCommand

[![License](https://img.shields.io/github/license/VoyagerStar3897081534/OnTimeCommand)](LICENSE)
[![Release](https://img.shields.io/github/v/release/VoyagerStar3897081534/OnTimeCommand)](https://github.com/VoyagerStar3897081534/OnTimeCommand/releases)
[![Issues](https://img.shields.io/github/issues/VoyagerStar3897081534/OnTimeCommand)](https://github.com/VoyagerStar3897081534/OnTimeCommand/issues)

一个强大的Minecraft Paper服务器插件，允许您按设定的时间间隔自动执行命令。

[:gb: English](README-en.md) | [:fr: Français](README-fr.md) | [:es: Español](README-es.md) | [:ru: Русский](README-ru.md) | [:sa: العربية](README-ar.md)

## 🌟 功能特性

- ⏰ **定时命令执行** - 按指定时间间隔自动执行Minecraft命令
- 🔧 **灵活的命令管理** - 支持添加、删除、启用、禁用定时任务
- 🎣 **Orbital TNT功能** - 特殊的钓鱼竿功能，可生成TNT爆炸效果
- 🔍 **交互式界面** - 可点击的任务列表查看和管理
- 🛡️ **权限系统** - 细粒度的权限控制
- 🔄 **热重载** - 支持配置文件热重载，无需重启服务器
- 📊 **实时监控** - 查看所有定时任务的状态和详情
- 📝 **多语言支持** - 可以随时切换使用语言

## 📋 系统要求

- **Minecraft版本**: 1.21+
- **服务端**: Paper 1.21+
- **Java版本**: Java 21+

## 🚀 安装方法

1. 下载最新版本的 `.jar` 文件
2. 将插件文件放入服务器的 `plugins` 文件夹
3. 重启服务器
4. 插件会自动生成配置文件

## 📖 使用指南

### 基本命令

#### 主要管理命令

```bash
/ontimecommand <子命令> [参数...] - 管理定时命令（别名：/otc）
/seecommand - 查看所有定时命令列表
/reloadotc - 重新加载所有配置文件
/otcsetlang <子命令> -切换服务器该插件使用语言 
```

#### 子命令详解

**添加新任务**

```bash
/ontimecommand add <任务名称> <间隔秒数>
# 示例：添加一个每60秒执行的任务
/ontimecommand add welcome-message 60
```

**添加命令到任务**

```bash
/ontimecommand addcommand <任务名称> <命令1> [命令2] [命令3]...
# 示例：为任务添加多个命令
/ontimecommand addcommand welcome-message "say 欢迎!" "title @a title 欢迎"
```

**启用/禁用任务**

```bash
/ontimecommand enable <任务名称>
/ontimecommand disable <任务名称>
```

**删除命令或任务**

```bash
/ontimecommand deletecommand <任务名称> <命令编号>
/ontimecommand delete <任务名称>
```

**查看任务详情**

```bash
/ontimecommand seeinfo <任务名称>
```

### 权限节点

| 权限节点                   | 描述                  | 默认权限 |
|------------------------|---------------------|------|
| `ontimecommand.admin`  | 使用所有OnTimeCommand功能 | OP   |
| `ontimecommand.player` | 仅查看命令列表             | 所有玩家 |

### 配置文件

#### 定时命令配置 (`on-time-command-list.yml`)

```yaml
commands:
  welcome-message:
    interval: 30
    commands:
      - "say 欢迎来到服务器!"
      - "title @a title 欢迎!"
    disabled: false

  clean-drops:
    interval: 300
    commands:
      - "kill @e[type=item]"
      - "say 掉落物已清理"
    disabled: true
```

#### Orbital TNT配置 (`orbital-tnt-config.yml`)

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

## 🎣 Orbital TNT 功能

当玩家使用名为"Orbital TNT"的特殊钓鱼竿抛竿时，会在抛竿位置生成TNT爆炸效果：

1. 在抛竿位置生成中心TNT
2. 围绕中心点生成多个TNT圆环
3. 每个圆环间隔一定距离和时间
4. 创建壮观的连锁爆炸效果

## 🔧 开发者信息

### 构建项目

```bash
# 克隆仓库
git clone https://github.com/VoyagerStar3897081534/OnTimeCommand.git
cd OnTimeCommand

# 构建项目
mvn clean package
```

### Maven配置

项目使用Maven进行构建管理，支持多种构建配置：

```bash
# 发布稳定版本
mvn clean package -DversionPackageType=release

# 发布测试版本
mvn clean package -DversionPackageType=beta

# 开发版本（默认）
mvn clean package
```

### 项目结构

```
src/
├── main/
│   ├── java/org/VoyagerStar/onTimeCommand/
│   │   ├── command/
│   │   │   ├── executor/          # 命令执行器
│   │   │   └── tabCompleter/      # 命令补全
│   │   ├── init/                  # 初始化模块
│   │   ├── listener/              # 事件监听器
│   │   ├── OnTimeCommand.java     # 主类
│   │   └── RunCommandOnTime.java  # 定时任务管理
│   └── resources/
│       ├── on-time-command-list.yml
│       ├── orbital-tnt-config.yml
│       └── paper-plugin.yml
└── test/
    └── java/                      # 单元测试
```

## 🤝 贡献指南

欢迎提交Issue和Pull Request！

1. Fork 本仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📝 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 🆘 支持与帮助

- 💬 **问题反馈**: [GitHub Issues](https://github.com/VoyagerStar3897081534/OnTimeCommand/issues)
- 📚 **文档**: [Wiki](https://github.com/VoyagerStar3897081534/OnTimeCommand/wiki)
- 📧 **联系作者**: VoyagerStar

## 🙏 致谢

感谢所有为这个项目做出贡献的开发者和用户！

---

<p align="center">
  Made by VoyagerStar
</p>