@echo off
setlocal enabledelayedexpansion

:: Проверка аргументов
if "%~1"=="" (
    echo Use: %~nx0 ^<src_dir^> ^<dest_file^>
    exit /b 1
)
if "%~2"=="" (
    echo Use: %~nx0 ^<src_dir^> ^<dest_file^>
    exit /b 1
)

set "SRC_DIR=%~1"
set "OUT_FILE=%~2"

:: Очистка/создание выходного файла
> "%OUT_FILE%" echo.

:: Перебор всех .java файлов рекурсивно
for /r "%SRC_DIR%" %%f in (*.java) do (
    echo //// FILE: %%f >> "%OUT_FILE%"
    type "%%f" >> "%OUT_FILE%"
    echo. >> "%OUT_FILE%"
    echo. >> "%OUT_FILE%"
)

echo Compete! All files merged in "%OUT_FILE%"
endlocal
