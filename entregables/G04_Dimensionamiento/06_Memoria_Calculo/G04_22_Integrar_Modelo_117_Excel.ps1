#requires -Version 5.1
<#
G04 Paso 1.22 (FIX COM) - integrar en copia del Excel G04_07 las decisiones internas G03 (117 CFP).
Modo normal: preflight sin escritura. -Aplicar: genera copia XLSX, CSV derivado e informe.
REQUISITO: Microsoft Excel de escritorio instalado en Windows.
NO cambia el XLSX original de 37 hojas ni el CSV maestro de 111 filas.
#>
[CmdletBinding()]
param([switch]$Aplicar)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$esperada = 'feature/cielo-g04-dimensionamiento-estimacion'
$shaMaestro = '2020f519d206400ace78803b5e374a98c6b10e61e2eb6a132af00cf33a99a34a'
# SHA256 del libro original G04_07 auditado en el proyecto; si cambio, hay que reconciliar.
$shaLibro = '2916f17fb7bd2285c0037dd8f6b41b98ca2f76390f96e002a618c42ee0073472'
$estado = 'Validado internamente G03'

function Fail([string]$msg) { throw ('G04 1.22: ' + $msg) }
function Sha([string]$path) { return (Get-FileHash -Algorithm SHA256 -LiteralPath $path).Hash.ToLowerInvariant() }
function Need([string]$path) { if (-not (Test-Path -LiteralPath $path -PathType Leaf)) { Fail "No existe: $path" } }
function CountOf($rows,[string]$type) { return @($rows | Where-Object { $_.Tipo -eq $type }).Count }

$repo = (& git -C $PSScriptRoot rev-parse --show-toplevel 2>$null)
if ($LASTEXITCODE -ne 0 -or -not $repo) { Fail 'Script fuera del repositorio AgroRetorno.' }
$repo = $repo.Trim()
$branch = (& git -C $repo branch --show-current).Trim()
if ($branch -ne $esperada) { Fail "Rama incorrecta: $branch" }
$head = (& git -C $repo rev-parse HEAD).Trim()
$base = Join-Path $repo 'entregables\G04_Dimensionamiento'
$csvMaestro = Join-Path $base '02_COSMIC\G04_03_Movimientos_COSMIC.csv'
$csvEscenario = Join-Path $base '04_Comparaciones\G04_20_Escenario_117_COSMIC.csv'
$csvDecisiones = Join-Path $base '04_Comparaciones\G04_21_Decisiones_Internas_117_COSMIC.csv'
$xlsOriginal = Join-Path $base '06_Memoria_Calculo\G04_07_NESMA_COSMIC_COCOMO_HorasReales.xlsx'
$xlsNuevo = Join-Path $base '06_Memoria_Calculo\G04_22_NESMA_COSMIC_COCOMO_HorasReales_117_G03.xlsx'
$csvNuevo = Join-Path $base '02_COSMIC\G04_22_Movimientos_117_ModeloInterno.csv'
$informe = Join-Path $base '04_Comparaciones\G04_22_Informe_Integracion_Excel.md'
foreach ($f in @($csvMaestro,$csvEscenario,$csvDecisiones,$xlsOriginal)) { Need $f }
foreach ($f in @($xlsNuevo,$csvNuevo,$informe)) {
    if (Test-Path -LiteralPath $f) { Fail "Ya existe la salida, no sobrescribir: $f" }
}
if ((Sha $csvMaestro) -ne $shaMaestro) { Fail ('CSV maestro modificado, SHA256=' + (Sha $csvMaestro)) }
if ((Sha $xlsOriginal) -ne $shaLibro) { Fail ('Libro distinto del auditado, SHA256=' + (Sha $xlsOriginal) + '. No aplicar sin conciliar nueva version.') }
$master = @(Import-Csv -LiteralPath $csvMaestro -Delimiter ';' -Encoding UTF8)
$escenario = @(Import-Csv -LiteralPath $csvEscenario -Delimiter ';' -Encoding UTF8)
$decisiones = @(Import-Csv -LiteralPath $csvDecisiones -Delimiter ';' -Encoding UTF8)
if ($master.Count -ne 111 -or $escenario.Count -ne 117 -or $decisiones.Count -ne 117) {
    Fail "Cantidad inesperada maestro=$($master.Count), escenario=$($escenario.Count), decisiones=$($decisiones.Count)"
}
for ($i=0; $i -lt 117; $i++) {
    $id = ('M{0:D3}' -f ($i+1))
    if ($escenario[$i].'Movimiento ID' -ne $id -or $decisiones[$i].ID -ne $id) { Fail "Error de orden/ID en $id" }
    if ($decisiones[$i].'Decision interna G04' -ne 'INCLUIR EN MODELO DE TRABAJO G03') { Fail "Decisión distinta de incluir en $id" }
    if ($i -lt 111) {
        $a=$master[$i];$b=$escenario[$i]
        foreach ($field in @('Movimiento ID','Proceso','Nombre del proceso','RF','Actor','Tipo','Grupo de datos','Detalle','Código')) {
            if ($a.$field -cne $b.$field) { Fail "CSV escenario distinto de maestro en $id/$field" }
        }
    }
}
if ((CountOf $escenario 'E') -ne 21 -or (CountOf $escenario 'X') -ne 27 -or
    (CountOf $escenario 'R') -ne 41 -or (CountOf $escenario 'W') -ne 28) {
    Fail 'Distribucion inesperada; se esperan E=21 X=27 R=41 W=28.'
}
Write-Host "G04 1.22 PREVIO OK | HEAD=$($head.Substring(0,12)) | maestro=111 | modelo=117" -ForegroundColor Green
Write-Host 'E=21, X=27, R=41, W=28 | Libro origen=37 hojas (pendiente corroborar con Excel).'
Write-Host "Libro original SHA256: $(Sha $xlsOriginal)"
if (-not $Aplicar) {
    Write-Host 'No se escribio ningun archivo. Ejecuta de nuevo con -Aplicar para generar una COPIA nueva.' -ForegroundColor Yellow
    return
}

$excel = $null
$book = $null
$success = $false
try {
    try { $excel = New-Object -ComObject Excel.Application } catch { Fail 'Microsoft Excel de escritorio no disponible (COM). Ejecutar en Windows con Excel instalado.' }
    $excel.Visible = $false
    $excel.DisplayAlerts = $false
    $excel.AskToUpdateLinks = $false
    $book = $excel.Workbooks.Open($xlsOriginal,0,$true)
    if ($book.Worksheets.Count -ne 37) { Fail "El libro no contiene 37 hojas: $($book.Worksheets.Count)" }
    $mov = $book.Worksheets.Item('COSMIC_Movimientos')
    $pro = $book.Worksheets.Item('COSMIC_Procesos')
    $res = $book.Worksheets.Item('COSMIC_Resumen')
    $nes = $book.Worksheets.Item('NESMA_Resumen')
    for ($i=0; $i -lt 111; $i++) {
        $r=6+$i
        $id=[string]$mov.Cells.Item($r,1).Value2
        $p=[string]$mov.Cells.Item($r,2).Value2
        $tipo=[string]$mov.Cells.Item($r,6).Value2
        $grupo=[string]$mov.Cells.Item($r,7).Value2
        if ($id -ne $master[$i].'Movimiento ID' -or $p -ne $master[$i].Proceso -or
            $tipo -ne $master[$i].Tipo -or $grupo -ne $master[$i].'Grupo de datos') {
            Fail "El XLSX no coincide con CSV maestro en fila $r / $($master[$i].'Movimiento ID')"
        }
    }
    if ([string]$mov.Cells.Item(117,1).Value2) { Fail 'Ya hay datos en A117; no se pueden agregar filas sin inspeccion.' }
    # Copia formato, validaciones y formula relativa de la ultima fila hacia las seis nuevas.
    for ($rowCopy=117; $rowCopy -le 122; $rowCopy++) {
        # Range.Copy(Destination) no requiere cambiar CutCopyMode; ocultar resultado COM True.
        [void]($mov.Range('A116:M116').Copy($mov.Range("A${rowCopy}:M${rowCopy}")))
    }
    for ($i=0; $i -lt 117; $i++) {
        $r=$i+6
        $item=$escenario[$i]
        if ($i -ge 111) {
            $fields=@('Movimiento ID','Proceso','Nombre del proceso','RF','Actor','Tipo','Grupo de datos','Detalle','Justificación','Código')
            for ($j=0; $j -lt $fields.Count; $j++) {
                $mov.Cells.Item($r,$j+1).Value2 = [string]$item.($fields[$j])
            }
        }
        $mov.Cells.Item($r,11).Value2 = 'Sí'
        $mov.Cells.Item($r,12).Formula = '=IF(K' + $r + '="Sí",1,0)'
        $mov.Cells.Item($r,13).Value2 = $estado
    }
    # Expandir SUMIFS en TODAS las filas, incluidas P01/P02 excluidas.
    for ($r=6; $r -le 28; $r++) {
        for ($col=6; $col -le 9; $col++) {
            $f=[string]$pro.Cells.Item($r,$col).Formula
            if (-not $f.Contains('$116')) { Fail "No se encontro el limite de la formula SUMIFS: fila $r columna $col" }
            $pro.Cells.Item($r,$col).Formula = $f.Replace('$116','$122')
        }
        $pro.Cells.Item($r,11).Value2 = $(if ($r -le 7) {'Excluido del modelo G03'} else {$estado})
    }
    $pro.Cells.Item(30,1).Value2 = 'TOTAL MODELO INTERNO G03 - CFP'
    $res.Range('G7').Formula = '=COUNTA(COSMIC_Movimientos!A6:A122)'
    $mov.Range('A4').Value2 = '117 decisiones internas G03 documentadas; no hay certificacion COSMIC ni conformidad G01.'
    $res.Range('A2').Value2 = 'Cielo Cristal Meza Vizcarra - modelo interno G03 (117 CFP; no certificacion externa)'
    $res.Range('A4').Value2 = 'La medicion es una decision interna G03 basada en reglas R21; no equivale a conformidad formal COSMIC o G01.'
    $comp = $book.Worksheets.Item('COMPARACION_Metodos')
    $comp.Range('G8').Value2 = 'MODELO INTERNO G03'
    $comp.Range('H8').Value2 = '117 CFP de trabajo adoptados; sin validacion formal COSMIC ni aprobacion G01.'
    $excel.CalculateFullRebuild()
    $observados = @{
        CFP=[int]$pro.Range('J30').Value2; E=[int]$pro.Range('F30').Value2;
        X=[int]$pro.Range('G30').Value2; R=[int]$pro.Range('H30').Value2;
        W=[int]$pro.Range('I30').Value2; Resumen=[int]$res.Range('A7').Value2;
        Movimientos=[int]$res.Range('G7').Value2; NESMA=[int]$nes.Range('B16').Value2
    }
    if ($observados.CFP -ne 117 -or $observados.E -ne 21 -or $observados.X -ne 27 -or
        $observados.R -ne 41 -or $observados.W -ne 28 -or $observados.Resumen -ne 117 -or
        $observados.Movimientos -ne 117 -or $observados.NESMA -ne 131) {
        Fail ('Los totales calculados no corresponden: ' + ($observados | Out-String))
    }
    if ([int]$comp.Range('E8').Value2 -ne 117) { Fail 'Comparacion de metodos no recalculo COSMIC a 117.' }
    $book.SaveAs($xlsNuevo,51) # 51 = formato XLSX; guarda COPIA, nunca el original.
    $book.Close($false)
    $book=$null
    # CSV derivado coherente con el estado interno de las 117 filas, sin tocar el maestro.
    $columnas=@('Movimiento ID','Proceso','Nombre del proceso','RF','Actor','Tipo','Grupo de datos','Detalle','Justificación','Código','Contar','Estado')
    $filasNuevas = foreach ($item in $escenario) {
        $fila=[ordered]@{}
        foreach ($columna in $columnas) { $fila[$columna] = [string]$item.$columna }
        $fila['Contar'] = 'Sí'; $fila['Estado'] = $estado
        [pscustomobject]$fila
    }
    $filasNuevas | Export-Csv -LiteralPath $csvNuevo -NoTypeInformation -Delimiter ';' -Encoding UTF8
    $shaSalida=Sha $xlsNuevo
    $csvSha=Sha $csvNuevo
    $reporte = @"
# G04 - Paso 1.22 | Integracion de modelo interno G03 en copia XLSX

**Rama:** ``$branch``  
**Commit de base:** ``$head``  
**Fuente XLSX original:** ``$([IO.Path]::GetFileName($xlsOriginal))``  
**SHA-256 XLSX original:** ``$(Sha $xlsOriginal)``  
**Nuevo XLSX (sin sobrescribir original):** ``$([IO.Path]::GetFileName($xlsNuevo))``  
**SHA-256 XLSX nuevo:** ``$shaSalida``  
**Nuevo CSV derivado:** ``$([IO.Path]::GetFileName($csvNuevo))``  
**SHA-256 CSV derivado:** ``$csvSha``

## Resultado comprobado en Excel de escritorio

- Hojas del libro: **37**, conservadas.
- COSMIC: **117 movimientos** con decision de inclusion interna G03.
- E=21, X=27, R=41, W=28; **117 CFP de trabajo**.
- Se agregaron M112-M117 en filas 117-122 con formulas de CFP individuales.
- SUMIFS de COSMIC_Procesos ampliados de fila 116 a 122; COSMIC_Resumen y COMPARACION_Metodos recalculados.
- NESMA_Resumen!B16: **131 UFP**, preservados.
- Estados de revision: **Validado internamente G03**; no implican certificacion COSMIC, aval G01 ni pruebas externas.
- El maestro CSV de 111 y el XLSX G04_07 originales quedaron **intactos**.

## Interpretacion

117 CFP es el **modelo interno adoptado** en G04_21. Persisten los limites de
validez metodologica establecidos en ese dictamen (GET sin datos, POST-redirect-GET,
agrupacion de salidas y grupos R/W). Las verificaciones automaticas de este paso
acreditan coherencia estructural y aritmetica del XLSX, no certificacion COSMIC.
"@
    [IO.File]::WriteAllText($informe,$reporte,([System.Text.UTF8Encoding]::new($false)))
    $success = $true
    Write-Host 'G04 1.22 OK | 37 hojas | E=21 X=27 R=41 W=28 | 117 CFP | NESMA 131 UFP' -ForegroundColor Green
    Write-Host "Libro nuevo: $xlsNuevo"
    Write-Host "CSV derivado: $csvNuevo"
    Write-Host "Informe: $informe"
    Write-Host 'Origenes intactos; la validacion es INTERNA G03, no formal.' -ForegroundColor Yellow
}
finally {
    if ($null -ne $book) { try { $book.Close($false) } catch {} }
    if ($null -ne $excel) {
        try { $excel.Quit() } catch {}
        try { [void][Runtime.InteropServices.Marshal]::ReleaseComObject($excel) } catch {}
    }
    [GC]::Collect()
    [GC]::WaitForPendingFinalizers()
    if (-not $success) {
        if (Test-Path -LiteralPath $xlsNuevo) { Remove-Item -LiteralPath $xlsNuevo -Force -ErrorAction SilentlyContinue }
        if (Test-Path -LiteralPath $csvNuevo) { Remove-Item -LiteralPath $csvNuevo -Force -ErrorAction SilentlyContinue }
        if (Test-Path -LiteralPath $informe) { Remove-Item -LiteralPath $informe -Force -ErrorAction SilentlyContinue }
    }
}
