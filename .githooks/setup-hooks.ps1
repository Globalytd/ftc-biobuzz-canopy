param(
    [string]$GitUserName = "Daniel Musigire",
    [string]$GitUserEmail = "",
    [switch]$SetCoAuthorTemplate
)

$ErrorActionPreference = "Stop"

$repoRoot = git rev-parse --show-toplevel 2>$null
if (-not $repoRoot) {
    Write-Error "Run this from inside the git repository."
}

Set-Location $repoRoot

# Use tracked hooks so team members share the same automation.
git config core.hooksPath .githooks

if ($GitUserName) {
    git config user.name "$GitUserName"
}
if ($GitUserEmail) {
    git config user.email "$GitUserEmail"
}

if ($SetCoAuthorTemplate) {
    $templatePath = Join-Path $repoRoot ".githooks/commit-template.txt"
    if (-not (Test-Path $templatePath)) {
        @"

Co-authored-by: Katriel Nakiberu <katriel@example.com>
"@ | Set-Content -Path $templatePath -Encoding UTF8
    }
    git config commit.template .githooks/commit-template.txt
}

Write-Host "Configured core.hooksPath=.githooks"
Write-Host ("Current git user.name: " + (git config --get user.name))
$currentEmail = git config --get user.email
if ($currentEmail) {
    Write-Host ("Current git user.email: " + $currentEmail)
} else {
    Write-Host "Current git user.email is not set."
}
if ($SetCoAuthorTemplate) {
    Write-Host "Configured commit.template=.githooks/commit-template.txt"
}

