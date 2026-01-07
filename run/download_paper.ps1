# 下载最新版Paper 1.21 服务器核心
$version = "1.21.4"
$build = "644"
$paper_url = "https://api.papermc.io/v2/projects/paper/versions/$version/builds/$build/downloads/paper-$version-$build.jar"
$jar_name = "paper-1.21.4.jar"

Write-Host "正在下载Paper $version 服务器核心..."
Write-Host "URL: $paper_url"

try {
    Invoke-WebRequest -Uri $paper_url -OutFile $jar_name -Headers @{"User-Agent"="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"}
    Write-Host "下载完成: $jar_name"
} catch {
    Write-Host "下载失败: $($_.Exception.Message)"
    Write-Host "请手动下载 Paper $version 服务器核心并重命名为 $jar_name"
    Write-Host "下载地址: $paper_url"
}