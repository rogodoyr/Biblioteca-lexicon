param(
    [string]$InputFile = "Informe_Tecnico_Lexicon.md",
    [string]$OutputFile = "Informe_Tecnico_Lexicon.docx"
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression.FileSystem

function Escape-Xml([string]$Text) {
    [System.Security.SecurityElement]::Escape($Text)
}

function New-Paragraph([string]$Text, [string]$Style = 'Normal') {
    $escapedText = Escape-Xml $Text
    '<w:p><w:pPr><w:pStyle w:val="' + $Style + '"/></w:pPr><w:r><w:t xml:space="preserve">' + $escapedText + '</w:t></w:r></w:p>'
}

if (-not (Test-Path -LiteralPath $InputFile)) {
    throw "No se encontro el informe de entrada: $InputFile"
}

$paragraphs = [System.Collections.Generic.List[string]]::new()
$inCodeBlock = $false

foreach ($line in Get-Content -LiteralPath $InputFile) {
    if ($line -match '^```') {
        $inCodeBlock = -not $inCodeBlock
        continue
    }

    if ($line -match '^---\s*$') {
        continue
    }

    $text = $line -replace '\*\*', '' -replace '`', ''

    if ($text -match '^# (.+)$') {
        $paragraphs.Add((New-Paragraph $Matches[1] 'Title'))
    } elseif ($text -match '^## (.+)$') {
        $paragraphs.Add((New-Paragraph $Matches[1] 'Heading1'))
    } elseif ($text -match '^### (.+)$') {
        $paragraphs.Add((New-Paragraph $Matches[1] 'Heading2'))
    } elseif ($text -match '^\|[-:| ]+\|\s*$') {
        continue
    } elseif ($text -match '^\|(.+)\|\s*$') {
        $cells = $Matches[1].Split('|') | ForEach-Object { $_.Trim() }
        $paragraphs.Add((New-Paragraph ($cells -join '    ') 'TableText'))
    } elseif ($text -match '^\- (.+)$') {
        $paragraphs.Add((New-Paragraph ("• " + $Matches[1]) 'Normal'))
    } elseif ($inCodeBlock) {
        $paragraphs.Add((New-Paragraph $text 'Code'))
    } elseif ([string]::IsNullOrWhiteSpace($text)) {
        $paragraphs.Add('<w:p/>')
    } else {
        $paragraphs.Add((New-Paragraph $text 'Normal'))
    }
}

$documentXml = @"
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:body>
    $($paragraphs -join "`n    ")
    <w:sectPr>
      <w:pgSz w:w="12240" w:h="15840"/>
      <w:pgMar w:top="1440" w:right="1440" w:bottom="1440" w:left="1440"/>
    </w:sectPr>
  </w:body>
</w:document>
"@

$contentTypes = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
  <Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/>
  <Override PartName="/docProps/core.xml" ContentType="application/vnd.openxmlformats-package.core-properties+xml"/>
  <Override PartName="/docProps/app.xml" ContentType="application/vnd.openxmlformats-officedocument.extended-properties+xml"/>
</Types>
'@

$rootRelationships = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties" Target="docProps/core.xml"/>
  <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties" Target="docProps/app.xml"/>
</Relationships>
'@

$documentRelationships = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>
'@

$styles = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:docDefaults><w:rPrDefault><w:rPr><w:rFonts w:ascii="Arial" w:hAnsi="Arial"/><w:sz w:val="22"/></w:rPr></w:rPrDefault></w:docDefaults>
  <w:style w:type="paragraph" w:default="1" w:styleId="Normal"><w:name w:val="Normal"/><w:qFormat/></w:style>
  <w:style w:type="paragraph" w:styleId="Title"><w:name w:val="Title"/><w:basedOn w:val="Normal"/><w:qFormat/><w:rPr><w:b/><w:sz w:val="36"/></w:rPr></w:style>
  <w:style w:type="paragraph" w:styleId="Heading1"><w:name w:val="Heading 1"/><w:basedOn w:val="Normal"/><w:qFormat/><w:rPr><w:b/><w:sz w:val="28"/></w:rPr></w:style>
  <w:style w:type="paragraph" w:styleId="Heading2"><w:name w:val="Heading 2"/><w:basedOn w:val="Normal"/><w:qFormat/><w:rPr><w:b/><w:sz w:val="24"/></w:rPr></w:style>
  <w:style w:type="paragraph" w:styleId="TableText"><w:name w:val="Table Text"/><w:basedOn w:val="Normal"/><w:rPr><w:sz w:val="20"/></w:rPr></w:style>
  <w:style w:type="paragraph" w:styleId="Code"><w:name w:val="Code"/><w:basedOn w:val="Normal"/><w:rPr><w:rFonts w:ascii="Consolas" w:hAnsi="Consolas"/><w:sz w:val="18"/></w:rPr></w:style>
</w:styles>
'@

$coreProperties = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<cp:coreProperties xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties" xmlns:dc="http://purl.org/dc/elements/1.1/">
  <dc:title>Informe Técnico: Sistema de Biblioteca Distribuida Lexicon</dc:title>
  <dc:creator>Lexicon</dc:creator>
</cp:coreProperties>
'@

$appProperties = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Properties xmlns="http://schemas.openxmlformats.org/officeDocument/2006/extended-properties"><Application>Microsoft Office Word</Application></Properties>
'@

$tempDirectory = Join-Path $env:TEMP ("lexicon-docx-" + [guid]::NewGuid())
New-Item -ItemType Directory -Path $tempDirectory | Out-Null
New-Item -ItemType Directory -Path (Join-Path $tempDirectory '_rels') | Out-Null
New-Item -ItemType Directory -Path (Join-Path $tempDirectory 'word/_rels') -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $tempDirectory 'docProps') | Out-Null

[System.IO.File]::WriteAllText((Join-Path $tempDirectory '[Content_Types].xml'), $contentTypes, [System.Text.UTF8Encoding]::new($false))
[System.IO.File]::WriteAllText((Join-Path $tempDirectory '_rels/.rels'), $rootRelationships, [System.Text.UTF8Encoding]::new($false))
[System.IO.File]::WriteAllText((Join-Path $tempDirectory 'word/document.xml'), $documentXml, [System.Text.UTF8Encoding]::new($false))
[System.IO.File]::WriteAllText((Join-Path $tempDirectory 'word/styles.xml'), $styles, [System.Text.UTF8Encoding]::new($false))
[System.IO.File]::WriteAllText((Join-Path $tempDirectory 'word/_rels/document.xml.rels'), $documentRelationships, [System.Text.UTF8Encoding]::new($false))
[System.IO.File]::WriteAllText((Join-Path $tempDirectory 'docProps/core.xml'), $coreProperties, [System.Text.UTF8Encoding]::new($false))
[System.IO.File]::WriteAllText((Join-Path $tempDirectory 'docProps/app.xml'), $appProperties, [System.Text.UTF8Encoding]::new($false))

if (Test-Path -LiteralPath $OutputFile) {
    Remove-Item -LiteralPath $OutputFile -Force
}
[System.IO.Compression.ZipFile]::CreateFromDirectory($tempDirectory, (Resolve-Path -LiteralPath '.').Path + '\' + $OutputFile)
Remove-Item -LiteralPath $tempDirectory -Recurse -Force

Write-Output "Documento Word creado: $OutputFile"
