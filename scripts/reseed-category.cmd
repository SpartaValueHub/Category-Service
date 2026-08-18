@echo off
REM PowerShell 파이프로 넣으면 한글이 ??? 로 깨짐. 반드시 cmd 리다이렉션 + utf8mb4 사용.
REM 로컬 기본: localhost:3306 (팀 공용 DB는 HOST/PORT/PASS만 바꿔서 실행)
chcp 65001 >nul
set HOST=127.0.0.1
set PORT=3306
set USER=root
set PASS=1234
set MYSQL="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
set SQL=%~dp0seed-category.sql

%MYSQL% -h %HOST% -P %PORT% -u %USER% -p%PASS% --default-character-set=utf8mb4 < "%SQL%"
if errorlevel 1 (
  echo seed failed
  exit /b 1
)
%MYSQL% -h %HOST% -P %PORT% -u %USER% -p%PASS% --default-character-set=utf8mb4 -e "SELECT depth, COUNT(*) cnt FROM category_db.category GROUP BY depth ORDER BY depth;"
echo seed ok
