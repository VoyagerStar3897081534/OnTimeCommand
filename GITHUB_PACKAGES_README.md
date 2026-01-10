# GitHub Packages 集成指南

本项目已配置为支持与 GitHub Packages 协同工作。

## 快速开始

### 1. 配置 Maven Settings

1. 将 [settings-template.xml](file:///D:/OnTimeCommand/settings-template.xml) 复制到 `~/.m2/settings.xml`
2. 将 `YOUR_PERSONAL_ACCESS_TOKEN_HERE` 替换为您的 GitHub 个人访问令牌
3. 将 `YourGitHubUsername` 替换为您的 GitHub 用户名

### 2. 发布到 GitHub Packages

```bash
mvn clean deploy
```

### 3. 使用此包作为依赖项

在其他项目的 [pom.xml](file:///D:/OnTimeCommand/pom.xml) 中添加：

```xml

<dependency>
    <groupId>org.VoyagerStar</groupId>
    <artifactId>ontimecommand</artifactId>
    <version>1.0.0</version>
</dependency>
```

确保其他项目也有正确的仓库配置和认证设置。

## 重要提示

- 确保您的个人访问令牌具有适当的权限
- artifactId 必须遵循 Maven 命名规范（只包含小写字母、数字或连字符）
- 不要将个人访问令牌提交到版本控制系统