@echo off
setlocal
IF NOT EXIST node_modules (
  echo Installing deps...
  npm.cmd install
)
node .\node_modules\vite\bin\vite.js preview