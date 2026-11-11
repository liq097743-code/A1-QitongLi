param([switch]$Test)
$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
$compiler = Get-Command javac -ErrorAction SilentlyContinue
if ($compiler) {
    $javaBin = Split-Path $compiler.Source
} else {
    $portableRoot = Join-Path $env:LOCALAPPDATA 'CodexJava'
    $portable = Get-ChildItem $portableRoot -Filter javac.exe -Recurse -ErrorAction SilentlyContinue |
        Select-Object -First 1
    if (!$portable) { throw 'Install JDK 17 and add its bin directory to PATH.' }
    $javaBin = Split-Path $portable.FullName
}
New-Item -ItemType Directory -Force out | Out-Null
$sources = @(Get-ChildItem src/*.java, tests/*.java | ForEach-Object { $_.FullName })
& (Join-Path $javaBin 'javac.exe') -encoding UTF-8 -Xlint:all -d out @sources
if ($LASTEXITCODE -ne 0) { throw 'Compilation failed.' }
$entryPoint = if ($Test) { 'AppointmentSystemTest' } else { 'AssignmentOne' }
& (Join-Path $javaBin 'java.exe') -cp out $entryPoint
if ($LASTEXITCODE -ne 0) { throw 'Execution failed.' }
