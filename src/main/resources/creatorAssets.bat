@echo off
setlocal enabledelayedexpansion

set "GRADLE_PROPS=%~dp0..\..\..\gradle.properties"

for /f "tokens=2 delims==" %%A in ('findstr /b "mod_id=" "%GRADLE_PROPS%"') do (
    set "MODID=%%A"
)

if not defined MODID (
    echo Error: No mod_id=value found in gradle.properties
    pause
    exit /b 1
)

echo MODID = %MODID%

set "BASE_DIR=%~dp0"
set "ASSETS_DIR=%BASE_DIR%assets\%MODID%\"
set "BLOCKSTATES_DIR=%ASSETS_DIR%blockstates\"
set "ITEMS_DIR=%ASSETS_DIR%items\"
set "MODELS_DIR=%ASSETS_DIR%models\"
set "ITEM_MODELS_DIR=%MODELS_DIR%item\"
set "BLOCK_MODELS_DIR=%MODELS_DIR%block\"

echo.
echo [1] Generate Block
echo [2] Generate Item
choice /C 12 /N /M "Select action:"

if errorlevel 2 (
    call :generate_item
) else (
    call :generate_block
)

exit /b 0

:generate_block
set /p BLOCK_ID=Print ID of block: 
echo Generating JSON for %BLOCK_ID%...

(
    echo {
    echo   "variants": {
    echo     "": { "model": "%MODID%:block/%BLOCK_ID%" }
    echo   }
    echo }
) > "%BLOCKSTATES_DIR%/%BLOCK_ID%.json"

(
    echo {
    echo   "parent": "block/cube_all",
    echo   "textures": {
    echo     "all": "%MODID%:block/%BLOCK_ID%"
    echo   }
    echo }
) > "%BLOCK_MODELS_DIR%/%BLOCK_ID%.json"

(
    echo {
    echo   "parent": "%MODID%:block/%BLOCK_ID%"
    echo }
) > "%ITEM_MODELS_DIR%/%BLOCK_ID%.json"

(
    echo {
    echo   "model": {
    echo     "type": "minecraft:model",
    echo     "model": "%MODID%:block/%BLOCK_ID%"
    echo   }
    echo }
) > "%ITEM_DIR%/%BLOCK_ID%.json"

echo %BLOCK_ID% Successfully created!
goto :eof

:generate_item
set /p ITEM_ID=Print ID of item: 
echo Generating JSON for %ITEM_ID%...
(
    echo {
    echo   "parent": "item/generated",
    echo   "textures": {
    echo     "layer0": "%MODID%:item/%ITEM_ID%"
    echo   }
    echo }
) > "%ITEM_MODELS_DIR%/%ITEM_ID%.json"

(
    echo {
    echo   "model": {
    echo     "type": "minecraft:model",
    echo     "model": "%MODID%:item/%ITEM_ID%"
    echo   }
    echo }
) > "%ITEMS_DIR%/%ITEM_ID%.json"

echo %ITEM_ID% Successfully created!
goto :eof

pause