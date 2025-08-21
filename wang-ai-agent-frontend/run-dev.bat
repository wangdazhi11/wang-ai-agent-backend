@echo off
setlocal
where node >nul 2>nul || (
  echo [Error] Node.js not found. Please install from https://nodejs.org/ && exit /b 1
)
where npm >nul 2>nul || echo [Warn] npm not found in PATH. Using node to run vite.
IF EXIST node_modules ( goto :start ) ELSE ( echo Installing deps... && npm.cmd install )
:start
node .\node_modules\vite\bin\vite.js