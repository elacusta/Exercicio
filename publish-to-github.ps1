param(
    [Parameter(Mandatory=$true)]
    [string]$RemoteUrl
)

if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    Write-Error "Git não está instalado ou não está no PATH."
    exit 1
}

Set-Location $PSScriptRoot

if (-not (Test-Path .git)) {
    Write-Host "Inicializando repositório Git..."
    git init
} else {
    Write-Host "Repositório Git já inicializado."
}

git branch -M main 2>$null | Out-Null

git add .

git commit -m "Initial commit: Android Kegel app" 2>$null | Out-Null

git remote remove origin 2>$null | Out-Null

git remote add origin $RemoteUrl

git push -u origin main
