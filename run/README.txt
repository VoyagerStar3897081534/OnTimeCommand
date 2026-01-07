Minecraft 服务器运行说明
========================

此目录包含运行 Minecraft 服务器所需的所有文件，适用于 OnTimeCommand 插件的测试。

文件说明：
- paper-1.21.4.jar: Paper 服务器核心文件
- server.properties: 服务器基本配置
- bukkit.yml: Bukkit 相关配置
- spigot.yml: Spigot 相关配置
- eula.txt: 最终用户许可协议文件
- start.bat: Windows 启动脚本
- download_paper.ps1: 下载服务器核心的 PowerShell 脚本

使用说明：
1. 确保您的系统已安装 Java 21 或更高版本
2. 运行 start.bat 文件启动服务器
3. 第一次运行时，脚本会自动下载 Paper 1.21.4 服务器核心
4. 服务器启动后，EULA 已预先同意（eula.txt 中设置为 true）
5. 服务器将生成必要的文件和文件夹，包括 plugins 文件夹用于放置插件

插件安装：
- 将编译好的 OnTimeCommand 插件 JAR 文件复制到 plugins 文件夹中
- 重新启动服务器以加载插件

注意事项：
- 请根据您的系统内存调整 start.bat 中的 -Xms 和 -Xmx 参数
- 默认设置为 -Xms2G -Xmx2G (最小和最大内存均为 2GB)
- 服务器首次启动时会生成更多配置文件，请耐心等待