# G03 - Paso 1.9 - Verificacion reproducible (Windows PowerShell 5.1)
# Ejecutar desde raiz de AgroRetorno; no modifica codigo fuente ni otras ramas.
[CmdletBinding()]
param([switch]$AbrirVistas)
$ErrorActionPreference = 'Stop'
$branchEsperada = 'feature/cielo-g03-analisis-de-sistemas'
$root = (& git rev-parse --show-toplevel 2>$null)
if ($LASTEXITCODE -ne 0 -or [string]::IsNullOrWhiteSpace($root)) { throw 'Ejecuta este archivo dentro del repositorio Git AgroRetorno.' }
Set-Location $root.Trim()
$branch = (& git branch --show-current).Trim()
if ($branch -ne $branchEsperada) { throw "Estas en '$branch', pero debes estar en '$branchEsperada'. No se ha modificado ningun archivo." }
$commit = (& git rev-parse --short HEAD).Trim()
$base = Join-Path $root 'entregables\G03_Analisis_Sistemas'
$uml = Join-Path $base '02_Diagramas_UML'
$rev = Join-Path $base '06_Revision'
$caps = Join-Path $base '05_Capturas'
$doc = Join-Path $base '01_SDD'
$mock = Join-Path $base '04_Mockups'
$reg = New-Object System.Collections.Generic.List[string]
function Log([string]$value) { $script:reg.Add($value); Write-Host $value }
Log 'G03 - AUDITORIA LOCAL AUTOMATICA (no equivale a revision visual)'
Log ('Fecha/hora local: ' + (Get-Date -Format 'yyyy-MM-dd HH:mm:ss'))
Log ('Rama: ' + $branch)
Log ('Commit: ' + $commit)
Log ''
$fallos = 0
$checks = @(
 @{p='G03_02_Casos_de_Uso.puml'; dir=$uml},
 @{p='G03_03_Clases_Completo.puml'; dir=$uml},
 @{p='G03_Arquitectura_Componentes.puml'; dir=$uml},
 @{p='G03_04_Secuencia_01_Registro_Solicitud.puml'; dir=$uml},
 @{p='G03_04_Secuencia_02_Aprobacion.puml'; dir=$uml},
 @{p='G03_04_Secuencia_03_Entrega.puml'; dir=$uml},
 @{p='G03_04_Secuencia_Flujo_Integral.puml'; dir=$uml},
 @{p='G03_06_SDD_IEEE1016_AgroRetorno_Cielo.docx'; dir=$doc},
 @{p='G03_06_SDD_IEEE1016_AgroRetorno_Cielo.pdf'; dir=$doc},
 @{p='G03_05_M01_Solicitud.html'; dir=$mock},
 @{p='G03_05_M02_Revision.html'; dir=$mock},
 @{p='G03_05_M03_Entrega.html'; dir=$mock},
 @{p='G03_05_M04_Devolucion.html'; dir=$mock},
 @{p='G03_05_Estilos_Mockups.css'; dir=$mock}
)
Log 'ARCHIVOS OBLIGATORIOS'
foreach ($item in $checks) {
  $p = Join-Path $item.dir $item.p
  if (Test-Path -LiteralPath $p -PathType Leaf) { Log ('OK: ' + $item.p) }
  else { Log ('FALTA: ' + $item.p); $fallos++ }
}
Log ''
Log 'EXPORTACIONES DE SECUENCIA: RF EN SVG (NO sustituye inspeccion visual)'
$svgs = @(
 @{stem='G03_04_01_Registro_Solicitud'; expected=@('RF07')},
 @{stem='G03_04_02_Aprobacion'; expected=@('RF09')},
 @{stem='G03_04_03_Entrega'; expected=@('RF11')},
 @{stem='G03_04_Flujo_Integral_Resumen'; expected=@('RF07','RF09','RF11')}
)
foreach ($item in $svgs) {
  $svg = Join-Path $uml ($item.stem + '.svg')
  $png = Join-Path $uml ($item.stem + '.png')
  if (-not (Test-Path -LiteralPath $svg) -or -not (Test-Path -LiteralPath $png)) {
     Log ('FALTA SVG o PNG: ' + $item.stem); $fallos++; continue
  }
  $s = Get-Content -LiteralPath $svg -Raw -Encoding UTF8
  $seen = @([regex]::Matches($s, 'RF\d{2}') | ForEach-Object {$_.Value} | Select-Object -Unique)
  $missing = @($item.expected | Where-Object {$_ -notin $seen})
  $wrong = @($seen | Where-Object {$_ -notin $item.expected})
  if ($missing.Count -eq 0 -and $wrong.Count -eq 0) { Log ('OK: ' + $item.stem + ' -> ' + ($seen -join ', ')) }
  else { Log ('REVISAR: ' + $item.stem + ' detectado=' + ($seen -join ', ')); $fallos++ }
}
Log ''
Log 'REVISION DE DOCX: deteccion de texto en encabezados/pies'
$word = Join-Path $doc 'G03_06_SDD_IEEE1016_AgroRetorno_Cielo.docx'
if (Test-Path -LiteralPath $word) {
  try {
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    $zip = [System.IO.Compression.ZipFile]::OpenRead($word)
    try {
      $partes = @($zip.Entries | Where-Object {$_.FullName -match '^word/(header|footer)\d*\.xml$'})
      $textoEncontrado = @()
      foreach($part in $partes) {
        $reader = New-Object System.IO.StreamReader($part.Open())
        try { $xmlText = $reader.ReadToEnd() } finally { $reader.Dispose() }
        [xml]$xml = $xmlText
        $nodes = $xml.SelectNodes("//*[local-name()='t']")
        $val = (@($nodes | ForEach-Object {$_.InnerText}) -join ' ').Trim()
        if ($val.Length -gt 0) { $textoEncontrado += ($part.FullName + ': ' + $val.Substring(0, [Math]::Min(120,$val.Length))) }
      }
      if ($textoEncontrado.Count -eq 0) { Log 'OK: no se detecto texto en encabezados/pies de partes DOCX.' }
      else { Log 'ATENCION: se detecto texto en encabezados/pies DOCX:'; foreach ($item in $textoEncontrado){Log $item}; $fallos++ }
    } finally { $zip.Dispose() }
  } catch { Log ('NO VERIFICADO DOCX: ' + $_.Exception.Message); $fallos++ }
}
Log ''
Log 'CAPTURAS PERSONALES'
$images = @()
if (Test-Path $caps) {$images=@(Get-ChildItem -LiteralPath $caps -File | Where-Object {$_.Extension -in @('.png','.jpg','.jpeg','.webp')})}
Log ('Capturas encontradas: ' + $images.Count)
foreach($im in $images) { Log (' - ' + $im.Name) }
Log ''
Log ('Resultado comprobaciones automaticas: ' + $(if($fallos -eq 0){'SIN FALTANTES DETECTADOS'}else{"$fallos INCIDENCIAS"}))
Log 'PENDIENTE: comprobar visualmente documentos, diagramas y mockups; capturar pantalla y llenar acta.'
$path = Join-Path $rev 'G03_09_Auditoria_Local.txt'
$reg | Set-Content -LiteralPath $path -Encoding UTF8
Write-Host ('`nRegistro guardado: ' + $path)
if ($AbrirVistas) {
  foreach($item in $svgs) { $p=Join-Path $uml ($item.stem + '.svg'); if(Test-Path $p) {Start-Process -FilePath $p} }
  foreach($p in @('G03_05_M01_Solicitud.html','G03_05_M02_Revision.html','G03_05_M03_Entrega.html','G03_05_M04_Devolucion.html')) {
    $f=Join-Path $mock $p
    if(Test-Path $f) {Start-Process -FilePath $f}
  }
  $pdf=Join-Path $doc 'G03_06_SDD_IEEE1016_AgroRetorno_Cielo.pdf'
  if(Test-Path $pdf) {Start-Process -FilePath $pdf}
}
