$license = Get-Content -Raw -Path ".\javaLicenceText" -Encoding UTF8
$licenseLines = $license -split "`r?`n"
$licenseLines = $licenseLines | ForEach-Object { " * $_" }
$replacementBlock = @("/**") + $licenseLines + " */"

Get-ChildItem -Path ".\src" -Recurse -Filter "*.java" | ForEach-Object {
    $file = $_.FullName
    $content = Get-Content -Raw -Path $file -Encoding UTF8

    $pattern = '(?s)/\*\*\r?\n( \*.*\r?\n)+ \*/'
    if ($content -match $pattern) {
        $newContent = [regex]::Replace($content, $pattern, ($replacementBlock -join "`r`n"), 1)
        Set-Content -Path $file -Value $newContent -Encoding UTF8
    }
}
