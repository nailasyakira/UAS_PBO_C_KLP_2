@echo off
REM Compile semua file lalu jalankan versi GUI. Klik dua kali file ini untuk memakainya.
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt && java -cp out supermarket.gui.MainGUI
pause
