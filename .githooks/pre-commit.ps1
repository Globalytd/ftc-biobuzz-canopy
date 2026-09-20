$ErrorActionPreference = "Stop"

$repoRoot = git rev-parse --show-toplevel 2>$null
if (-not $repoRoot) {
    Write-Error "Unable to locate repository root."
}

Set-Location $repoRoot

$authorDaniel = "Daniel Musigire"
$authorKatriel = "Katriel Nakiberu"

$stagedFiles = git diff --cached --name-only --diff-filter=ACM |
    Where-Object { $_ -match '^TeamCode/src/main/java/org/firstinspires/ftc/teamcode/.+\.java$' }

$stagedTestFiles = $stagedFiles |
    Where-Object { $_ -match '^TeamCode/src/main/java/org/firstinspires/ftc/teamcode/gytd/test/.+\.java$' }

if (-not $stagedFiles) {
    exit 0
}

$updatedFiles = New-Object System.Collections.Generic.List[string]

$classRegex = New-Object System.Text.RegularExpressions.Regex('^\s*(public\s+)?(final\s+|abstract\s+)?class\s+\w+')

function Find-ClassIndex {
    param([string[]]$Lines)

    for ($i = 0; $i -lt $Lines.Length; $i++) {
        if ($classRegex.IsMatch($Lines[$i])) {
            return $i
        }
    }
    return -1
}

function Find-JavadocStart {
    param(
        [string[]]$Lines,
        [int]$JavadocEnd
    )

    for ($i = $JavadocEnd; $i -ge 0; $i--) {
        if ($Lines[$i] -match '/\*\*') {
            return $i
        }
        if ($Lines[$i] -match '/\*' -and $Lines[$i] -notmatch '/\*\*') {
            return -1
        }
    }
    return -1
}

function Get-StagedDiffLines {
    param([string]$RelativePath)

    $diffText = git diff --cached --unified=0 -- "$RelativePath" | Out-String
    return [System.Text.RegularExpressions.Regex]::Split($diffText, "\r?\n")
}

function Test-CodeChangedWithoutJavadocEdit {
    param([string]$RelativePath)

    $diffLines = Get-StagedDiffLines -RelativePath $RelativePath
    $codeChanged = $false
    $javadocChanged = $false

    foreach ($line in $diffLines) {
        if ($line -notmatch '^[+-]') {
            continue
        }
        if ($line -match '^\+\+\+' -or $line -match '^---') {
            continue
        }

        if ($line -match '^[+-]\s*(/\*\*|\*/|\*)') {
            $javadocChanged = $true
        }

        if ($line -match '^[+-]\s*$') {
            continue
        }
        if ($line -match '^[+-]\s*(/\*\*|\*/|\*|//)') {
            continue
        }

        $codeChanged = $true
    }

    return ($codeChanged -and -not $javadocChanged)
}

function Get-MissingTestJavadocSections {
    param([string]$AbsolutePath)

    if (-not (Test-Path -LiteralPath $AbsolutePath)) {
        return @()
    }

    $text = [System.IO.File]::ReadAllText($AbsolutePath)
    $requiredPhrases = @(
        "Configuration required",
        "Controls:",
        "Safety notes:"
    )

    $missing = New-Object System.Collections.Generic.List[string]
    foreach ($phrase in $requiredPhrases) {
        if ($text -notmatch [regex]::Escape($phrase)) {
            $missing.Add($phrase)
        }
    }
    return $missing
}

foreach ($relativePath in $stagedFiles) {
    $absolutePath = Join-Path $repoRoot $relativePath
    if (-not (Test-Path -LiteralPath $absolutePath)) {
        continue
    }

    $content = [System.IO.File]::ReadAllText($absolutePath)
    $newline = if ($content.Contains("`r`n")) { "`r`n" } else { "`n" }
    $lines = [System.Collections.Generic.List[string]]::new()
    $splitLines = [System.Text.RegularExpressions.Regex]::Split($content, "\r?\n")
    $lines.AddRange([string[]]$splitLines)

    $classIndex = Find-ClassIndex -Lines $lines
    if ($classIndex -lt 0) {
        continue
    }

    $scan = $classIndex - 1
    while ($scan -ge 0 -and [string]::IsNullOrWhiteSpace($lines[$scan])) {
        $scan--
    }
    while ($scan -ge 0 -and $lines[$scan].TrimStart().StartsWith("@")) {
        $scan--
    }
    $insertAnchor = $scan + 1

    $javadocEnd = $insertAnchor - 1
    while ($javadocEnd -ge 0 -and [string]::IsNullOrWhiteSpace($lines[$javadocEnd])) {
        $javadocEnd--
    }

    $changed = $false
    if ($javadocEnd -ge 0 -and $lines[$javadocEnd].Trim().EndsWith("*/")) {
        $javadocStart = Find-JavadocStart -Lines $lines -JavadocEnd $javadocEnd
        if ($javadocStart -ge 0) {
            $blockText = [string]::Join($newline, $lines[$javadocStart..$javadocEnd])
            $missing = New-Object System.Collections.Generic.List[string]
            if ($blockText -notmatch [regex]::Escape($authorDaniel)) {
                $missing.Add(" * @author $authorDaniel")
            }
            if ($blockText -notmatch [regex]::Escape($authorKatriel)) {
                $missing.Add(" * @author $authorKatriel")
            }
            if ($missing.Count -gt 0) {
                $lines.InsertRange($javadocEnd, $missing)
                $changed = $true
            }
        }
    }

    if (-not $changed) {
        $classBlockStart = [Math]::Max(0, $insertAnchor)
        $shouldInsertNew = $true
        if ($javadocEnd -ge 0 -and $lines[$javadocEnd].Trim().EndsWith("*/")) {
            $shouldInsertNew = $false
        }

        if ($shouldInsertNew) {
            $newBlock = New-Object System.Collections.Generic.List[string]
            $newBlock.Add("/**")
            $newBlock.Add(" * @author $authorDaniel")
            $newBlock.Add(" * @author $authorKatriel")
            $newBlock.Add(" */")
            if ($insertAnchor -lt $lines.Count -and -not [string]::IsNullOrWhiteSpace($lines[$insertAnchor])) {
                $newBlock.Add("")
            }
            $lines.InsertRange($classBlockStart, $newBlock)
            $changed = $true
        }
    }

    if ($changed) {
        $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
        [System.IO.File]::WriteAllText($absolutePath, ([string]::Join($newline, $lines)), $utf8NoBom)
        git add -- "$relativePath" | Out-Null
        $updatedFiles.Add($relativePath)
    }
}

if ($updatedFiles.Count -gt 0) {
    Write-Host "[pre-commit] Added/updated class author tags in:"
    foreach ($path in $updatedFiles) {
        Write-Host "  - $path"
    }
}

$testDocViolations = New-Object System.Collections.Generic.List[string]
foreach ($testPath in $stagedTestFiles) {
    $absoluteTestPath = Join-Path $repoRoot $testPath

    $missingSections = Get-MissingTestJavadocSections -AbsolutePath $absoluteTestPath
    if ($missingSections.Count -gt 0) {
        $testDocViolations.Add("$testPath is missing required Javadoc sections: $([string]::Join(', ', $missingSections))")
    }

    if (Test-CodeChangedWithoutJavadocEdit -RelativePath $testPath) {
        $testDocViolations.Add("$testPath has code changes but no Javadoc changes. Update the class Javadoc to match behavior/config updates.")
    }
}

if ($testDocViolations.Count -gt 0) {
    Write-Host "[pre-commit] Test Javadoc check failed:" -ForegroundColor Red
    foreach ($violation in $testDocViolations) {
        Write-Host "  - $violation" -ForegroundColor Red
    }
    Write-Host "Fix the Javadocs in modified test files, then commit again." -ForegroundColor Yellow
    exit 1
}

exit 0



