# 配置 Apache Maven 与 GitHub Packages 协同工作

本文档详细介绍了如何配置您的 Maven 项目以与 GitHub Packages 协同工作，包括发布包到 GitHub Packages 和从 GitHub Packages
使用依赖项。

## 1. 配置 Maven Settings 文件

首先，在您的用户主目录下创建或编辑 `~/.m2/settings.xml` 文件（在 Windows 上通常是
`C:\Users\YourUsername\.m2\settings.xml`）：

```xml

<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

    <activeProfiles>
        <activeProfile>github</activeProfile>
    </activeProfiles>

    <profiles>
        <profile>
            <id>github</id>
            <repositories>
                <repository>
                    <id>central</id>
                    <url>https://repo.maven.apache.org/maven2</url>
                </repository>
                <repository>
                    <id>github</id>
                    <url>https://maven.pkg.github.com/VoyagerStar3897081534/OnTimeCommand</url>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
            </repositories>
        </profile>
    </profiles>

    <servers>
        <server>
            <id>github</id>
            <username>VoyagerStar3897081534</username>
            <password>TOKEN</password>
        </server>
    </servers>
</settings>
```

### 注意事项：

- 将 `TOKEN` 替换为您的 GitHub 个人访问令牌
- 将 `VoyagerStar3897081534` 替换为您的 GitHub 用户名
- 将 `OnTimeCommand` 替换为您的仓库名

## 2. 个人访问令牌创建

要创建用于 GitHub Packages 的个人访问令牌：

1. 访问 GitHub 设置页面（Settings）
2. 点击 "Developer settings"
3. 选择 "Personal access tokens" > "Tokens (classic)"
4. 点击 "Generate new token"
5. 选择适当的权限范围，对于 GitHub Packages 至少需要 `write:packages`、`read:packages` 和 `delete:packages` 权限
6. 生成并安全保存令牌

## 3. 项目配置

您的项目 [pom.xml](file:///D:/OnTimeCommand/pom.xml)
文件已经配置了 [distributionManagement](file:///D:/OnTimeCommand/pom.xml#L177-L181) 部分，如下所示：

```xml

<distributionManagement>
    <repository>
        <id>github</id>
        <name>GitHub VoyagerStar3897081534 Apache Maven Packages</name>
        <url>https://maven.pkg.github.com/VoyagerStar3897081534/OnTimeCommand</url>
    </repository>
</distributionManagement>
```

## 4. 发布包到 GitHub Packages

要将项目发布到 GitHub Packages，请执行以下命令：

```bash
mvn clean deploy
```

这将构建您的项目并将生成的包部署到 GitHub Packages。

## 5. 从 GitHub Packages 使用依赖项

如果您的其他项目需要使用发布到 GitHub Packages 的包，可以在那些项目的 [pom.xml](file:///D:/OnTimeCommand/pom.xml)
文件中添加依赖项：

```xml

<dependencies>
    <dependency>
        <groupId>org.VoyagerStar</groupId>
        <artifactId>ontimecommand</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

并确保在那些项目的 `~/.m2/settings.xml` 文件中包含了 GitHub Packages 仓库配置。

## 6. 版本管理

该项目使用动态版本管理，版本号由 `maven.properties` 文件中的 `versionNumber` 属性和时间戳组成。确保在发布前更新版本号。

## 7. 注意事项

1. **命名规范**：Maven 包的 artifactId 只能包含小写字母、数字或连字符
2. **安全性**：切勿将个人访问令牌提交到代码仓库
3. **权限**：确保您的令牌具有适当的权限来读取、写入和删除包
4. **网络**：确保可以访问 `https://maven.pkg.github.com`

## 8. 故障排除

- 如果遇到认证错误，请检查 `~/.m2/settings.xml` 文件中的凭证
- 如果无法找到包，请确保仓库 URL 正确无误
- 检查您的 GitHub 账户是否有足够的权限访问 GitHub Packages