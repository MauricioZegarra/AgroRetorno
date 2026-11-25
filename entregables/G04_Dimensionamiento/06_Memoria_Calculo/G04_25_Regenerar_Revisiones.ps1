# G04 Paso 1.25 - Revisiones actualizadas del libro de 117 CFP.
# Lee el XLSX mediante Excel de escritorio y genera tablas visuales PNG.
# No guarda ni modifica el XLSX. Los PNG son visualizaciones de valores,
# no capturas manuales de pantalla.
# Ejecutar en Windows PowerShell 5.1 con Microsoft Excel instalado.

[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$base = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$repo = (Resolve-Path (Join-Path $base '..\..')).Path
$capturas = Join-Path $base '05_Capturas'
$libro = Join-Path $base '06_Memoria_Calculo\G04_22_NESMA_COSMIC_COCOMO_HorasReales_117_G03.xlsx'
$nueva = Join-Path $capturas 'Revisiones_Actualizadas_117'
$antiguas = @(
  (Join-Path $capturas 'Exportaciones_Automaticas'),
  (Join-Path $capturas 'Revision_Real_117')
)
$expectedSha = '4e17024f115d7ec107c40567354f41db27d18ea04dbc63bbe4fcb4c9624de621'
$stamp = Get-Date -Format 'yyyyMMdd_HHmmss'
$backup = Join-Path $env:LOCALAPPDATA "AgroRetorno\G04_25_Respaldos\$stamp"
$stage = Join-Path $env:TEMP "G04_25_PNG_$stamp"

if (-not (Test-Path -LiteralPath $libro -PathType Leaf)) {
    throw "No existe el libro de 117 CFP: $libro"
}
$branch = (& git -C $repo branch --show-current 2>$null | Out-String).Trim()
if ($LASTEXITCODE -ne 0 -or $branch -ne 'feature/cielo-g04-dimensionamiento-estimacion') {
    throw "Rama incorrecta: '$branch'. Cambia primero a feature/cielo-g04-dimensionamiento-estimacion."
}

$hash = (Get-FileHash -LiteralPath $libro -Algorithm SHA256).Hash.ToLowerInvariant()
if ($hash -ne $expectedSha) {
    Write-Warning 'El XLSX difiere del archivo auditado en G04_23. Las imagenes reflejaran el archivo actual, pero la auditoria anterior no acredita este hash nuevo.'
}

# Vistas comparables con las antiguas 13, mas un detalle de las seis Exit nuevas.
$vistas = @(
    @{ Num='01'; Hoja='Resumen';             Rango='A1:H19';   Nombre='Resumen' },
    @{ Num='02'; Hoja='NESMA_Datos';         Rango='A1:K17';   Nombre='NESMA_Datos' },
    @{ Num='03'; Hoja='NESMA_Transacciones'; Rango='A1:J18';   Nombre='NESMA_Transacciones' },
    @{ Num='04'; Hoja='NESMA_Resumen';       Rango='A1:H22';   Nombre='NESMA_Resumen' },
    @{ Num='05'; Hoja='COSMIC_Movimientos';  Rango='A1:K19';   Nombre='COSMIC_Movimientos' },
    @{ Num='06'; Hoja='COSMIC_Resumen';      Rango='A1:G23';   Nombre='COSMIC_Resumen' },
    @{ Num='07'; Hoja='COCOMO_FuenteLOC';    Rango='A1:H19';   Nombre='COCOMO_FuenteLOC' },
    @{ Num='08'; Hoja='COCOMO_Modelo';       Rango='A1:G22';   Nombre='COCOMO_Modelo' },
    @{ Num='09'; Hoja='COSTOS_Resumen';      Rango='A1:H18';   Nombre='COSTOS_Resumen' },
    @{ Num='10'; Hoja='SENSIBILIDAD_Resumen';Rango='A1:G19';   Nombre='SENSIBILIDAD_Resumen' },
    @{ Num='11'; Hoja='COMPARACION_Metodos'; Rango='A1:G17';   Nombre='COMPARACION_Metodos' },
    @{ Num='12'; Hoja='REALES_Registro';     Rango='A1:K15';   Nombre='REALES_Registro' },
    @{ Num='13'; Hoja='REALES_Resumen';      Rango='A5:B16';   Nombre='REALES_Resumen' },
    @{ Num='14'; Hoja='COSMIC_Movimientos';  Rango='A115:M122';Nombre='COSMIC_M112_M117' }
)

# FIX (v2): Renderizar directamente los valores del rango con System.Drawing.
# No usa Range.CopyPicture, portapapeles ni ChartObjects (dependientes de Excel/COM).
# La salida es una visualizacion tabular, NO una captura manual ni copia exacta del estilo Excel.
Add-Type -AssemblyName System.Drawing

function Exportar-RangoPng {
    param(
        [Parameter(Mandatory=$true)] $Hoja,
        [Parameter(Mandatory=$true)] [string] $Rango,
        [Parameter(Mandatory=$true)] [string] $Destino
    )

    $rng = $null
    $bmp = $null
    $g = $null
    $fontTitle = $null
    $fontHead = $null
    $fontCell = $null
    $fontFoot = $null
    $brushTitle = $null
    $brushBlue = $null
    $brushWhite = $null
    $brushInk = $null
    $brushMuted = $null
    $brushAlternate = $null
    $brushGrid = $null
    $penGrid = $null
    $fmt = $null
    try {
        $rng = $Hoja.Range($Rango)
        if ($null -eq $rng) { throw "No se pudo leer el rango $Rango." }
        $values = $rng.Value2
        if ($null -eq $values) { throw "El rango $Rango no contiene valores." }
        if (-not ($values -is [System.Array]) -or $values.Rank -ne 2) {
            throw "Excel devolvio un valor inesperado para $Rango."
        }
        $nr = [int]$values.GetLength(0)
        $nc = [int]$values.GetLength(1)
        $r0 = [int]$values.GetLowerBound(0)
        $c0 = [int]$values.GetLowerBound(1)
        if ($nr -lt 1 -or $nc -lt 1 -or $nr -gt 200 -or $nc -gt 35) {
            throw "Dimensiones no esperadas en $Rango : ${nr}x${nc}."
        }

        $filas = New-Object System.Collections.ArrayList
        $anchos = New-Object 'int[]' $nc
        $altos = New-Object 'int[]' $nr
        for ($c = 0; $c -lt $nc; $c++) { $anchos[$c] = 110 }

        for ($i = 0; $i -lt $nr; $i++) {
            $fila = New-Object System.Collections.ArrayList
            $mayor = 0
            for ($j = 0; $j -lt $nc; $j++) {
                $v = $values.GetValue(($i + $r0), ($j + $c0))
                $celda = ''
                if ($null -ne $v) {
                    if ($v -is [double] -or $v -is [decimal] -or $v -is [single]) {
                        $celda = ([double]$v).ToString('0.####', [Globalization.CultureInfo]::InvariantCulture)
                    } else {
                        $celda = [string]$v
                    }
                }
                $celda = $celda.Replace("`r", ' ').Replace("`n", ' ').Trim()
                if ($celda.Length -gt 0) { $mayor = [Math]::Max($mayor, $celda.Length) }
                $charWidth = if ($j -eq 0) { 7 } else { 6 }
                $ancho = [Math]::Max(110, [Math]::Min(275, ($celda.Length * $charWidth + 26)))
                $anchos[$j] = [Math]::Max([int]$anchos[$j], [int]$ancho)
                [void]$fila.Add($celda)
            }
            [void]$filas.Add($fila)
            if ($i -eq 0) {
                $altos[$i] = 43
            } elseif ($mayor -gt 110) {
                $altos[$i] = 112
            } elseif ($mayor -gt 65) {
                $altos[$i] = 84
            } elseif ($mayor -gt 32) {
                $altos[$i] = 62
            } else {
                $altos[$i] = 43
            }
        }

        $w = 38
        foreach ($aw in $anchos) { $w += [int]$aw }
        $h = 125
        foreach ($ah in $altos) { $h += [int]$ah }
        $w = [int][Math]::Max(560,$w)
        $h = [int][Math]::Max(230,$h)
        $bmp = [System.Drawing.Bitmap]::new($w, $h)
        $g = [System.Drawing.Graphics]::FromImage($bmp)
        $g.Clear([System.Drawing.Color]::White)
        $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
        $g.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::ClearTypeGridFit

        $fontTitle = [System.Drawing.Font]::new('Segoe UI', 17.0, [System.Drawing.FontStyle]::Bold)
        $fontHead = [System.Drawing.Font]::new('Segoe UI', 10.0, [System.Drawing.FontStyle]::Bold)
        $fontCell = [System.Drawing.Font]::new('Segoe UI', 9.0, [System.Drawing.FontStyle]::Regular)
        $fontFoot = [System.Drawing.Font]::new('Segoe UI', 8.0, [System.Drawing.FontStyle]::Regular)
        $brushTitle = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(29, 74, 61))
        $brushBlue = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(29, 93, 73))
        $brushWhite = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::White)
        $brushInk = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(37, 47, 47))
        $brushMuted = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(102, 113, 113))
        $brushAlternate = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(244, 249, 247))
        $brushGrid = [System.Drawing.SolidBrush]::new([System.Drawing.Color]::FromArgb(230, 239, 235))
        $penGrid = [System.Drawing.Pen]::new([System.Drawing.Color]::FromArgb(209, 223, 216), 1.0)
        $fmt = [System.Drawing.StringFormat]::new()
        $fmt.Alignment = [System.Drawing.StringAlignment]::Near
        $fmt.LineAlignment = [System.Drawing.StringAlignment]::Center
        $fmt.Trimming = [System.Drawing.StringTrimming]::EllipsisCharacter

        $g.FillRectangle($brushTitle, 0, 0, $w, 71)
        $g.DrawString(('G04  |  ' + [string]$Hoja.Name), $fontTitle, $brushWhite,
            [System.Drawing.RectangleF]::new(18.0, 8.0, [single]($w-36), 32.0))
        $g.DrawString(('Vista exportada: ' + $Rango + '  |  Modelo interno 117 CFP'), $fontCell, $brushWhite,
            [System.Drawing.RectangleF]::new(18.0, 41.0, [single]($w-36), 24.0))

        $y = 80
        for ($i = 0; $i -lt $nr; $i++) {
            $x = 18
            for ($j = 0; $j -lt $nc; $j++) {
                $cw = [int]$anchos[$j]
                $ch = [int]$altos[$i]
                if ($i -eq 0) {
                    $g.FillRectangle($brushBlue, $x, $y, $cw, $ch)
                } elseif (($i % 2) -eq 0) {
                    $g.FillRectangle($brushAlternate, $x, $y, $cw, $ch)
                }
                $g.DrawRectangle($penGrid, $x, $y, $cw, $ch)
                $rect = [System.Drawing.RectangleF]::new(
                    [single]($x+7), [single]($y+4), [single]($cw-13), [single]($ch-8))
                $txt = [string]$filas[$i][$j]
                if ($i -eq 0) {
                    $g.DrawString($txt, $fontHead, $brushWhite, $rect, $fmt)
                } else {
                    $g.DrawString($txt, $fontCell, $brushInk, $rect, $fmt)
                }
                $x += $cw
            }
            $y += [int]$altos[$i]
        }
        $g.DrawString('Exportacion automatizada desde los valores del XLSX. No es captura de pantalla ni certificacion externa.',
            $fontFoot, $brushMuted, [System.Drawing.RectangleF]::new(19.0,[single]($h-36),[single]($w-38),24.0))
        $bmp.Save($Destino, [System.Drawing.Imaging.ImageFormat]::Png)
        if (-not (Test-Path -LiteralPath $Destino -PathType Leaf)) {
            throw 'No se genero la imagen PNG.'
        }
        if ((Get-Item -LiteralPath $Destino).Length -lt 3000) {
            throw 'PNG demasiado pequeno; revision necesaria.'
        }
    } catch {
        if (Test-Path -LiteralPath $Destino) {
            Remove-Item -LiteralPath $Destino -Force -ErrorAction SilentlyContinue
        }
        throw "Fallo al renderizar $($Hoja.Name)!${Rango}: $($_.Exception.Message). Las carpetas originales no se han borrado."
    } finally {
        if ($null -ne $fmt) { $fmt.Dispose() }
        if ($null -ne $penGrid) { $penGrid.Dispose() }
        foreach ($b in @($brushTitle,$brushBlue,$brushWhite,$brushInk,$brushMuted,$brushAlternate,$brushGrid)) {
            if ($null -ne $b) { $b.Dispose() }
        }
        foreach ($f in @($fontTitle,$fontHead,$fontCell,$fontFoot)) {
            if ($null -ne $f) { $f.Dispose() }
        }
        if ($null -ne $g) { $g.Dispose() }
        if ($null -ne $bmp) { $bmp.Dispose() }
        if ($null -ne $rng) {
            try { [void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($rng) } catch {}
        }
    }
}

New-Item -ItemType Directory -Path $stage -Force | Out-Null
$excel = $null
$wb = $null
$terminado = $false
$cosmic = $null
$nesma = $null
$kloc = $null
$comparacion = $null
$reales = $null
try {
    Write-Host 'Abriendo Excel en modo de solo lectura...' -ForegroundColor Cyan
    $excel = New-Object -ComObject Excel.Application
    $excel.Visible = $false
    $excel.DisplayAlerts = $false
    $excel.ScreenUpdating = $false
    $wb = $excel.Workbooks.Open($libro, 0, $true)
    $excel.CalculateFull()

    $cosmic = [double]$wb.Worksheets.Item('COSMIC_Resumen').Range('A7').Value2
    $nesma = [double]$wb.Worksheets.Item('NESMA_Resumen').Range('B16').Value2
    $kloc = [double]$wb.Worksheets.Item('COCOMO_Modelo').Range('B6').Value2
    $comparacion = [double]$wb.Worksheets.Item('COMPARACION_Metodos').Range('E8').Value2
    $reales = [string]$wb.Worksheets.Item('REALES_Resumen').Range('B5').Text
    if ($cosmic -ne 117 -or $nesma -ne 131 -or [Math]::Abs($kloc - 3.491) -gt 0.001 -or $comparacion -ne 117) {
        throw "Datos inesperados: COSMIC=$cosmic, NESMA=$nesma, KLOC=$kloc, comparacion=$comparacion. No se alteraron capturas anteriores."
    }

    foreach ($v in $vistas) {
        $name = ('G04_25_{0}_{1}.png' -f $v.Num, $v.Nombre)
        $out = Join-Path $stage $name
        Write-Host ("Exportando {0} ..." -f $name)
        Exportar-RangoPng -Hoja ($wb.Worksheets.Item($v.Hoja)) -Rango $v.Rango -Destino $out
    }
    $files = @(Get-ChildItem -LiteralPath $stage -Filter '*.png' -File)
    if ($files.Count -ne $vistas.Count) {
        throw "Se esperaban $($vistas.Count) PNG, pero solo hay $($files.Count). No se alteraron carpetas anteriores."
    }
    $terminado = $true
} finally {
    if ($null -ne $wb) {
        try { $wb.Close($false) | Out-Null } catch {}
        try { [void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($wb) } catch {}
    }
    if ($null -ne $excel) {
        try { $excel.Quit() | Out-Null } catch {}
        try { [void][Runtime.InteropServices.Marshal]::FinalReleaseComObject($excel) } catch {}
    }
    [GC]::Collect()
    [GC]::WaitForPendingFinalizers()
}
if (-not $terminado) { throw 'No se han publicado las imagenes porque la exportacion fallo.' }

# Solo cuando TODAS las imagenes existen, respaldar y retirar las carpetas viejas.
New-Item -ItemType Directory -Path $backup -Force | Out-Null
foreach ($folder in (@($antiguas) + @($nueva))) {
    if (Test-Path -LiteralPath $folder) {
        $dest = Join-Path $backup (Split-Path -Leaf $folder)
        Move-Item -LiteralPath $folder -Destination $dest -ErrorAction Stop
        Write-Host "Respaldo: $dest"
    }
}
Move-Item -LiteralPath $stage -Destination $nueva -ErrorAction Stop

# Manifiesto fiel al origen: proyecto de la responsable, generado desde Excel local.
$lista = ($vistas | ForEach-Object { '- `G04_25_' + $_.Num + '_' + $_.Nombre + '.png`: `' + $_.Hoja + '!' + $_.Rango + '`' }) -join "`n"
$hoy = Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz'
$matchAudit = if ($hash -eq $expectedSha) { 'Si, coincide con el G04_23 auditado.' } else { 'No; requiere reauditar la version actual.' }
$manifest = @"
# G04 Paso 1.25 - Revisiones visuales actualizadas (117 CFP)

Responsable de la guia: Cielo Cristal Meza Vizcarra.
Fecha de generacion local: $hoy.
Fuente: ``G04_22_NESMA_COSMIC_COCOMO_HorasReales_117_G03.xlsx``.
SHA-256 del XLSX consultado: ``$hash``.
Coincidencia con la version auditada: $matchAudit

Resultados leidos desde Excel: NESMA **$nesma UFP**; COSMIC **$cosmic CFP**;
comparacion **$comparacion CFP**; COCOMO **$kloc KLOC**;
REALES_Resumen!B5: **$reales**.

Estas imagenes son **tablas visuales generadas automaticamente desde valores de Excel** de la guia de la responsable.
No se describen como capturas manuales de pantalla ni como certificacion externa.
El libro fuente se abrio en modo de solo lectura y no se guardo. La presentacion PNG no reproduce exactamente los estilos del Excel.

## Archivos y rangos

$lista

El documento G04_08 y sus imagenes anteriores corresponden al estado historico de 111 CFP.
Las dos carpetas previas se retiraron de esta ruta y respaldaron fuera del repositorio.
Respaldo: ``$backup``.
"@
[IO.File]::WriteAllText((Join-Path $nueva 'README.md'), $manifest.TrimEnd() + "`n", [Text.UTF8Encoding]::new($false))

$acta = Join-Path $base '04_Comparaciones\G04_25_Registro_Revisiones_117.md'
$actaContent = @"
# G04 Paso 1.25 - Registro de actualizacion visual

**Responsable:** Cielo Cristal Meza Vizcarra
**Fecha de exportacion local:** $hoy
**Libro fuente:** ``G04_22_NESMA_COSMIC_COCOMO_HorasReales_117_G03.xlsx``
**SHA-256:** ``$hash``
**Coincide con la version de G04_23:** $matchAudit

Se regeneraron **$($vistas.Count) tablas PNG** desde los valores de las celdas del Excel de escritorio,
con resultado COSMIC **$cosmic CFP**, NESMA **$nesma UFP** y COCOMO **$kloc KLOC**.
Se incluye detalle de las filas nuevas M112-M117. Se retiraron del arbol del proyecto
las exportaciones historicas G04_08 y la carpeta temporal de capturas manuales.

**Ruta vigente:** ``05_Capturas/Revisiones_Actualizadas_117/``.
**Procedencia correcta:** tablas de datos renderizadas mediante automatizacion local de Excel,
no fotografias/capturas manuales ni evidencia de revision externa.
**Estado del esfuerzo real:** $reales (segun REALES_Resumen!B5).

El libro fuente **no fue modificado**; las carpetas antiguas se respaldaron en
``$backup``. Este paso no cambia las decisiones metodologicas ni equivale a validar la ERS G01.
"@
[IO.File]::WriteAllText($acta, $actaContent.TrimEnd() + "`n", [Text.UTF8Encoding]::new($false))

$readmePath = Join-Path $base 'README.md'
$readme = [IO.File]::ReadAllText($readmePath)
$oldSentence = 'Las exportaciones automaticas de `05_Capturas/Exportaciones_Automaticas` no son capturas personales tomadas en el equipo.'
$newSentence = 'Las vistas vigentes estan en `05_Capturas/Revisiones_Actualizadas_117/` (exportaciones locales del Excel vigente, Paso 1.25); las imagenes G04_08 se conservan solo como antecedente historico en su registro de procedencia.'
if ($readme.Contains($oldSentence)) {
    $readme = $readme.Replace($oldSentence, $newSentence)
} elseif (-not $readme.Contains('Revisiones_Actualizadas_117')) {
    $readme = $readme.TrimEnd([char[]]"`r`n") + "`n`n## Evidencias visuales actualizadas`n`n" + $newSentence + "`n"
}
[IO.File]::WriteAllText($readmePath, $readme, [Text.UTF8Encoding]::new($false))

$historico = Join-Path $base '06_Memoria_Calculo\G04_08_Procedencia_Exportaciones.md'
if (Test-Path -LiteralPath $historico) {
    $prev = [IO.File]::ReadAllText($historico)
    $memo = '> **Nota de version (Paso 1.25):** las imagenes G04_08 corresponden al libro historico de 111 CFP y se retiraron del arbol de entrega. Las visualizaciones vigentes del libro de 117 CFP estan en `05_Capturas/Revisiones_Actualizadas_117/`. Este archivo conserva la procedencia de la version anterior.'
    if (-not $prev.Contains('Nota de version (Paso 1.25)')) {
        $prev = $memo + "`n`n" + $prev
        [IO.File]::WriteAllText($historico, $prev, [Text.UTF8Encoding]::new($false))
    }
}

$bitacora = Join-Path $base 'Bitacora.md'
$bit = [IO.File]::ReadAllText($bitacora)
if (-not $bit.Contains('## G04 - Paso 1.25')) {
    $entry = @"

## G04 - Paso 1.25: Actualizacion de vistas de revision

Se regeneraron $($vistas.Count) imagenes desde el libro G04_22 de 117 CFP,
131 UFP y 3.491 KLOC, usando Excel en modo de solo lectura.
Imagenes vigentes: 05_Capturas/Revisiones_Actualizadas_117/.
Origen: visualizacion automatizada de valores locales, no captura manual de pantalla.
Las carpetas anteriores se respaldaron fuera del repositorio.
El estado de horas reales y los limites de G01 no fueron modificados.
"@
    $bit = $bit.TrimEnd([char[]]"`r`n") + "`n" + $entry.TrimStart([char[]]"`r`n").TrimEnd([char[]]"`r`n") + "`n"
    [IO.File]::WriteAllText($bitacora, $bit, [Text.UTF8Encoding]::new($false))
}

Write-Host ''
Write-Host "LISTO: $($vistas.Count) PNG actualizados en $nueva" -ForegroundColor Green
Write-Host "COSMIC=$cosmic; NESMA=$nesma; COCOMO KLOC=$kloc; REALES=$reales"
Write-Host "Respaldo de carpetas previas: $backup"
Write-Host 'El Excel no fue guardado ni modificado. No se realizo commit ni push.'
Write-Host 'Revisa con: git status; git diff --check; git diff --stat'
