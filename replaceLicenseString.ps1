$confirmation = Read-Host "Make sure you are running PS 7.0+ since 5.x will fuck up the encoding to UTF8withBOM. Proceed? (y/n)"

if ($confirmation -ne 'y') {
    Write-Host "Operation cancelled by user." -ForegroundColor Yellow
    exit
}

$licenseRaw = Get-Content -Raw -Path ".\javaLicenseText" -Encoding utf8
$licenseLines = $licenseRaw -split "`r?`n" | ForEach-Object { " * $_" }
$licenseBlock = @("/**") + $licenseLines + " */"
$license = $licenseBlock -join "`r`n"

Get-ChildItem -Path ".\src" -Recurse -Filter "*.java" | ForEach-Object {
    $file = $_.FullName
    Write-Host "`nProcessing: $file" -ForegroundColor Magenta

    $content = Get-Content -Raw -Path $file -Encoding utf8
    $packageIndex = $content.IndexOf("package ")

    if ($packageIndex -ge 0) {
        # Match a license block only at the start of file (allowing leading whitespace/newlines)
        # up to before the package line.
        $head = $content.Substring(0, $packageIndex)

        $pattern = '^(?:\s*/\*\*.*?\*/\s*)'  # license block at start with optional surrounding whitespace
        $match = [regex]::Match($head, $pattern, [System.Text.RegularExpressions.RegexOptions]::Singleline)

        if ($match.Success) {
            $newHead = $head.Remove($match.Index, $match.Length)
            $newHead = $newHead.Insert($match.Index, $license + "`r`n")
            $newContent = $newHead + $content.Substring($packageIndex)
            Set-Content -Path $file -Value $newContent.TrimEnd("`r", "`n") -Encoding utf8
            Write-Host "Replaced license block" -ForegroundColor Green
        }
        else {
            # Insert license block if none exists before package line
            $newContent = $license + "`r`n" + $content
            Set-Content -Path $file -Value $newContent.TrimEnd("`r", "`n") -Encoding utf8
            Write-Host "Inserted license block" -ForegroundColor Yellow
        }
    }
    else {
        Write-Host "No package line found; no replacement" -ForegroundColor Red
    }
}
