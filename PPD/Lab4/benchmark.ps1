
$executable = "java --classpath C:\UBB\PPD\Lab2\out\production\Lab2 Main"

$executable = $args[0]
$inputFile = $args[1]
$numerOfThreads = $args[2]

# check if number of threads is a null
if ($null -eq $numerOfThreads) {
    $numerOfThreads = 1
}

$numberOfExecutions = 10

$sum = 0

for ($i = 0; $i -lt $numberOfExecutions; $i++) {
    Write-Host "Starting execution $i"
    $output = & java -classpath out\production\Lab2 $executable $inputFile $numerOfThreads
    Write-Host $output "ok"
    $duration = $output[$output.length - 1]
    Write-Host $duration
    $sum += $duration
}

$average = $sum / $numberOfExecutions

Write-Host "Average duration: $average"

if (!(Test-Path performance.csv)){
    New-Item performance.csv -ItemType File
    #Scrie date in csv
    Set-Content performance.csv 'Tip Matrice,Nr threads,Timp executie'
}

# Append
Add-Content performance.csv "$($inputFile),$($numerOfThreads),$($average)"