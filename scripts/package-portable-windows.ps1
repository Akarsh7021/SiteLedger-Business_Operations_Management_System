$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$distRoot = Join-Path $root "dist"
$appDir = Join-Path $distRoot "SiteLedger"
$jarName = "SiteLedger.jar"

Set-Location $root

if (!(Test-Path ".\mvnw.cmd")) {
    throw "Maven wrapper not found. Expected .\mvnw.cmd in $root"
}

if ([string]::IsNullOrWhiteSpace($env:JAVA_HOME)) {
    throw "JAVA_HOME is not set. Install JDK 17 or newer, then set JAVA_HOME to the JDK folder."
}

if (!(Test-Path (Join-Path $env:JAVA_HOME "bin\java.exe"))) {
    throw "JAVA_HOME does not point to a valid JDK/JRE folder: $env:JAVA_HOME"
}

Remove-Item -Recurse -Force -ErrorAction SilentlyContinue (Join-Path $root "target")

.\mvnw.cmd clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    throw "Maven build failed. Fix the build error above before packaging."
}

$jar = Get-ChildItem -Path (Join-Path $root "target") -Filter "*.jar" |
    Where-Object { $_.Name -notlike "*.original" } |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

if ($null -eq $jar) {
    throw "No runnable jar found in target."
}

New-Item -ItemType Directory -Force -Path $appDir | Out-Null
Copy-Item -Force -LiteralPath $jar.FullName -Destination (Join-Path $appDir $jarName)

if (Test-Path (Join-Path $root "employee-payroll.db")) {
    Copy-Item -Force -LiteralPath (Join-Path $root "employee-payroll.db") -Destination (Join-Path $appDir "employee-payroll.db")
}

@'
@echo off
cd /d "%~dp0"

where java >nul 2>nul
if errorlevel 1 (
  echo Java was not found.
  echo Install Java 17 or newer, then run this file again.
  echo Download: https://adoptium.net/temurin/releases/?version=17
  pause
  exit /b 1
)

java -version 2>&1 | findstr /r /c:"version \"1\\." >nul
if not errorlevel 1 (
  echo Java 17 or newer is required. This laptop appears to have old Java.
  echo Download: https://adoptium.net/temurin/releases/?version=17
  pause
  exit /b 1
)

start "" "http://127.0.0.1:8081"
java -jar SiteLedger.jar
pause
'@ | Set-Content -Encoding ASCII -Path (Join-Path $appDir "Start SiteLedger.bat")

@'
SiteLedger - Local App Package

How to run:
1. Double-click "Start SiteLedger.bat".
2. The app opens at http://127.0.0.1:8081

Requirements:
- Java 17 or newer must be installed.
- Data is stored locally in employee-payroll.db in this same folder.

First launch:
- Create the username and password on the setup page.
- After setup, sign in with that account.

Important:
- Keep this whole folder together. Do not delete employee-payroll.db unless you want to remove the local data.
'@ | Set-Content -Encoding ASCII -Path (Join-Path $appDir "README-FIRST.txt")

Write-Host ""
Write-Host "Portable app package created:"
Write-Host $appDir
Write-Host ""
Write-Host "Give your dad the whole SiteLedger folder."
